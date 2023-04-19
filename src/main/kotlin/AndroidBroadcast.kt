import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/*
 * An example of resolving task from job interview from AndroidBroadcast channel
 * https://www.youtube.com/watch?v=ZqLyxt3XTZQ
 *
 * Task description:
 *
 * 1) There is a BluDevice interface:
 * interface BluDevice {
 *     fun requestSync(url: String): String
 * }
 * of an object that returns some response.
 * It takes some time to complete a requestSync(String) call.
 *
 * 2) You should implement CommandExecutor interface:
 * interface CommandExecutor {
 *   fun requestAsync(url: String, onResponse: (String) -> Unit)
 * }
 * The implementation of this interface can be called from different parts of the application.
 *
 * 3) The implementation should avoid multiple "BluDevice.requestSync(String)"
 * calls for the same URL.
 *
 * 4) The implementation should call the onResponse() method only once,
 * and only when the result from BluDevice is ready.
 */
fun main() {
    val bluDevice: BluDevice = BlueDeviceImpl()

    val commandExecutor = CommandExecutorImpl(bluDevice)
    val url = "https://ya.ru"

    val startTime = System.currentTimeMillis()
    runBlocking {
        for (i in 0..20) {
            commandExecutor.requestAsync(url) { response ->
                println("[$i] response: $response, delay: ${System.currentTimeMillis() - startTime}")
            }
            println("[$i] requestAsync, delay: ${System.currentTimeMillis() - startTime}")
            delay(100L)
        }
    }
}

