package com.nznlabs.familymap240.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<BINDING : ViewBinding> : Fragment() {

    // Use private nullable binding in order to set in onCreateView and onDestroyView
    private var _binding: BINDING? = null

    /**
     * Wraps private nullable property. Lifecycle valid from [onCreateView] to [onDestroyView].
     * Treat similar to [requireContext] which throws if context is null, meaning you are aware of when you can call it and should use it.
     */
    protected val binding: BINDING
        get() = _binding!!

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bind()
        return binding.root
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyView(binding)
    }

    /** Override per fragment to add any further onDestroyView related cleanup. */
    @CallSuper
    open fun onDestroyView(binding: BINDING) {
        // Make LeakCanary happy by nulling out binding reference here: https://stackoverflow.com/questions/57647751/android-databinding-is-leaking-memory
        _binding = null
    }

    abstract fun bind(): BINDING
}