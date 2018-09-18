from models.client import Client
from models.message import Message

clients = [Client(i) for i in range(800)]

for client in clients:
    client.links += [client]
    for _client in clients:
        if client != _client:
            client.links.append(_client)

sender = 0

message = Message()
message.content = "Hello from Client 1"
message.sender = clients[sender].uid
clients[sender].broadcast(message)

for client in clients:
    print(f"Client {client.uid} has {len(client.messages)} messages stored")
