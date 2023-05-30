package neuron.activation

import round
import kotlin.math.E
import kotlin.math.exp

object SigmaFunction: ActivationFunction() {
    override operator fun invoke(): Double.() -> Double = {
        (1 / (1 + exp(-this))).round()
    }
}