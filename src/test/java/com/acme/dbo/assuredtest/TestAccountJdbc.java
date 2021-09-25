package com.acme.dbo.assuredtest;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class TestAccountJdbc {
    RequestSpecification request;
    private Connection connection;

    @BeforeEach
    public void setConnect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby://localhost/dbo-db");
    }

    @AfterEach
    public void closeConnect() throws SQLException {
        connection.close();
    }

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

    @Disabled
    @Test
    @DisplayName("Проверка сценария GET account")
    public void shouldGetAccount() throws SQLException {
        int newAccountId;
        try (final PreparedStatement newAccount = connection.prepareStatement("INSERT INTO ACCOUNT(AMOUNT, CLIENT_ID, CREATE_STAMP) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            newAccount.setDouble(1, 4000);
            newAccount.setInt(2, 2);
            newAccount.setTimestamp(3, Timestamp.from(Instant.now()));

            assumeTrue(newAccount.executeUpdate() == 1);

            try (final ResultSet generatedKeys = newAccount.getGeneratedKeys()) {
                assumeTrue(generatedKeys.next());
                newAccountId = generatedKeys.getInt(1);
            }
        }

        int countAccount;
        try (final PreparedStatement countAccounts = connection.prepareStatement("SELECT COUNT(*) FROM ACCOUNT");
             final ResultSet resultSet = countAccounts.executeQuery()) {
            assumeTrue(resultSet.next());
            countAccount = resultSet.getInt(1);
        }

        try {
            request.when()
                    .get("/account/")
                    .then()
                    .statusCode(SC_OK)
                    .body("size()", is(countAccount), "id", hasItem(newAccountId));
        } finally {
            try (final PreparedStatement deleteAccount = connection.prepareStatement("DELETE FROM ACCOUNT WHERE ID=?")) {
                deleteAccount.setInt(1, newAccountId);
                assumeTrue(deleteAccount.executeUpdate() == 1);
            }
        }
    }
}
