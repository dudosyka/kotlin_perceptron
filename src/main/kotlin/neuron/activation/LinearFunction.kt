package neuron.activation

object LinearFunction: ActivationFunction() {
    override operator fun invoke(): Double.() -> Double = {
        this
    }
}