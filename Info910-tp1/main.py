from ktcrypt.utils import *
from ktcrypt.vigenere import vigenere
import pprint


f = open('resources/baudelaire-c1.txt')

data = sanitize(f.read())
encrypted = vigenere(data, 'AZERTY')

pprint.pprint(detect_vigenere(encrypted))

