import kotlinx.coroutines.*

class RequestHandler(
    private val blueDevice: BluDevice,
    private val url: String
) {

    private val lock = Any()
    private val dequeLock = Any()
    private val listeners: ArrayDeque<(String) -> Unit> = ArrayDeque(3)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var asyncCall: Deferred<String>? = null
    private var notifyJob: Job? = null

    fun addListener(onResponse: (String) -> Unit) {
        synchronized(lock) {
            synchronized(dequeLock) {
                listeners.addLast(onResponse)
            }

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

    private suspend fun notifyListeners() {
        val result = asyncCall!!.await()
        synchronized(dequeLock) {
            while (listeners.isNotEmpty()) {
                val listener = listeners.removeFirst()
                listener.invoke(result)
            }
        }
    }
}