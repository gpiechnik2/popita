from django.shortcuts import render
from rest_framework import status
from rest_framework.response import Response
from rest_framework import generics, viewsets
from rest_framework.permissions import IsAuthenticatedOrReadOnly
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from .serializers import UserSerializer, UserJWTSerializer
from .models import User

import requests
import jwt

#get user profile info
class UserProfileApiView(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticatedOrReadOnly,)
    serializer_class = UserSerializer
    http_method_names = ['get', 'patch', 'head']
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

class CustomAuthToken(ObtainAuthToken):

    serializer_class = UserJWTSerializer

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

        print(response.status_code)
        if response.status_code != 200:
            return Response(status = status.HTTP_400_BAD_REQUEST)

        #decode user token
        encoded_token = jwt.decode(id_token, verify = False)

        #get user info from id_token
        email = encoded_token['email']
        first_name = encoded_token['given_name']

        #check if user exists, if not, create new with email above
        user = User.objects.filter(email = email)

        if not user:
            first_name = serializer.validated_data['given_name']

            #create random hash
            password = User.objects.make_random_password()
            user = User.objects.create(email = email, first_name = first_name, password = password)
        else:
            user = user[0]

        #return user token, if does not exist, create new before
        token, created = Token.objects.get_or_create(user = user)
        return Response({
            'token': token.key
        })
