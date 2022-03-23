package com.nznlabs.familymap240.di

import com.google.gson.Gson
import com.nznlabs.familymap240.repository.ServerProxy
import com.nznlabs.familymap240.session.SessionManager
import com.nznlabs.familymap240.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Modules {
    val appModule = module {
        single { SessionManager() }
        single { ServerProxy() }
    }

    val viewModelModule = module {
        viewModel { MainViewModel() }
    }

}