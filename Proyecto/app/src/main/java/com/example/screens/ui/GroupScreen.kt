package com.example.screens.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.screens.data.Group
import com.example.screens.footer.AppNavigationBar2
import com.example.screens.ui.theme.InputGreen
import com.example.screens.ui.theme.PetSafeGreen
import com.example.screens.ui.theme.ScreensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreenWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit,
    onCreateGroupClick: () -> Unit = {},
    onGroupClick: (String) -> Unit = {}
) {
    val sampleGroups = listOf(
        Group(1, "Family", "2 members"),
        Group(2, "Friends", "3 members"),
        Group(3, "Walkers", "1 member")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Groups") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            AppNavigationBar2(navController = navController)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Create Group") },
                icon = { Icon(Icons.Default.Add, contentDescription = "Create Group") },
                onClick = { onCreateGroupClick() },
                containerColor = PetSafeGreen,
                contentColor = Color.Black
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "My Groups",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(sampleGroups) { group ->
                GroupItem(
                    group = group,
                    onClick = { onGroupClick(group.name) }
                )
            }
        }
    }
}

@Composable
fun GroupItem(group: Group, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = InputGreen)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = PetSafeGreen
            )
        }
    }
}

@Composable
fun GroupItem(group: Group, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = InputGreen)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = group.description,
                style = MaterialTheme.typography.bodyMedium,
                color = PetSafeGreen
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroupScreen() {
    ScreensTheme {
        GroupScreenWithNavigation(
            navController = rememberNavController(),
            onBackClick = {}
        )
    }
}