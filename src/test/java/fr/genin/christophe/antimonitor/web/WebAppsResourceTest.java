package fr.genin.christophe.antimonitor.web;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.vertx.mutiny.pgclient.PgPool;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest

@QuarkusTestResource(DatabaseResource.class)
public class WebAppsResourceTest {



    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/health")
          .then()
             .statusCode(200)
             .body(is("{\"up\":true}"));
    }

}