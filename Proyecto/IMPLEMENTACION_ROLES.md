# Sistema de Roles - Dueño vs Paseador

## 📋 Resumen de la Implementación

Se ha implementado un sistema completo de roles de usuario con dos tipos:

### 🏠 **OWNER (Dueño)**
- Tiene una **residencia fija** registrada durante el signup
- La **zona segura es estática** y se centra en las coordenadas de su casa
- Puede monitorear sus mascotas desde su hogar
- Recibe notificaciones cuando las mascotas salen de la zona segura

### 🚶 **WALKER (Paseador/Entrenador)**
- **No requiere dirección de residencia**
- La **zona segura es dinámica** y sigue su ubicación GPS en tiempo real
- Ideal para paseadores que se mueven con las mascotas
- La zona segura se actualiza automáticamente mientras camina

---

## 🗂️ Archivos Creados/Modificados

### ✅ Archivos Nuevos

#### 1. **`data/UserProfile.kt`**
```kotlin
- Modelo de datos para perfiles de usuario
- Incluye UserType enum (OWNER, WALKER)
- Métodos para conversión a/desde Firestore
- Ubicación: app/src/main/java/com/example/screens/data/UserProfile.kt
```

#### 2. **`repository/UserRepository.kt`**
```kotlin
- Repositorio para gestión de perfiles
- Lógica de Firestore lista pero comentada
- Funciona con datos mock mientras tanto
- Ubicación: app/src/main/java/com/example/screens/repository/UserRepository.kt
```

---

### 🔧 Archivos Modificados

#### 1. **`viewmodel/AuthViewModel.kt`**
- ✅ Integrado UserRepository
- ✅ Método `signup()` actualizado con parámetros adicionales (name, userType, homeLocation, etc.)
- ✅ Método `login()` ahora carga el perfil del usuario
- ✅ StateFlow `currentUserProfile` para observar el perfil actual
- ✅ Métodos: `updateHomeLocation()`, `loadUserProfile()`, `logout()`

#### 2. **`ui/Signup.kt`**
- ✅ Selector visual de rol (botones OWNER/WALKER con emojis)
- ✅ Campos adicionales: name, phoneNumber
- ✅ Campo homeAddress **condicional** (solo visible para OWNER)
- ✅ Validaciones actualizadas
- ✅ Integración con nuevo método signup del ViewModel

#### 3. **`ui/Login.kt`**
- ✅ Actualizado para recibir UserProfile en el callback
- ✅ Manejo de errores mejorado

#### 4. **`ui/MapSensor.kt`**
- ✅ Función `MapPageWithNavigation` acepta parámetro `userProfile`
- ✅ Función `InteractiveMapView` acepta parámetro `userProfile`
- ✅ **Zona segura dinámica** según tipo de usuario:
  ```kotlin
  val safeZoneCenter = when (userProfile?.userType) {
      UserType.OWNER -> userProfile.homeLocation  // Fija
      UserType.WALKER -> userLocation              // Sigue GPS
      else -> defaultLocation
  }
  ```
- ✅ Marcador del mapa muestra texto diferente según rol

#### 5. **`navigation/Navigation.kt`**
- ✅ AuthViewModel compartido a nivel de navegación
- ✅ userProfile pasado a MapPageWithNavigation
- ✅ Imports agregados: collectAsState, getValue

---

## 🔌 Cómo Conectar Firestore

### Paso 1: Descomentar Código en `UserRepository.kt`

Busca las secciones marcadas con `// FIRESTORE` y descomentalas:

```kotlin
// DESCOMENTAR ESTA LÍNEA:
// import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(
    // DESCOMENTAR ESTA LÍNEA:
    // private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
```

### Paso 2: En cada método, comenta MOCK DATA y descomenta FIRESTORE

**Ejemplo en `saveUserProfile()`:**

```kotlin
suspend fun saveUserProfile(userProfile: UserProfile): Boolean {
    return try {
        // COMENTAR ESTAS LÍNEAS (MOCK DATA):
        /*
        delay(500)
        mockUserProfiles[userProfile.userId] = userProfile
        _currentUserProfile.value = userProfile
        true
        */

        // DESCOMENTAR ESTAS LÍNEAS (FIRESTORE):
        firestore.collection("users")
            .document(userProfile.userId)
            .set(userProfile.toFirestoreMap())
            .await()
        _currentUserProfile.value = userProfile
        true

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
```

### Paso 3: Agregar dependencia de Firestore

En `app/build.gradle.kts`, verifica que tengas:

```kotlin
dependencies {
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    // ... otras dependencias
}
```

### Paso 4: Estructura de Firestore

La colección en Firestore será:

```
users (collection)
  ├── {userId} (document)
  │   ├── userId: String
  │   ├── email: String
  │   ├── name: String
  │   ├── userType: "OWNER" | "WALKER"
  │   ├── homeLatitude: Double (nullable)
  │   ├── homeLongitude: Double (nullable)
  │   ├── homeAddress: String
  │   ├── phoneNumber: String
  │   ├── profileImageUrl: String
  │   └── createdAt: Long
```

---

## 🚀 Flujo de Usuario

### Registro (Signup)

1. Usuario selecciona su rol: **Owner** o **Walker**
2. Completa campos básicos: nombre, email, teléfono, contraseña
3. **Si es Owner**: se solicita dirección de casa
4. **Si es Walker**: no se solicita dirección
5. Al crear cuenta:
   - Firebase Auth crea el usuario
   - Se guarda el UserProfile en Firestore (o mock)
   - Se redirige a login

### Login

1. Usuario ingresa credenciales
2. Firebase Auth autentica
3. Se carga el UserProfile desde Firestore
4. Se almacena en AuthViewModel.currentUserProfile
5. Navega al mapa

### Mapa

1. **Owner**:
   - Zona segura verde centrada en `homeLocation`
   - Marcador dice "Hogar (Zona Segura)"
   - Zona no se mueve

2. **Walker**:
   - Zona segura verde centrada en `userLocation` (GPS actual)
   - Marcador dice "Mi Ubicación (Zona Móvil)"
   - Zona se actualiza con el movimiento GPS

---

## 📝 Notas Importantes

### Geocodificación Pendiente

En `Signup.kt` línea 585, hay un TODO:

```kotlin
// TODO: Usar Geocoding API de Google Maps para convertir dirección a coordenadas
val homeLocation = if (selectedUserType == UserType.OWNER) {
    LatLng(4.6097, -74.0817) // Placeholder de Bogotá
} else null
```

**Para implementar geocodificación:**

1. Habilitar Geocoding API en Google Cloud Console
2. Usar biblioteca `com.google.android.gms.location.Geocoder`
3. Ejemplo:
   ```kotlin
   val geocoder = Geocoder(context)
   val addresses = geocoder.getFromLocationName(homeAddress, 1)
   val location = addresses?.firstOrNull()?.let {
       LatLng(it.latitude, it.longitude)
   }
   ```

### Permisos de Ubicación

El paseador necesita permisos de ubicación en tiempo real. Los permisos ya están manejados en:
- `MapSensor.kt`: solicita ACCESS_FINE_LOCATION y ACCESS_COARSE_LOCATION
- `AndroidManifest.xml`: permisos declarados

### Datos de Prueba

Para testing sin Firestore, usa el método:
```kotlin
userRepository.createMockProfile(userId, UserType.WALKER)
```

---

## 🎨 Diseño UI

### Selector de Rol en Signup

```
┌─────────────────────────────────────┐
│  I am a:                            │
│                                     │
│  ┌──────────┐  ┌──────────┐       │
│  │    🏠    │  │    🚶    │       │
│  │  Owner   │  │  Walker  │       │
│  └──────────┘  └──────────┘       │
│                                     │
│  Zona segura fija alrededor de...  │
└─────────────────────────────────────┘
```

Botón seleccionado: fondo verde (`PetSafeGreen`)
Botón no seleccionado: fondo transparente

---

## ✅ Testing

### Para probar Owner:
1. Registrarse seleccionando "Owner"
2. Completar dirección de casa
3. En el mapa, la zona verde debe estar centrada en las coordenadas guardadas
4. Moverse físicamente NO debe mover la zona segura

### Para probar Walker:
1. Registrarse seleccionando "Walker"
2. NO se solicita dirección
3. En el mapa, la zona verde debe seguir tu ubicación GPS
4. Moverse físicamente SÍ debe mover la zona segura

---

## 🔮 Próximas Mejoras

### Sugerencias de expansión:

1. **Asignación de Paseadores a Mascotas**
   - Dueño puede asignar un paseador a su mascota
   - El dueño ve la ubicación del paseador en tiempo real

2. **Historial de Paseos**
   - Guardar rutas de paseadores con timestamps
   - Mostrar estadísticas (distancia, duración)

3. **Roles Adicionales**
   - VETERINARIAN: clínicas veterinarias
   - HOTEL: hoteles para mascotas
   - FAMILY_MEMBER: miembros de la familia con acceso limitado

4. **Notificaciones Contextuales**
   - Dueño recibe alerta si paseador sale de zona permitida
   - Paseador recibe recordatorios de check-in

5. **Geocodificación Automática**
   - Integrar Google Places Autocomplete
   - Validación de direcciones en tiempo real

---

## 📞 Soporte

Si tienes dudas o encuentras bugs, revisa:
- `UserProfile.kt:1` - Modelo de datos
- `UserRepository.kt:1` - Lógica de persistencia
- `AuthViewModel.kt:32` - Método login()
- `AuthViewModel.kt:56` - Método signup()
- `MapSensor.kt:401` - Lógica de zona dinámica

---

**Implementación completada el:** $(date)
**Estado:** ✅ Funcional con datos mock, listo para conectar Firestore
**Testing:** ⚠️ Pendiente testing completo con Firebase
