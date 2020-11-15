from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import LocalizationViewSet

router = DefaultRouter()
router.register('localization', LocalizationViewSet, base_name = 'localization')

#router = routers.SimpleRouter(trailing_slash =F alse)
#router.register(r'localization', viewset = LocalizationViewSet, base_name = 'localization')

urlpatterns = [
    path('', include(router.urls)),
]
