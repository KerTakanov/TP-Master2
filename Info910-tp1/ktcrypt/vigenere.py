def vigenere(raw, key):
    key_index = 0
    encrypted_data = ""

    for c in raw:
        c_ord = ord(c) - ord('a')
        k_ord = ord(key[key_index]) - ord('A')

        e_ord = c_ord + k_ord

        encrypted_data += chr((e_ord % 26) + ord('A'))

        key_index = (key_index + 1) % len(key)

    return encrypted_data
