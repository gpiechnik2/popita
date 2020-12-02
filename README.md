
![Image of Popita](https://github.com/gpiechnik2/popita/blob/master/static/Popita_app.jpg)

# Popita
Loremi psum dolor mit

## Features
- Lorem ipsum dolor mit set 
- Lorem ipsum dolor mit set 
- Lorem ipsum dolor mit set 
- Lorem ipsum dolor mit set 

## What's included
- Android app
- Django app
- Adobe Xd project

## Dependencies

**In strings.xml file complete the data below with your credentials**

- For **facebook_app_id** and **fb_login_protocol_scheme**, go to [Facebook Login for Android - Quickstart](https://developers.facebook.com/docs/facebook-login/android/v2.4)
- For **web_client_id**, go to [Get your backend server's OAuth 2.0 client ID](https://developers.google.com/identity/sign-in/android/start-integrating#get_your_backend_servers_oauth_20_client_id)
- For **maps_api_key** go to [Creating API keys](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
- **server_ip** is the ip of the server to which we will send a request from the mobile application.
```
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="fb_login_protocol_scheme">YOUR_FACEBOOK_LOGIN_PROTOCOL_SCHEME</string>
<string name ="web_client_id">YOUR_WEB_CLIENT_ID</string>
<strng name="maps_api_key">YOUR_MAPS_API_KEY</string>
<string name="server_ip">YOUR_SERVER_IP</string>
```

## Setup

**Install dependencies**
```
pip install requirements.txt
```

**Migrate database**
```
python3 manage.py migrate
```

**Run server**
```
python3 manage.py
```

## TODO

## License
