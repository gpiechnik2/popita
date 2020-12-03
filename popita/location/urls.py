from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import LocalizationViewSet

router = DefaultRouter()
router.register('', LocalizationViewSet, base_name = '')

urlpatterns = [
    path('', include(router.urls)),
    #path('', LocalizationViewSet.as_view({'get': 'list'}))

]
