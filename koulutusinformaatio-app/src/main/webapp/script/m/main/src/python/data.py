import json
import inspect
import requests
import random
import string
import sys

if len(sys.argv) > 1:
    base_url = sys.argv[1]
else:
    base_url = "https://opintopolku.fi"

session = requests.Session()

def _(item, dn_key):
    for k in dn_key.split("."):
        if k == "" or not isinstance(item, dict) or k not in item:
            return None
        item = item[k]
    return item

def sym(length=10):
   return ''.join(random.choice(string.lowercase) for i in range(length))

def dotted_getter(dn_key):
    def g(obj):
        return _(obj, dn_key)
    return g

def s(template, **kwargs):
    "Usage: s(string, **locals())"
    if not kwargs:
        frame = inspect.currentframe()
        try:
            kwargs = frame.f_back.f_locals
        finally:
            del frame
        if not kwargs:
            kwargs = globals()

    return template.format(**kwargs)

def get(url):
    r = session.get(base_url + url)
    print base_url + url + ": " + str(r.status_code) + " " + r.reason
    if r.status_code == requests.codes.ok:
        return r.json()
    else:
        return {}

def out(data):
    print json.dumps(data, indent=4)

def transformlostoproviders(los):
    tempdict = {}
    for lo in los:
        id = lo["provider"]["id"]
        if not id in tempdict:
            tempdict[id] = lo["provider"]
            tempdict[id]["los"] = []
            tempdict[id]["los"].append(lo)
        else:
            tempdict[id]["los"].append(lo)
    return tempdict.values()

#def uniq(l, key):
#    filter(sorted(l, key=key), key=key)
