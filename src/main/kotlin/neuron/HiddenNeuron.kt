package neuron

import neuron.activation.SigmaFunction

class HiddenNeuron(input: List<Pair<Double, Double>>): Neuron(input)  {
    override val func: Double.() -> Double = SigmaFunction()
}