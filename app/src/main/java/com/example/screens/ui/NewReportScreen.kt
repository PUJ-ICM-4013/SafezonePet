package com.example.screens.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.screens.permission.*
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.components.AppTextField
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreenWithNavigation(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) } // URI temporal para la cámara
    var showCameraRationale by remember { mutableStateOf(false) }
    var showGalleryRationale by remember { mutableStateOf(false) }
    var showCameraDenied by remember { mutableStateOf(false) }
    var showGalleryDenied by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            capturedImageUri = null
            println("Image selected from gallery: $uri")
        }
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
        val imageFileName = "JPEG_${timeStamp}_"
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
            capturedImageUri = tempCameraUri
            selectedImageUri = null
            println("Photo captured successfully: $capturedImageUri")
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Report") },
            text = { Text("Are you sure you want to submit this lost pet report?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        val imageToSubmit = capturedImageUri ?: selectedImageUri
                        println("Submitting report with image: $imageToSubmit")
                        onSubmitClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PetSafeGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Report") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (title.isNotBlank() && details.isNotBlank()) {
                        showDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PetSafeGreen,
                    contentColor = Color.Black
                ),
                enabled = title.isNotBlank() && details.isNotBlank()
            ) {
                Text("Submit Report", style = MaterialTheme.typography.labelLarge)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            Text("Report Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Enter Title") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Enter Details") },
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))


            Text("Media", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            val imageToShow = capturedImageUri ?: selectedImageUri

            if (imageToShow != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imageToShow,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            onError = { error ->
                                println("Error loading image: ${error.result.throwable.message}")
                            },
                            onSuccess = {
                                println("Image loaded successfully: $imageToShow")
                            }
                        )

                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = PetSafeGreen
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Image loaded",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "Ready",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Image attached to report",
                    style = MaterialTheme.typography.bodySmall,
                    color = PetSafeGreen
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
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
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PetSafeGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Upload Photo")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    if (cameraPermissionHandler.isPermissionGranted(android.Manifest.permission.CAMERA)) {
                        tempCameraUri = createImageUri()
                        cameraLauncher.launch(tempCameraUri!!)
                    } else {
                        showCameraRationale = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, PetSafeGreen),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = "Take Photo")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take Photo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewReportScreen() {
    NewReportScreenWithNavigation(
        onBackClick = {},
        onSubmitClick = {}
    )
}