import com.sun.net.httpserver.*;
import handler.MedicineHandler;
import handler.PatientHandler;
import dao.DBUtil;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("   Apotek Sehat Sentosa - Server");
        System.out.println("========================================\n");
        
        // Test database connection
        System.out.println("[Server] Testing database connection...");
        DBUtil.testConnection();
        
        System.out.println("\n[Server] Creating HTTP server...");
        
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(8080), 0);
        
        // Routes
        server.createContext("/api/medicines", new MedicineHandler());
        server.createContext("/api/patients", new PatientHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("[Server] ✓ HTTP Server started on port 8080");
        System.out.println("\n[API Endpoints]");
        System.out.println("  GET    http://localhost:8080/api/medicines");
        System.out.println("  POST   http://localhost:8080/api/medicines");
        System.out.println("  GET    http://localhost:8080/api/patients");
        System.out.println("  POST   http://localhost:8080/api/patients");
        System.out.println("\n[Frontend URLs]");
        System.out.println("  Live Server: http://localhost:5500/frontend/");
        System.out.println("  Python HTTP: http://localhost:8000");
        System.out.println("\n========================================\n");
    }
}