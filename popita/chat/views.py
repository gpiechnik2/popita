from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from django.http import Http404

from .models import Message, Room
from .serializers import MessageSerializer, RoomSerializer

class RoomViewSet(viewsets.ModelViewSet):
    """
    A viewset for viewing and editing room instances.
    """

    permission_classes = [IsAuthenticated]
    serializer_class = RoomSerializer

    def get_queryset(self):
        user = self.request.user
        return Room.objects.filter(receivers__in = [user])

class MessageViewSet(viewsets.ModelViewSet):
    """
    A viewset for viewing and editing message instances.
    """

    permission_classes = [IsAuthenticated]
    serializer_class = MessageSerializer

    def get_queryset(self):
        user = self.request.user
        room_id = self.kwargs['rooms_pk']

        if not Room.objects.filter(receivers__in = [user], pk = room_id):
            raise Http404

        return Message.objects.filter(room = room_id)
