package Practice.Factory;

import io.restassured.response.Response;

public class restFactory {
	
	restClient client = new restClient();
	
	public Response createBooking(String endpoint, Object requestPayload) {
		return client.doCreate(endpoint, requestPayload);
	}

	public Response getBookingDetails(String endpoint, String bookingId) {
		return client.doGet(endpoint, bookingId);
	}
}
