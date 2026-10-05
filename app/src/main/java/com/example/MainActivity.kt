package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.model.AppScreen
import com.example.ui.TeleModViewModel
import com.example.ui.components.PacketInspectorSheet
import com.example.ui.components.TelegramDrawer
import com.example.ui.screens.ArchitectureScreen
import com.example.ui.screens.AyuSettingsScreen
import com.example.ui.screens.SourceCodeScreen
import com.example.ui.screens.TelegramChatListScreen
import com.example.ui.screens.TelegramChatScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: TeleModViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val isDrawerOpen by viewModel.isDrawerOpen.collectAsState()
                val showPacketSheet by viewModel.showPacketSheet.collectAsState()
                val packets by viewModel.packets.collectAsState()
                val isSearching by viewModel.isSearching.collectAsState()

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // Keep drawer state in sync
                LaunchedEffect(isDrawerOpen) {
                    if (isDrawerOpen) drawerState.open() else drawerState.close()
                }

                LaunchedEffect(drawerState.isOpen) {
                    if (!drawerState.isOpen && isDrawerOpen) {
                        viewModel.closeDrawer()
                    }
                }

                // Global Back Navigation
                BackHandler(enabled = drawerState.isOpen || isSearching || currentScreen != AppScreen.CHAT_LIST) {
                    if (drawerState.isOpen) {
                        scope.launch { drawerState.close() }
                        viewModel.closeDrawer()
                    } else if (isSearching) {
                        viewModel.setSearching(false)
                    } else if (currentScreen != AppScreen.CHAT_LIST) {
                        viewModel.navigateTo(AppScreen.CHAT_LIST)
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentScreen == AppScreen.CHAT_LIST,
                    drawerContent = {
                        TelegramDrawer(
                            onNavigate = { screen ->
                                scope.launch { drawerState.close() }
                                viewModel.navigateTo(screen)
                            },
                            onOpenSavedMessages = {
                                scope.launch { drawerState.close() }
                                viewModel.openChat(4L) // 4L is Saved Messages
                            },
                            onOpenPackets = {
                                scope.launch { drawerState.close() }
                                viewModel.togglePacketSheet()
                            },
                            onClose = {
                                scope.launch { drawerState.close() }
                                viewModel.closeDrawer()
                            }
                        )
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentScreen) {
                            AppScreen.CHAT_LIST -> {
                                TelegramChatListScreen(
                                    viewModel = viewModel,
                                    onOpenDrawer = {
                                        scope.launch { drawerState.open() }
                                        viewModel.openDrawer()
                                    }
                                )
                            }
                            AppScreen.CHAT_VIEW -> {
                                TelegramChatScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateTo(AppScreen.CHAT_LIST) }
                                )
                            }
                            AppScreen.AYU_SETTINGS -> {
                                AyuSettingsScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateTo(AppScreen.CHAT_LIST) }
                                )
                            }
                            AppScreen.PATCHES_VIEW -> {
                                SourceCodeScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateTo(AppScreen.CHAT_LIST) }
                                )
                            }
                            AppScreen.ARCHITECTURE_VIEW -> {
                                ArchitectureScreen(
                                    onBack = { viewModel.navigateTo(AppScreen.CHAT_LIST) }
                                )
                            }
                        }

                        // Live MTProto RPC Packet Inspector Bottom Sheet
                        if (showPacketSheet) {
                            PacketInspectorSheet(
                                packets = packets,
                                onClear = { viewModel.clearPackets() },
                                onDismiss = { viewModel.togglePacketSheet() }
                            )
                        }
                    }
                }
            }
        }
    }
}
