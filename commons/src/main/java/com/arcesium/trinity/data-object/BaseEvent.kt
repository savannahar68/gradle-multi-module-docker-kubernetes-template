package com.example.org.`data-object`

import java.time.LocalDateTime




interface BaseEvent {
    fun startValidTime(): LocalDateTime?

    fun endValidTime(): LocalDateTime? {
        return LocalDateTime.MAX
    }

    fun knowledgeBeginTime(): LocalDateTime?

    fun knowledgeEndTime(): LocalDateTime? {
        return LocalDateTime.MAX
    }
}