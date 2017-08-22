DROP TABLE CUSTOMER IF EXISTS;

DROP TABLE ADDRESS IF EXISTS;

CREATE TABLE ADDRESS
( 
  ADDRESS_ID BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL 
, STREET_NUMBER VARCHAR(8)
, STREET_NAME VARCHAR(64) NOT NULL 
, STREET_TYPE VARCHAR(32) 
, SUBURB VARCHAR(64) NULL 
, CITY VARCHAR(64) NULL 
, COUNTRY VARCHAR(64) NULL 
, PRIMARY KEY ( ADDRESS_ID ) 
);


CREATE TABLE CUSTOMER
( 
 CUSTOMER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL,
 NAME VARCHAR(50) NOT NULL, 
 ADDRESS_ID BIGINT NULL, 
 TELEPHONE_NUMBER VARCHAR(64) NULL,
 PRIMARY KEY ( CUSTOMER_ID ) 
);

ALTER TABLE CUSTOMER ADD CONSTRAINT FK_ADDRESS FOREIGN KEY ( ADDRESS_ID )
 REFERENCES ADDRESS ( ADDRESS_ID );

