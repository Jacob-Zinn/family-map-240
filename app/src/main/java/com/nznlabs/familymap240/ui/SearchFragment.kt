package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nznlabs.familymap240.adapter.SearchListAdapter
import com.nznlabs.familymap240.databinding.FragmentSearchBinding
import com.nznlabs.familymap240.viewmodel.MainViewModel
import models.Event
import models.Person
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber


class SearchFragment : BaseFragment<FragmentSearchBinding>(), SearchListAdapter.Interaction, TextWatcher {

    private val viewModel by sharedViewModel<MainViewModel>()

    private lateinit var searchAdapter: SearchListAdapter


    override fun bind(): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchBar()
        initRecView()
        binding.close.setOnClickListener{
            binding.searchBar.setText("", TextView.BufferType.EDITABLE)
        }
    }

    private fun initSearchBar() {
        binding.searchBar.addTextChangedListener(this)
    }

    private fun initRecView() {

        searchAdapter = SearchListAdapter(
            persons = listOf(),
            events = listOf(),
            this
        )
        binding.recView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun querySearch() {
        val search: String = binding.searchBar.text.toString().lowercase()
        if (search.isBlank()) {
            searchAdapter = SearchListAdapter(persons = listOf(), events = listOf(), this)
            binding.recView.adapter = searchAdapter
            return
        }
        val personsResult = mutableListOf<Person>()
        val eventsResult = mutableListOf<Event>()
        viewModel.persons.value?.let { persons ->
            for (person in persons) {
                if ("${person.value.firstName} ${person.value.lastName}".lowercase().contains(search)) {
                    personsResult.add(person.value)
                }
            }
        }

        viewModel.events.value?.let { events ->
            for (event in events) {
                if ("${event.value.eventType} ${event.value.country} ${event.value.city} ${event.value.year}".lowercase().contains(search)) {
                    eventsResult.add(event.value)
                }
            }
        }

        searchAdapter = SearchListAdapter(
            persons = personsResult,
            events = eventsResult,
            this
        )
        binding.recView.adapter = searchAdapter

    }

    override fun personSelected(position: Int, person: Person) {
        try {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToPersonFragment(personID = person.personID))
        } catch (e: Exception) {
            Timber.d("Navigation to person fragment failed.")
        }
    }

    override fun eventSelected(position: Int, event: Event) {
        try {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToEventFragment(eventID = event.eventID))
        } catch (e: Exception) {
            Timber.d("Navigation to event fragment failed.")
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {
        querySearch()
    }

}