package com.freetonleague.core.exception.config;

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
    public static final String TEAM_DISBAND_ERROR = "Team is participate in active tournament. Wait for tournament is finished or contact to organizers.";
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
    public static final String TOURNAMENT_VALIDATION_ERROR = "Tournament was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_STATUS_FINISHED_ERROR = "Tournament can be finished only with setting winner list of the tournament. Please try to set winners and finished status simultaneously.";
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
    public static final String TOURNAMENT_SERIES_STATUS_FINISHED_ERROR = "Tournament tournament series can be finished only automatically when all match is finished. Request to set status was rejected.";
    public static final String TOURNAMENT_SERIES_STATUS_DELETE_ERROR = "Tournament series delete should be done with specific request. Please try again with other method.";
    public static final String TOURNAMENT_SERIES_GENERATION_ERROR = "Tournament series generation failed. Please try again";
    public static final String TOURNAMENT_SERIES_VALIDATION_ERROR = "Tournament series was specified with errors. Please check request parameters";

    public static final String TOURNAMENT_SERIES_RIVAL_NOT_FOUND_ERROR = "Tournament series rival was not found. Please check request parameters";
    public static final String TOURNAMENT_SERIES_RIVAL_VALIDATION_ERROR = "Tournament series rival was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_SERIES_RIVAL_MODIFICATION_ERROR = "Tournament series rival modifying failed. Please try again";

    public static final String TOURNAMENT_MATCH_CREATION_ERROR = "Tournament match creation failed. Please try again";
    public static final String TOURNAMENT_MATCH_NOT_FOUND_ERROR = "Tournament match was not found. Please check request parameters";
    public static final String TOURNAMENT_MATCH_STATUS_DELETE_ERROR = "Tournament match delete should be done with specific request. Please try again with other method.";
    public static final String TOURNAMENT_MATCH_STATUS_FINISHED_ERROR = "Tournament match can be finished only with setting the winner of the match. Please try to set match winner and finished status simultaneously.";
    public static final String TOURNAMENT_MATCH_MODIFICATION_ERROR = "Tournament match modifying failed. Please try again";
    public static final String TOURNAMENT_MATCH_DISABLE_ERROR = "Tournament match is not active and modification forbidden. Please contact to organizers.";
    public static final String TOURNAMENT_MATCH_VALIDATION_ERROR = "Tournament match was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_MATCH_PROPERTIES_CONVERTED_ERROR = "Tournament match properties was badly saved. Please try to modify or create new one";

    public static final String TOURNAMENT_MATCH_RIVAL_FORBIDDEN_ERROR = "Need proper team status to manage match settings for team. Please check your rights.";
    public static final String TOURNAMENT_MATCH_RIVAL_NOT_FOUND_ERROR = "Tournament match rival was not found. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_MODIFY_ERROR = "Tournament match rival modifying failed. Please try again";
    public static final String TOURNAMENT_MATCH_RIVAL_VALIDATION_ERROR = "Tournament match rival was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_NOT_FOUND_ERROR = "Tournament match rival participant was not found. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_MODIFY_ERROR = "Tournament match rival participant modifying failed. Please try again";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_VALIDATION_ERROR = "Tournament match rival participant was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_MATCH_RIVAL_PARTICIPANT_BANNED_ERROR = "Team participant was banned for this match. Request for activate participant in match was rejected. Please contact to organizers.";

    public static final String TOURNAMENT_TEAM_PROPOSAL_NOT_FOUND_ERROR = "Tournament team proposal was not found. Please check request parameters";
    public static final String TOURNAMENT_TEAM_PROPOSAL_EXIST_ERROR = "Tournament team proposal already existed. Creation duplicates is prohibited";
    public static final String TOURNAMENT_TEAM_PROPOSAL_FORBIDDEN_ERROR = "Need proper team status to manage team proposals to tournaments. Please check your rights.";
    public static final String TOURNAMENT_TEAM_PROPOSAL_QUIT_ERROR = "Quit from specified tournament is prohibited. Request is rejected. Please contact to organizers to get more information.";
    public static final String TOURNAMENT_TEAM_PROPOSAL_VISIBLE_ERROR = "Tournament team proposal is not visible and modification forbidden. Please contact to organizers.";
    public static final String TOURNAMENT_TEAM_PROPOSAL_VALIDATION_ERROR = "Tournament team proposal was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_TEAM_PROPOSAL_VERIFICATION_ERROR = "Tournament proposal can't be created to specified tournament. Please check participation parameters, account balance or contact to organizers";
    public static final String TOURNAMENT_TEAM_PROPOSAL_PARAMETERS_VERIFICATION_ERROR = "Team can't participate in specified tournament. Each participant should fill in the parameters in the profile";
    public static final String TOURNAMENT_TEAM_PROPOSAL_CREATION_ERROR = "Tournament team proposal creation failed. Please try again";
    public static final String TOURNAMENT_TEAM_PROPOSAL_MODIFICATION_ERROR = "Tournament team proposal modifying failed. Please try again";

    public static final String TOURNAMENT_TEAM_PARTICIPANT_VALIDATION_ERROR = "Tournament team participant was specified with errors. Please check request parameters";
    public static final String TOURNAMENT_TEAM_PARTICIPANT_NOT_FOUND_ERROR = "Tournament team participant was not found. Please check request parameters";

    public static final String DOCKET_NOT_FOUND_ERROR = "Docket was not found. Please check request parameters";
    public static final String DOCKET_VISIBLE_ERROR = "Docket is not visible and modification forbidden. Please contact to organizers.";
    public static final String DOCKET_STATUS_DELETE_ERROR = "Docket delete should be done with specific request. Please try again with other method.";
    public static final String DOCKET_CREATION_ERROR = "Docket creation failed. Please try again";
    public static final String DOCKET_VALIDATION_ERROR = "Docket was specified with errors. Please check request parameters";
    public static final String DOCKET_STATUS_FINISHED_ERROR = "Docket can be finished only with setting winner list of the docket. Please try to set winners and finished status simultaneously.";
    public static final String DOCKET_MODIFICATION_ERROR = "Docket modifying failed. Please try again";

    public static final String DOCKET_USER_PROPOSAL_NOT_FOUND_ERROR = "User proposal to Docket was not found. Please check request parameters";
    public static final String DOCKET_USER_PROPOSAL_EXIST_ERROR = "User proposal to Docket already existed. Creation duplicates is prohibited";
    public static final String DOCKET_USER_PROPOSAL_VALIDATION_ERROR = "User proposal to Docket was specified with errors. Please check request parameters";
    public static final String DOCKET_USER_PROPOSAL_VERIFICATION_ERROR = "User can't participate in specified docket. Please check account balance or contact to organizers";
    public static final String DOCKET_USER_PROPOSAL_CREATION_ERROR = "User proposal to Docket creation failed. Please try again";
    public static final String DOCKET_USER_PROPOSAL_LIMIT_EXCEED_ERROR = "User proposal's to Docket can't be created. Docket proposal's limit exceeded.";
    public static final String DOCKET_USER_PROPOSAL_MODIFICATION_ERROR = "User proposal to Docket modifying failed. Please try again";

    public static final String ACCOUNT_INFO_NOT_FOUND_ERROR = "Financial account info was not found. Please check request parameters";
    public static final String TRANSACTION_WITHDRAW_CREATION_ERROR = "Request for financial withdraw transaction was rejected by provider. Please check request parameters or try again later";
    public static final String TRANSACTION_VALIDATION_ERROR = "Request for financial transaction was specified with errors. Please check request parameters";
    public static final String TRANSACTION_NOT_FOUND_ERROR = "Financial transaction info was not found. Please check request parameters";
    public static final String ACCOUNT_COUPON_APPLY_ERROR = "Applying coupon by advertisement company hash was unsuccessful." +
            "Please check request parameters or contact organizers";

    public static final String NEWS_NOT_FOUND_ERROR = "News was not found. Please check request parameters";
    public static final String NEWS_VISIBLE_ERROR = "Docket is not visible and modification forbidden. Please contact to organizers.";
    public static final String NEWS_STATUS_DELETE_ERROR = "News delete should be done with specific request. Please try again with other method.";
    public static final String NEWS_CREATION_ERROR = "News creation failed. Please try again";
    public static final String NEWS_VALIDATION_ERROR = "News was specified with errors. Please check request parameters";
    public static final String NEWS_MODIFICATION_ERROR = "News modifying failed. Please try again";

    public static final String PRODUCT_NOT_FOUND_ERROR = "Product was not found. Please check request parameters";
    public static final String PRODUCT_VISIBLE_ERROR = "Product is not visible and modification forbidden. Please contact to organizers.";
    public static final String PRODUCT_STATUS_DELETE_ERROR = "Product delete should be done with specific request. Please try again with other method.";
    public static final String PRODUCT_CREATION_ERROR = "Product creation failed. Please try again";
    public static final String PRODUCT_VALIDATION_ERROR = "Product was specified with errors. Please check request parameters";
    public static final String PRODUCT_MODIFICATION_ERROR = "Product modifying failed. Please try again";

    public static final String PRODUCT_PURCHASE_NOT_FOUND_ERROR = "Product purchase was not found. Please check request parameters";
    public static final String PRODUCT_PURCHASE_VALIDATION_ERROR = "Product purchase was specified with errors. Please check request parameters";
    public static final String PRODUCT_PURCHASE_VERIFICATION_ERROR = "User can't purchase specified product quantity. Please check account balance or contact to organizers";
    public static final String PRODUCT_PURCHASE_CREATION_ERROR = "Product purchase creation failed. Please try again";
    public static final String PRODUCT_IS_OUT_OF_STOCK_ERROR = "Product is out of stock. Product purchase creation failed.";
    public static final String PRODUCT_PURCHASE_MODIFICATION_ERROR = "Product purchase modifying failed. Please try again";

    public static final String NOTIFICATION_VALIDATION_ERROR = "Notification was specified with errors. Please check request parameters";

    public static final String FEIGN_UNEXPECTED_ERROR = "Some error with service connections. Try again";

    //---//
    public static final String FINANCE_UNIT_ACCOUNT_NOT_FOUND_ERROR = "Financial account was not found. Please check request parameters";
    public static final String FINANCE_UNIT_ACCOUNT_CREATION_ERROR = "Financial account creation failed. Please try again";
    public static final String FINANCE_UNIT_TRANSACTION_CREATION_ERROR = "Financial transaction was not created and not saved. Please check request parameters";
    public static final String FINANCE_UNIT_TRANSACTION_MODIFY_ABORTED_ERROR = "Aborted financial transaction can't be modified. Request denied";
    public static final String FINANCE_UNIT_TRANSACTION_MODIFY_ABORT_ERROR = "Aborted financial transaction can't be modified. Request denied";
    public static final String FINANCE_UNIT_TRANSACTION_ABORT_ERROR = "Financial transaction can't be aborted. Request denied";
    public static final String FINANCE_UNIT_TRANSACTION_MODIFY_FINISHED_ERROR = "Only aborted operation available for finished transaction. Request denied";
    public static final String FINANCE_UNIT_TOKEN_VALIDATION_ERROR = "Specified token is not valid for operate with deposit transactions. Request denied";
}
