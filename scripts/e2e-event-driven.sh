#!/bin/bash

# Automated End-to-End Workflow Test (Event-Driven)
# Flow: Create -> Add Constituents -> List (REST) -> [Auto Listing -> Auto Pricing -> Auto Publishing] (Events)

BASE_URL="http://localhost:8081/api/v1/baskets"
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

function get_status() {
    curl -s "$BASE_URL/$1" | jq -r '.status'
}

echo "--- Starting Event-Driven End-to-End Workflow Test ---"

# 1. Create Basket
echo -e "\n1. Creating a new basket..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Event Driven Index",
           "type": "EQUITY",
           "sourceSystem": "OMS",
           "divisor": 100.0
         }')

BASKET_ID=$(echo "$CREATE_RESPONSE" | jq -r '.id')
echo "Basket ID: $BASKET_ID"

# 2. Add Constituents
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

# 3. Trigger initial Listing (Rest of the flow is automatic)
echo -e "\n3. Triggering initial LISTING (REST)..."
curl -s -X POST "$BASE_URL/$BASKET_ID/list"
echo -e "${YELLOW}Initial command sent. The workflow is now running asynchronously via Kafka events.${NC}"

# 4. Polling for final status
echo -e "\n4. Polling for final status (PUBLISHED)..."
MAX_RETRIES=20
RETRY_COUNT=0
FINAL_STATUS="PUBLISHED"

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    CURRENT_STATUS=$(get_status "$BASKET_ID")
    echo "Current Status: $CURRENT_STATUS"
    
    if [ "$CURRENT_STATUS" == "$FINAL_STATUS" ]; then
        echo -e "\n${GREEN}SUCCESS: Workflow completed automatically!${NC}"
        break
    fi
    
    RETRY_COUNT=$((RETRY_COUNT+1))
    sleep 3
done

if [ "$CURRENT_STATUS" != "$FINAL_STATUS" ]; then
    echo -e "\n${RED}FAILURE: Workflow timed out or failed. Final status reached: $CURRENT_STATUS${NC}"
    exit 1
fi

echo -e "\n--- ${GREEN}Event-Driven Workflow Test Passed!${NC} ---"
echo "Final Basket Summary:"
curl -s "$BASE_URL/$BASKET_ID" | jq .
