#!/usr/bin/env python

import sys

# input comes from STDIN (standard input)
for line in sys.stdin:
    line = line.strip()  # remove leading and trailing whitespace
  
    custID = "-1"
    custName = "-1"
    code = "5"
    tID = "-1"
    tranCid = "-1"

    # split the line into words
    words = line.split(",")

    if words[1].isalpha() and words[3]==code: #from the customers table
	custID = words[0]
	custName = words[1]
	code = words[3]
	print '%s,%s,%s' % (custID, custName, code)
    else:		#from the transactions table
	tID = words[0]
	tranCid = words[1]
	print '%s,%s' % (tranCid, tID)
	
    # increase counters
    # for word in words:
        # write the results to STDOUT (standard output);
        # what we output here will be the input for the
        # Reduce step, i.e. the input for reducer.py
        #
        # tab-delimited; the trivial word count is 1
        # print '%s\t%s' % (word, 1)
