package business.notification;

/**
 * Created by paz on 19.04.16.
 */
public enum NotificationType {
    // General Types
    INFORMATION,
    ERROR,

    // User notifications
    HOLYDAY_REQUEST,
    SPECIAL_HOLIDAY_REQUEST,
    BUSINESS_TRIP_INFORMATION,
    SICK_LEAVE_INFORMATION,
    HOLIDAY_PAYOUT_REQUEST,
    OVERTIME_PAYOUT_REQUEST,

    // Boss notifications
    HOLIDAY_ACCEPT,
    HOLIDAY_REJECT,
    HOLIDAY_PAYOUT_ACCEPT,
    HOLIDAY_PAYOUT_REJECT,
    OVERTIME_PAYOUT_ACCEPT,
    OVERTIME_PAYOUT_REJECT,
    FIRE_EMPLOYEE

}