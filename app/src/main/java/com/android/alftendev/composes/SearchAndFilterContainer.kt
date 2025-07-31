package com.android.alftendev.composes

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.android.alftendev.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchAndFilterContainer(
    context: Context,
    allNotifications: List<T>,
    getPackageName: (T) -> String,
    isDeleted: (T) -> Boolean,
    notificationText: (T) -> String,
    modifier: Modifier = Modifier,
    content: @Composable (filteredItems: List<T>) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var showFiltersPopup by remember { mutableStateOf(false) }
    var selectedApp by remember { mutableStateOf(context.getString(R.string.defaultSwitchValue)) }
    var showOnlyDeleted by remember { mutableStateOf(false) }

    val appNames = remember(allNotifications) {
        listOf(context.getString(R.string.defaultSwitchValue)) + allNotifications.map(getPackageName)
            .distinct().sorted()
    }

    val filteredItems = remember(searchText, selectedApp, showOnlyDeleted, allNotifications) {
        allNotifications.filter { item ->
            val textMatch = notificationText(item).contains(searchText, ignoreCase = true)
            val appMatch =
                selectedApp == context.getString(R.string.defaultSwitchValue) || getPackageName(item) == selectedApp
            val deletedMatch = !showOnlyDeleted || isDeleted(item)
            textMatch && appMatch && deletedMatch
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text(context.getString(R.string.search)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(4.dp))
            Button(onClick = { showFiltersPopup = true }) {
                Text(context.getString(R.string.filters_label))
            }
        }

        if (showFiltersPopup) {
            Popup(onDismissRequest = { showFiltersPopup = false }) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            context.getString(R.string.filters_label),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedApp,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(context.getString(R.string.app_label)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                appNames.forEach { appName ->
                                    DropdownMenuItem(
                                        text = { Text(appName) },
                                        onClick = {
                                            selectedApp = appName
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(context.getString(R.string.deleted_noti_widget_description))
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = showOnlyDeleted,
                                onCheckedChange = { showOnlyDeleted = it })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showFiltersPopup = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(context.getString(R.string.apply_label))
                        }
                    }
                }
            }
        }

        content(filteredItems)
    }
}