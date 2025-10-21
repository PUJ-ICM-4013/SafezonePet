package com.example.screens.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.screens.permission.*
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.ScreensTheme
import com.example.screens.ui.auth.AuthViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfilePhotoSelector(
    profileImageUri: Uri?,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clickable { onImageClick() },
        contentAlignment = Alignment.Center
    ) {

        if (profileImageUri != null) {
            AsyncImage(
                model = profileImageUri,
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = 4.dp,
                        color = PetSafeGreen,
                        shape = CircleShape
                    )
            )
        } else {

            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default profile",
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Surface(
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape,
            color = PetSafeGreen
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Add photo",
                tint = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SignupScreenWithNavigation(
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onSignupSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val viewModelError by viewModel.error
    LaunchedEffect(viewModelError) {
        if (viewModelError != null) {
            showError = true
            errorMessage = viewModelError ?: "An error occurred"
            isLoading = false
        }
    }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) } // URI temporal para la cámara
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showCameraRationale by remember { mutableStateOf(false) }
    var showGalleryRationale by remember { mutableStateOf(false) }
    var showCameraDenied by remember { mutableStateOf(false) }
    var showGalleryDenied by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    val galleryPermissionHandler = rememberPermissionHandler { result ->
        when (result) {
            is PermissionResult.Granted -> {
                galleryLauncher.launch("image/*")
            }
            is PermissionResult.Denied -> {
                showGalleryDenied = true
            }
            is PermissionResult.PermanentlyDenied -> {
                showGalleryDenied = true
            }
        }
    }

    fun createImageUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "PROFILE_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(null)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            profileImageUri = tempCameraUri
            println("Profile photo captured successfully: $profileImageUri")
        } else {
            println("Photo capture failed")
            tempCameraUri = null
        }
    }

    val cameraPermissionHandler = rememberPermissionHandler { result ->
        when (result) {
            is PermissionResult.Granted -> {
                tempCameraUri = createImageUri()
                cameraLauncher.launch(tempCameraUri!!)
            }
            is PermissionResult.Denied -> {
                showCameraDenied = true
            }
            is PermissionResult.PermanentlyDenied -> {
                showCameraDenied = true
            }
        }
    }
    if (showCameraRationale) {
        val message = PermissionMessages.getCameraRationale()
        PermissionRationaleDialog(
            title = message.title,
            message = message.message,
            onDismiss = { showCameraRationale = false },
            onConfirm = {
                showCameraRationale = false
                cameraPermissionHandler.requestPermission(PermissionType.CAMERA)
            }
        )
    }

    if (showGalleryRationale) {
        val message = PermissionMessages.getGalleryRationale()
        PermissionRationaleDialog(
            title = message.title,
            message = message.message,
            onDismiss = { showGalleryRationale = false },
            onConfirm = {
                showGalleryRationale = false
                galleryPermissionHandler.requestPermission(PermissionType.READ_MEDIA_IMAGES)
            }
        )
    }

    if (showCameraDenied) {
        val message = PermissionMessages.getCameraDenied()
        PermissionPermanentlyDeniedDialog(
            title = message.title,
            message = message.message,
            onDismiss = { showCameraDenied = false },
            onOpenSettings = {
                showCameraDenied = false
                cameraPermissionHandler.openAppSettings()
            }
        )
    }

    if (showGalleryDenied) {
        val message = PermissionMessages.getGalleryDenied()
        PermissionPermanentlyDeniedDialog(
            title = message.title,
            message = message.message,
            onDismiss = { showGalleryDenied = false },
            onOpenSettings = {
                showGalleryDenied = false
                galleryPermissionHandler.openAppSettings()
            }
        )
    }
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Add Profile Photo") },
            text = { Text("Choose how you want to add your profile picture") },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            if (cameraPermissionHandler.isPermissionGranted(android.Manifest.permission.CAMERA)) {
                                tempCameraUri = createImageUri()
                                cameraLauncher.launch(tempCameraUri!!)
                            } else {
                                showCameraRationale = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PetSafeGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Take Photo")
                    }

                    Button(
                        onClick = {
                            showImageSourceDialog = false
                            if (galleryPermissionHandler.isPermissionGranted(
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                        android.Manifest.permission.READ_MEDIA_IMAGES
                                    } else {
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                                    }
                                )) {
                                galleryLauncher.launch("image/*")
                            } else {
                                showGalleryRationale = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PetSafeGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose from Gallery")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "SafeZonePet",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProfilePhotoSelector(
            profileImageUri = profileImageUri,
            onImageClick = { showImageSourceDialog = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add profile photo (optional)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Create your account",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            value = email,
            onValueChange = {
                email = it
                showError = false
            },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                showError = false
            },
            label = { Text("Confirm password") }
        )

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    email.isBlank() -> {
                        showError = true
                        errorMessage = "Please enter an email"
                    }
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        showError = true
                        errorMessage = "Please enter a valid email"
                    }
                    password.isBlank() -> {
                        showError = true
                        errorMessage = "Please enter a password"
                    }
                    password.length < 6 -> {
                        showError = true
                        errorMessage = "Password must be at least 6 characters"
                    }
                    password != confirmPassword -> {
                        showError = true
                        errorMessage = "Passwords do not match"
                    }
                    else -> {
                        showError = false
                        isLoading = true
                        viewModel.signup(email.trim(), password) {
                            isLoading = false
                            onSignupSuccess()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign up", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Already have an account? Sign in",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onSignInClick() }
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun SignupScreenPreview() {
    ScreensTheme {
        Surface {
            SignupScreenWithNavigation(
                onSignupSuccess = {},
                onBackClick = {},
                onSignInClick = {}
            )
        }
    }
}