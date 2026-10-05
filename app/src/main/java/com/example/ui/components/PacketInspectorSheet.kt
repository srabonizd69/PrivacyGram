package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PacketLog
import com.example.data.model.PacketStatus
import com.example.ui.theme.TelegramDeletedRed
import com.example.ui.theme.TelegramGhostCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacketInspectorSheet(
    packets: List<PacketLog>,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPacket by remember { mutableStateOf<PacketLog?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF090D14),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MTProto RPC Gateway Monitor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TelegramGhostCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "Live TL-Schema Request & Update Interceptions",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    )
                }

                Row {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.testTag("clear_packets_sheet")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "Clear",
                            tint = Color.LightGray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.LightGray
                        )
                    }
                }
            }

            Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 8.dp))

            if (packets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No MTProto network packets captured yet.\nSend messages, scroll, or revoke items to test!",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(packets, key = { it.id }) { pkt ->
                        PacketRow(packet = pkt, onClick = { selectedPacket = pkt })
                    }
                }
            }
        }
    }

    selectedPacket?.let { pkt ->
        AlertDialog(
            onDismissRequest = { selectedPacket = null },
            title = {
                Text(
                    text = pkt.method,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Status: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        val badgeColor = when (pkt.status) {
                            PacketStatus.BLOCKED -> TelegramDeletedRed
                            PacketStatus.PASSED -> Color(0xFF4CAF50)
                            PacketStatus.CACHED -> TelegramGhostCyan
                        }
                        Text(
                            text = pkt.status.name,
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Time: ${pkt.timestamp}", fontSize = 12.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Impact & Hook Explanation:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(pkt.explanation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("MTProto TL Object:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Text(
                            text = pkt.payloadSnippet,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPacket = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PacketRow(packet: PacketLog, onClick: () -> Unit) {
    val badgeColor = when (packet.status) {
        PacketStatus.BLOCKED -> TelegramDeletedRed
        PacketStatus.PASSED -> Color(0xFF4CAF50)
        PacketStatus.CACHED -> TelegramGhostCyan
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = badgeColor.copy(alpha = 0.2f),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = packet.status.name,
                color = badgeColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = packet.method.replace("TLRPC.", ""),
            color = Color(0xFFE2E8F0),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Text(
            text = packet.timestamp,
            color = Color.Gray,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

