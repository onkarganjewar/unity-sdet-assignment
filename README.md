# Unity Technologies -- SDET Coding Assignment

Implemented a solution for the provided QA project using spring boot framework.


## Pre-requisites

1. [Maven](https://maven.apache.org/download.cgi) (at least 3.3.9)
2. [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)


## Steps to run

1. Clean the directory (Optional)
    ```
    mvn clean
    ```
2. Clean and build the project (Will take some time for the first time build)
    ```
    mvn clean install
    ```
3. Run all the test cases
    ```
    mvn test
    ```
4. Start the spring boot application server
    ```
    mvn spring-boot:run
    ```


## Demo

* Start the server
```
mvn spring-boot:run
```

* Application will now run at http://localhost:8080/

* Use any [REST client console](https://chrome.google.com/webstore/detail/rest-console/cokgbflfommojglbmbpenpphppikmonn) or [POSTMAN](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=en) chrome extensions to test the following RESTful Web Services:


### POST - To create a new location

#### Request

```http
POST /createproject
```

```json
{  
    "id": 1,  
    "projectName": "test project number 1",  
    "creationDate": "05112017 00:00:00",  
    "expiryDate": "05202017 00:00:00",  
    "enabled": true,   
    "targetCountries": ["USA", "CANADA", "MEXICO", "BRAZIL"],  
    "projectCost": 5.5,  
    "projectUrl": "http://www.unity3d.com",  
    "targetKeys": [{  
            "number": 25,  
            "keyword": "movie"  
        },  
        {  
            "number": 30,  
            "keyword": "sports"  
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
    "projectName":"test project number 1",  
    "projectCost": 5.5,  
    "projectUrl": "http://www.unity3d.com"  
}

```
