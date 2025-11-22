package com.example.smsfirewallsandbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SmsAdapter(private val items: List<SmsModel>) :
    RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderText: TextView = itemView.findViewById(R.id.sender)
        val messageText: TextView = itemView.findViewById(R.id.message)
        val dateText: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val sms = items[position]
        holder.senderText.text = sms.address
        holder.messageText.text = sms.body
        holder.dateText.text = android.text.format.DateFormat.format(
            "dd MMM HH:mm",
            sms.date
        )
    }

    override fun getItemCount(): Int = items.size
}
