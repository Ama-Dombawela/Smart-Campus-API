# Smart-Campus-API

**Module:** 5COSC022W - Client-Server Architectures  
**Student Name:** Ama Dombawela 
**UOW No:** W2120682 
**IIT Student No**: 20231642  

---

## Overview

The Smart Campus API is a RESTful web service implemented using **JAX-RS (Jersey 2.32)** and packaged as a **WAR** file for deployment on **Apache Tomcat**.

The system is designed to manage three linked campus entities:

- **Rooms** (physical spaces on campus)
- **Sensors** (devices installed in rooms)
- **SensorReadings** (historical measurements per sensor)

Data persistence is handled using an **in-memory datastore** (`CampusDataStore`) backed by `ConcurrentHashMap` collections. No external relational or NoSQL database is used.

---

## API Design

The JAX-RS application class `SmartCampusApplication` defines the API base path as:

`@ApplicationPath("/api/v1")`

All endpoints are therefore rooted at:

`/<context-path>/api/v1`

- Base URL depends on the Tomcat deployment context path.
- The fixed JAX-RS application root is `/api/v1`.

For this current setup, the base URL is:

`http://localhost:8080/SmartCampusAPI/api/v1`

Registered resource classes:

- `DiscoveryResource` (`@Path("/")`) for API metadata
- `RoomResource` (`@Path("/rooms")`) for room operations
- `SensorResource` (`@Path("/sensors")`) for sensor operations
- `SensorReadingResource` accessed through a sub-resource locator at `@Path("/{sensorId}/readings")`

Sub-resource rationale for `/sensors/{sensorId}/readings`:

- It models the real-world hierarchy where readings belong to a specific sensor.
- It keeps reading operations modular and separated from core sensor CRUD responsibilities.

### Discovery Endpoint Clarification

- `GET /api/v1` is the API entry point.
- In usage examples, this is called as `GET http://localhost:8080/SmartCampusAPI/api/v1/` (same root endpoint with trailing slash).
- It returns API metadata (version, description, contact).
- It also returns navigation links to `/rooms` and `/sensors`.

### HATEOAS (Hypermedia)

- HATEOAS is a REST principle where responses include links to discover available resources dynamically.
- In this API, the Discovery endpoint provides navigable links to `/api/v1/rooms` and `/api/v1/sensors`.

### JAX-RS Resource Lifecycle

- JAX-RS resource classes are typically request-scoped (a new instance is created per request).
- This avoids shared mutable state inside resource classes.
- Shared data is managed in `CampusDataStore`, which uses `ConcurrentHashMap` for thread-safe concurrent access.

---

## Resource Structure

The implemented hierarchy is:

```text
/api/v1/
├── GET /                                -> DiscoveryResource.discover()
├── /rooms                               -> RoomResource
│   ├── GET /rooms                       -> getAllRooms()
│   ├── POST /rooms                      -> createRoom()
│   ├── GET /rooms/{roomId}              -> getRoomById()
│   └── DELETE /rooms/{roomId}           -> deleteRoom()
└── /sensors                             -> SensorResource
    ├── GET /sensors                     -> getAllSensors(type?)
    ├── POST /sensors                    -> createSensor()
    ├── GET /sensors/{sensorId}          -> getSensorById()
    └── /sensors/{sensorId}/readings     -> SensorReadingResource (sub-resource locator)
        ├── GET /sensors/{sensorId}/readings   -> getReadings()
        └── POST /sensors/{sensorId}/readings  -> addReading()
```

---

## Key Design Decisions

- **Thread-safe in-memory storage:** `CampusDataStore` uses `ConcurrentHashMap` for rooms, sensors, and readings.
- **Sub-resource locator pattern:** `SensorResource#getReadingResource(...)` delegates reading operations to `SensorReadingResource`.
- **Custom exception mapping:** dedicated `ExceptionMapper` classes provide controlled JSON error responses for business/runtime failures.
- **Business rule 1:** a room cannot be deleted if it still has linked sensors.
- **Business rule 2:** sensors with status `MAINTENANCE` cannot accept new readings.
- **Consistency rule:** when a new reading is posted, the parent `Sensor.currentValue` is updated automatically.

---

## Project Structure

```text
SmartCampusAPI/
├── pom.xml
├── LICENSE
├── nb-configuration.xml
├── src/
│   ├── main/
│   │   ├── java/com/smartcampus/api/
│   │   │   ├── config/
│   │   │   │   └── SmartCampusApplication.java
│   │   │   ├── datastore/
│   │   │   │   └── CampusDataStore.java
│   │   │   ├── exception/
│   │   │   │   ├── LinkedResourceNotFoundException.java
│   │   │   │   ├── RoomNotEmptyException.java
│   │   │   │   ├── SensorUnavailableException.java
│   │   │   │   └── mapper/
│   │   │   │       ├── GlobalExceptionMapper.java
│   │   │   │       ├── LinkedResourceNotFoundExceptionMapper.java
│   │   │   │       ├── RoomNotEmptyExceptionMapper.java
│   │   │   │       └── SensorUnavailableExceptionMapper.java
│   │   │   ├── filter/
│   │   │   │   └── LoggingFilter.java
│   │   │   ├── model/
│   │   │   │   ├── ErrorMessage.java
│   │   │   │   ├── Room.java
│   │   │   │   ├── Sensor.java
│   │   │   │   └── SensorReading.java
│   │   │   └── resource/
│   │   │       ├── BaseResource.java
│   │   │       ├── DiscoveryResource.java
│   │   │       ├── RoomResource.java
│   │   │       ├── SensorResource.java
│   │   │       └── SensorReadingResource.java
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml
│   │   └── webapp/
│   │       ├── index.html
│   │       ├── META-INF/
│   │       │   └── context.xml
│   │       └── WEB-INF/
│   │           ├── beans.xml
│   │           └── web.xml
│   └── test/
│       └── java/
└── target/
```

### In-Memory Data Management

The application uses in-memory storage via **CampusDataStore** for managing all Rooms, Sensors, and SensorReadings data.

---

## Prerequisites

Before building and running the project, install:

- **Java JDK 8** or newer
- **Apache Maven 3.6+**
- **Apache Tomcat 9+**

---

## Build and Run Instructions

### 1. Build the WAR

From `SmartCampusAPI/`:

```bash
mvn clean package
```

Expected output WAR (from `pom.xml`):

```text
target/SmartCampusAPI-1.0-SNAPSHOT.war
```

### 2. Deploy to Tomcat

Copy `target/SmartCampusAPI-1.0-SNAPSHOT.war` to Tomcat's `webapps/` directory, then start Tomcat.

### 3. Verify Base URL

With the current Tomcat deployment context on this setup:

```text
http://localhost:8080/SmartCampusAPI/api/v1
```

---

## Full API Endpoint Reference

### Part 1: Discovery

| Method | URL | Description |
|---|---|---|
| GET | `/api/v1/` | API metadata and navigation links |

### Part 2: Room Management

| Method | URL | Description |
|---|---|---|
| GET | `/api/v1/rooms` | List all rooms |
| POST | `/api/v1/rooms` | Create a new room |
| GET | `/api/v1/rooms/{roomId}` | Retrieve a specific room |
| DELETE | `/api/v1/rooms/{roomId}` | Delete a room (fails if sensors linked) |

### Part 3: Sensor Operations

| Method | URL | Description |
|---|---|---|
| GET | `/api/v1/sensors` | List all sensors |
| GET | `/api/v1/sensors?type=<type>` | Filter sensors by type (e.g., ?type=Temperature) |
| POST | `/api/v1/sensors` | Register a new sensor |
| GET | `/api/v1/sensors/{sensorId}` | Retrieve a specific sensor |

### Part 4: Sensor Readings (Sub-Resource)

| Method | URL | Description |
|---|---|---|
| GET | `/api/v1/sensors/{sensorId}/readings` | Get all readings for a sensor |
| POST | `/api/v1/sensors/{sensorId}/readings` | Add a new reading for a sensor |

---

## Sample cURL Commands

The examples below use full URLs directly (base: `http://localhost:8080/SmartCampusAPI/api/v1`).

### Discovery

#### GET / — Retrieve API metadata

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/"
```

---

### Room Management

#### POST /rooms — Create a new room

```bash
curl -X POST "http://localhost:8080/SmartCampusAPI/api/v1/rooms" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 120
  }'
```

#### GET /rooms — List all rooms

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/rooms"
```

#### GET /rooms/{roomId} — Retrieve a specific room

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301"
```

#### DELETE /rooms/{roomId} — Delete a room

```bash
curl -X DELETE "http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301"
```

---

### Sensor Operations

#### POST /sensors — Register a new sensor

```bash
curl -X POST "http://localhost:8080/SmartCampusAPI/api/v1/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "LIB-301"
  }'
```

#### GET /sensors — List all sensors

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors"
```

#### GET /sensors?type=<type> — Filter sensors by type

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature"
```

#### GET /sensors/{sensorId} — Retrieve a specific sensor

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001"
```

---

### Sensor Readings (Sub-Resource)

#### POST /sensors/{sensorId}/readings — Add a new reading

```bash
curl -X POST "http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings" \
  -H "Content-Type: application/json" \
  -d '{
    "value": 23.6
  }'
```

#### GET /sensors/{sensorId}/readings — Retrieve all readings for a sensor

```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings"
```

---

## Error Handling

The API uses custom `ExceptionMapper` implementations for controlled error responses:

| Exception Class | Mapper Class | HTTP Status | Meaning |
|---|---|---|---|
| `RoomNotEmptyException` | `RoomNotEmptyExceptionMapper` | `409 Conflict` | Attempted to delete a room that still has linked sensors |
| `LinkedResourceNotFoundException` | `LinkedResourceNotFoundExceptionMapper` | `422 Unprocessable Entity` | Linked room does not exist when creating a sensor |
| `SensorUnavailableException` | `SensorUnavailableExceptionMapper` | `403 Forbidden` | Sensor is in `MAINTENANCE` and cannot accept readings |
| Any unhandled exception | `GlobalExceptionMapper` | `500 Internal Server Error` | Generic server-side failure response |

In addition, `BaseResource` contains shared helper methods used by resource classes to build common JSON responses (including `400`, `404`, and `409`) in non-exception flow paths.

---

## Notes 

- The implementation follows REST resource separation with clear URI design.
- Thread-safety and request concurrency are addressed through `ConcurrentHashMap` and synchronized write sections where required.
- The codebase demonstrates custom error handling, sub-resource location, and practical business rule enforcement in a Tomcat-deployable JAX-RS application.
