package com.example

import com.example.data.model.PacketStatus
import com.example.data.repository.TeleModRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TeleModRepositoryTest {

    private lateinit var repository: TeleModRepository

    @Before
    fun setup() {
        repository = TeleModRepository()
    }

    @Test
    fun testGhostModeBlocksReadReceipts() {
        // Ghost mode enabled by default
        assertTrue(repository.settings.value.ghostMode)

        repository.simulateReadMessages()
        val latestPacket = repository.packets.value.first()

        assertEquals("TLRPC.TL_messages_readHistory", latestPacket.method)
        assertEquals(PacketStatus.BLOCKED, latestPacket.status)
    }

    @Test
    fun testHideTypingBlocksSetTyping() {
        assertTrue(repository.settings.value.hideTyping)

        repository.simulateUserTyping()
        val latestPacket = repository.packets.value.first()

        assertEquals("TLRPC.TL_messages_setTyping", latestPacket.method)
        assertEquals(PacketStatus.BLOCKED, latestPacket.status)
    }

    @Test
    fun testAntiDeleteCachesDeletedMessage() {
        assertTrue(repository.settings.value.antiDelete)

        // Simulate incoming message
        repository.simulateIncomingMessage("Classified message")
        val activeChatBefore = repository.getActiveChat()!!
        val msgCountBefore = activeChatBefore.messages.size

        // Simulate delete
        repository.simulateDeleteLastMessage()

        // Message should NOT be removed from list; it should be flagged isDeleted=true
        val activeChatAfter = repository.getActiveChat()!!
        assertEquals(msgCountBefore, activeChatAfter.messages.size)
        val deletedMsg = activeChatAfter.messages.last()
        assertTrue(deletedMsg.isDeleted)
    }

    @Test
    fun testTogglingPrivacySettings() {
        repository.toggleGhostMode()
        assertFalse(repository.settings.value.ghostMode)

        repository.simulateReadMessages()
        val packet = repository.packets.value.first()
        assertEquals(PacketStatus.PASSED, packet.status)
    }
}
