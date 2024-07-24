package com.tinqinacademy.myhotel.rest.restapiroutes;

public class RestApiRoutes {
    private final static String ROOT = "/api/v1";
    public final static String ROOT_HOTEL = ROOT + "/hotel";
    public final static String ROOT_SYSTEM = ROOT + "/system";
    public static final String AUTH = ROOT + "/auth";

    public final static String CHECK_AVAILABILITY = ROOT_HOTEL + "/rooms";
    public final static String RETRIEVE_BASIC_INFO = ROOT_HOTEL + "/{roomId}";
    public final static String BOOK_ROOM = ROOT_HOTEL + "/{roomId}";
    public final static String DELETE_RESERVATION = ROOT_HOTEL + "/{bookingId}";

    public final static String REGISTER_NEW_GUEST = ROOT_SYSTEM + "/register";
    public final static String REPORT_VISITORS = ROOT_SYSTEM + "/register";
    public final static String CREATE_ROOM = ROOT_SYSTEM + "/room";
    public final static String UPDATE_ROOM = CREATE_ROOM + "/{roomId}";
    public final static String REMOVE_ROOM = CREATE_ROOM + "/{roomId}";
    public final static String PART_UPDATE_ROOM =   CREATE_ROOM + "/{roomId}";
    public static final String SIGN_UP = AUTH + "/signup";

}
