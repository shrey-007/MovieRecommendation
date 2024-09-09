# start database
sudo systemctl start mongod

# fill database
cd PythonAPI
mongoimport --db movies --collection movies --type csv --file movies.csv --headerline

cd ..

# run python api, but .pkl files git se jab aa rhi hai toh unme error hai toh Filezilla se main folder app mai app.py,dockerfile,.pkl,requirements.txt daalo instead of fetching from git
sudo docker build -t flaskapp .
sudo docker run -d -p 5000:5000 flaskapp

# run application
cd DockerImages/springboot
java -jar MoviesRecommender-0.0.1-SNAPSHOT.jar
