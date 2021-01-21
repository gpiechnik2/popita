
![Image of Popita](https://github.com/gpiechnik2/popita/blob/master/static/Popita_app.jpg)

# Popita
Popita is an android mobile app that is completly based on rest API. The app gives users the ability to view and send messages to people nearby who are currently drinking.

## Features
- Login and account registration.
- Authentication via provider(Google or Facebook).
- Sending, receiving, and filtering messages from other users.
- Setting and getting the users locations in real time.
- Displaying recently active users within 5km(8 miles).
- Editing a user profile and credentials.
- Viewing users profiles.

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
```kotlin
<string name="facebook_app_id">YOUR_FACEBOOK_APP_ID</string>
<string name="fb_login_protocol_scheme">YOUR_FACEBOOK_LOGIN_PROTOCOL_SCHEME</string>
<string name ="web_client_id">YOUR_WEB_CLIENT_ID</string>
<string name="maps_api_key">YOUR_MAPS_API_KEY</string>
<string name="server_ip">YOUR_SERVER_IP</string>
```

## Setup

**Install dependencies**
```console
pip install requirements.txt
```

**Migrate database**
```console
python3 manage.py migrate
```

**Run server**
```console
python3 manage.py
```

## Documentation
For detailed rest API documentation, please go [here](https://github.com/gpiechnik2/popita/blob/master/API.md).

## External resources
In project has been used resources from [Contra](https://contrauikit.com/?ref=uistoredesign?ref=uistore.design) and [Open peeps](https://openpeeps.com/).

## License
Copyright 2020 Grzegorz Piechnik

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
