from accounts.models import User
from .models import Message, Room
from rest_framework import serializers
from djoser.serializers import UserSerializer

class UserInfoSerializer(UserSerializer):

    class Meta:
        model = User
        exclude = ('email', 'password', 'is_superuser', 'last_name', 'is_staff', 'date_joined', 'groups', 'user_permissions', 'last_login', 'is_active',)

class RoomSerializer(serializers.ModelSerializer):

    receivers = UserInfoSerializer(many = True)

    class Meta:
        model = Room
        fields = ['id', 'receivers']

    def validate_receivers(self, validated_data):

        user = self.context['request'].user

        if not user:
            return serializers.ValidationError('User must be in database.')

        for info in validated_data:
            if user.first_name in str(info['first_name']):
                return validated_data
            else:
                continue

        return serializers.ValidationError('Current user not found in receivers')

class MessageSerializer(serializers.ModelSerializer):

    sender = serializers.PrimaryKeyRelatedField(many = False, queryset = User.objects.all())
    room = RoomSerializer(read_only = True)

    class Meta:
        model = Message
        fields = ['id', 'sender', 'room', 'message', 'timestamp']

    def create(self, request):
        user = self.context['request'].user
        sender = request['sender']
        message = request['message']
        room = Room.objects.filter(receivers__in = [user]).filter(receivers__in = [sender])

        if not room:
            room = Room.objects.create(receivers = [user, sender])[0]
        else:
            room = room[0]

        message_obj = Message.objects.create(sender = sender, room = room, message = message)
        return message_obj

    def validate_sender(self, validated_data):

        if not User.objects.filter(email = str(validated_data)):
            raise serializers.ValidationError('User must be in database.')

        return validated_data
