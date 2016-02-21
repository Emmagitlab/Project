#__author__ = 'T440'

import sys

# maps words to their counts
foundKey = ""
foundValue = ""
isFirst = 1
currentCount = 0
currentName = "-1"
currentCustID = "-1"
isCust = False

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    try:
        # parse the input we got from mapper.py
        keyc,keyt,name,trans,countrycode = line.split(',')

        #the first line should be a mapping line, otherwise we need to set the currentCountryName to not known
        if keyt == "-1":#get name + keyc
            currentName = name
            currentCustID = keyc
            isCust = True
        else:
            isCust = False # count

        if not isCust: #count tranid

            currentKey = '%s\t%s' % (currentCustID,currentName)

            if foundKey != currentKey:
                if isFirst == 0:
                    print ('%s\t%s' % (foundKey,currentCount))
                    currentCount = 1
                else:
                    isFirst = 0

                foundKey = currentKey

            currentCount += 1 
    except:
        pass

try:
    print ('%s\t%s' % (foundKey,currentCount))
except:
    pass



