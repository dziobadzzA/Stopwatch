package com.example.stopwatch

import android.os.CountDownTimer
import com.example.stopwatch.databinding.StopwatchItemBinding

data class Stopwatch(
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var timer: CountDownTimer? = null,
    var bindingItem: StopwatchItemBinding? = null,
    var viewHolder: StopwatchViewHolder? = null,
    var period: Long,
    var state: Boolean
)