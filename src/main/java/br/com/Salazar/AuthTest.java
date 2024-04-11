package br.com.Salazar;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class AuthTest {

    @Test
    public void deveAcessarSWAPI(){
        given()
            .log().all()
        .when()
            .get("https://swapi.dev/api/people/1/")
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is("Luke Skywalker"));
    }
    @Test
    public void deveObterClima(){
        given()
                .log().all()
                .queryParam("q", "Gravatai,BR")
                .queryParam("appid", "e900512117cf7b204cb41894de60a121")
                .queryParam("units", "metric")
                .when()
                .get("https://api.openweathermap.org/data/2.5/weather")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is("Gravataí"))
                .body("coord.lon", is(-50.9919f))
                .body("coord.lat", is(-29.9444f))
                .body("main.temp", greaterThan(15f))
        ;
    }

}
