package gralog.preferences;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every variable annotated with MenuPrefVariable
 * will be accessible for modification via a drop down
 * menu from the top bar.
 *
 * TODO: Reference which class makes use of this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MenuPrefVariable {

    /**
     * The name that should be displayed in the menu panel
     */
    String name() default "";
}
