interface CommandExecutor {
    fun requestAsync(url: String, onResponse: (String) -> Unit)
}