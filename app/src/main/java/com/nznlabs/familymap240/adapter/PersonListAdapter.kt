package com.nznlabs.familymap240.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.nznlabs.familymap240.R
import com.nznlabs.familymap240.databinding.LayoutExpandedEventListItemBinding
import com.nznlabs.familymap240.databinding.LayoutExpandedPersonListItemBinding
import com.nznlabs.familymap240.databinding.LayoutGroupItemBinding
import models.Event
import models.Person
import java.lang.IllegalArgumentException

class PersonListAdapter(
    private val events: List<Event>,
    private val relatives: List<Map<String, Person>>,
    private val rootPerson: Person,
    private val interaction: Interaction?
) :
    BaseExpandableListAdapter() {

    companion object {
        private const val EVENT_GROUP_POS = 0
        private const val RELATIVE_GROUP_POS = 1
    }

    override fun getGroupCount(): Int {
        return 2
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        when (groupPosition) {
            EVENT_GROUP_POS -> {
                return events.size
            }
            RELATIVE_GROUP_POS -> {
                return relatives.size
//                var count = 0
//                if (relatives["mother"] != null) {
//                    count++
//                }
//                if (relatives["father"] != null) {
//                    count++
//                }
//                if (relatives["spouse"] != null) {
//                    count++
//                }
//                count += (relatives["children"] as List<*>).size
//                return count
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return when (groupPosition) {
            EVENT_GROUP_POS -> {
                "LIFE EVENTS"
            }
            RELATIVE_GROUP_POS -> {
                "FAMILY"
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        when (groupPosition) {
            EVENT_GROUP_POS -> {
                return events[groupPosition]
            }
            RELATIVE_GROUP_POS -> {
                return relatives[groupPosition]
//
//                return when (childPosition) {
//                    0 -> {
//                        relatives["father"] as Person
//                    }
//                    1 -> {
//                        relatives["mother"] as Person
//                    }
//                    2 -> {
//                        relatives["spouse"] as Person
//                    }
//                    else -> {
//                        val children = relatives["children"] as List<*>
//                        children[childPosition - 3] as Person
//                    }
//                }
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    @SuppressLint("SetTextI18n")
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {

        val groupBinding = LayoutGroupItemBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )

        when (groupPosition) {
            EVENT_GROUP_POS -> {
                groupBinding.title.text = "LIFE EVENTS"
            }
            RELATIVE_GROUP_POS -> {
                groupBinding.title.text = "FAMILY"
            }
            else -> throw IllegalArgumentException()
        }

        return groupBinding.root
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childView: View

        when (groupPosition) {
            EVENT_GROUP_POS -> {
                val childBinding = LayoutExpandedEventListItemBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                initEventView(childBinding, childPosition)
                childView = childBinding.root
            }
            RELATIVE_GROUP_POS -> {
                val childBinding = LayoutExpandedPersonListItemBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                initRelativeView(childBinding, childPosition)
                childView = childBinding.root
            }
            else -> throw IllegalArgumentException()
        }

        return childView
    }

    override fun isChildSelectable(groupPosition: Int, childPositio: Int): Boolean {
        return true
    }


    @SuppressLint("SetTextI18n")
    private fun initEventView(binding: LayoutExpandedEventListItemBinding, childPosition: Int) {
        val event: Event = events[childPosition]
        binding.eventInfo.text = "${event.eventType.uppercase()}: ${event.city}, ${event.country} (${event.year})"
        binding.name.text = "${rootPerson.firstName} ${rootPerson.lastName}"
        binding.root.setOnClickListener {
            interaction?.eventSelected(event)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initRelativeView(binding: LayoutExpandedPersonListItemBinding, childPosition: Int) {
        val personObj: Map<String, Person> = relatives[childPosition]
//        val person: Person = when (childPosition) {
//            0 -> {
//                binding.relation.text = "Father"
//                relatives["father"] as Person
//            }
//            1 -> {
//                binding.relation.text = "Mother"
//                relatives["mother"] as Person
//            }
//            2 -> {
//                binding.relation.text = "Spouse"
//                relatives["spouse"] as Person
//            }
//            else -> {
//                binding.relation.text = "Child"
//                val children = relatives["children"] as List<*>
//                children[childPosition - 3] as Person
//            }
//        }

        val relation = personObj.keys.first()
        binding.relation.text = relation

        binding.name.text = "${personObj[relation]!!.firstName} ${personObj[relation]!!.lastName}"
        if (personObj[relation]!!.gender == "m") {
            binding.genderImg.setImageResource(R.drawable.ic_baseline_male_blue_24)
        } else {
            binding.genderImg.setImageResource(R.drawable.ic_round_female_red_24)
        }
        binding.root.setOnClickListener {
            interaction?.personSelected(personObj[relation]!!)
        }
    }

    interface Interaction {
        fun personSelected(person: Person)
        fun eventSelected(event: Event)
    }

}