package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CodePatch
import com.example.ui.TeleModViewModel
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TelegramGhostCyan
import com.example.ui.theme.TelegramTextSecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceCodeScreen(
    viewModel: TeleModViewModel,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val patches = viewModel.patches
    val selectedPatch by viewModel.selectedPatch.collectAsState()
    val context = LocalContext.current

    var selectedViewTab by remember { mutableStateOf(0) } // 0: Patched Code, 1: Original Code, 2: Walkthrough

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("patches_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "DrKLO & TDLib Code Patches",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Official Class & File Path Inspector",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TelegramGhostCyan,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(12.dp)
        ) {
            // Horizontal Category Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(patches) { patch ->
                    val isSelected = patch.id == selectedPatch.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectPatch(patch)
                            selectedViewTab = 0
                        },
                        label = {
                            Text(
                                text = patch.title.substringBefore(":"),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TelegramBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("patch_chip_${patch.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Patch Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = selectedPatch.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "File Path",
                            tint = TelegramGhostCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = selectedPatch.repoPath,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TelegramGhostCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DataObject,
                            contentDescription = "Target Method",
                            tint = TelegramTextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Methods: ${selectedPatch.targetMethods}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TelegramTextSecondaryDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedPatch.purpose,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Code View Switcher (Patched vs Original vs Walkthrough)
            TabRow(
                selectedTabIndex = selectedViewTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedViewTab == 0,
                    onClick = { selectedViewTab = 0 },
                    text = { Text("Patched Code", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedViewTab == 1,
                    onClick = { selectedViewTab = 1 },
                    text = { Text("Original Official", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
                Tab(
                    selected = selectedViewTab == 2,
                    onClick = { selectedViewTab = 2 },
                    text = { Text("Step-by-Step", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Code / Content Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            ) {
                when (selectedViewTab) {
                    0 -> {
                        CodeViewerPanel(
                            code = selectedPatch.patchedSnippet,
                            language = selectedPatch.language,
                            onCopy = {
                                copyToClipboard(context, selectedPatch.patchedSnippet)
                            }
                        )
                    }
                    1 -> {
                        CodeViewerPanel(
                            code = selectedPatch.originalSnippet,
                            language = selectedPatch.language,
                            onCopy = {
                                copyToClipboard(context, selectedPatch.originalSnippet)
                            }
                        )
                    }
                    2 -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp)
                        ) {
                            item {
                                Text(
                                    text = "DETAILED IMPLEMENTATION WALKTHROUGH",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TelegramGhostCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = selectedPatch.detailedWalkthrough,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFFE2E8F0),
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeViewerPanel(code: String, language: String, onCopy: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val horizontalScrollState = rememberScrollState()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = language.uppercase(),
                            color = TelegramGhostCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = onCopy,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("copy_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 11.sp)
                    }
                }
            }

            item {
                Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                    Text(
                        text = code,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFF93C5FD),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Telegram Patch Snippet", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Snippet copied to clipboard!", Toast.LENGTH_SHORT).show()
}
