# Kafka Producer
---
run instructions
1. git clone on your local machine
2. go to the root dir of the git clone and mvn package
3. java -jar target/producer-0.0.1-SNAPSHOT.jar --kafka.username=xxxxxx --kafka.password=xxxxx --writable.dir=/tmp

For step 3 you'll need to provide credentials to Confluent Cloud that were provided to you.  
The api.key is the username and api.secret is the password.
You'll also need to make sure you pass it a valid writable directory absolute path for writable.dir, in the above example /tmp is used.  
If that location is writable then that can be used.