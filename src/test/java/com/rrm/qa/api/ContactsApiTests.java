package com.rrm.qa.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactsApiTests {
    private static final String BASE_URL = "https://thinking-tester-contact-list.herokuapp.com";
    private static String token;       // Bearer token
    private static String contactId;   // created contact id

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;

        // 1) Sign up a unique user (safe even if DB is purged)
        String email = "viral.rrm." + System.currentTimeMillis() + "@example.com";
        String password = "Password123!";

        Map<String, Object> signupBody = new HashMap<>();
        signupBody.put("firstName", "Viral");
        signupBody.put("lastName", "Joshi");
        signupBody.put("email", email);
        signupBody.put("password", password);

        // POST /users  -> create user
        given()
            .contentType(ContentType.JSON)
            .body(signupBody)
        .when()
            .post("/users")
        .then()
            .statusCode(anyOf(is(201), is(200))); // API typically returns 201 on create

        // 2) Login to get token
        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", password);

        token =
            given()
                .contentType(ContentType.JSON)
                .body(loginBody)
            .when()
                .post("/users/login")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");

        // token is returned without "Bearer " prefix; we add it in headers later
    }

    @Test
    @Order(1)
    void api_createContact_thenGetById_andValidateFields() {
        // Intent: Validate simple CREATE + READ (GET by id)
        // Preconditions: Authorized token available

        Map<String, Object> createContact = new HashMap<>();
        createContact.put("firstName", "Test");
        createContact.put("lastName", "Contact");
        createContact.put("birthdate", "1994-01-01");
        createContact.put("email", "contact." + System.currentTimeMillis() + "@example.com");
        createContact.put("phone", "5195024375");
        createContact.put("street1", "1 Main St");
        createContact.put("city", "Toronto");
        createContact.put("province", "ON");
        createContact.put("postalCode", "M1M1M1");
        createContact.put("country", "Canada");

        // CREATE
        contactId =
            given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(createContact)
            .when()
                .post("/contacts")
            .then()
                .statusCode(anyOf(is(201), is(200)))
                .body("_id", notNullValue())
                .body("firstName", equalTo("Test"))
                .body("lastName", equalTo("Contact"))
                .extract()
                .path("_id");

        // READ (GET by id)
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/contacts/" + contactId)
        .then()
            .statusCode(200)
            .body("_id", equalTo(contactId))
            .body("firstName", equalTo("Test"))
            .body("lastName", equalTo("Contact"));
    }

    @Test
    @Order(2)
    void api_updateContact_thenDeleteContact_andVerifyNotFound() {
        // Intent: Validate UPDATE + DELETE + negative GET after delete
        // Preconditions: Contact created in prior test

        Assertions.assertNotNull(contactId, "contactId is null. Run create test first.");

        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("lastName", "Updated");

        // UPDATE (PATCH)
        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .patch("/contacts/" + contactId)
        .then()
            .statusCode(200)
            .body("lastName", equalTo("Updated"));

        // READ to verify
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/contacts/" + contactId)
        .then()
            .statusCode(200)
            .body("lastName", equalTo("Updated"));

        // DELETE
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/contacts/" + contactId)
        .then()
            .statusCode(anyOf(is(200), is(204)));

        // NEGATIVE READ after delete -> should be 404
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/contacts/" + contactId)
        .then()
            .statusCode(404);
    }
    
}
