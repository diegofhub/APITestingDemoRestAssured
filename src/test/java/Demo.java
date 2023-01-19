import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

import com.github.dzieciou.testing.curl.CurlLoggingRestAssuredConfigFactory;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.json.JSONObject;
import org.testng.annotations.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.io.File;

public class Demo {

    public Demo() {
        baseURI = "https://petstore.swagger.io/v2/";
        RestAssuredConfig config = CurlLoggingRestAssuredConfigFactory.createConfig();
    }

    //We must use the TestNG @Test Annotation to define the method as a test


    //////////////////////////////////    //////////////////////////////////    //////////////////////////////////
    //        ____  ____________    //    //        ____  ____________    //    //        ____  ____________    //
    //       / __ \/ ____/_  __/    //    //       / __ \/ ____/_  __/    //    //       / __ \/ ____/_  __/    //
    //      / /_/ / __/   / /       //    //      / /_/ / __/   / /       //    //      / /_/ / __/   / /       //
    //     / ____/ /___  / /        //    //     / ____/ /___  / /        //    //     / ____/ /___  / /        //
    //    /_/   /_____/ /_/         //    //    /_/   /_____/ /_/         //    //    /_/   /_____/ /_/         //
    //////////////////////////////////    //////////////////////////////////    //////////////////////////////////


    // 1. POST /pet - Add a new pet to the store

    @Test
    public void testPostPet() {

        RestAssuredConfig config = CurlLoggingRestAssuredConfigFactory.createConfig();

        String json = Json.createObjectBuilder()
                .add("id", 69420)
                .add("category", Json.createObjectBuilder()
                        .add("id", 0)
                        .add("name", "string"))
                .add("name", "Tobby")
                .add("photoUrls", Json.createArrayBuilder()
                        .add("https://dog.ceo/api/breeds/image/random"))
                .add("tags", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("id", 0)
                                .add("name", "string")))
                .add("status", "available")
                .build()
                .toString();

        System.out.println(json);

        given()
                .config(config)
                .header("Content-Type","application/json" )
                .header("Accept","application/json")
                .body(json)
                .when()
                .post("/pet")
                .then()
                .statusCode(200);

        /*
        String test = "{\n" +
                "  \"id\": 0,\n" +
                "  \"category\": {\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"string\"\n" +
                "  },\n" +
                "  \"name\": \"doggie\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
         */

    }

    // 2. PUT /pet - Update an existing pet

    //Add a new pet to the store
    @Test(dependsOnMethods={"testPostPet"})
    public void testPutPet() {

        RestAssuredConfig config = CurlLoggingRestAssuredConfigFactory.createConfig();

        String json = Json.createObjectBuilder()
                .add("id", 69420)
                .add("category", Json.createObjectBuilder()
                        .add("id", 0)
                        .add("name", "string"))
                .add("name", "TobbyTheDoggy")
                .add("photoUrls", Json.createArrayBuilder()
                        .add("https://dog.ceo/api/breeds/image/random"))
                .add("tags", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("id", 0)
                                .add("name", "string")))
                .add("status", "available")
                .build()
                .toString();

        System.out.println(json);

        given()
                .config(config)
                .header("Content-Type","application/json" )
                .header("Accept","application/json")
                .body(json)
                .when()
                .put("/pet")
                .then()
                .statusCode(200);

    }

    // 3. GET /pet/findByStatus - Finds pet by Available status

    //Finds Pets by status
    @Test
    public void testGetPetsByAvailableStatus() {

        String availablePets = given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200).extract().body().asString();

        System.out.println(availablePets);

    }

    // 4. GET /pet/findByStatus - Finds pet by Pending status

    @Test
    public void testGetPetsByPendingStatus() {

        String availablePets = given()
                .queryParam("status", "pending")
                .when()
                .get("/pet/findByStatus")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200).extract().body().asString();

        System.out.println(availablePets);

    }

    // 5. GET /pet/findByStatus - Finds pet by Sold status

    @Test
    public void testGetPetsBySoldStatus() {

        String availablePets = given()
                .queryParam("status", "sold")
                .when()
                .get("/pet/findByStatus")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200).extract().body().asString();

        System.out.println(availablePets);

    }

    // 6. GET /pet/{petId} - Find pet by ID

    @Test(dependsOnMethods={"testPostPet"})
    public void testGetPetById() {

        String body = given()
                .when()
                .get("/pet/69420")
                .then()
                .body("id", equalTo(69420))
                .statusCode(200)
                .extract().response().body().asString();

        System.out.println(body);
    }

    // 7. POST /pet/{petId} - Updates a pet in the store with form data

    @Test(dependsOnMethods={"testPostPet"})
    public void testPostUpdatePetById() {

        String body = given()
                .when()
                .queryParams("name", "Flipper", "status", "sold")
                .post("/pet/69420")
                .then()
                .statusCode(200)
                .extract().body().asString();

        System.out.println(body);
    }

    // 7. DELETE /pet/{petId} - Deletes a pet

    @Test(dependsOnMethods={"testPostPet", "testGetPetById", "testPostUpdatePetById"})
    public void testDeletePetById() {

        given()
                .header("api_key", "admin")
                .when()
                .delete("/pet/69420")
                .then()
                .statusCode(200);
    }

    // 8. POST /pet/{petId}/uploadImage - Uploads an image

    @Test(dependsOnMethods={"testPostPet"})
    public void testPostUploadPetPicture() {

        given()
                .multiPart("file", new File("dog.jpg"))
                .when()
                .post("pet/69420/uploadImage")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200);
    }

    /////////////////////////////////////////////    /////////////////////////////////////////////    /////////////////////////////////////////////
    //       _______________  ____  ______    //     //       _______________  ____  ______     //    //       _______________  ____  ______     //
    //      / ___/_  __/ __ \/ __ \/ ____/     //    //      / ___/_  __/ __ \/ __ \/ ____/     //    //      / ___/_  __/ __ \/ __ \/ ____/     //
    //      \__ \ / / / / / / /_/ / __/        //    //      \__ \ / / / / / / /_/ / __/        //    //      \__ \ / / / / / / /_/ / __/        //
    //     ___/ // / / /_/ / _, _/ /___        //    //     ___/ // / / /_/ / _, _/ /___        //    //     ___/ // / / /_/ / _, _/ /___        //
    //    /____//_/  \____/_/ |_/_____/        //    //    /____//_/  \____/_/ |_/_____/        //    //    /____//_/  \____/_/ |_/_____/        //
    //                                         //    //                                         //    //                                         //
    /////////////////////////////////////////////    /////////////////////////////////////////////    /////////////////////////////////////////////


    // 9. GET /order/{orderId} - Find order by ID

    @Test(dependsOnMethods={"testPostOrder"})
    public void testGetOrderById() {

        int orderId = 1;

        String body = given()
                .when()
                .get("store/order/" + orderId)
                .then()
                //.body("id", equalTo(orderId))
                .statusCode(200)
                .extract().response().body().asString();

        System.out.println(body);
    }

    // 10. DELETE /store/order/{orderId} - Deletes an order

    @Test(dependsOnMethods={"testPostOrder", "testGetOrderById"})
    public void testDeleteOrderById() {

        int orderId = 1;

        given()
            .when()
            .delete("/store/order/" + orderId)
            .then()
            .statusCode(200);
    }

    // 12. POST store/order/ - Place an order for a pet

    @Test
    public void testPostOrder() {

        String json = Json.createObjectBuilder()
                .add("id", 1)
                .add("petId", 0)
                .add("quantity", 0)
                .add("shipDate", "2023-01-17T00:07:17.454Z")
                .add("status", "placed")
                .add("complete", true)
                .build()
                .toString();

        String body =
                given()
                    .header("Content-Type","application/json" )
                    .header("Accept","application/json")
                    .body(json)
                    .when()
                    .post("/store/order")
                    .then()
                    .statusCode(200)
                    .extract().response().body().asString();

        System.out.println(body);
    }

    // 13. GET store/inventory - Returns pet inventories by status

    @Test
    public void testGetStoreInventory() {

        // in this first example, we will start with a simple GET to the store inventory.
        // then, we will expect a status code 200
        // and extract the response body and save it as a String

        String getStoreInventoryResponseBody =
                given()
                    .when()
                    .get("store/inventory")
                        .then()
                            .statusCode(200)
                            .extract()
                            .body().asString();

        System.out.println(getStoreInventoryResponseBody);

        // with .given() we create the context for the test we are going to execute,
        // that is, we specify the information we are going to send with the HTTP Request
        // such ass: headers, body, route...

        // with .when() we specify the HTTP verb and the endpoint

        // we use .get() for the GET HTTP verb
        // we pass the endpoint int .get() as a parameter

    }


    //TODO
    //@BeforeMethod
    //https://www.youtube.com/watch?v=Y2ceK8n3dN0


    //////////////////////////////////////    //////////////////////////////////////    //////////////////////////////////////
    //       __  _______ __________     //    //       __  _______ __________     //    //       __  _______ __________     //
    //      / / / / ___// ____/ __ \    //    //      / / / / ___// ____/ __ \    //    //      / / / / ___// ____/ __ \    //
    //     / / / /\__ \/ __/ / /_/ /    //    //     / / / /\__ \/ __/ / /_/ /    //    //     / / / /\__ \/ __/ / /_/ /    //
    //    / /_/ /___/ / /___/ _, _/     //    //    / /_/ /___/ / /___/ _, _/     //    //    / /_/ /___/ / /___/ _, _/     //
    //    \____//____/_____/_/ |_|      //    //    \____//____/_____/_/ |_|      //    //    \____//____/_____/_/ |_|      //
    //                                  //    //                                  //    //                                  //
    //////////////////////////////////////    //////////////////////////////////////    //////////////////////////////////////


    // 14.
    //Add a new pet to the store
    @Test
    public void testPostUser() {

        // now, we will send a POST request to create a new user.

        // for this, we need to create a Json with the necessary parameters
        // we will use java.util.Map

        JSONObject user = new JSONObject();
        user.put("id", 1);
        user.put("username", "dieg0");
        user.put("firstName", "user");
        user.put("lastName", "user");
        user.put("email", "user@gmail.com");
        user.put("password", "user");
        user.put("phone", "123456789");
        user.put("userStatus", 0);

        /*
        String test = "{\n" +
                "  \"id\": 0,\n" +
                "  \"username\": \"string\",\n" +
                "  \"firstName\": \"string\",\n" +
                "  \"lastName\": \"string\",\n" +
                "  \"email\": \"string\",\n" +
                "  \"password\": \"string\",\n" +
                "  \"phone\": \"string\",\n" +
                "  \"userStatus\": 0\n" +
                "}";
         */

        // our request:
        given()
            .header("Content-Type","application/json" )
            .header("Accept","application/json")
            .body(user.toString())
        .when()
            .post("/user")
        .then()
            .statusCode(200);

    }

    @Test(dependsOnMethods={"testPostPet"})
    public void testPutUser() {

        int userId = 1;

        JSONObject json = new JSONObject();
        json.put("id", userId);
        json.put("username", "dieg0");
        json.put("firstName", "dieguito");
        json.put("lastName", "user");
        json.put("email", "user@gmail.com");
        json.put("password", "user");
        json.put("phone", "123456789");
        json.put("userStatus", 0);

        given()
                .header("Content-Type","application/json" )
                .header("Accept","application/json")
                .body(json.toString())
                .when()
                .put("/user/" + userId)
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods={"testPostUser", "testPutPet", "testGetUserLogin", "testGetUserByUsername"})
    public void testDeleteUserByUsername() {

        String username = "dieg0";

        given()
                .when()
                .delete("/user/" + username)
                .then()
                .statusCode(200);
    }

    //Get user by user name
    @Test(dependsOnMethods={"testPostUser"})
    public void testGetUserByUsername() {

        String getUserResponseBody = given()
                                                .when()
                                                    .get("user/string")
                                                .then()
                                                    .statusCode(200).extract().body().asString();

        System.out.println(getUserResponseBody);
    }

    @Test(dependsOnMethods={"testPostUser"})
    public void testGetUserLogin() {

        String username = "dieg0";
        String password = "user";

        String output = given()
                            .when()
                            .queryParams("username", username, "password", password)
                            .get("user/login")
                            .then()
                            .statusCode(200).extract().body().asString();

        System.out.println(output);
    }

    //Get user by user name
    @Test
    public void testGetUserLogout() {

        given()
            .when()
            .get("user/logout")
            .then()
            .statusCode(200);
    }

    //Get user by user name
    @Test
    public void testPostUserWithArray() {

        JsonObject json = Json.createObjectBuilder()
                .add("id", 1)
                .add("username", "pepe")
                .add("firstName", "string")
                .add("lastName", "string")
                .add("email", "string")
                .add("password", "string")
                .add("phone", "string")
                .add("userStatus", 0)
                .build();

        JsonObject json2 = Json.createObjectBuilder()
                .add("id", 123456789)
                .add("username", "juan")
                .add("firstName", "string")
                .add("lastName", "string")
                .add("email", "string")
                .add("password", "string")
                .add("phone", "string")
                .add("userStatus", 0)
                .build();

        JsonArrayBuilder builder = Json.createArrayBuilder();
            builder.add(json);
            builder.add(json2);

        JsonArray jsonArray = builder.build();

        String jsonArrayString = jsonArray.toString();
        System.out.println(jsonArrayString);

        String responseBody = given()
                                .header("Content-Type","application/json" )
                                .header("Accept","application/json")
                                .body(jsonArrayString)
                                .when()
                                    .post("/user/createWithArray")
                                    .then()
                                        .statusCode(200)
                                        .extract().response().toString();

        System.out.println(responseBody);

    }

    @Test
    public void testPostUserWithList() {

        JsonObject json = Json.createObjectBuilder()
                .add("id", 1)
                .add("username", "pepe")
                .add("firstName", "string")
                .add("lastName", "string")
                .add("email", "string")
                .add("password", "string")
                .add("phone", "string")
                .add("userStatus", 0)
                .build();

        JsonObject json2 = Json.createObjectBuilder()
                .add("id", 123456789)
                .add("username", "juan")
                .add("firstName", "string")
                .add("lastName", "string")
                .add("email", "string")
                .add("password", "string")
                .add("phone", "string")
                .add("userStatus", 0)
                .build();

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(json);
        builder.add(json2);

        JsonArray jsonArray = builder.build();

        String jsonArrayString = jsonArray.toString();
        System.out.println(jsonArrayString);

        String responseBody = given()
                .header("Content-Type","application/json" )
                .header("Accept","application/json")
                .body(jsonArrayString)
                .when()
                .post("/user/createWithList")
                .then()
                .statusCode(200)
                .extract().response().toString();

        System.out.println(responseBody);

    }

    /*
    @Test
    public void testDocuments() {
        // Test post a document.
        File myFile = new File("myFile");
        given()
                .multiPart("file", new File(myFile))
                .expect().statusCode(201).when()
                .post(HOST + "/documents");
    }

     */



/*
    @Test(enabled=true)
    public void sampleAPITest() {

        RestAssuredConfig config = CurlLoggingRestAssuredConfigFactory.createConfig();

        given().config(config)
                .when()
                .get("https://dog.ceo/api/breeds/image/random")
                .then()
                .assertThat()
                .statusCode(200);
    }
 */

}


/*
    @Test
    void createfile() {
        try {
            Writer file = new FileWriter("", false);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
*/