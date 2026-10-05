package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TelegramGhostCyan
import com.example.ui.theme.TelegramSurfaceVariantDark
import com.example.ui.theme.TelegramTextSecondaryDark

@Composable
fun TelegramDrawer(
    onNavigate: (AppScreen) -> Unit,
    onOpenSavedMessages: () -> Unit,
    onOpenPackets: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = Color.White,
        modifier = Modifier.width(310.dp)
    ) {
        // User Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TelegramBlue)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E3A8A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }

                    IconButton(
                        onClick = { /* Toggle Night Mode */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Theme",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Alex Rivers",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "+1 (555) 019-2834 • @alex_dev",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Switch Account",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom AyuGram Mod Highlight Tile
        Surface(
            color = TelegramGhostCyan.copy(alpha = 0.12f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable { onNavigate(AppScreen.AYU_SETTINGS) }
                .testTag("drawer_ayu_settings")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "AyuGram Settings",
                    tint = TelegramGhostCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AyuGram Mod Settings",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TelegramGhostCyan
                        )
                    )
                    Text(
                        text = "Ghost Mode • Spy • Anti-Delete",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TelegramTextSecondaryDark,
                            fontSize = 11.sp
                        )
                    )
                }
                Surface(
                    color = TelegramGhostCyan,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "MOD",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }

        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            modifier = Modifier.padding(vertical = 6.dp)
        )

        // Navigation Items
        DrawerItem(
            icon = Icons.Outlined.Group,
            title = "New Group",
            onClick = { onClose() }
        )
        DrawerItem(
            icon = Icons.Outlined.Campaign,
            title = "New Channel",
            onClick = { onClose() }
        )
        DrawerItem(
            icon = Icons.Outlined.Person,
            title = "Contacts",
            onClick = { onClose() }
        )
        DrawerItem(
            icon = Icons.Outlined.Call,
            title = "Calls",
            onClick = { onClose() }
        )
        DrawerItem(
            icon = Icons.Outlined.BookmarkBorder,
            title = "Saved Messages",
            onClick = { onOpenSavedMessages() }
        )
        DrawerItem(
            icon = Icons.Outlined.Settings,
            title = "Settings",
            onClick = { onNavigate(AppScreen.AYU_SETTINGS) }
        )

        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            modifier = Modifier.padding(vertical = 6.dp)
        )

        // Developer Mod Items
        DrawerItem(
            icon = Icons.Outlined.Code,
            title = "DrKLO & TDLib Code Patches",
            badge = "SRC",
            onClick = { onNavigate(AppScreen.PATCHES_VIEW) }
        )
        DrawerItem(
            icon = Icons.Outlined.AccountTree,
            title = "Architecture & MTProto Pipeline",
            onClick = { onNavigate(AppScreen.ARCHITECTURE_VIEW) }
        )
        DrawerItem(
            icon = Icons.Outlined.Terminal,
            title = "MTProto Live RPC Inspector",
            badge = "LIVE",
            onClick = { onOpenPackets() }
        )
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = TelegramTextSecondaryDark,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Surface(
                color = TelegramSurfaceVariantDark,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = badge,
                    color = TelegramGhostCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
