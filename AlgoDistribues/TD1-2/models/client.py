class Client:
    def __init__(self, uid):
        self.uid = uid
        self.links = []
        self.messages = []

    def broadcast(self, message, depth=0):
        send_idx = self.uid + (2 ** depth)

        if send_idx < len(self.links):
            self.links[send_idx].recv(message)
            self.links[send_idx].broadcast(message, depth + 1)

            self.broadcast(message, depth + 1)

    def recv(self, message):
        self.messages += [message]
