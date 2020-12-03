# Documentation of REST API for Popita app.

**Show user rooms**
----
  Returns json data about rooms of a user.

* **URL**

  chat/rooms/

* **Method:**

  `GET`
  
* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

* **URL Params**

  None
  
* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
        "count": 1,
        "next": null,
        "previous": null,
        "results": [
            {
            "id": 1,
            "last_message": "I’m a painter you know.",
                "last_sender": 0,
                "receiver_id": 1,
                "receiver_name": "Johnny",
                "last_message_timestamp": "01:38"
            }
          ]
        }
    ```
 
* **Error Response:**

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
        "detail": "Invalid token."
      }
    ```
* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/chat/rooms/
  ```
  
 **Show user spicified room**
----
  Returns json data about specified room of a user.

* **URL**

  chat/rooms/:room_id/

* **Method:**

  `GET`
 
* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`
  
*  **URL Params**

   **Required:**
 
   `room_id=[integer]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "id": 1,
          "receivers": [
              {
                  "id": 1,
                  "first_name": "Johnny",
                  "gender": "Male",
                  "background_color": "Blue",
                  "job": "Graphic designer",
                  "preferred_drink": "Amarena",
                  "description": "I’m a painter you know. I can paint whatever I want. If you want to buy painting message."
              }
          ]
      }
    ```
 
* **Error Response:**

  * **Code:** 404 NOT FOUND <br />
    **Content:**
    
    ```
        {
          "detail": "Not found."
        }
    ```
  
  OR

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
        "detail": "Invalid token."
      }
    ```

* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/chat/rooms/:room_id/
  ```
  
 **Show messages from a spicified room**
----
  Returns json data about messages of a specified room of a user.

* **URL**

  chat/rooms/:room_id/messages/

* **Method:**

  `GET`

* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

   **Required:**
 
   `room_id=[integer]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "count": 1,
          "next": null,
          "previous": null,
          "results": [
              {
                  "id": 1,
                  "message": "My first message",
                  "timestamp": "2020-11-20 21:11",
                  "receiver": 0
              }
          ]
      }
    ```
 
* **Error Response:**

  * **Code:** 404 NOT FOUND <br />
    **Content:**
    
    ```
        {
          "detail": "Not found."
        }
    ```
  
  OR

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
        "detail": "Invalid token."
      }
    ```

* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/chat/rooms/:id/messages/
  ```
  
   **Show specified message from spicified room**
----
  Returns json data about specified message of a user.

* **URL**

  chat/rooms/:room_id/messages/:message_id/

* **Method:**

  `GET`

* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

   **Required:**
 
   `room_id=[integer]` <br />
   `message_id=[integer]`

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "id": 1,
          "receiver": 2,
          "room": {
              "id": 1,
              "receivers": [
                  {
                      "id": 1,
                      "first_name": "Johnny",
                      "gender": "Male",
                      "background_color": "Blue",
                      "job": "Graphic designer",
                      "preferred_drink": "Amarena",
                      "description": "I’m a painter you know. I can paint whatever I want. If you want to buy painting message."
                  },
                  {
                      "id": 2,
                      "first_name": "Ashley",
                      "gender": "Female",
                      "background_color": "Orange",
                      "job": "Tester",
                      "preferred_drink": "N/A",
                      "description": "I’m a painter you know. I can paint whatever I want. If you want to buy painting message."
                  }
              ]
          },
          "message": "My first message.",
          "timestamp": "2020-11-20 21:11"
      }
    ```
 
* **Error Response:**

  * **Code:** 404 NOT FOUND <br />
    **Content:**
    
    ```
        {
          "detail": "Not found."
        }
    ```
  
  OR

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
        "detail": "Invalid token."
      }
    ```

* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/chat/rooms/:room_id/messages/:message_id/
  ```

   **Show localization data**
----
  Returns json data about nearby people.

* **URL**

  localization/localizations/

* **Method:**

  `GET`

* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

  None

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "count": 1,
          "next": null,
          "previous": null,
          "results": [
              {
                  "id": 1,
                  "user": {
                      "id": 2,
                      "first_name": "Johnny"
                  },
                  "longitude": 22.001986,
                  "latitude": 50.044788,
                  "attitude": 1.0,
                  "location": "Sienkiewicza 3/10, Rzeszów, Poland.",
                  "timestamp": "2020-11-20 17:11",
                  "distance": 0.08216830339470174
              }
          ]
      }
    ```
 
* **Error Response:**

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
        "detail": "Invalid token."
      }
    ```

* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/localization/localizations/
  ```

   **Show localization data**
----
  Returns json data about nearby people.

* **URL**

  localization/localizations/

* **Method:**

  `GET`

* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

  None

* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "count": 1,
          "next": null,
          "previous": null,
          "results": [
              {
                  "id": 1,
                  "user": {
                      "id": 2,
                      "first_name": "Johnny"
                  },
                  "longitude": 22.001986,
                  "latitude": 50.044788,
                  "attitude": 1.0,
                  "location": "Sienkiewicza 3/10, Rzeszów, Poland.",
                  "timestamp": "2020-11-20 17:11",
                  "distance": 0.08216830339470174
              }
          ]
      }
    ```
 
* **Error Response:**

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
        "detail": "Invalid token."
      }
    ```

* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/localization/localizations/
  ```
