/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.preferences;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every variable annotated with MenuPrefVariable
 * will be accessible for modification via a drop down
 * menu from the top bar.
 * <p>
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
