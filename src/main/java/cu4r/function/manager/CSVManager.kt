package cu4r.function.manager

import cu4r.data.Row
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class CSVManager : FileManager {
    override fun save(file: File, items: List<Row>) {
        val path = file.toPath()
        val text = items.joinToString("\r\n") { it.elements.joinToString(",") { it.value } }
        Files.createDirectories(path.parent)
        Files.write(path, text.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }
}