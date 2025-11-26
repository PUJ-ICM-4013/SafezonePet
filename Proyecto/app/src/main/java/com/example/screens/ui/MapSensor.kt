package com.example.screens.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.screens.data.GeofenceData
import com.example.screens.data.Pet
import com.example.screens.R
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.geofence.GeofenceHelper
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.TextWhite
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.example.screens.data.PetLocation
import com.example.screens.data.RouteInfo
import com.example.screens.sensors.rememberLightSensor
import com.example.screens.repository.RouteRepository
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.delay

// ============================================================================
// IMPORTAR REALTIME DATABASE Y NOTIFICACIONES
// ============================================================================
import com.example.screens.repository.RealtimeLocationRepository
import com.example.screens.data.RealtimeLocation
import com.example.screens.notifications.NotificationHelper

// Funciones auxiliares (sin cambios)
fun getCircularBitmap(bitmap: Bitmap): Bitmap {
    val size = minOf(bitmap.width, bitmap.height)
    val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }

    val rect = Rect(0, 0, size, size)
    val radius = size / 2f

    canvas.drawARGB(0, 0, 0, 0)
    canvas.drawCircle(radius, radius, radius, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    val left = (bitmap.width - size) / 2f
    val top = (bitmap.height - size) / 2f
    val srcRect = Rect(left.toInt(), top.toInt(), (left + size).toInt(), (top + size).toInt())

    canvas.drawBitmap(bitmap, srcRect, rect, paint)

    return output
}

fun createCircularMarkerIcon(
    context: Context,
    drawableId: Int,
    isInSafeZone: Boolean,
    size: Int = 120
): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    val bitmap = drawable?.toBitmap(size, size, Bitmap.Config.ARGB_8888)
        ?: Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    val circularBitmap = getCircularBitmap(bitmap)

    val borderSize = 14
    val borderedBitmap = Bitmap.createBitmap(size + borderSize * 2, size + borderSize * 2, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(borderedBitmap)
    val centerX = (size + borderSize * 2) / 2f
    val centerY = (size + borderSize * 2) / 2f

    val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.argb(80, 0, 0, 0)
    canvas.drawCircle(centerX + 3, centerY + 3, (size + borderSize) / 2f, paint)

    paint.color = if (isInSafeZone) {
        android.graphics.Color.parseColor("#4CAF50")
    } else {
        android.graphics.Color.parseColor("#F44336")
    }
    canvas.drawCircle(centerX, centerY, (size + borderSize) / 2f, paint)

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(centerX, centerY, (size + 4) / 2f, paint)

    canvas.drawBitmap(circularBitmap, borderSize.toFloat(), borderSize.toFloat(), null)

    return BitmapDescriptorFactory.fromBitmap(borderedBitmap)
}

fun createUserMarkerIcon(context: Context, size: Int = 100): BitmapDescriptor {
    val bitmap = Bitmap.createBitmap(size + 20, size + 20, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val centerX = (size + 20) / 2f
    val centerY = (size + 20) / 2f

    val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.argb(50, 0, 0, 0)
    canvas.drawCircle(centerX + 2, centerY + 2, size / 2f, paint)

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(centerX, centerY, size / 2f, paint)

    paint.color = android.graphics.Color.parseColor("#2196F3")
    canvas.drawCircle(centerX, centerY, (size / 2f) - 6, paint)

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(centerX, centerY, size / 8f, paint)

    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 3f
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(centerX, centerY, (size / 2f) - 3, paint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InteractiveMapView(
    modifier: Modifier = Modifier,
    petLocations: List<PetLocation>,
    userLocation: LatLng?,
    safeZoneCenter: LatLng?,
    safeZoneRadius: Float = 500f,
    selectedPet: PetLocation? = null,
    routeInfo: RouteInfo? = null,
    isDarkMode: Boolean = false,
    userProfile: com.example.screens.data.UserProfile? = null,
    onLocationClick: (PetLocation) -> Unit = {},
    onRouteCalculated: (RouteInfo?) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val routeRepository = remember { RouteRepository() }

    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val defaultLocation = LatLng(4.6097, -74.0817)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedPet?.location ?: safeZoneCenter ?: userLocation ?: defaultLocation,
            15f
        )
    }

    LaunchedEffect(selectedPet) {
        selectedPet?.let {
            cameraPositionState.animate(
                update = com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(it.location, 16f),
                durationMs = 1000
            )
        }
    }

    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = locationPermissions.allPermissionsGranted,
                mapType = MapType.NORMAL,
                mapStyleOptions = null
            )
        )
    }

    LaunchedEffect(isDarkMode) {
        try {
            val styleRes = if (isDarkMode) R.raw.map_style_dark else R.raw.map_style_light
            val style = MapStyleOptions.loadRawResourceStyle(context, styleRes)
            mapProperties = mapProperties.copy(mapStyleOptions = style)
            Log.i("MapStyle", "Estilo aplicado: ${if (isDarkMode) "oscuro" else "claro"}")
        } catch (e: Exception) {
            Log.e("MapStyle", "Error al cargar estilo: ${e.localizedMessage}")
        }
    }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true,
            compassEnabled = true
        )
    }

    Column(modifier = modifier) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!locationPermissions.allPermissionsGranted) {
                Button(
                    onClick = { locationPermissions.launchMultiplePermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = PetSafeGreen)
                ) {
                    Icon(Icons.Default.MyLocation, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Habilitar ubicación")
                }
            }

            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) { Text("Tipo de mapa") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Normal") }, onClick = {
                        mapProperties = mapProperties.copy(mapType = MapType.NORMAL)
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Satélite") }, onClick = {
                        mapProperties = mapProperties.copy(mapType = MapType.SATELLITE)
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Terreno") }, onClick = {
                        mapProperties = mapProperties.copy(mapType = MapType.TERRAIN)
                        expanded = false
                    })
                }
            }
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(12.dp)),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Mi ubicación actual",
                    icon = createUserMarkerIcon(context)
                )
            }

            safeZoneCenter?.let {
                val markerTitle = when (userProfile?.userType) {
                    com.example.screens.data.UserType.OWNER -> "Hogar (Zona Segura)"
                    com.example.screens.data.UserType.WALKER -> "Mi Ubicación (Zona Móvil)"
                    else -> "Zona Segura"
                }
                Marker(
                    state = MarkerState(position = it),
                    title = markerTitle,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
                Circle(
                    center = it,
                    radius = safeZoneRadius.toDouble(),
                    fillColor = androidx.compose.ui.graphics.Color(0x2200FF00),
                    strokeColor = PetSafeGreen,
                    strokeWidth = 2f
                )
            }

            petLocations.forEach { pet ->
                Marker(
                    state = MarkerState(position = pet.location),
                    title = pet.pet.name,
                    snippet = "Toca para ver la ruta",
                    icon = createCircularMarkerIcon(
                        context,
                        pet.pet.imageRes,
                        pet.isInSafeZone
                    ),
                    onClick = {
                        onLocationClick(pet)
                        userLocation?.let { origin ->
                            scope.launch {
                                val result = routeRepository.getRoute(origin, pet.location)
                                result.fold(
                                    onSuccess = { route ->
                                        onRouteCalculated(route)
                                        Log.d("MapSensor", "Ruta calculada: ${route.distance}, ${route.duration}")
                                    },
                                    onFailure = { error ->
                                        Log.e("MapSensor", "Error calculando ruta: ${error.message}")
                                        onRouteCalculated(null)
                                    }
                                )
                            }
                        }
                        false
                    }
                )
            }

            routeInfo?.polylinePoints?.takeIf { it.isNotEmpty() }?.let {
                Polyline(points = it, color = PetSafeGreen, width = 10f)
            }
        }
    }
}

@Composable
fun SearchBar(
    pets: List<PetLocation>,
    onPetFound: (PetLocation?) -> Unit
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            val found = pets.find { pet -> pet.pet.name.contains(it, true) }
            onPetFound(found)
        },
        placeholder = { Text("Buscar mascota...") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / 60000
    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "Hace $minutes min"
        else -> "Hace ${minutes/60} h"
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapPageWithNavigation(
    navController: NavController,
    userProfile: com.example.screens.data.UserProfile? = null,
    onSettingsClick: () -> Unit,
    onConnectClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val geofenceHelper = remember { GeofenceHelper(context) }
    val isDarkMode = rememberLightSensor()

    val realtimeRepo = remember { RealtimeLocationRepository() }
    val petRepo = remember { com.example.screens.repository.PetRepository() }

    // Estado para ubicaciones en tiempo real desde Firebase
    var realtimeLocations by remember { mutableStateOf<List<RealtimeLocation>>(emptyList()) }
    var userPets by remember { mutableStateOf<List<com.example.screens.data.PetData>>(emptyList()) }

    // Control de simulación
    var isSimulationRunning by remember { mutableStateOf(false) }

    // Sistema de notificaciones
    val notificationHelper = remember { NotificationHelper(context) }

    // Rastrear estado previo de cada mascota (para detectar cambios)
    val previousSafeZoneState = remember { mutableStateMapOf<String, Boolean>() }

    // Obtener ubicación GPS real del dispositivo
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
    }

    // Zona segura dinámica según el tipo de usuario
    val safeZoneCenter = remember(userProfile, userLocation) {
        when (userProfile?.userType) {
            com.example.screens.data.UserType.OWNER -> {
                userProfile.homeLocation ?: LatLng(4.6097, -74.0817)
            }
            com.example.screens.data.UserType.WALKER -> {
                userLocation ?: LatLng(4.6097, -74.0817)
            }
            else -> LatLng(4.6097, -74.0817)
        }
    }
    val safeZoneRadius = 500f

    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                        Log.d("MapSensor", "📍 Ubicación GPS: ${it.latitude}, ${it.longitude}")
                    }
                }
            } catch (e: SecurityException) {
                Log.e("MapSensor", "Error obteniendo ubicación GPS: ${e.message}")
            }
        }
    }

    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            Log.d("MapSensor", "🐕 Cargando mascotas del usuario ${user.uid}")
            petRepo.getPetsByOwner(
                ownerId = user.uid,
                callback = { pets ->
                    userPets = pets
                    Log.d("MapSensor", "✅ Mascotas cargadas: ${pets.size}")
                    pets.forEach { pet ->
                        Log.d("MapSensor", "  - ${pet.name}")
                    }
                },
                onError = { error ->
                    Log.e("MapSensor", " Error cargando mascotas: $error")
                }
            )
        }
    }

    DisposableEffect(Unit) {
        Log.d("MapSensor", " Iniciando listener de Firebase Realtime Database")

        val listener = realtimeRepo.listenToLocations(
            callback = { locations ->
                realtimeLocations = locations
                Log.d("MapSensor", " Ubicaciones actualizadas: ${locations.size}")
                locations.forEach { loc ->
                    Log.d("MapSensor", "  - ${loc.petName}: (${loc.latitude}, ${loc.longitude})")
                }
            },
            onError = { error ->
                Log.e("MapSensor", "Error en listener: $error")
            }
        )

        onDispose {
            Log.d("MapSensor", " Removiendo listener")
            realtimeRepo.removeListener()
        }
    }

    val petPositions = remember { mutableStateMapOf<String, LatLng>() }

    LaunchedEffect(isSimulationRunning, userPets) {
        if (!isSimulationRunning || userPets.isEmpty()) return@LaunchedEffect

        // Inicializar posiciones de inicio si no existen
        userPets.forEachIndexed { index, pet ->
            if (!petPositions.containsKey(pet.petId)) {
                val initialLocation = when (index % 3) {
                    0 -> LatLng(4.6097, -74.0817)
                    1 -> LatLng(4.6100, -74.0820)
                    else -> LatLng(4.6095, -74.0815)
                }
                petPositions[pet.petId] = initialLocation
                Log.d("MapSensor", "📍 ${pet.name} posición inicial: $initialLocation")
            }
        }

        Log.d("MapSensor", "🎮 Iniciando simulación de ubicaciones...")

        while (isSimulationRunning && userPets.isNotEmpty()) {
            kotlinx.coroutines.delay(2000)

            userPets.forEachIndexed { index, pet ->
                // Obtener posición actual
                val currentPos = petPositions[pet.petId] ?: LatLng(4.6097, -74.0817)

                // Cada mascota se mueve en una dirección específica
                val (latIncrement, lngIncrement) = when (index % 3) {
                    0 -> Pair(0.0010, 0.0010)
                    1 -> Pair(-0.0010, 0.0010)
                    else -> Pair(0.0010, -0.0010)
                }

                // Nueva posición moviéndose constantemente en la misma dirección
                val randomLat = currentPos.latitude + latIncrement
                val randomLng = currentPos.longitude + lngIncrement

                // Actualizar posición almacenada para la próxima iteración
                petPositions[pet.petId] = LatLng(randomLat, randomLng)

                // Calcular si está en zona segura
                val petLoc = LatLng(randomLat, randomLng)
                val distance = FloatArray(1)
                android.location.Location.distanceBetween(
                    petLoc.latitude, petLoc.longitude,
                    safeZoneCenter.latitude, safeZoneCenter.longitude,
                    distance
                )
                val isInZone = distance[0] <= safeZoneRadius

                val location = RealtimeLocation(
                    petId = pet.petId,
                    petName = pet.name,
                    latitude = randomLat,
                    longitude = randomLng,
                    timestamp = System.currentTimeMillis(),
                    isInSafeZone = isInZone
                )

                realtimeRepo.updatePetLocation(
                    location = location,
                    onSuccess = {
                        Log.d("MapSensor", " ${pet.name} ubicación simulada")
                    }
                )
            }
        }
    }



   //registrar mascotas desde realtime
    val petLocations = remember(realtimeLocations, safeZoneCenter, safeZoneRadius) {
        val petImages = mapOf(
            "Buddy" to R.drawable.buddy,
            "Max" to R.drawable.max,
            "Charlie" to R.drawable.charlie
        )

        realtimeLocations.map { rtLocation ->
            // RECALCULAR si está en zona segura
            val petLoc = LatLng(rtLocation.latitude, rtLocation.longitude)
            val distance = FloatArray(1)
            android.location.Location.distanceBetween(
                petLoc.latitude, petLoc.longitude,
                safeZoneCenter.latitude, safeZoneCenter.longitude,
                distance
            )
            val isInSafeZone = distance[0] <= safeZoneRadius

            // Detectar cambios de estado y enviar notificaciones
            val previousState = previousSafeZoneState[rtLocation.petId]
            if (previousState != null && previousState != isInSafeZone) {
                // Hubo un cambio de estado
                if (isInSafeZone) {
                    // REGRESÓ a zona segura
                    Log.d("MapSensor", " ${rtLocation.petName} REGRESÓ a zona segura")
                    notificationHelper.sendGeofenceEnterNotification(rtLocation.petName)
                } else {
                    // SALIÓ de zona segura
                    Log.d("MapSensor", "${rtLocation.petName} SALIÓ de zona segura")
                    notificationHelper.sendGeofenceExitNotification(rtLocation.petName)
                }
            }
            // Actualizar estado anterior
            previousSafeZoneState[rtLocation.petId] = isInSafeZone

            // Log para debugging
            val statusIcon = if (isInSafeZone) "" else "⚠ "
            val statusText = if (isInSafeZone) "SEGURA" else "FUERA DE ZONA"
            Log.d("MapSensor", "$statusIcon ${rtLocation.petName}: $statusText (${distance[0].toInt()}m de zona segura)")

            PetLocation(
                pet = Pet(
                    name = rtLocation.petName,
                    imageRes = petImages[rtLocation.petName] ?: R.drawable.perro
                ),
                location = petLoc,
                isInSafeZone = isInSafeZone,
                lastUpdate = formatTimestamp(rtLocation.timestamp)
            )
        }
    }

    var selectedPet by remember { mutableStateOf<PetLocation?>(null) }
    var currentRoute by remember { mutableStateOf<RouteInfo?>(null) }
    var geofenceEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SafeZone") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        bottomBar = { AppNavigationBar2(navController = navController) },
        floatingActionButton = {
            Column {
                // Botón de Simulacion
                if (userPets.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            isSimulationRunning = !isSimulationRunning
                            Log.d("MapSensor", if (isSimulationRunning) " Simulación iniciada" else "Simulación detenida")
                        },
                        containerColor = if (isSimulationRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                        contentColor = TextWhite,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            if (isSimulationRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isSimulationRunning) "Detener Simulación" else "Iniciar Simulación"
                        )
                    }
                }

                // Boton de Geofence
                FloatingActionButton(
                    onClick = {
                        geofenceEnabled = !geofenceEnabled
                        if (geofenceEnabled) {
                            val geofenceData = GeofenceData(
                                id = "safezone",
                                latitude = safeZoneCenter.latitude,
                                longitude = safeZoneCenter.longitude,
                                radius = safeZoneRadius,
                                petId = "zona",
                                petName = "zona segura"
                            )
                            geofenceHelper.addGeofence(geofenceData, {}, {})
                        } else {
                            geofenceHelper.removeAllGeofences({}, {})
                        }
                    },
                    containerColor = if (geofenceEnabled) PetSafeGreen else MaterialTheme.colorScheme.secondary,
                    contentColor = TextWhite,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.Shield, null)
                }

                // Botón de Conectar/Agregar Mascota
                FloatingActionButton(
                    onClick = onConnectClick,
                    containerColor = PetSafeGreen,
                    contentColor = TextWhite
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                SearchBar(pets = petLocations, onPetFound = { found -> selectedPet = found })
                Spacer(Modifier.height(8.dp))

                InteractiveMapView(
                    petLocations = petLocations,
                    userLocation = userLocation,
                    safeZoneCenter = safeZoneCenter,
                    safeZoneRadius = safeZoneRadius,
                    selectedPet = selectedPet,
                    routeInfo = currentRoute,
                    isDarkMode = isDarkMode,
                    userProfile = userProfile,
                    onLocationClick = { pet ->
                        selectedPet = pet
                    },
                    onRouteCalculated = { route ->
                        currentRoute = route
                    }
                )

                Spacer(Modifier.height(16.dp))

                currentRoute?.let { route ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = PetSafeGreen.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Ruta a ${selectedPet?.pet?.name ?: "mascota"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Row {
                                    Icon(
                                        Icons.Default.DirectionsWalk,
                                        contentDescription = null,
                                        tint = TextWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "${route.distance} • ${route.duration}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextWhite
                                    )
                                }
                            }
                            IconButton(
                                onClick = { currentRoute = null }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar ruta",
                                    tint = TextWhite
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color.Black
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Estado de Mascotas",
                            style = MaterialTheme.typography.titleMedium,
                            color = PetSafeGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))

                        if (petLocations.isEmpty()) {
                            Text(
                                "Esperando datos de Firebase...",
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        petLocations.forEach { pet ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPet = pet
                                    }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    AsyncImage(
                                        model = pet.pet.imageRes,
                                        contentDescription = pet.pet.name,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = 3.dp,
                                                color = if (pet.isInSafeZone) PetSafeGreen else MaterialTheme.colorScheme.error,
                                                shape = CircleShape
                                            ),
                                        contentScale = ContentScale.Crop,
                                        error = painterResource(R.drawable.perro)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = pet.pet.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = androidx.compose.ui.graphics.Color.White
                                        )
                                        Text(
                                            text = pet.lastUpdate,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (pet.isInSafeZone)
                                        PetSafeGreen.copy(alpha = 0.1f)
                                    else
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = if (pet.isInSafeZone) "✓ Segura" else "⚠ Alerta",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (pet.isInSafeZone) PetSafeGreen else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            if (pet != petLocations.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}