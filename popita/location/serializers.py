from rest_framework import serializers
from djoser.serializers import UserSerializer
from math import cos, asin, sqrt, pi

from accounts.models import User
from .models import Localization

class UserInfoSerializer(UserSerializer):

    class Meta:
        model = User
        exclude = ('email', 'password', 'is_superuser', 'last_name', 'is_staff', 'date_joined', 'groups', 'user_permissions', 'last_login', 'is_active', 'gender', 'background_color', 'job', 'preferred_drink', 'description')

class LocalizationSerializer(serializers.ModelSerializer):

    user = UserInfoSerializer(many = False, read_only = True)
    timestamp = serializers.DateTimeField(format = '%Y-%m-%d %H:%m', input_formats = None, read_only = True)
    location = serializers.CharField(required = True)

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

        if is_list_view:

            #user coordinates
            latitude = instance.latitude
            longitude = instance.longitude

            #your coordinates
            user = self.context['request'].user
            self_localization = Localization.objects.filter(user = user)[0]
            self_latitude = self_localization.latitude
            self_longitude = self_localization.longitude

            #check distance
            p = pi / 180
            a = 0.5 - cos((self_latitude - latitude) * p) / 2 + cos(latitude * p) * cos(self_latitude * p) * (1 - cos((self_longitude - longitude) * p)) / 2
            distance = 12742 * asin(sqrt(a)) #2*R*asin...

            extra_ret = {
                "distance" : distance,
            }

            ret.update(extra_ret)

        return ret
