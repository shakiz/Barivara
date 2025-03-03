package com.shakil.barivara.utils

object ScreenNameConstants {
    const val appSreenHome = "app_screen_home"
    const val appSreenTenantList = "app_screen_tenant_list"
    const val appSreenTenantDetails = "app_screen_tenant_details"
    const val appSreenRoomList = "app_screen_room_list"
    const val appSreenRoomDetails = "app_screen_room_details"
    const val appSreenWelcone = "app_screen_welcome"
    const val appSreenLoginSelection = "app_screen_login_selection"
    const val appSreenLoginOtp = "app_screen_login_otp"
    const val appSreenLoginOtpVerify = "app_screen_login_otp_verify"
    const val appSreenLoginPassword = "app_screen_login_password"
    const val appSreenLoginPasswordSetup = "app_screen_login_password_setup"
    const val appSreenChangePassword = "app_screen_change_password"
    const val appSreenMyProfile = "app_screen_my_profile"
    const val appSreenTutorial = "app_screen_tutorial"
    const val appSreenDashboard = "app_screen_dashboard"
    const val appSreenGenerateBill = "app_screen_generate_bill"
    const val appScreenGenerateBillMarkAsPaidBottomSheet =
        "app_screen_generate_bill_mark_as_paid_dialog"
}

object ButtonActionConstants {
    const val actionHomeTenant = "action_home_tenant"
    const val actionHomeRoom = "action_home_room"
    const val actionHomeGenerateBill = "action_home_generate_bill"
    const val actionHomeGeneratedBillHistory = "action_home_generated_bill_history"
    const val actionHomeDashboard = "action_home_dashboard"
    const val actionHomeMenuItemMyProfile = "action_home_menu_item_profile"
    const val actionHomeMenuItemTutorial = "action_home_menu_item_tutorial"
    const val actionHomeMenuItemVideoTutorial = "action_home_menu_item_video_tutorial"
    const val actionHomeMenuItemChangePassword = "action_home_menu_item_change_password"
    const val actionHomeMenuItemRateUs = "action_home_menu_item_rate_us"
    const val actionHomeMenuItemLogout = "action_home_menu_item_logout"

    //Tenant List and Details Actions
    const val actionTenantListClose = "action_tenant_list_close"
    const val actionTenantDelete = "action_tenant_list_delete"
    const val actionTenantUpdate = "action_tenant_list_update"
    const val actionTenantMakeCall = "action_tenant_list_make_call"
    const val actionTenantSendMessage = "action_tenant_list_send_message"
    const val actionAddNewTenant = "action_tenant_add"
    const val actionTenantDetailsClose = "action_tenant_details_close"

    //Room List and Details Actions
    const val actionRoomListClose = "action_room_list_close"
    const val actionRoomDelete = "action_room_list_delete"
    const val actionRoomUpdate = "action_room_list_update"
    const val actionAddNewRoom = "action_room_add"
    const val actionRoomDetailsClose = "action_room_details_close"


    //Generate Bill Actions
    const val actionGenerateBillNotifyUser = "action_generate_bill_notify_user"
    const val actionGenerateBillMarkAsPaid = "action_generate_bill_mark_as_paid"

    //Tutorial Actions
    const val actionTutorialClose = "action_tutorial_close"
}
