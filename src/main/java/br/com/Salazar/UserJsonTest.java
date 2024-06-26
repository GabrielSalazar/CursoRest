package br.com.Salazar;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    @Test
    public void deveVerificarPrimeiroNivel(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/1")
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("name", containsString("Silva"))
            .body("age", greaterThan(18));
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas(){
        Response response = RestAssured.request(Method.GET,"https://restapi.wcaquino.me/users/1");

        //path
        Assert.assertEquals(new Integer(1), response.path("id"));
        Assert.assertEquals(new Integer(1), response.path("%s","id"));

        //json path
        JsonPath jpath = new JsonPath(response.asString());
        Assert.assertEquals(1,jpath.getInt("id"));

        // from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1, id);
    }

    @Test
    public void deveVerificarSegundoNivel(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/2")
        .then()
            .statusCode(200)
            .body("name", containsString("Joaquina"))
            .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void deveVerificarLista(){
        given()
        .when()
            .get("https://restapi.wcaquino.me/users/3")
        .then()
            .statusCode(200)
            .body("name", containsString("Joaquina"))
            .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void deveVerificarLista2(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/3")
                .then()
                .statusCode(200)
                .body("name", containsString("Ana"))
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name", is("Luizinho"))
                .body("filhos.name", hasItems("Luizinho"))
                .body("filhos.name", hasItems("Luizinho","Zezinho"));

    }

    @Test
    public void deveRetornarErroUsuarioInexistente(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/4")
                .then()
                .statusCode(404)
                .body("error",is("Usuário inexistente"));
    }
    @Test
    public void deveVerificarListaRaiz(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$",hasSize(3))
                .body("name",hasItems("João da Silva", "Maria Joaquina","Ana Júlia"))
                .body("age[1]",is(25))
                .body("filhos.name", hasItem(Arrays.asList("Zezinho","Luizinho")))
                .body("salary",contains(1234.5678f,2500,null));
    }
    @Test
    public void deveFazerVerificacoesAvancadas(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$",hasSize(3))

                //findAll = retorna todos que ele encontrar na lista
                //find = retorna somente o primeiro

                //Verifica se a quantidade de registros entre todos os usuários da lista onde a idade é menor ou iguail a 25, sabendo que temos 2 usuarios
                .body("age.findAll{it <= 25}.size()",is(2))

                //Verifica se a quantidade de registros entre todos os usuários da lista onde
                // a idade é menor ou igual a 25 e maior que 20, sabendo que temos 1 usuario nessa condição
                .body("age.findAll{it <= 25 && it > 20}.size()",is(1))

                //Verifica se o nome dos registros entre todos os usuários da lista onde
                // a idade é menor ou igual a 25 e maior que 20, onde entre os nomes listados possuem "Maria Joaquina"
                .body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))

                //Verifica se o nome do primeiro registro da lista onde
                // a idade é menor ou igual a 25 e maior que 20, onde o nome é "Maria Joaquina"
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))

                //Verifica se o nome do último registro da lista onde
                // a idade é menor ou igual a 25, onde o nome é "Ana Júlia"
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))

                //Verifica se o primeiro registro entre toda a lista na condição de ter
                // a idade menor ou igual a 25, onde o nome é "Ana Júlia"
                .body("find{it.age <= 25}.name", is("Maria Joaquina"))


                //Verifica entre os registros de toda a lista na condição de ter
                // o nome que contenha a letra "n" como também os nomes "Ana Júlia" e "Maria Joaquina" estejam na lista
                .body("findAll{it.name.contains('n')}.name", hasItems("Ana Júlia","Maria Joaquina"))

                //Verifica entre os registros de toda a lista na condição de ter
                // os nomes que tenham mais que 10 caracteres como também os nomes "João da Silva" e "Maria Joaquina" estejam na lista
                .body("findAll{it.name.length() > 10 }.name", hasItems("João da Silva","Maria Joaquina"))

                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))

                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))

                //.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"),arrayWithSize(1)))
                .body("age.collect{it * 2}",hasItems(60,50,40))
                .body("id.max()",is(3))
                .body("salary.min()", is(1234.5678f))
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f,0.001)))
                .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d),lessThan(5000d)));
    }
    @Test
    public void devoUnirJsonPathComJAVA(){
        ArrayList<String> names =
         given()
         .when()
                .get("https://restapi.wcaquino.me/users")
         .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}");

         Assert.assertEquals(1,names.size());
         Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa Joaquina"));
         Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
    }
}
