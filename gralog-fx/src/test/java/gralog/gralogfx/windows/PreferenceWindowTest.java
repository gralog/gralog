package gralog.gralogfx.windows;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;

public class PreferenceWindowTest {

    @Test
    public void RunPreferenceTest(){
        Application.launch(DummyStage.class);
    }

    public static class DummyStage extends Application{

        public DummyStage() { } // required

        @Override
        public void start(Stage primaryStage){
            PreferenceWindow p = new PreferenceWindow();
        }
    }
}
