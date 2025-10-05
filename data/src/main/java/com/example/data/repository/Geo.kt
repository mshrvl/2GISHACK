package com.example.data.repository

@kotlinx.serialization.Serializable
data class GeoJSONResponse(
    val type: String,
    val features: List<Feature>
)

@kotlinx.serialization.Serializable
data class Feature(
    val type: String,
    val geometry: Geometry
)

@kotlinx.serialization.Serializable
data class Geometry(
    val type: String,
    val coordinates: List<List<Double>>
)