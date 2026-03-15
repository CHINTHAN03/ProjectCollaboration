# Propulsion Workspace 

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Javalin](https://img.shields.io/badge/Javalin-6.1.3-blue.svg)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green.svg)
![Vanilla JS](https://img.shields.io/badge/JavaScript-Vanilla-yellow.svg)

Propulsion Workspace is a high-performance, lightweight project collaboration and task intelligence system. Engineered with a responsive vanilla web frontend and a lightning-fast Java REST API, it provides teams with an intuitive Kanban interface, role-based access control, and an automated workload-balancing intelligence engine.

## Table of Contents
- [Features](#-features)
- [Gallery](#-gallery)
- [Architecture & Tech Stack](#-architecture--tech-stack)
- [API Reference](#-api-reference)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)

##  Features

* **Intelligence Engine (Workload Balancer):** A custom, algorithmic recommendation engine that analyzes active project workloads across the team and automatically suggests the optimal developer for new tasks to mitigate burnout.
* **Interactive Kanban Board:** Frictionless drag-and-drop task management across distinct workflow states (To Do, In Progress, Review, Completed).
* **Role-Based Access Control (RBAC):** Distinct permission hierarchies for Admins, Project Managers, and Developers.
* **Admin Security Console:** A secure, dedicated interface for managing user access and executing critical system-wide operations (e.g., database purging).
* **Real-Time Analytics Dashboard:** Visual metrics tracking project health, task distribution, and overall completion percentages.
* **Collaborative Task Threads:** Dedicated discussion feeds and timestamped comments attached to individual task details.

## Gallery

> **Note:** Screenshots are stored in the `/docs` directory of this repository.

### Workspace & Kanban Board
![Main Dashboard](docs/dashboard-kanban.png)
*The core workspace featuring the interactive Kanban board, role-based UI toggles, and real-time project health analytics.*

### Collaborative Task Management
![Task Details & Comments](docs/task-details.png)
*The slide-over task panel featuring real-time discussion feeds, status tracking, and assignment details.*

### Task Intelligence Engine
![Intelligence Engine](docs/intelligence-engine.png)
*The task creation module showcasing the custom algorithm's optimal assignee recommendation based on active team workloads.*

### Admin Security Console
![Admin Console](docs/admin-console.png)
*The secure administrative dashboard restricted to Admin roles, utilized for user directory management and system-wide data purges.*
##  Architecture & Tech Stack

The application is built with a clean separation of concerns, utilizing a custom-built REST API that communicates with a NoSQL database, consumed entirely by a vanilla frontend.

**Backend**
* **Core:** Java 21
* **Framework:** [Javalin 6.1.3](https://javalin.io/) (Lightweight, asynchronous REST API)
* **Database:** MongoDB Atlas (Native `mongodb-driver-sync` 5.0.0)
* **Data Binding:** Jackson Databind

**Frontend**
* **Core:** HTML5, CSS3, Vanilla JavaScript
* **Communication:** Fetch API for asynchronous HTTP requests
* **UI/UX:** Custom CSS variables, responsive slide-over panels, interactive modals, and drag-and-drop mechanics.

##  API Reference

The backend exposes a comprehensive RESTful API. Key endpoints include:

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/login` | Authenticate user and retrieve role | Public |
| `GET` | `/api/projects/{id}/tasks` | Fetch all tasks for a specific project | Authenticated |
| `PUT` | `/api/tasks/{id}/status` | Update task workflow status | Authenticated |
| `GET` | `/api/projects/{id}/recommend`| **Triggers the Intelligence Engine** | Project Manager |
| `DELETE` | `/api/admin/wipe` | Purge workspace data | Admin Only |

##  Getting Started

### Prerequisites
* Java Development Kit (JDK) 21+
* Maven
* MongoDB Atlas Cluster (or local MongoDB instance)

### Installation

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/CHINTHAN03/ProjectCollaboration.git](https://github.com/CHINTHAN03/ProjectCollaboration.git)
   cd ProjectCollaboration

2. **Database Configuration:**
   * Navigate to `src/main/java/utils/DatabaseConnection.java`.
   * Update the `CONNECTION_STRING` variable with your active MongoDB URI.

3. **Build the Project:**
   ```bash
   mvn clean install
   ```

4. **Initialize the Server:**
   * Run the `main` method in `src/main/java/App.java`. The Javalin server will initialize on `http://localhost:7070`.

5. **Launch the Client:**
   * Open `frontend/index.html` in any modern web browser to access the application.

## 📂 Project Structure

```text
third_project/
├── frontend/
│   ├── css/
│   │   ├── dashboard.css
│   │   └── style.css
│   ├── js/
│   │   ├── auth.js
│   │   └── dashboard.js
│   ├── dashboard.html
│   └── index.html
├── src/main/java/
│   ├── dao/                 # Data Access Objects (MongoDB Operations)
│   ├── models/              # Application Data Entities
│   ├── utils/               # Database Singleton & Helpers
│   └── App.java             # Main Javalin Server Entry Point
└── pom.xml                  # Maven Configuration & Dependencies
```