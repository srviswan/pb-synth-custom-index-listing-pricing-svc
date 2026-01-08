#!/bin/bash

BASE_URL="http://localhost:8081/api/v1/baskets"

echo "1. Creating a new basket..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "E2E Workflow Index",
           "type": "EQUITY",
           "sourceSystem": "OMS",
           "divisor": 100.0
         }')

BASKET_ID=$(echo $CREATE_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Basket ID: $BASKET_ID"

echo -e "\n2. Adding constituents (AAPL, MSFT)..."
curl -s -X PUT "$BASE_URL/$BASKET_ID/constituents" \
     -H "Content-Type: application/json" \
     -d '[
           {
             "instrumentId": "AAPL",
             "instrumentType": "STOCK",
             "weight": 0.5,
             "currency": "USD"
           },
           {
             "instrumentId": "MSFT",
             "instrumentType": "STOCK",
             "weight": 0.5,
             "currency": "USD"
           }
         ]' > /dev/null

echo -e "\n3. Triggering LISTING..."
curl -s -X POST "$BASE_URL/$BASKET_ID/list"
echo "Check Listing Service logs for fan-out events."

sleep 5

echo -e "\n4. Triggering PRICING..."
curl -s -X POST "$BASE_URL/$BASKET_ID/price"
echo "Check Pricing Service logs for DQ checks and Pricing completed event."

sleep 5

echo -e "\n5. Checking status after Pricing..."
curl -s -X GET "$BASE_URL/$BASKET_ID" | json_pp

echo -e "\n6. Triggering PUBLISHING..."
curl -s -X POST "$BASE_URL/$BASKET_ID/publish"
echo "Check Publishing Service logs for Integrity checks."
