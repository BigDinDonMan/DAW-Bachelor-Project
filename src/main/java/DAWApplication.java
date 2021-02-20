import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public class DAWApplication extends javafx.application.Application {

    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = loader.load();
        MainWindowController controller = loader.getController();
        Scene s = new Scene(root);
        s.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet(), getClass().getResource("styles/app-style.css").toExternalForm());
        stage.setScene(s);
        controller.setMainStage(stage);
        stage.setTitle("Digital Audio Workstation");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
