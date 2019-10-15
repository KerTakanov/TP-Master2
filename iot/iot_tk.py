import bluetooth
from tkinter import *
from random import randint
from copy import copy
from PIL import Image, ImageTk
from threading import Thread
from queue import Queue
import cv2
import time
#http://flagpedia.net


class IOT:
    def menuPage(self):
        if self.points > 0:
            self.label_pts['text'] = "Félicitations ! Tu as finis le mode jeu avec " + self.label_pts['text']
            self.label_pts.pack()
        else:
            self.label_pts.pack_forget()

        self.label.pack_forget()
        self.btn_sign.pack_forget()
        self.flag_canvas.pack_forget()
        self.btn_game.pack()
        self.btn_learn.pack()
        self.result_canvas.pack_forget()

        self.mode = 'menu'

    def pageGame(self):
        self.btn_game.pack_forget()
        self.label_country.pack_forget()

        self.btn_learn.pack()
        self.label.pack()
        self.label_pts.pack()
        self.btn_sign.pack()
        self.flag_canvas.pack(padx=5, pady=5, side=LEFT)
        self.result_canvas.pack(padx=5, pady=5)

        self.mode = 'game'
        self.result(-1)
        self.points = 0
        self.answers = 0
        self.calc_points()

    def pageLearn(self):
        self.label_pts.pack_forget()
        self.label.pack_forget()
        self.flag_canvas.pack_forget()
        self.btn_learn.pack_forget()
        self.result_canvas.pack_forget()

        self.btn_sign.pack()
        self.label_country.pack()
        self.flag_canvas.pack(padx=5, pady=5, side=LEFT)
        self.btn_game.pack()

        self.curr_city = None
        self.update_flag()

        self.mode = 'learn'

    def __init__(self):
        self.cities = ['France', 'Afrique du Sud', 'Russie', 'Inde', 'Australie', 'Bresil', 'Canada']
        self.cities_in_play = []
        self.curr_city = None
        self.mode = 'menu' # game | learn
        self.points = 0
        self.answers = 0

        self.fenetre = Tk()
        self.fenetre.attributes("-fullscreen", True)

        # menu
        self.btn_game = Button(self.fenetre, text="Jeu", command=self.pageGame)
        self.btn_learn = Button(self.fenetre, text="Apprentissage", command=self.pageLearn)

        # page jeu
        self.label = Label(self.fenetre, text="Trouver " + self.get_next())
        self.label_country = Label(self.fenetre, text='')
        self.label_pts = Label(self.fenetre, text="0 points")
        self.result_canvas = Canvas(self.fenetre, width=256, height=256)

        self.btn_sign = Button(self.fenetre, text="Langage des signes", command=lambda: self.show_video('trouver' if not self.curr_city else self.curr_city))
        
        self.flag_canvas = Canvas(self.fenetre, width=580, height=387)
        self.update_flag()

        self.menuPage()

        self.queue = Queue()
        print(id(self.queue))

        Thread(target=self.receiveMessages, args=(self.queue,)).start()
        self.fenetre.after(200, self.onMsgReceived)

    def calc_points(self):
        self.label_pts['text'] = str(int(self.points ** 1.35)) + " points"

    def run(self):
        self.fenetre.mainloop()

    def get_next(self):
        if not self.cities_in_play:
            self.cities_in_play = copy(self.cities)

        rnd = randint(0, len(self.cities_in_play) - 1)
        self.curr_city = self.cities_in_play.pop(rnd)

        return self.curr_city

    def answer(self, city):
        print('cities')
        print(city)
        print(self.curr_city)
        if self.curr_city is None:
            return False
        if city == self.curr_city:
            self.curr_city = None
            self.result(1)
            self.points += 1
            self.answers += 1
            self.calc_points()
            return True
        self.points *= 0.8
        self.result(0)
        self.calc_points()
        return False

    # result should be either 1 (success), 0 (failure) -1 (None)
    def result(self, result):
        image = Image.open('happy.bmp' if result == 1 else 'sad.bmp' if result == 0 else 'f_None.bmp')
        self.photo_result = ImageTk.PhotoImage(image)
        self.result_canvas.create_image(self.photo_result.width()/2, self.photo_result.height()/2, image=self.photo_result)

    def update_flag(self):
        self.label_country['text'] = "Ce pays se nomme '" + self.curr_city + "'" if self.curr_city is not None else 'Choisissez un pays avec la télécommande'
        image = Image.open('f_' + (self.curr_city if self.curr_city is not None else 'None') + '.bmp')
        self.photo = ImageTk.PhotoImage(image)
        self.flag_canvas.create_image((self.photo.width()/2, self.photo.height()/2), image=self.photo)

    def show_video(self, name):
        cap = cv2.VideoCapture('s_' + name + '.mp4')

        alive = True
        while alive:
            ret, frame = cap.read()

            if ret:
                gray = cv2.cvtColor(frame, cv2.COLOR_RGBA2RGB)

                cv2.imshow('frame', gray)
                if cv2.waitKey(25) & 0xFF == ord('q'):
                    break
            else:
                alive = False

        cap.release()
        cv2.destroyAllWindows()

    def check_answers_count(self):
        if self.answers > 7:
            self.menuPage()

    def next(self, country):
        if self.mode == 'game':
            if self.answer(country):
                city = self.get_next()
                self.update_flag()
                self.label['text'] = "Trouver " + city
                self.check_answers_count()
            else:
                self.label['text'] = "Raté ! Essaie encore"
        elif self.mode == 'learn':
            self.curr_city = country
            self.update_flag()

    def onMsgReceived(self):
        while not self.queue.empty():
            msg = str(self.queue.get(True))
            print('rcv ')
            print(msg)
            self.next(msg)
        self.fenetre.after(200, self.onMsgReceived)

    def receiveMessages(self, queue):
        """while True:
            server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
            port = 0
            print('Trying to connect')
            server_sock.bind(("", port))
            server_sock.listen(1)
            client_sock, address = server_sock.accept()
            print("Accepted connection from " + str(address))
            print("Waiting data")
            data = client_sock.recv(1024).decode()
            queue.put(data)
            print("received [%s]" % data)"""
        while True:
            time.sleep(4)
            print('Msg put')
            queue.put(self.cities[randint(0, 6)])


IOT().run()
