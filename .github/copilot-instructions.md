# Copilot Instructions for rrm-automation-challenges

## What this project is
- Java Maven project focused on automation test challenges.
- Main source is minimal; the important code is under `src/test/java/com/rrm/qa/ui/DemoblazeUiTests.java`.
- The repository currently uses JUnit 5 and Selenium for UI automation, with RestAssured dependencies present for API tests.

## Primary developer workflows
- Build and run tests with Maven:
  - `mvn test`
  - `mvn -Dtest=DemoblazeUiTests test`
- There is no separate application server or production app in this repo; `src/main/java/com/rrm/qa/App.java` is placeholder code.

## Key project conventions
- Tests use JUnit Jupiter annotations: `@BeforeEach`, `@AfterEach`, `@Test`.
- Selenium WebDriver state is created per test in `setUp()` and torn down in `tearDown()`.
- The existing UI test is explicit and procedural: navigate to `https://www.demoblaze.com/`, click a product, add to cart, assert alert text, then verify the cart contents.
- Assertions are standard JUnit assertions like `assertTrue(...)` with clear message expectations.

## Patterns and files to follow
- `src/test/java/com/rrm/qa/ui/DemoblazeUiTests.java` is the canonical example for UI automation patterns.
- `pom.xml` defines the project as a test-focused Maven module with test-scoped Selenium and RestAssured dependencies.
- Do not assume there is a Spring, web framework, or service orchestration layer in this repo.

## What to avoid
- Avoid adding generic application architecture guidance; this repository is primarily a test harness.
- Do not invent service boundaries or unimplemented API layers beyond the existing Selenium UI test.
- Do not assume an IDE-specific configuration; use Maven commands and standard JUnit/Selenium conventions.

## Useful details for code generation
- Add new tests under `src/test/java/com/rrm/qa/...` following JUnit 5 style.
- If adding page interactions, keep locators and wait logic explicit and robust with `WebDriverWait`.
- Keep browser setup simple: `new ChromeDriver()` and `driver.manage().window().setSize(new Dimension(1400, 900));` as the existing pattern.

## Areas for future expansion
- API automation is expected but not yet implemented; `pom.xml` already includes `rest-assured` and `json-path`.
- If adding API tests, place them under `src/test/java/com/rrm/qa/api` and use RestAssured in a JUnit 5 test class.
