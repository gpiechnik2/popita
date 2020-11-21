from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
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
        localizations = Localization.objects.all()

        localization_response = []
        for localization in localizations:

            p = pi / 180
            a = 0.5 - cos((localization.latitude - self_latitude) * p) / 2 + cos(self_latitude * p) * cos(localization.latitude * p) * (1 - cos((localization.longitude - self_longitude) * p)) / 2
            distance = 12742 * asin(sqrt(a))

            if distance <= 5:
                if localization.user.id != user.id:
                    localization_response.append(localization)
                else:
                    continue
            else:
                continue

        return localization_response
