package com.example.mapbox.ui.viewModel.sim

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mapbox.repository.OpenCellRepository
import dev.shreyaspatil.celllocationfinder.service.buildUnwiredLabsService
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CellLocationViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CellLocationViewModel::class.java)) {
            return CellLocationViewModel(
                OpenCellRepository(buildUnwiredLabsService())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
