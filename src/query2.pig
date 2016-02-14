/* this is the hw2 query2 file*/

cust = LOAD './p1_dataset/Customers.csv' USING PigStorage(',') AS (id,name,age,code,salary);
tran = LOAD './p1_dataset/Transactions.csv' USING PigStorage(',') AS (tid,cid,ttotal,items,descip);
custfiled = FOREACH cust GENERATE id, name, salary;
tranfiled = FOREACH tran GENERATE tid, cid, ttotal, items;
groupit = GROUP tranfiled BY (cid);
results = FOREACH groupit 
   GENERATE group, COUNT(tranfiled.tid) AS t_count, SUM(tranfiled.ttotal) AS al_count, MIN(tranfiled.items) AS mitt;
jointwo = JOIN results by group, custfiled by id;
lastone = FOREACH jointwo
   GENERATE custfiled.id, custfiled.name, custfiled.salary, results.t_count, results.al_count, results.mitt;
STORE results INTO 'hw2_q2_out.csv' USING PigStorage(',');
