package com.shakil.barivara.model.notification

class Notification(
    NotificationId: String?,
    Title: String?,
    Message: String?,
    DateTime: String?,
    var seenStatus: Int
) {
    var notificationId: String? = NotificationId
    var title: String? = Title
    var message: String? = Message
    var dateTime: String? = DateTime

}
