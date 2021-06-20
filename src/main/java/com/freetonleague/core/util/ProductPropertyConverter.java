package com.freetonleague.core.util;

import com.freetonleague.core.domain.dto.ProductPropertyDto;
import com.freetonleague.core.domain.enums.IndicatorValueClassType;
import com.freetonleague.core.domain.enums.IndicatorValueMultiplicityType;
import com.freetonleague.core.exception.GameDisciplineManageException;
import com.freetonleague.core.exception.config.ExceptionMessages;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ProductPropertyConverter {

    /**
     * Verified ProductPropertyType List field entry form DB (from jsonB type) to correct type entity
     */
    public static List<ProductPropertyDto> convertAndValidateProperties(List<ProductPropertyDto> productPropertyList) {
        if (productPropertyList == null) {
            return null;
        }
        AtomicReference<ProductPropertyDto> currentIndicator = new AtomicReference<>();
        try {
            productPropertyList.forEach(productProperty -> {
                currentIndicator.set(productProperty);
                composeVerifiedValue(productProperty);
            });
        } catch (Exception exc) {
            throw new GameDisciplineManageException(ExceptionMessages.PRODUCT_PROPERTIES_CONVERTED_ERROR,
                    "Problem occurred with value: ' " + currentIndicator + "'. Details: " + exc.getMessage());
        }
        return productPropertyList;
    }

    /**
     * Verified GameDisciplineIndicator List field entry form DB (from jsonB type) to correct type entity
     */
    public static List<ProductPropertyDto> convertAndValidateSelectedProperties(List<ProductPropertyDto> selectedProductPropertyList) {
        if (selectedProductPropertyList == null) {
            return null;
        }
        AtomicReference<ProductPropertyDto> currentIndicator = new AtomicReference<>();
        try {
            selectedProductPropertyList.forEach(productProperty -> {
                currentIndicator.set(productProperty);
                composeVerifiedValue(productProperty);
            });
        } catch (Exception exc) {
            throw new GameDisciplineManageException(ExceptionMessages.PRODUCT_PURCHASE_PROPERTIES_CONVERTED_ERROR,
                    "Problem occurred with value: ' " + currentIndicator + "'. Details: " + exc.getMessage());
        }
        return selectedProductPropertyList;
    }

    private static ProductPropertyDto composeVerifiedValue(ProductPropertyDto productProperty) {
        IndicatorValueMultiplicityType valueMultiplicityType = productProperty.getProductPropertyType().getValueMultiplicityType();
        if (valueMultiplicityType.isArrayable()) {
            List<?> entryValueList = (List<?>) productProperty.getProductPropertyValue();
            productProperty.setProductPropertyValue(entryValueList.parallelStream()
                    .map(value -> getVerifiedValue(productProperty.getProductPropertyType().getValueClassType(), value))
                    .collect(Collectors.toList()));
        } else {
            productProperty.setProductPropertyValue(
                    getVerifiedValue(productProperty.getProductPropertyType().getValueClassType(),
                            productProperty.getProductPropertyValue()));
        }
        return productProperty;
    }

    private static ProductPropertyDto composeVerifiedSelectedValue(ProductPropertyDto productProperty) {
        IndicatorValueMultiplicityType selectedValueMultiplicityType =
                productProperty.getProductPropertyType().getSelectedValueMultiplicityType();
        if (selectedValueMultiplicityType.isArrayable()) {
            List<?> entryValueList = (List<?>) productProperty.getProductPropertyValue();
            productProperty.setProductPropertyValue(entryValueList.parallelStream()
                    .map(value -> getVerifiedValue(productProperty.getProductPropertyType().getValueClassType(), value))
                    .collect(Collectors.toList()));
        } else {
            productProperty.setProductPropertyValue(
                    getVerifiedValue(productProperty.getProductPropertyType().getValueClassType(),
                            productProperty.getProductPropertyValue()));
        }
        return productProperty;
    }

    private static Object getVerifiedValue(IndicatorValueClassType classType, Object value) throws IllegalArgumentException {
        Object verifiedValue;
        if (value instanceof List<?>) {
            throw new IllegalArgumentException(String.format("Specified value '%s' is an array, but primitive type was expected", value));
        }
        String entryValue = String.valueOf(value);
        switch (classType) {
            case BOOLEAN:
                verifiedValue = Boolean.valueOf(entryValue);
                break;
            case DOUBLE:
                verifiedValue = Double.valueOf(entryValue);
                break;
            case INTEGER:
                verifiedValue = Integer.valueOf(entryValue);
                break;
            case LONG:
                verifiedValue = Long.valueOf(entryValue);
                break;
            case STRING:
            default:
                verifiedValue = entryValue;
        }
        return verifiedValue;
    }
}
