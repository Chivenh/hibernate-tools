CREATE TABLE BASIC ( A INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY (A)  )
CREATE TABLE SOMECOLUMNSNOPK ( PK VARCHAR(25) NOT NULL, B CHAR, C INT NOT NULL )
CREATE TABLE MULTIKEYED ( ORDERID VARCHAR(10), CUSTOMERID VARCHAR(10), NAME VARCHAR(10), PRIMARY KEY(ORDERID, CUSTOMERID) )