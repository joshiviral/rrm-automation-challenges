# RRM Test Automation Challenges

**Candidate:** Viral J. Joshi
**Position:** QA Automation Lead — Red River Mutual
**Submitted:** April 2026

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Build Tool | Maven 3.x |
| UI Automation | Selenium WebDriver 4.23 |
| API Automation | RestAssured 5.5 |
| Test Runner | JUnit 5 (Jupiter) |
| CI/CD | GitHub Actions |

---

## Project Structure

```
rrm-automation-challenges/
├── .github/
│   └── workflows/
│       └── ci.yml                  # GitHub Actions CI pipeline
├── src/
│   └── test/
│       └── java/
│           └── com/rrm/qa/
│               ├── DemoblazeUiTests.java    # Challenge 1 — UI
│               └── ContactsApiTests.java    # Challenge 2 — API
├── pom.xml
└── README.md
```

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- Google Chrome (latest) installed

### Run all tests
```bash
mvn test
```

### Run UI tests only (Challenge 1)
```bash
mvn -Dtest=DemoblazeUiTests test
```

### Run API tests only (Challenge 2)
```bash
mvn -Dtest=ContactsApiTests test
```

---

## Challenge 1 — UI Test Automation

**Target:** [https://www.demoblaze.com](https://www.demoblaze.com)
**Framework:** Selenium WebDriver + JUnit 5
**Pattern:** Page Object Model (POM)

### Design Decisions
- ChromeDriver is managed automatically via Selenium Manager (no manual driver setup required)
- Alert dialogs (add-to-cart confirmation) are handled via `Alert.accept()`
- Explicit waits (`WebDriverWait`) used throughout — no `Thread.sleep()` calls
- Tests are independent and can run in any order

---

### TC-UI-01 — Add Product to Cart and Verify Cart Contents

| Field | Detail |
|---|---|
| **Intent** | Verify that a user can browse to a product, add it to the cart, and the item correctly appears in the cart view |
| **Pre-conditions** | demoblaze.com is accessible; no prior items in cart session |
| **Test Steps** | 1. Navigate to https://www.demoblaze.com <br> 2. Click on "Samsung galaxy s6" product card <br> 3. Click "Add to cart" button <br> 4. Accept the browser alert confirmation <br> 5. Click the Cart navigation link <br> 6. Wait for cart table to load |
| **Expected Result** | Cart table contains a row with "Samsung galaxy s6" listed as a line item |
| **Error Handling** | If alert does not appear within 5 seconds, test fails with timeout. If cart table does not load within 10 seconds, test fails with explicit wait timeout. |

---

### TC-UI-02 — Place Order and Verify Purchase Confirmation

| Field | Detail |
|---|---|
| **Intent** | Validate the complete end-to-end purchase flow from cart through to order confirmation, covering the most critical user journey on the site |
| **Pre-conditions** | At least one product has been added to the cart (handled within test setup) |
| **Test Steps** | 1. Navigate to https://www.demoblaze.com <br> 2. Add "Samsung galaxy s6" to cart and accept alert <br> 3. Navigate to Cart <br> 4. Click "Place Order" button <br> 5. Fill in order form: Name, Country, City, Credit Card, Month, Year <br> 6. Click "Purchase" button <br> 7. Wait for confirmation dialog |
| **Expected Result** | Confirmation sweet-alert dialog appears containing both a purchase `Id` and an `Amount` value, confirming the transaction was processed |
| **Error Handling** | If the order modal does not appear within 5 seconds of clicking Place Order, test fails. If the confirmation dialog does not appear within 10 seconds of clicking Purchase, test fails with timeout. |

---

## Challenge 2 — API Test Automation

**Target:** [https://thinkingtester-contact-list.herokuapp.com/](https://thinkingtester-contact-list.herokuapp.com/)
**Framework:** RestAssured 5.5 + JUnit 5
**Coverage:** Full CRUD lifecycle + negative validation

### Design Decisions
- A **unique test user** is registered at runtime per test run using `System.currentTimeMillis()` as a suffix — prevents conflicts on a shared environment
- The user is registered, logged in, and the JWT token extracted — all within the test class setup (`@BeforeAll`)
- `contactId` is stored as a class-level variable, populated during the Create test and reused in subsequent Read → Update → Delete tests
- Tests are ordered using JUnit 5's `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)` to enforce CRUD sequence
- All response bodies are validated for both status code AND key field values — not just HTTP status

---

### TC-API-01 — Create Contact (POST /contacts)

| Field | Detail |
|---|---|
| **Intent** | Verify that an authenticated user can create a new contact record with all required and optional fields |
| **Pre-conditions** | Valid JWT token obtained from login; contact data is complete |
| **Test Steps** | 1. Send POST to `/contacts` with Authorization Bearer token <br> 2. Request body includes: firstName, lastName, birthdate, email, phone, street1, city, stateProvince, postalCode, country <br> 3. Capture `_id` from response |
| **Expected Result** | HTTP 201 Created; response body contains `_id` field; `firstName` and `email` match the request values |
| **Error Handling** | If response status is not 201, assertion fails with actual status code logged. If `_id` is absent from response, test fails with descriptive message. |

---

### TC-API-02 — Get Contact by ID (GET /contacts/:id)

| Field | Detail |
|---|---|
| **Intent** | Verify that a previously created contact can be retrieved by its unique ID and all fields match the creation payload |
| **Pre-conditions** | TC-API-01 has run successfully; `contactId` is populated |
| **Test Steps** | 1. Send GET to `/contacts/{contactId}` with Authorization Bearer token <br> 2. Parse response body |
| **Expected Result** | HTTP 200 OK; `_id` matches the stored `contactId`; `firstName`, `lastName`, and `email` match creation values |
| **Error Handling** | If `contactId` is null (prior test failed), test is skipped with a clear assumption failure message. |

---

### TC-API-03 — Update Contact (PUT /contacts/:id)

| Field | Detail |
|---|---|
| **Intent** | Verify that an existing contact's fields can be fully updated via a PUT request and the changes are persisted |
| **Pre-conditions** | TC-API-01 has run; valid `contactId` available |
| **Test Steps** | 1. Send PUT to `/contacts/{contactId}` with updated payload (changed lastName, email, phone) <br> 2. Parse response body |
| **Expected Result** | HTTP 200 OK; response body reflects the updated `lastName` and `email` values, confirming the update was applied |
| **Error Handling** | If response status is not 200, assertion fails with actual status and response body logged for diagnosis. |

---

### TC-API-04 — Delete Contact and Verify 404 (DELETE + GET /contacts/:id)

| Field | Detail |
|---|---|
| **Intent** | Verify that a contact can be deleted and is subsequently no longer retrievable — validating both the delete operation and the system's handling of a missing resource |
| **Pre-conditions** | TC-API-01 has run; valid `contactId` available |
| **Test Steps** | 1. Send DELETE to `/contacts/{contactId}` with Authorization Bearer token <br> 2. Verify delete response <br> 3. Send GET to `/contacts/{contactId}` <br> 4. Assert response indicates resource not found |
| **Expected Result** | DELETE returns HTTP 200; subsequent GET returns HTTP 404, confirming the contact no longer exists in the system |
| **Error Handling** | Both assertions are evaluated independently. If DELETE fails, a descriptive failure message is logged before the follow-up GET is attempted. |

---

## Notes on DemoBlaze (Challenge 1)

DemoBlaze is a shared public demo environment. A few considerations:

- **Shared cart state:** The cart persists for a browser session. Each test run starts fresh with a new ChromeDriver instance.
- **Alert timing:** The "Product added" alert can appear with a short delay — handled using `FluentWait` with `ExpectedConditions.alertIsPresent()`.
- **Network variability:** The Heroku-hosted site can be slow to respond. All waits use configurable explicit waits, not fixed sleeps.

## Notes on Contact List API (Challenge 2)

- The API is hosted on Heroku free tier — cold starts can add 5–10 seconds to the first request. Tests account for this with RestAssured's connection timeout configuration.
- Each test run creates a fresh user account. Old test accounts are not cleaned up (no admin delete endpoint available).
- JUnit 5's `@TestMethodOrder` ensures CRUD tests run in correct dependency order: Create → Read → Update → Delete.

---

*Submitted for Red River Mutual — QA Automation Lead Technical Assessment, April 2026*
