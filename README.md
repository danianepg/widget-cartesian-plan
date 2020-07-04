## Widgets on Cartesian Plan
The current project has the solution developed by [Daniane P. Gomes](https://www.linkedin.com/in/danianepg/).

Widgets on a cartesian plan: In memory version tries to mimic Spring native behaviours such as storage, pagination and ordering.

### Project Stack
 - Java 11
 - Maven 
 - SpringBoot 2.3
 - In memory database H2.

### How to Run
* Build the project:
```mvn clean install```
* Run with in-memory storage
```mvn spring-boot:run```
* Run with SQL database storage H2
```mvn spring-boot:run -Dspring-boot.run.profiles=db``` 

### The API
* Find all widgets: 
	Perform GET to [http://localhost:8080/api/widgets](http://localhost:8080/api/widgets)
	
* Find by id
	Perform GET to [http://localhost:8080/api/widgets/{id}](http://localhost:8080/api/widgets/%7Bid%7D)

* Filter by the desired area
Perform GET to [http://localhost:8080/api/widgets/filter?lowerX={lowerX}&lowerY={lowerY}&upperX={upperX}&upperY={upperY}](http://localhost:8080/api/widgets/filter?lowerX={lowerX}&lowerY={lowerY}&upperX={upperX}&upperY={upperY})

* Create a new widget
	Perform POST to [http://localhost:8080/api/widgets](http://localhost:8080/api/widgets)

* Update by id or create new in case it does not exists
	Perform PUT to [http://localhost:8080/api/widgets/{id}](http://localhost:8080/api/widgets/%7Bid%7D)
	
* Delete by id
	Perform DELETE to [http://localhost:8080/api/widgets/{id}](http://localhost:8080/api/widgets/%7Bid%7D)

### Storage
**In Memory**
For in-memory storage the data structure ConcurrentHashMap to leverage the concurrence problems.

**Database**
H2 was chosen as a SQL database to storage.

### Tests
They are unit and integration tests that coverage 92,1% of the application for both in-memory and database storage.

**Integration Tests**
* Test the application with a SQL database:
```src\test\java\com\danianepg\widget\controller\WidgetControllerDatabaseTest.java```
* Test the application with in-memory storage:
```src\test\java\com\danianepg\widget\controller\WidgetControllerInMemoryTest.java```

**Unit tests**
* ```src\test\java\com\danianepg\widget\repositories\WidgetInMemoryRepositoryTest.java```
* ```src\test\java\com\danianepg\widget\services\database\WidgetDatabaseServiceTest.java```
* ```src\test\java\com\danianepg\widget\services\inmemory\WidgetInMemoryServiceTest.java```

### Complications Implemented

**Pagination**

To define paging and sorting parameters, inform on the URL the parameters

 - page: page number starting from 0. 
 - size: for the number of records to be presented on the page.
 - sort: desired field name to order followed by ```,desc``` to descending order or empty for ascending order.

Example:
Retrieve the **second page** of records with **two records** per page, ordered by field **z desc**:
[http://localhost:8080/api/widgets?size=2&page=1&sort=z,desc](http://localhost:8080/api/widgets?size=2&page=1&sort=z,desc)

**Filtering**

Filter by widgets present in a certain area through the endpoint  [http://localhost:8080/api/widgets/filter?lowerX={lowerX}&lowerY={lowerY}&upperX={upperX}&upperY={upperY}](http://localhost:8080/api/widgets/filter?lowerX={lowerX}&lowerY={lowerY}&upperX={upperX}&upperY={upperY}).

Example: 
Filter all the widgets that are present in the area with lower boundary x=0 and y=0 and upper boundary x=100 and y=150:
[http://localhost:8080/api/widgets/filter?lowerX=0&lowerY=0&upperX=100&upperY=150](http://localhost:8080/api/widgets/filter?lowerX=0&lowerY=0&upperX=100&upperY=150)

Pagination can also be applied to filtered results.


**SQL Database**

H2 was chosen as SQL database to storage. All methods to save, update, delete, filter, paging and sorting were delegated to Spring. The database storage is activated through Spring Profiles when informed the profile ```db```.

Connect to [H2 console](http://localhost:8080/h2-console) to view the database. Use the parameters below:
 - Driver class name: org.h2.Driver 
 - JDBC URL: jdbc:h2:mem:testdb 
 - User Name: sa
The database is already initilized with some data defined on ``` src\main\resources\data.sql```


### Questions?
Email me: danianepg@gmail.com :)
