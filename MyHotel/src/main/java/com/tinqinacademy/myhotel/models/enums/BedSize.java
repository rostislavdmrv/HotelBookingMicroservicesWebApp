package com.tinqinacademy.myhotel.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BedSize {
    SINGLE("single"),
    SMALL_DOUBLE("smallDouble"),
    DOUBLE("double"),
    QUEEN_SIZE("queenSize"),
    KING_SIZE("kingSize"),
    UNKNOWN("");



    private final String code;

    BedSize(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @JsonCreator
    public static BedSize getFromCode(String code) {
        for (BedSize size : BedSize.values()) {
            if(size.getCode().equals(code)) {
                return size;

            }
        }
        return BedSize.UNKNOWN;
    }

    @Override
    @JsonValue
    public String toString() {
        return getCode();
    }
}
