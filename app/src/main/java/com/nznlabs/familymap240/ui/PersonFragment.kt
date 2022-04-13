package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nznlabs.familymap240.adapter.PersonListAdapter
import com.nznlabs.familymap240.databinding.FragmentPersonBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import models.Event
import models.Person
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class PersonFragment : BaseFragment<FragmentPersonBinding>() , PersonListAdapter.Interaction {

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
            initPersonInfo(rootPerson)
            initListView(rootPerson, it)
        }
    }

    private fun initPersonInfo(person: Person){
        binding.firstName.text = person.firstName
        binding.lastName.text = person.lastName
        binding.gender.text = if (person.gender == "m") "Male" else "Female"
    }

    private fun initListView(rootPerson: Person, persons: MutableMap<String, Person>) {
        val events: List<Event> = viewModel.personEvents.value?.get(rootPerson.personID)?.toList() ?: mutableListOf()
        val sortedEvents = sortEvents(events)
        val relatives = findRelatives(rootPerson, persons)

        personAdapter = PersonListAdapter(events = sortedEvents, relatives, rootPerson, interaction = this)
        binding.listView.setAdapter(personAdapter)
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

    override fun personSelected(person: Person) {
        try {
            findNavController().navigate(PersonFragmentDirections.actionPersonFragmentSelf(personID = person.personID))
        } catch (e: Exception) {
            Timber.d("Navigation to person fragment failed.")
        }
    }

    override fun eventSelected(event: Event) {
        try {
            findNavController().navigate(PersonFragmentDirections.actionPersonFragmentToEventFragment(eventID = event.eventID))
        } catch (e: Exception) {
            Timber.d("Navigation to event fragment failed.")
        }
    }

}