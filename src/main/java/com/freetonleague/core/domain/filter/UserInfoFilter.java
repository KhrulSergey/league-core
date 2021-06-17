package com.freetonleague.core.domain.filter;

import com.freetonleague.core.domain.enums.UserParameterType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class UserInfoFilter {

    @NotNull
    private Map<UserParameterType, String> parameters;

}
