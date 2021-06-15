package com.freetonleague.core.domain.filter;

import com.freetonleague.core.domain.enums.UserParameterType;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
public class UserInfoFilter {

    @NotNull
    private Map<UserParameterType, String> parameters;

}
