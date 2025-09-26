> The "Scatter By Auction" method is a decentralized approach to finding the best flights, instead of a central server dictating the optimal flight options, the service broadcasts the search query to all available search microservices (non-stop, stops, partners) simultaneously. Each microservice then independently responds with its best flight options, typically based on a bidding or ranking process. The main service then aggregates these responses and present them to the user

### Problem 
> The task is to implement a decentralized system using the "Scatter By Auction" method for finding the best flights. The main service broadcasts a search query to multiple microservices that respond with their best flight options independently. These responses are then aggregated and presented to the user.

#### Assumptions:
1. There are multiple microservices providing flight options.
2. Each microservice responds asynchronously.
3. The main service must handle these asynchronous responses efficiently.
4. The system needs to be designed for scalability and performance.
5. Edge cases such as network latency, missing or delayed responses need to be handled.

#### Brute Force:
A simple approach could involve broadcasting the search query to each microservice sequentially and waiting for their responses before aggregating them. However, this method is inefficient due to the sequential nature of the calls.

#### Optimize
we can use a parallel processing approach to broadcast the query to all microservices simultaneously and handle their responses asynchronously. This will significantly reduce the overall response time.

- **Tradeoffs**:
  - **Latency**: By broadcasting in parallel, the initial latency is reduced.
  - **Complexity**: Handling asynchronous responses adds complexity to the system.
  
- **Big-O**:
  - Broadcasting to \( n \) microservices: \( O(n) \)
  - Aggregating responses: \( O(k \cdot m) \), where \( k \) is the number of microservices and \( m \) is the average number of flights per service response.
  
- **Optimization Strategy**:
  - Use a thread pool to handle asynchronous responses.
  - Implement a timeout mechanism for handling delays.

#### Edge Cases
1. Network latency - Ensure that the main service can still process responses even if some microservices are slow or unresponsive
2. Missing or delayed responses - Handle cases where certain microservices don not respond or their responses are delayed.
3. System failure - Have fallback mechanisms in place for critical microservices

#### APIs/Classes 
- **FlightSearchRequest**: Represents the search query sent to the microservices
- **MicroserviceClient**: Handles communication with individual microservices
- **AsyncServiceHandler**: Manages asynchronous response handling

#### Architecure/Data Model:
- **Microservices Layer**: Responsible for handling search queries and returning flight options.
- **Main Service Layer**: Boardcasts the search query to multiple microservices, handles responses asynchronously and aggregates them before presenting to the user.
