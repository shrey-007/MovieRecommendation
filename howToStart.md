1. open docker
2. run command docker pull rabbitmq:4.0.0-rc.2-management
3. run command docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:4.0.0-rc.2-management
4. Open web ui of rabbit mq at http://localhost:15672/
5. Both username and password are "guest"
6. create a queue named queue_demo, and an exchange named exchange_demo and bind the exchange to the queue using a routing key named routing_key_demo
7. open terminal in PythonApi folder and run "python app.py" command
8. Run the project, it is working completely fine