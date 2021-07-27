package com.freetonleague.core.domain.enums.finance;

public enum Currency {
    RUB("43", "rub", "Рубли", "Rubble", "https://kuna.io/icons/currency/png/RUB@3x.png", false),
    USDT("41", "usdt", "Доллар", "Tether", "https://kuna.io/icons/currency/png/USDT@3x.png", true),
    TON("61", "ton", "Кристалы ТОН", "TON Crystal", "https://kuna.io/icons/currency/png/TON@3x.png", true);

    private final String externalGUID;
    private final String code;
    private final String name;
    private final String nativeName;
    private final String iconUrl;
    //is cryptoCurrency
    private final boolean coin;

    Currency(String externalGUID, String code, String name, String nativeName, String iconUrl, boolean coin) {
        this.externalGUID = externalGUID;
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
        this.iconUrl = iconUrl;
        this.coin = coin;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public boolean isTon() {
        return this == TON;
    }
}
