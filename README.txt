My name is Teodor Cotruta.

This folder contains the code and the executable for the CRUD project which 
builds REST services for managing customer records. The executable is 27MB containing 
tomcat and all the required libraries to run the application and tests. 
Therefore it may not be possible to send it through email. 

Follow the instructions below in order to run and test the REST servives I created.
The "curent directory" in this file, means the directory that contains the pom.xml file and the README.txt.
Details of the application, what it does, how to run and how to test it are below.

REQUIREMENTS
====================
Write an application that provides CRUD RESTfull services for customer data.
Each customer has a name, adress, telephone number. 
Further, the code should demonstrate your level of proficiency in Test Driven Development.
The technology to be used: Java 7 or later, free choice of Java Frameworks.

SOLUTION DESIGN
===================
I created 2 entities to hold customer data: Customer and Address.
There relationship between the 2 is: 1 Customer has 0..1 Address(es)
The records are stored in a memory database.
The frameworks used to implement the application are: 
- spring-boot, which provides a CrudRepository and a JpaRepository, inheriting from the former one.
- jpa with hibernate, which is transparent to the user, wich provides the entities and the ORM to the database. 
- hsqldb for the inmemory database
- guava for cache management
The tables for the storage are created at the startup by 2 scripts executed by spring-boot, one is creating the tables, 
the second one is populating them with customer and address sample data.
Subsequent data can be managed using the REST services provided.
The project is a maven project
The Unit tests are using Junit and dependency injection to mock an MVC Servlet with
org.springframework.test.web.servlet.MockMvc. The code and unit tests are using dependency injection.

ARCHITECTURE
===============
The application is built in a tiered architecture: 
DATA, ORM (JPA), SERVICES to query data, REST controllers to expose the CRUD services.
This is part of a Spring MVC architecture without a view, 
REST services being the controllers and Entities being the Model.

For more details, Javadoc comments are provided in the code.

EXECUTABLE, PHYSICAL DESIGN
============================
The applicaiton can be run using maven or java.
The application provides REST services which can be used to manage Customer records.
A Customer has a reference to an address so REST end points for managing an address are also created.
The application is a spring boot application. On startup it scans for entities, 
sets up a cache manager and creates customer and address records in an hsql in memory database.
Once started the application end points are available on the following paths:

"/customer" to read all the customer records using a GET method
"/customer/{id}" to read one record using a GET
"/customer" to create a new customer record using POST
"/customer/{customerId}/address/{addressId}" to PUT update the address of a customer
"/customer/{id}" to update a record using PUT. 
"/customer/{id}" to delete a record with DELETE
"/address" to read all the records using a GET
"/address/{id}" to read one record using a GET
"/address" to create a record using POST
"/address/{id}" to update the records using PUT
"/address/{id}" to delete a record with DELETE


HOW TO RUN THE APPLICATION
==========================
1. 
If you have maven installed and configured, with JAVA_HOME pointing to a jdk 7 or 8 and Apache Maven 3 or later, 
and access to a maven repository with the spring libraries, 
then in a command window go this directory and run:
shell> mvn clean spring-boot:run
which will start the application, which will run on localhost:8080
Then open a command line window and execute the curl commands to see the REST services working.
The commands are documented below. On windows we have to escape the " for some curl versions. 
On UNIX it should work without escaping \".
If there are issues running mvn, here is the config I used:
shell> mvn --version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-11T05:41:47+13:00)
Java version: 1.7.0_79, vendor: Oracle Corporation
...
The pom is set to use jdk 7. You could change it to 8, but 7 works fine.

2.
If you don't have maven installed, you can run the application using java 7 or 8 as follows.
2.1 Open a command window, navigate to the current directory and type:
2.2 shell> java -jar target/teo-customer-crud-1.0-SNAPSHOT.jar
The jar was built with java version 1.7.0_79, so you can use java 1.7.0_79 and above, like 1.8xx
This will start the application which runs on a tomcat server. The server is part of the jar.
After the start, the application is available on localhost:8080
In order to test the REST services, please use the curl commands form below. 
You can also use soap-ui or postman.

If you have any quesitons, please let me know, email: teodor@xtra.co.nz

TESTING the REST Services
============================
There are 2 groups of services: customer CRUD and address CRUD, managing data for 2 entities: Customer and Address.
A customer references an address, cardinality being: 1 customer has 0..1 address(es)

ADDRESS SERVICES
====================
CREATE address:
command:
curl -H "Content-Type: application/json" -X POST -d "{ \"streetName\" : \"Wallavera\" , \"city\": \"SpringWater\" , \"country\" : \"New Zealand\" , \"streetNumber\" : \"33A\", \"streetType\" : \"avenue\", \"suburb\" : \"GreenBay\"}" http://localhost:8080/address
return:
{"addressId":6,"city":"SpringWater","country":"New Zealand","streetName":"Wallavera","streetNumber":"33A","streetType":"avenue","suburb":"GreenBay"}

READ address
command
curl localhost:8080/address/6
return
{"addressId":6,"city":"SpringWater","country":"New Zealand","streetName":"Wallavera","streetNumber":"33A","streetType":"avenue","suburb":"GreenBay"}

READ ALL addreses
command
curl localhost:8080/address
return an array of addresses
[{"addressId":1,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"BIG STREET","streetNumber":"2/34","streetType":"AVENUE","suburb":"NEW MARKET"},{"addressId":2,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"SINGLEVIEW","streetNumber":"5","streetType":"STREET","suburb":"TAKAPUNA"},{"addressId":3,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"QUEEN","streetNumber":"65b","streetType":"STREET","suburb":"ELLERSLIE"},{"addressId":4,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"VICTORIA","streetNumber":"125/42","streetType":"LANE","suburb":"GREENHORN"},{"addressId":5,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"BIG STREET","streetNumber":"9","streetType":"STREET","suburb":"LINCOLN"},{"addressId":6,"city":"SpringWater","country":"New Zealand","streetName":"Wallavera","streetNumber":"33A","streetType":"avenue","suburb":"GreenBay"}]

UPDATE an address
update address with id = 6
command
curl -H "Content-Type: application/json" -X PUT -d "{ \"addressId\": \"6\", \"streetName\" : \"Wallavera\" , \"city\": \"Wellington\" , \"country\" : \"Angola\" , \"streetNumber\" : \"33a\", \"streetType\" : \"street\"}" http://localhost:8080/address
return
{"addressId":6,"city":"Wellington","country":"Angola","streetName":"Wallavera","streetNumber":"33a","streetType":"street","suburb":null}

DELETE an address
command
curl -X DELETE localhost:8080/address/6
return

verify the address has been deleted:
curl localhost:8080/address/6
return


CUSTOMER SERVICES
====================
READ a customer
command
curl localhost:8080/customer/1
response
C:\Users\teodor>curl localhost:8080/customer/1
{"customerId":1,"name":"JEREMY","telephoneNumber":"0800 000 ZERO","address":{"addressId":1,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"BIG STREET","streetNumber":"2/34","streetType":"AVENUE","suburb":"NEW MARKET"}}


READ ALL customers
command
curl localhost:8080/customer
result:
[{"customerId":1,"name":"JEREMY","telephoneNumber":"0800 000 ZERO","address":{"addressId":1,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"BIG STREET","streetNumber":"2/34","streetType":"AVENUE","suburb":"NEW MARKET"}},{"customerId":2,"name":"MARIA","telephoneNumber":"+64 09 3452","address":{"addressId":2,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"SINGLEVIEW","streetNumber":"5","streetType":"STREET","suburb":"TAKAPUNA"}},{"customerId":3,"name":"ISABELLE","telephoneNumber":"CAN BE LETTERS","address":{"addressId":2,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"SINGLEVIEW","streetNumber":"5","streetType":"STREET","suburb":"TAKAPUNA"}},{"customerId":4,"name":"MILES","telephoneNumber":"09 449 1234","address":{"addressId":3,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"QUEEN","streetNumber":"65b","streetType":"STREET","suburb":"ELLERSLIE"}}]

CREATE a customer
command
curl -H "Content-Type: application/json" -X POST -d "{  \"name\": \"teo\" , \"telephoneNumber\" : \"094491234\" ,\"address\" :{ \"addressId\": \"1\", \"streetName\" : \"BIG STREET\" , \"city\": \"AUCKLAND\" , \"country\" : \"NEW ZELAND\" , \"streetNumber\" : \"2/34\", \"streetType\" : \"AVENUE\", \"suburb\" : \"NEW MARKET\"}}" http://localhost:8080/customer
{"customerId":6,"name":"teo","telephoneNumber":"094491234","address":{"addressId":1,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"BIG STREET","streetNumber":"2/34","streetType":"AVENUE","suburb":"NEW MARKET"}}

UPDATE a customer
command
curl -H "Content-Type: application/json" -X PUT -d "{ \"customerId\":6, \"name\": \"teo cotruta\" , \"telephoneNumber\" : \"094491234\" ,\"address\" :{ \"addressId\": \"1\", \"streetName\" : \"BIG STREET\" , \"city\": \"AUCKLAND\" , \"country\" : \"NEW ZELAND\" , \"streetNumber\" : \"2/34\", \"streetType\" : \"AVENUE\", \"suburb\" : \"NEW MARKET\"}}" http://localhost:8080/customer
result
{"customerId":6,"name":"teo cotruta","telephoneNumber":"094491234","address":{"addressId":1,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"BIG STREET","streetNumber":"2/34","streetType":"AVENUE","suburb":"NEW MARKET"}}

UPDATE CUSTOMER ADDRESS
commnd
curl  -X PUT http://localhost:8080/customer/6/address/2
result
{"customerId":6,"name":"teo cotruta","telephoneNumber":"094491234","address":{"addressId":2,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"SINGLEVIEW","streetNumber":"5","streetType":"STREET","suburb":"TAKAPUNA"}}

DELETE a customer
command
curl -X DELETE localhost:8080/customer/1
result

verify the customer has been deleted
command
curl localhost:8080/customer/1
result: nothing

===================
END of REST Services tests.

UNIT TESTS
=============
IN order to run the unit tests, you can import the maven project in eclipse and run the JUnit test classes.
Alternatively you can run maven with the test goal:
shell>mvn test
This will execute all teh junit tests.

Thank you for your time
Teodor Cotruta, 05/08/2016
