package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppScreen
import com.example.data.model.ChatDialog
import com.example.data.model.ChatMessage
import com.example.data.model.CodePatch
import com.example.data.model.PacketLog
import com.example.data.model.PrivacySettings
import com.example.data.repository.TeleModRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class TeleModViewModel : ViewModel() {

    private val repository = TeleModRepository()

    val settings: StateFlow<PrivacySettings> = repository.settings
    val dialogs: StateFlow<List<ChatDialog>> = repository.dialogs
    val packets: StateFlow<List<PacketLog>> = repository.packets
    val activeChatId: StateFlow<Long> = repository.activeChatId
    val patches: List<CodePatch> = repository.getPatches()

    private val _currentScreen = MutableStateFlow(AppScreen.CHAT_LIST)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedFolder = MutableStateFlow("All")
    val selectedFolder: StateFlow<String> = _selectedFolder.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    private val _showPacketSheet = MutableStateFlow(false)
    val showPacketSheet: StateFlow<Boolean> = _showPacketSheet.asStateFlow()

    private val _selectedPatch = MutableStateFlow<CodePatch>(patches.first())
    val selectedPatch: StateFlow<CodePatch> = _selectedPatch.asStateFlow()

    val filteredDialogs: StateFlow<List<ChatDialog>> = combine(
        dialogs,
        selectedFolder,
        searchQuery
    ) { list, folder, query ->
        list.filter { dialog ->
            val matchesFolder = (folder == "All") || (dialog.folderCategory == folder)
            val matchesQuery = query.isBlank() ||
                    dialog.title.contains(query, ignoreCase = true) ||
                    dialog.messages.any { it.text.contains(query, ignoreCase = true) }
            matchesFolder && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeChat: StateFlow<ChatDialog?> = combine(
        dialogs,
        activeChatId
    ) { list, id ->
        list.firstOrNull { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        _isDrawerOpen.value = false
    }

    fun openChat(dialogId: Long) {
        repository.setActiveChat(dialogId)
        // If entering chat, trigger read history simulation
        repository.simulateReadMessages(dialogId)
        _currentScreen.value = AppScreen.CHAT_VIEW
    }

    fun setFolder(folder: String) {
        _selectedFolder.value = folder
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setSearching(searching: Boolean) {
        _isSearching.value = searching
        if (!searching) {
            _searchQuery.value = ""
        }
    }

    fun openDrawer() {
        _isDrawerOpen.value = true
    }

    fun closeDrawer() {
        _isDrawerOpen.value = false
    }

    fun togglePacketSheet() {
        _showPacketSheet.value = !_showPacketSheet.value
    }

    fun selectPatch(patch: CodePatch) {
        _selectedPatch.value = patch
    }

    // Privacy Toggles
    fun toggleGhostMode() = repository.toggleGhostMode()
    fun toggleHideTyping() = repository.toggleHideTyping()
    fun toggleAntiDelete() = repository.toggleAntiDelete()
    fun toggleStealthVoice() = repository.toggleStealthVoice()
    fun toggleGhostStories() = repository.toggleGhostStories()
    fun toggleShowDeletedBadge() = repository.toggleShowDeletedBadge()
    fun toggleKeepRevokedMedia() = repository.toggleKeepRevokedMedia()

    // Simulation Triggers
    fun sendOutgoingMessage(text: String) = repository.sendOutgoingMessage(text)
    fun simulateIncomingMessage(text: String) = repository.simulateIncomingMessage(text)
    fun simulateSenderTyping() = repository.simulateSenderTyping()
    fun simulateDeleteLastMessage() = repository.simulateDeleteLastMessage()
    fun simulateReadMessages() = repository.simulateReadMessages()
    fun simulateUserTyping() = repository.simulateUserTyping()
    fun clearPackets() = repository.clearPackets()
}
