package com.farmerinven.apsola.eiowjf.presentation.di

import com.farmerinven.apsola.eiowjf.data.repo.FarmStorageRepository
import com.farmerinven.apsola.eiowjf.data.shar.FarmStorageSharedPreference
import com.farmerinven.apsola.eiowjf.data.utils.FarmStoragePushToken
import com.farmerinven.apsola.eiowjf.data.utils.FarmStorageSystemService
import com.farmerinven.apsola.eiowjf.domain.usecases.FarmStorageGetAllUseCase
import com.farmerinven.apsola.eiowjf.presentation.pushhandler.FarmStoragePushHandler
import com.farmerinven.apsola.eiowjf.presentation.ui.load.FarmStorageLoadViewModel
import com.farmerinven.apsola.eiowjf.presentation.ui.view.FarmStorageViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val farmStorageModule = module {
    factory {
        FarmStoragePushHandler()
    }
    single {
        FarmStorageRepository()
    }
    single {
        FarmStorageSharedPreference(get())
    }
    factory {
        FarmStoragePushToken()
    }
    factory {
        FarmStorageSystemService(get())
    }
    factory {
        FarmStorageGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        FarmStorageViFun(get())
    }
    viewModel {
        FarmStorageLoadViewModel(get(), get(), get())
    }
}