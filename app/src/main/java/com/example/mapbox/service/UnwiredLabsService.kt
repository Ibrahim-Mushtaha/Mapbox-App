package com.example.mapbox.service

import com.example.mapbox.model.request.CellInfo
import com.example.mapbox.model.response.CellLocation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UnwiredLabsService {

    @POST("v2/process.php")
    suspend fun getLocationByCellInfo(@Body cellInfo: CellInfo): Response<CellLocation>

    companion object {
        const val BASE_URL = "https://ap1.unwiredlabs.com/"
    }
}