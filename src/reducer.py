#__author__ = 'T440'

import sys

# maps words to their counts
foundKey = ""
foundValue = ""
isPrint = 0
currentCount = 0
currentName = "-1"
currentCustID = "-1"


# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()
    try:
        # parse the input we got from mapper.py
        keyt,keyc,name,trans,countrycode = line.split(',')

        #the first line should be a mapping line, otherwise we need to set the currentCountryName to not known
        #if keyt == "-1":#get name + keyc

        #if isPrint ==1 and currentCount != 0:
          #  isPrint = 0
           # print(key,currentCustID,name,key,currentCount)

        if trans == "-1":
            if currentCount!=0:
                print(key,currentCount)
            currentName = name
            currentCustID = keyc
            # print ("name",name,keyc)
            currentCount = 0

        if trans !="-1" :
            if keyt == currentCustID:
               currentCount+=1
               name = currentName
               # print ("currentCount",currentCount)

        key = '%s,%s' % (keyt,name)

    except:
        pass


        # if keyc == "-1": #count tranid
        #print("currentKey"+":"+currentKey)
        # key = '%s\t%s' % (currentCustID,name)

    #     if trans != "-1":
    #         if foundKey != currentCustID:
    #             if isFirst == 0:
    #                 print('%s,%s' % ("", ""))
    #                 currentCount = 0
    #             else:
    #                 isFirst = 0
    #             foundKey = currentCustID
    #         currentCount = 1 + currentCount  # we increment anything not in the map list

# try:
#     print ('%s\t%s' % (key,currentCount))
# except:
#     pass



