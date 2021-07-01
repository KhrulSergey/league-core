package com.freetonleague.core.common;

import com.freetonleague.core.domain.enums.UserRoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestUserRoles {

    UserRoleType[] value();

}
