package com.freetonleague.core.exception;

public class ExceptionMessages {
    public static final String AUTHENTICATION_ERROR = "User is not authorized";
    public static final String AUTHENTICATION_PROVIDER_ERROR = "Some problems with user authorization. Please try again";
    public static final String AUTHENTICATION_SESSION_ERROR = "Some problems with user session. Please try relogin";
    public static final String FORBIDDEN_ERROR = "Need proper permission to access. Please try login";
    public static final String BLOCKCHAIN_DUPLICATE_EXCEPTION_ERROR = "Need proper permission to access. Please try login";
    public static final String VALIDATION_ERROR = "Unacceptable arguments. Please check request parameters";
    public static final String METHOD_ARGUMENT_VALIDATION_ERROR = "Method argument not valid";
    public static final String ENTITY_NOT_FOUND_ERROR = "Entity not found. Please check request parameters";
    public static final String REQUEST_MESSAGE_READABLE_ERROR = "Malformed JSON Request. Please check request parameters";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred. Please check stacktrace";
    public static final String AUTH_PROVIDER_CONFIG_ERROR = "Auth provider is not responding with current config";

    public static final String USER_NOT_FOUND_ERROR = "User was not found. Please check request parameters";
    public static final String USER_DUPLICATE_FOUND_ERROR = "User has not been clearly defined. Please check request parameters";
    public static final String USER_MODIFICATION_ERROR = "User modification failed. Please try again";

}
