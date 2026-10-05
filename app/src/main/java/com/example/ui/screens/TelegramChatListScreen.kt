package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.data.model.ChatDialog
import com.example.ui.TeleModViewModel
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TelegramGhostCyan
import com.example.ui.theme.TelegramTextSecondaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramChatListScreen(
    viewModel: TeleModViewModel,
    onOpenDrawer: () -> Unit
) {
    val filteredDialogs by viewModel.filteredDialogs.collectAsState()
    val selectedFolder by viewModel.selectedFolder.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val packets by viewModel.packets.collectAsState()

    val folders = listOf("All", "Personal", "Work", "Channels")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search chats or messages...", fontSize = 15.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("search_text_field")
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Telegram",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            if (settings.ghostMode) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = TelegramGhostCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.AYU_SETTINGS) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VisibilityOff,
                                            contentDescription = "Ghost Mode Active",
                                            tint = TelegramGhostCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "GHOST",
                                            fontSize = 9.sp,
                                            color = TelegramGhostCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (isSearching) {
                        IconButton(onClick = { viewModel.setSearching(false) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    } else {
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier.testTag("menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Navigation Menu", tint = Color.White)
                        }
                    }
                },
                actions = {
                    if (!isSearching) {
                        IconButton(
                            onClick = { viewModel.setSearching(true) },
                            modifier = Modifier.testTag("search_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }

                        // AyuGram Quick Shield Button
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.AYU_SETTINGS) },
                            modifier = Modifier.testTag("action_mod_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "AyuGram Settings",
                                tint = if (settings.ghostMode) TelegramGhostCyan else Color.White
                            )
                        }

                        // MTProto Packet Monitor Icon
                        IconButton(
                            onClick = { viewModel.togglePacketSheet() },
                            modifier = Modifier.testTag("action_open_packets")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (packets.isNotEmpty()) {
                                        Badge(containerColor = TelegramBlue) {
                                            Text("${packets.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Terminal, contentDescription = "Packets", tint = Color.White)
                            }
                        }
                    } else if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Fast action: trigger simulation message from contact
                    viewModel.simulateIncomingMessage("Hey Alex, sending another test message!")
                },
                containerColor = TelegramBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_new_chat")
            ) {
                Icon(Icons.Default.Edit, contentDescription = "New Message")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Folder Tabs (All, Personal, Work, Channels)
            ScrollableTabRow(
                selectedTabIndex = folders.indexOf(selectedFolder).coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.White,
                edgePadding = 12.dp,
                divider = {},
                indicator = { tabPositions ->
                    val index = folders.indexOf(selectedFolder).coerceAtLeast(0)
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = TelegramBlue,
                        height = 3.dp
                    )
                }
            ) {
                folders.forEach { folderName ->
                    val isSelected = selectedFolder == folderName
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.setFolder(folderName) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = folderName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) TelegramBlue else TelegramTextSecondaryDark
                                )
                                if (folderName == "Work") {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = TelegramBlue,
                                        shape = CircleShape,
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.testTag("tab_$folderName")
                    )
                }
            }

            // Chat Dialogs List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("dialogs_list")
            ) {
                items(filteredDialogs, key = { it.id }) { dialog ->
                    DialogItemRow(
                        dialog = dialog,
                        onClick = { viewModel.openChat(dialog.id) }
                    )
                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(start = 72.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DialogItemRow(
    dialog: ChatDialog,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("chat_item_${dialog.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with optional online dot
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(dialog.avatarColorHex)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dialog.avatarInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            if (dialog.isOnline) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and Last Message
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = dialog.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (dialog.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = TelegramBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Time or Pinned Indicator
                Text(
                    text = dialog.lastMessageTime,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TelegramTextSecondaryDark,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle Row: Last message or typing indicator + Unread badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (dialog.isTyping) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "typing...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TelegramBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                } else {
                    val lastMsg = dialog.messages.lastOrNull()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (lastMsg?.isOut == true) {
                            Icon(
                                imageVector = if (lastMsg.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                                contentDescription = null,
                                tint = if (lastMsg.isRead) TelegramGhostCyan else TelegramTextSecondaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Text(
                            text = lastMsg?.text ?: dialog.subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TelegramTextSecondaryDark
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right badges: Pinned pin or unread count
                if (dialog.unreadCount > 0) {
                    Surface(
                        color = TelegramBlue,
                        shape = CircleShape,
                        modifier = Modifier.height(20.dp).widthIn(min = 20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            Text(
                                text = "${dialog.unreadCount}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (dialog.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = TelegramTextSecondaryDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
