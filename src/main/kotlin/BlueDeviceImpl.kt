class BlueDeviceImpl: BluDevice {
    override fun requestSync(url: String): String {
        println("BluDevice. request url : $url")
        Thread.sleep(1000L)
        println("BluDevice. finally, we have a response for url: $url")
        return "$url - response"
    }
}