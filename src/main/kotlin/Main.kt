import dto.Input
import dto.OutputData
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlin.math.pow

fun Double.round(decimals: Int = 6): Double {
    val multiplier = 10.0.pow(decimals)
    return kotlin.math.round((this * multiplier)) / multiplier
}

fun main() {
    embeddedServer(Netty, port = 8082, module = Application::mainModule).start(wait = true)
}

fun Application.mainModule() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }
    routing {
        post("/") {
            val input = call.receive<Input>()
            val output : MutableList<OutputData> = mutableListOf()
            println(input)
            input.input.forEach {
                val network = Network(it)
                network.run()
                try {
                    println(it.learnEpoch)
                    output.add(if (it.learnEpoch > 0) {
                        val newWeights = network.backPropagation()

                        OutputData(
                            pvk = network.getResult(),
                            mistake = network.getMistakeMetric().round(4),
                            hiddenMatrix = newWeights.first,
                            outputMatrix = newWeights.second
                        )
                    } else {
                        OutputData(
                            pvk = network.getResult(),
                            mistake = 0.0
                        )
                    })

                } catch (e: Exception) {
                    println(e)
                    println(e.stackTraceToString())
                    call.respond("output")
                }
            }
            call.respond(output)
        }
    }
}