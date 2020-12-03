from djoser.serializers import UserCreateSerializer, UserSerializer
from rest_framework import serializers
from .models import *

class UserGoogleJWTSerializer(serializers.Serializer):
    id_token = serializers.CharField()

class UserFacebookATSerializer(serializers.Serializer):
    access_token = serializers.CharField()
    facebookId = serializers.CharField()

class UserCreateSerializer(UserCreateSerializer):
    class Meta(UserCreateSerializer.Meta):
        model = User
        fields = ('id', 'email', 'first_name', 'password')

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'first_name', 'gender', 'background_color', 'job', 'preferred_drink', 'description')

    def update(self, instance, validated_data):

        instance.first_name = validated_data.get('first_name')
        instance.gender = validated_data.get('gender')
        instance.background_color = validated_data.get('background_color')
        instance.job = validated_data.get('job')
        instance.preferred_drink = validated_data.get('preferred_drink')
        instance.description = validated_data.get('description')

        instance.save()

        return instance

    def validate_first_name(self, validated_data):
        if validated_data is None:
            raise serializers.ValidationError('Name can not be null.')
        return validated_data

    def validate_gender(self, validated_data):
        if validated_data is None:
            raise serializers.ValidationError('Gender can not be null.')
        return validated_data

    def validate_background_color(self, validated_data):
        if validated_data is None:
            raise serializers.ValidationError('Background color can not be null.')
        return validated_data

    def validate_job(self, validated_data):
        if validated_data is None:
            raise serializers.ValidationError('Job can not be null.')
        return validated_data

    def validate_preferred_drink(self, validated_data):
        if validated_data is None:
            raise serializers.ValidationError('Preferred drink can not be null.')
        return validated_data

    def validate_description(self, validated_data):
        if validated_data is None:
            raise serializers.ValidationError('Description can not be null.')
        return validated_data
