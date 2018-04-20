package cu4r.function.manager

import cu4r.data.Row
import java.io.File
import java.nio.file.Files

interface FileManager {
    fun save(file: File, items: List<Row>)
}