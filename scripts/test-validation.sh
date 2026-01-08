#!/bin/bash

BASE_URL="http://localhost:8081/api/v1/baskets"

echo "1. Creating a new basket..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Invalid Index",
           "type": "EQUITY",
           "sourceSystem": "OMS",
           "divisor": 100.0
         }')

BASKET_ID=$(echo $CREATE_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Basket ID: $BASKET_ID"

echo -e "\n2. Attempting to add an INVALID instrument (NON_EXISTENT)..."
curl -s -i -X PUT "$BASE_URL/$BASKET_ID/constituents" \
     -H "Content-Type: application/json" \
     -d '[
           {
             "instrumentId": "NON_EXISTENT",
             "instrumentType": "STOCK",
             "weight": 1.0,
             "currency": "USD"
           }
         ]' | head -n 20

echo -e "\n3. Attempting to add an ELIGIBLE instrument (AAPL)..."
curl -s -i -X PUT "$BASE_URL/$BASKET_ID/constituents" \
     -H "Content-Type: application/json" \
     -d '[
           {
             "instrumentId": "AAPL",
             "instrumentType": "STOCK",
             "weight": 1.0,
             "currency": "USD"
           }
         ]' | head -n 20
