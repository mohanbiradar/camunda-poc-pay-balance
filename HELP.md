# Pay My Balance - Camunda 8 Demo Setup

## 📋 Prerequisites

1. **Java 17** or higher
2. **Maven 3.6+**
3. **Camunda 8** (Self-Managed) running locally
    - Zeebe Gateway on port `26500`
    - Operate on port `8081`
    - Tasklist on port `8082`

## 🚀 Setup Instructions

### 1. Project Structure
Create the following directory structure:
```
pay-my-balance/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/demo/paymybalance/
│       │       ├── PayMyBalanceApplication.java
│       │       ├── controller/
│       │       │   └── PayMyBalanceController.java
│       │       ├── model/
│       │       │   ├── Customer.java
│       │       │   ├── AccountBalance.java
│       │       │   ├── PaymentRequest.java
│       │       │   ├── FraudCheckResult.java
│       │       │   └── PaymentResponse.java
│       │       ├── service/
│       │       │   ├── CustomerService.java
│       │       │   ├── AccountService.java
│       │       │   ├── FraudService.java
│       │       │   ├── PaymentGatewayService.java
│       │       │   └── NotificationService.java
│       │       └── worker/
│       │           ├── ValidateCustomerWorker.java
│       │           ├── RetrieveBalanceWorker.java
│       │           ├── FraudCheckWorker.java
│       │           ├── InitiatePaymentWorker.java
│       │           ├── ConfirmPaymentWorker.java
│       │           ├── UpdateBalanceWorker.java
│       │           └── SendConfirmationWorker.java
│       └── resources/
│           ├── pay-my-balance-process.bpmn
│           ├── payment-options-form.form
│           └── application.yml
└── pom.xml
```

### 2. Deploy BPMN Process
1. Copy the `pay-my-balance-process.bpmn` file to `src/main/resources/`
2. The process will be auto-deployed on application startup

### 3. Start the Application
```bash
mvn clean spring-boot:run
```

The application will start on port `8080`.

## 🧪 Testing the Demo

### Test Data Available
- **Customer ID**: `CUST001` (Active)
- **Card Account ID**: `CC001`
- **Current Balance**: $2,450.75
- **Available Credit**: $7,549.25

### Method 1: Using cURL
```bash
# Test payment initiation
curl -X POST http://localhost:8080/api/payment/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "cardAccountId": "CC001",
    "paymentAmount": 500.00,
    "paymentMethod": "ACH",
    "paymentDate": "2024-01-15T10:30:00"
  }'
```

### Method 2: Using Postman
**POST** `http://localhost:8080/api/payment/initiate`

**Headers:**
- `Content-Type: application/json`

**Body (raw JSON):**
```json
{
  "customerId": "CUST001",
  "cardAccountId": "CC001",
  "paymentAmount": 500.00,
  "paymentMethod": "ACH",
  "paymentDate": "2024-01-15T10:30:00"
}
```

### Method 3: Using Camunda Tasklist
1. Navigate to `http://localhost:8082` (Tasklist)
2. Complete the User Task "Present Payment Options to User"
3. Fill in the form fields:
    - Payment Amount: `1000.00`
    - Payment Method: `ACH`
    - Payment Date: Select current date/time
    - Check the confirmation checkbox

## 📊 Monitoring the Demo

### 1. Console Output
Watch the console for detailed logging:
```
=== Validating Customer ===
Customer ID: CUST001
Card Account ID: CC001
✓ Customer validation successful

=== Retrieving Balance ===
Card Account ID: CC001
✓ Balance retrieved: $2450.75
  Available Credit: $7549.25

=== Performing Fraud Check ===
Customer ID: CUST001
Payment Amount: $500.00
Payment Method: ACH
✓ Fraud check result: APPROVED
  Risk Score: 0.25
  Reason: Low risk transaction

=== Initiating Payment ===
Card Account ID: CC001
Payment Amount: $500.00
Payment Method: ACH
✓ Payment initiated successfully
  Transaction ID: TXN-A1B2C3D4
  Status: PROCESSING

=== Confirming Payment ===
Transaction ID: TXN-A1B2C3D4
✓ Payment confirmation: COMPLETED
  Message: Payment completed successfully

=== Updating Account Balance ===
Card Account ID: CC001
Payment Amount: $500.00
Transaction ID: TXN-A1B2C3D4
✓ Account balance updated successfully

=== Sending Confirmation ===
Customer ID: CUST001
Payment Amount: $500.00
Transaction ID: TXN-A1B2C3D4
=== PAYMENT CONFIRMATION ===
Customer ID: CUST001
Payment Amount: $500.00
Transaction ID: TXN-A1B2C3D4
Status: Payment Successful
=============================
✓ Confirmation sent successfully
```

### 2. Camunda Operate
1. Navigate to `http://localhost:8081`
2. View the running process instances
3. Check process flow execution
4. Monitor variables and incidents

### 3. Process Variables
Key variables tracked throughout the process:
- `customerId`
- `cardAccountId`
- `paymentAmount`
- `paymentMethod`
- `currentBalance`
- `fraudCheckResult`
- `transactionId`
- `paymentStatus`

## 🎭 Demo Scenarios

### Scenario 1: Successful Payment (Happy Path)
- Customer: `CUST001`
- Account: `CC001`
- Amount: `$500.00`
- Expected Result: ✅ Payment completed successfully

### Scenario 2: High-Risk Transaction
- Customer: `CUST001`
- Account: `CC001`
- Amount: `$8000.00` (high amount triggers higher risk score)
- Expected Result: May be rejected due to fraud check

### Scenario 3: Invalid Customer
- Customer: `CUST999` (doesn't exist)
- Account: `CC999`
- Amount: `$100.00`
- Expected Result: ❌ Process fails at customer validation

### Scenario 4: Suspended Account
- Customer: `CUST003`
- Account: `CC003`
- Amount: `$100.00`
- Expected Result: ❌ Validation fails due to suspended status

## 🔧 Troubleshooting

### Common Issues

1. **Connection Refused to Zeebe**
    - Ensure Camunda 8 is running
    - Check port 26500 is accessible
    - Verify `application.yml` gateway address

2. **Process Not Deploying**
    - Check BPMN file is in `src/main/resources/`
    - Verify BPMN syntax
    - Check application logs for deployment errors

3. **Jobs Not Being Picked Up**
    - Ensure worker annotations match task types
    - Check if workers are registered as Spring components
    - Verify worker threads are not blocked

4. **Form Not Displaying**
    - Ensure form file is in resources
    - Check form ID matches BPMN reference
    - Verify form JSON syntax

## 📈 Demo Presentation Tips

1. **Start with the Big Picture**
    - Show the BPMN diagram first
    - Explain the business process flow
    - Highlight key decision points

2. **Show Real Execution**
    - Use Postman/cURL to trigger process
    - Watch console logs in real-time
    - Navigate through Operate to show process state

3. **Demonstrate User Task**
    - Complete the form in Tasklist
    - Show how user input affects process flow
    - Explain form validation

4. **Handle Failures Gracefully**
    - Test with invalid data to show error handling
    - Demonstrate retry mechanisms
    - Show incident resolution in Operate

5. **Business Value Points**
    - Automated compliance checks
    - Audit trail and monitoring
    - Scalable process orchestration
    - Human-in-the-loop capabilities

## 🚀 Extensions for Advanced Demo

1. **Add DMN Decision Tables**
    - Replace fraud service with DMN rules
    - Show business rule management

2. **Message Events**
    - Add payment confirmation via message
    - Demonstrate event correlation

3. **Timers**
    - Add timeout for payment confirmation
    - Show escalation scenarios

4. **Multi-Instance**
    - Handle multiple payments in parallel
    - Demonstrate bulk processing

5. **Error Boundary Events**
    - Add specific error handling
    - Show compensation flows

🎭 Demo Scenarios to Show:
Scenario 1: Happy Path ⭐

Customer: CUST001
Account: CC001
Amount: $500
Result: Complete success with balance update

Scenario 2: High-Risk Detection ⚠️

Customer: CUST001
Amount: $8000 (triggers high risk score)
Result: May route to rejection path

Scenario 3: Suspended Customer ❌

Customer: CUST003
Result: Fails at validation step

Scenario 4: User Task Demo 👤

Start process without REST API
Complete form in Tasklist UI
Show human-in-the-loop capability