mkdir app
cd app
git init
git remote add origin https://github.com/shrey-007/MovieRecommendation.git
git pull origin master
# write something in application.properties
sudo docker build -t tommy DockerImages/springboot/
sudo docker run -d -p 8080:8080 --name spring-boot-app tommy
