package neuron.activation

abstract class ActivationFunction {
    abstract fun invoke(): Double.() -> Double
}