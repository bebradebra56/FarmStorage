package com.farmerinven.apsola.eiowjf.domain.usecases

import android.util.Log
import com.farmerinven.apsola.eiowjf.data.repo.FarmStorageRepository
import com.farmerinven.apsola.eiowjf.data.utils.FarmStoragePushToken
import com.farmerinven.apsola.eiowjf.data.utils.FarmStorageSystemService
import com.farmerinven.apsola.eiowjf.domain.model.FarmStorageEntity
import com.farmerinven.apsola.eiowjf.domain.model.FarmStorageParam
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication

class FarmStorageGetAllUseCase(
    private val farmStorageRepository: FarmStorageRepository,
    private val farmStorageSystemService: FarmStorageSystemService,
    private val farmStoragePushToken: FarmStoragePushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : FarmStorageEntity?{
        val params = FarmStorageParam(
            farmStorageLocale = farmStorageSystemService.farmStorageGetLocale(),
            farmStoragePushToken = farmStoragePushToken.farmStorageGetToken(),
            farmStorageAfId = farmStorageSystemService.farmStorageGetAppsflyerId()
        )
        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Params for request: $params")
        return farmStorageRepository.farmStorageGetClient(params, conversion)
    }



}