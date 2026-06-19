# DynamoDb Lambda

## Regenerate source files for Table Items

[local url](http://localhost:8080/dynamodb-lambda)
will regenerate ```.dynamodb.*.json``` files on disk

## Table

### Schema

Tree representation in tree

https://stackoverflow.com/a/54384998

DynamoDB Keys - Everything You Need To Know

https://dynobase.dev/dynamodb-keys/

### Keys

On all DynamoDB tables
HASH: Name
RANGE: Id

### GSI

HASH: Parent
RANGE: Id
Projection: All

In application code, items are requested

- on GSI index
- By passing their parentId

from top of the hierarchy to bottom.

## DynamoDb Mapper

DynamoDBMapper Query Examples (DynamoDB Java Cheat Sheet)

https://dynobase.dev/dynamodb-java-with-dynamodbmapper/

## DynamoDB Converter Tool

https://dynobase.dev/dynamodb-json-converter-tool/