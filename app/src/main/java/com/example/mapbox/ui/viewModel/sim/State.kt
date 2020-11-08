package com.example.mapbox.ui.viewModel.sim

import com.example.mapbox.model.response.CellLocation

sealed class State {
    object Loading : State()
    data class Success(val response: CellLocation) : State()
    data class Failed(val message: String) : State()
}
