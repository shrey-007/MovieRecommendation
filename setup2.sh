# start database
sudo systemctl start mongod

# fill database
cd PythonAPI
mongoimport --db movies --collection movies --type csv --file movies.csv --headerline

# run application
cd DockerImages/springboot
java -jar MoviesRecommender-0.0.1-SNAPSHOT.jar
