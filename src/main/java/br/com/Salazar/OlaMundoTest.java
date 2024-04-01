package br.com.Salazar;

import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import static io.restassured.RestAssured.*;

public class OlaMundoTest {

    @Test
    public void testeOlaMundo(){
        Response response = request(Method.GET,"https://restapi.wcaquino.me/ola");
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertEquals(201,response.statusCode());

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured(){
        Response response = request(Method.GET,"https://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        get("https://restapi.wcaquino.me/ola").then().statusCode(200);
        given()
        .when()
            .get("https://restapi.wcaquino.me/ola")
        .then()
             .statusCode(200);
    }
    @Test
    public void devoConhecerMatchersHamcrest(){
        Assert.assertThat("Maria", Matchers.is("Maria"));
    }
}
