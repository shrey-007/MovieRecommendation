1. Developed a machine learning model trained on a dataset of 5000+ movies, achieving 95% accuracy in predicting
   movie recommendations based on the user’s previously watched movies-: User ko jo recommedation dikhayi usko usme se 95% movies achii lagi. Means agar 5 similar movies dikhayi toh unme se 4 uske taste ki hi thi.
2.  Built a RESTful API handling 10,000+ requests/day, merged with a Spring Boot application to display movie
    recommendations on the user dashboard-:
    - I used a load testing tool like Apache JMeter to simulate concurrent users hitting my API.
    - Example Setup: 10 virtual users, Each making 1 request every 2 seconds, Test duration: 15 minutes
    - 10 users × 0.5 req/sec × 3600 sec = 18,000 requests/hour
    - My API was able to handle this traffic but, it was giving very late response(latency was high), so i converted call to async calls
3. Implemented asynchronous API calls using RabbitMQ, reducing response time by 40% -: 
    - As i said , when API was having load , latency increased so to reduce it i used async calls.
    - Log timestamps before and after async optimization using Slf4j
    - Compare average response times:
      Before: 2.5s → After: 1.5s → = 40% reduction
4. Optimized chunked video streaming, improving playback buffering time by 30% -: In the same way(logging timestamps) i measured the buffering time.
5. Leveraged MongoDB for storage of movies, and performed unit testing with 10+ test cases using JUnit and
   Mockito -: 
   - Jab bhi mai kuch naya feature add krta tha , toh mujhe dekhna hota tha ki project sahi chal rha hai ki nhi 
   - Toh uske liye mujhe poora project run krna padta tha python API, rabbitMQ, since itni saari microservices hai toh project start krne mai time lagta tha, but mujhe dekhna tha ki feature sahi kaam kr rha hai ki nhi toh i did unit testing
   - Unit testing is fast, and usme mai sirf jo new feature add kra hai uske akele ki testing kr skta hu, poora project run krke test krne ki need ni hai.
6. Engineered logging with SLF4J and Log4j storing 500MB+ logs daily, and integrated with Splunk for monitoring
   and analysis-: At production(AWS EC2), it was very difficult to read the logs, so i used splunk
7. Deployed the application on AWS, utilizing Docker for dependency management -: used docker to download the exact same versions of python libraries in AWS , which is use in local project
8. Streamlined development workflows using shell scripting, Jenkins pipelines, and Docker for CI/CD. -: 
    - When i make a change, i push to github
    - i pull in AWS
    - I create a war file
    - I again deploy the code on tomcat, and restart the project 
    - this is a lengthy process , so i used Jenkins
    - I make changes in local, and push them to github and pipeline activates
9. Tracked application health and performance using AWS CloudWatch, setting up 5+ alerts-:
    - High CPU Utilization alert, indicating performance issue
    - High Memory Usage alert, indicating memory leaks
    - cost alert

