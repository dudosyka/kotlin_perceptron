package neuron.activation

import round
import kotlin.math.tanh

object TanhFunction: ActivationFunction() {
    override operator fun invoke(): Double.() -> Double = {
        tanh(this).round()
    }
}