# RRM Test Automation Challenges (Java)

## Tech Stack
- Java 17, Maven
- UI: Selenium WebDriver + JUnit 5
- API: RestAssured + JUnit 5

## How to Run
```bash
mvn test


Run only UI:
mvn -Dtest=DemoblazeUiTests test


Run only API:
mvn -Dtest=ContactsApiTests test

Challenge 1 — UI (Demoblaze)
Scenario 1: Add product to cart and verify cart contents
Steps: Open product → Add to cart → Accept alert → Open cart → Verify item in cart
Expected: “Samsung galaxy s6” is listed in cart

Scenario 2: Place order and verify confirmation
Steps: Add product → Cart → Place Order → Fill form → Purchase
Expected: Confirmation contains Id and Amount


Challenge 2 — API (Thinking Tester Contact List)
Scenario 1: Create contact → Get by id → Validate fields
Scenario 2: Update contact → Verify → Delete → Verify 404

Notes:

API tests create a unique user at runtime, login, then run CRUD operations using the token.

