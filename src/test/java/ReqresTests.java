import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static io.restassured.module.jsv.JsonSchemaValidator.*;

public class ReqresTests {

    @Test
    public void singleUserGetRequestCheckStatusCode200() {
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .when()
                .get("/api/users/2")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK);
    }


    @Test
    public void singleUserGetCheckResponseJsonBody() {
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .when()
                .get("/api/users/2")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id", is(2))
                .body("data.email", is("janet.weaver@reqres.in"))
                .body("data.first_name", is("Janet"))
                .body("data.last_name", is("Weaver"))
                .body("data.avatar", is("https://reqres.in/img/faces/2-image.jpg"))
                .body("support.url", is("https://reqres.in/#support-heading"))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    public void singleUserNotFound() {
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .when()
                .get("/api/users/23")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void listUsersGet() {
        List<Users> users = RestAssured.given()
                .spec(Specifications.requestSpecification())
                .when()
                .get("/api/users?page=2")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().body().jsonPath().getList("data", Users.class);

        // у user 8 "first_name": "Lindsay"
        users.stream().filter(el -> el.getId() == 8).forEach(el -> Assert.assertEquals(el.getFirst_name(), "Lindsay"));


        // у user 12 "last_name": "Howell"
        users.stream().filter(el -> el.getId() == 12).forEach(el -> Assert.assertEquals(el.getLast_name(), "Howell"));

        // у всех users "email" оканчивается на @reqres.in
        users.forEach(el -> Assert.assertTrue(el.getEmail().contains("@reqres.in")));


        // у всех users "avatar" содержат свой id
        users.forEach(el -> Assert.assertTrue(el.getAvatar().contains(el.getId().toString())));

        // содержит ли поле "email" имя "first_name"
        users.forEach(el -> Assert.assertTrue(el.getEmail().toLowerCase().contains(el.getFirst_name().toLowerCase())));

//        // найти элемент с  "last_name": "Edwards", и проверить чтобы имя у элемента было  "first_name": "George"
        users.stream().filter(el -> el.getLast_name().equals("Edwards")).forEach(el -> Assert.assertEquals(el.getFirst_name(), "George"));
    }

    @Test
    public void createUserPost1() {
        String userJob = FileUtil.readStringFromFile("./src/main/resources/For_create.txt");
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body(userJob)
                .when()
                .post("/api/users")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_CREATED)
                .body("name", notNullValue())
                .body("job", notNullValue());
    }

    @Test
    public void createUserPost2() {
        UserJob userJob = new UserJob("morpheus", "leader");
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body(userJob)
                .when()
                .post("/api/users")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_CREATED)
                .body("name", is(userJob.getName()))
                .body("job", is(userJob.getJob()));
    }

    @Test
    public void registerSuccessfulPost1() {
        String registerSuccessfull = FileUtil.readStringFromFile("./src/main/resources/For_register.txt");
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body(registerSuccessfull)
                .when()
                .post("/api/register")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void registerSuccessfulPost2() {
        RegisterSuccessfull registerSuccessfull = new RegisterSuccessfull("eve.holt@reqres.in", "pistol");
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body(registerSuccessfull)
                .when()
                .post("/api/register")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue())
                .body("token", notNullValue());
    }


    @Test
    public void loginSuccessfulPost1() {
        String dataUser = FileUtil.readStringFromFile("./src/main/resources/For_login.txt");
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body(dataUser)
                .when()
                .post("/api/login")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue());
    }

    @Test
    public void loginSuccessfulPost2() {
        Login login = new Login("eve.holt@reqres.in", "cityslicka");
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body(login)
                .when()
                .post("/api/login")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK)
                .body("token", notNullValue());
    }


    @Test
    public void listResourceGet() {
        List<ListResourcePojo> resourcePojoList = RestAssured.given()
                .spec(Specifications.requestSpecification())
                .when()
                .get("/api/unknown")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK)
                .body("data.id", notNullValue())
                .body("data.name", notNullValue())
                .body("data.year", notNullValue())
                .body("data.color", notNullValue())
                .body("data.pantone_value", notNullValue())
                .extract().body().jsonPath().getList("data", ListResourcePojo.class);

        // проверить что у пользователя с  "id": 3, было "name": "true red"
        resourcePojoList.stream().filter(el -> el.getId() == 3).forEach(el -> Assert.assertEquals(el.getName(), "true red"));

        // проверить, что поле "color" содержало #
        resourcePojoList.forEach(el -> Assert.assertTrue(el.getColor().contains("#")));

        // проверить что поле "pantone_value" содержало -
        resourcePojoList.forEach(el -> Assert.assertTrue(el.getPantone_value().contains("-")));

        // проверить что у пользователя с  "year": 2005 было  "id": 6
        resourcePojoList.stream().filter(el -> el.getYear() == 2005).forEach(el -> Assert.assertEquals(el.getId().toString(), "6"));

    }

    @Test
    public void updatePut() {
        UserJob updateUser = new UserJob("Sergey", "automated tester Java");
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body(updateUser)
                .when()
                .put("/api/users/2")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK)
                .body("name", notNullValue())
                .body("job", notNullValue());
    }

    @Test
    public void userDelete() {
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .when()
                .delete("/api/users/2")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_NO_CONTENT);

    }

    @Test
    public void loginUnsuccessfulPost() {
        RestAssured.given()
                .spec(Specifications.requestSpecification())
                .body("{\"email\": \"peter@klaven\"}")
                .when()
                .post("/api/login")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", is("Missing password"));

    }
}

