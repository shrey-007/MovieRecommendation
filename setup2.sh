sudo systemctl start mongod
cd DockerImages/springboot
mongoimport --db movies --collection movies --type csv --file /PythonAPI/movies.csv --headerline
java -jar MoviesRecommender-0.0.1-SNAPSHOT.jar
