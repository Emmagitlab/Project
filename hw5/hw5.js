/**
 * cs561 hw5
 * Team_10 
 * April/07/2015
 */

//==============================================================================
// (2) update _id=2 for the name John McCarthy
print("\n====================result for q_2");
var ijohn = db.test.findOne({'name.first':"John", 'name.last':"McCarthy"});
var oldID = ijohn._id;
ijohn._id = 2;
db.test.remove({_id:oldID});
db.test.insert(ijohn);
printjson(ijohn);

//==============================================================================
// (3) insert two new records
print("\n====================result for q_3");
var new1 = {
  "_id":20,
  "name":{
    "first":"Alex",
    "last":"Chen"
    },
    "birth":ISODate("1933-08-27T04:00:00Z"),
    "death":ISODate("1984-11-07T04:00:00Z"),
    "contribs":[
      "C++",
      "Simula"
    ],
    "awards":[
      {
        "award": "WPI Award",
        "year": 1977,
        "by": "WPI"
      }
    ]
}

var new2 = {
  "_id":30,
  "name":{
    "first":"David",
    "last":"Mark"
    },
    "birth":ISODate("1911-04-12T04:00:00Z"),
    "death":ISODate("2000-11-07T04:00:00Z"),
    "contribs":[
      "C++",
      "FP",
      "Lisp"
    ],
    "awards":[
      {
        "award": "WPI Award",
        "year": 1963,
        "by": "WPI"
      },
      {
        "award": "Turing Award",
        "year": 1966,
        "by": "ACM"
      }
    ]
}

db.test.insert(new1);
db.test.insert(new2);

//==================================================================
// (4) report Turing Award when year>1976
print("\n====================result for q_4");
cursor = db.test.find({
  "awards":{
    $elemMatch:{
      award:"Turing Award", 
      year:{$gt:1976}, 
      by:"ACM"
    }
  }
});
while(cursor.hasNext()){
  printjson(cursor.next());
}

//====================================================================
// (5) report people who got less than 3 awards or contribute "FP"
print("\n====================result for q_5\n");
cursor5 = db.test.find({$or:[{"awards":null}, {"awards":{$size:1}}, {"awards":{$size:2}}, {contribs:"FP"}]});
while(cursor5.hasNext()){
  printjson(cursor5.next());
}

//=======================================================================
// (6) report name and contribution array, who got Dennis Ritchie
print("\n====================result for q_6\n");
cursor6 = db.test.find({'name.first':"Dennis", 'name.last':"Ritchie"},{"_id":0, "name":1, "contribs":1});
printjson(cursor6.next());

//==========================================================================
// (7) update the Guido van Rossum and add OOP to the contribs
print("\n====================result for q_7\n");
db.test.update({'name.first':"Guido", 'name.last':"van Rossum"}, {$push:{"contribs":"OOP"}});
newone = db.test.find({'name.first':"Guido", 'name.last':"van Rossum"});
printjson(newone.next());

//============================================================================
// (8) insert a new field array comments into name: Alex Chen
print("\n====================result for q_8\n");
var comarray = ["He taught in 3 universities", "died from cancer", "lived in CA"];
db.test.update({'name.first':"Alex", 'name.last':"Chen"}, {$set: {"comments":comarray}});
newone = db.test.find({'name.first':"Alex", 'name.last':"Chen"});
printjson(newone.next());

//==============================================================================
// (9) find the people who has the same contribs with Alex Chen
print("\n====================result for q_9\n");
var contributions = db.test.find({'name.first':"Alex", 'name.last':"Chen"}, {"_id":0, "contribs":1});
contlist = contributions.next();
theone = contlist.contribs[0];
thetwo = contlist.contribs[1];
findone = db.test.find({"contribs":theone},{"_id":0, "name":1});
findtwo = db.test.find({"contribs":thetwo},{"_id":0, "name":1});

print("{Contribution: \'" + theone +"\',");
print("People: [");
while(findone.hasNext()){
  printjson(findone.next());
}
print("]}\n");

print("{Contribution: \'" + thetwo + "\',");
print("People: [");
while(findtwo.hasNext()){
  printjson(findtwo.next());
}
print("]}");

//===============================================================================
// (10) find document first name with "Jo*" and sorted with last name
print("\n====================result for q_10\n");
var sortJo = db.test.find({"name.first":{$regex: "Jo"}}, {"_id":1, "name":1}).sort({"name.last":-1}); //decending order
while(sortJo.hasNext()){
  printjson(sortJo.next());
}

//=================================================================================
// (11) report array of distinct organizations that give awards
print("\n====================result for q_11\n");


//=================================================================================
// (12) delete from all documents that the death field
print("\n====================result for q_12\n");
newone = db.test.find();
while(newone.hasNext()){
  temp = newone.next();
  theid = temp._id;
  db.test.update({"_id":theid}, {$unset:{"death":""}});
}
newone = db.test.find();
while(newone.hasNext()){
  printjson(newone.next());
}

//====================================================================================
// (13) delete awards that given in 2011
print("\n====================result for q_13\n");
newone = db.test.find({$or:[{"awards.year":2011}, {"awards.year":"2011"}]});
while(newone.hasNext()){
  temp = newone.next();
  theid = temp._id;
  //printjson(theid);
  db.test.update({"_id":theid}, {$pull:{"awards":{$or:[{"year":"2011"}, {"year":2011}]}}});
  output = db.test.find({"_id":theid});
  printjson(output.next());
}

//=====================================================================================
// (14) update the _id=30, award given by WPI and set the year=1965
print("\n====================result for q_14\n");
db.test.update({"_id":30, "awards.by":"WPI"},{$set: {"awards.$.year" : 1965}});
newone = db.test.find({_id:30});
printjson(newone.next());

//====================================================================================
// (15) add or copy all contribute from document _id=3 to the document _id=30
print("\n====================result for q_15\n");
id3contribs = db.test.find({_id:3}, {"_id":0, "contribs":1});
temp = id3contribs.next();
db.test.update({_id:30}, {$pushAll:temp});
temp = db.test.find({_id:30});
printjson(temp.next());

//======================================================================================
// (16) report the name who have won at least 2 awards in 2001
print("\n====================result for q_16\n");
// results = db.test.find({"awards.year":2001, $or:[{"awards":{$size:2}}, {"awards":{$size:3}},{"awards":{$size:4}},{"awards":{$size:5}}] }, {"_id":0, "name":1});
results = db.test.find({"awards.year":2001, "awards":{$size:3}}, {"_id":0, "name":1});
while(results.hasNext()){
  printjson(results.next());
}

//=====================================================================================
// (17) report the largest id of document
print("\n====================result for q_17\n");
projection = db.test.find();
var maxID = 0;
var currentID;
while(projection.hasNext()){
  theone = projection.next();
  currentID = theone._id;
  if(currentID>maxID){
    maxID = currentID;
  }
}
maxreport = db.test.find({_id:maxID});
printjson(maxreport.next());

//=====================================================================================
// (18) report only one document where one of awards is given by ACM
print("\n====================result for q_18\n");
rewards = db.test.find({"awards.by":"ACM"});
printjson(rewards.next());

//===================================================================================
// (19) delete documents that inserted in Q3 _id=20 and _id=30
print("\n====================results for q_19 and q_20\n");
db.test.remove({$or:[{_id:20}, {_id:30}]});

//=====================================================================================
// (20) print the total number of documents in test collection
totaldoc = db.test.find().count();
print("The total number of documents is: "+totaldoc);