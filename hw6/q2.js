/**
 * CS561 hw6
 * Question #2 
 * Team 10
 * April/22/2016
 * 
 **/
 
//===================================================================
// (1) write the aggregation query that groups by awards.award
print("\n==========================result for (1)");
var mapFuction = function(){
  for (var idx = 0; idx < this.awards.length; idx++){
    var key = this.awards[idx].award;
    var value = {count: 1};
    emit(key,value);
    
  }
  
};
var reduceFunction = function(key, countObjVals){
  reducedVal = {count: 0};
  
  for (var idx = 0; idx < countObjVals.length; idx++){
    reduceVal.count += countObjVals[idx].count;
  }
  return reduceVal;
  
}



 
//===================================================================
// (2) write the aggregation query that groups by birth.year
print("\n==========================result for (2)");

//===================================================================
// (3) report the documents that have smallest and largest _id 
print("\n==========================result for (3)");
var minID = 999999;
var maxID = -999999;
var alldoc = db.test.find();
while(alldoc.hasNext()){
  temp = alldoc.next()._id;
  if(temp<minID){
    minID = temp;
  }
  if(temp>maxID){
    maxID = temp;
  }
}
maxdoc = db.test.findOne({_id:maxID});
mindoc = db.test.findOne({_id:minID});
print("Max_Document: ");
printjson(maxdoc);
print("\nMin_Document: ");
printjson(mindoc);

//=============================================================================
// (4) use $text operator to search "Turing Award" as one sentence(not separate keywords)
print("\n==========================result for (4)");
db.test.createIndex({"awards.award":"text"});
var results = db.test.find({$text: {$search: "\"Turing Award\""}});
while(results.hasNext()){
    printjson(results.next());
}

//=============================================================================
// (5) use $text operator to search either contain "Turing" or "National Meddal"
print("\n==========================result for (5)");
db.test.createIndex({"awards.award":"text"});
var results = db.test.find({$text: {$search: "Turing \"National Medal\""}});
while(results.hasNext()){
    printjson(results.next());
}

print("\n=============================end of q2");