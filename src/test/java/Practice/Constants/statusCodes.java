package Practice.Constants;

public enum statusCodes {
	OK(200, "OK"),
	CREATED(201, "CREATED");
	
	private final int code;
	private final String msg;
	
	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	statusCodes(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
