#!/bin/bash

sleep 30

result=$(curl -s http://localhost:8080/actuator/health)

if [[ "$result" =~ "UP" ]]; then
    exit 0
else
    exit 1
fi