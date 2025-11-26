package com.boeani.chatapp.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public record ClientHandler(Socket socket, Path webrootPath) implements Runnable {

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
             BufferedReader clientReader = new BufferedReader(new InputStreamReader(input));

             OutputStream output = socket.getOutputStream();
             DataOutputStream writer = new DataOutputStream(output)
        ) {
            String[] request;
            String firstLine = clientReader.readLine();
            String currentLine;

            if (firstLine != null && !firstLine.isEmpty()) {
                do {
                    currentLine = clientReader.readLine();
                } while (currentLine != null && !currentLine.isEmpty());

                request = firstLine.split(" ");

                if (request.length != 3) {
                    System.err.println("Invalid request line format!");
                    socket.close();
                } else {
                    String httpMethod = request[0];
                    String requestedPath = request[1];

                    if (!httpMethod.equals("GET")) {
                        System.out.printf("Method not valid: %s%n", httpMethod);
                        socket.close();
                        return;
                    }

                    if (requestedPath.equals("/")) {
                        requestedPath = "index.html";
                    }

                    Path normalizedTargetFilePath = getPath(requestedPath);
                    Path absoluteWebrootPath = webrootPath.toAbsolutePath().normalize();

                    // Jetzt werden ZWEI ABSOLUTE UND NORMALISIERTE PFADE verglichen:
                    // Z.B. Path.of("C:\...\webroot\index.html").startsWith(Path.of("C:\...\webroot"))
                    // Das wird jetzt korrekt zu 'true' ausgewertet!
                    if (!normalizedTargetFilePath.startsWith(absoluteWebrootPath)) {
                        System.err.println("Security warning: Attempted access outside webroot: " + normalizedTargetFilePath);
                        socket.close();
                        return;
                    }
                    // ...

                    if (Files.exists(normalizedTargetFilePath) && !Files.isDirectory(normalizedTargetFilePath) && Files.isReadable(normalizedTargetFilePath)) {
                        long fileSize = Files.size(normalizedTargetFilePath);
                        String contentType = getString(normalizedTargetFilePath);

                        writer.writeBytes("HTTP/1.1 200 OK\r\n");
                        writer.writeBytes("Content-Type: " + contentType + "\r\n");
                        writer.writeBytes("Content-Length: " + fileSize + "\r\n");
                        writer.writeBytes("\r\n");
                        writer.flush();

                        try (InputStream inputStream = Files.newInputStream(normalizedTargetFilePath)) {
                            byte[] buffer = new byte[4096];

                            int bytesRead;

                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                writer.write(buffer, 0, bytesRead);
                            }
                        }
                        writer.flush();
                    } else {
                        String errorHtml = "<h1>404 Not Found</h1><p>The requested resource " + requestedPath + " could not be found.</p>";
                        int errorLength = errorHtml.getBytes().length;
                        writer.writeBytes("HTTP/1.1 404 Not Found");
                        writer.writeBytes("Content-Type: text/html\r\n");
                        writer.writeBytes("Content-Length: " + errorLength + "\r\n");
                        writer.writeBytes("\r\n");
                        writer.flush();
                        writer.write(errorHtml.getBytes());
                        writer.flush();
                    }
                }
            } else {
                System.err.println("Invalid lines in document!");
            }
        } catch (IOException e) {
            System.err.println("Error processing client request: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private String getString(Path normalizedTargetFilePath) {
        Path fileName = normalizedTargetFilePath.getFileName();
        int lastIndexOfDot = fileName.toString().lastIndexOf(".");

        String fileExtension = fileName.toString().substring(lastIndexOfDot + 1);

        String contentType;

        if (lastIndexOfDot == -1) {
            fileExtension = "";
        }

        contentType = switch (fileExtension) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "text/javascript";
            case "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "txt" -> "text/plain";
            default -> "application/octet-stream";
        };
        return contentType;
    }

    private Path getPath(String requestedPath) {
        String pathForResolution = requestedPath;
        if (pathForResolution.startsWith("/")) {
            pathForResolution = pathForResolution.substring(1); // Entferne den führenden "/"
        }

        // ...
        Path targetFilePath = webrootPath.resolve(Path.of(pathForResolution));
        // NEU: Mache den targetFilePath ZUERST absolut und DANN normalisiere ihn!
        // <--- HIER IST DIE ANPASSUNG!
        return targetFilePath.toAbsolutePath().normalize();
    }
}
