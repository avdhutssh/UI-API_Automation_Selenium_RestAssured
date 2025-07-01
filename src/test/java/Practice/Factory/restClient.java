package Practice.Factory;

import static io.restassured.RestAssured.given;

import Practice.Specs.requestSpecificationBuilder;
import Practice.Specs.responseSpecificationBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class restClient {

	private static RequestSpecification requestSpec= requestSpecificationBuilder.requestSpec();
	private static ResponseSpecification respSpec= responseSpecificationBuilder.responseSpec();

	public Response doGet(String endpoint, String pathParam) {
		Response response = given()
        .pathParam("id", pathParam)
        .spec(requestSpec)
        .when()
        .get(endpoint+"/{id}")
        .then()
        .spec(respSpec)
        .extract().response();
		
		return response;
	}
	
	
	public Response doCreate(String endpoint, Object requestPayload) {
		Response response = given()
				.spec(requestSpec)
                .body(requestPayload)
                .when()
                .post(endpoint)
                .then()
                .spec(respSpec)
                .extract().response();
		
		return response;
	}

}
