/* this is the hw2 query4.pig file */

cust = LOAD './p1_dataset/Customers.csv' USING PigStorage(',') AS (id,name,age,code,salary);
tran = LOAD './p1_dataset/Transactions.csv' USING PigStorage(',') AS (tid,cid,ttotal,items,descip);
custfiled = FOREACH cust GENERATE id, name;
tranfiled = FOREACH tran GENERATE tid, cid;
groupit = GROUP tranfiled BY (cid);
countit = FOREACH groupit GENERATE group, COUNT(tranfiled.tid) AS tcount;
jointwo = JOIN countit BY group, custfiled BY id;
projit = FOREACH jointwo GENERATE custfiled.name AS newname, countit.tcount AS tcc;

moregroup = GROUP projit ALL;
results = FOREACH moregroup GENERATE projit.newname, MIN(projit.tcc);
/*
jointwo = JOIN tranfiled BY cid, custfiled BY id USING 'replicated';
projit = FOREACH jointwo GENERATE tranfiled.tid AS newtid, tranfiled.cid AS newcid, custfiled.name AS cname;
results = FOREACH groupit GENERATE group, projit.cname, COUNT(projit.newtid);
*/
STORE results INTO 'hw2_q4_out' USING PigStorage(',');
