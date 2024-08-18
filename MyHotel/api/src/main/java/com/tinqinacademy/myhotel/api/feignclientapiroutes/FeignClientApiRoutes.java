package com.tinqinacademy.myhotel.api.feignclientapiroutes;

import com.tinqinacademy.myhotel.api.restapiroutes.RestApiRoutes;

public class FeignClientApiRoutes {

    public final static String CHECK_AVAILABILITY ="GET " + RestApiRoutes.CHECK_AVAILABILITY +
            "?startDate={startDate}&endDate={endDate}&bedCount={bedCount}&bedSizes={bedSizes}&bathroomType={bathroomType}";
    public final static String RETRIEVE_BASIC_INFO ="GET " + RestApiRoutes.RETRIEVE_BASIC_INFO;
    public final static String BOOK_ROOM="POST " + RestApiRoutes.BOOK_ROOM;
    public final static String DELETE_RESERVATION ="DELETE " + RestApiRoutes.DELETE_RESERVATION;
    public final static String REGISTER_GUEST = "POST" + RestApiRoutes.REGISTER_NEW_GUEST;
    public final static String REPORT_VISITORS = "GET" + RestApiRoutes.REPORT_VISITORS +
            "?startDate={startDate}&endDate={endDate}&birthDate={birthDate}&firstName={firstName}" +
            "&lastName={lastName}&phoneNo={phoneNo}&idCardNo&idCardNo={idCardNo}&idCardValidity={idCardValidity}" +
            "&idCardIssueDate={idCardIssueDate}&roomNo={roomNo}";
    public final static String CREATE_ROOM = "POST" + RestApiRoutes.CREATE_ROOM;
    public final static String UPDATE_ROOM = "PUT" + RestApiRoutes.UPDATE_ROOM;
    public final static String PART_UPDATE_ROOM = "PATCH" + RestApiRoutes.PART_UPDATE_ROOM;
    public final static String DELETE_ROOM_BY_ADMIN ="DELETE"+RestApiRoutes.REMOVE_ROOM;



}
