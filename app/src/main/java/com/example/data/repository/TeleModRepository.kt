package com.example.data.repository

import com.example.data.model.ChatDialog
import com.example.data.model.ChatMessage
import com.example.data.model.CodePatch
import com.example.data.model.PacketLog
import com.example.data.model.PacketStatus
import com.example.data.model.PrivacySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeleModRepository {

    private val _settings = MutableStateFlow(PrivacySettings())
    val settings: StateFlow<PrivacySettings> = _settings.asStateFlow()

    private val _dialogs = MutableStateFlow<List<ChatDialog>>(emptyList())
    val dialogs: StateFlow<List<ChatDialog>> = _dialogs.asStateFlow()

    private val _activeChatId = MutableStateFlow<Long>(1L)
    val activeChatId: StateFlow<Long> = _activeChatId.asStateFlow()

    private val _packets = MutableStateFlow<List<PacketLog>>(emptyList())
    val packets: StateFlow<List<PacketLog>> = _packets.asStateFlow()

    private var packetCounter = 1L
    private var messageCounter = 200L

    init {
        loadInitialDialogs()
    }

    private fun currentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    private fun currentChatTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    private fun loadInitialDialogs() {
        val dmitryMsgs = listOf(
            ChatMessage(
                id = 101L,
                text = "Hey! Have you integrated the new MTProto packet hook into MessagesController?",
                senderName = "Dmitry",
                isOut = false,
                timestamp = "10:14",
                isRead = true
            ),
            ChatMessage(
                id = 102L,
                text = "Yes, researching how AyuGram drops TL_messages_readHistory before ConnectionsManager JNI.",
                senderName = "You",
                isOut = true,
                timestamp = "10:15",
                isRead = true
            ),
            ChatMessage(
                id = 103L,
                text = "Awesome! Try reading my next messages while Ghost Mode is active — you won't trigger double checkmarks.",
                senderName = "Dmitry",
                isOut = false,
                timestamp = "10:16",
                isRead = false
            ),
            ChatMessage(
                id = 104L,
                text = "Also try revoking a message: our Anti-Delete SQLite hook will preserve it with a red [Deleted] badge!",
                senderName = "Dmitry",
                isOut = false,
                timestamp = "10:17",
                isRead = false
            )
        )

        val durovMsgs = listOf(
            ChatMessage(
                id = 201L,
                text = "Telegram for Android 11.2 has been rolled out with Star gifts, faster video streaming, and enhanced Mini App bots.",
                senderName = "Pavel Durov",
                isOut = false,
                timestamp = "Yesterday",
                isRead = true
            )
        )

        val ayugramMsgs = listOf(
            ChatMessage(
                id = 301L,
                text = "AyuGram v5.4.1 FOSS release: Fixed SQLite schema migration on Android 15. Added local caching for deleted voice messages.",
                senderName = "AyuGram Announcements",
                isOut = false,
                timestamp = "May 24",
                isRead = true
            )
        )

        val savedMsgs = listOf(
            ChatMessage(
                id = 401L,
                text = "DrKLO/Telegram build flags:\n- APP_ID: my.telegram.org\n- ndkVersion: 23.1.7779620\n- cmake: 3.22.1",
                senderName = "Saved Messages",
                isOut = true,
                timestamp = "10:02",
                isRead = true
            )
        )

        val aliceMsgs = listOf(
            ChatMessage(
                id = 501L,
                text = "Hey Alex! Check the updated Telegram sticker design mockups when you have a moment.",
                senderName = "Alice",
                isOut = false,
                timestamp = "09:45",
                isRead = true
            )
        )

        val tipsMsgs = listOf(
            ChatMessage(
                id = 601L,
                text = "Tip: You can organize your chats into folders like Personal, Work, and Channels.",
                senderName = "Telegram Tips",
                isOut = false,
                timestamp = "Sun",
                isRead = true
            )
        )

        _dialogs.value = listOf(
            ChatDialog(
                id = 1L,
                title = "Dmitry (Dev Lead)",
                subtitle = "online",
                lastMessageTime = "10:17",
                unreadCount = 2,
                isPinned = true,
                isOnline = true,
                avatarColorHex = 0xFF2AABEE,
                avatarInitials = "D",
                folderCategory = "Work",
                messages = dmitryMsgs
            ),
            ChatDialog(
                id = 2L,
                title = "Pavel Durov",
                subtitle = "official channel",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isPinned = true,
                isChannel = true,
                isVerified = true,
                avatarColorHex = 0xFF2481CC,
                avatarInitials = "P",
                folderCategory = "Channels",
                messages = durovMsgs
            ),
            ChatDialog(
                id = 3L,
                title = "AyuGram Announcements",
                subtitle = "34.8K subscribers",
                lastMessageTime = "May 24",
                unreadCount = 0,
                isChannel = true,
                avatarColorHex = 0xFF00C2FF,
                avatarInitials = "A",
                folderCategory = "Channels",
                messages = ayugramMsgs
            ),
            ChatDialog(
                id = 4L,
                title = "Saved Messages",
                subtitle = "Cloud storage",
                lastMessageTime = "10:02",
                unreadCount = 0,
                avatarColorHex = 0xFF5AC8FB,
                avatarInitials = "★",
                folderCategory = "Personal",
                messages = savedMsgs
            ),
            ChatDialog(
                id = 5L,
                title = "Alice (UI/UX)",
                subtitle = "last seen 15 min ago",
                lastMessageTime = "09:45",
                unreadCount = 0,
                isOnline = false,
                avatarColorHex = 0xFFE040FB,
                avatarInitials = "A",
                folderCategory = "Personal",
                messages = aliceMsgs
            ),
            ChatDialog(
                id = 6L,
                title = "Telegram Tips",
                subtitle = "Service notification",
                lastMessageTime = "Sun",
                unreadCount = 0,
                isVerified = true,
                avatarColorHex = 0xFF26A69A,
                avatarInitials = "T",
                folderCategory = "All",
                messages = tipsMsgs
            )
        )
    }

    fun setActiveChat(dialogId: Long) {
        _activeChatId.value = dialogId
    }

    fun getActiveChat(): ChatDialog? {
        return _dialogs.value.firstOrNull { it.id == _activeChatId.value }
    }

    fun updateSettings(newSettings: PrivacySettings) {
        _settings.value = newSettings
    }

    fun toggleGhostMode() {
        _settings.value = _settings.value.copy(ghostMode = !_settings.value.ghostMode)
    }

    fun toggleHideTyping() {
        _settings.value = _settings.value.copy(hideTyping = !_settings.value.hideTyping)
    }

    fun toggleAntiDelete() {
        _settings.value = _settings.value.copy(antiDelete = !_settings.value.antiDelete)
    }

    fun toggleStealthVoice() {
        _settings.value = _settings.value.copy(stealthVoiceListen = !_settings.value.stealthVoiceListen)
    }

    fun toggleGhostStories() {
        _settings.value = _settings.value.copy(ghostStories = !_settings.value.ghostStories)
    }

    fun toggleShowDeletedBadge() {
        _settings.value = _settings.value.copy(showDeletedBadge = !_settings.value.showDeletedBadge)
    }

    fun toggleKeepRevokedMedia() {
        _settings.value = _settings.value.copy(keepRevokedMedia = !_settings.value.keepRevokedMedia)
    }

    fun simulateUserTyping() {
        val time = currentTime()
        val isBlocked = _settings.value.hideTyping
        val status = if (isBlocked) PacketStatus.BLOCKED else PacketStatus.PASSED
        val explanation = if (isBlocked) {
            "BLOCKED by Spy Mode hook: TL_messages_setTyping suppressed. Contact sees no 'typing...' label."
        } else {
            "TRANSMITTED to MTProto DC: TL_messages_setTyping sent. Contact sees 'typing...' indicator."
        }

        val packet = PacketLog(
            id = packetCounter++,
            timestamp = time,
            method = "TLRPC.TL_messages_setTyping",
            category = "Spy Mode",
            status = status,
            payloadSnippet = """
                TL_messages_setTyping {
                  peer: TL_inputPeerUser { user_id: 849201 },
                  action: TL_sendMessageTypingAction {}
                }
            """.trimIndent(),
            explanation = explanation
        )
        _packets.value = listOf(packet) + _packets.value
    }

    fun simulateReadMessages(dialogId: Long? = null) {
        val targetId = dialogId ?: _activeChatId.value
        val time = currentTime()
        val isGhost = _settings.value.ghostMode

        if (isGhost) {
            val packet = PacketLog(
                id = packetCounter++,
                timestamp = time,
                method = "TLRPC.TL_messages_readHistory",
                category = "Ghost Mode",
                status = PacketStatus.BLOCKED,
                payloadSnippet = """
                    TL_messages_readHistory {
                      peer: TL_inputPeerUser { user_id: 849201 },
                      max_id: 104
                    }
                """.trimIndent(),
                explanation = "BLOCKED by Ghost Mode hook: Read receipt packet dropped. Message read locally but sender retains single tick (Unread)."
            )
            _packets.value = listOf(packet) + _packets.value

            // In Ghost Mode: unread counter clears locally in UI, but messages retain single checkmark for outgoing or stay unconfirmed on server
            _dialogs.value = _dialogs.value.map { dialog ->
                if (dialog.id == targetId) {
                    dialog.copy(unreadCount = 0)
                } else dialog
            }
        } else {
            val packet = PacketLog(
                id = packetCounter++,
                timestamp = time,
                method = "TLRPC.TL_messages_readHistory",
                category = "Ghost Mode",
                status = PacketStatus.PASSED,
                payloadSnippet = """
                    TL_messages_readHistory {
                      peer: TL_inputPeerUser { user_id: 849201 },
                      max_id: 104
                    }
                """.trimIndent(),
                explanation = "TRANSMITTED: Read receipt sent to server. Double tick displayed on sender's device."
            )
            _packets.value = listOf(packet) + _packets.value

            _dialogs.value = _dialogs.value.map { dialog ->
                if (dialog.id == targetId) {
                    dialog.copy(
                        unreadCount = 0,
                        messages = dialog.messages.map { it.copy(isRead = true) }
                    )
                } else dialog
            }
        }
    }

    fun simulateIncomingMessage(text: String, dialogId: Long? = null) {
        val targetId = dialogId ?: _activeChatId.value
        val time = currentChatTime()
        val targetChat = _dialogs.value.firstOrNull { it.id == targetId } ?: return

        val newMsg = ChatMessage(
            id = messageCounter++,
            text = text,
            senderName = targetChat.title.substringBefore(" "),
            isOut = false,
            timestamp = time,
            isRead = false
        )

        _dialogs.value = _dialogs.value.map { dialog ->
            if (dialog.id == targetId) {
                dialog.copy(
                    lastMessageTime = time,
                    unreadCount = dialog.unreadCount + 1,
                    isTyping = false,
                    messages = dialog.messages + newMsg
                )
            } else dialog
        }

        val packet = PacketLog(
            id = packetCounter++,
            timestamp = currentTime(),
            method = "TLRPC.TL_updateNewMessage",
            category = "Message Sync",
            status = PacketStatus.PASSED,
            payloadSnippet = """
                TL_updateNewMessage {
                  message: TL_message {
                    id: ${newMsg.id},
                    from_id: TL_peerUser { user_id: 849201 },
                    message: "$text"
                  }
                }
            """.trimIndent(),
            explanation = "INCOMING: Stored in local SQLite database cache. Unread inbox pointer incremented."
        )
        _packets.value = listOf(packet) + _packets.value
    }

    fun simulateSenderTyping(dialogId: Long? = null) {
        val targetId = dialogId ?: _activeChatId.value
        _dialogs.value = _dialogs.value.map { dialog ->
            if (dialog.id == targetId) {
                dialog.copy(isTyping = true)
            } else dialog
        }

        val packet = PacketLog(
            id = packetCounter++,
            timestamp = currentTime(),
            method = "TLRPC.TL_updateUserTyping",
            category = "Inbound Action",
            status = PacketStatus.PASSED,
            payloadSnippet = """
                TL_updateUserTyping {
                  user_id: 849201,
                  action: TL_sendMessageTypingAction {}
                }
            """.trimIndent(),
            explanation = "INBOUND: Contact started typing in this chat session."
        )
        _packets.value = listOf(packet) + _packets.value
    }

    fun simulateDeleteLastMessage(dialogId: Long? = null) {
        val targetId = dialogId ?: _activeChatId.value
        val targetChat = _dialogs.value.firstOrNull { it.id == targetId } ?: return
        val lastReceived = targetChat.messages.lastOrNull { !it.isOut && !it.isDeleted } ?: return
        val time = currentTime()
        val antiDeleteActive = _settings.value.antiDelete

        if (antiDeleteActive) {
            val packet = PacketLog(
                id = packetCounter++,
                timestamp = time,
                method = "TLRPC.TL_updateDeleteMessages",
                category = "Anti-Delete",
                status = PacketStatus.CACHED,
                payloadSnippet = """
                    TL_updateDeleteMessages {
                      messages: [${lastReceived.id}]
                    }
                """.trimIndent(),
                explanation = "INTERCEPTED by Anti-Delete: SQLite DELETE command mutated to UPDATE deleted=1. Message remains rendered with [DELETED] badge."
            )
            _packets.value = listOf(packet) + _packets.value

            _dialogs.value = _dialogs.value.map { dialog ->
                if (dialog.id == targetId) {
                    dialog.copy(
                        messages = dialog.messages.map { msg ->
                            if (msg.id == lastReceived.id) {
                                msg.copy(isDeleted = true, deletedAt = time)
                            } else msg
                        }
                    )
                } else dialog
            }
        } else {
            val packet = PacketLog(
                id = packetCounter++,
                timestamp = time,
                method = "TLRPC.TL_updateDeleteMessages",
                category = "Anti-Delete",
                status = PacketStatus.PASSED,
                payloadSnippet = """
                    TL_updateDeleteMessages {
                      messages: [${lastReceived.id}]
                    }
                """.trimIndent(),
                explanation = "PROCESSED: Anti-Delete is OFF. Message purged from local SQLite storage and removed from UI."
            )
            _packets.value = listOf(packet) + _packets.value

            _dialogs.value = _dialogs.value.map { dialog ->
                if (dialog.id == targetId) {
                    dialog.copy(
                        messages = dialog.messages.filter { it.id != lastReceived.id }
                    )
                } else dialog
            }
        }
    }

    fun sendOutgoingMessage(text: String, dialogId: Long? = null) {
        val targetId = dialogId ?: _activeChatId.value
        val time = currentChatTime()
        val newMsg = ChatMessage(
            id = messageCounter++,
            text = text,
            senderName = "You",
            isOut = true,
            timestamp = time,
            isRead = false
        )

        _dialogs.value = _dialogs.value.map { dialog ->
            if (dialog.id == targetId) {
                dialog.copy(
                    lastMessageTime = time,
                    messages = dialog.messages + newMsg
                )
            } else dialog
        }

        val packet = PacketLog(
            id = packetCounter++,
            timestamp = currentTime(),
            method = "TLRPC.TL_messages_sendMessage",
            category = "Outgoing",
            status = PacketStatus.PASSED,
            payloadSnippet = """
                TL_messages_sendMessage {
                  peer: TL_inputPeerUser { user_id: 849201 },
                  message: "$text",
                  random_id: ${System.currentTimeMillis()}
                }
            """.trimIndent(),
            explanation = "TRANSMITTED: Outgoing message dispatched via MTProto ConnectionsManager."
        )
        _packets.value = listOf(packet) + _packets.value
    }

    fun clearPackets() {
        _packets.value = emptyList()
    }

    fun getPatches(): List<CodePatch> {
        return listOf(
            CodePatch(
                id = "ghost_mode",
                title = "1. Ghost Mode: Intercepting Read Receipts",
                repoPath = "TMessagesProj/src/main/java/org/telegram/messenger/MessagesController.java",
                className = "org.telegram.messenger.MessagesController",
                targetMethods = "markDialogAsRead(), markMessageContentAsRead()",
                purpose = "Prevent sending read confirmations (TL_messages_readHistory / TL_channels_readHistory) to the MTProto server so the sender never receives double checkmarks.",
                language = "Java",
                originalSnippet = """
// Original MessagesController.java in DrKLO/Telegram:
public void markDialogAsRead(long did, int max_id, int max_date, int offset, 
                             boolean isChannel, int top_message_id, 
                             int read_type, boolean isFolder) {
    if (did == 0) return;
    TLRPC.TL_messages_readHistory req = new TLRPC.TL_messages_readHistory();
    req.peer = getMessagesController().getInputPeer(did);
    req.max_id = max_id;
    getConnectionsManager().sendRequest(req, (response, error) -> {
        // handle response
    });
}
                """.trimIndent(),
                patchedSnippet = """
// PATCHED MessagesController.java (AyuGram Style):
public void markDialogAsRead(long did, int max_id, int max_date, int offset, 
                             boolean isChannel, int top_message_id, 
                             int read_type, boolean isFolder) {
    if (did == 0) return;
    
    // [PRIVACY HOOK]: Check if Ghost Mode is active
    if (AyuConfig.isGhostModeEnabled()) {
        // Update local database ONLY so unread count updates in client UI
        // WITHOUT transmitting the read packet to Telegram servers!
        getMessagesStorage().updateDialogReadInboxMax(did, max_id);
        return; // Intercept & swallow network request
    }
    
    // Fallback: regular sending if user disabled Ghost Mode
    if (isChannel) {
        TLRPC.TL_channels_readHistory req = new TLRPC.TL_channels_readHistory();
        req.channel = getMessagesController().getInputChannel(did);
        req.max_id = max_id;
        getConnectionsManager().sendRequest(req, null);
    } else {
        TLRPC.TL_messages_readHistory req = new TLRPC.TL_messages_readHistory();
        req.peer = getMessagesController().getInputPeer(did);
        req.max_id = max_id;
        getConnectionsManager().sendRequest(req, null);
    }
}
                """.trimIndent(),
                detailedWalkthrough = """
1. Location: In DrKLO/Telegram repository, open `TMessagesProj/src/main/java/org/telegram/messenger/MessagesController.java`.
2. Target Methods:
   - `markDialogAsRead(...)`: Called when scrolling messages in `ChatActivity.java`.
   - `markMessageContentAsRead(...)`: Called when playing voice notes/video notes (`TLRPC.TL_messages_readMessageContents`).
   - `sendReadStories(...)`: Called when viewing user stories (`TLRPC.TL_stories_readStories`).
3. Local vs Remote State:
   Telegram maintains two separate tracking concepts:
   - Server inbox pointer: Synchronized via `TLRPC.TL_messages_readHistory`.
   - Local unread cache: Managed inside `MessagesStorage.java` (`dialogs` table `read_inbox_max_id`).
   To preserve seamless UX, update local `MessagesStorage` so unread counters don't stay perpetually stuck in your UI, but skip `ConnectionsManager.sendRequest()`.
4. Central Blocker in ConnectionsManager:
   Alternatively, add a global filter in `ConnectionsManager.java` (see Patch #4).
                """.trimIndent()
            ),
            CodePatch(
                id = "spy_mode",
                title = "2. Spy Mode: Disable sendChatAction (Hide Typing)",
                repoPath = "TMessagesProj/src/main/java/org/telegram/messenger/MessagesController.java",
                className = "org.telegram.messenger.MessagesController",
                targetMethods = "sendTyping(long did, int top_msg_id, int action, int classGuid)",
                purpose = "Block typing indicators, audio recording, and file uploading notifications from being broadcasted to peers.",
                language = "Java",
                originalSnippet = """
// Original MessagesController.java:
public void sendTyping(final long did, int top_msg_id, final int action, int classGuid) {
    if (did == 0) return;
    TLRPC.TL_messages_setTyping req = new TLRPC.TL_messages_setTyping();
    req.peer = getInputPeer(did);
    if (req.peer == null) return;
    req.action = getTypingAction(action);
    getConnectionsManager().sendRequest(req, null);
}
                """.trimIndent(),
                patchedSnippet = """
// PATCHED MessagesController.java (AyuGram Style):
public void sendTyping(final long did, int top_msg_id, final int action, int classGuid) {
    if (did == 0) return;
    
    // [PRIVACY HOOK]: Intercept and drop all typing notifications
    if (AyuConfig.isHideTypingEnabled()) {
        return; // Pure no-op! No RPC packet generated or queued
    }
    
    TLRPC.TL_messages_setTyping req = new TLRPC.TL_messages_setTyping();
    req.peer = getInputPeer(did);
    if (req.peer == null) return;
    req.action = getTypingAction(action);
    getConnectionsManager().sendRequest(req, null);
}
                """.trimIndent(),
                detailedWalkthrough = """
1. Location: In DrKLO/Telegram repository, open `TMessagesProj/src/main/java/org/telegram/messenger/MessagesController.java`.
2. Action Types Handled:
   `action` corresponds to:
   - 0: `TL_sendMessageTypingAction` (text typing)
   - 1: `TL_sendMessageRecordAudioAction` (recording voice message)
   - 2: `TL_sendMessageRecordVideoAction` (recording video note)
   - 3: `TL_sendMessageUploadPhotoAction` (uploading image)
3. Client Side Triggers:
   In `TMessagesProj/src/main/java/org/telegram/ui/ChatActivity.java`, `chatActivityEnterView` attaches a `TextWatcher` that calls `MessagesController.getInstance(currentAccount).sendTyping(...)` every 4.5 seconds of user input.
   Dropping it inside `MessagesController.sendTyping()` guarantees that neither keyboard input nor voice record triggers will emit network packets.
4. TDLib Equivalent:
   In TDLib, the client sends `td_api.SendChatAction(chatId, messageThreadId, action)`.
   Simply do not invoke `client.send(new TdApi.SendChatAction(...))` from the UI controller when Spy Mode is enabled.
                """.trimIndent()
            ),
            CodePatch(
                id = "anti_delete",
                title = "3. Anti-Delete: Cache & SQLite Schema Persistence",
                repoPath = "TMessagesProj/src/main/java/org/telegram/messenger/MessagesStorage.java",
                className = "org.telegram.messenger.MessagesStorage",
                targetMethods = "markMessagesAsDeleted(), emptyHistory()",
                purpose = "Prevent deletion of incoming messages when sender revokes them. Intercept delete updates and update SQLite flag instead of purging rows.",
                language = "Java",
                originalSnippet = """
// Original MessagesStorage.java in DrKLO/Telegram:
public void markMessagesAsDeleted(long dialog_id, ArrayList<Integer> messages, boolean isChannel) {
    try {
        String ids = TextUtils.join(",", messages);
        // Permanently removes rows from SQLite database!
        database.executeFast(String.format(Locale.US, 
            "DELETE FROM messages WHERE mid IN(%s)", ids)).stepThis().dispose();
        database.executeFast(String.format(Locale.US, 
            "DELETE FROM media_v4 WHERE mid IN(%s)", ids)).stepThis().dispose();
    } catch (Exception e) {
        FileLog.e(e);
    }
}
                """.trimIndent(),
                patchedSnippet = """
// PATCHED MessagesStorage.java (AyuGram / TeleMod Style):

// 1. Schema Migration (Run during database initialization):
// In MessagesStorage.openDatabase():
// database.executeFast("ALTER TABLE messages ADD COLUMN is_deleted INTEGER DEFAULT 0;");

public void markMessagesAsDeleted(long dialog_id, ArrayList<Integer> messages, boolean isChannel) {
    try {
        String ids = TextUtils.join(",", messages);
        
        // [PRIVACY HOOK]: Anti-Delete Interception
        if (AyuConfig.isAntiDeleteEnabled()) {
            long deleteTimestamp = System.currentTimeMillis() / 1000;
            // DO NOT DELETE! Mark as deleted instead:
            database.executeFast(String.format(Locale.US, 
                "UPDATE messages SET is_deleted = 1, edit_date = %d WHERE mid IN(%s)", 
                deleteTimestamp, ids)).stepThis().dispose();
                
            // Retain cached media attachments in media_v4 and local storage!
            return;
        }
        
        // Fallback: standard purge if Anti-Delete disabled
        database.executeFast(String.format(Locale.US, 
            "DELETE FROM messages WHERE mid IN(%s)", ids)).stepThis().dispose();
        database.executeFast(String.format(Locale.US, 
            "DELETE FROM media_v4 WHERE mid IN(%s)", ids)).stepThis().dispose();
    } catch (Exception e) {
        FileLog.e(e);
    }
}
                """.trimIndent(),
                detailedWalkthrough = """
1. Location: In DrKLO/Telegram repository:
   - `TMessagesProj/src/main/java/org/telegram/messenger/MessagesStorage.java`
   - `TMessagesProj/src/main/java/org/telegram/messenger/MessagesController.java`
   - `TMessagesProj/src/main/java/org/telegram/messenger/MessageObject.java`
   - `TMessagesProj/src/main/java/org/telegram/ui/Cells/ChatMessageCell.java`
2. Server Delete Updates:
   Telegram servers deliver deletion notifications via MTProto updates:
   - `TLRPC.TL_updateDeleteMessages` (private chats & small groups)
   - `TLRPC.TL_updateDeleteChannelMessages` (supergroups & channels)
3. MessagesController.java Intercept:
   In `MessagesController.processUpdates(...)`:
   Normally calls `storage.markMessagesAsDeleted(...)` and `NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.messagesDeleted, ...)`.
   In custom client, map each targeted `MessageObject.isDeleted = true` and dispatch `NotificationCenter.messagesModified` to trigger a UI re-render without removing the item from the adapter.
4. UI Visual Cue:
   In `ChatMessageCell.java`, add a small red trash icon `ic_delete` or text label `[Deleted]` near the timestamp `timeLayout` to visually differentiate revoked messages from intact ones.
                """.trimIndent()
            ),
            CodePatch(
                id = "central_network_hook",
                title = "4. Central Network Hook: ConnectionsManager Gateway",
                repoPath = "TMessagesProj/src/main/java/org/telegram/tgnet/ConnectionsManager.java",
                className = "org.telegram.tgnet.ConnectionsManager",
                targetMethods = "sendRequest(TLObject object, RequestDelegate onComplete, ...)",
                purpose = "Single centralized firewall to intercept and drop any outbound MTProto RPC packet (read history, stories, typing, online status) before serialization.",
                language = "Java",
                originalSnippet = """
// Original ConnectionsManager.java:
public int sendRequest(TLObject object, RequestDelegate onComplete, 
                       QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, 
                       int flags, int datacenterId, int conType, boolean immediate) {
    final int requestToken = lastRequestToken.getAndIncrement();
    Utilities.stageQueue.postRunnable(() -> {
        sendRequestInternal(object, onComplete, onQuickAck, onWriteToSocket, 
                            flags, datacenterId, conType, immediate, requestToken);
    });
    return requestToken;
}
                """.trimIndent(),
                patchedSnippet = """
// PATCHED ConnectionsManager.java (Master Privacy Filter):
public int sendRequest(TLObject object, RequestDelegate onComplete, 
                       QuickAckDelegate onQuickAck, WriteToSocketDelegate onWriteToSocket, 
                       int flags, int datacenterId, int conType, boolean immediate) {

    // [CENTRAL PRIVACY FIREWALL]
    if (object instanceof TLRPC.TL_messages_readHistory || 
        object instanceof TLRPC.TL_channels_readHistory) {
        if (AyuConfig.isGhostModeEnabled()) {
            if (onComplete != null) {
                // Return synthetic TLRPC.TL_boolTrue to satisfy caller contract
                onComplete.run(new TLRPC.TL_boolTrue(), null);
            }
            return 0; // Dropped without hitting Native MTProto C++ layer!
        }
    }

    if (object instanceof TLRPC.TL_messages_setTyping) {
        if (AyuConfig.isHideTypingEnabled()) {
            if (onComplete != null) onComplete.run(new TLRPC.TL_boolTrue(), null);
            return 0; // Dropped
        }
    }

    if (object instanceof TLRPC.TL_stories_readStories) {
        if (AyuConfig.isGhostStoriesEnabled()) {
            if (onComplete != null) onComplete.run(new TLRPC.TL_boolTrue(), null);
            return 0; // Dropped
        }
    }

    final int requestToken = lastRequestToken.getAndIncrement();
    Utilities.stageQueue.postRunnable(() -> {
        sendRequestInternal(object, onComplete, onQuickAck, onWriteToSocket, 
                            flags, datacenterId, conType, immediate, requestToken);
    });
    return requestToken;
}
                """.trimIndent(),
                detailedWalkthrough = """
Why hook ConnectionsManager?
- Single Point of Interception: Rather than modifying 20+ UI Activities and controllers, hooking `ConnectionsManager.sendRequest()` intercepts packets globally across the entire app.
- Native Isolation: All Java TLObject calls pass through `ConnectionsManager` before JNI invocation into `NativeByteBuffer` and MTProto C++ socket engine.
- Synthetic Ack: Returning `TLRPC.TL_boolTrue()` into `RequestDelegate` ensures that upstream callers don't throw NPEs or crash waiting for response callbacks.
                """.trimIndent()
            ),
            CodePatch(
                id = "tdlib_comparison",
                title = "5. TDLib (Telegram Database Library) Implementation",
                repoPath = "TDLib C++ / Java JNI Client Layer (td/telegram/Client.java)",
                className = "org.drinkless.tdlib.Client & Custom MessageCache",
                targetMethods = "Client.send(TdApi.Function), onResult(TdApi.Object)",
                purpose = "How to achieve identical Ghost Mode, Hide Typing, and Anti-Delete features when using official TDLib JSON/JNI interface.",
                language = "Kotlin",
                originalSnippet = """
// Standard TDLib Kotlin Wrapper:
fun markAsRead(chatId: Long, messageIds: LongArray) {
    client.send(TdApi.ViewMessages(chatId, 0, messageIds, true)) { /* result */ }
}

fun sendTyping(chatId: Long) {
    client.send(TdApi.SendChatAction(chatId, 0, TdApi.ChatActionTyping())) { /* result */ }
}
                """.trimIndent(),
                patchedSnippet = """
// PATCHED TDLib Privacy Interceptor:
class PrivacyAwareTdClient(private val baseClient: TdApi.Client) {

    var ghostMode = true
    var hideTyping = true
    var antiDelete = true
    val localMessageStore = HashMap<Long, MutableMap<Long, CachedTdMessage>>()

    fun send(query: TdApi.Function<*>, handler: TdApi.Client.ResultHandler) {
        when (query) {
            is TdApi.ViewMessages -> {
                if (ghostMode) {
                    // Suppress TdApi.ViewMessages! TDLib will not mark as read on DC.
                    handler.onResult(TdApi.Ok())
                    return
                }
            }
            is TdApi.SendChatAction -> {
                if (hideTyping) {
                    // Suppress typing or voice record action
                    handler.onResult(TdApi.Ok())
                    return
                }
            }
        }
        baseClient.send(query, handler)
    }

    // Intercept updates from TDLib event loop:
    fun handleIncomingUpdate(update: TdApi.Update) {
        when (update) {
            is TdApi.UpdateNewMessage -> {
                // Cache locally in SQLite/Room
                saveToLocalDb(update.message)
            }
            is TdApi.UpdateDeleteMessages -> {
                if (antiDelete) {
                    // DO NOT delete from local UI or Room database!
                    // Mark as deleted:
                    update.messageIds.forEach { msgId ->
                        markMessageAsDeletedInDb(update.chatId, msgId)
                    }
                    // Dispatch custom UI notification:
                    notifyMessageRevoked(update.chatId, update.messageIds)
                    return // Stop propagation to default UI
                }
            }
        }
    }
}
                """.trimIndent(),
                detailedWalkthrough = """
TDLib Differences:
1. TDLib encapsulates MTProto protocol, encryption, and local database (SQLite) internally in C++ core (`libtdjson.so` or `libtdjni.so`).
2. Ghost Mode in TDLib:
   TDLib sends read receipts when `td_api.viewMessages` or `td_api.openChat` is invoked.
   To preserve Ghost Mode, do NOT send `viewMessages` with `force_read = true`, and intercept `viewMessages` calls from the UI.
3. Anti-Delete in TDLib:
   Because TDLib's internal SQLite database automatically deletes rows upon receiving `updateDeleteMessages` from Telegram DC, an Anti-Delete client using TDLib MUST maintain its own secondary Room / SQLite database!
   Save every `updateNewMessage` to your app's private Room database so when TDLib purges its internal store, your client seamlessly retrieves the message from your secondary store.
                """.trimIndent()
            ),
            CodePatch(
                id = "build_vars",
                title = "6. BuildVars.java: API Credentials & Packaging",
                repoPath = "TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java",
                className = "org.telegram.messenger.BuildVars",
                targetMethods = "APP_ID, APP_HASH, BUILD_VERSION_STRING",
                purpose = "Configures official Telegram MTProto connection credentials obtained from my.telegram.org.",
                language = "Java",
                originalSnippet = """
// Original BuildVars.java template in DrKLO/Telegram:
public class BuildVars {
    public static int APP_ID = 0; // Set your API ID from my.telegram.org
    public static String APP_HASH = ""; // Set your API HASH from my.telegram.org
    public static String BUILD_VERSION_STRING = "11.2.0";
    public static boolean DEBUG_VERSION = false;
}
                """.trimIndent(),
                patchedSnippet = """
// Configured BuildVars.java with your credentials:
package org.telegram.messenger;

public class BuildVars {
    public static int APP_ID = 33770220;
    public static String APP_HASH = "63d9d50827ad3e87d6b01aab4789ac49";
    public static String BUILD_VERSION_STRING = "11.2.0 (TeleMod v5.4.1)";
    public static boolean DEBUG_VERSION = false;
    public static boolean CHECK_UPDATES = false;
    public static String PLAYSTORE_APP_URL = "";
}
                """.trimIndent(),
                detailedWalkthrough = """
1. Location: In DrKLO/Telegram repository, open `TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java`.
2. Setting Credentials:
   - Replace `APP_ID` with `33770220` (integer).
   - Replace `APP_HASH` with `"63d9d50827ad3e87d6b01aab4789ac49"` (string).
3. APK Packaging:
   - Run `gradle :app:assembleDebug` or `gradle :app:assembleRelease`.
   - The compiled APK is placed in `app/build/outputs/apk/debug/app-debug.apk` and copied to `downloadapkfile/TeleMod.apk`.
                """.trimIndent()
            )
        )
    }
}
