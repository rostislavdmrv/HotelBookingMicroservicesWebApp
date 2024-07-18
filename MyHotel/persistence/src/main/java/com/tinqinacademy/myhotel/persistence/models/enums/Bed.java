package com.tinqinacademy.myhotel.persistence.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Bed {
    SINGLE("single", 1),
    SMALL_DOUBLE("smallDouble", 2),
    DOUBLE("double", 2),
    KING_SIZE("kingSize", 3),
    QUEEN_SIZE("queensSize", 3),
    UNKNOWN("", 0);

    private final String code;
    private final Integer capacity;

    Bed(String code, Integer capacity) {
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
