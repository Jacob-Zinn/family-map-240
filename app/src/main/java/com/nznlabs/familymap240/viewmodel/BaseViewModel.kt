package com.nznlabs.familymap240.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import org.koin.core.component.KoinComponent
import timber.log.Timber

abstract class BaseViewModel: ViewModel(), KoinComponent {

    // Helper methods based on: https://github.com/hadilq/LiveEvent

    // quite simply, these helper methods allows the ViewModel (only) to update
    // the live data object defined as LiveData (not MutableLiveData) - if callers
    // need to update it, a public setter must be made available

    // helper methods are provided for both set() and postValue() - it is incumbent
    // upon the developer to understand the threading ramifications and distinction

    // example:
    // val publicLiveData: LiveData<AnyDataType> = MutableLiveData()
    // private fun publicSetter(newValue: AnyDataType) { publicLiveData.set(newValue) }

    /** Helper function to avoid needing downcast declarations for public MutableLiveData */
    protected fun <T> LiveData<T>.set(value: T) {
        (this as? MutableLiveData<T>)?.setValue(value)
            ?: (this as? LiveEvent<T>)?.setValue(value)
            ?: run { Timber.w("[post] unable to setValue for $this") }
    }

    /** Helper function to avoid needing downcast declarations for public MutableLiveData */
    protected fun <T> LiveData<T>.postValue(value: T) {
        (this as? MutableLiveData<T>)?.postValue(value)
            ?: (this as? LiveEvent<T>)?.postValue(value)
            ?: run { Timber.w("[post] unable to postValue for $this") }
    }

    protected fun <T> LiveData<T>.forceRefresh(value: T) {
        (this as? MutableLiveData<T>)?.setValue(value)
            ?: (this as? LiveEvent<T>)?.setValue(value)
            ?: run { Timber.w("[post] unable to forceRefreshValue for $this") }
    }
}