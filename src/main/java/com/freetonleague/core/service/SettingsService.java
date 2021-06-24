package com.freetonleague.core.service;

public interface SettingsService {

    String TON_TO_UC_EXCHANGE_RATE_KEY = "TON_TO_UC_EXCHANGE_RATE_KEY";

    String getValue(String key);

    void forceUpdate();

}
