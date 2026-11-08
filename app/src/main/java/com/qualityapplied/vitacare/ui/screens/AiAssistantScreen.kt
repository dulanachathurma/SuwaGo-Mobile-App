package com.qualityapplied.vitacare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.qualityapplied.vitacare.data.ChatMsg
import com.qualityapplied.vitacare.ui.AppViewModel
import com.qualityapplied.vitacare.ui.components.SmallPill
import com.qualityapplied.vitacare.ui.theme.Slate100
import com.qualityapplied.vitacare.ui.theme.Slate200
import com.qualityapplied.vitacare.ui.theme.Slate400
import com.qualityapplied.vitacare.ui.theme.Slate500
import com.qualityapplied.vitacare.ui.theme.Slate700
import com.qualityapplied.vitacare.ui.theme.Slate800
import com.qualityapplied.vitacare.ui.theme.Teal600
import com.qualityapplied.vitacare.ui.theme.Teal700
import com.qualityapplied.vitacare.ui.tr

/** AI health assistant chat. Answers come from the backend (/api/ai/chat, powered by Claude). */
@Composable
fun AiAssistantScreen(vm: AppViewModel) {
    val lang = LocalConfiguration.current.locales[0].language
    val listState = rememberLazyListState()
    var input by rememberSaveable { mutableStateOf("") }
    val messages = vm.chat

    fun send(text: String) {
        if (text.isBlank() || vm.chatBusy) return
        vm.sendChat(text, lang)
        input = ""
    }

    // Always scroll to the newest message (index 0 = greeting, then messages, then "thinking")
    LaunchedEffect(messages.size, vm.chatBusy) {
        val last = messages.size + if (vm.chatBusy) 1 else 0
        listState.animateScrollToItem(last)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(), // Lifts the typing bar and chat content smoothly above the soft keyboard
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("🤖 " + tr("ai.title"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate800)
                Text(tr("ai.intro"), fontSize = 11.sp, color = Slate500)
            }
            if (messages.isNotEmpty()) {
                SmallPill(tr("ai.clear"), onClick = { vm.clearChat() }, background = Slate100, textColor = Slate500)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { Bubble(ChatMsg("assistant", tr("ai.hello"))) }
            if (messages.isEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ai.s1", "ai.s2", "ai.s3", "ai.s4").forEach { key ->
                            val q = tr(key)
                            val shape = RoundedCornerShape(14.dp)
                            Text(
                                q,
                                fontSize = 13.sp,
                                color = Teal700,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape)
                                    .background(Color.White)
                                    .border(1.dp, Slate200, shape)
                                    .clickable { send(q) }
                                    .padding(12.dp),
                            )
                        }
                    }
                }
            }
            items(messages) { m -> Bubble(m) }
            if (vm.chatBusy) item { Bubble(ChatMsg("assistant", tr("ai.thinking"))) }
        }

        Text(
            tr("ai.disclaimer"),
            fontSize = 9.sp,
            color = Slate400,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
        )

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(tr("ai.placeholder"), color = Slate400, fontSize = 14.sp) },
                maxLines = 3,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { send(input) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal600,
                    unfocusedBorderColor = Slate200,
                    cursorColor = Teal600,
                ),
            )
            Spacer(Modifier.padding(horizontal = 4.dp))
            val canSend = input.isNotBlank() && !vm.chatBusy
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (canSend) Teal600 else Slate200)
                    .clickable(enabled = canSend) { send(input) },
                contentAlignment = Alignment.Center,
            ) {
                Text("➤", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun Bubble(m: ChatMsg) {
    val mine = m.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
    ) {
        val shape = RoundedCornerShape(
            topStart = 16.dp, topEnd = 16.dp,
            bottomStart = if (mine) 16.dp else 4.dp,
            bottomEnd = if (mine) 4.dp else 16.dp,
        )
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(shape)
                .background(if (mine) Teal600 else Color.White)
                .then(if (mine) Modifier else Modifier.border(1.dp, Slate200, shape))
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                m.text,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = if (mine) Color.White else Slate700,
            )
        }
    }
}
