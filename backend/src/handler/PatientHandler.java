package handler;

import com.sun.net.httpserver.*;
import com.google.gson.*;
import dao.PatientDAO;
import model.Patient;
import java.io.*;
import java.sql.SQLException;

public class PatientHandler implements HttpHandler {
    private PatientDAO patientDAO = new PatientDAO();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if (path.equals("/api/patients")) {
                if ("GET".equals(method)) {
                    handleGetAllPatients(exchange);
                } else if ("POST".equals(method)) {
                    handleAddPatient(exchange);
                } else {
                    sendError(exchange, 405, "Method not allowed");
                }
            } else if (path.matches("/api/patients/\\d+")) {
                int id = Integer.parseInt(path.replaceAll("[^0-9]", ""));
                if ("GET".equals(method)) {
                    handleGetPatientById(exchange, id);
                } else if ("PUT".equals(method)) {
                    handleUpdatePatient(exchange, id);
                } else if ("DELETE".equals(method)) {
                    handleDeletePatient(exchange, id);
                } else {
                    sendError(exchange, 405, "Method not allowed");
                }
            } else if (path.equals("/api/patients/search") && query != null) {
                handleSearchPatient(exchange, query);
            } else {
                sendError(exchange, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(exchange, 500, "Error: " + e.getMessage());
        }
    }

    private void handleGetAllPatients(HttpExchange exchange) throws IOException {
        try {
            String response = gson.toJson(patientDAO.getAllPatients());
            sendResponse(exchange, 200, response);
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleGetPatientById(HttpExchange exchange, int id) throws IOException {
        try {
            Patient patient = patientDAO.getPatientById(id);
            if (patient != null) {
                sendResponse(exchange, 200, gson.toJson(patient));
            } else {
                sendError(exchange, 404, "Patient not found");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleAddPatient(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        try {
            Patient patient = gson.fromJson(body, Patient.class);
            if (patient.getFull_name() == null || patient.getFull_name().isEmpty()) {
                sendError(exchange, 400, "Field 'full_name' is required");
                return;
            }
            Patient created = patientDAO.addPatient(patient);
            sendResponse(exchange, 201, gson.toJson(created));
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Invalid JSON");
        }
    }

    private void handleUpdatePatient(HttpExchange exchange, int id) throws IOException {
        String body = readRequestBody(exchange);
        try {
            Patient updated = gson.fromJson(body, Patient.class);
            if (patientDAO.updatePatient(id, updated)) {
                Patient patient = patientDAO.getPatientById(id);
                sendResponse(exchange, 200, gson.toJson(patient));
            } else {
                sendError(exchange, 404, "Patient not found");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            sendError(exchange, 400, "Invalid JSON");
        }
    }

    private void handleDeletePatient(HttpExchange exchange, int id) throws IOException {
        try {
            if (patientDAO.deletePatient(id)) {
                sendResponse(exchange, 200, "{\"message\":\"Deleted\"}");
            } else {
                sendError(exchange, 404, "Patient not found");
            }
        } catch (SQLException e) {
            sendError(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handleSearchPatient(HttpExchange exchange, String query) throws IOException {
        String[] params = query.split("=");
        if (params.length == 2 && "name".equals(params[0])) {
            String name = params[1].replace("%20", " ");
            try {
                String response = gson.toJson(patientDAO.searchByName(name));
                sendResponse(exchange, 200, response);
            } catch (SQLException e) {
                sendError(exchange, 500, "Database error: " + e.getMessage());
            }
        } else {
            sendError(exchange, 400, "Invalid search parameter");
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