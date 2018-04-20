package cu4r

import cu4r.data.Row
import cu4r.function.manager.CSVManager
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.control.skin.TableColumnHeader
import javafx.stage.FileChooser
import javafx.util.Callback
import javafx.util.converter.DefaultStringConverter
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.*

class MainController : Initializable {
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        tableView.isEditable = true
        tableView.items.clear()
        defaultColumn.run { initializeEditableColumn(0) }
        tableView.columns.setAll(defaultColumn)
    }

    @FXML private lateinit var tableView: TableView<Row>
    @FXML private lateinit var defaultColumn: TableColumn<Row, String>

    private val csvOpener = FileChooser().apply {
        this.title = "Open"
        this.initialDirectory = File(".")
        this.extensionFilters.addAll(
                FileChooser.ExtensionFilter("CSV File", "*.csv")
        )
    }
    private val saveFileOpener = FileChooser().apply {
        this.title = "Save As"
        this.initialDirectory = File(".")
        this.extensionFilters.addAll(
                FileChooser.ExtensionFilter("CSV File", "*.csv")
        )
    }
    private val newNameDialog = { d: String -> TextInputDialog(d).apply { this.title = "Input New Name" } }
    private val headerChangeHandler = EventHandler<ActionEvent> { e ->
        val menuItem = e.source as? MenuItem ?: return@EventHandler
        val columnHeader = menuItem.parentPopup.ownerNode as? TableColumnHeader ?: return@EventHandler
        columnHeader.tableColumn.text = newNameDialog.invoke(columnHeader.tableColumn.text)
                .showAndWait().orElse(columnHeader.tableColumn.text)
    }
    private val columnAddHandler: (Int) -> EventHandler<ActionEvent> = { bias: Int ->
        EventHandler { e ->
            val columns = tableView.columns
            val menuItem = e.source as? MenuItem ?: return@EventHandler
            val columnHeader = menuItem.parentPopup.ownerNode as? TableColumnHeader ?: return@EventHandler
            val columnIndex = columns.indexOf(columnHeader.tableColumn) + bias
            tableView.items.forEach { it.elements.add(columnIndex, SimpleStringProperty()) }
            columns.add(TableColumn<Row, String>().apply {
                this.initializeEditableColumn(columns.size)
            })
            (0 until columns.size - columnIndex - 1).forEach {
                columns[columns.size - it - 1].text = columns[columns.size - it - 2].text
            }
            columns[columnIndex].text = newNameDialog.invoke("").showAndWait().orElse("")
        }
    }
    private val rowAddHandler = EventHandler<ActionEvent> {
        tableView.items.add(Row(*(0 until tableView.columns.size).map { "" }.toTypedArray()))
    }
    private val contextMenu = ContextMenu(
            MenuItem("Change Header").apply { onAction = headerChangeHandler },
            SeparatorMenuItem(),
            MenuItem("Add Column Left").apply { onAction = columnAddHandler.invoke(0) },
            MenuItem("Add Column Right").apply { onAction = columnAddHandler.invoke(1) },
            MenuItem("Add Row").apply { onAction = rowAddHandler }
    )

    @FXML
    fun onOpen() {
        val csv = csvOpener.showOpenDialog(tableView.scene.window) ?: return
        val hasHeader =
                Alert(Alert.AlertType.CONFIRMATION, "Does this file have a header line?", ButtonType.YES, ButtonType.NO)
                        .showAndWait().orElse(ButtonType.YES) == ButtonType.YES
        val lines = Files.readAllLines(csv.toPath())
        val numberOfColumns = lines[0].count { it == ',' } + 1
        if (!lines.all { it.count { it == ',' } == numberOfColumns - 1 }) {
            throw IllegalStateException("Number of columns must be the same as other lines.")
        }
        val rows = lines.map { Row(*it.split(",").toTypedArray()) }
        val columns = (0 until numberOfColumns).map {
            TableColumn<Row, String>(if (hasHeader) rows[0].elements[it].value else "$it").apply {
                this.initializeEditableColumn(it)
            }
        }
        tableView.items.setAll(rows.run { if (hasHeader) drop(1) else this })
        tableView.columns.setAll(columns)
    }

    @FXML
    fun onSaveAs() {
        val file = saveFileOpener.showSaveDialog(tableView.scene.window) ?: return
        val headerRow = Row(*tableView.columns.map { it.text }.toTypedArray())
        when (file.extension) {
            "csv" -> CSVManager()
            else -> throw UnsupportedOperationException("Unsupported extension.")
        }.save(file, listOf(headerRow) + tableView.items)
    }

    private fun TableColumn<Row, String>.initializeEditableColumn(column: Int) {
        this.contextMenu = this@MainController.contextMenu
        this.cellValueFactory = Callback { p -> p.value.elements[column] }
        this.cellFactory = Callback { TextFieldTableCell(DefaultStringConverter()) }
        this.onEditCommit = EventHandler { e ->
            e.tableView.items[e.tablePosition.row].elements[column].value = e.newValue
        }
    }
}
