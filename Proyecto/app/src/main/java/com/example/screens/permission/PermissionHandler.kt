package com.example.screens.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.screens.data.PermissionMessage

enum class PermissionType(val permission: String) {
    CAMERA(android.Manifest.permission.CAMERA),
    READ_MEDIA_IMAGES(android.Manifest.permission.READ_MEDIA_IMAGES),
    READ_EXTERNAL_STORAGE(android.Manifest.permission.READ_EXTERNAL_STORAGE)
}

sealed class PermissionResult {
    object Granted : PermissionResult()
    object Denied : PermissionResult()
    object PermanentlyDenied : PermissionResult()
}


@Composable
fun rememberPermissionHandler(
    onPermissionResult: (PermissionResult) -> Unit
): PermissionHandler {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionResult(PermissionResult.Granted)
        } else {
            onPermissionResult(PermissionResult.Denied)
        }
    }

    return remember {
        PermissionHandler(
            context = context,
            permissionLauncher = permissionLauncher,
            onPermissionResult = onPermissionResult
        )
    }
}

class PermissionHandler(
    private val context: Context,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    private val onPermissionResult: (PermissionResult) -> Unit
) {

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun requestPermission(permissionType: PermissionType) {
        val permission = getPermissionForVersion(permissionType)

        when {
            isPermissionGranted(permission) -> {
                onPermissionResult(PermissionResult.Granted)
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun getPermissionForVersion(permissionType: PermissionType): String {
        return when (permissionType) {
            PermissionType.READ_MEDIA_IMAGES -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    android.Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                }
            }
            else -> permissionType.permission
        }
    }
}

object PermissionMessages {

    fun getCameraRationale() = PermissionMessage(
        title = "Camera Permission Required",
        message = "SafeZonePet needs camera access to take photos of your lost pets for reports."
    )

    fun getGalleryRationale() = PermissionMessage(
        title = "Gallery Permission Required",
        message = "SafeZonePet needs gallery access to select photos of your pets for reports and profiles."
    )

    fun getCameraDenied() = PermissionMessage(
        title = "Camera Access Denied",
        message = "Camera permission is required to take photos of lost pets."
    )

    fun getGalleryDenied() = PermissionMessage(
        title = "Gallery Access Denied",
        message = "Gallery permission is required to select photos from your device."
    )
}
