from rest_framework import serializers
from djoser.serializers import UserSerializer

from accounts.models import User
from .models import Localization

class UserInfoSerializer(UserSerializer):

    class Meta:
        model = User
        exclude = ('email', 'password', 'is_superuser', 'last_name', 'is_staff', 'date_joined', 'groups', 'user_permissions', 'last_login', 'is_active',)

class LocalizationSerializer(serializers.ModelSerializer):

    user = UserInfoSerializer(many = False, read_only = True)
    timestamp = serializers.DateTimeField(format = '%Y-%m-%d %H:%m', input_formats = None, read_only = True)

    class Meta:
        model = Localization
        fields = ['id', 'user', 'longitude', 'latitude', 'attitude', 'location', 'timestamp']

    def create(self, request):

        user = self.context['request'].user
        longitude = request['longitude']
        latitude = request['latitude']
        attitude = request['attitude']
        location = request['location']

        last_localization = Localization.objects.filter(user = user)

        if not last_localization:

            current_localization = Localization.objects.create(
                user = user,
                longitude = longitude,
                latitude = latitude,
                attitude = attitude,
                location = location
            )

        else:

            current_localization = last_localization[0]
            current_localization.longitude = longitude
            current_localization.latitude = latitude
            current_localization.attitude = attitude
            current_localization.location = location
            current_localization.save()

        return current_localization

    def validate_user(self, validated_data):

        if not User.objects.filter(email = str(validated_data)):
            raise serializers.ValidationError('User must be in database.')

        return validated_data

    #change receivers
    def to_representation(self, instance):
        ret = super(LocalizationSerializer, self).to_representation(instance)
        # check the request is list view or detail view
        is_list_view = isinstance(self.instance, list)

        #your coordinates
        self_latitude = instance.latitude
        self_longitude = instance.longitude

        
        if is_list_view:

            #user = self.context['request'].user
            distance = 0


            extra_ret = {
                "distance" : distance,
            }

            ret.update(extra_ret)

        return ret
