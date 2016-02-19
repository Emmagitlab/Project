#!/usr/bin/env Rscript
# this is hw2 query6 file running RHadoop to do mapreduce job
library(rmr2)

## wordcount-signature
wordcount =
	function(input, output = NULL, pattern = " "){
	
	## query6-map function
	## Important: Producing key or value of type Int may not work
	##            That is why the map() produces ‘1’ as a string
	wc.map = function(., lines) {
		  tuples <- unlist(strsplit(x = lines, split = "\\r")) #split one line to tokens
      row <- length(tuples)
      
      #for(i in 1:(row/2)){
        newtuple <- unlist(strsplit(x = tuples[2], split = ","))
        # if(length(newtuple)==5){
        code <- toString(newtuple[4])
        keyval(toString(tuples[10]), "1")
        #}
      #}
		  # code <- tuples[ ,4]
      
    
      #if(length(tuples)==5){
        # code <- toString(tuples[4])
         # values <- stringmerge("1,",tuple[1])
		    # keyval(code, toString(length(tuples)))
     # }else{
       # keyval("9", "1")
      #}
		  # keyval(unlist(strsplit(x = lines, split = pattern)),'1')
	}

	## wordcount-reduce function
	## Important: Reducer also write the output as string because Int
	##            may not work
	wc.reduce = function(code, counts) {
      ctt <- sum(as.integer(counts))
      # values <- stringmerge(toString(ctt),",")
      # values <- stringmerge(values,tuples[2])
		  keyval(code, toString(ctt))
	}

	## wordcount-mapreduce job configuration
	mapreduce(
	input = input,
	output = output,
	map = wc.map,
	reduce = wc.reduce,
	# combine = TRUE,
	input.format=make.input.format("text"))
}

## Define inputs and outputs 
## The two commented lines can be used if you want to pass your own text or local file

## inputText = capture.output(license())
## inputPath = to.dfs(keyval(NULL, inputText))
inputPath = '/user/hadoop/p1_dataset/CustTest.csv'
# inputPath2 = '/user/hadoop/p1_dataset/Transactions.csv'
outputPath = '/user/hadoop/hw2_Rout'

## Execute (fire map-reduce job). The output is a binary HDFS file
wordcount(inputPath, outputPath, pattern = ",")

## Get Results from HDFS
results <- from.dfs(outputPath)

## Decompose the key and value columns
x <- results$key
y <- as.integer(results$val)

## Plot the values
barplot(y, main="Customer Counting", xlab="CountryCode", ylab="CustNumber", names.arg=x)

## Sort the values and Plot them
z <- sort(y)
barplot(z, main="Sorted Customer Counting", xlab="CountryCode", ylab="CustNumber", names.arg=x, col="blue")
