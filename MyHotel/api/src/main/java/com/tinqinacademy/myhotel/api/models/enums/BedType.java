package com.tinqinacademy.myhotel.api.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BedType {
    SINGLE("single", 1),
    SMALL_DOUBLE("smallDouble", 2),
    DOUBLE("double", 2),
    KING_SIZE("kingSized", 3),
    QUEEN_SIZE("queenSized", 3),
    UNKNOWN("", 0);

    private final String code;
    private final Integer capacity;

    BedType(String code, Integer capacity) {
        this.code = code;
        this.capacity = capacity;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public String getCode() {
        return code;
    }

    @JsonCreator
    public static BedType getFromCode(String code) {
        for (BedType size : BedType.values()) {
            if(size.getCode().equals(code)) {
                return size;

            }
        }
        return BedType.UNKNOWN;
    }

    @Override
    @JsonValue
    public String toString() {
        return getCode();
    }
}
