fun nativeImageExecutableName(): String = if (isWindows()) "native-image.cmd" else "native-image"

fun javaExecutableName(): String = if (isWindows()) "java.exe" else "java"

fun isWindows(): Boolean =
    System.getProperty("os.name", "unknown").contains("Windows", ignoreCase = true)
