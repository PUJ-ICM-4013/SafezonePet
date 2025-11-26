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
import com.example.screens.data.CommunityReport
import com.example.screens.repository.CommunityReportRepository
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreenWithNavigation(
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String,
    onBackClick: () -> Unit,
    onSubmitClick: (String) -> Unit // <- devuelve reportId
) {
    val repo = remember { CommunityReportRepository() }
    val scope = rememberCoroutineScope()

    var status by remember { mutableStateOf("LOST") } // LOST | FOUND
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

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
        }
    }

    val galleryPermissionHandler = rememberPermissionHandler { result ->
        when (result) {
            is PermissionResult.Granted -> galleryLauncher.launch("image/*")
            is PermissionResult.Denied -> showGalleryDenied = true
            is PermissionResult.PermanentlyDenied -> showGalleryDenied = true
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
        }
        tempCameraUri = null
    }

    val cameraPermissionHandler = rememberPermissionHandler { result ->
        when (result) {
            is PermissionResult.Granted -> {
                tempCameraUri = createImageUri()
                cameraLauncher.launch(tempCameraUri!!)
            }
            is PermissionResult.Denied -> showCameraDenied = true
            is PermissionResult.PermanentlyDenied -> showCameraDenied = true
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

    val imageToSubmit = capturedImageUri ?: selectedImageUri

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!submitting) showDialog = false },
            title = { Text("Confirm Report") },
            text = { Text("Are you sure you want to submit this report?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            submitting = true
                            submitError = null
                            try {
                                val report = CommunityReport(
                                    status = status,
                                    title = title.trim(),
                                    description = details.trim(),
                                    imageUrl = imageToSubmit?.toString() ?: "",
                                    reporterUid = currentUserId,
                                    reporterName = currentUserName,
                                    reporterEmail = currentUserEmail,
                                    reporterPhone = phone.trim()
                                )
                                val saved = repo.createReport(report)
                                showDialog = false
                                onSubmitClick(saved.id) // <- navega con reportId
                            } catch (e: Exception) {
                                e.printStackTrace()
                                submitError = "No se pudo enviar el reporte"
                            } finally {
                                submitting = false
                            }
                        }
                    },
                    enabled = !submitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PetSafeGreen,
                        contentColor = Color.Black
                    )
                ) {
                    if (submitting) CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    else Text("Submit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { if (!submitting) showDialog = false }
                ) { Text("Cancel") }
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
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PetSafeGreen,
                    contentColor = Color.Black
                ),
                enabled = title.isNotBlank() && details.isNotBlank() && !submitting
            ) {
                Text("Submit Report", style = MaterialTheme.typography.labelLarge)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Status
            Text("Status", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = status == "LOST",
                    onClick = { status = "LOST" },
                    label = { Text("Lost") }
                )
                FilterChip(
                    selected = status == "FOUND",
                    onClick = { status = "FOUND" },
                    label = { Text("Found") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Report Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = title,
                onValueChange = { title = it; submitError = null },
                label = { Text("Enter Title") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = details,
                onValueChange = { details = it; submitError = null },
                label = { Text("Enter Details") },
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Phone (optional)", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = phone,
                onValueChange = { phone = it; submitError = null },
                label = { Text("Contact phone") }
            )

            if (submitError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(submitError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Media", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            val imageToShow = capturedImageUri ?: selectedImageUri

            if (imageToShow != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = imageToShow,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
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
                                Text("Ready", style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Image attached to report", style = MaterialTheme.typography.bodySmall, color = PetSafeGreen)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val perm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            android.Manifest.permission.READ_MEDIA_IMAGES
                        } else {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }

                        if (galleryPermissionHandler.isPermissionGranted(perm)) {
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
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
        currentUserId = "uid_demo",
        currentUserName = "Demo User",
        currentUserEmail = "demo@email.com",
        onBackClick = {},
        onSubmitClick = { /* reportId */ }
    )
}
