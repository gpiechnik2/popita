from django.db import models
from accounts.models import User

# Create your modelsclass Room(models.Model):
class Localization(models.Model):

    user = models.ForeignKey(User, on_delete = models.CASCADE)
    longitude = models.FloatField()
    latitude = models.FloatField()
    attitude = models.FloatField()
    location = models.CharField(max_length = 300, blank = True, default = 'Unknown')
    timestamp = models.DateTimeField(auto_now_add = True)

    def __str__(self):
        return self.location
