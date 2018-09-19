package gralog.gralogfx.windows;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;

public class PreferenceWindowTest {

    // TODO: The following test is disabled because it tries to open a dialog.
    // For continuous integration (Travis), the tests need to run fully
    // automatic.
    /*
    @Test
    public void RunPreferenceTest(){
        Application.launch(DummyStage.class);
    }
    */

    public static class DummyStage extends Application{

        public DummyStage() { } // required

        @Override
        public void start(Stage primaryStage){
            PreferenceWindow p = new PreferenceWindow();
        }
    }
}
