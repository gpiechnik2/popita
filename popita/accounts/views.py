from django.shortcuts import render
from rest_framework import status
from rest_framework.response import Response
from rest_framework import viewsets, mixins, renderers
from rest_framework.permissions import IsAuthenticatedOrReadOnly, IsAuthenticated
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.decorators import action

from .serializers import UserProfileSerializer, UserGoogleJWTSerializer, UserFacebookATSerializer, UserSerializer, PasswordSerializer
from .models import User

import requests
import jwt
import json

class UserViewSet(viewsets.ViewSet):

    #register
    @action(detail = True, methods = ['post'])
    def create(self, request, *args, **kwargs):

        #check if serializer is valid
        serializer = UserSerializer(data = request.data)
        if serializer.is_valid():

            #check if user with posted email exists
            email = serializer.validated_data['email']
            users = User.objects.filter(email = email)

            #if user with given credentials exists, return status 409
            if users:
                return Response({"email": "User with given email already exists."}, status = status.HTTP_409_CONFLICT)

            #check if password and re_password are the same
            password = serializer.validated_data['password']
            re_password = serializer.validated_data['re_password']

            if password != re_password:
                return Response(status = status.HTTP_400_BAD_REQUEST)

            #create user with given data
            first_name = serializer.validated_data.get('first_name', '')
            user = User.objects.create(email = email, first_name = first_name)
            user.set_password(password)
            user.save()

            #return user token, if does not exist, create new before
            token, created = Token.objects.get_or_create(user = user)
            return Response({
                'auth_token': token.key
            })

        else:
            return Response(serializer.errors,
                            status = status.HTTP_400_BAD_REQUEST)

    #set password
    @action(detail = True, methods = ['post'], permission_classes=[IsAuthenticated])
    def set_password(self, request, *args, **kwargs):

        serializer = PasswordSerializer(data = request.data)
        if serializer.is_valid():

            #check if user is anonymous
            user = self.request.user
            if user.is_anonymous:
                return Response({"User": "Anonymous users can not change user password."},
                                status = status.HTTP_401_UNAUTHORIZED)

            #check if validated data is the same

            if not user.check_password(serializer.data.get("current_password")):
                return Response({"current_password": "Wrong password."}, status = status.HTTP_400_BAD_REQUEST)

            if serializer.data['current_password'] != serializer.data['new_password']:
                user.set_password(serializer.data['new_password'])
                user.save()
                return Response({"password": "Password has been changed."}, status = status.HTTP_200_OK)

            else:
                return Response(serializer.errors,
                                status = status.HTTP_400_BAD_REQUEST)
        else:
            return Response(serializer.errors,
                            status = status.HTTP_400_BAD_REQUEST)

#get user profile info
class UserProfileApiView(mixins.UpdateModelMixin, mixins.RetrieveModelMixin, viewsets.GenericViewSet):
    permission_classes = (IsAuthenticatedOrReadOnly,)
    serializer_class = UserProfileSerializer
    http_method_names = ['get', 'patch']
    queryset = User.objects.all()


    def partial_update(self, request, pk):

        user = self.request.user
        profile_id = self.kwargs['pk']

        if int(user.id) != int(profile_id):
            return Response(status = status.HTTP_401_UNAUTHORIZED)

        serializer = UserSerializer(request.user, data = request.data, partial = True) # set partial=True to update a data partially

        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status = status.HTTP_200_OK)

        return Response(status = status.HTTP_404_NOT_FOUND)

    #check user info
    @action(detail = False, methods = ['get'], permission_classes=[IsAuthenticated])
    def me(self, request):

        #get current user
        user = self.request.user

        #check if is anonymous
        if user.is_anonymous:
            return Response(status = status.HTTP_401_UNAUTHORIZED)

        #if not exists, return 400
        if user:
            return Response({
                'user_id': user.id,
                'first_name': user.first_name,
                'gender': user.gender,
                'background_color': user.background_color,
                'job': user.job,
                'preferred_drink': user.preferred_drink,
                'description': user.description,

            })
        else:
            return Response(status = status.HTTP_400_BAD_REQUEST)

class GoogleJwtAuthToken(ObtainAuthToken):

    serializer_class = UserGoogleJWTSerializer
    renderer_classes = [renderers.JSONRenderer]

    def post(self, request, *args, **kwargs):

        #check if serializer is valid
        serializer = self.serializer_class(data = request.data,
                                           context = {'request': request})
        serializer.is_valid(raise_exception=True)

        #check if google id_token is correct
        id_token = serializer.validated_data['id_token']

        #if not, return status 400
        url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token={}".format(id_token)
        response = requests.post(url)

        if response.status_code != 200:
            return Response(status = status.HTTP_400_BAD_REQUEST)

        #decode user token
        encoded_token = jwt.decode(id_token, options = {"verify_signature": False}, algorithms = ["HS256"])

        #get user info from id_token
        email = encoded_token['email']
        first_name = encoded_token['given_name']

        #check if user exists, if not, create new with email above
        user = User.objects.filter(email = email)

        if not user:

            #create random hash
            password = User.objects.make_random_password()
            user = User.objects.create(email = email, first_name = first_name)
            user.set_password(password)
            user.save()
        else:
            user = user[0]

        #return user token, if does not exist, create new before
        token, created = Token.objects.get_or_create(user = user)
        return Response({
            'auth_token': token.key
        })

class FacebookAccesToken(ObtainAuthToken):

    serializer_class = UserFacebookATSerializer

    def post(self, request, *args, **kwargs):

        #check if serializer is valid
        serializer = self.serializer_class(data = request.data,
                                           context = {'request': request})
        serializer.is_valid(raise_exception=True)

        #check if google id_token is correct
        access_token = serializer.validated_data['access_token']
        facebookId = serializer.validated_data['facebookId']

        #if not, return status 400
        url = "https://graph.facebook.com/{}?fields=first_name,email&access_token={}".format(facebookId, access_token)
        response = requests.post(url)

        if response.status_code != 200:
            return Response(status = status.HTTP_400_BAD_REQUEST)

        #decode response
        json_data = json.loads(response.text)

        #get user info from id_token
        first_name = json_data['first_name']
        email = json_data['email']

        #check if user exists, if not, create new with email above
        user = User.objects.filter(email = email)

        if not user:

            #create random hash
            password = User.objects.make_random_password()
            user = User.objects.create(email = email, first_name = first_name)
            user.set_password(password)
            user.save()

        else:
            user = user[0]

        #return user token, if does not exist, create new before
        token, created = Token.objects.get_or_create(user = user)
        return Response({
            'auth_token': token.key
        })
