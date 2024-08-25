package com.tinqinacademy.myhotel.api.validations.bathroomtype;

import com.tinqinacademy.myhotel.api.models.enums.BathroomType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BathroomTypeValidator implements ConstraintValidator<BathroomTypeValidation, String> {
    private static final Set<String> VALID_BATHROOM_TYPES =
            EnumSet.allOf(BathroomType.class)
                    .stream()
                    .filter(bathroom -> bathroom != BathroomType.UNKNOWN)
                    .map(BathroomType::toString)
                    .collect(Collectors.toSet());

    private boolean optional;

    @Override
    public void initialize(BathroomTypeValidation bathroomTpeValidation) {
        this.optional = bathroomTpeValidation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (optional && (value == null || value.isEmpty())){
            return true;
        }

        if (value == null || value.isEmpty()){
            return false;
        }

        return VALID_BATHROOM_TYPES.contains(value);
    }
}
