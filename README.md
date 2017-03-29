Prototype query-builder-daemon that uses Xenon to execute StoryTeller queries.

To compile use:

```bash
# (needs Java version 8)
./gradlew installDist
```

Then to run

```bash
cd ./build/install/query-builder-daemon  
./bin/query-builder-daemon
```


### Query daemon commands
- ``/status``
- ``/submit``
- ``/clearall``
- ``/clearuser``
- ``/rebuild``

**Status**
----
  Returns status of the Daemon.

* **URL**

  /status

* **Method:**

  `GET`
  
*  **URL Params**

  None

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:**

    ```
    QueryBuilderDaemon:\nQueries in queue " + running.size() + "\nQueries processed " + finishedQueries.get() + 
                "\nQueries failed " + failedQueries.get()
    ```

* **Error Response:**

  None

* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/status",
      dataType: "json",
      type : "GET",
      success : function(r) {
        console.log(r);
      }
    });
  ```
  
**Submit**
----
  Submits a new query for evaluation to the Daemon.

* **URL**

  /submit

* **Method:**

  `POST`
  
*  **URL Params**

  None

* **Data Params**

    ```javascript
    {
      "id": [number],
      "query": [string]
    }
    ```

* **Success Response:**

  * **Code:** 200 <br />

* **Error Response:**

  None

* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/submit",
      dataType: "json",
      data: 
        {
          "id": 1,
          "query": "--eventType fn:Assuming"
        }
      type : "POST",
      success : function(r) {
        console.log(r);
      }
    });
  ```
  
**Clear all jobs**
----
  Clears all previous jobs from the Daemon.

* **URL**

  /clearall

* **Method:**

  `POST`
  
*  **URL Params**

  None

* **Data Params**

   None

* **Success Response:**

  * **Code:** 200 <br />

* **Error Response:**

  None

* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/clearall",
      dataType: "json",      
      type : "POST",
      success : function(r) {
        console.log(r);
      }
    });
    ```
  
**Clear User Jobs**
----
  Clears the selected user's jobs from the Daemon.

* **URL**

  /clearuser

* **Method:**

  `POST`
  
*  **URL Params**

  None

* **Data Params**

    ```javascript
    {      
      "username": [string]
    }
    ```

* **Success Response:**

  * **Code:** 200 <br />

* **Error Response:**

  None

* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/clearuser",
      dataType: "json",
      data: 
        {
          "username": "maarten"
        }
      type : "POST",
      success : function(r) {
        console.log(r);
      }
    });
  ```
  
**Rebuild database**
----
  Rebuilds the database for the query-builder-client.

* **URL**

  /rebuild

* **Method:**

  `POST`
  
*  **URL Params**

  None

* **Data Params**

   None

* **Success Response:**

  * **Code:** 200 <br />

* **Error Response:**

  None

* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/rebuild",
      dataType: "json",      
      type : "POST",
      success : function(r) {
        console.log(r);
      }
    });
    ```
