package com.example.screens.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.screens.R
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.permission.*
import com.example.screens.ui.components.AppTextField
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.ScreensTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileHeader(
    name: String,
    breed: String,
    profileImageUri: Uri?,
    onImageClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {

            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Pet profile picture",
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
                Image(
                    painter = painterResource(id = R.drawable.buddy),
                    contentDescription = "Pet profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
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
                    contentDescription = "Change photo",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (name.isNotBlank()) name else "Pet's Name",
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = if (breed.isNotBlank()) breed else "Pet's Breed",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetProfilePageWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pet Profile") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = { AppNavigationBar2(navController = navController) }
    ) { innerPadding ->
        PetProfileScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun PetProfileScreen(modifier: Modifier = Modifier) {
    var petName by remember { mutableStateOf("Buddy") }
    var breed by remember { mutableStateOf("Chiguauga") }
    var age by remember { mutableStateOf("2") }
    var vet by remember { mutableStateOf("Dr. Smith") }
    var address by remember { mutableStateOf("123 Vet Street") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) } // URI temporal para cámara
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

    // Crear URI temporal para la foto
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
            // La foto fue tomada exitosamente
            profileImageUri = tempCameraUri
        }
        tempCameraUri = null
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
            title = { Text("Change Profile Photo") },
            text = { Text("Choose how you want to update your pet's profile picture") },
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(
            name = petName,
            breed = breed,
            profileImageUri = profileImageUri,
            onImageClick = { showImageSourceDialog = true }
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppTextField(
            value = petName,
            onValueChange = { petName = it },
            label = { Text("Pet's Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text("Breed") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age (years)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = vet,
            onValueChange = { vet = it },
            label = { Text("Regular Veterinarian") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Veterinarian's Address") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                println("Profile Saved!")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Save Changes", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetProfileScreenPreview() {
    ScreensTheme {
        Surface {
            PetProfilePageWithNavigation(
                navController = rememberNavController(),
                onBackClick = {}
            )
        }
    }
}