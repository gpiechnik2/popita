from django.urls import path, include
from .views import RoomViewSet, MessageViewSet
from rest_framework.routers import SimpleRouter
from rest_framework_nested import routers

router = SimpleRouter()
router.register('rooms', RoomViewSet, base_name = 'rooms')

message_router = routers.NestedSimpleRouter(router, 'rooms', lookup = 'rooms')
message_router.register('messages', MessageViewSet, base_name='room-messages')


urlpatterns = [
    path('', include(router.urls)),
    path('', include(message_router.urls)),
]
