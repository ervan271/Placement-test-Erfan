package handler;

import com.sun.net.httpserver.*;
import com.google.gson.*;
import dao.MedicineDAO;
import model.Medicine;
import java.io.*;
import java.sql.SQLException;

public class MedicineHandler implements HttpHandler {
    private MedicineDAO medicineDAO = new MedicineDAO();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if (path.equals("/api/medicines")) {
                if ("GET".equals(method)) {
                    handleGetAllMedicines(exchange);
                } else if ("POST".equals(method)) {
                    handleAddMedicine(exchange);
                } else {
                    sendError(exchange, 405, "Method not allowed");
                }
            } else if (path.matches("/api/medicines/\\d+")) {
                int id = Integer.parseInt(path.replaceAll("[^0-9]", ""));
                if ("GET".equals(method)) {
                    handleGetMedicineById(exchange, id);
                } else if ("PUT".equals(method)) {
                    handleUpdateMedicine(exchange, id);
                } else if ("DELETE".equals(method)) {
                    handleDeleteMedicine(exchange, id);
                } else {
                    sendError(exchange, 405, "Method not allowed");
                }
            } else if (path.equals("/api/medicines/low-stock")) {
                handleGetLowStock(exchange);
            } else if (path.equals("/api/medicines/near-expiry")) {
                handleGetNearExpiry(exchange);
            } else if (path.matches("/api/medicines/\\d+/deactivate")) {
                int id = Integer.parseInt(path.replaceAll("[^0-9]", ""));
                handleDeactivateMedicine(exchange, id);
            } else {
                sendError(exchange, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(exchange, 500, "Error: " + e.getMessage());
        }
    }

    private void handleGetAllMedicines(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(medicineDAO.getAllMedicines());
            sendResponse(exchange, 200, response);
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleGetMedicineById(HttpExchange exchange, int id) throws IOException {
        try {
            Medicine medicine = medicineDAO.getMedicineById(id);
            if (medicine != null) {
                sendResponse(exchange, 200, gson.toJson(medicine));
            } else {
                sendError(exchange, 404, "Medicine not found");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleAddMedicine(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        try {
            Medicine medicine = gson.fromJson(body, Medicine.class);
            if (medicine.getName() == null || medicine.getName().isEmpty()) {
                sendError(exchange, 400, "Field 'name' is required");
                return;
            }
            Medicine created = medicineDAO.addMedicine(medicine);
            sendResponse(exchange, 201, gson.toJson(created));
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Invalid JSON");
        }
    }

    private void handleUpdateMedicine(HttpExchange exchange, int id) throws IOException {
        String body = readRequestBody(exchange);
        try {
            Medicine updated = gson.fromJson(body, Medicine.class);
            if (medicineDAO.updateMedicine(id, updated)) {
                Medicine medicine = medicineDAO.getMedicineById(id);
                sendResponse(exchange, 200, gson.toJson(medicine));
            } else {
                sendError(exchange, 404, "Medicine not found");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Invalid JSON");
        }
    }

    private void handleDeleteMedicine(HttpExchange exchange, int id) throws IOException {
        try {
            if (medicineDAO.deleteMedicine(id)) {
                sendResponse(exchange, 200, "{\"message\":\"Deleted\"}");
            } else {
                sendError(exchange, 404, "Medicine not found");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleDeactivateMedicine(HttpExchange exchange, int id) throws IOException {
        try {
            if (medicineDAO.deactivateMedicine(id)) {
                Medicine medicine = medicineDAO.getMedicineById(id);
                sendResponse(exchange, 200, gson.toJson(medicine));
            } else {
                sendError(exchange, 404, "Medicine not found");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleGetLowStock(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(medicineDAO.getLowStockMedicines());
            sendResponse(exchange, 200, response);
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleGetNearExpiry(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(medicineDAO.getNearExpiryMedicines());
            sendResponse(exchange, 200, response);
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        String error = "{\"error\":\"" + message + "\"}";
        sendResponse(exchange, status, error);
    }
}