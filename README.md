
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

## Setup
**In file secure.properties complete the data below with your credentials**
- **MAPS_API_KEY** is used for lorem ipsum. The key can be found at: [link to API key!](http://google.com) 
- **SERVER_IP** is the ip of the server to which we will send a request from the mobile application.
- **WEB_APP_CLIENT_ID** is the key used for lorem ipsum. The key can be found at: [link to API key!](http://google.com) 

```
MAPS_API_KEY=<YOUR_MAPS_API_KEY>
SERVER_IP=<YOUR_SERVER_IP>
WEB_APP_CLIENT_ID=<YOUR_WEB_APP_CLIENT_ID>
```


**In strings.xml file complete the data below with your credentials**

- For **facebook_app_id** and **fb_login_protocol_scheme**, go to [Facebook Login for Android - Quickstart](https://developers.facebook.com/docs/facebook-login/android/v2.4)
- For **web_client_id**, go to [GIGA](https://google.pl)
```
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="fb_login_protocol_scheme">YOUR_FACEBOOK_LOGIN_PROTOCOL_SCHEME</string>
<string name ="web_client_id">YOUR_WEB_CLIENT_ID</string>
```

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

## License
