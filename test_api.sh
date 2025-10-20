#!/bin/bash

BASE_URL="http://localhost:8080/api"
TOKEN=""
COLOR_RESET="\033[0m"
COLOR_INFO="\033[0;36m"
COLOR_OK="\033[0;32m"
COLOR_ERR="\033[0;31m"
COLOR_WARN="\033[0;33m"

echo -e "${COLOR_INFO}🚀 Starting API Tests...${COLOR_RESET}\n"

# ======================================================
# AUTHENTICATION
# ======================================================
echo -e "${COLOR_OK}🔐 Logging in...${COLOR_RESET}"
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"superAdmin","password":"admin123"}')

TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.token // empty')
if [ -z "$TOKEN" ]; then
  echo -e "${COLOR_ERR}❌ Failed to obtain token. Check login endpoint.${COLOR_RESET}"
  echo "Response: $TOKEN_RESPONSE"
  exit 1
fi
echo -e "✅ Token acquired: ${COLOR_INFO}${TOKEN:0:30}...${COLOR_RESET}\n"

# ======================================================
# CLIENT ENDPOINTS
# ======================================================
echo -e "${COLOR_OK}👥 Testing Client Endpoints...${COLOR_RESET}\n"

# Create PERSON client
echo -e "➡️ Creating Person client..."
PERSON_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "$BASE_URL/clients" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john.doe@email.com","phone":"+33698765432","type":"PERSON","birthDate":"1992-05-15"}')

PERSON_HTTP_CODE=$(echo "$PERSON_RESPONSE" | grep "HTTP_CODE" | cut -d':' -f2)
PERSON_ID=$(echo "$PERSON_RESPONSE" | jq -r '.id // empty')
echo -e "🆔 Created Person ID: ${COLOR_INFO}${PERSON_ID}${COLOR_RESET}"
echo -e "📦 Full Response: ${PERSON_RESPONSE}\n"

# Create COMPANY client
echo -e "➡️ Creating Company client..."
COMPANY_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "$BASE_URL/clients" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"TechNova\",\"email\":\"contact@technova.com\",\"phone\":\"+33123456789\",\"type\":\"COMPANY\",\"companyId\":\"abc-999\"}")

COMPANY_HTTP_CODE=$(echo "$COMPANY_RESPONSE" | grep "HTTP_CODE" | cut -d':' -f2)
COMPANY_ID=$(echo "$COMPANY_RESPONSE" | jq -r '.id // empty')
echo -e "🏢 Created Company ID: ${COLOR_INFO}${COMPANY_ID}${COLOR_RESET}"
echo -e "📦 Full Response: ${COMPANY_RESPONSE}\n"

# ======================================================
# CONTRACT ENDPOINTS
# ======================================================
echo -e "${COLOR_OK}📜 Testing Contract Endpoints...${COLOR_RESET}\n"

# Create contract with LocalDateTime
CURRENT_TIME=$(date +"%Y-%m-%dT%H:%M:%S")
echo -e "➡️ Creating contract with custom start date..."
CONTRACT_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "$BASE_URL/contracts" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"clientId\":\"$PERSON_ID\",\"startDate\":\"$CURRENT_TIME\",\"costAmount\":300.00}")
CONTRACT_ID=$(echo "$CONTRACT_RESPONSE" | jq -r '.id // empty')
echo -e "📄 Created Contract ID: ${COLOR_INFO}${CONTRACT_ID}${COLOR_RESET}"
echo -e "📦 Full Response: ${CONTRACT_RESPONSE}\n"

# Create contract without startDate → should default to now
echo -e "➡️ Creating contract without startDate (should default to now)..."
CONTRACT_AUTO_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "$BASE_URL/contracts" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"clientId\":\"$PERSON_ID\",\"costAmount\":150.00}")
echo -e "📦 Response: ${CONTRACT_AUTO_RESPONSE}\n"

# Update contract cost
echo -e "➡️ Updating contract cost 300 to 500 for: ${CONTRACT_ID}"
UPDATE_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X PUT "$BASE_URL/contracts/${CONTRACT_ID}/cost?newAmount=500.00" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")
echo -e "📦 Response: ${UPDATE_RESPONSE}\n"

# Filter contracts by update date
echo -e "➡️ Fetching contracts updated after current timestamp..."
FILTER_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X GET "$BASE_URL/contracts/client/${PERSON_ID}?updatedDate=${CURRENT_TIME}" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")
echo -e "📦 Response: ${FILTER_RESPONSE}\n"

# Get total active contracts sum
echo -e "➡️ Fetching total active contracts sum..."
SUM_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X GET "$BASE_URL/contracts/client/${PERSON_ID}/total" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")
echo -e "📦 Total Active Sum: ${SUM_RESPONSE}\n"

# Test invalid contract creation (negative amount)
echo -e "🚫 Testing invalid contract (negative amount)..."
INVALID_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}\n" -X POST "$BASE_URL/contracts" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"clientId\":\"$PERSON_ID\",\"costAmount\":-50.00}")
echo -e "📦 Response: ${INVALID_RESPONSE}\n"

# ======================================================
# CLEANUP
# ======================================================
echo -e "${COLOR_OK}🧹 Cleaning test data...${COLOR_RESET}\n"
curl -s -X DELETE "$BASE_URL/clients/${PERSON_ID}" -H "Authorization: Bearer $TOKEN" -o /dev/null
curl -s -X DELETE "$BASE_URL/clients/${COMPANY_ID}" -H "Authorization: Bearer $TOKEN" -o /dev/null

echo -e "${COLOR_INFO}✅ Test completed successfully.${COLOR_RESET}\n"
