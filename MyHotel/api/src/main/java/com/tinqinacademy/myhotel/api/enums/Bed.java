package com.tinqinacademy.myhotel.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Bed {
    SINGLE("single"),
    SMALL_DOUBLE("smallDouble"),
    DOUBLE("double"),
    QUEEN_SIZE("queenSize"),
    KING_SIZE("kingSize"),
    UNKNOWN("");



    private final String code;

    Bed(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Bed getFromCode(String code) {
        for (Bed size : Bed.values()) {
            if(size.getCode().equals(code)) {
                return size;

            }
        }
        return Bed.UNKNOWN;
    }

    @Override
    @JsonValue
    public String toString() {
        return getCode();
    }
}
