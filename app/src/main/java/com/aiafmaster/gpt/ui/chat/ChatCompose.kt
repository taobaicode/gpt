package com.aiafmaster.gpt.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import com.aiafmaster.gpt.ChatData
import com.aiafmaster.gpt.ChatViewModel
import com.aiafmaster.gpt.R
import kotlinx.coroutines.launch

@Composable
fun chatCompose(chatViewModel: ChatViewModel) {
    val messageHistory by chatViewModel.chatHistory.observeAsState()
    val messages by chatViewModel.chat.observeAsState()
    MaterialTheme {
        chatMain(messages = messages) {message-> chatViewModel.onAsk(message)}
    }
}

@Composable
fun chatMain(messages: List<ChatData>?, onSend: (String)->Unit) {
    ConstraintLayout(modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxHeight()) {
        val (lazyCol, textField, sendButton) = createRefs()
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        if (messages != null) {
//            chatLazyList(messages, Modifier.constrainAs(lazyCol) {
//                top.linkTo(parent.top, margin = 16.dp)
//                bottom.linkTo(row.top, margin = 16.dp)
//            })
            LazyColumn (state=listState, modifier = Modifier
                .constrainAs(lazyCol) {
                    top.linkTo(parent.top, margin = 8.dp)
                    bottom.linkTo(textField.top, margin = 8.dp)
                    height = Dimension.fillToConstraints
                }
                .padding(start = 6.dp, end = 6.dp)) {
                items(count = messages.size) {
                    messageCard(messages[it])
                }
            }
            LaunchedEffect(messages.size){
                coroutineScope.launch {
                    listState.scrollToItem(messages.size-1)
                }
            }
        }
        var message by rememberSaveable {mutableStateOf("")}
//    Row (modifier = modifier){
        OutlinedTextField(modifier = Modifier
//            .padding(start=6.dp)
            .constrainAs(textField) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
                start.linkTo(parent.start, margin = 12.dp)
                end.linkTo(sendButton.start, margin = 6.dp)
                width=Dimension.fillToConstraints },

            value = message,
            onValueChange = { message = it },
            label = { Text("Message") })
        Button(modifier = Modifier.constrainAs(sendButton) {
            bottom.linkTo(parent.bottom, margin = 16.dp)
            end.linkTo(parent.end, margin=16.dp)
//            baseline.linkTo(textField.baseline)
            },
            onClick = {
                onSend(message)
                message=""
            }) {
            Text("Send")
        }
//        messageBox(onSend, Modifier.constrainAs(row) {
//            bottom.linkTo(parent.bottom)
//        })
    }
}

@Composable
fun messageCard(message: ChatData) {
    Card (
        modifier= Modifier
            .fillMaxWidth()
            .padding(8.dp),
        backgroundColor = MaterialTheme.colors.background,
        elevation= 4.dp
            ) {
        Column{
            Row {
                val res = remember{
                    if(message.bot) R.drawable.chatbot_icon else R.drawable.chat_question_icon}
                Icon(
                    painter = painterResource(res),
                    "Question",
                modifier = Modifier.padding(6.dp).width(40.dp).height(40.dp))
            }
            Text(message.content, modifier=Modifier.padding(6.dp))
        }

    }
}

@Composable
fun messageText(message: String) {
    Text(message,
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
//                            .border(
//                            border = BorderStroke(1.dp, Color.Gray),
//                            shape = RoundedCornerShape(4.dp))
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(6.dp))

}

@Composable
fun chatLazyList(messages: List<ChatData>, modifier: Modifier) {
    LazyColumn (modifier = modifier.wrapContentHeight()) {
        items(count = messages.size) {
            Text(messages[it].content)
        }
    }
}
@Composable
fun messageBox(onSend: (String)->Unit, modifier: Modifier) {
    var message by rememberSaveable {mutableStateOf("")}
//    Row (modifier = modifier){
        OutlinedTextField(modifier = modifier,
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") })
        TextButton(modifier = modifier, onClick = { onSend(message) }) {
            Text("Send")
        }
//    }
}