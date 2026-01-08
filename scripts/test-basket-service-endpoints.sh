#!/bin/bash

BASE_URL="http://localhost:8081/api/v1/baskets"

echo "1. Creating a new basket..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Tech Giants Index",
           "type": "EQUITY",
           "sourceSystem": "OMS"
         }')

echo "Response: $CREATE_RESPONSE"
BASKET_ID=$(echo $CREATE_RESPONSE | grep -o '"id":"[^"]*' | cut -d'"' -f4)

if [ -z "$BASKET_ID" ]; then
    echo "Failed to extract Basket ID. Is the service running on port 8081?"
    exit 1
fi

echo -e "\nBasket ID: $BASKET_ID"

echo -e "\n2. Updating constituents for the basket..."
curl -s -X PUT "$BASE_URL/$BASKET_ID/constituents" \
     -H "Content-Type: application/json" \
     -d '[
           {
             "instrumentId": "AAPL",
             "instrumentType": "STOCK",
             "weight": 0.6,
             "currency": "USD"
           },
           {
             "instrumentId": "MSFT",
             "instrumentType": "STOCK",
             "weight": 0.4,
             "currency": "USD"
           }
         ]' | json_pp || echo "Update call finished."

echo -e "\n3. Fetching the updated basket..."
curl -s -X GET "$BASE_URL/$BASKET_ID" | json_pp || echo "Fetch call finished."
