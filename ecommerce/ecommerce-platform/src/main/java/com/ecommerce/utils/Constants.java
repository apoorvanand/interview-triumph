public class Constants {
    public static final String USER_ROLE_ADMIN = "ADMIN";
    public static final String USER_ROLE_CUSTOMER = "CUSTOMER";
    public static final String USER_ROLE_SELLER = "SELLER";

    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";

    public static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";

    public static final String DEFAULT_CURRENCY = "USD";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}