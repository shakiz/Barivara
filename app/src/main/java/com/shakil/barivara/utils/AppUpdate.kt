package com.shakil.barivara.utils

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class AppUpdateHelper(
    context: Context
) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)

    companion object {
        const val IMMEDIATE_UPDATE_REQUEST_CODE = 100
    }

    // Check for an update
    fun checkForUpdate(
        onUpdateAvailable: () -> Unit,
        onUpdateNotAvailable: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    onUpdateAvailable()
                }

                else -> {
                    onUpdateNotAvailable()
                }
            }
        }.addOnFailureListener {
            onError(it)
        }
    }

    // Start immediate update flow
    fun startImmediateUpdate(activity: Activity) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    activity,
                    IMMEDIATE_UPDATE_REQUEST_CODE
                )
            }
        }
    }

    // Handle update completion
    fun completeUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }
}
