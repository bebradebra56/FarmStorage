package com.farmerinven.apsola.eiowjf.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.farmerinven.apsola.MainActivity
import com.farmerinven.apsola.R
import com.farmerinven.apsola.databinding.FragmentLoadFarmStorageBinding
import com.farmerinven.apsola.eiowjf.data.shar.FarmStorageSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FarmStorageLoadFragment : Fragment(R.layout.fragment_load_farm_storage) {
    private lateinit var farmStorageLoadBinding: FragmentLoadFarmStorageBinding

    private val farmStorageLoadViewModel by viewModel<FarmStorageLoadViewModel>()

    private val farmStorageSharedPreference by inject<FarmStorageSharedPreference>()

    private var farmStorageUrl = ""

    private val farmStorageRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        farmStorageSharedPreference.farmStorageNotificationState = 2
        farmStorageNavigateToSuccess(farmStorageUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        farmStorageLoadBinding = FragmentLoadFarmStorageBinding.bind(view)

        farmStorageLoadBinding.farmStorageGrandButton.setOnClickListener {
            val farmStoragePermission = Manifest.permission.POST_NOTIFICATIONS
            farmStorageRequestNotificationPermission.launch(farmStoragePermission)
        }

        farmStorageLoadBinding.farmStorageSkipButton.setOnClickListener {
            farmStorageSharedPreference.farmStorageNotificationState = 1
            farmStorageSharedPreference.farmStorageNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            farmStorageNavigateToSuccess(farmStorageUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                farmStorageLoadViewModel.farmStorageHomeScreenState.collect {
                    when (it) {
                        is FarmStorageLoadViewModel.FarmStorageHomeScreenState.FarmStorageLoading -> {

                        }

                        is FarmStorageLoadViewModel.FarmStorageHomeScreenState.FarmStorageError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is FarmStorageLoadViewModel.FarmStorageHomeScreenState.FarmStorageSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val farmStorageNotificationState = farmStorageSharedPreference.farmStorageNotificationState
                                when (farmStorageNotificationState) {
                                    0 -> {
                                        farmStorageLoadBinding.farmStorageNotiGroup.visibility = View.VISIBLE
                                        farmStorageLoadBinding.farmStorageLoadingGroup.visibility = View.GONE
                                        farmStorageUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > farmStorageSharedPreference.farmStorageNotificationRequest) {
                                            farmStorageLoadBinding.farmStorageNotiGroup.visibility = View.VISIBLE
                                            farmStorageLoadBinding.farmStorageLoadingGroup.visibility = View.GONE
                                            farmStorageUrl = it.data
                                        } else {
                                            farmStorageNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        farmStorageNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                farmStorageNavigateToSuccess(it.data)
                            }
                        }

                        FarmStorageLoadViewModel.FarmStorageHomeScreenState.FarmStorageNotInternet -> {
                            farmStorageLoadBinding.farmStorageStateGroup.visibility = View.VISIBLE
                            farmStorageLoadBinding.farmStorageLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun farmStorageNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_farmStorageLoadFragment_to_farmStorageV,
            bundleOf(FARM_STORAGE_D to data)
        )
    }

    companion object {
        const val FARM_STORAGE_D = "farmStorageData"
    }
}