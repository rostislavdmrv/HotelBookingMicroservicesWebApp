package com.tinqinacademy.myhotel.api.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BedSize {
    SINGLE("single", 1),
    SMALLDOUBLE("smalldouble", 2),
    DOUBLE("double", 2),
    KINGSIZE("kingsized", 3),
    QUEENSIZE("queensized", 3),
    UNKNOWN("", 0);

    private final String code;
    private final Integer capacity;

    BedSize(String code, Integer capacity) {
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
