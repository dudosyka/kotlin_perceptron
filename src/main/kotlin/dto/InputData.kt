package dto

import kotlinx.serialization.Serializable

@Serializable
data class InputData (
    val input: List<Double>,
    val hiddenMatrix: List<List<Double>>,
    val outputMatrix: List<List<Double>>,
    val answer: List<Double>,
    val learnEpoch: Int
)

@Serializable
data class Input (
    val input: List<InputData>
)