package cu4r.data

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

class Row(
        vararg elements: String
) {
    val elements: MutableList<StringProperty> = elements.map {
        val element = it.replace(Regex("""^ """), "")
        SimpleStringProperty(element)
    }.toMutableList()
}