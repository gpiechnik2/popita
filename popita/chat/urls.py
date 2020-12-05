from django.urls import path, include
from .views import RoomViewSet, MessageViewSet, MessagePostViewSet
from rest_framework.routers import SimpleRouter
from rest_framework_nested import routers

router = SimpleRouter()
router.register('rooms', RoomViewSet, basename = 'rooms')

message_router = routers.NestedSimpleRouter(router, 'rooms', lookup = 'rooms')
message_router.register('messages', MessageViewSet, basename = 'room-messages')

urlpatterns = [
    path('', include(router.urls)),
    path('', include(message_router.urls)),
    path('message/new/', MessagePostViewSet.as_view({
        'post': 'create'
    })),
]
