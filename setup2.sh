#The url in dashboard.js for the search bar will not work coz localhost is written in it

# start database
sudo systemctl start mongod

# fill database
cd PythonAPI
mongoimport --db movies --collection movies --type csv --file movies.csv --headerline

cd ..

# run python api, but .pkl files git se jab aa rhi hai toh unme error hai toh Filezilla se main folder app mai app.py,dockerfile,.pkl,requirements.txt daalo instead of fetching from git
sudo docker build -t flaskapp .
sudo docker run -d -p 5000:5000 flaskapp

## setup rabbit mq
docker pull rabbitmq:4.0.0-rc.2-management
docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:4.0.0-rc.2-management
#credentials are guest, guest, login and create exchange(exchange_demo), queue(queue_demo) and bind them through routing key routing_key_demo

# run application
cd DockerImages/springboot
java -jar MoviesRecommender-0.0.1-SNAPSHOT.jar
