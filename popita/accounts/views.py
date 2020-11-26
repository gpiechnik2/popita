from django.shortcuts import render
from rest_framework import status
from rest_framework.response import Response
from rest_framework import generics, viewsets
from rest_framework.permissions import IsAuthenticatedOrReadOnly
from .serializers import UserSerializer
from .models import User

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
