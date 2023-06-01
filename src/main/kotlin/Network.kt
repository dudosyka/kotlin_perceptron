import dto.InputData
import neuron.HiddenNeuron
import neuron.InputNeuron
import neuron.Neuron
import neuron.OutputNeuron
import kotlin.math.pow

class Network(
    private val inputData: InputData
) {
    private val n = 0.7
    private val alpha = 0.3
    private var input: MutableList<InputNeuron> = mutableListOf()
    private val hidden: MutableList<HiddenNeuron> = mutableListOf()
    private val calculationLayers: MutableList<MutableList<HiddenNeuron>> = mutableListOf()
    private val output: MutableList<OutputNeuron> = mutableListOf()

    fun run() {
        inputData.input.forEach {
            input.add(InputNeuron(listOf(Pair(it, 1.0))))
        }
        input.forEach {
            println(it)
        }


        createLayer(input, inputData.hiddenMatrix).forEach {
            hidden.add(HiddenNeuron(it))
        }
        hidden.forEach {
            println(it)
        }

        inputData.calculationLayers.forEachIndexed {
            index, value -> run {
                calculationLayers.add(mutableListOf())
                createLayer(hidden, value).forEach {
                    calculationLayers[index].add(HiddenNeuron(it))
                }
            }
        }
        calculationLayers.forEach { layer ->
            println("Calculation layers:")
            layer.forEach {
                println(it)
            }
        }
        println()


        val calculateBy = if (calculationLayers.size > 0) calculationLayers.last() else hidden


        createLayer(calculateBy, inputData.outputMatrix).forEach {
            output.add(OutputNeuron(it))
        }
        output.forEach {
            println(it)
        }
    }

    private fun createLayer(prevLayer: List<Neuron>, matrix: List<List<Double>>): List<MutableList<Pair<Double, Double>>> {
        return matrix.map {
            val output = it.mapIndexed {
                index, item -> run {
                    return@run Pair(prevLayer[index].activate(), item)
                }
            }.toMutableList()
            return@map output
        }
    }

    fun getResult(): List<Double> {
        return output.map { it.activate() }
    }

    fun getMistakeMetric(): Double {
        var sum = 0.0
        output.forEachIndexed {
            i, item -> run {
                sum += (item.activate() - inputData.answer[i]).pow(2)
            }
        }
        return sum.round(5)
    }

    private fun calculateNewWeight(neuron: Neuron, oldWeight: Double, oldDelta: Double): Pair<Double, Double> {
        val p = neuron.q * neuron.activate()
        val deltaWeight = n * p + alpha * oldDelta
        return Pair(oldWeight + deltaWeight, deltaWeight)
    }

    fun backPropagation(): Pair<List<List<Double>>, List<List<Double>>> {
        var hiddenMatrix = inputData.hiddenMatrix
        val deltasHidden = hiddenMatrix.map {
            it.map { 0.0 }.toMutableList()
        }
        var calculation = inputData.calculationLayers
        val deltasCalculation = calculation.map { layer ->
            layer.map {
                it.map { 0.0 }.toMutableList()
            }
        }
        var outputMatrix = inputData.outputMatrix
        val deltasOutput = outputMatrix.map {
            it.map { 0.0 }.toMutableList()
        }
        for (i in 1..inputData.learnEpoch) {
            println("Start epoch $i")
            println("Current mistake: ${getMistakeMetric()}")

            hidden.clear()
            createLayer(input, hiddenMatrix).forEach {
                hidden.add(HiddenNeuron(it))
            }

            calculationLayers.clear()
            calculation.forEachIndexed {
                index, value -> run {
                    calculationLayers.add(mutableListOf())
                    createLayer(hidden, value).forEach {
                        calculationLayers[index].add(HiddenNeuron(it))
                    }
                }
            }

            var calculateBy: MutableList<out Neuron> = if (calculationLayers.size > 0) calculationLayers.last() else hidden

            output.clear()
            createLayer(calculateBy, outputMatrix).forEach {
                output.add(OutputNeuron(it))
            }

            outputMatrix = output.mapIndexed {
                index, item -> run {
                    item.q = (inputData.answer[index] - item.activate()) * item.activate() * (1 - item.activate())
                    return@run item.inputNeuron.mapIndexed {
                        indexIn, it -> run {
                            val newWeight = calculateNewWeight(item, it.second, deltasOutput[index][indexIn])
                            deltasOutput[index][indexIn] = newWeight.second
                            newWeight.first.round()
                        }
                    }
                }
            }
            calculation = calculationLayers.asReversed().mapIndexed {
                layerIndex, layer ->
                    layer.mapIndexed {
                        index, item -> run {
                            val sumOfChildren = output.sumOf {
                                it.inputNeuron[index].second * it.q
                            }
                            item.q = item.activate() * (1 - item.activate()) * sumOfChildren
                            return@run item.inputNeuron.mapIndexed {
                                indexIn, it -> run {
                                    val newWeight = calculateNewWeight(item, it.second, deltasCalculation[layerIndex][index][indexIn])
                                    deltasCalculation[layerIndex][index][indexIn] = newWeight.second
                                    newWeight.first.round()
                                }
                            }
                        }
                    }
            }

            calculateBy = if (calculationLayers.size > 0) calculationLayers.last() else output

            hiddenMatrix = hidden.mapIndexed {
                index, item -> run {
                    val sumOfChildren = calculateBy.sumOf {
                        it.inputNeuron[index].second * it.q
                    }
                    item.q = item.activate() * (1 - item.activate()) * sumOfChildren
                    return@run item.inputNeuron.mapIndexed {
                        indexIn, it -> run {
                            val newWeight = calculateNewWeight(item, it.second, deltasHidden[index][indexIn])
                            deltasHidden[index][indexIn] = newWeight.second
                            newWeight.first.round()
                        }
                    }
                }
            }
        }

        return Pair(hiddenMatrix, outputMatrix)
    }
}