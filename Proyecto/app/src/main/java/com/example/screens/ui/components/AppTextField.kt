package com.example.screens.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.screens.ui.theme.InputGreen
import com.example.screens.ui.theme.TextBlack

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,


        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = InputGreen,
            focusedContainerColor = InputGreen,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = TextBlack,
            focusedTextColor = TextBlack
        )
    )
}