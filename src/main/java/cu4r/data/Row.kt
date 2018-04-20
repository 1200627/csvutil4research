package cu4r.data

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class Row(
        vararg elements: String
) {
    val elements: MutableList<StringProperty> = elements.map { SimpleStringProperty(it) }.toMutableList()
}