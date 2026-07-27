# Rust Worker Integration Guide for Backend Testing

This document outlines the implementation of the Rust worker and the technical contract for communication with the Java backend via AWS SQS. This information can be used to create integration tests on the backend to verify the end-to-end workflow.

---

## 1. Overview of the Rust Worker

A Rust worker has been implemented to handle the asynchronous resolution of betting events.

- **Functionality**: The worker polls an SQS queue for messages, processes them according to a predefined business logic, and sends the results to another SQS queue.
- **Project Location**: `C:/Users/m/RustroverProjects/underdogs-worker/`
- **How to Run**: The worker is started with `cargo run`. It requires AWS credentials (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`) to be configured in its environment to connect to SQS.

---

## 2. SQS Queue Configuration

- **Request Queue**: `underdogs-requests-queue`
  - The Java backend sends job requests here.
  - The Rust worker polls messages from this queue.

- **Response Queue**: `underdogs-responses-queue`
  - The Rust worker sends the results of the processed jobs here.
  - The Java backend should listen for results on this queue.

---

## 3. Communication Contract

### Request Payload (Sent by Java Backend)

To test the worker, the backend should send a message to `underdogs-requests-queue` with the following JSON structure. The worker will only process messages where the `action` is `"RESOLVE_EVENT_BETS"`.

```json
{
  "correlation_id": "string (UUID)",
  "action": "RESOLVE_EVENT_BETS",
  "payload": {
    "event_id": "string",
    "winning_team_id": "string",
    "bets": [
      {
        "bet_id": "string",
        "user_id": "string",
        "predicted_team_id": "string",
        "amount_wagered": "integer",
        "odds": "number (double)"
      }
    ]
  }
}
```

**Example Request:**
```json
{
  "correlation_id": "123e4567-e89b-12d3-a456-426614174000",
  "action": "RESOLVE_EVENT_BETS",
  "payload": {
    "event_id": "evt_98765",
    "winning_team_id": "team_A",
    "bets": [
      {
        "bet_id": "bet_001",
        "user_id": "usr_555",
        "predicted_team_id": "team_A",
        "amount_wagered": 100,
        "odds": 1.5
      },
      {
        "bet_id": "bet_002",
        "user_id": "usr_777",
        "predicted_team_id": "team_B",
        "amount_wagered": 50,
        "odds": 2.1
      }
    ]
  }
}
```

### Response Payload (Sent by Rust Worker)

After processing, the Rust worker will send a message to `underdogs-responses-queue`. The backend tests should verify that the received message matches this structure and contains the correct calculations.

- The `correlation_id` will be the same as the one from the request.
- `kibbles_to_credit` is calculated as `(amount_wagered * odds)` for winning bets and `0` for losing bets.

```json
{
  "correlation_id": "string (UUID)",
  "event_id": "string",
  "status": "SUCCESS",
  "results": [
    {
      "bet_id": "string",
      "user_id": "string",
      "status": "WON" or "LOST",
      "kibbles_to_credit": "integer"
    }
  ],
  "error_message": null
}
```

**Example Response:**
Based on the example request above, the worker will produce this response:
```json
{
  "correlation_id": "123e4567-e89b-12d3-a456-426614174000",
  "event_id": "evt_98765",
  "status": "SUCCESS",
  "results": [
    {
      "bet_id": "bet_001",
      "user_id": "usr_555",
      "status": "WON",
      "kibbles_to_credit": 150
    },
    {
      "bet_id": "bet_002",
      "user_id": "usr_777",
      "status": "LOST",
      "kibbles_to_credit": 0
    }
  ],
  "error_message": null
}
```

---
## 4. Testing Scenarios to Consider

When creating backend tests, consider the following scenarios:
- **Happy Path**: A valid request is sent, and a `SUCCESS` response is received with correct calculations for `WON` and `LOST` bets.
- **Invalid Action**: A request is sent with an `action` other than `"RESOLVE_EVENT_BETS"`. The worker should produce a `FAILURE` response.
- **Malformed JSON**: A message that is not valid JSON is sent. The worker should handle this gracefully and not crash. The worker will produce a `FAILURE` response with a new `correlation_id`.
- **Edge Cases**:
    - A bet list (`bets`) that is empty.
    - Integer vs. floating-point calculations for `kibbles_to_credit`.
    - Bets with `odds` of 0 or 1.