package Practice.Specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class requestSpecificationBuilder {
	
	public static RequestSpecification requestSpec() {
		return new RequestSpecBuilder()
			.setContentType(ContentType.JSON)
			.log(LogDetail.ALL)
			.build();
	}

}
