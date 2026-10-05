package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.ui.TeleModViewModel
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TelegramGhostCyan
import com.example.ui.theme.TelegramTextSecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyuSettingsScreen(
    viewModel: TeleModViewModel,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val settings by viewModel.settings.collectAsState()
    val packets by viewModel.packets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
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
                            text = "AyuGram Settings",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Custom Telegram FOSS Privacy Engine",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header 1: Ghost Mode
            item {
                SettingsSectionHeader(title = "GHOST MODE (READ RECEIPTS)")
            }

            item {
                SettingsSwitchRow(
                    icon = Icons.Outlined.VisibilityOff,
                    title = "Send read receipts",
                    subtitle = "When disabled, blocks TL_messages_readHistory / viewMessages so the sender never gets double checkmarks.",
                    checked = !settings.ghostMode,
                    onCheckedChange = { viewModel.toggleGhostMode() },
                    statusLabel = if (settings.ghostMode) "Ghost Active (Blocked)" else "Standard (Sent)",
                    testTag = "setting_ghost_mode"
                )
            }

            item {
                SettingsSwitchRow(
                    icon = Icons.Outlined.AutoStories,
                    title = "Send stories read receipts",
                    subtitle = "Blocks TL_stories_readStories. View peer stories completely invisibly.",
                    checked = !settings.ghostStories,
                    onCheckedChange = { viewModel.toggleGhostStories() },
                    statusLabel = if (settings.ghostStories) "Stealth Active" else "Sent",
                    testTag = "setting_ghost_stories"
                )
            }

            item {
                SettingsSwitchRow(
                    icon = Icons.Outlined.VolumeOff,
                    title = "Stealth voice & video play",
                    subtitle = "Blocks TL_messages_readMessageContents. Voice notes will not be marked as listened.",
                    checked = !settings.stealthVoiceListen,
                    onCheckedChange = { viewModel.toggleStealthVoice() },
                    statusLabel = if (settings.stealthVoiceListen) "Stealth Active" else "Sent",
                    testTag = "setting_stealth_voice"
                )
            }

            // Header 2: Spy Mode / Hide Typing
            item {
                SettingsSectionHeader(title = "SPY MODE (CHAT ACTIONS)")
            }

            item {
                SettingsSwitchRow(
                    icon = Icons.Outlined.EditOff,
                    title = "Send typing status",
                    subtitle = "When disabled, blocks TL_messages_setTyping. The contact never sees 'typing...' when you write.",
                    checked = !settings.hideTyping,
                    onCheckedChange = { viewModel.toggleHideTyping() },
                    statusLabel = if (settings.hideTyping) "Hidden (No RPC)" else "Broadcasting",
                    testTag = "setting_hide_typing"
                )
            }

            // Header 3: Anti-Delete Message
            item {
                SettingsSectionHeader(title = "ANTI-DELETE ENGINE")
            }

            item {
                SettingsSwitchRow(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Save deleted messages",
                    subtitle = "Intercepts TL_updateDeleteMessages and mutates SQLite DELETE to UPDATE deleted=1 so revoked messages stay visible.",
                    checked = settings.antiDelete,
                    onCheckedChange = { viewModel.toggleAntiDelete() },
                    statusLabel = if (settings.antiDelete) "Preserving Messages" else "Standard Purge",
                    testTag = "setting_anti_delete"
                )
            }

            item {
                SettingsSwitchRow(
                    icon = Icons.Outlined.Label,
                    title = "Show [Deleted] label",
                    subtitle = "Renders a red trash icon and timestamp when a message was revoked by the sender.",
                    checked = settings.showDeletedBadge,
                    onCheckedChange = { viewModel.toggleShowDeletedBadge() },
                    statusLabel = if (settings.showDeletedBadge) "Visible" else "Hidden",
                    testTag = "setting_deleted_badge"
                )
            }

            item {
                SettingsSwitchRow(
                    icon = Icons.Outlined.Attachment,
                    title = "Retain revoked media files",
                    subtitle = "Keeps voice notes, images, and videos in local storage when sender deletes them for everyone.",
                    checked = settings.keepRevokedMedia,
                    onCheckedChange = { viewModel.toggleKeepRevokedMedia() },
                    statusLabel = if (settings.keepRevokedMedia) "Kept on Disk" else "Purged",
                    testTag = "setting_keep_media"
                )
            }

            // Header 4: Developer Tools & Patches
            item {
                SettingsSectionHeader(title = "TELEGRAM API CREDENTIALS & BUILD")
            }

            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = TelegramGhostCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "my.telegram.org API Configuration",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("API ID:", fontSize = 13.sp, color = TelegramTextSecondaryDark)
                            Text(
                                text = "${com.example.data.BuildVars.APP_ID}",
                                fontSize = 13.sp,
                                color = TelegramGhostCyan,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("API HASH:", fontSize = 13.sp, color = TelegramTextSecondaryDark)
                            Text(
                                text = com.example.data.BuildVars.APP_HASH,
                                fontSize = 11.sp,
                                color = TelegramGhostCyan,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("APK Location:", fontSize = 13.sp, color = TelegramTextSecondaryDark)
                            Text(
                                text = "/downloadapkfile/TeleMod.apk",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                SettingsSectionHeader(title = "DEVELOPER TOOLS & CODE HOOKS")
            }

            item {
                SettingsLinkRow(
                    icon = Icons.Outlined.Code,
                    title = "DrKLO & TDLib Code Patches",
                    subtitle = "Exact Java & Kotlin code snippets, class names, and file paths",
                    onClick = { viewModel.navigateTo(AppScreen.PATCHES_VIEW) },
                    testTag = "link_patches"
                )
            }

            item {
                SettingsLinkRow(
                    icon = Icons.Outlined.AccountTree,
                    title = "Architecture & MTProto Pipeline",
                    subtitle = "DrKLO native bindings vs TDLib C++ pipeline and SQLite schema guide",
                    onClick = { viewModel.navigateTo(AppScreen.ARCHITECTURE_VIEW) },
                    testTag = "link_architecture"
                )
            }

            item {
                SettingsLinkRow(
                    icon = Icons.Outlined.Terminal,
                    title = "MTProto Live RPC Inspector",
                    subtitle = "Live log of captured, blocked, and passed TL network packets (${packets.size} logs)",
                    onClick = { viewModel.togglePacketSheet() },
                    testTag = "link_packet_inspector"
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            color = TelegramBlue,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
        ),
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    statusLabel: String,
    testTag: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TelegramBlue,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TelegramTextSecondaryDark,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Status: $statusLabel",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (checked) TelegramBlue else TelegramGhostCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = TelegramBlue,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = Color(0xFF2B3A4A)
                )
            )
        }
    }
    Divider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 56.dp)
    )
}

@Composable
fun SettingsLinkRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TelegramGhostCyan,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TelegramTextSecondaryDark,
                        fontSize = 12.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TelegramTextSecondaryDark,
                modifier = Modifier.size(14.dp)
            )
        }
    }
    Divider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 56.dp)
    )
}
