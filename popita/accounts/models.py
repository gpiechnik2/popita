from django.db import models
from django.contrib.auth.models import AbstractUser

class User(AbstractUser):
    email = models.EmailField(verbose_name = 'email', max_length = 255, unique = True)
    REQUIRED_FIELDS = ['username', 'first_name']
    USERNAME_FIELD = 'email'

    def get_username(self):
        return self.email
