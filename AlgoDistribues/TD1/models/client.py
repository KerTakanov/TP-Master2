import models.message as msg


class Client:
    def __init__(self, uid):
        self.uid = uid
        self.links = []
        self.messages = []

    def broadcast(self, message: msg.Message):
        message.broadcast = True

        filter_links = [link for link in self.links if link.uid not in message.sent_to]
        message.sent_to += [link.uid for link in self.links]

        for link in filter_links:
            link.recv(message)

    def recv(self, message):
        self.messages += [message]
        self.broadcast(message)
