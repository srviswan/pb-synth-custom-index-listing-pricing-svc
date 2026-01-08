#!/bin/bash

# End-to-End Test for Custom Index Listing & Pricing Suite
# Flow: Create -> Add Constituents -> List -> Price -> Publish

BASE_URL="http://localhost:8081/api/v1/baskets"
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

function check_status() {
    local basket_id=$1
    local expected_status=$2
    local actual_status=$(curl -s "$BASE_URL/$basket_id" | jq -r '.status')
    
    if [ "$actual_status" == "$expected_status" ]; then
        echo -e "${GREEN}SUCCESS: Basket $basket_id is in $expected_status status.${NC}"
    else
        echo -e "${RED}FAILURE: Expected status $expected_status, but got $actual_status for basket $basket_id.${NC}"
        exit 1
    fi
}

echo "--- Starting End-to-End Workflow Test ---"

# 1. Create Basket
echo -e "\n1. Creating a new basket..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "E2E Comprehensive Index",
           "type": "EQUITY",
           "sourceSystem": "OMS",
           "divisor": 100.0
         }')

BASKET_ID=$(echo "$CREATE_RESPONSE" | jq -r '.id')
if [ "$BASKET_ID" == "null" ] || [ -z "$BASKET_ID" ]; then
    echo -e "${RED}Failed to create basket.${NC}"
    echo "$CREATE_RESPONSE"
    exit 1
fi
echo "Basket ID: $BASKET_ID"
check_status "$BASKET_ID" "DRAFT"

# 2. Add Constituents
echo -e "\n2. Adding constituents (AAPL, MSFT)..."
UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/$BASKET_ID/constituents" \
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
         ]')
echo "Constituents added."

# 3. Trigger Listing
echo -e "\n3. Triggering LISTING..."
curl -s -X POST "$BASE_URL/$BASKET_ID/list"
echo "Listing command sent. Waiting for async processing..."
sleep 2 # Wait for event processing
check_status "$BASKET_ID" "LISTED"

# 4. Trigger Pricing
echo -e "\n4. Triggering PRICING..."
curl -s -X POST "$BASE_URL/$BASKET_ID/price"
echo "Pricing command sent. Waiting for async processing..."
sleep 3 # Wait for pricing (includes market data fetch)
check_status "$BASKET_ID" "PRICED"

# 5. Trigger Publishing
echo -e "\n5. Triggering PUBLISHING..."
curl -s -X POST "$BASE_URL/$BASKET_ID/publish"
echo "Publishing command sent. Waiting for async processing..."
sleep 2
check_status "$BASKET_ID" "READY_FOR_PUBLISHING"

echo -e "\n--- ${GREEN}End-to-End Workflow Test Passed Successfully!${NC} ---"
echo "Summary of Final Basket State:"
curl -s "$BASE_URL/$BASKET_ID" | jq .
