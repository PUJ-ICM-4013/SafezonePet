package com.example.screens.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
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

    // Centrar la imagen si no es cuadrada
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

    // Crear bitmap con borde de color
    val borderSize = 14
    val borderedBitmap = Bitmap.createBitmap(size + borderSize * 2, size + borderSize * 2, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(borderedBitmap)
    val centerX = (size + borderSize * 2) / 2f
    val centerY = (size + borderSize * 2) / 2f

    val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    // Sombra
    paint.style = Paint.Style.FILL
    paint.color = android.graphics.Color.argb(80, 0, 0, 0)
    canvas.drawCircle(centerX + 3, centerY + 3, (size + borderSize) / 2f, paint)

    paint.color = if (isInSafeZone) {
        android.graphics.Color.parseColor("#4CAF50")
    } else {
        android.graphics.Color.parseColor("#F44336")
    }
    canvas.drawCircle(centerX, centerY, (size + borderSize) / 2f, paint)

    // Borde blanco interno
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(centerX, centerY, (size + 4) / 2f, paint)

    // Dibujar la imagen circular encima
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

    // Aplicar estilo JSON
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
            // Ubicación GPS actual del usuario (marcador azul)
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Mi ubicación actual",
                    icon = createUserMarkerIcon(context)
                )
            }

            // Zona segura (residencia/hogar del usuario)
            safeZoneCenter?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Hogar (Zona Segura)",
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
                        // Calcular ruta con OSRM cuando se toca el marcador
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
                        false // Permite que se muestre el InfoWindow
                    }
                )
            }

            // Dibujar ruta
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapPageWithNavigation(
    navController: NavController,
    onSettingsClick: () -> Unit,
    onConnectClick: () -> Unit
) {
    val context = LocalContext.current
    val geofenceHelper = remember { GeofenceHelper(context) }
    val isDarkMode = rememberLightSensor()

    // Obtener ubicación GPS real del dispositivo (para trazar rutas)
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

    // Zona segura (residencia del usuario)
    val safeZoneCenter = remember { LatLng(4.6097, -74.0817) } // Coordenadas del hogar
    val safeZoneRadius = 500f

    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                        Log.d("MapSensor", "Ubicación GPS del dispositivo obtenida: ${it.latitude}, ${it.longitude}")
                    }
                }
            } catch (e: SecurityException) {
                Log.e("MapSensor", "Error obteniendo ubicación GPS: ${e.message}")
            }
        }
    }

    val pets = listOf(
        Pet("Buddy", R.drawable.buddy),
        Pet("Max", R.drawable.max),
        Pet("Charlie", R.drawable.charlie)
    )
    val petLocations = remember {
        listOf(
            PetLocation(pets[0], LatLng(4.6097, -74.0817), true, "Hace 5 min"),
            PetLocation(pets[1], LatLng(4.6110, -74.0830), true, "Hace 10 min"),
            PetLocation(pets[2], LatLng(4.6150, -74.0850), false, "Hace 2 min")
        )
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
                FloatingActionButton(
                    onClick = {
                        geofenceEnabled = !geofenceEnabled
                        if (geofenceEnabled) {
                            // Geofence se crea en la zona segura (hogar)
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

                // Mapa interactivo
                InteractiveMapView(
                    petLocations = petLocations,
                    userLocation = userLocation,
                    safeZoneCenter = safeZoneCenter,
                    safeZoneRadius = safeZoneRadius,
                    selectedPet = selectedPet,
                    routeInfo = currentRoute,
                    isDarkMode = isDarkMode,
                    onLocationClick = { pet ->
                        selectedPet = pet
                    },
                    onRouteCalculated = { route ->
                        currentRoute = route
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Información de la ruta calculada
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
                                    // Imagen circular de la mascota con borde de color
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