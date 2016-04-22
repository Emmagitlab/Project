//CS561 hw6
//Question #1 
//Team 10
//April/21/2016

//=====================================================================
// Craete the Parents reference tree
db.categories.drop();
db.createCollection("categories");

db.categories.insert({_id:"MongoDB", parent:"Databases"});
db.categories.insert({_id:"dbm", parent:"Databases"});
db.categories.insert({_id:"Databases", parent:"Programming"});
db.categories.insert({_id:"Languages", parent:"Programming"});
db.categories.insert({_id:"Programming", parent:"Books"});
db.categories.insert({_id:"Books", parent:null});

//====================================================================
// (1) list all ancestors and their distance levels
print("\n====================result for (1)");
var ancestor;
var stack = [];
var item = db.categories.findOne({_id:"MongoDB"}).parent;
var distance = 0;
while(item!=null){
  distance++;
  ancestor = {Name:item, Level:distance};
  stack.push(ancestor);
  item = db.categories.findOne({_id:item}).parent;
}
printjson(stack);

//=====================================================================
// (2) based on the root, find height of the tree
print("\n====================result for (2)");
var decendants = [];
var savedparent;
var countlv = 1;
var item = db.categories.find({parent:"Books"});
savedparent = null;
while(item.hasNext()){
  decendants.push(item.next()._id);
}

while(decendants.length>0){
  var child = decendants.pop();
  
  //here is to check if poping child has different parents, that is a new level.
  if((temp = db.categories.findOne({_id:child}).parent)!=savedparent){
    savedparent = temp;
    countlv++; 
  }
  
  item = db.categories.find({parent:child});
  while(item.hasNext()){
    var newid = item.next()._id;
    decendants.push(newid);
    //print(decendants);
  }
}
print("Height of the Tree: " + countlv);

//==============================================================================
// (3) changing to use child reference model now, find the parent of "dbm"
print("\n====================result for (3)");
db.categories.drop();
db.createCollection("categories");

db.categories.insert({_id:"MongoDB", children:[]});
db.categories.insert({_id:"dbm", children:[]});
db.categories.insert({_id:"Databases", children:["MongoDB", "dbm"]});
db.categories.insert({_id:"Languages", children:[]});
db.categories.insert({_id:"Programming", children:["Databases", "Languages"]});
db.categories.insert({_id:"Books", children:["Programming"]});

var children = db.categories.findOne({children:"dbm"})._id;
printjson(children);

//===============================================================================
// (4) using child reference model now, find the parent of "dbm"
print("\n====================result for (4)");
var results = [];
var children = db.categories.findOne({_id:"Books"}).children;

// this is deepth first search method, you may have other method
while(children.length>0){
  var child = children.pop();
  results.push(child);
  var temp = db.categories.findOne({_id:child}).children;
  while(temp.length>0){
    children.push(temp.pop());
  }
}
printjson(results);

//===============================================================================
// (5) report the siblings of "Database"
print("\n====================result for (5)");
var theparent = db.categories.findOne({children:"Databases"})._id;
results = db.categories.findOne({_id:theparent}).children;
while(results.length>0){
  if((temp = results.pop()) != "Databases"){ //remove the Databases itself
    printjson(temp);
  }
}

print("\n=========================end of q1");