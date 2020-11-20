from django.db import models
from django.contrib.auth.models import AbstractUser
from .managers import CustomUserManager

class User(AbstractUser):

    GENDER_CHOICES = (
        ('Male', 'Male'),
        ('Female', 'Female'),
    )

    BACKGROUND_CHOICES = (
        ('Orange', 'Orange'),
        ('Blue', 'Blue'),
        ('Pink', 'Pink'),
        ('Yellow', 'Yellow'),
        ('Green', 'Green'),
    )

    username = None
    username_validator = None
    email = models.EmailField(verbose_name = 'email', max_length = 255, unique = True)
    gender = models.CharField(max_length = 6, choices = GENDER_CHOICES, default = 'M')
    background_color = models.CharField(max_length = 6, choices = BACKGROUND_CHOICES, default = 'Orange')
    job = models.CharField(max_length = 26, default = 'N/A')
    preferred_drink = models.CharField(max_length = 30, default = 'N/A')
    description = models.CharField(max_length = 300, default = 'N/A')

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['first_name']

    objects = CustomUserManager()

    ACCOUNT_USER_MODEL_USERNAME_FIELD = None
    ACCOUNT_EMAIL_REQUIRED = True
    ACCOUNT_UNIQUE_EMAIL = True
    ACCOUNT_USERNAME_REQUIRED = False
    ACCOUNT_AUTHENTICATION_METHOD = 'email'
    ACCOUNT_EMAIL_VERIFICATION = 'mandatory'
    ACCOUNT_CONFIRM_EMAIL_ON_GET = True
    #ACCOUNT_EMAIL_CONFIRMATION_ANONYMOUS_REDIRECT_URL = '/?verification=1'
    #ACCOUNT_EMAIL_CONFIRMATION_AUTHENTICATED_REDIRECT_URL = '/?verification=1'

    def get_username(self):
        return self.email
