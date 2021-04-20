package com.freetonleague.core.exception;

public class ExceptionMessages {

    public static final String AUTHENTICATION_ERROR = "User is not authorized";
    public static final String AUTHENTICATION_PROVIDER_ERROR = "Some problems with user authorization. Please try again";
    public static final String AUTHENTICATION_SESSION_ERROR = "Some problems with user session. Please try relogin";
    public static final String AUTHENTICATION_PROVIDER_CONFIG_ERROR = "Auth provider is not responding with current config";

    public static final String FORBIDDEN_ERROR = "Need proper permission to access. Please try login";
    public static final String VALIDATION_ERROR = "Unacceptable arguments. Please check request parameters";
    public static final String METHOD_ARGUMENT_VALIDATION_ERROR = "Method argument not valid";
    public static final String ENTITY_NOT_FOUND_ERROR = "Entity not found. Please check request parameters";
    public static final String REQUEST_MESSAGE_READABLE_ERROR = "Malformed JSON Request. Please check request parameters";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred. Please check stacktrace";

    public static final String BLOCKCHAIN_DUPLICATE_EXCEPTION_ERROR = "Need proper permission to access. Please try login";

    public static final String USER_NOT_FOUND_ERROR = "User was not found. Please check request parameters";
    public static final String USER_REQUIRED_ERROR = "User was not set in request. Please check request parameters";
    public static final String USER_MODIFICATION_ERROR = "User modification failed. Please try again";
    public static final String USER_DUPLICATE_FOUND_ERROR = "User has not been clearly defined. Please check request parameters";
    public static final String USER_DUPLICATE_BY_LOGIN_ERROR = "User with requested username already exists on portal. Please change name";
    public static final String USER_DUPLICATE_BY_EMAIL_ERROR = "User with requested email already exists on portal. Please login or restore password";

    public static final String TEAM_CREATION_ERROR = "Team creation failed. Please try again";
    public static final String TEAM_DUPLICATE_BY_NAME_ERROR = "Team with requested name already exists on portal. Please change name";
    public static final String TEAM_MODIFY_ERROR = "Team modifying failed. Please try again";
    public static final String TEAM_PARTICIPANT_MEMBERSHIP_ERROR = "Participant is not the team member. Please check request parameters";
    public static final String TEAM_NOT_FOUND_ERROR = "Team was not found. Please check request parameters";
    public static final String TEAM_FORBIDDEN_ERROR = "Need proper team status to manage team settings. Please check your rights.";
    public static final String TEAM_EXPELLING_ERROR = "Need proper status to exclude from team. Please check your rights.";
    public static final String TEAM_EXPELLING_PARTICIPANT_ERROR = "Only active participant and not captain can be excluded from a team.";
    public static final String TEAM_DISABLE_ERROR = "Team is not active and modification forbidden. Please contact to organizers.";

    public static final String TEAM_PARTICIPANT_NOT_FOUND_ERROR = "Participant was not found. Please check request parameters";
    public static final String TEAM_PARTICIPANT_INVITE_FORBIDDEN_ERROR = "Need proper team status to manage team invitation. Please check your rights.";
    public static final String TEAM_PARTICIPANT_INVITE_EXPIRED_ERROR = "Invitation link was already used or expired. Please ask team captain for a new one.";
    public static final String TEAM_PARTICIPANT_INVITE_ASSIGNED_ERROR = "Invitation link was intended to another used. Please ask team captain for another one.";
    public static final String TEAM_PARTICIPANT_INVITE_REJECTED_ERROR = "User is already participate to a team. Please check participation status or ask captain to check.";
    public static final String TEAM_PARTICIPANT_INVITE_DUPLICATE_ERROR = "Invitation to requested user is already existed. Wait for user decision and status update.";
    public static final String TEAM_PARTICIPANT_INVITE_NOT_FOUND_ERROR = "Invitation to a team was not found. Please check request parameters";

    public static final String GAME_DISCIPLINE_CREATION_ERROR = "Game discipline creation failed. Please try again";
    public static final String GAME_DISCIPLINE_DUPLICATE_BY_NAME_ERROR = "Game discipline with requested name already exists on portal. Please change name";
    public static final String GAME_DISCIPLINE_NOT_FOUND_ERROR = "Game discipline was not found. Please check request parameters";
    public static final String GAME_DISCIPLINE_NOT_ACTIVE_ERROR = "Game discipline is not active. Getting of information rejected. Please check request parameters";

    public static final String GAME_DISCIPLINE_SETTINGS_CREATION_ERROR = "Game discipline settings creation failed. Please try again";
    public static final String GAME_DISCIPLINE_SETTINGS_DUPLICATE_BY_NAME_ERROR = "Game discipline settings with requested name already exists on portal. Please change name";
    public static final String GAME_DISCIPLINE_SETTINGS_NOT_FOUND_ERROR = "Game discipline settings was not found. Please check request parameters";
    public static final String GAME_DISCIPLINE_SETTINGS_MATCH_DISCIPLINE_ERROR = "Game discipline settings is not match requested discipline. Please check request parameters";
    public static final String GAME_DISCIPLINE_SETTINGS_CONVERTED_ERROR = "Game discipline settings was badly saved. Please try to modify or create new one";
    public static final String GAME_DISCIPLINE_SETTINGS_PRIMARY_MODIFICATION_ERROR = "Only one primary game discipline settings must exist for one discipline. " +
            "Changing primary flag available only with saving new entry. Please check request parameters";

    public static final String TOURNAMENT_NOT_FOUND_ERROR = "Tournament was not found. Please check request parameters";
    public static final String TOURNAMENT_VISIBLE_ERROR = "Tournament is not visible and modification forbidden. Please contact to organizers.";
    public static final String TOURNAMENT_STATUS_DELETE_ERROR = "Tournament delete should be done with specific request. Please try again with other method.";
    public static final String TOURNAMENT_CREATION_ERROR = "Tournament creation failed. Please try again";
    public static final String TOURNAMENT_MODIFICATION_ERROR = "Tournament modifying failed. Please try again";

    public static final String TOURNAMENT_SETTINGS_VALIDATION_ERROR = "Tournament settings was specified with errors. Please check request parameters";

    public static final String TOURNAMENT_ORGANIZER_NOT_FOUND_ERROR = "Tournament organizer was not found. Please check request parameters";
    public static final String TOURNAMENT_ORGANIZER_VALIDATION_ERROR = "Tournament organizer was specified with errors. Please check request parameters";

    public static final String TOURNAMENT_ROUND_NOT_FOUND_ERROR = "Tournament round was not found. Please check request parameters";
    public static final String TOURNAMENT_ROUND_DISABLE_ERROR = "Tournament round is not active and modification forbidden. Please contact to organizers.";
    public static final String TOURNAMENT_ROUND_CREATION_ERROR = "Tournament round creation failed. Please try again";
    public static final String TOURNAMENT_ROUND_STATUS_DELETE_ERROR = "Tournament round delete should be done with specific request. Please try again with other method.";
    public static final String TOURNAMENT_ROUND_MODIFICATION_ERROR = "Tournament round modifying failed. Please try again";
    public static final String TOURNAMENT_ROUND_GENERATION_ERROR = "Tournament round generation failed. Please try again";
    public static final String TOURNAMENT_ROUND_VALIDATION_ERROR = "Tournament round was specified with errors. Please check request parameters";

    public static final String TOURNAMENT_SERIES_NOT_FOUND_ERROR = "Tournament series was not found. Please check request parameters";
    public static final String TOURNAMENT_SERIES_DISABLE_ERROR = "Tournament series is not active and modification forbidden. Please contact to organizers.";
    public static final String TOURNAMENT_SERIES_CREATION_ERROR = "Tournament series creation failed. Please try again";
    public static final String TOURNAMENT_SERIES_MODIFICATION_ERROR = "Tournament series modifying failed. Please try again";
    public static final String TOURNAMENT_SERIES_STATUS_DELETE_ERROR = "Tournament series delete should be done with specific request. Please try again with other method.";
    public static final String TOURNAMENT_SERIES_GENERATION_ERROR = "Tournament series generation failed. Please try again";
    public static final String TOURNAMENT_SERIES_VALIDATION_ERROR = "Tournament series was specified with errors. Please check request parameters";

    public static final String TOURNAMENT_MATCH_CREATION_ERROR = "Tournament match creation failed. Please try again";
    public static final String TOURNAMENT_MATCH_NOT_FOUND_ERROR = "Tournament match was not found. Please check request parameters";
    public static final String TOURNAMENT_MATCH_MODIFICATION_ERROR = "Tournament match modifying failed. Please try again";
    public static final String TOURNAMENT_MATCH_DISABLE_ERROR = "Tournament match is not active and modification forbidden. Please contact to organizers.";
    public static final String TOURNAMENT_MATCH_VALIDATION_ERROR = "Tournament match was specified with errors. Please check request parameters";

    public static final String TOURNAMENT_MATCH_RIVAL_FORBIDDEN_ERROR = "Need proper team status to manage match settings for team. Please check your rights.";
    public static final String TOURNAMENT_MATCH_RIVAL_NOT_FOUND_ERROR = "Tournament match rival was not found. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR = "Tournament match rival was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_NOT_FOUND_ERROR = "Tournament match rival participant was not found. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_MODIFY_ERROR = "Tournament match rival participant modifying failed. Please try again";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_VALIDATION_ERROR = "Tournament match rival participant was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_BANNED_ERROR = "Team participant was banned for this match. Request for activate participant in match was rejected. Please contact to organizers.";

    public static final String TOURNAMENT_TEAM_PARTICIPANT_VALIDATION_ERROR = "Tournament team participant was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_TEAM_PARTICIPANT_NOT_FOUND_ERROR = "Tournament team participant was not found. Please check request parameters";
}
