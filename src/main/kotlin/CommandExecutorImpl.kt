import java.util.concurrent.ConcurrentHashMap

class CommandExecutorImpl(private val bluDevice: BluDevice): CommandExecutor {

    private val urlRequestHandlerMap = ConcurrentHashMap<String, RequestHandler>()

    override fun requestAsync(url: String, onResponse: (String) -> Unit) {
        val requestHandler = urlRequestHandlerMap.computeIfAbsent(url) {
            RequestHandler(bluDevice, url)
        }
        requestHandler.addListener(onResponse)
    }
}