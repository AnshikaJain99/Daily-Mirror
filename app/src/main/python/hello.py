import json
import text2emotion as te

def helloWorld(text):
    return (str(te.get_emotion(text)))