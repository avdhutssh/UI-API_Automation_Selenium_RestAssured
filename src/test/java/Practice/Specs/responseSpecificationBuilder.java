package Practice.Specs;

import java.util.concurrent.TimeUnit;

import org.hamcrest.Matchers;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public class responseSpecificationBuilder {

	public static ResponseSpecification responseSpec() {
		return new ResponseSpecBuilder()
			.expectResponseTime(Matchers.lessThan(15000L), TimeUnit.MILLISECONDS)
			.build();
	}
	
}
