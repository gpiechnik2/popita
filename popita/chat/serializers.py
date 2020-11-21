from accounts.models import User
from .models import Message, Room
from rest_framework import serializers
from djoser.serializers import UserSerializer

class UserInfoSerializer(UserSerializer):

    class Meta:
        model = User
        exclude = ('email', 'password', 'is_superuser', 'last_name', 'is_staff', 'date_joined', 'groups', 'user_permissions', 'last_login', 'is_active',)

class RoomSerializer(serializers.ModelSerializer):

    receivers = UserInfoSerializer(many = True, read_only = True)

    class Meta:
        model = Room
        fields = ['id', 'receivers']

    def to_representation(self, instance):
        ret = super(RoomSerializer, self).to_representation(instance)
        # check the request is list view or detail view
        is_list_view = isinstance(self.instance, list)
        room_id = instance.id

        if is_list_view:

            user = self.context['request'].user

            #add last message key
            last_message = Message.objects.filter(room = instance.id).reverse()
            if last_message:
                last_message = last_message[0]
                last_message_receiver = last_message.receiver
                last_message_timestamp =str(last_message.timestamp)
                last_message_timestamp = last_message_timestamp[11] + last_message_timestamp[12] + last_message_timestamp[13] + last_message_timestamp[14] + last_message_timestamp[15]

                if str(user) == str(last_message_receiver):
                    last_message = str(last_message)
                    last_sender = 0
                else:
                    last_message = "You: " + str(last_message)
                    last_sender = 1

            else:
                last_message = ""
                last_sender = 0

            #get receivers info, append them to new values
            if ret.get("receivers"):
                if ret.get("receivers")[0]['id'] == user.id:
                    try:
                        user_info = ret.get("receivers")[1]
                        receiver_id = user_info['id']
                        receiver_name = user_info['first_name']
                    except IndexError:
                        #self user exception
                        user_info = ret.get("receivers")[0]
                        receiver_id = user_info['id']
                        receiver_name = user_info['first_name']
                else:
                    user_info = ret.get("receivers")[0]
                    receiver_id = user_info['id']
                    receiver_name = user_info['first_name']

            #and remove them
            ret.pop("receivers")

            extra_ret = {
                "last_message" : last_message,
                "last_sender" : last_sender,
                "receiver_id" : receiver_id,
                "receiver_name" : receiver_name,
                "last_message_timestamp" : last_message_timestamp,
            }

            ret.update(extra_ret)

        return ret

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

    receiver = serializers.PrimaryKeyRelatedField(many = False, queryset = User.objects.all())
    room = RoomSerializer(read_only = True)
    timestamp = serializers.DateTimeField(format = '%Y-%m-%d %H:%m', input_formats = None, read_only = True)

    class Meta:
        model = Message
        fields = ['id', 'receiver', 'room', 'message', 'timestamp']

    def create(self, request):
        user = self.context['request'].user
        receiver = request['receiver']
        message = request['message']

        room = Room.objects.filter(receivers__in = [user]).filter(receivers__in = [receiver])

        if not room:
            room = Room.objects.create()
            room.receivers.add(user)
            room.receivers.add(receiver)

        else:
            room = room[0]

        message_obj = Message.objects.create(receiver = receiver, room = room, message = message)
        return message_obj

    def validate_receiver(self, validated_data):

        if not User.objects.filter(email = str(validated_data)):
            raise serializers.ValidationError('User must be in database.')

        #chatting with yourself exception
        user = self.context['request'].user
        if str(validated_data) == str(user):
            raise serializers.ValidationError('You can not chat with yourself.')

        return validated_data

    #change receivers
    def to_representation(self, instance):
        ret = super(MessageSerializer, self).to_representation(instance)
        # check the request is list view or detail view
        is_list_view = isinstance(self.instance, list)
        receiver = instance.receiver

        if is_list_view:

            user = self.context['request'].user

            #receiver == 1: You are receiver, receiver == 0: You are sender
            if str(user) == str(receiver):
                main_receiver = 1
            else:
                main_receiver = 0

            #remove room receivers, receiver based on room receivers and timestamp
            ret.pop("room")
            ret.pop("receiver")

            extra_ret = {
                "receiver" : main_receiver,
            }

            ret.update(extra_ret)

        return ret
