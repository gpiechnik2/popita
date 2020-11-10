from django.db import models
from accounts.models import User

# Create your models here.

class Room(models.Model):
    title = models.CharField(max_length=100, blank=True, default='')
    receivers = models.ManyToManyField(User)

    def __str__(self):
        return self.title


class Message(models.Model):

    receiver = models.ForeignKey(User, on_delete = models.CASCADE)
    room = models.ForeignKey(Room, on_delete = models.CASCADE)
    message = models.CharField(max_length = 1500)
    timestamp = models.DateTimeField(auto_now_add = True)
    read_status = models.BooleanField(default = False)

    def __str__(self):
        return self.message

    class Meta:
        ordering = ('timestamp',)
