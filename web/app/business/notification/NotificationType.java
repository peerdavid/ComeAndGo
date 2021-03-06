package business.notification;

/**
 * Created by paz on 19.04.16.
 */
public enum NotificationType {
    // General Types
    INFORMATION,
    ERROR,

    // User as sender notifications
    HOLIDAY_REQUEST,
    SPECIAL_HOLIDAY_REQUEST,
    BUSINESS_TRIP_INFORMATION,
    SICK_LEAVE_INFORMATION,
    HOLIDAY_PAYOUT_REQUEST,
    OVERTIME_PAYOUT_REQUEST,
    PARENTAL_LEAVE_REQUEST,
    EDUCATIONAL_LEAVE_REQUEST,

    // Boss as sender notifications
    HOLIDAY_ACCEPT,
    HOLIDAY_REJECT,
    HOLIDAY_PAYOUT_ACCEPT,
    HOLIDAY_PAYOUT_REJECT,
    OVERTIME_PAYOUT_ACCEPT,
    OVERTIME_PAYOUT_REJECT,
    SPECIAL_HOLIDAY_ACCEPT,
    SPECIAL_HOLIDAY_REJECT,
    EDUCATIONAL_LEAVE_ACCEPT,
    EDUCATIONAL_LEAVE_REJECT,
    FIRE_EMPLOYEE,

    // Personnel Manager as sender notifications
    CREATED_TIMETRACK,
    CHANGED_TIMETRACK,
    DELETED_TIMETRACK
}
