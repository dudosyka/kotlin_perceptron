package neuron

import neuron.activation.LinearFunction

class InputNeuron(input: List<Pair<Double, Double>>) : Neuron(input) {
    override val func: Double.() -> Double = LinearFunction()
}