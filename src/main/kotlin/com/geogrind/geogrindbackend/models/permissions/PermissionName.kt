package com.geogrind.geogrindbackend.models.permissions

enum class PermissionName {
    CAN_VERIFY_OTP,

    // User Profile
    CAN_VIEW_PROFILE,
    CAN_EDIT_PROFILE,

    // Files upload
    CAN_VIEW_FILES,
    CAN_UPLOAD_FILES,
    CAN_DELETE_FILES,

    // Map
    CAN_VIEW_MAP,

    // Session
    CAN_CREATE_SESSION,
    CAN_STOP_SESSION,

    // Message
    CAN_VIEW_CHAT,
    CAN_EDIT_CHAT,
}