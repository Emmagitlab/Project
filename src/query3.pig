/* this is hw2 query2.pig file */

cust = LOAD './p1_dataset/Customers.csv' USING PigStorage(',') AS (id,name,age,code,salary);
tran = LOAD './p1_dataset/Transactions.csv' USING PigStorage(',') AS (tid,cid,ttotal,items,descip);
custfiled = FOREACH cust GENERATE id, code;
tranfiled = FOREACH tran GENERATE tid, cid, ttotal;
jointwo = JOIN tranfiled by cid, custfiled by id USING 'replicated'; /*the big table join small table*/
/* grouptit = GROUP jointwo BY (custfiled.code); */

/* results = FOREACH grouptit GENERATE group, MIN(tranfiled.ttotal); */

projit = FOREACH jointwo GENERATE tranfiled.tid, custfiled.code AS ccode, custfiled.id AS newid, tranfiled.ttotal AS newtotal;

grouptit = GROUP projit BY (ccode); 


results = FOREACH grouptit {
	dis_id = DISTINCT projit.newid;
	GENERATE group, COUNT(dis_id), MIN(projit.newtotal), MAX(projit.newtotal);
};

/* results = FOREACH grouptit GENERATE group, MIN(projit.newtotal), MAX(projit.newtotal); */

STORE grouptit INTO 'hw2_q2_out' USING PigStorage(',');
