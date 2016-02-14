/* this hw2 query1 file */

/* cust = LOAD './p1_dataset/Customers.csv' USING PigStorage(',') AS (id,name,age,code,salary); */
tran = LOAD './p1_dataset/Transactions.csv' USING PigStorage(',') AS (tid,cid,ttotal,items,descip);
filed = FOREACH tran GENERATE tid, cid, ttotal;
groupit = GROUP filed BY (cid);
results = FOREACH groupit GENERATE group, COUNT(filed.tid), SUM(filed.ttotal);
STORE results INTO 'hw2_q1_out.csv' USING PigStorage(',');
