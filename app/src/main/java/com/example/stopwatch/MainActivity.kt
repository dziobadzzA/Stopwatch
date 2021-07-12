package com.example.stopwatch


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stopwatch.databinding.ActivityMainBinding
import java.lang.Exception
import androidx.lifecycle.ProcessLifecycleOwner


class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private val state = mutableListOf<State>()
    private var nextId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {

            try {

                val time =  Integer.parseInt(binding.textTimeView.text.toString())

                if (time > 0) {

                    val element = Stopwatch(
                        nextId++,
                        binding.textTimeView.text.toString().toInt().toLong() * 60 * 1000,
                        false,
                        period = binding.textTimeView.text.toString().toInt().toLong() * 60 * 1000,
                        state = false
                    )
                    stopwatches.add(element)
                    state.add(State(element.id, element.currentMs))
                    stopwatchAdapter.submitList(stopwatches.toList())
                }

            }
            catch (e:Exception){
                Toast.makeText(this, "Check please number", Toast.LENGTH_LONG).show()
            }



        }


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        val time = getTimeNotification()

        if (time > 0){
            startIntent.putExtra(STARTED_TIMER_TIME_MS, time)
            startService(startIntent)
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun start(id: Int) {
        replaceState(id, true)
        updateStart(id)
    }

    override fun stop(id: Int, currentMs: Long) {
        replaceState(id, false)
    }

    override fun reset(id: Int): Long {
       return updateState(id)
    }

    private fun getTimeNotification(): Long{

        var result = 0L

        for(i in 0 until stopwatchAdapter.currentList.size) {
            if (stopwatchAdapter.currentList[i].isStarted) {
                result = stopwatchAdapter.currentList[i].currentMs
                break
            }
        }

        return result
    }

    override fun delete(id: Int) {

        if (stopwatches.find { it.id == id }!!.isStarted){
           stopwatchAdapter.stopStartTimer(id)
        }

        stopwatches.remove(stopwatches.find { it.id == id })
        state.remove(state.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }


    private fun replaceState(id: Int, isStarted: Boolean) {
        for (i in 0 until stopwatches.size){
            if (id == stopwatches[i].id){
                stopwatches[i].isStarted = isStarted
                break
            }
        }
    }

    private fun updateState(id: Int): Long {

        var result = 0L

        for (i in 0 until stopwatches.size){
            if (id == stopwatches[i].id){
                stopwatches[i].isStarted = false
                stopwatches[i].currentMs = state[i].currentMs
                result = state[i].currentMs
                break
            }
        }

        return result

    }

    private fun updateStart(id: Int){

        for (i in 0 until stopwatches.size)
            if (id != stopwatches[i].id)
                stopwatches[i].isStarted = false

        stopwatchAdapter.stopTimer(id)

    }

}