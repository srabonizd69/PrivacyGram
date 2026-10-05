package com.example.data.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val senderName: String,
    val isOut: Boolean,
    val timestamp: String,
    val isRead: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedAt: String? = null,
    val isVoice: Boolean = false,
    val voiceDurationSec: Int = 0,
    val rawTlMethod: String? = null
)

enum class PacketStatus {
    BLOCKED,
    PASSED,
    CACHED
}

data class PacketLog(
    val id: Long,
    val timestamp: String,
    val method: String,
    val category: String,
    val status: PacketStatus,
    val payloadSnippet: String,
    val explanation: String
)

data class PrivacySettings(
    val ghostMode: Boolean = true,
    val hideTyping: Boolean = true,
    val antiDelete: Boolean = true,
    val stealthVoiceListen: Boolean = true,
    val ghostStories: Boolean = true,
    val keepRevokedMedia: Boolean = true,
    val showDeletedBadge: Boolean = true
)

data class ChatDialog(
    val id: Long,
    val title: String,
    val subtitle: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isChannel: Boolean = false,
    val isVerified: Boolean = false,
    val isOnline: Boolean = false,
    val avatarColorHex: Long = 0xFF2AABEE,
    val avatarInitials: String = "",
    val isTyping: Boolean = false,
    val folderCategory: String = "All",
    val messages: List<ChatMessage> = emptyList()
)

data class CodePatch(
    val id: String,
    val title: String,
    val repoPath: String,
    val className: String,
    val targetMethods: String,
    val purpose: String,
    val language: String,
    val originalSnippet: String,
    val patchedSnippet: String,
    val detailedWalkthrough: String
)

enum class AppScreen {
    CHAT_LIST,
    CHAT_VIEW,
    AYU_SETTINGS,
    PATCHES_VIEW,
    ARCHITECTURE_VIEW
}
