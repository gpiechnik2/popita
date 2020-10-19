from django.db import models

# Create your models here.
class Messages(models.Model):
    receiver = models.ForeignKey(User, on_delete = models.CASCADE)
    messages = models.ManyToManyField(Message)

    def __str__(self):
        return self.receiver

    class Meta:
        ordering = ('receiver')

class Message(models.Model):

    sender = models.ForeignKey(User, on_delete = models.CASCADE)
    message = models.CharField(max_length = 1500)
    timestamp = models.DateTimeField(auto_now_add = True)
    read_status = models.BooleanFeild(default = False)

    def __str__(self):
        return self.message

    class Meta:
        ordering = ('timestamp')
