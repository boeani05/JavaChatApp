package com.boeani.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class ServerApp {
    private ServerSocket serverSocket;
    private final Path webrootPath;

    public ServerApp() {
        this.webrootPath = Path.of("./webroot");

        try {
            this.serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.err.println("An error occurred while trying to open a socket!");
        }
    }

    public void startServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler newHandler = new ClientHandler(socket, webrootPath);

                new Thread(newHandler).start();
            } catch (IOException e) {
                System.err.println("An error occurred while trying to connect to the server!");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting HTTP Server...");
        ServerApp server = new ServerApp();
        server.startServer();
    }
}
