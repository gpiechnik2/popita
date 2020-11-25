from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import filters
from django_filters.rest_framework import DjangoFilterBackend
from django.http import Http404

from .models import Message, Room
from .serializers import MessageSerializer, RoomSerializer

class RoomViewSet(viewsets.ModelViewSet):
    """
    A viewset for viewing and editing room instances.
    """

    permission_classes = [IsAuthenticated]
    serializer_class = RoomSerializer
    http_method_names = ['get', 'head']

    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['receivers']

    def get_queryset(self):
        user = self.request.user
        return Room.objects.filter(receivers__in = [user])

class MessageViewSet(viewsets.ModelViewSet):
    """
    A viewset for viewing messages.
    """

    permission_classes = [IsAuthenticated]
    serializer_class = MessageSerializer
    http_method_names = ['get', 'post', 'head']

    def get_queryset(self):
        user = self.request.user
        room_id = self.kwargs['rooms_pk']

        if not Room.objects.filter(receivers__in = [user], pk = room_id):
            raise Http404

        return Message.objects.filter(room = room_id).order_by('timestamp')
