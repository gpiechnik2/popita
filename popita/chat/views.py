from django.http.response import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from rest_framework.parsers import JSONParser
from accounts.models import User
from accounts.serializers import UserSerializer

from .models import Message
from .serializers import MessageSerializer, MessagesSerializer

def conversations(request, sender = None):
    """
    API endpoint that allows conversations to be viewed or edited.
    """

    if request.method == 'GET':
        messages = Messages.objects.filter(sender = sender)
        serializer = MessagesSerializer()

        return JsonResponse(serializer.data, safe = False)

    elif request.method == 'POST':

        return JsonResponse(serializer.data, safe = False)

@csrf_exempt
def message_list(request, sender = None, receiver = None):
    """
    API endpoint that allows messages to be viewed or edited.
    """

    if request.method == 'GET':
        messages = Message.objects.filter(sender_id = sender, receiver_id = receiver)
        serializer = MessageSerializer(messages, many = True, context = {'request' : request})
        return JsonResponse(serializer.data, safe = False)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        serializer = MessageSerializer(data = data)
        if serializer.is_valid():
            serializer.save()
            return JsonResponse(serializer.data, status = 201)
        return JsonResponse(serializer.errors, status = 400)
