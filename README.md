# Smart Campus Sensor & Room Management API

## API Design Overview

The Smart Campus API is built using Java and JAX-RS, following advanced RESTful architectural principles to ensure scalability, reliability, and ease of use for client developers.

Resource-Oriented Architecture: The API is structured around core campus entities (Rooms and Sensors), utilizing standard HTTP methods (GET, POST, PUT, DELETE) for predictable CRUD operations.

HATEOAS Compliance (Richardson Maturity Model Level 3): Responses include hypermedia links (_links), allowing client applications to dynamically discover related resources and available actions without hardcoding URLs.

Sub-Resource Locators: Complex nested routes (e.g., accessing a specific sensor's readings within a specific room) are handled via delegated sub-resources, keeping controller classes focused and maintaining a clean separation of concerns.

Robust Exception Handling: A Global Exception Mapper intercepts unhandled errors, preventing internal stack traces from leaking to the client. Custom exceptions (e.g., 422 Unprocessable Entity for linked resource errors, 409 Conflict for deleting non-empty rooms) provide semantically accurate HTTP status codes and actionable JSON error messages.

Thread-Safe Data Management: Data is managed in-memory using ConcurrentHashMap, ensuring thread safety and preventing race conditions during concurrent API requests.

Centralized Observability: JAX-RS container filters are utilized for global request and response logging, separating cross-cutting concerns from core business logic.

## Technology Stack

- Java 21
- Maven
- JAX-RS with Jersey
- Apache Tomcat 9
- In-memory data structures only

##Main Endpoints

-GET /api/v1/rooms
-POST /api/v1/rooms
-DELETE /api/v1/rooms/{roomId}
-GET /api/v1/rooms/{roomId}/sensors
-POST /api/v1/rooms/{roomId}/sensors
-GET /api/v1/sensors/{sensorId}/readings
-POST /api/v1/sensors/{sensorId}/readings
 
##Build and Launch Instructions

This project is managed via Maven. Follow these steps to build the application and launch the server locally.

Prerequisites
Java Development Kit (JDK) 11 or higher installed.

Apache Maven installed (or use your IDE's built-in Maven wrapper).

A Java-compatible web server (e.g., Apache Tomcat or GlassFish).

Step-by-Step Setup
1.Clone the Repository
  Open your terminal (or PowerShell) and run:
  `git clone https://github.com/Senura730/smart-campus-api.git`
  `cd smart-campus-api`

2.Clean and Build the Project
  Compile the code and package it into a deployable .war file by running:
  `mvn clean package`
  Note: This will generate a .war file inside the newly created target/ directory.

3.Deploy and Launch the Server
  -Via NetBeans (Recommended): Open the project in NetBeans. Right-click the project root in the Projects window and select Run. NetBeans will automatically          deploy the application to your configured server (GlassFish/Tomcat) and open your browser.

  -Via Tomcat (Manual): Copy the generated .war file from the target/ directory into the webapps/ directory of your Apache Tomcat installation. Start Tomcat by       running bin/startup.bat (Windows) or bin/startup.sh (Mac/Linux).

4.Verify the Deployment
  Once the server is running, verify the API is live by navigating to the base URL in your browser or terminal:
  `http://localhost:8080/csa-coursework-api/api/v1/rooms`
  (Note: Adjust the port number 8080 if your local server is configured differently).


##Sample API Interactions (cURL Commands)

Once the server is running, you can use the following curl commands in your terminal to test the API's functionality.

1. Retrieve a list of all rooms
`curl -X GET http://localhost:8080/csa-coursework-api/api/v1/rooms \
     -H "Accept: application/json"`

2. Register a new room
 ` curl -X POST http://localhost:8080/csa-coursework-api/api/v1/rooms \
     -H "Content-Type: application/json" \
     -d "{\"id\": \"101\", \"name\": \"Computer Lab A\", \"capacity\": 40}" `

3. Add a new sensor to a specific room
   `curl -X POST http://localhost:8080/csa-coursework-api/api/v1/rooms/101/sensors \
     -H "Content-Type: application/json" \
     -d "{\"sensorId\": \"S-99\", \"type\": \"Temperature\", \"status\": \"ACTIVE\"}" `

4. Retrieve all sensors currently active in a specific room
   `curl -X GET "http://localhost:8080/csa-coursework-api/api/v1/rooms/101/sensors?status=ACTIVE" \
     -H "Accept: application/json" `

5. Delete a room safely (Idempotent operation)
   `curl -X DELETE http://localhost:8080/csa-coursework-api/api/v1/rooms/101 `

## Design Notes

- Resources are exposed under `/api/v1` using `@ApplicationPath`.
- `web.xml` maps Jersey's `ServletContainer` for classic servlet deployment.
- Shared campus data is stored in a singleton service so it remains available across requests.
- Mutating operations are synchronized where multiple structures must be updated together.
- The sub-resource locator pattern is used for `sensors/{sensorId}/readings`.
- Exception mappers return JSON error responses for business rule violations and unexpected failures.
- Request and response logging is handled through JAX-RS filters.



## Report
### ***Part 1: Service Architecture \& Setup*** 

#### **1.Project \& Application Configuration**

The JAX-RS runtime treats resource classes (RoomResource) as perrequest, not as singletons by default. Each HTTP request received creates an entirely new instance of the class and immediately destroys it after a response is dispatched.

Since instances are created and destroyed every request, normal instance variables (ArrayList) will be reset. The data stored in the mock database had to be extracted into a centralized CampusService since static variables preserve information across requests, and we needed to persist the information between multiple consecutive API calls. Also, due to this per-request life cycle the server handles simultaneous calls by allocating concurrent threads.

&#x20;So imagine two users doing a POST at the same time, both will try to write at that shared service exactly in that millisecond. So in order to avoid race condition, ConcurrentModificationExceptions or data loss, the CampusService stores the state using ConcurrentHashMap which allows for thread-safe, atomic updates that do not block when searching and inserting elements.



#### **2.The ”Discovery” Endpoint**

The Hypermedia architecture style is seen as the epitome of RESTful architecture design as it can help an API reach level three of the Richardson Maturity Model, which is the highest level that any API can reach. Unlike a basic REST API that only responds back with plain data, a HATEOAS compliant API will not only provide you with data but will also include navigational links, such as resource links.

Benefits Over Static Documentation:

•Eliminates Hardcoded URLs

•Always Up-to-Date because it act as an active endpoint 

•Links can change based on the resource’s current condition



####***Part 2: Room Management***

#### 1.Room Resource Implementation

The choice between returning just IDs or full objects comes down to a tradeoff between payload size and the number of network requests.

•Returning Only IDs

&#x09; Network Bandwidth: Highly efficient. The JSON payload extremely small.

&#x09; Client Processing: Poor efficiency. It gives rise to the "N + 1 problem." The client application would have to issue an initial 	request for obtaining the list of IDs, followed by individual API calls for each and every ID for fetching the names and data of 	the respective rooms. It results in excessive load times and consumes the power of the client application..

•Returning Full Objects

&#x09;Network Bandwidth: Higher usage. The JSON payload is much larger.

&#x09;Client Processing: Highly efficient. The client receives everything it needs in a single request.

#### 2.Room Deletion \& Safety Logic

Yes, the DELETE operation in this implementation is strictly idempotent. Here is what happens if a client mistakenly sends the exact same DELETE request multiple times:

•First Request: The API will find the ConcurrentHashMap and remove it will return 200 ok success massage and the server’s state is updated.

•Duplicate Requests: When the second request arrives, the API will look for the room and will not find it. Instead of crashing or modifying any data, the system throws a ResourceNotFoundException and returns a 404 Not Found error safely.

##### 

### Part 3: Sensor Operations \& Linking

#### 1.Sensor Resource \& Integrity

When @Consumes (MediaType.APPLICATION\_JSON) is applied to a POST method it creates a strict gatekeeper. It tells the API to only accept incoming data if it is formatted as JSON. If a client sends data in a different format (xml,txt) JAX-RS will handle it like this:

•Automatic Interception: JAX-RS will read the incoming HTTP Content and because the header doesn’t match the JSON requirement, the framework will block the request before it touches the Java code.

•The 415 Error: Without processing that data, JAX-RS will automatically generate and return a 415 Unsupported Media Type HTTP status code to the client.

•Server Protection: By bypassing the Java method, JAX-RS protects the server. It prevents your code from crashing and wasting CPU power trying to parse a language that it was not built to read.

#### 2.Filtered Retrieval \& Search

In RESTful API path parameters are used for identifying a specific resource and query parameters are used for sorting ,filtering and modifying a collection.

•Query Parameter is better for filtering because:

&#x09;Naturally optional, can call the same method using /sensors to get all or can add ?type=CO2 to filter.

&#x09;Query parameters stack cleanly.



### Part 4: Deep Nesting with Sub- Resources

#### 1.The Sub-Resource Locator Pattern

Creating Sub Resource Locator pattern avoids the massive file where hundreds of nested endpoints are packed into a single controller. By assigning sub paths to a separate class gain major architectural benefits,

•Cleaner Routing: The parent handles /sensors/{id}, and the child simply handles /readings.

•Easier to handle: This keeps the code easier to read, test and maintain.



### Part 5: Advanced Error Handling, Exception Mapping \& Logging

#### 1.Dependency Validation (422 Unprocessable Entity)

404 Not Found error says that the URL is wrong and 422 Unprocessable Entity says the URL is correct but the data inside is logical error. If the client tries to register a sensor to a Room ID that does not exist, the "Sensor" resource is valid, but the "Room" reference is not. 422 clearly says that the relationship between room and the sensor is the problem not the API path itself. Also 422 providers better debugging.  

#####2.The Global Safety Net (500)

Exposing raw java traces to the consumers is a threat because it reveals the internal implementation. An attacker can gather system structure (web server, frameworks in use), vulnerabilities in the system and internal files. To mitigate this risk the project utilize a global exception mapper. This mapper intercepts all unhandled exceptions and returns a generic 500 Internal Server Error JSON payload. This ensures the server’s internal details remain hidden.

#### 3.API Request \& Response Logging Filters

Filters ensure global coverage by automatically intercepting all requests and responses, eliminating the risk of missed logs inherent in manual coding. They maintain separation of concerns by isolating cross-cutting logging logic from core business code, and follow the DRY principle, enabling system-wide updates to log formats from a single file rather than across every individual resource method.



