package io.github.elaydis.calllogmonitor.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.elaydis.calllogmonitor.databinding.ItemCallLogBinding
import io.github.elaydis.calllogmonitor.utils.formatToHoursMinutesAndSeconds

class CallLogRecyclerViewAdapter : RecyclerView.Adapter<CallLogRecyclerViewAdapter.ViewHolder>() {

    private var logEntryModels = mutableListOf<LogEntryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindLogEntryModel(logEntryModels[position])
    }

    override fun getItemCount(): Int {
        return logEntryModels.size
    }

    fun addLogEntryModel(logEntryModel: LogEntryModel) {
        logEntryModels.add(logEntryModel)
        notifyItemInserted(logEntryModels.size - 1)
    }

    inner class ViewHolder(private val binding: ItemCallLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLogEntryModel(callLogEntryModel: LogEntryModel) {
            with(binding) {
                nameTextView.text = callLogEntryModel.callerName
                durationTextView.text = callLogEntryModel.duration.formatToHoursMinutesAndSeconds()
            }
        }
    }
}
