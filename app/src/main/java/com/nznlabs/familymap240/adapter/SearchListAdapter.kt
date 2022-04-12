package com.nznlabs.familymap240.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.nznlabs.familymap240.R
import com.nznlabs.familymap240.databinding.LayoutEventListItemBinding
import com.nznlabs.familymap240.databinding.LayoutPersonListItemBinding
import models.Event
import models.Person

class SearchListAdapter(
    private val persons: List<Person>,
    private val events: List<Event>,
    private val interaction: Interaction?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val PERSON_ITEM = 0
        private const val EVENT_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // Currently useless when statement, but keeping framework anyways
        when (viewType) {

            PERSON_ITEM -> {
                val itemBinding = LayoutPersonListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PersonViewHolder(itemBinding, interaction)
            }

            EVENT_ITEM -> {
                val itemBinding = LayoutEventListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return EventViewHolder(itemBinding, interaction)
            }
            else -> {
                val itemBinding = LayoutPersonListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return PersonViewHolder(itemBinding, interaction)
            }

        }
    }

    internal inner class SessionsRecyclerChangeCallback(
        private val adapter: SearchListAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PersonViewHolder -> {
                holder.bind(persons[position])
            }
            is EventViewHolder -> {
                holder.bind(events[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < persons.size) PERSON_ITEM else EVENT_ITEM
    }

    override fun getItemCount(): Int {
        return (events.size + persons.size)
    }

    class PersonViewHolder
    constructor(
        private val itemBinding: LayoutPersonListItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Person) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.personSelected(bindingAdapterPosition, person = item)
            }

            if (item.gender == "m") {
                itemBinding.genderImg.setImageResource(R.drawable.ic_baseline_male_blue_24)
            } else {
                itemBinding.genderImg.setImageResource(R.drawable.ic_round_female_red_24)
            }
            itemBinding.name.text = "${item.firstName}  ${item.lastName}"

        }
    }

    class EventViewHolder constructor(
        private val itemBinding: LayoutEventListItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: Event) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.eventSelected(bindingAdapterPosition, event = item)
            }
            itemBinding.eventInfo.text = item.eventType
            itemBinding.name.text = item.associatedUsername
        }
    }


    interface Interaction {
        fun personSelected(position: Int, person: Person)
        fun eventSelected(position: Int, event: Event)
    }

}
