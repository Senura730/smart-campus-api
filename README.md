##### ***Part 1: Service Architecture \& Setup*** 

###### **1.Project \& Application Configuration**

The JAX-RS runtime treats resource classes (RoomResource) as perrequest, not as singletons by default. Each HTTP request received creates an entirely new instance of the class and immediately destroys it after a response is dispatched.

Since instances are created and destroyed every request, normal instance variables (ArrayList) will be reset. The data stored in the mock database had to be extracted into a centralized CampusService since static variables preserve information across requests, and we needed to persist the information between multiple consecutive API calls. Also, due to this per-request life cycle the server handles simultaneous calls by allocating concurrent threads.

&#x20;So imagine two users doing a POST at the same time, both will try to write at that shared service exactly in that millisecond. So in order to avoid race condition, ConcurrentModificationExceptions or data loss, the CampusService stores the state using ConcurrentHashMap which allows for thread-safe, atomic updates that do not block when searching and inserting elements.



###### **2.The ”Discovery” Endpoint**

The Hypermedia architecture style is seen as the epitome of RESTful architecture design as it can help an API reach level three of the Richardson Maturity Model, which is the highest level that any API can reach. Unlike a basic REST API that only responds back with plain data, a HATEOAS compliant API will not only provide you with data but will also include navigational links, such as resource links.

Benefits Over Static Documentation:

•Eliminates Hardcoded URLs

•Always Up-to-Date because it act as an active endpoint 

•Links can change based on the resource’s current condition



##### ***Part 2: Room Management***

###### 1.Room Resource Implementation

The choice between returning just IDs or full objects comes down to a tradeoff between payload size and the number of network requests.

•Returning Only IDs

&#x09; Network Bandwidth: Highly efficient. The JSON payload extremely small.

&#x09; Client Processing: Poor efficiency. It gives rise to the "N + 1 problem." The client application would have to issue an initial 	request for obtaining the list of IDs, followed by individual API calls for each and every ID for fetching the names and data of 	the respective rooms. It results in excessive load times and consumes the power of the client application..

•Returning Full Objects

&#x09;Network Bandwidth: Higher usage. The JSON payload is much larger.

&#x09;Client Processing: Highly efficient. The client receives everything it needs in a single request.

###### 2.Room Deletion \& Safety Logic

Yes, the DELETE operation in this implementation is strictly idempotent. Here is what happens if a client mistakenly sends the exact same DELETE request multiple times:

•First Request: The API will find the ConcurrentHashMap and remove it will return 200 ok success massage and the server’s state is updated.

•Duplicate Requests: When the second request arrives, the API will look for the room and will not find it. Instead of crashing or modifying any data, the system throws a ResourceNotFoundException and returns a 404 Not Found error safely.

##### 

##### Part 3: Sensor Operations \& Linking

###### 1.Sensor Resource \& Integrity

When @Consumes (MediaType.APPLICATION\_JSON) is applied to a POST method it creates a strict gatekeeper. It tells the API to only accept incoming data if it is formatted as JSON. If a client sends data in a different format (xml,txt) JAX-RS will handle it like this:

•Automatic Interception: JAX-RS will read the incoming HTTP Content and because the header doesn’t match the JSON requirement, the framework will block the request before it touches the Java code.

•The 415 Error: Without processing that data, JAX-RS will automatically generate and return a 415 Unsupported Media Type HTTP status code to the client.

•Server Protection: By bypassing the Java method, JAX-RS protects the server. It prevents your code from crashing and wasting CPU power trying to parse a language that it was not built to read.

###### 2.Filtered Retrieval \& Search

In RESTful API path parameters are used for identifying a specific resource and query parameters are used for sorting ,filtering and modifying a collection.

•Query Parameter is better for filtering because:

&#x09;Naturally optional, can call the same method using /sensors to get all or can add ?type=CO2 to filter.

&#x09;Query parameters stack cleanly.



##### Part 4: Deep Nesting with Sub- Resources

###### 1.The Sub-Resource Locator Pattern

Creating Sub Resource Locator pattern avoids the massive file where hundreds of nested endpoints are packed into a single controller. By assigning sub paths to a separate class gain major architectural benefits,

•Cleaner Routing: The parent handles /sensors/{id}, and the child simply handles /readings.

•Easier to handle: This keeps the code easier to read, test and maintain.



##### Part 5: Advanced Error Handling, Exception Mapping \& Logging

###### 1.Dependency Validation (422 Unprocessable Entity)

404 Not Found error says that the URL is wrong and 422 Unprocessable Entity says the URL is correct but the data inside is logical error. If the client tries to register a sensor to a Room ID that does not exist, the "Sensor" resource is valid, but the "Room" reference is not. 422 clearly says that the relationship between room and the sensor is the problem not the API path itself. Also 422 providers better debugging.  

###### 2.The Global Safety Net (500)

Exposing raw java traces to the consumers is a threat because it reveals the internal implementation. An attacker can gather system structure (web server, frameworks in use), vulnerabilities in the system and internal files. To mitigate this risk the project utilize a global exception mapper. This mapper intercepts all unhandled exceptions and returns a generic 500 Internal Server Error JSON payload. This ensures the server’s internal details remain hidden.

###### 3.API Request \& Response Logging Filters

Filters ensure global coverage by automatically intercepting all requests and responses, eliminating the risk of missed logs inherent in manual coding. They maintain separation of concerns by isolating cross-cutting logging logic from core business code, and follow the DRY principle, enabling system-wide updates to log formats from a single file rather than across every individual resource method.



