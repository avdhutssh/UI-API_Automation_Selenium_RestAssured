package Practice;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import Practice.Factory.restFactory;
import io.restassured.RestAssured;

public class _00_BaseTest {

    String newBookingID;
    restFactory factory;
    String endPointBooking = "/booking";

    @BeforeSuite
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        factory = new restFactory();
    }
    
    @AfterSuite
    public void teardown() {
    	RestAssured.reset();
    }
    
}
