package Practice;

import org.testng.Assert;
import org.testng.annotations.Test;

import Practice.Constants.statusCodes;
import Practice.POJO.BookingDates;
import Practice.POJO.BookingRequest;
import io.restassured.response.Response;

public class _01_API_Test extends _00_BaseTest{

    @Test
    public void test_01_CreateBooking() {
        BookingDates dates = new BookingDates("2018-01-01", "2019-01-01");
        BookingRequest bookingRequest =  new BookingRequest("Avdhut", "Shirgaonkar", 111, true, dates, "Breakfast");
        
        Response response = factory.createBooking(endPointBooking, bookingRequest);
        
        System.out.println(response.asString());
        newBookingID = response.jsonPath().getString("bookingid");
        Assert.assertEquals(response.statusCode(), statusCodes.OK.getCode());
        Assert.assertTrue(response.statusLine().contains(statusCodes.OK.getMsg()));
        Assert.assertNotNull(newBookingID);
        Assert.assertEquals(response.jsonPath().getString("booking.firstname"), bookingRequest.getFirstname());

    }

    @Test
    public void test_02_GetBookingDetails() {
        Response response = factory.getBookingDetails(endPointBooking, newBookingID);
        BookingRequest retrievedBooking = response.as(BookingRequest.class);
        System.out.println("=====================================");
        System.out.println(response.asString());
        Assert.assertEquals(response.statusCode(), statusCodes.OK.getCode());
        Assert.assertTrue(response.statusLine().contains(statusCodes.OK.getMsg()));
        Assert.assertEquals(retrievedBooking.getFirstname(), "Avdhut");
        Assert.assertEquals(retrievedBooking.getLastname(), "Shirgaonkar");
        Assert.assertEquals(retrievedBooking.getTotalprice(), 111);
        Assert.assertTrue(retrievedBooking.isDepositpaid());
        Assert.assertEquals(retrievedBooking.getAdditionalneeds(), "Breakfast");
        
    }
}
