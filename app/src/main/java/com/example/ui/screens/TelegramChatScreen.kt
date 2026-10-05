package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.data.model.ChatMessage
import com.example.ui.TeleModViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramChatScreen(
    viewModel: TeleModViewModel,
    onBack: () -> Unit
) {
    // Handle Android system back gesture/button
    BackHandler {
        onBack()
    }

    val activeChat by viewModel.activeChat.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val packets by viewModel.packets.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showQuickSimBar by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val messages = activeChat?.messages ?: emptyList()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    activeChat?.let { chat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(chat.avatarColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chat.avatarInitials,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = chat.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 16.sp
                                        )
                                    )
                                    if (chat.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = TelegramBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (chat.isTyping) {
                                        "typing..."
                                    } else if (settings.ghostMode) {
                                        "Ghost: receipts blocked • online"
                                    } else {
                                        chat.subtitle
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (chat.isTyping) TelegramBlue else if (settings.ghostMode) TelegramGhostCyan else TelegramTextSecondaryDark,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Quick Ghost Mode Toggle in Top Bar
                    IconButton(
                        onClick = { viewModel.toggleGhostMode() },
                        modifier = Modifier.testTag("quick_ghost_toggle")
                    ) {
                        Icon(
                            imageVector = if (settings.ghostMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Ghost Mode Toggle",
                            tint = if (settings.ghostMode) TelegramGhostCyan else Color.White.copy(alpha = 0.6f)
                        )
                    }

                    // 3-Dots Action Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("chat_options_menu")
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Simulate: Contact Sends Message") },
                                onClick = {
                                    showMenu = false
                                    viewModel.simulateIncomingMessage("Hey Alex, checking if you're online!")
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = TelegramBlue)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Simulate: Contact Revokes Message") },
                                onClick = {
                                    showMenu = false
                                    viewModel.simulateDeleteLastMessage()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = TelegramDeletedRed)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Simulate: Contact Starts Typing") },
                                onClick = {
                                    showMenu = false
                                    viewModel.simulateSenderTyping()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = TelegramBlue)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Mark Messages as Read") },
                                onClick = {
                                    showMenu = false
                                    viewModel.simulateReadMessages()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DoneAll, contentDescription = null, tint = TelegramGhostCyan)
                                }
                            )

                            Divider()

                            DropdownMenuItem(
                                text = { Text("Inspect MTProto RPC Logs (${packets.size})") },
                                onClick = {
                                    showMenu = false
                                    viewModel.togglePacketSheet()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Terminal, contentDescription = null, tint = TelegramGhostCyan)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("AyuGram Privacy Settings") },
                                onClick = {
                                    showMenu = false
                                    viewModel.navigateTo(AppScreen.AYU_SETTINGS)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = TelegramGhostCyan)
                                }
                            )
                        }
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
                .background(TelegramBgDark)
        ) {
            // AyuGram Stealth Status Pill Banner
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TelegramGhostCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AyuGram Stealth Deck",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TelegramGhostCyan,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.togglePacketSheet() },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text(
                                    text = "${packets.size} MTProto RPCs",
                                    fontSize = 10.sp,
                                    color = TelegramBlue
                                )
                            }

                            TextButton(
                                onClick = { showQuickSimBar = !showQuickSimBar },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text(
                                    text = if (showQuickSimBar) "Hide Tools" else "Sim Tools",
                                    fontSize = 10.sp,
                                    color = TelegramTextSecondaryDark
                                )
                            }
                        }
                    }

                    // Quick Toggle Chips inside Chat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InChatStatusChip(
                            title = "Ghost",
                            isActive = settings.ghostMode,
                            activeColor = TelegramGhostCyan,
                            onClick = { viewModel.toggleGhostMode() },
                            modifier = Modifier.weight(1f)
                        )
                        InChatStatusChip(
                            title = "Spy Mode",
                            isActive = settings.hideTyping,
                            activeColor = TelegramBlue,
                            onClick = { viewModel.toggleHideTyping() },
                            modifier = Modifier.weight(1f)
                        )
                        InChatStatusChip(
                            title = "Anti-Delete",
                            isActive = settings.antiDelete,
                            activeColor = TelegramDeletedRed,
                            onClick = { viewModel.toggleAntiDelete() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Optional Quick Simulation Action Bar
                    AnimatedVisibility(visible = showQuickSimBar) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val replies = listOf(
                                        "Did you see my deleted photo?",
                                        "Testing MTProto Ghost Mode right now.",
                                        "Deleting this message in 2 seconds...",
                                        "AyuGram suppresses setTyping packets!"
                                    )
                                    viewModel.simulateIncomingMessage(replies.random())
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.weight(1f).height(28.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Inbound Msg", fontSize = 10.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.simulateDeleteLastMessage() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TelegramDeletedRed),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.weight(1f).height(28.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Revoke Msg", fontSize = 10.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.simulateReadMessages() },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier.weight(1f).height(28.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Mark Read", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Chat Messages Canvas (Telegram Background & Bubbles)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .testTag("chat_messages_list"),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    TelegramMessageBubble(message = msg)
                }
            }

            // Real Telegram Bottom Message Input Dock
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Emoji picker */ },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SentimentSatisfied,
                            contentDescription = "Emoji",
                            tint = TelegramTextSecondaryDark
                        )
                    }

                    TextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                            if (it.isNotEmpty()) {
                                viewModel.simulateUserTyping()
                            }
                        },
                        placeholder = {
                            Text(
                                text = "Message",
                                color = TelegramTextSecondaryDark,
                                fontSize = 15.sp
                            )
                        },
                        singleLine = false,
                        maxLines = 4,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("telegram_message_input")
                    )

                    IconButton(
                        onClick = { /* Attachment clip */ },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach",
                            tint = TelegramTextSecondaryDark
                        )
                    }

                    // Send or Mic button
                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                viewModel.sendOutgoingMessage(inputText)
                                inputText = ""
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(TelegramBlue)
                                .testTag("telegram_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                // Simulate sending short voice note
                                viewModel.sendOutgoingMessage("Voice note (0:04)")
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(TelegramSurfaceVariantDark)
                                .testTag("telegram_mic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Note",
                                tint = TelegramBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // In-line typing status note
                if (inputText.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (settings.hideTyping) Icons.Default.Shield else Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (settings.hideTyping) TelegramGhostCyan else TelegramBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (settings.hideTyping) {
                                "Spy Mode active: TL_messages_setTyping suppressed from peer"
                            } else {
                                "Sending typing indicator to peer..."
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = if (settings.hideTyping) TelegramGhostCyan else TelegramBlue
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InChatStatusChip(
    title: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isActive) activeColor.copy(alpha = 0.15f) else Color(0xFF1E293B),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isActive) activeColor.copy(alpha = 0.6f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isActive) activeColor else Color.Gray)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$title: ${if (isActive) "ON" else "OFF"}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) activeColor else Color.LightGray
            )
        }
    }
}

@Composable
fun TelegramMessageBubble(message: ChatMessage) {
    val isOut = message.isOut

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOut) Arrangement.End else Arrangement.Start
    ) {
        val bubbleColor = when {
            message.isDeleted -> TelegramDeletedBg
            isOut -> TelegramBubbleOutDark
            else -> TelegramBubbleInDark
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOut) 16.dp else 4.dp,
                bottomEnd = if (isOut) 4.dp else 16.dp
            ),
            modifier = Modifier
                .widthIn(max = 290.dp)
                .border(
                    width = if (message.isDeleted) 1.dp else 0.dp,
                    color = if (message.isDeleted) TelegramDeletedRed else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                )
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                // Sender name if inbound in group or channel
                if (!isOut && message.isDeleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Deleted by sender",
                            tint = TelegramDeletedRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "DELETED BY SENDER (${message.deletedAt ?: "Revoked"})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TelegramDeletedRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                // Message Text
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Time and Checkmark
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = TelegramTextSecondaryDark
                        )
                    )

                    if (isOut) {
                        Spacer(modifier = Modifier.width(4.dp))
                        if (message.isRead) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Read",
                                tint = TelegramGhostCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Sent / Unread",
                                tint = TelegramTextSecondaryDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
