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
### Working of OAuth2 Login -:
1. Whenever, a page is hit securityFilterChain() function is called from the class  SecurityConfig, which tells which is
the login page url and what to do after successful login. So it first redirects the user to the login page, as it has information which os login page
2. When the login is done successfully by Google, the again control comes to securityFilterChain() which runs the successHandler method which is present class OAuthAuthenticationSuccessHandler as onAuthenticationSuccess()
3. This function onAuthenticationSuccess(), gets the details of the logged user and finds whether it is login case or signup case, if signup case then it first saves the user in database and then redirects to dashboard and if login case then it redirects to dashboard
4. I have also created a RootController , which runs every single time before any controller. Since onAuthenticationSuccess() redirects user to the dashboard controller so before calling dashboard controller , spring first calls RootController which gets the User object from the database and add it to model object , through which any controller can get the logged user inforamtion
