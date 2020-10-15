from django.db import models
from django.contrib.auth import get_user_model

User = get_user_model()

# Create your models here.

class Message(models.Model):
    author = models.ForeignKey(User, on_delete = models.CASCADE)
    content = models.models.CharField(max_length = 500)
    timestamp = models.DatetimeField(default = datetime.now)
    status = models.BooleanField(default = False)

    #PK
    def __str__(self):
        return self.author.email

class Messages(models.Model):
    participant = models.ForeignKey(User, on_delete = models.CASCADE, blank = True)
    messages = models.ManyToManyField(Message, blank = True)

    def __str__(self):
        return "{}".format(self.pk)
