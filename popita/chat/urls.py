from django.urls import path
from .views import message_list

urlpatterns = [
    # URL form : "/api/messages/1/2"
    path('api/messages/<int:sender>/<int:receiver>', message_list, name='message-detail'),  #GET
    # URL form : "/api/messages/"
    path('api/messages/', message_list, name='message-list'),   #POST
]
