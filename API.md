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
    
  OR
    
  * **Code:** 400 BAD REQUEST 
    
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
                  "first_name": "Johnny"
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

  OR
  
  * **Code:** 400 BAD REQUEST 

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
                  "message": "My message",
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
    
  OR
  
  * **Code:** 400 BAD REQUEST 

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
                      "first_name": "Johnny"
                  },
                  {
                      "id": 2,
                      "first_name": "Joe"
                  }
              ]
          },
          "message": "My message.",
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

  OR

  * **Code:** 400 BAD REQUEST 

* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/chat/rooms/:room_id/messages/:message_id/
  ```

**Send message data**
----
  Returns json data about send message.

* **URL**

  chat/message/new/

* **Method:**

  `POST`

* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

  None

* **Data Params**

    ```
      {
          "receiver": <Integer>,
          "message": <String>
      }
    ```
    
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
                      "first_name": "Johnny"
                  },
                  {
                      "id": 2,
                      "first_name": "Joe"
                  }
              ]
          },
          "message": "My message.",
          "timestamp": "2020-12-03 16:12"
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

  OR

  * **Code:** 400 BAD REQUEST 


* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" -d '{"receiver": <RECEIVER_ID>, "message": <MESSAGE>}' http://hostname/chat/message/new/
  ```

**Show localization data**
----
  Returns json data about nearby people.

* **URL**

  localizations/

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

  OR

  * **Code:** 400 BAD REQUEST 


* **Sample Call:**

  ```
    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/localizations/
  ```
  
**Change localization data**
----
  Returns json data about your updated location.

* **URL**

  localizations/

* **Method:**

  `POST`

* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

  None

* **Data Params**

    ```
      {
          "longitude": <Double>,
          "latitude": <Double>,
          "attitude": <Double>,
          "location": <String>
      }
    ```

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "id": 1,
          "user": {
              "id": 1,
              "first_name": "Johnny"
          },
          "longitude": 12.312,
          "latitude": 1.342423,
          "attitude": 12312.0,
          "location": "Sienkiewicza 7/23 Rzeszów, Poland",
          "timestamp": "2020-12-03 17:12"
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

  OR

  * **Code:** 400 BAD REQUEST 

* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" -d '{"logitude": <LONGITUDE>, "latitude": <LATITUDE>, "attitude": <ATTITUDE>, "location": <LOCATION>}' http://hostname/localizations/
  ```

**Register user**
----
  Returns auth token.

* **URL**

  auth/users/

* **Method:**

  `POST`
  
* **Header Params**

  None

* **URL Params**

  None
  
* **Data Params**

    ```
      {
          "email": <String>,
          "password": <String>,
          "re_password": <String>
      }
    ```

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "auth_token": "eb5aecc34b2d8a5f46d3193d48e0b16b40029a12"
      }
    ```
 
* **Error Response:**

  * **Code:** 409 CONFLICT <br />
    **Content:**
    
    ```
      {
          "email": "User with given email already exists."
      }
    ```
  
  OR
  
  * **Code:** 400 BAD REQUEST 
  
* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d '{"email": <EMAIL>, "password": <PASSWORD>, "re_password": <PASSWORD>}' http://hostname/auth/users/
  ```

**Set user password**
----
  Returns info about successful changing the password.

* **URL**

  auth/users/set_password/

* **Method:**

  `POST`
  
* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

* **URL Params**

  None
  
* **Data Params**

    ```
      {
          "current_password": <String>,
          "new_password": <String>
      }
    ```

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "password": "Password has been changed."
      }
    ```
 
* **Error Response:**

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
          "User": "Anonymous users can not change user password."
      }
    ```
  
  OR
  
  * **Code:** 400 BAD REQUEST 
  
* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d '{"email": <EMAIL>, "password": <PASSWORD>, "re_password": <PASSWORD>}' http://hostname/auth/users/
  ```

**Login user**
----
  Returns auth token.

* **URL**

  auth/token/login/

* **Method:**

  `POST`
  
* **Header Params**

  None

* **URL Params**

  None
  
* **Data Params**

    ```
      {
          "email": <String>,
          "password": <String>
      }
    ```

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "auth_token": "38c5cba2c678551ad3077dfbcef4015cec12ab47"
      }
    ```
 
* **Error Response:**

  * **Code:** 400 BAD REUQEST
    
* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d '{"email": <EMAIL>, "password": <PASSWORD>}' http://hostname/auth/token/login/
  ```

**Remove current user token**
----
  Returns status 204 NO CONTENT.

* **URL**

  auth/token/logout/

* **Method:**

  `POST`
  
* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

* **URL Params**

  None
  
* **Data Params**

  None

* **Success Response:**

  * **Code:** 204 <br />
    
* **Error Response:**

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:**
    
    ```
      {
          "detail": "Invalid token."
      }
    ```
    
  OR
  
  * **Code:** 400 BAD REQUEST 

* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/auth/token/logout/
  ```

**User profile info**
----
  Returns user profile info.

* **URL**

  auth/profiles/me/

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
          "user_id": 1,
          "first_name": "Johnny",
          "gender": "Male",
          "background_color": "Orange",
          "job": "Tester",
          "preferred_drink": "Amarena",
          "description": "I'm a painter you know."
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
 
  OR
 
  * **Code:** 400 BAD REQUEST 

* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/auth/profiles/me/
  ```
  
**User profile info**
----
  Returns user profile info.

* **URL**

  auth/profiles/:profile_id/

* **Method:**

  `GET`
  
* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

   **Required:**
 
   `profile_id=[integer]`
  
* **Data Params**

  None

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "user_id": 1,
          "first_name": "Johnny",
          "gender": "Male",
          "background_color": "Orange",
          "job": "Tester",
          "preferred_drink": "Amarena",
          "description": "I'm a painter you know."
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
  
  OR
  
  * **Code:** 400 BAD REQUEST 
    
* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Token <YOUR_TOKEN>" http://hostname/auth/profiles/<PROFILE_ID>/
  ```
  
**Login user with Google**
----
  Returns auth token.

* **URL**

  auth/google/token/login/

* **Method:**

  `POST`
  
* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

  None
  
* **Data Params**

    ```
      {
          "id_token": <String>
      }
    ```

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "user_id": 1,
          "first_name": "Johnny",
          "gender": "Male",
          "background_color": "Orange",
          "job": "Tester",
          "preferred_drink": "Amarena",
          "description": "I'm a painter you know."
      }
    ```    
* **Error Response:**

  * **Code:** 400 BAD REQUEST 
    
* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d '{"id_token": <ID_TOKEN>}' http://hostname/auth/google/token/login/
  ``` 
  
**Login user with Facebook**
----
  Returns auth token.

* **URL**

  auth/facebook/token/login/

* **Method:**

  `POST`
  
* **Header Params**

  `Authorization: Token <AUTHORIZATION_TOKEN>`

*  **URL Params**

  None
  
* **Data Params**

    ```
      {
          "access_token": <String>,
          "facebookId": <String>
      }
    ```

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 
    
    ```
      {
          "user_id": 1,
          "first_name": "Johnny",
          "gender": "Male",
          "background_color": "Orange",
          "job": "Tester",
          "preferred_drink": "Amarena",
          "description": "I'm a painter you know."
      }
    ```    
* **Error Response:**

  * **Code:** 400 BAD REQUEST 
    
* **Sample Call:**

  ```
    curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d '{"access_token": <ACCESS_TOKEN>, "facebookId": <FACEBOOKID>}' http://hostname/auth/facebook/token/login/
  ``` 
