#Smart-Campus-API
**Module:** 5COSC022W - Client-Server Architectures  
**Author:** Ama Dombawela | UOW No: W2120682 | IIT Student No: 20231642  
**Version:** 1.0  

---

## Overview

The Smart Campus API is a RESTful web service built using **JAX-RS (Jersey 2.32)** and packaged as a **WAR file** deployable on **Apache Tomcat**. It provides a comprehensive backend for managing university campus infrastructure — specifically Rooms and the Sensors deployed within them — along with historical Sensor Readings.

### API Design

The API follows REST architectural principles with a versioned base path of `/api/v1`. It is organized around three primary resources:

| Resource | Base Path | Description |
|---|---|---|
| Discovery | `GET /api/v1/` | API metadata and resource links |
| Rooms | `/api/v1/rooms` | Manage campus rooms |
| Sensors | `/api/v1/sensors` | Manage sensors and their readings |

### Resource Hierarchy

/api/v1/
├── rooms/
│   ├── GET    /              → List all rooms
│   ├── POST   /              → Create a new room
│   ├── GET    /{roomId}      → Get a specific room
│   └── DELETE /{roomId}      → Delete a room (blocked if sensors exist)
└── sensors/
├── GET    /              → List all sensors (optional ?type= filter)
├── POST   /              → Register a new sensor
├── GET    /{sensorId}    → Get a specific sensor
└── /{sensorId}/readings/
├── GET  /            → Get all readings for a sensor
└── POST /            → Add a new reading for a sensor


### Key Design Decisions

- **In-memory data store** using `ConcurrentHashMap` via `CampusDataStore` — no database required.
- **Sub-resource locator pattern** used for sensor readings to keep code modular and maintainable.
- **Custom Exception Mappers** for all error scenarios — no raw stack traces are ever exposed.
- **Business Logic Constraints** — a room cannot be deleted if it has sensors assigned; a sensor under `MAINTENANCE` cannot accept new readings.
- **Data Consistency** — posting a new reading automatically updates the parent sensor's `currentValue`.

---

## Project Structure
SmartCampusAPI/
├── src/
│   └── main/
│       ├── java/com/smartcampus/api/
│       │   ├── config/
│       │   │   └── SmartCampusApplication.java       # JAX-RS app config & @ApplicationPath
│       │   ├── datastore/
│       │   │   └── CampusDataStore.java              # In-memory data store (ConcurrentHashMap)
│       │   ├── exception/
│       │   │   ├── LinkedResourceNotFoundException.java
│       │   │   ├── RoomNotEmptyException.java
│       │   │   ├── SensorUnavailableException.java
│       │   │   └── mapper/
│       │   │       ├── GlobalExceptionMapper.java                 # Catches all unexpected errors (500)
│       │   │       ├── LinkedResourceNotFoundExceptionMapper.java # 422
│       │   │       ├── RoomNotEmptyExceptionMapper.java           # 409
│       │   │       └── SensorUnavailableExceptionMapper.java      # 403
│       │   ├── filter/
│       │   │   └── LoggingFilter.java                # Request/response logging
│       │   ├── model/
│       │   │   ├── ErrorMessage.java                 # Standard error response POJO
│       │   │   ├── Room.java
│       │   │   ├── Sensor.java
│       │   │   └── SensorReading.java
│       │   └── resource/
│       │       ├── BaseResource.java                 # Shared helper methods
│       │       ├── DiscoveryResource.java            # GET /api/v1/
│       │       ├── RoomResource.java                 # /api/v1/rooms
│       │       ├── SensorResource.java               # /api/v1/sensors
│       │       └── SensorReadingResource.java        # /api/v1/sensors/{id}/readings
│       ├── resources/
│       │   └── META-INF/
│       │       └── persistence.xml
│       └── webapp/
│           ├── META-INF/
│           │   └── context.xml
│           ├── WEB-INF/
│           │   ├── beans.xml
│           │   └── web.xml
│           └── index.html
├── .gitignore
├── LICENSE
└── pom.xml


---

## Prerequisites

Before building and running the project, ensure you have the following installed:

- **Java JDK 8** or higher
- **Apache Maven 3.6+**
- **Apache Tomcat 9** (or any Servlet 3.x compatible container)

---

## Build & Run Instructions

### Step 1 — Clone the Repository

```bash
git clone https://github.com/Ama-Dombawela/Smart-Campus-API.git
cd Smart-Campus-API/SmartCampusAPI
```

### Step 2 — Build the Project

```bash
mvn clean package
```

Once successful, the WAR file will be at:
