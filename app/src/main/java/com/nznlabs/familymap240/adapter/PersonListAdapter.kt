package com.nznlabs.familymap240.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.nznlabs.familymap240.databinding.LayoutGroupItemBinding
import com.nznlabs.familymap240.databinding.LayoutPersonListItemBinding
import models.Event
import models.Person
import java.lang.IllegalArgumentException

class PersonListAdapter(private val events: List<Event>, private val relatives: Map<String, Any?>) :
    BaseExpandableListAdapter() {

    companion object {
        private const val EVENT_GROUP_POS = 0
        private const val RELATIVE_GROUP_POS = 1
    }

    override fun getGroupCount(): Int {
        return 2
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return when (groupPosition) {
            EVENT_GROUP_POS -> {
                events.size
            }
            RELATIVE_GROUP_POS -> {
                relatives.size
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
                return when (childPosition) {
                    0 -> {
                        relatives["father"] as Person
                    }
                    1 -> {
                        relatives["mother"] as Person
                    }
                    2 -> {
                        relatives["spouse"] as Person
                    }
                    else -> {
                        val children = relatives["children"] as List<*>
                        children[childPosition - 3] as Person
                    }
                }
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

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {

        val itemBinding = LayoutGroupItemBinding.inflate(
            LayoutInflater.from(parent?.context),
            parent,
            false
        )

        when (groupPosition) {
            EVENT_GROUP_POS -> {
                itemBinding.title.text = "LIFE EVENTS"
            }
            RELATIVE_GROUP_POS -> {
                itemBinding.title.text = "FAMILY"
            }
            else -> throw IllegalArgumentException()
        }

        return itemBinding.title
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view: View

        when (groupPosition) {
            EVENT_GROUP_POS -> {
                view = LayoutGroupItemBinding.inflate(
                    LayoutInflater.from(parent?.context),
                    parent,
                    false
                )
                    itemBinding.title.text = "LIFE EVENTS"
            }
            RELATIVE_GROUP_POS -> {
                itemBinding.title.text = "FAMILY"
            }
            else -> throw IllegalArgumentException()
        }

        return itemBinding.title
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        TODO("Not yet implemented")
    }

}