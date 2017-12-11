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

---
This app will read the 3 csv files in the resources folder and publish them each its own topic.
It will add a delay half second delay in between each publish to slow things down for so 
users can see whats happening.

The app will take the 3 avsc  (avro schema) files from the resources/avro dir and run the maven-avro-pluging (see the pom.xml)
and generate pojos into the com.rbc.cloud.hackathon.data package.  The csv files will get
read in the Kafka*Producer class row by row and each row will get converted to an instance of 
the avro schema generated pojo, which gets sent to a kafka topic.  The pojos are commited to 
github but they get re-generated everytime you do a maven build or package.

So for example each row in the customers.csv file becomes a Customers.java class, and this is
the payload of a messages that's sent to the matching topic.

---
In order to build and run this app you will require a version of the jdk > 1.8.0_101.  If you have jdk 1.8 pay 
special attention to its update number (i.e. 101) and make sure you meet the minimum standard.


