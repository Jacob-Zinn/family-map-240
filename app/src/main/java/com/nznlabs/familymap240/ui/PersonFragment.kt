package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.nznlabs.familymap240.adapter.PersonListAdapter
import com.nznlabs.familymap240.databinding.FragmentPersonBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import models.Event
import models.Person
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PersonFragment : BaseFragment<FragmentPersonBinding>()  {

    private val viewModel by sharedViewModel<MainViewModel>()
    private val args: PersonFragmentArgs by navArgs()
    private lateinit var personAdapter: PersonListAdapter

    override fun bind(): FragmentPersonBinding {
        return FragmentPersonBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.persons.value?.let {
            val rootPerson: Person = it[args.personID]!!
            initListView(rootPerson, it)
        }
    }


    private fun initListView(rootPerson: Person, persons: MutableMap<String, Person>) {
        val events: List<Event> = viewModel.personEvents.value?.get(rootPerson.personID)?.toList() ?: mutableListOf()
        val sortedEvents = sortEvents(events)
        val relatives = findRelatives(rootPerson, persons)

        personAdapter = PersonListAdapter(events = sortedEvents, relatives = relatives)
    }

    private fun sortEvents(events: List<Event>): List<Event> {

        val birthEvents = events.filter { it.eventType == "birth" }
        val otherEvents = events.filter {it.eventType != "birth" && it.eventType != "death"}.sortedBy { it.year }
        val deathEvents = events.filter { it.eventType == "death" }

        val sortedEvents = mutableListOf<Event>()
        sortedEvents.addAll(birthEvents)
        sortedEvents.addAll(otherEvents)
        sortedEvents.addAll(deathEvents)
        return sortedEvents
    }

    private fun findRelatives(rootPerson: Person, persons: MutableMap<String, Person>): Map<String, Any?> {
        val relatives = mutableMapOf<String, Any?>()
        relatives["father"] = persons[rootPerson.fatherID]
        relatives["mother"] = persons[rootPerson.motherID]
        relatives["spouse"] = persons[rootPerson.spouseID]
        // checking for children
        val children = mutableListOf<Person>()
        for (person in persons) {
            if (person.value.fatherID == rootPerson.personID || person.value.motherID == rootPerson.personID) {
                children.add(person.value)
            }
        }
        relatives["children"] = children

        return relatives
    }

}