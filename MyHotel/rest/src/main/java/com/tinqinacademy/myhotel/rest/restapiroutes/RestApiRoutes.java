package com.tinqinacademy.myhotel.rest.restapiroutes;

public class RestApiRoutes {
    private final static String API = "/api/v1";
    public final static String API_HOTEL = API + "/hotel";
    public final static String API_SYSTEM = API + "/system";

    public final static String CHECK_AVAILABILITY = API_HOTEL + "/rooms";
    public final static String RETRIEVE_BASIC_INFO = API_HOTEL + "/{roomId}";
    public final static String BOOK_ROOM = API_HOTEL + "/{roomId}";
    public final static String DELETE_RESERVATION = API_HOTEL + "/{bookingId}";

    public final static String REGISTER_NEW_GUEST = API_SYSTEM + "/register";
    public final static String RENTER_OCCUPANCIES = API_SYSTEM + "/register";
    public final static String CREATE_ROOM = API_SYSTEM + "/room";
    public final static String UPDATE_ROOM = CREATE_ROOM + "/{roomId}";
    public final static String REMOVE_ROOM = CREATE_ROOM + "/{roomId}";

}
