package com.farmerinven.apsola.eiowjf.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class FarmStorageDataStore : ViewModel(){
    val farmStorageViList: MutableList<FarmStorageVi> = mutableListOf()
    var farmStorageIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var farmStorageContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var farmStorageView: FarmStorageVi

}