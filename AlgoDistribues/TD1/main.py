from models.client import Client
from models.message import Message

clients = [Client(i) for i in range(6)]

for client in clients:
    for _client in clients:
        if client != _client:
            client.links.append(_client)

message = Message()
message.content = "Hello from Client 1"

clients[0].broadcast(message)

for client in clients:
    print(f"Client {client.uid} has {len(client.messages)} messages stored")
