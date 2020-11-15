from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from django.http import Http404
from django.http import Http404
from math import cos, asin, sqrt, pi

from .serializers import LocalizationSerializer
from .models import Localization

class LocalizationViewSet(viewsets.ModelViewSet):
    """
    A viewset for viewing and editing room instances.
    """

    permission_classes = [IsAuthenticated]
    serializer_class = LocalizationSerializer
    http_method_names = ['get', 'post', 'head']

    def get_queryset(self):
        user = self.request.user

        self_localization = Localization.objects.filter(user = user)

        #if user localization is not specified, raise 404
        if not self_localization:
            raise Http404

        #your coordinates
        self_latitude = self_localization[0].latitude
        self_longitude = self_localization[0].longitude

        #check if person in database is < 5km from you
        localization_response = []
        for localization in Localization.objects.filter(user != user):
            if distance(self_latitude, self_longitude, localization.latitude, localization.longitude) <= 5:
                localization_response.append(localization)
            else:
                continue

        return localization_response

    def distance(lat1, lon1, lat2, lon2):
        p = pi / 180
        a = 0.5 - cos((lat2 - lat1) * p) / 2 + cos(lat1 * p) * cos(lat2 * p) * (1 - cos((lon2 - lon1) * p)) / 2
        return 12742 * asin(sqrt(a)) #2*R*asin...
