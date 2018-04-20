package cu4r

import cu4r.data.Row
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableView
import java.net.URL
import java.util.*

class MainController : Initializable {
    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    @FXML private lateinit var tableView: TableView<Row>
}
