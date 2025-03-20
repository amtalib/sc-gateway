# SC-Gateway

### Basic functionalities:
 1. Routing of requests via YML configuration. `(See config: spring.cloud.gateway.routes.id and spring.cloud.gateway.routes.uri)`
 </br></br>
 2. Round-robin load balancing with health-check to prevent picking a bad one. `(See config: spring.cloud.loadbalancer.health-check)`
 </br></br>
 3. Automatic service discovery. `(See config: spring.cloud.gateway.discovery)` - Dependent on SC-Eureka
 </br></br>
 4. Automatic fail-over `(See config: spring.cloud.gateway.routes.filters.name: Retry)`
 </br>  
 5. Circuit-breaker `(See config: spring.cloud.gateway.routes.filters.name: CircuitBreaker)`
 </br></br>
 6. URI prefix (e.g., /api) - supports adding of prefix to endpoints `(See config: spring.cloud.gateway.routes.filters.RewritePath)`
 </br></br>
 7. Maximum header size validation `(See config: spring.cloud.gateway.routes.filters.RequestHeaderSize)`, if breached, and error HTTP 431 is returned.
 </br></br>
 8. Maximum request size validation `(See config: spring.cloud.gateway.routes.filters.RequestSize)`, if breached, and error HTTP 431 is returned.
  
### Custom functionalities:  
 1. Validates API-Token `(See HeaderValidationFilter.class)` - Dependent on API Token
 2. Generates unique TranRefNo and add `X-TranRef-No` to headers. `(See LoggingFilter.class)`
 3. PENDING: Logs transactions - integrated to logger api and pub/sub. `(See LoggingFilter.class)`  
 4. Rate limiter - limits specific user to `X` ` (See redis-rate-limiter.replenishRate)`  number of requests per second using `JWT.iss`. `X-Forwarded-For` shall be used if `JWT.iss` is not present.

### Dependencies:
 1. Spring sc-eureka
 2. Config-server
 3. Redis for the pub/sub (Required if logging to DB is activated)
 4. Token API
 5. Logger API (required if activated)
 
### Technologies:
 1. Java 17+
 2. Spring-boot 3.3.0
 3. Spring cloud 2023.0.2
 
### Environment variables:
 * ACTIVE_PROFILE
 * CONFIG_SERVER
 * CONFIG_SERVER_USERNAME
 * CONFIG_SERVER_PASSWORD  
	
### Run procedure:
 * Step 1. Run `config-server` to serve `sc-eureka-dev.yml` and `sc-gateway-dev.yml`
 * Step 2. Launch `redis`
 * Step 3. Set the environment variables.
 * Step 4. Launch `sc-gateway`
 * Step 5. Launch `microservices`

### Build procedure:
 * Step 1. Export `sc-gateway` into a jar file (Use STS or maven build).
 * Step 2. Build docker image
 * Step 3. Push to docker image registry (Harbor)  

### Client discovery requirement
Note: See sample-service-application.yml for reference.
 1. Define `server.servlet.context-path`. Should be the same with the application name and must be in lowercase.
<pre>
server:
  servlet:
     # Should be the same with the application name.
     context-path: '/service-a'
</pre>
 2. Define `eureka.client` and `eureka.instance` in the application. 
 <pre>
eureka:
  client:
     registerWithEureka: true
     fetchRegistry: true
     serviceUrl:
       defaultZone: http://localhost:7001/eureka/
     healthcheck:
       enabled: true 			
     # Fetches new instance faster, default is 30sec
     registry-fetch-interval-seconds: 5
  instance:
     preferIpAddress: true
     #ipAddress: ${INSTANCE_HOSTNAME}
     ipAddress: localhost
     # Reduces time for detection, default is 30sec
     lease-renewal-interval-in-seconds: 10
     # Default is 30sec
     lease-expiration-duration-in-seconds: 5
</pre>
 3. Define `management.info`, `management.endpoints`, and `management.endpoint` to configure actuator. sc-gateway needs the full `/health` data for automatic failover.
<pre>
management:
  info:
     env:     
       enabled: true
     git:
       mode: full						
  # Added to enable actuator endpoints
  endpoints:
     web:
       exposure:
         # To view all available URLs in the actuator, invoke http://localhost:7101/<application context-path>a/actuator
         include: "*"
         exclude: threaddump
  endpoint:
     health:
       show-details: always								
     metrics:
       access: read-only
</pre> 
      
 

