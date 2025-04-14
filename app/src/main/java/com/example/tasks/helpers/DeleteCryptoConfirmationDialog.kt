package com.example.tasks.helpers

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.tasks.R
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun DeleteTodoConfirmationDialog(
    todoTitle: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var isSwitchChecked by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(text = "TODO: " + todoTitle)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Are you sure you want to delete the \"" + todoTitle + "\" todo?")

                Spacer(modifier = Modifier.height(16.dp))

                Switch(
                    checked = isSwitchChecked,
                    onCheckedChange = { isSwitchChecked = it },
                )
                Text(
                    text = "Yes, I am sure I want to delete this TODO",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete()
                },
                enabled = isSwitchChecked
            ) {
                Text(text = "Delete")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(text = "Cancel")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}