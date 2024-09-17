### Movies Recommendation Api 
In a folder named PythonAPI , I have created our flask API, which takes movie as a input and recommends similar movies

### Spring Boot
Through springboot controller we are calling that flask api, but to call spring boot controller you have to paste this in browser/postman 
http://localhost:8080/movies/recommend?movie=Avatar

### Running Application in AWS EC2
1. Create a folder, and initialise it as a git repository
2. Run this command "git remote add origin https://github.com/shrey-007/MovieRecommendation.git"
3. Run this command "git pull origin master"
4. Run this command "chmod +x setup.sh"
5. Run this command "./setup.sh", it will download all dependencies

---

# Basic Idea of Tools Used-:
# Logging

### 1. **SLF4J (Simple Logging Facade for Java)**

- **What is SLF4J?**
    - **SLF4J** is a simple facade or abstraction for various logging frameworks (like Logback, Log4j, etc.).
    - It provides a generic API (set of interfaces) for logging in Java applications without directly binding to any specific logging framework. This allows developers to switch between different logging frameworks easily without changing the application code.

- **How does SLF4J work?**
    - SLF4J is just an abstraction layer. When you use SLF4J in your code, it doesn't log anything by itself. Instead, it delegates the actual logging work to the underlying logging framework (like Logback or Log4j) at runtime.
    - You need to include both SLF4J and a specific logging framework implementation in your project dependencies for logging to function correctly.

- **Why use SLF4J?**
    - It offers a consistent API across different logging frameworks, providing flexibility to change the logging implementation without modifying the application code.
    - It reduces the dependency on a specific logging framework, enhancing modularity and maintainability.

### 2. **Logback**

- **What is Logback?**
    - **Logback** is a powerful, flexible, and fast logging framework for Java applications.
    - It is the default logging framework used by **Spring Boot**. Logback was created by the same developer who created Log4j, Ceki Gülcü, and is intended as a successor to Log4j.

- **Features of Logback:**
    - **Performance**: Logback is designed for high performance, making it faster and more efficient than Log4j.
    - **Advanced Configuration**: Logback supports advanced and flexible configurations via XML and Groovy scripts.
    - **Rolling Policy**: Logback provides rolling policies to manage log files, like time-based or size-based rolling.
    - **Filter Mechanisms**: Logback allows filtering of log messages at different levels or by various criteria.

- **Why use Logback?**
    - It is the default logging framework in Spring Boot and is considered more modern and efficient compared to Log4j.
    - Logback is very customizable and provides excellent performance and flexibility.

### 3. **Log4j and Log4j2**

- **What is Log4j?**
    - **Log4j** is one of the oldest and most widely used logging frameworks for Java. It provides a rich set of features for logging, such as multiple output destinations (console, file, etc.), logging levels, and filtering capabilities.
    - **Log4j2** is the newer, more advanced version of Log4j, with significant improvements in performance, scalability, and flexibility.

- **Features of Log4j2:**
    - **Improved Performance**: Log4j2 is faster and has lower latency than Log4j 1.x, thanks to its asynchronous logging capabilities.
    - **Plugin Architecture**: Log4j2 is built on a plugin-based architecture, making it more extensible and configurable.
    - **Advanced Filtering**: Log4j2 provides more sophisticated filtering options compared to Log4j 1.x.
    - **Support for JSON and XML**: It offers more flexibility in formatting logs in various formats like JSON and XML.

- **Why use Log4j2?**
    - If you need advanced logging features, better performance, and more flexible configuration options, Log4j2 is a good choice.
    - It is also a good choice for existing applications that are already using Log4j, as it is backward compatible with Log4j 1.x configurations.

### **How They Work Together:**

- **SLF4J** acts as a facade or bridge between your application code and the actual logging framework (like Logback or Log4j2). When you use SLF4J in your code, it doesn’t directly perform logging. Instead, it calls the underlying logging framework, which performs the logging.

- You can use **SLF4J** with either **Logback** (default in Spring Boot) or **Log4j2**. The choice depends on your application's needs and preferences.

### **Which Should You Use?**

- **Logback** is a good choice for most Spring Boot applications due to its default inclusion, ease of use, and good performance.
- **Log4j2** can be a better choice if you require its advanced features, such as asynchronous logging or custom plugins.
- **SLF4J** should always be used as the logging API in your application to provide flexibility in choosing the underlying logging framework.

Would you like to know how to set up logging with any specific framework, or do you have more questions about these logging tools?

---

# Testing
For testing in your Spring Boot application, you should use both **JUnit** and **Mockito** together, as they serve different but complementary purposes:

### 1. **JUnit**
- **Purpose**: JUnit is a testing framework that provides the foundation for writing and running tests in Java. It allows you to define test cases, assert conditions, and organize test suites.
- **Use Cases**:
  - Writing unit tests for individual methods and classes.
  - Running tests with annotations like `@Test`, `@BeforeEach`, `@AfterEach`, etc.
  - Checking the correctness of your code by asserting expected outcomes.

### 2. **Mockito**
- **Purpose**: Mockito is a mocking framework that complements JUnit by allowing you to create mock objects. These mocks simulate the behavior of real objects, enabling you to test components in isolation.
- **Use Cases**:
  - Mocking dependencies of the class under test (e.g., services, repositories) to avoid testing real implementations.
  - Verifying interactions between components, such as ensuring a specific method was called with the expected arguments.
  - Simulating different scenarios, such as exceptions or specific return values from dependencies.

### Recommended Approach

- **Use JUnit** as the primary testing framework to define and run your tests.
- **Use Mockito** to mock dependencies and isolate the component under test.

### Conclusion

- **Use JUnit** for creating and running your test cases.
- **Use Mockito** for mocking dependencies and verifying interactions.

Combining these two frameworks allows you to write comprehensive and effective tests for your Spring Boot application.

---

# Spring Security
1. Before any API is hit, filters are called for preprocessing. If the request is valid then it forwarded to the controller else it is discarded. We can also use Filers for postprocessing
2. Fir request jaaegi AuthenticationManager ko , joki find krega request ke according ki konse Authentication Provider ko request forward kre. There are many Authenticators like DaoAuthenticationProvider.
3. Ab jis bhi AuthenticationProvider ko request gyi uske paas login form mai di gyi information hai, but jo user ka asli password hai voh nhi pata use, toh vo pata krega through UserDetailService(loadUserByUsername()). This UserDetailService will get the real password of user from database using UserRepository
4. Ab UserRepository database user ki info degi UserDetailService ko , UserDetailService user ki information degi AuthenticationProvider ko. Now AuthenticationProvider ke paas login form ki details and database ki details dono hai, toh ab voh match karega. If it matches successfully toh SecurityContext mai authentication set ho jaaega

Below is detailed explanation-:  

Spring Security provides authentication, authorization, and other security-related features to Spring-based applications. Here's a breakdown of its internal workings:

### 1. **Filter Chain**
- **Spring Security Filter Chain** is the backbone of Spring Security. It intercepts HTTP requests and applies security logic.
- The chain contains a set of **security filters** that are applied in a specific order to process authentication, authorization, and other security-related actions.
- The `DelegatingFilterProxy` in `web.xml` or Spring Boot’s `@EnableWebSecurity` annotation delegates requests to the filter chain.
- Filters like `UsernamePasswordAuthenticationFilter`, `BasicAuthenticationFilter`, and `OAuth2LoginAuthenticationFilter` handle different types of authentication.

### 2. **Authentication**
- **Authentication** is the process of verifying the identity of a user or system.
- Spring Security uses the **`AuthenticationManager`** to handle authentication.
- The `AuthenticationManager` delegates the authentication to one or more **`AuthenticationProvider`** instances. These providers are responsible for verifying credentials (e.g., username/password).
- A typical provider is `DaoAuthenticationProvider`, which retrieves user details from a `UserDetailsService`.

#### Workflow:
- A filter (e.g., `UsernamePasswordAuthenticationFilter`) intercepts a login request.
- It creates an **`Authentication`** object (e.g., `UsernamePasswordAuthenticationToken`) containing user credentials.
- The `AuthenticationManager` authenticates this token by passing it to the `AuthenticationProvider`.
- On success, it stores the authentication result in the **`SecurityContext`**.

### 3. **SecurityContext and SecurityContextHolder**
- Once authentication is successful, the **`SecurityContext`** holds the `Authentication` object for the session.
- The `SecurityContext` is stored in the **`SecurityContextHolder`**, which is a thread-local storage mechanism.
- This allows Spring Security to access the user’s authentication status throughout the request.

### 4. **Authorization**
- After authentication, **authorization** determines if a user has the right permissions to access certain resources.
- The **`AccessDecisionManager`** takes care of this by checking if the user’s roles match the required roles for a resource.
- It works alongside the **`GrantedAuthority`** objects stored in the `Authentication` object.
- The `@PreAuthorize`, `@Secured`, or access control annotations can be used to apply fine-grained authorization checks at the method level.

### 5. **UserDetailsService**
- The **`UserDetailsService`** is responsible for loading user-specific data (e.g., username, password, roles).
- It returns a **`UserDetails`** object containing the user’s credentials and granted authorities.
- This service is used by `DaoAuthenticationProvider` for verifying credentials.

### 6. **Password Encoding**
- Spring Security uses **`PasswordEncoder`** (e.g., `BCryptPasswordEncoder`) to hash and verify passwords.
- When a user logs in, the raw password is encoded and compared to the stored encoded password for verification.

### 7. **CSRF Protection**
- Spring Security includes built-in **CSRF (Cross-Site Request Forgery)** protection.
- It requires a token to be included in non-GET HTTP requests (e.g., POST, PUT, DELETE) to ensure the request is legitimate.

### 8. **Session Management**
- Spring Security can manage user sessions, including tracking if a user is logged in and how many sessions are active.
- It can also handle session fixation attacks by changing the session ID after successful login.

### 9. **OAuth2 / JWT Authentication**
- Spring Security integrates with **OAuth2** providers, handling OAuth2 login flow and token-based authentication (e.g., JWT).
- OAuth2 login involves filters like `OAuth2LoginAuthenticationFilter` and uses OAuth-specific tokens in the `SecurityContext`.

### Summary
The core components of Spring Security (filter chain, authentication, authorization, and session management) work together to provide robust security to a Spring application. The flexibility of using custom filters, providers, and user services allows for extensive customization.

---

# Working of Password Encoder
`BCryptPasswordEncoder` is a password hashing function commonly used in Spring Boot for securely storing passwords. It uses the BCrypt algorithm, which is designed to be slow, making it resistant to brute force attacks. Here's how it works:

1. **Salting**: Every password is hashed with a unique salt. This means even if two users have the same password, their hashed passwords will be different. This makes rainbow table attacks ineffective.

2. **Hashing**: The password is combined with the salt, and the BCrypt algorithm hashes the result. The BCrypt algorithm is intentionally slow by design, which helps mitigate brute force attacks.

3. **Encoding Strength**: BCrypt allows for configurable "work factor" (also known as the cost factor), which increases the complexity and time it takes to hash the password as computing power increases over time.

4. **Hash Verification**: When a user logs in, the password provided is hashed using the same salt stored with the original hash, and the resulting hash is compared to the stored one. If they match, the password is correct.

Let’s walk through an example of how hashing works with a password, hash function, and salt.

### Assumptions:
- **Password**: `mypassword123`
- **Salt**: `randomSalt123`
- **Hash Function**: Simplified hash function (e.g., SHA-256 for demonstration, although BCrypt would use a more complex process)

Here’s the step-by-step process:

### Step 1: Original Password
The user enters a password:

```
Password: mypassword123
```

### Step 2: Generate Salt
A random salt is generated. In this case, let’s assume the salt is:

```
Salt: randomSalt123
```

### Step 3: Concatenate Password and Salt
Now, we concatenate the password and the salt to form a new string:

```
Password + Salt: mypassword123randomSalt123
```

### Step 4: Apply the Hash Function
Next, we apply a hash function (e.g., SHA-256) to the concatenated password and salt.

Let’s assume the output of `SHA-256` for `mypassword123randomSalt123` is:

```
Hash: c038eb7bb2227e2d9e05a74a22fbbaf3b0c0b3a4fa43dce8f676f5dbf7c0a048
```

### Step 5: Store the Salt and Hashed Password
Now, we store both the **salt** and the **hashed password** in the database. Typically, the salt is stored along with the hash so that it can be used for verification later.

```
Stored Hash: c038eb7bb2227e2d9e05a74a22fbbaf3b0c0b3a4fa43dce8f676f5dbf7c0a048
Stored Salt: randomSalt123
```

### Step 6: Verifying the Password
When the user logs in again, the following process happens:
1. The entered password (e.g., `mypassword123`) is concatenated with the stored salt (`randomSalt123`).
2. The same hash function is applied to this combination.
3. The resulting hash is compared to the stored hash.

If the hash matches, the password is correct.

### Example in Code (SHA-256 for Simplicity):

Here’s a simplified Java example using `SHA-256` (BCrypt would involve more steps, but this illustrates the concept).

```

### Output:
```
Password: mypassword123
Salt: randomSalt123
Hashed Password: 9U5OYz9rb1fzZcSR+xA+2bC6RxaL/zMXfVeqyE6KMLk=
```

In this case, the **salted and hashed password** is stored securely. When a user tries to log in, the same salt is applied, and the hash is recalculated and compared to the stored hash for verification.

This demonstrates the process of **password salting and hashing** to protect passwords from common attacks. In real applications, using a slower and more secure algorithm like BCrypt is recommended.