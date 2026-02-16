package com.farmerinven.apsola.eiowjf.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.farmerinven.apsola.eiowjf.presentation.app.FarmStorageApplication
import com.farmerinven.apsola.eiowjf.presentation.ui.load.FarmStorageLoadFragment
import org.koin.android.ext.android.inject

class FarmStorageV : Fragment(){

    private lateinit var farmStoragePhoto: Uri
    private var farmStorageFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val farmStorageTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        farmStorageFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        farmStorageFilePathFromChrome = null
    }

    private val farmStorageTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            farmStorageFilePathFromChrome?.onReceiveValue(arrayOf(farmStoragePhoto))
            farmStorageFilePathFromChrome = null
        } else {
            farmStorageFilePathFromChrome?.onReceiveValue(null)
            farmStorageFilePathFromChrome = null
        }
    }

    private val farmStorageDataStore by activityViewModels<FarmStorageDataStore>()


    private val farmStorageViFun by inject<FarmStorageViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (farmStorageDataStore.farmStorageView.canGoBack()) {
                        farmStorageDataStore.farmStorageView.goBack()
                        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "WebView can go back")
                    } else if (farmStorageDataStore.farmStorageViList.size > 1) {
                        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "WebView can`t go back")
                        farmStorageDataStore.farmStorageViList.removeAt(farmStorageDataStore.farmStorageViList.lastIndex)
                        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "WebView list size ${farmStorageDataStore.farmStorageViList.size}")
                        farmStorageDataStore.farmStorageView.destroy()
                        val previousWebView = farmStorageDataStore.farmStorageViList.last()
                        farmStorageAttachWebViewToContainer(previousWebView)
                        farmStorageDataStore.farmStorageView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (farmStorageDataStore.farmStorageIsFirstCreate) {
            farmStorageDataStore.farmStorageIsFirstCreate = false
            farmStorageDataStore.farmStorageContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return farmStorageDataStore.farmStorageContainerView
        } else {
            return farmStorageDataStore.farmStorageContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "onViewCreated")
        if (farmStorageDataStore.farmStorageViList.isEmpty()) {
            farmStorageDataStore.farmStorageView = FarmStorageVi(requireContext(), object :
                FarmStorageCallBack {
                override fun farmStorageHandleCreateWebWindowRequest(farmStorageVi: FarmStorageVi) {
                    farmStorageDataStore.farmStorageViList.add(farmStorageVi)
                    Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "WebView list size = ${farmStorageDataStore.farmStorageViList.size}")
                    Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "CreateWebWindowRequest")
                    farmStorageDataStore.farmStorageView = farmStorageVi
                    farmStorageVi.farmStorageSetFileChooserHandler { callback ->
                        farmStorageHandleFileChooser(callback)
                    }
                    farmStorageAttachWebViewToContainer(farmStorageVi)
                }

            }, farmStorageWindow = requireActivity().window).apply {
                farmStorageSetFileChooserHandler { callback ->
                    farmStorageHandleFileChooser(callback)
                }
            }
            farmStorageDataStore.farmStorageView.farmStorageFLoad(arguments?.getString(
                FarmStorageLoadFragment.FARM_STORAGE_D) ?: "")
//            ejvview.fLoad("www.google.com")
            farmStorageDataStore.farmStorageViList.add(farmStorageDataStore.farmStorageView)
            farmStorageAttachWebViewToContainer(farmStorageDataStore.farmStorageView)
        } else {
            farmStorageDataStore.farmStorageViList.forEach { webView ->
                webView.farmStorageSetFileChooserHandler { callback ->
                    farmStorageHandleFileChooser(callback)
                }
            }
            farmStorageDataStore.farmStorageView = farmStorageDataStore.farmStorageViList.last()

            farmStorageAttachWebViewToContainer(farmStorageDataStore.farmStorageView)
        }
        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "WebView list size = ${farmStorageDataStore.farmStorageViList.size}")
    }

    private fun farmStorageHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        farmStorageFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Launching file picker")
                    farmStorageTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "Launching camera")
                    farmStoragePhoto = farmStorageViFun.farmStorageSavePhoto()
                    farmStorageTakePhoto.launch(farmStoragePhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(FarmStorageApplication.FARM_STORAGE_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                farmStorageFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun farmStorageAttachWebViewToContainer(w: FarmStorageVi) {
        farmStorageDataStore.farmStorageContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            farmStorageDataStore.farmStorageContainerView.removeAllViews()
            farmStorageDataStore.farmStorageContainerView.addView(w)
        }
    }


}