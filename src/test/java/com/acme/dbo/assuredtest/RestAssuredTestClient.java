package com.acme.dbo.assuredtest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;


public class RestAssuredTestClient {
    RequestSpecification request;
    private Response response;

    @BeforeEach
    public void init() {
        request =
                given()
                    .baseUri("http://localhost")
                    .port(8080)
                    .basePath("/dbo/api/")
                    .header("X-API-VERSION", 1)
                    .contentType(ContentType.JSON);
    }


    @Test
    @DisplayName("Проверка сценария GET client by Exist id")
    public void shouldGetClientByExistId() {
        request.when()
            .get("/client/{id}", 2)
            .then()
            .statusCode(SC_OK)
            .body("id", is(2),
            "login", is("account@acme.com"));
    }


    @Test
    @DisplayName("Проверка сценария DELETE client by Login")
    public void shouldDeleteClientByLogin() {
        request
            .when()
                .body("{\n" +
                " \"login\": \"203390@email.com\",\n" +
                " \"salt\": \"some-salt\",\n" +
                " \"secret\": \"749f09bade8aca749f09bade8aca7556\"\n" +
                "}")
                .post("/client")
            .then().statusCode(SC_CREATED);

        request.when()
                .delete("/client/login/{login}", "203390@email.com")
                .then()
                .statusCode(SC_OK);
    }


    @Test
    @DisplayName("Проверка сценария GET client by Not Exist id")
    public void shouldGetClientByNotExistId() {
        request.when()
                .get("/client/{id}", 5676)
                .then()
                .statusCode(SC_NOT_FOUND);
    }


    @Test
    @DisplayName("Проверка сценария DELETE client by Not Exist id")
    public void shouldDeleteClientByNotExistId() {
        request.when()
                .delete("/client/{id}", 85555)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Проверка сценария GET client by Exist id")
    public void shouldGetClientByExistId2() throws NoSuchMethodException {
        response = request.when()
                .get("/client/{id}", 2);
        System.out.println(response.jsonPath().getString("login"));
    }
}
