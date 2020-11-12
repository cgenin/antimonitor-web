package fr.genin.christophe.antimonitor.web;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest

@QuarkusTestResource(DatabaseResource.class)
public class WebAppsResourceTest {


    @Test
    public void testHealthEndpoint() {
        given()
                .when().get("/api/_health")
                .then()
                .statusCode(200);
    }

    @Test
    public void testindex() {
        given()
                .when().get("/")
                .then()
                .statusCode(200);
    }

}