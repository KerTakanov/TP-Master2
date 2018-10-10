import unidecode
import operator


def sanitize(text: str):
    return unidecode.unidecode(text.lower().replace('\n', '').replace('.', '').replace(' ', ''))


def freq(text: str):
    alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    freqs = {}

    for letter in alphabet:
        freqs[letter] = text.count(letter) / len(text)

    return freqs


def apparitions(text: str):
    dico_apparitions = {}

    for letter in 'ABCDEFGHIJKLMNOPQRSTUVWXYZ':
        dico_apparitions[letter] = text.count(letter)

    return dico_apparitions


def coincidence_index(freqs):
    ic = 0

    for letter in 'ABCDEFGHIJKLMNOPQRSTUVWXYZ':
        ic += freqs[letter] ** 2

    return ic


def mutual_coincidence_index(freqs_txt, freqs_locale, shift):
    def shifted_letter(letter, shift):
        l_ord = ord(letter) - ord('A')
        l_ord += shift
        return chr((l_ord % 26) + ord('A'))

    ic = 0

    for letter in 'ABCDEFGHIJKLMNOPQRSTUVWXYZ':
        ic += freqs_txt[shifted_letter(letter, shift)] * freqs_locale[letter]

    return ic


def locale_freq_fr():
    return {
        'E': .173,
        'A': .084,
        'S': .081,
        'I': .073,
        'N': .071,
        'T': .071,
        'R': .066,
        'L': .06,
        'U': .057,
        'O': .053,
        'D': .042,
        'C': .03,
        'M': .03,
        'P': .03,
        'G': .013,
        'V': .013,
        'B': .011,
        'F': .011,
        'Q': .01,
        'H': .009,
        'X': .004,
        'J': .003,
        'Y': .003,
        'K': .0001,
        'W': .0001,
        'Z': .0001
    }


def subtext(text, shift, step):
    return text[shift:-1:step if step else 1]


def coincidence_is_french_or_mono(coincidence):
    return 0.058 < coincidence < 0.098


def detect_vigenere(encrypted):
    key = ""

    freq_fr = locale_freq_fr()
    freq_txt = freq(encrypted)

    ic_fr = coincidence_index(locale_freq_fr())
    ic_txt = coincidence_index(freq_txt)

    print(ic_fr)

    for n in range(1, int(len(encrypted) / 10)):
        nb_fr = 0

        for shift in range(n - 1):
            freqs = freq(subtext(encrypted, n, shift))
            ic = coincidence_index(freqs)

            if coincidence_is_french_or_mono(ic):
                nb_fr += 1

        print(nb_fr, n)

        if nb_fr == n:
            freqs = freq(encrypted)

            for j in range(n):
                mutual_coincidence = {}
                for c in 'ABCDEFGHIJKLMNOPQRSTUVWXYZ':
                    mutual_coincidence[c] = freqs[c] * locale_freq_fr()[c]

                key += max(mutual_coincidence.items(), key=operator.itemgetter(1))[0]

            return key


    """
    for i in range(1, int(len(encrypted)/10) + 1):
        nb_fr = 0

        for j in range(i):
            ic = coincidence_index(subtext(encrypted, j, i))

            if coincidence_is_french_or_mono(ic):
                nb_fr += 1

        if nb_fr == i:
            freqs = freq(encrypted)

            for j in range(i):
                mutual_coincidence = {}
                for c in 'ABCDEFGHIJKLMNOPQRSTUVWXYZ':
                    mutual_coincidence[c] = freqs[c] * locale_freq_fr()[c]

                key += max(mutual_coincidence.items(), key=operator.itemgetter(1))[0]

    return key"""
