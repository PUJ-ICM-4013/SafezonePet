package com.example.screens.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.screens.ui.theme.PetSafeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostPetReportPageWithNavigation(
    navController: NavController,
    reportId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Report") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { AppNavigationBar2(navController = navController) }
    ) { innerPadding ->
        LostPetReportScreen(reportId = reportId, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun LostPetReportScreen(reportId: String, modifier: Modifier = Modifier) {
    val repo = remember { com.example.screens.repository.CommunityReportRepository() }
    var loading by remember { mutableStateOf(true) }
    var report by remember { mutableStateOf<com.example.screens.data.CommunityReport?>(null) }

    LaunchedEffect(reportId) {
        loading = true
        report = repo.getReport(reportId)
        loading = false
    }

    if (loading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val r = report ?: run {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Reporte no encontrado", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Imagen
        if (r.imageUrl.isNotBlank()) {
            coil.compose.AsyncImage(
                model = r.imageUrl,
                contentDescription = "Report image",
                modifier = Modifier.fillMaxWidth().height(250.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.perro),
                contentDescription = "Report image",
                modifier = Modifier.fillMaxWidth().height(250.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = if (r.status.uppercase() == "LOST") "LOST" else "FOUND",
                color = if (r.status.uppercase() == "LOST") MaterialTheme.colorScheme.error else PetSafeGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            Text(text = r.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.height(8.dp))
            Text(text = r.description, fontSize = 16.sp, lineHeight = 20.sp)

            Spacer(Modifier.height(16.dp))

            Text("Contact Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))

            Text(text = r.reporterName.ifBlank { "Reporter" }, fontWeight = FontWeight.Bold)
            Text(text = r.reporterEmail, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(12.dp))

            if (r.reporterPhone.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone")
                        Spacer(Modifier.width(8.dp))
                        Text(r.reporterPhone, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (r.reporterEmail.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                        Spacer(Modifier.width(8.dp))
                        Text(r.reporterEmail, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLostPetReportScreen() {
    LostPetReportPageWithNavigation(
        navController = rememberNavController(),
        reportId = "preview_report_id",
        onBackClick = {}
    )
}