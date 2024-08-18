package com.tinqinacademy.myhotel.api.restapiroutes;

public class RestApiRoutes {
    private final static String ROOT = "/api/v1";
    public final static String HOTEL = ROOT + "/hotel";
    public final static String SYSTEM = ROOT + "/system";
    public static final String AUTH = ROOT + "/auth";

    public static final String ROOM_ID = "/{roomId}";
    public static final String ROOM = "/room";
    public static final String REGISTER = "/register";
    public static final String BOOKING_ID = "/{bookingId}";

    public final static String CHECK_AVAILABILITY = HOTEL + "/rooms";
    public final static String RETRIEVE_BASIC_INFO = HOTEL + ROOM_ID;
    public final static String BOOK_ROOM = HOTEL + ROOM_ID;
    public final static String DELETE_RESERVATION = HOTEL + BOOKING_ID;

    public final static String REGISTER_NEW_GUEST = SYSTEM + REGISTER;
    public final static String REPORT_VISITORS = SYSTEM + REGISTER;
    public final static String CREATE_ROOM = SYSTEM + ROOM;
    public final static String UPDATE_ROOM = CREATE_ROOM + ROOM_ID;
    public final static String REMOVE_ROOM = CREATE_ROOM + ROOM_ID;
    public final static String PART_UPDATE_ROOM =   CREATE_ROOM + ROOM_ID;
    public static final String SIGN_UP = AUTH + "/signup";

}
