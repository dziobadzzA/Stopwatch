package com.example.stopwatch

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.stopwatch.databinding.StopwatchItemBinding


class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(stopwatch: Stopwatch, holder: StopwatchViewHolder) {

        stopwatch.bindingItem = binding
        stopwatch.bindingItem?.stopwatchTimer!!.text = stopwatch.currentMs.displayTime()
        stopwatch.viewHolder = holder

        stopwatch.bindingItem?.customView?.setPeriod(stopwatch.period)
        stopwatch.bindingItem?.customView?.setCurrent(stopwatch.currentMs, true)

        if (stopwatch.isStarted)
            startTimer(stopwatch)
        else
            stopTimer(stopwatch)

        if (stopwatch.state){
            closeState(stopwatch)
        }

        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {

        stopwatch.bindingItem?.startPauseButton?.setOnClickListener {

            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)
                stopTimer(stopwatch)
            } else {
                listener.start(stopwatch.id)
                startTimer(stopwatch)
            }

        }

        stopwatch.bindingItem?.restartButton?.setOnClickListener {
            clear(stopwatch)
        }

        stopwatch.bindingItem?.deleteButton?.setOnClickListener {
            listener.delete(stopwatch.id)
        }

    }

    private fun startTimer(stopwatch: Stopwatch) {

        stopwatch.state = false

        val drawable = resources.getDrawable(R.drawable.ic_baseline_pause_24)

        stopwatch.bindingItem?.startPauseButton?.setImageDrawable(drawable)
        stopwatch.bindingItem?.textState?.text = "Stop"

        updateColor(Color.WHITE, stopwatch)

        stopwatch.timer?.cancel()
        stopwatch.timer = getCountDownTimer(stopwatch)
        stopwatch.timer?.start()

        stopwatch.bindingItem?.blinkingIndicator!!.isInvisible = false

        stopwatch.bindingItem?.blinkingIndicator!!.setBackgroundResource(R.drawable.blinkingcircle)

        (stopwatch.bindingItem!!.blinkingIndicator.background as? AnimationDrawable)?.start()

    }

    private fun stopTimer(stopwatch: Stopwatch) {

        val drawable = resources.getDrawable(R.drawable.ic_baseline_play_arrow_24)
        stopwatch.bindingItem?.startPauseButton?.setImageDrawable(drawable)
        stopwatch.bindingItem?.textState?.text = "Start"

        stopwatch.timer?.cancel()

        stopwatch.bindingItem?.blinkingIndicator!!.isInvisible = true
        (stopwatch.bindingItem!!.blinkingIndicator.background as? AnimationDrawable)?.stop()

    }

    fun closeStart(stopwatch: Stopwatch){

       if (stopwatch.isStarted)
           startTimer(stopwatch)
       else
           stopTimer(stopwatch)

    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs -= interval
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                if (stopwatch.currentMs == 0L){
                    onFinish()
                    stopwatch.state = true
                }
                else
                    stopwatch.bindingItem?.customView?.setCurrent(stopwatch.currentMs, false)

            }

            override fun onFinish() {
               closeState(stopwatch)
            }
        }

    }

    private fun closeState(stopwatch: Stopwatch){
        updateColor(Color.BLUE, stopwatch)
        clear(stopwatch)
    }

    private companion object {

        private const val UNIT_TEN_MS = 20L
        private const val PERIOD = 1000L * 60L * 60L * 24L // Day

    }

    private fun clear(stopwatch: Stopwatch){
        stopTimer(stopwatch)
        val time = listener.reset(stopwatch.id)
        stopwatch.bindingItem?.stopwatchTimer!!.text = time.displayTime()
        stopwatch.currentMs = time
        stopwatch.bindingItem?.customView?.setCurrent(stopwatch.currentMs, true)
    }

    private fun updateColor(int: Int, stopwatch: Stopwatch){
        stopwatch.bindingItem?.fieldItem?.setBackgroundColor(int)
        stopwatch.bindingItem?.stopwatchTimer?.setBackgroundColor(int)
        stopwatch.bindingItem?.blinkingIndicator?.setBackgroundColor(int)
        stopwatch.bindingItem?.deleteButton?.setBackgroundColor(int)
        stopwatch.bindingItem?.startPauseButton?.setBackgroundColor(int)
        stopwatch.bindingItem?.restartButton?.setBackgroundColor(int)
        stopwatch.bindingItem?.customView?.setBackgroundColor(int)
        stopwatch.bindingItem?.textState?.setBackgroundColor(int)
    }



}