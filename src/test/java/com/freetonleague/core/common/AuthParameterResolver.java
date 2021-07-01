package com.freetonleague.core.common;

import com.freetonleague.core.common.bean.AuthParameterResolverBean;
import com.freetonleague.core.domain.model.User;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class AuthParameterResolver implements ParameterResolver {

    private AuthParameterResolverBean authParameterResolverBean;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();

        return User.class.equals(type) ||
                HttpHeaders.class.equals(type);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        initParameterResolverBean(extensionContext);

        return authParameterResolverBean.resolveParameter(parameterContext, extensionContext);
    }

    private void initParameterResolverBean(ExtensionContext extensionContext) {
        if (authParameterResolverBean != null) {
            return;
        }

        ApplicationContext applicationContext = SpringExtension.getApplicationContext(extensionContext);

        authParameterResolverBean = applicationContext.getBean(AuthParameterResolverBean.class);
    }

}
