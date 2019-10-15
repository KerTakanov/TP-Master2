from flask import Flask, request, Response
from random import randint
from copy import copy

app = Flask(__name__)

cities = ['Paris', 'NY', 'Kyoto', 'Casablanca']
cities_in_play = []
curr_city = None


@app.route("/next")
def get_next():
    global cities, cities_in_play, curr_city
    if not cities_in_play:
        cities_in_play = copy(cities)

    rnd = randint(0, len(cities_in_play) - 1)
    curr_city = cities_in_play.pop(rnd)

    return curr_city


@app.route("/answer", methods=['POST'])
def answer():
    global curr_city
    if curr_city is None:
        return Response(response="None", status=204)
    if request.get_json()['city'] == curr_city:
        curr_city = None
        return Response(response="Yep", status=200)
    return Response(response="Nope", status=400)


if __name__ == '__main__':
    app.run()
