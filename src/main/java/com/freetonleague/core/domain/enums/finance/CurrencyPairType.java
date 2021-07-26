package com.freetonleague.core.domain.enums.finance;

import java.util.List;

import static com.freetonleague.core.domain.enums.finance.CurrencyMarketProviderType.*;

public enum CurrencyPairType {
    TON_USDT("tonusdt", Currency.TON, Currency.USDT, CurrencyPairDirectionType.FORWARD, true, BROXUS),  // $/ASK price = TON amount
    USDT_TON("tonusdt", Currency.USDT, Currency.TON, CurrencyPairDirectionType.BACKWARD, true, BROXUS), // TON*BID price = $ amount
    USDT_RUB("usdtrub", Currency.USDT, Currency.RUB, CurrencyPairDirectionType.FORWARD, true, KUNA),  // RUB/ASK price = $ amount
    RUB_USDT("usdtrub", Currency.RUB, Currency.USDT, CurrencyPairDirectionType.BACKWARD, true, KUNA), // $*BID price = RUB amount
    TON_RUB("tonrub", Currency.TON, Currency.RUB, CurrencyPairDirectionType.FORWARD, false, INNER),    // RUB/ASK price = TON amount
    RUB_TON("tonrub", Currency.RUB, Currency.TON, CurrencyPairDirectionType.BACKWARD, false, INNER);   // TON*BID price = RUB amount

    private final String code;
    private final Currency toBuy;
    private final Currency toSell;
    private final CurrencyPairDirectionType pairDirection;
    private final boolean isExistOnMarket;
    private final CurrencyMarketProviderType preferredProvider;

    CurrencyPairType(String code, Currency toBuy, Currency toSell, CurrencyPairDirectionType pairDirection,
                     boolean isExistOnMarket, CurrencyMarketProviderType preferredProvider) {
        this.code = code;
        this.toBuy = toBuy;
        this.toSell = toSell;
        this.pairDirection = pairDirection;
        this.isExistOnMarket = isExistOnMarket;
        this.preferredProvider = preferredProvider;
    }

    public static CurrencyPairType getByCodeWithForwardDirection(String code) {
        return List.of(CurrencyPairType.values()).parallelStream()
                .filter(c -> c.code.equals(code) && c.pairDirection == CurrencyPairDirectionType.FORWARD)
                .findFirst().orElse(null);
    }

    public static CurrencyPairType getByCurrencies(Currency toBuy, Currency toSell) {
        return List.of(CurrencyPairType.values()).parallelStream()
                .filter(c -> c.toBuy == toBuy && c.toSell == toSell).findFirst().orElse(null);
    }

    public static CurrencyPairType getByCode(String code) {
        return List.of(CurrencyPairType.values()).parallelStream()
                .filter(c -> c.code.equals(code)).findFirst().orElse(null);
    }

    public boolean isExistOnMarket() {
        return isExistOnMarket;
    }

    public String getCode() {
        return code;
    }

    public Currency getCurrencyToBuy() {
        return toBuy;
    }

    public Currency getCurrencyToSell() {
        return toSell;
    }

    public CurrencyPairDirectionType getPairDirection() {
        return pairDirection;
    }

    public CurrencyMarketProviderType getPreferredProvider() {
        return preferredProvider;
    }
}
