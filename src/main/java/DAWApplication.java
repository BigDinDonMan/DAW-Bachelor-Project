import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DAWApplication extends javafx.application.Application {

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        MainWindowController controller = loader.getController();
        controller.setMainStage(stage);
        Scene s = new Scene(root);
        stage.setScene(s);
        stage.setTitle("Digital Audio Workstation");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
