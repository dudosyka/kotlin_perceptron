import dto.InputData
import neuron.HiddenNeuron
import neuron.InputNeuron
import neuron.Neuron
import neuron.OutputNeuron
import kotlin.math.pow

class Network(
    private val inputData: InputData
) {
    private val n = 0.1
    private val alpha = 0.8
    private var input: MutableList<InputNeuron> = mutableListOf()
    private val hidden: MutableList<HiddenNeuron> = mutableListOf()
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


        createLayer(hidden, inputData.outputMatrix).forEach {
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
        return sum.round(4)
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

            output.clear()
            createLayer(hidden, outputMatrix).forEach {
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
            hiddenMatrix = hidden.mapIndexed {
                index, item -> run {
                    val sumOfChildren = output.sumOf {
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