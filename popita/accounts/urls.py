from django.urls import path, include
from django.contrib.auth.models import User
from rest_framework.routers import DefaultRouter, SimpleRouter
from rest_framework import routers, serializers, viewsets
from .views import UserViewSet, UserProfileApiView, GoogleJwtAuthToken, FacebookAccesToken

router = SimpleRouter()
router.register('profiles', UserProfileApiView, base_name = 'profiles')

urlpatterns = [
    path('users/', UserViewSet.as_view({
        'post': 'create'
    })),
    path('users/set_password/', UserViewSet.as_view({
        'post': 'set_password'
    })),
    path('', include('djoser.urls.authtoken')),
    path('', include(router.urls)),
    path('google/token/login/', GoogleJwtAuthToken.as_view()),
    path('facebook/token/login/', FacebookAccesToken.as_view())
]
