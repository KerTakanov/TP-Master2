import unidecode


def sanitize(text: str):
    return unidecode.unidecode(text.lower().replace('\n', '').replace('.', '').replace(' ', ''))


def freq(text: str):
    alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    freqs = {}

    for letter in alphabet:
        freqs[letter] = text.count(letter) / len(text)

    return freqs


def print_freq(text: str):
    import pprint
    pprint.pprint(freq(text))


def coincidence_index(text, shift=-1, step=-1, mutual=False, freq_locale=None):
    if shift != -1 and step != -1:
        text = subtext(text, shift, step)

    freq_text = freq(text)

    coincidence = 0

    for letter in freq_text:
        coincidence += freq_text[letter] * freq_text[letter] if mutual else freq_locale[letter] * freq_text[letter]

    return coincidence


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
    return text[shift:-1:step]


def coincidence_is_french_or_mono(coincidence):
    return 0.058 < coincidence < 0.098


def detect_vigenere(encrypted):
    for i in range(1, int(len(encrypted)/10) + 1):
        nb_fr = 0

        for j in range(i):
            if coincidence_is_french_or_mono(coincidence_index(subtext(encrypted, j, i))):
                nb_fr += 1

        if nb_fr == i:
            key = ""

            for j in range(i):
                mutual_coincidences = [coincidence_index()]

    return None
