Transfer website
================

This application makes it possible to create a safe transfer of a file to another user.

The application uses Liquibase to create and upgrade the database, and Thymeleaf as the templating engine.
You can find the layout template at src/main/webapp/WEB-INF/thymeleaf/layout.html. The database is in-memory
for the production and file-based for test. You can therefore just drop the WAR file into Tomcat, and it will
create tables, load initial data and launch.

Dependencies
------------
* Tomcat 7
* Java 1.7
* Spring 4
* Thymeleaf 2.1.4
* H2 Database Engine

Automated tests
---------------
There are test examples of both controllers and data access objects using the Spring test package.

How to build
------------
You need Git to check the code out from the repository and to build you need Java and Maven. All other dependencies will automatically be downloaded by Maven.

To build you do:
```
git clone https://github.com/eea/eionet.transfer.git
cd eionet.transfer
mvn clean install
cp target/transfer.war /var/lib/tomcat/webapps/ROOT.war
```

This will create a `target` subdirectory, build the code, run the tests and put a WAR file in target. You then deploy this file to Tomcat. It contains an embedded database.

Deployment
----------
The default configuration is to allow you to deploy to your own workstation directly. You install the target/transfer.war to Tomcat's webapps directory as ROOT.war. You can make it create an initial user with administrator rights by setting system properties to configure the application.

On a CentOS system you can start Tomcat with the environment variable JAVA_OPTS set to some value or add lines to /etc/sysconfig/tomcat that looks like this:
```
JAVA_OPTS="-Dcas.service=http://transfers.com -Dinitial.username=myname"
JAVA_OPTS="$JAVA_OPTS -Ddb.url=jdbc:h2:tcp://localhost:8043//work/transferdb -Dstorage.dir=/work -Dupload.dir=/work"
```
These are the properties you can set:
```
db.driver
db.url
db.username
db.password
storage.dir
upload.dir
initial.username # Not needed when integrated with CAS.
initial.password
cas.service
cas.server.host
```
The default values are in src/main/resources/application.properties and src/main/resources/cas.properties.

