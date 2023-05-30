package dto

import kotlinx.serialization.Serializable

@Serializable
data class OutputData (
    val pvk: List<Double>,
    val mistake: Double,
    val hiddenMatrix: List<List<Double>>? = null,
    val outputMatrix: List<List<Double>>? = null
)