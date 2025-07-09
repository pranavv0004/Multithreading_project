# Multithreading Project

## Overview

Developed a multi-threaded web server in Java, capable of handling multiple client requests concurrently. This project demonstrates efficient server-side programming using modern Java concurrency features and custom HTTP protocol handling.

## Features

- **Multi-threading with Thread Pool**
  - Efficiently manages and serves multiple client requests in parallel using a thread pool.

- **Static File Hosting**
  - Serves static content including HTML, CSS, JavaScript, images, and JSON files.

- **Custom HTTP Request Parsing**
  - Implements a custom parser for HTTP requests, supporting various methods and headers.
  - Detects and serves files with correct MIME types.

- **Optimized Response Handling**
  - Sends appropriate HTTP response headers, including `Content-Length` and `Connection: close`, for optimal client compatibility.

- **Basic Error Handling**
  - Returns `400 Bad Request` for malformed requests.
  - Returns `404 Not Found` for missing resources.
