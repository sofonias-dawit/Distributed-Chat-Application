# Distributed Chat Application (Java)

A multi-client, real-time chat application built in **Java** to demonstrate core **Distributed Systems** concepts. The system uses a centralized server to manage concurrent clients, relay messages, and maintain shared state. Clients interact through a simple and modern **Java Swing** GUI.

This project was developed for a Distributed Systems course.
---
## Features

* Real-time group chat
* Live online user list
* Private messaging using `/pm <username> <message>`
* Server-side message timestamps
* Multi-threaded server for concurrent clients
* Graceful handling of client disconnections
* Clean Java Swing UI (Nimbus Look and Feel)
---
## Technologies Used

* **Java** (JDK 11+)
* **Java Swing** (GUI)
* **Java Sockets** (`ServerSocket`, `Socket`)
* **Multithreading** (`Thread`, `Runnable`)
---
## How to Run

### Prerequisites

Ensure Java 11 or newer is installed:

```bash
java -version
```

### Steps

1. **Clone the repository**

```bash
git clone https://github.com/sofonias-dawit/Distributed-Chat-Application.git
cd Distributed-Chat-Application
```

2. **Compile**

```bash
javac *.java
```

3. **Start the Server**

```bash
java Server
```

4. **Start Clients** (open a new terminal for each)

```bash
java ChatClient
```

Enter a unique username and begin chatting.

---

## System Architecture

* **Server:**
  Uses a `ServerSocket` and a thread-per-client model. Maintains connected users using a thread-safe map and handles public and private message routing.

* **Client:**
  Java Swing application using a responsive two-thread model (UI thread + background listener thread).

* **Protocol:**
  Text-based TCP protocol supporting public messages, private messages (`/pm`), and user list updates.

---

## Project Structure

```
/
|-- Server.java
|-- ClientHandler.java
|-- ChatClient.java
|-- README.md
```

---

## Future Enhancements

* Multiple chat rooms
* SSL/TLS secure communication
* Persistent chat history
* Server replication for fault tolerance

---
Thanks!
