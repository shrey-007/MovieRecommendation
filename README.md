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
