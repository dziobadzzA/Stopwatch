package com.example.stopwatch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.stopwatch.databinding.StopwatchItemBinding

class StopwatchAdapter(
    private val listener: StopwatchListener
) : ListAdapter<Stopwatch, StopwatchViewHolder>(itemComparator) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(layoutInflater, parent, false)
        return StopwatchViewHolder(binding!!, listener, binding!!.root.context.resources)
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bind(getItem(position), holder)
    }

    fun stopTimer(id:Int){

        for (i in 0 until currentList.size){

            if (id != currentList[i].id)
                currentList[i].isStarted = false

            currentList[i].viewHolder?.closeStart(currentList[i])

        }

    }

    fun stopStartTimer(id:Int){

        for (i in 0 until currentList.size){

            if (id == currentList[i].id) {
                currentList[i].isStarted = false
                currentList[i].viewHolder?.closeStart(currentList[i])
                break
            }

        }

    }

    override fun submitList(list: List<Stopwatch>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    private companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>() {

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) = Any()
        }
    }
}