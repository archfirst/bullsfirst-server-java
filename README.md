# bullsfirst-server-java
Bullsfirst is a sample trading application demonstrating best practices in software development. You can read more about it on [archfirst.org](https://archfirst.org/bullsfirst/).

## Requirements
- [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [Apache Maven 3.x](https://maven.apache.org/)
- [MySQL 5.x](http://dev.mysql.com/downloads/)
- [mysql-connector-java 5.x](http://dev.mysql.com/downloads/connector/j/)
- [GlassFish 3.1.2.2](http://download.java.net/glassfish/3.1.2.2/release/glassfish-3.1.2.2.zip)

## Build Instructions

### Install quickfixj in Maven repository
This step is required because connecting to the quickfixj maven repository was I was giving the following error:

    No connector available to access repository MarketceteraRepo (>http://repo.marketcetera.org/maven) of type default using the available factories WagonRepositoryConnectorFactory

- Download quickfixj 1.5.0 from http://sourceforge.net/project/showfiles.php?group_id=163099 and unzip it at C:/lib/quickfixj-1.5.0 (you will have to rename to this). Run the following command to install the library in the Maven repository:

    mvn install:install-file -Dfile=C:/lib/quickfixj-1.5.0/quickfixj-all-1.5.0.jar -DgroupId=quickfixj -DartifactId=quickfixj-all -Dversion=1.5.0 -Dpackaging=jar -DgeneratePom=true

### Start MySQL Database Server and login as root
- Start MySQL System Tray Monitor (from Start > All Programs > MySQL).
- In the system tray, right click on MySQL System Tray Monitor and select Start Instance.

    > mysql --user=root --password
    Enter password: xxxx
    mysql> show databases;

### Create a Database for Bullsfirst Exchange
    mysql> create database bfexch_javaee;
    mysql> create user 'bfexch_javaee'@'localhost' identified by '<password>';
    mysql> grant all on bfexch_javaee.* TO 'bfexch_javaee'@'localhost';

### Create a Database for Bullsfirst OMS JavaEE
    mysql> create database bfoms_javaee;
    mysql> create user 'bfoms_javaee'@'localhost' identified by '<password>';
    mysql> grant all on bfoms_javaee.* TO 'bfoms_javaee'@'localhost';

### Configure GlassFish
- Open a Command shell and traverse to GLASSFISH_HOME\bin
- Stop the GlassFish server.
    asadmin stop-domain domain1
- Copy the MySQL driver (mysql-connector-java-5.1.13-bin.jar) to GLASSFISH_HOME\lib. (On the eApps server we store a copy of the driver at ~/install/mysql-connector-java-5.1.34-bin.jar.)
- Type the following command to create a master-password file under GLASSFISH_HOME\domains\domain1 (required by maven-glassfish-plugin):
    asadmin change-master-password --savemasterpassword=true domain1
    Enter the current master password> changeit
    Enter the new master password> [new-password]
    Enter the new master password again> [new-password]
- Type the following command at the command prompt to start the server:
    asadmin start-domain domain1
- Login to the GlassFish admin console as admin (url http://localhost:4848)
- Add Hibernate JPA provider (in addition to the default TopLink JPA provider).
- In the navigation bar, click on Update Tool.
- Select Component called hibernate and click Install.
- Stop GlassFish before proceeding to the next step. (Hibernate JPA provider will be automatically recognized during the next startup):
    asadmin stop-domain domain1
- Unfortunately what gets installed is hibernate 3.5.0 which is incompatible with Bullsfirst. Replace it with hibernate 3.6.0. To do this, pick up the hibernate3.jar from hibernate-3.6.0.Final distribution and drop it in GLASSFISH_HOME\lib, overwriting the original file. (On the eApps server we store a copy of the jar file at ~/install/hibernate3.jar.)

### Configure GlassFish for slf4j Logging
Based on [this](http://hwellmann.blogspot.com/2010/12/glassfish-logging-with-slf4j-part-2.html) article
- Copy the following JARs to GLASSFISH_HOME\lib\endorsed:
    wget http://central.maven.org/maven2/org/slf4j/jul-to-slf4j/1.6.1/jul-to-slf4j-1.6.1.jar
    wget http://central.maven.org/maven2/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar
    wget http://central.maven.org/maven2/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar
    wget http://mirrors.ibiblio.org/pub/mirrors/maven2/log4j/log4j/1.2.8/log4j-1.2.8.jar
    log4j-config-xxx.jar (from GlassFish Logging Configuration) (choose between dev or prod)
- Edit GLASSFISH_HOME\domains\domain1\config\domain.xml and add the following properties in the jvm-options section (there are two such sections – put these lines in the first section that’s under <config name="server-config">):
    <jvm-options>-Djava.util.logging.config.file=${com.sun.aas.instanceRoot}/config/my_logging.properties</jvm-options>
    <jvm-options>-Dlog4j.log.file=${com.sun.aas.instanceRoot}/logs/glassfish.log</jvm-options>
- Create my_logging.properties file as specified in the jvm-options above under GLASSFISH_HOME\domains\domain1\config with the following contents:
    handlers = org.slf4j.bridge.SLF4JBridgeHandler
    com.sun.enterprise.server.logging.GFFileHandler.flushFrequency=1
    com.sun.enterprise.server.logging.GFFileHandler.file=${com.sun.aas.instanceRoot}/logs/server.log
    com.sun.enterprise.server.logging.GFFileHandler.rotationTimelimitInMinutes=0
    com.sun.enterprise.server.logging.GFFileHandler.logtoConsole=false
    com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=2000000
    com.sun.enterprise.server.logging.GFFileHandler.alarms=false
    com.sun.enterprise.server.logging.GFFileHandler.formatter=com.sun.enterprise.server.logging.UniformLogFormatter
    com.sun.enterprise.server.logging.GFFileHandler.retainErrorsStasticsForHours=0
- Restart GlassFish. You will now see only a few messages in server.log, all the rest go to glassfish.log.

### Create JDBC Connection Pools on GlassFish

#### Create bfexch_javaee Connection Pool
- Login to the GlassFish admin console as admin (url http://localhost:4848)
- In the navigation bar, click on Resources > JDBC > Connection Pools
- Click New on the Connection Pools page.
- Create a new connection pool using the following parameters and click Next
    Pool Name: bfexch_javaee
    Resource Type: javax.sql.ConnectionPoolDataSource
    Database Vendor: MySql
- Fill in the following properties and click Finish
    User: bfexch_javaee
    Password: bfexch_javaee (put the correct password for your database)
    URL & Url: jdbc:mysql://localhost:3306/bfexch_javaee
- You will now be on the JDBC Connection Pools page. Click on the connection pool you just created and then click on Ping to make sure the connection is setup properly.
- In the navigation bar, click on Resources > JDBC > JDBC Resources
- Click New on the JDBC Resources page.
- Create a new JDBC resource using the following parameters and click OK
    JNDI Name: bfexch_javaee_connection_pool
    Pool Name: bfexch_javaee

#### Create bfoms_javaee Connection Pool
Follow the same steps above but replace bfexch_javaee with bfoms_javaee (remember to put the correct database password)

### Create JDBC Security Realm
- In the navigation bar, click on Configurations > server-config > Security > Realms
- Click New.
- Fill in the Fields as follows:
    - Name: bullsfirst-javaee
    - Class Name: com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm
    - JAAS Context: jdbcRealm
    - JNDI (datasource): bfoms_javaee_connection_pool
    - User Table: Users
    - User Name Column: username
    - Password Column: passwordHash
    - Group Table: UserGroup
    - Group Name Column: groupname
    - Digest Algorithm: MD5
    - Encoding: Base64

### Configure JMS on GlassFish (Open MQ)
- In the navigation bar, click on Configurations > server-config > Java Message Service
- Confirm that the Type field is set to EMBEDDED
- In the navigation bar, click on Resources > JMS Resources > Connection Factories
- Click on New...
- Fill in the following fields:
    Pool Name: jms/ConnectionFactory
    Resource Type: javax.jms.ConnectionFactory
- Click Ok. This creates a connection factory called jms/ConnectionFactory and also (under Connectors)
    - a Connector Resource called jms/ConnectionFactory
    - a Connection Pool called jms/ConnectionFactory
- In the navigation bar, click on Resources > JMS Resources > Destination Resources
- Click on New...
- Fill in the following fields (this step also creates an Admin Object resource called jms/OmsToExchangeQueue):
    JNDI Name: jms/OmsToExchangeQueue
    Physical Destination Name: OmsToExchangeQueue
    Resource Type: javax.jms.Queue

- Similarly create two more queues and a topic (even though we don’t have a Spring OMS yet, the queue must be created. The exchange expects it to be there).
    JNDI Name: jms/ExchangeToOmsJavaeeQueue
    Physical Destination Name: ExchangeToOmsJavaeeQueue
    Resource Type: javax.jms.Queue

    JNDI Name: jms/ExchangeToOmsSpringQueue
    Physical Destination Name: ExchangeToOmsSpringQueue
    Resource Type: javax.jms.Queue

    JNDI Name: jms/ExchangeMarketPriceTopic
    Physical Destination Name: ExchangeMarketPriceTopic
    Resource Type: javax.jms.Topic

- Expose the dead message queue (mq.sys.dmq) to JNDI by creating a resource as follows:
    JNDI Name: jms/DeadMessageQueue
    Physical Destination Name: mq.sys.dmq
    Resource Type: javax.jms.Queue

- You can use imqcmd (under C:\apps\glassfish-3.1.2.2\mq\bin) to manage the queues. Default credentials to run this command are admin/admin.
    To query a queue:
        imqcmd query dst -t q -n ExchangeToOmsJavaeeQueue
        imqcmd query dst -t q -n mq.sys.dmq (dead message queue)
    To purge a queue:
        imqcmd purge dst -t q -n ExchangeToOmsJavaeeQueue
        imqcmd purge dst -t q -n mq.sys.dmq (dead message queue)

### Build Maven Projects
Either build projects one at a time as described below or run the build-all.bat batch file to build all projects in one shot.

#### Archfirst Common Libraries
- Open a Command shell and traverse to SRC_DIR\java\projects\archfirst-common:
- Type the following command at the command prompt to build the project
    mvn clean install

#### Bullsfirst Common Libraries
- Open a Command shell and traverse to SRC_DIR\java\projects\bullsfirst-common:
- Type the following command at the command prompt to build the project
    mvn clean install

#### Bullsfirst Exchange
- Traverse to SRC_DIR\java\projects\bullsfirst-exchange-javaee:
- Type the following command at the command prompt to build the project
    mvn clean install
- Create database schema and import data
    cd bfexch-ddl
    create-schema
    import
- Deploy application to Glassfish
    cd ..\bfexch-javaee-web
    mvn glassfish:deploy (assuming that GlassFish server is running)

#### Bullsfirst OMS Common
- Traverse to SRC_DIR\java\projects\bullsfirst-oms-common:
- Type the following command at the command prompt to build the project
    mvn clean install
- Create database schema and import data
    cd bfoms-common-ddl
    create-schema
    import (optional – create jhorner user)

#### Bullsfirst Java EE
- Traverse to SRC_DIR\java\projects\bullsfirst-oms-javaee:
- Type the following command at the command prompt to build the project
    mvn clean install
- Deploy application to Glassfish
    cd bfoms-javaee-web
    mvn glassfish:deploy (assuming that GlassFish server is running)

### Build Bullsfirst jQuery-Backbone Client and deploy to localhost
See instructions [here](https://github.com/archfirst/bullsfirst-jquery-backbone)

### Start Trading!
Point your browser to [http://localhost:8080](http://localhost:8080) to start trading.

