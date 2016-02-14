#!/usr/bin/env python
# this is the hw2 query5 reducer file.

from operator import itemgetter
import sys

custName = None
custID = "-1"
count = 0

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    tuples = line.split(',')

    # convert count (currently a string) to int
    # try:
    #    count = int(count)
    # except ValueError:
        # count was not a number, so silently
        # ignore/discard this line
    #    continue

    # this IF-switch only works because Hadoop sorts map output
    # by key (here: word) before it is passed to the reducer
    if len(tuples)==3:
	custID = tuple[0]
	custName = tuple[1]

    if len(tuples)==2 and tuples[0]==custID:
	count++
    elif len(tuples)==3:
	if count!=0:
		print '%s,%s,%s' % (custID, custName, count)
	custID = tuple[0]
	custName = tuple[1]
	count = 0
    
    # if current_word == word:
    #     current_count += count
    # else:
    #     if current_word:
            # write result to STDOUT
    #         print '%s\t%s' % (current_word, current_count)
    #    current_count = count
    #    current_word = word

# do not forget to output the last word if needed!
if count!=0:
    print '%s,%s,%s' % (custID, custName, count)
