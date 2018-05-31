package gralog.gralogfx.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:marcel.vogel@proemion.com">mv1015</a>
 *
 */
public class MultipleKeyCombination extends KeyCombination {

    private static List<KeyCode> codes = new ArrayList<>();

    public static void setupMultipleKeyCombination(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (!codes.contains(event.getCode())) {
                codes.add(event.getCode());
            }
        });
        scene.setOnKeyReleased((event) -> {
            codes.remove(event.getCode());
        });
    }

    private List<KeyCode> neededCodes;

    public MultipleKeyCombination (KeyCode... codes) {
        neededCodes = Arrays.asList(codes);
    }


    @Override
    public boolean match(KeyEvent event) {
        return codes.containsAll(neededCodes);
    }
}
