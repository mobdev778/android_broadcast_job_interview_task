import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedDeque

class RequestHandler(
    private val blueDevice: BluDevice,
    private val url: String
) {

    private val lock = Any()
    private val listeners: ConcurrentLinkedDeque<(String) -> Unit> = ConcurrentLinkedDeque()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var asyncCall: Deferred<String>? = null
    private var notifyJob: Job? = null

    private var onComplete: ((String) -> Unit)? = null

    fun addListener(onResponse: (String) -> Unit) {
        synchronized(lock) {
            listeners.addLast(onResponse)

            if (asyncCall == null) {
                asyncCall = coroutineScope.async {
                    blueDevice.requestSync(url)
                }
            }

            if (notifyJob?.isActive != true) {
                notifyJob = coroutineScope.launch {
                    notifyListeners()
                }
            }
        }
    }

    fun setCompleteListener(onComplete: (String) -> Unit) {
        this.onComplete = onComplete
    }

    private suspend fun notifyListeners() {
        val result = asyncCall!!.await()
        while (listeners.isNotEmpty()) {
            val listener = listeners.removeFirst()
            listener.invoke(result)
        }
        asyncCall = null
        this.onComplete?.invoke(url)
    }
}