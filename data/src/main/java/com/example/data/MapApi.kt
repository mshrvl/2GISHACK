package com.example.data

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MapApi {

    @GET("stimuli/")
    suspend fun getStimuly(): IrritantsResponse

    @PUT("stimuli/{id}")
    suspend fun setStatusStimuly(
        @Path("id") id: Int,
        @Query("is_active") isSelected: Boolean
    )

    @POST("route/")
    suspend fun createRoot(
        @Body body: CoordBody
    )
}


@Serializable
data class CoordBody(
    val startLat: Double = 55.695068,
    val start_lon: Double = 37.624836,
    val end_lat: Double = 55.708716,
    val end_lon: Double = 37.622594
)