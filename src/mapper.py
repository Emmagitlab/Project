#__author__ = 'T440'


import sys
for line in sys.stdin:
    line = line.strip()
    words = line.split()
    for word in words:
        keys = word.split(",")
        keyc = "60000"
        keyt = "-1"
        name = "-1"
        trans = "-1"
        countrycode = "-1"

       # vals = keys[1]+","+keys[3]
        if len(keys[1])<10:
            keyt = keys[1]
            trans = keys[0]
          #  print(keys[1],vals)
            print('%s,%s,%s,%s,%s' % (keyt,keyc,name,trans,countrycode))
        elif keys[3] == "5":
             keyc = keys[0]
             name= keys[1]
             countrycode = keys[3]
             keyt = keyc
           # print(keys[0],vals)
             if keys[3] !="-1":
              print('%s,%s,%s,%s,%s' % (keyt,keyc,name,trans,countrycode))









