from accounts.models import User
from .models import Message, Messages
from rest_framework import serializers

class MessagesSerializer(serializers.ModelSerializer):

    receiver = serializers.SlugRelatedField(many = False, slug_field = 'email', queryset = User.objects.all())

    class Meta:
        model = Messages
        fields = ['receiver', 'messages']

class MessageSerializer(serializers.ModelSerializer):

    sender = serializers.SlugRelatedField(many = False, slug_field = 'email', queryset = User.objects.all())

    class Meta:
        model = Message
        fields = ['sender', 'message', 'timestamp']
