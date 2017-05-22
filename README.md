# Unity Technologies -- SDET Coding Assignment

Implemented a solution for the provided QA project using spring boot framework.


## Pre-requisites

1. [Maven](https://maven.apache.org/download.cgi) (at least 3.3.9)
2. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)


## Steps to run

1. Clone this repository
  ```Shell
  git clone https://github.com/onkarganjewar/unity-sdet-assignment.git
  ```
2. Change directory to the '/SDET-Project'
  ```Shell
  cd SDET-Project/
  ```
3. Clean the directory (Optional)
  ```Shell
  mvn clean
  ```
4. Clean and build the project (Will take some time for the first time build)
  ```Shell
  mvn clean install
  ```
5. Run all the test cases
  ```Shell
  mvn test
  ```
6. Start the spring boot application server
  ```Shell
  mvn spring-boot:run
  ```


## Demo

* Start the server
  ```Shell
  mvn spring-boot:run
  ```

* Application will now run at http://localhost:8080/

**NOTE:** Project logs file will get stored under directory __'SDET-Project/logs/'__ by the name __'project-logging.log'__


* Use any [REST client console](https://chrome.google.com/webstore/detail/rest-console/cokgbflfommojglbmbpenpphppikmonn) or [POSTMAN](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=en) chrome extensions to test the following RESTful Web Services:


### POST - To create a new location

#### Request

```http
POST /createproject
```

```json
{
    "id": 9,
    "projectName": "test project number 9",
    "creationDate": "05112019 00:00:00",
    "enabled": true,
    "expiryDate": "05202021 00:00:00",
    "targetCountries": ["INDIA", "USA"],
    "projectCost": 22.5,
    "projectUrl": "https://www.google.com",
    "targetKeys": [{
        "number": 15,
        "keyword": "movie"
    }, {
        "number": 30,
        "keyword": "games"
    }]
}

```

#### Response

```
HTTP Response Code: 200
```

```json
“campaign is successfully created”
```



### GET - To retrieve a stored project

#### Request

```http
GET /requestproject?projectid=1
```

#### Response

```
HTTP Response Code: 200
```


```json
{
    "projectName": "test project number 9",
    "projectCost": 22.5,
    "projectUrl": "https://www.google.com"
}
```
