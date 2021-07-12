package com.example.stopwatch

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, currentMs: Long)

    fun reset(id: Int): Long

    fun delete(id: Int)
}