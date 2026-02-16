package com.farmerinven.apsola.eiowjf

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication

class FarmStorageGlobalLayoutUtil {

    private var farmStorageMChildOfContent: View? = null
    private var farmStorageUsableHeightPrevious = 0

    fun farmStorageAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        farmStorageMChildOfContent = content.getChildAt(0)

        farmStorageMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val farmStorageUsableHeightNow = farmStorageComputeUsableHeight()
        if (farmStorageUsableHeightNow != farmStorageUsableHeightPrevious) {
            val farmStorageUsableHeightSansKeyboard = farmStorageMChildOfContent?.rootView?.height ?: 0
            val farmStorageHeightDifference = farmStorageUsableHeightSansKeyboard - farmStorageUsableHeightNow

            if (farmStorageHeightDifference > (farmStorageUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(FarmStorageApplication.farmStorageInputMode)
            } else {
                activity.window.setSoftInputMode(FarmStorageApplication.farmStorageInputMode)
            }
//            mChildOfContent?.requestLayout()
            farmStorageUsableHeightPrevious = farmStorageUsableHeightNow
        }
    }

    private fun farmStorageComputeUsableHeight(): Int {
        val r = Rect()
        farmStorageMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}