# Simple Java HTTP Server for Static Files

## Project Overview

This is a minimalist HTTP server implemented in Java. Its primary purpose is to serve static files (like HTML, CSS, images, and text files) from a designated `webroot` directory to web browsers. This project was developed as part of a learning journey to deeply understand Java's networking capabilities and the fundamentals of the HTTP protocol.

## Features

*   **Multi-threaded Architecture:** Handles multiple client connections concurrently using Java's `Thread` and `Runnable` interfaces.
*   **Basic HTTP/1.1 GET Request Handling:** Parses incoming HTTP GET requests.
*   **Static File Serving:** Delivers files from a configurable `webroot` directory.
*   **Content-Type Detection:** Automatically determines the `Content-Type` (MIME type) based on file extensions.
*   **Robust Path Handling:** Correctly resolves and normalizes file paths.
*   **Security Safeguards:** Prevents directory traversal attacks (e.g., `../` attempts) to ensure files outside the `webroot` cannot be accessed.
*   **Error Handling:** Provides custom 404 Not Found responses for non-existent resources and logs other server-side errors.
*   **Resource Management:** Utilizes `try-with-resources` for automatic and reliable closing of network streams and file I/O.

## Learning Goals Achieved

Through the development of this server, I gained a comprehensive understanding of:

*   **Java Socket Programming:** Using `ServerSocket` and `Socket` for TCP communication.
*   **Concurrency:** Implementing `Runnable` and `Thread` for multi-client support.
*   **HTTP Protocol Basics:** Understanding the structure of request lines, headers, and responses (Status Line, Headers, Body).
*   **File I/O with `java.nio.file`:** Working with `Path`, `Files`, `InputStream`, and `OutputStream` for secure and efficient file access.
*   **Stream Management:** Best practices for resource handling using `try-with-resources`.
*   **Defensive Programming:** Implementing security checks and robust error handling.

## How to Run

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/boeani05/JavaChatApp.git
    cd JavaChatApp
    ```
2.  **Create the `webroot` Directory:**
    Ensure there is a directory named `webroot` in the root of your project (next to the `src` folder).
3.  **Place Static Files:**
    Populate the `webroot` directory with your static content (e.g., `index.html`, `style.css`, `about.html`, `favicon.ico`, `image.png`).
    *Example `index.html`:*
    ```html
    <!DOCTYPE html>
    <html>
    <head><title>My Java HTTP Server</title></head>
    <body>
        <h1>Hello from my Java Server!</h1>
        <p>This is a static file served by my custom HTTP server.</p>
        <p><a href="/about.html">Learn more</a></p>
    </body>
    </html>
    ```
4.  **Compile the Java Code:**
    ```bash
    javac src/main/java/com/boeani/chatapp/server/*.java
    ```
5.  **Start the Server:**
    ```bash
    java src/main/java/com/boeani/chatapp/server/ServerApp
    ```
    You should see "Starting HTTP Server..." in your console. The server will then listen for incoming connections.

## How to Test

1.  **Open your Web Browser:**
    Navigate to `http://localhost:8080/`
    *Expected:* Your `index.html` content should be displayed.
2.  **Test specific files:**
    Try `http://localhost:8080/about.html`, `http://localhost:8080/style.css`, etc.
    *Expected:* The respective files should be served.
3.  **Test 404 Not Found:**
    Navigate to `http://localhost:8080/nonexistent.html`
    *Expected:* A custom "404 Not Found" page should be displayed in the browser, and a "Resource not found" message in the server console.
4.  **Test Security (Directory Traversal):**
    Navigate to `http://localhost:8080/../ServerApp.java` (or any file outside `webroot`)
    *Expected:* A "404 Not Found" page in the browser, and a "Security warning: Attempted access outside webroot..." message in the server console.
5.  **Test `favicon.ico` (automatic browser request):**
    Simply accessing `http://localhost:8080/` should trigger your browser to request `/favicon.ico` automatically.
    *Expected:* No security warnings or errors in the server console for `favicon.ico`.
