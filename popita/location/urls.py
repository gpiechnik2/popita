from django.urls import path, include
from rest_framework.routers import SimpleRouter
from .views import LocalizationViewSet

#router = SimpleRouter()
#router.register('localizations', LocalizationViewSet, base_name = 'localizations')

urlpatterns = [
    #path('', include(router.urls)),
    path('', LocalizationViewSet.as_view({'get': 'list'}))

]
