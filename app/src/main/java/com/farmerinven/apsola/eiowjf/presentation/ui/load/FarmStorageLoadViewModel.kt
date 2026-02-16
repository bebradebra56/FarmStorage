package com.farmerinven.apsola.eiowjf.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmerinven.apsola.eiowjf.data.shar.FarmStorageSharedPreference
import com.farmerinven.apsola.eiowjf.data.utils.FarmStorageSystemService
import com.farmerinven.apsola.eiowjf.domain.usecases.FarmStorageGetAllUseCase
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageAppsFlyerState
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FarmStorageLoadViewModel(
    private val farmStorageGetAllUseCase: FarmStorageGetAllUseCase,
    private val farmStorageSharedPreference: FarmStorageSharedPreference,
    private val farmStorageSystemService: FarmStorageSystemService
) : ViewModel() {

    private val _farmStorageHomeScreenState: MutableStateFlow<FarmStorageHomeScreenState> =
        MutableStateFlow(FarmStorageHomeScreenState.FarmStorageLoading)
    val farmStorageHomeScreenState = _farmStorageHomeScreenState.asStateFlow()

    private var farmStorageGetApps = false


    init {
        viewModelScope.launch {
            when (farmStorageSharedPreference.farmStorageAppState) {
                0 -> {
                    if (farmStorageSystemService.farmStorageIsOnline()) {
                        FarmStorageApplication.farmStorageConversionFlow.collect {
                            when(it) {
                                FarmStorageAppsFlyerState.FarmStorageDefault -> {}
                                FarmStorageAppsFlyerState.FarmStorageError -> {
                                    farmStorageSharedPreference.farmStorageAppState = 2
                                    _farmStorageHomeScreenState.value =
                                        FarmStorageHomeScreenState.FarmStorageError
                                    farmStorageGetApps = true
                                }
                                is FarmStorageAppsFlyerState.FarmStorageSuccess -> {
                                    if (!farmStorageGetApps) {
                                        farmStorageGetData(it.farmStorageData)
                                        farmStorageGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _farmStorageHomeScreenState.value =
                            FarmStorageHomeScreenState.FarmStorageNotInternet
                    }
                }
                1 -> {
                    if (farmStorageSystemService.farmStorageIsOnline()) {
                        if (FarmStorageApplication.FARM_STORAGE_FB_LI != null) {
                            _farmStorageHomeScreenState.value =
                                FarmStorageHomeScreenState.FarmStorageSuccess(
                                    FarmStorageApplication.FARM_STORAGE_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > farmStorageSharedPreference.farmStorageExpired) {
                            Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Current time more then expired, repeat request")
                            FarmStorageApplication.farmStorageConversionFlow.collect {
                                when(it) {
                                    FarmStorageAppsFlyerState.FarmStorageDefault -> {}
                                    FarmStorageAppsFlyerState.FarmStorageError -> {
                                        _farmStorageHomeScreenState.value =
                                            FarmStorageHomeScreenState.FarmStorageSuccess(
                                                farmStorageSharedPreference.farmStorageSavedUrl
                                            )
                                        farmStorageGetApps = true
                                    }
                                    is FarmStorageAppsFlyerState.FarmStorageSuccess -> {
                                        if (!farmStorageGetApps) {
                                            farmStorageGetData(it.farmStorageData)
                                            farmStorageGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Current time less then expired, use saved url")
                            _farmStorageHomeScreenState.value =
                                FarmStorageHomeScreenState.FarmStorageSuccess(
                                    farmStorageSharedPreference.farmStorageSavedUrl
                                )
                        }
                    } else {
                        _farmStorageHomeScreenState.value =
                            FarmStorageHomeScreenState.FarmStorageNotInternet
                    }
                }
                2 -> {
                    _farmStorageHomeScreenState.value =
                        FarmStorageHomeScreenState.FarmStorageError
                }
            }
        }
    }


    private suspend fun farmStorageGetData(conversation: MutableMap<String, Any>?) {
        val farmStorageData = farmStorageGetAllUseCase.invoke(conversation)
        if (farmStorageSharedPreference.farmStorageAppState == 0) {
            if (farmStorageData == null) {
                farmStorageSharedPreference.farmStorageAppState = 2
                _farmStorageHomeScreenState.value =
                    FarmStorageHomeScreenState.FarmStorageError
            } else {
                farmStorageSharedPreference.farmStorageAppState = 1
                farmStorageSharedPreference.apply {
                    farmStorageExpired = farmStorageData.farmStorageExpires
                    farmStorageSavedUrl = farmStorageData.farmStorageUrl
                }
                _farmStorageHomeScreenState.value =
                    FarmStorageHomeScreenState.FarmStorageSuccess(farmStorageData.farmStorageUrl)
            }
        } else  {
            if (farmStorageData == null) {
                _farmStorageHomeScreenState.value =
                    FarmStorageHomeScreenState.FarmStorageSuccess(farmStorageSharedPreference.farmStorageSavedUrl)
            } else {
                farmStorageSharedPreference.apply {
                    farmStorageExpired = farmStorageData.farmStorageExpires
                    farmStorageSavedUrl = farmStorageData.farmStorageUrl
                }
                _farmStorageHomeScreenState.value =
                    FarmStorageHomeScreenState.FarmStorageSuccess(farmStorageData.farmStorageUrl)
            }
        }
    }


    sealed class FarmStorageHomeScreenState {
        data object FarmStorageLoading : FarmStorageHomeScreenState()
        data object FarmStorageError : FarmStorageHomeScreenState()
        data class FarmStorageSuccess(val data: String) : FarmStorageHomeScreenState()
        data object FarmStorageNotInternet: FarmStorageHomeScreenState()
    }
}