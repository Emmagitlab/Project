/* this is the hw2 query2 file*/

cust = LOAD './p1_dataset/Customers.csv' USING PigStorage(',') AS (id,name,age,code,salary);
tran = LOAD './p1_dataset/Transactions.csv' USING PigStorage(',') AS (tid,cid,ttotal,items,descip);
custfiled = FOREACH cust GENERATE id, name, salary;
tranfiled = FOREACH tran GENERATE tid, cid, ttotal, items;
groupit = GROUP tranfiled BY (cid);
results = FOREACH groupit 
   GENERATE group AS tkey, COUNT(tranfiled.tid) AS t_count, SUM(tranfiled.ttotal) AS al_count, MIN(tranfiled.items) AS mitt;
jointwo = JOIN results BY tkey, custfiled BY id;
lastone = FOREACH jointwo
   GENERATE custfiled::id as cid, custfiled::name as name, custfiled::salary as salary, results::t_count as tcount,  results::al_count as al_count, results::mitt as mitt;
STORE lastone INTO 'hw2_q2_out' USING PigStorage(',');
