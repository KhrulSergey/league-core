package com.freetonleague.core.domain.enums;

/**
 * All Possible product properties
 */
public enum ProductPropertyType {

    COLOR("color", "цвет", IndicatorValueClassType.STRING,
            IndicatorValueMultiplicityType.MULTISELECT, IndicatorValueMultiplicityType.SELECT),
    SIZE("size", "размер", IndicatorValueClassType.INTEGER,
            IndicatorValueMultiplicityType.MULTISELECT, IndicatorValueMultiplicityType.SELECT),
    VENDOR_CODE("vendor code", "артикул", IndicatorValueClassType.STRING,
            IndicatorValueMultiplicityType.MULTISELECT, IndicatorValueMultiplicityType.SELECT),
    IDENTIFIER("identifier", "идентификатор", IndicatorValueClassType.STRING,
            IndicatorValueMultiplicityType.TEXT, IndicatorValueMultiplicityType.TEXT),
    EXTERNAL_BANK_ADDRESS("external bank address", "банковский адрес", IndicatorValueClassType.STRING,
            IndicatorValueMultiplicityType.TEXT, IndicatorValueMultiplicityType.TEXT),
    ;

    private final String description;

    private final String russianDescription;

    private final IndicatorValueClassType valueClassType;

    /**
     * Possible type of indicator for product parameter description
     */
    private final IndicatorValueMultiplicityType valueMultiplicityType;

    /**
     * Possible type of indicator for selected parameter in product purchase
     */
    private final IndicatorValueMultiplicityType selectedValueMultiplicityType;

    ProductPropertyType(String description, String russianDescription, IndicatorValueClassType valueClassType,
                        IndicatorValueMultiplicityType valueMultiplicityType, IndicatorValueMultiplicityType selectedValueMultiplicityType) {
        this.description = description;
        this.russianDescription = russianDescription;
        this.valueClassType = valueClassType;
        this.valueMultiplicityType = valueMultiplicityType;
        this.selectedValueMultiplicityType = selectedValueMultiplicityType;
    }

    public String getDescription() {
        return description;
    }

    public String getRussianDescription() {
        return russianDescription;
    }

    public IndicatorValueClassType getValueClassType() {
        return valueClassType;
    }

    public IndicatorValueMultiplicityType getValueMultiplicityType() {
        return valueMultiplicityType;
    }

    public IndicatorValueMultiplicityType getSelectedValueMultiplicityType() {
        return selectedValueMultiplicityType;
    }
}
