#!/bin/bash

# Autonomous Choreography End-to-End Workflow Test
# Flow: Create (REST) -> [Auto Listing -> Auto Pricing -> Auto Publishing] (Choreographed Events)

BASE_URL="http://localhost:8081/api/v1/baskets"
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

function get_status() {
    curl -s "$BASE_URL/$1" | jq -r '.status'
}

echo "--- Starting Autonomous Choreography Workflow Test ---"

# 1. Create Basket (This automatically kicks off the workflow)
echo -e "\n1. Creating a new basket (Save will start the workflow)..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL" \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Choreographed autonomous Index",
           "type": "EQUITY",
           "sourceSystem": "OMS",
           "divisor": 100.0,
           "constituents": [
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
           ]
         }')

BASKET_ID=$(echo "$CREATE_RESPONSE" | jq -r '.id')
if [ "$BASKET_ID" == "null" ] || [ -z "$BASKET_ID" ]; then
    echo -e "${RED}Failed to create basket.${NC}"
    echo "$CREATE_RESPONSE"
    exit 1
fi
echo "Basket ID: $BASKET_ID"
echo -e "${YELLOW}Basket saved. The workflow is now running autonomously via choreographed Kafka events.${NC}"

# 2. Polling for final status
echo -e "\n2. Polling for final status (PUBLISHED)..."
MAX_RETRIES=25
RETRY_COUNT=0
FINAL_STATUS="PUBLISHED"

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    CURRENT_STATUS=$(get_status "$BASKET_ID")
    echo "Current Status: $CURRENT_STATUS"
    
    if [ "$CURRENT_STATUS" == "$FINAL_STATUS" ]; then
        echo -e "\n${GREEN}SUCCESS: Autonomous workflow completed!${NC}"
        break
    fi
    
    RETRY_COUNT=$((RETRY_COUNT+1))
    sleep 3
done

if [ "$CURRENT_STATUS" != "$FINAL_STATUS" ]; then
    echo -e "\n${RED}FAILURE: Workflow timed out or failed. Final status reached: $CURRENT_STATUS${NC}"
    exit 1
fi

echo -e "\n--- ${GREEN}Autonomous Choreography Test Passed!${NC} ---"
echo "Final Basket Summary:"
curl -s "$BASE_URL/$BASKET_ID" | jq .
