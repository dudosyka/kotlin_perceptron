package neuron

import neuron.activation.SigmaFunction

class OutputNeuron(input: List<Pair<Double, Double>>) : Neuron(inputNeuron = input) {
    override val func: Double.() -> Double = SigmaFunction()
}