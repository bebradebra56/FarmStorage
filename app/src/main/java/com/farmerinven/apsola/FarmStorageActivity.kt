package com.farmerinven.apsola

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.farmerinven.apsola.eiowjf.FarmStorageGlobalLayoutUtil
import com.farmerinven.apsola.eiowjf.farmStorageSetupSystemBars
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication
import com.farmerinven.apsola.eiowjf.presentation.pushhandler.FarmStoragePushHandler
import org.koin.android.ext.android.inject

class FarmStorageActivity : AppCompatActivity() {

    private val farmStoragePushHandler by inject<FarmStoragePushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        farmStorageSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_farm_storage)

        val farmStorageRootView = findViewById<View>(android.R.id.content)
        FarmStorageGlobalLayoutUtil().farmStorageAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(farmStorageRootView) { farmStorageView, farmStorageInsets ->
            val farmStorageSystemBars = farmStorageInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val farmStorageDisplayCutout = farmStorageInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val farmStorageIme = farmStorageInsets.getInsets(WindowInsetsCompat.Type.ime())


            val farmStorageTopPadding = maxOf(farmStorageSystemBars.top, farmStorageDisplayCutout.top)
            val farmStorageLeftPadding = maxOf(farmStorageSystemBars.left, farmStorageDisplayCutout.left)
            val farmStorageRightPadding = maxOf(farmStorageSystemBars.right, farmStorageDisplayCutout.right)
            window.setSoftInputMode(FarmStorageApplication.farmStorageInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "ADJUST PUN")
                val farmStorageBottomInset = maxOf(farmStorageSystemBars.bottom, farmStorageDisplayCutout.bottom)

                farmStorageView.setPadding(farmStorageLeftPadding, farmStorageTopPadding, farmStorageRightPadding, 0)

                farmStorageView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = farmStorageBottomInset
                }
            } else {
                Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "ADJUST RESIZE")

                val farmStorageBottomInset = maxOf(farmStorageSystemBars.bottom, farmStorageDisplayCutout.bottom, farmStorageIme.bottom)

                farmStorageView.setPadding(farmStorageLeftPadding, farmStorageTopPadding, farmStorageRightPadding, 0)

                farmStorageView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = farmStorageBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Activity onCreate()")
        farmStoragePushHandler.farmStorageHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            farmStorageSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        farmStorageSetupSystemBars()
    }
}