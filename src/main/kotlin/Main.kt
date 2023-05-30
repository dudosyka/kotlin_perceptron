import dto.InputData
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
import java.math.BigDecimal
import kotlin.math.pow

fun Double.round(decimals: Int = 6): Double {
    var multiplier = 10.0.pow(decimals)
    return kotlin.math.round((this * multiplier)) / multiplier
}

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, module = Application::mainModule).start(wait = true)
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
            val input = call.receive<InputData>()
            println(input)
            val network = Network(input)
            network.run()
            try {
                val newWeights = network.backPropagation()

                val output = OutputData(
                    pvk = network.getResult(),
                    mistake = network.getMistakeMetric().round(4),
                    hiddenMatrix = newWeights.first,
                    outputMatrix = newWeights.second
                )
                call.respond(output)
            } catch (e: Exception) {
                println(e)
                println(e.stackTraceToString())
                call.respond("output")
            }
        }
    }
}