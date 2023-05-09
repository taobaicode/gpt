package com.aiafmaster.gpt.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatGPTRepository(private val coroutineScope: CoroutineScope) {
    private val _stateFlow = MutableStateFlow<Int>(0)
    private val stateFlow: StateFlow<Int> = _stateFlow

    init {
        coroutineScope.launch {
            var job: Job?=null
            var job2: Job? = null
            val scope = CoroutineScope(Dispatchers.Default + CoroutineName("Test")).launch {
                job = launch {
                    _stateFlow.emit(1)
                    delay(1000)
                    _stateFlow.emit(2)
                    delay(1000)
                    _stateFlow.emit(3)
                    delay(1000)
                    _stateFlow.emit(4)
                }
                job2 = launch {
                    collectFlow()
                }
            }
            while(stateFlow.value != 4) {
                println("StateFlow value ${stateFlow.value}")
                delay(500)
            }
            delay(5000)
            println("Job1 complete ${job!!.isCompleted}")
            println("Job2 complete ${job2!!.isCompleted}")
            _stateFlow.emit(5)
            println("Scope complete? ${scope.isCompleted}")
            scope.cancelAndJoin()
            println("Job2 complete ${job2!!.isCompleted}")
            println("Scope complete? ${scope.isCompleted}")
        }
    }

    private suspend fun collectFlow(): Unit {
        var scope = coroutineScope {
            stateFlow.collect {
                println(it)
            }
        }
    }
}