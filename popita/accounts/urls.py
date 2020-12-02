from django.urls import path, include
from django.contrib.auth.models import User
from rest_framework.routers import DefaultRouter
from rest_framework import routers, serializers, viewsets
from .views import UserProfileApiView, CustomAuthToken

router = DefaultRouter()
router.register('profiles', UserProfileApiView, base_name = 'profiles')

urlpatterns = [
    path('', include('djoser.urls')),
    path('', include('djoser.urls.authtoken')),
    path('', include(router.urls)),
    path('jwt/token/login/', CustomAuthToken.as_view())
]
