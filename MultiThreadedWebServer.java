import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MultiThreadedWebServer {
    public static final int PORT = 8080;
    public static final String WEB_ROOT = "www";
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream outputStream = clientSocket.getOutputStream()) {
            
            String requestLine = in.readLine();
            if (requestLine == null || !requestLine.startsWith("GET")) {
                sendResponse(outputStream, "400 Bad Request", "text/html", "<h1>400 Bad Request</h1>");
                return;
            }
            
            String fileName = requestLine.split(" ")[1];
            if (fileName.equals("/")) {
                fileName = "/index.html";
            }
            
            File file = new File(MultiThreadedWebServer.WEB_ROOT + fileName);
            System.out.println("Thread " + Thread.currentThread().getName() + " serving: " + fileName);
            
            if (file.exists() && !file.isDirectory()) {
                sendFileResponse(outputStream, "200 OK", file);
            } else {
                sendResponse(outputStream, "404 Not Found", "text/html", "<h1>404 Not Found</h1>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendResponse(OutputStream out, String status, String contentType, String content) throws IOException {
        PrintWriter writer = new PrintWriter(out, true);
        writer.println("HTTP/1.1 " + status);
        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + content.length());
        writer.println("Connection: close");
        writer.println();
        writer.println(content);
    }

    private void sendFileResponse(OutputStream out, String status, File file) throws IOException {
        PrintWriter writer = new PrintWriter(out, true);
        writer.println("HTTP/1.1 " + status);
        writer.println("Content-Type: " + getMimeType(file.getName()));
        writer.println("Content-Length: " + file.length());
        writer.println("Connection: close");
        writer.println();
        
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        fis.close();
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }
}
