# 🏫 Smart-Campus-API

**Module:** 5COSC022W - Client-Server Architectures  
**Student Name:** Ama Dombawela  
**UOW No:** W2120682  
**IIT Student No:** 20231642 

---

## 📋 Overview

The **Smart Campus API** is a RESTful web service built using **JAX-RS (Jersey 2.32)** and packaged as a **WAR file** for deployment on **Apache Tomcat**. It provides a backend system for managing university campus infrastructure, including **Rooms**, the **Sensors installed within them**, and the associated **Sensor Readings** used to record historical data over time.

The system is designed to manage three linked campus entities:

- **Rooms** (physical spaces on campus)
- **Sensors** (devices installed in rooms)
- **SensorReadings** (historical measurements per sensor)

---

## 🏗️ API Design

The JAX-RS application class `SmartCampusApplication` defines the API base path as:

`@ApplicationPath("/api/v1")`

---

All endpoints are therefore rooted at:

`/<context-path>/api/v1`

- Base URL depends on the Tomcat deployment context path.
- The fixed JAX-RS application root is `/api/v1`.

---

For this current setup, the base URL is:

`http://localhost:8080/SmartCampusAPI/api/v1`

---

Registered resource classes:

- `DiscoveryResource` (`@Path("/")`) for API metadata
- `RoomResource` (`@Path("/rooms")`) for room operations
- `SensorResource` (`@Path("/sensors")`) for sensor operations
- `SensorReadingResource` accessed through a sub-resource locator at `@Path("/{sensorId}/readings")`

---

Sub-resource rationale for `/sensors/{sensorId}/readings`:

- It models the real-world hierarchy where readings belong to a specific sensor.
- It keeps reading operations modular and separated from core sensor CRUD responsibilities.

---

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

## 🎯 Key Design Decisions

- **Thread-safe in-memory storage:** `CampusDataStore` uses `ConcurrentHashMap` for rooms, sensors, and readings.
- **Sub-resource locator pattern:** `SensorResource#getReadingResource(...)` delegates reading operations to `SensorReadingResource`.
- **Custom exception mapping:** dedicated `ExceptionMapper` classes provide controlled JSON error responses for business/runtime failures.
- **Business rule 1:** a room cannot be deleted if it still has linked sensors.
- **Business rule 2:** sensors with status `MAINTENANCE` cannot accept new readings.
- **Consistency rule:** when a new reading is posted, the parent `Sensor.currentValue` is updated automatically.

---

## 📁 Project Structure

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

The application uses in-memory storage via CampusDataStore, backed by ConcurrentHashMap. No external relational or NoSQL database is used

---

## ⚙️ Prerequisites

Before building and running the project, install:

- **Java JDK 8** or newer
- **Apache Maven 3.6+**
- **Apache Tomcat 9+**

---

## 🚀 Git Workflow / Setup Instructions

Clone the repository to your local machine:

```bash
git clone https://github.com/Ama-Dombawela/Smart-Campus-API.git
```

Navigate into the project folder:

```bash
cd SmartCampusAPI
```

---

## 🔨 Build and Run Instructions

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

## 📚 Full API Endpoint Reference

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

## 💻 Sample cURL Commands

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

## ⚠️ Error Handling

The API uses custom `ExceptionMapper` implementations for controlled error responses:

| Exception Class | Mapper Class | HTTP Status | Meaning |
|---|---|---|---|
| `RoomNotEmptyException` | `RoomNotEmptyExceptionMapper` | `409 Conflict` | Attempted to delete a room that still has linked sensors |
| `LinkedResourceNotFoundException` | `LinkedResourceNotFoundExceptionMapper` | `422 Unprocessable Entity` | Linked room does not exist when creating a sensor |
| `SensorUnavailableException` | `SensorUnavailableExceptionMapper` | `403 Forbidden` | Sensor is in `MAINTENANCE` and cannot accept readings |
| Any unhandled exception | `GlobalExceptionMapper` | `500 Internal Server Error` | Generic server-side failure response |

In addition, `BaseResource` contains shared helper methods used by resource classes to build common JSON responses (including `400`, `404`, and `409`) in non-exception flow paths.

---

## 📝 Notes 

- The implementation follows REST resource separation with clear URI design.
- Thread-safety and request concurrency are addressed through `ConcurrentHashMap` and synchronized write sections where required.
- The codebase demonstrates custom error handling, sub-resource location, and practical business rule enforcement in a Tomcat-deployable JAX-RS application.

---

## 📝 Conceptual Report

### Part 1: Service Architecture & Setup

#### Question 1
Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

#### Answer
- JAX-RS resource classes are request-scoped by default, as defined by the JAX-RS specification. This means the runtime creates a new instance of the resource class for each incoming HTTP request and discards it once the response has been sent. The resource class is never reused across multiple requests.
- This lifecycle has a direct consequence on in-memory data management. Any data stored as an instance variable within a resource class would be lost after each request, since the object holding it is destroyed. Storing rooms or sensors directly inside a resource class would therefore result in an empty data set on every subsequent request, making persistence across calls impossible.
- To address this, all shared data in this implementation is managed through a dedicated class called CampusDataStore, which exists independently of the resource lifecycle. CampusDataStore uses ConcurrentHashMap to store rooms, sensors and readings. ConcurrentHashMap was selected over a standard HashMap because it provides built-in thread safety. When multiple HTTP requests are processed concurrently, a regular HashMap is susceptible to race conditions where simultaneous write operations can corrupt the data structure. ConcurrentHashMap handles concurrent access internally, ensuring that data remains consistent and no updates are lost under concurrent load.

---

#### Question 2
Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

#### Answer
- Hypermedia As The Engine Of Application State, or HATEOAS, is an architectural constraint of REST style API design that uses API responses to give links that help navigate related resources and actions. Instead of expecting the client to build URLs independently or refer to external documentation, the API response gives information about how to navigate to the related resource.
- It is a mark of good REST design since such API becomes self-documenting and dynamically discoverable. All that a client needs to know is how to traverse through links to explore all the endpoints available in the API, regardless of any URL structure.
- It is much more flexible and effective than the use of static documentation. Since changes in URL structure on the server's side lead to changes in the documentation, client applications have to be updated accordingly. By having links updated with the current version of API URLs, clients can dynamically discover API changes without modifying code.
- In this implementation, the Discovery endpoint available via GET /api/v1/ returns navigable links to /api/v1/rooms and /api/v1/sensors, enabling any client to discover the full API from the root.

---

### Part 2: Room Management

#### Question 3
When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

#### Answer
- Returning only room IDs produces a lightweight initial response with a minimal payload. However, this approach requires the client to issue a separate GET request for each individual room in order to retrieve any meaningful data such as name, capacity or sensor assignments. In a system with a large number of rooms, this results in a significant number of additional HTTP round trips, increasing both network latency and server load.
- Returning full room objects in a single response eliminates the need for subsequent requests. The client receives all required information in one call, which is more efficient in terms of network utilisation and reduces client-side complexity. The trade-off is a larger initial response payload, which consumes more bandwidth. However, for most practical use cases this cost is outweighed by the reduction in round trips.
- In this implementation, GET /api/v1/rooms returns complete room objects containing the id, name, capacity and sensorIds fields. This design is appropriate for a campus management context where facilities managers typically require full room details when viewing a list, making the single-request full-object approach the more practical choice.

---

#### Question 4
Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

#### Answer
- The DELETE operation is idempotent in this implementation. Idempotency is defined as the property whereby making the same request multiple times produces the same server-side state as making it once, regardless of how many times the operation is repeated.
- The first time DELETE /api/v1/rooms/{roomId} is called on an existing room, the room is found and removed from the data store and the server returns 200 OK. When the same request is sent again, the room no longer exists and the server responds with 404 Not Found. Although the HTTP response codes differ between the first and subsequent calls, the server state remains the same in both cases — the room does not exist. Idempotency concerns the effect on the resource state, not the response code returned to the client.
- Postman testing was used to verify this behaviour. The initial DELETE request for LAB-101 returned 200 and removed the room successfully. The second identical DELETE request returned 404, confirming the room was absent and the server state was unchanged. This is consistent with REST principles which classify DELETE, GET and PUT as idempotent operations.

---

### Part 3: Sensor Operations & Linking

#### Question 5
We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

#### Answer
- The @Consumes(MediaType.APPLICATION_JSON) annotation declared on the POST method specifies that the endpoint exclusively accepts requests carrying a Content-Type header of application/json. This annotation establishes a media type contract between the client and the server at the framework level.
- When a client sends a request with a Content-Type of text/plain or application/xml, the JAX-RS runtime intercepts the request before it reaches the resource method. The framework determines that no registered method can consume the supplied media type and automatically returns an HTTP 415 Unsupported Media Type response. The resource method is never invoked and no application-level code is executed.
- This behaviour is a valuable protection. Without this constraint, a non-JSON body would be passed into the JSON deserialisation layer, potentially causing parsing exceptions, unexpected runtime errors or corrupted data being written to the in-memory store. The @Consumes annotation enforces data format consistency at the framework level, ensuring that only well-formed JSON payloads reach the application logic.

---

#### Question 6
You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path. Why is the query parameter approach generally considered superior for filtering and searching collections?

#### Answer
- Path-based filtering embeds the filter value directly within the URL path, producing URLs such as /api/v1/sensors/type/CO2. This structure implies that type/CO2 is a distinct resource on the server, which is semantically incorrect. A sensor type is a search criterion applied to the sensors collection, not an independent resource. This approach also leads to URL proliferation as additional filter dimensions are introduced.
- The @QueryParam approach appends the filter as an optional modifier to the base resource URL, producing /api/v1/sensors?type=CO2. This correctly represents the operation — the sensors collection is being queried with an optional constraint. The base resource path remains stable and clean regardless of the filter applied.
- The query parameter is entirely optional, allowing the same endpoint to serve both unfiltered and filtered requests. Multiple filters can be combined by chaining query parameters such as ?type=CO2&status=ACTIVE without altering the URL structure. In this implementation, case insensitive matching was also applied to the type parameter, meaning ?type=temperature and ?type=Temperature return identical results, improving usability for client developers.

---

### Part 4: Deep Nesting with Sub-Resources

#### Question 7
Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path in one massive controller class?

#### Answer
- The Sub-Resource Locator pattern allows a parent resource class to delegate the handling of nested resource paths to a dedicated child resource class. In this implementation, SensorResource manages all core sensor operations. When a request arrives for /sensors/{sensorId}/readings, a locator method within SensorResource instantiates and returns SensorReadingResource, which then handles all reading-related operations independently.
- The primary architectural benefit is separation of concerns. Each resource class carries a single clearly defined responsibility. SensorResource is responsible for sensor lifecycle management and SensorReadingResource is responsible for reading operations. Neither class becomes overloaded with unrelated logic, keeping each class concise, readable and independently testable.
- In large APIs where many nested resource paths exist, placing all endpoint logic within a single controller class produces an unmanageable and tightly coupled codebase. The Sub-Resource Locator pattern distributes this responsibility across multiple focused classes, each of which remains small and cohesive as the API grows.
- An additional benefit is the centralisation of shared validation logic. The locator method validates the existence of the parent sensor before delegating to SensorReadingResource. This check is written once and applies consistently to all reading operations, eliminating code duplication and ensuring any change to the validation logic propagates automatically across all related endpoints.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

#### Question 8
Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

#### Answer
- HTTP 404 Not Found conventionally indicates that the resource identified by the request URL does not exist on the server. When a client sends a POST request to /api/v1/sensors, the endpoint exists and is reachable. The URL is valid, the HTTP method is correct and the JSON payload is syntactically well-formed. Returning 404 would incorrectly suggest that the /sensors endpoint itself cannot be found, which is misleading and technically inaccurate.
- HTTP 422 Unprocessable Entity is more semantically precise because it communicates that the request was received and understood by the server, but cannot be processed due to a logical inconsistency within the payload. The roomId field references a room that does not exist in the system. The issue is not the absence of the endpoint but the presence of an invalid reference within an otherwise valid request.
- This distinction provides clearer diagnostic information to client developers. A 422 response directs attention to the content of the request body rather than the URL, allowing the developer to identify and correct the invalid reference more efficiently.
- In this implementation, LinkedResourceNotFoundException is thrown when the specified roomId cannot be found, and the corresponding exception mapper returns a 422 response with a descriptive JSON error body identifying the missing resource.

---

#### Question 9
From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

#### Answer
- Exposing raw Java stack traces to external API consumers presents several significant security risks that fall under the principle of unintended information disclosure.
- A stack trace reveals the internal structure of the application including fully qualified package names, class names, method names and line numbers. This information allows an attacker to map the application architecture and identify specific components, frameworks and libraries in use. If any of these carry known vulnerabilities, the attacker can research and apply targeted exploits without extensive reconnaissance.
- Stack traces frequently expose version-specific details about the runtime environment including Java version, application server and third-party dependency versions. This enables an attacker to cross-reference disclosed versions against published CVE databases to identify applicable attack vectors.
- Stack traces may inadvertently reveal internal implementation details such as database query fragments, file system paths or object states at the time of failure. A NullPointerException trace reveals that a specific code path fails under particular conditions, which an attacker can deliberately trigger to probe application behaviour or cause denial of service.
- In this implementation, GlobalExceptionMapper implements ExceptionMapper<Throwable> and intercepts any unhandled exception before a response is sent to the client, returning a controlled HTTP 500 Internal Server Error with a generic JSON error body. No internal application details are exposed, ensuring the API adheres to the principle of minimal information disclosure.

---

#### Question 10
Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

#### Answer
- A JAX-RS filter decorated with @Provider is automatically applied to every incoming request and outgoing response across the entire API without requiring any modification to existing resource classes. In this implementation, LoggingFilter.java implements both ContainerRequestFilter and ContainerResponseFilter and applies universally to RoomResource, SensorResource and SensorReadingResource without touching those classes directly.
- Manually inserting Logger.info() statements inside every resource method introduces code duplication, increases maintenance overhead and violates the principle of separation of concerns. If the logging format needs to change, every method across every resource class would need to be updated individually. With a filter, the change is made in one place and propagates automatically to the entire API.
- Filters execute at the framework level before and after the resource method, ensuring every request and response is captured consistently — including requests that fail validation or are rejected before reaching the method body. Manual logging inside resource methods would miss these cases entirely.

---