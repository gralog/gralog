package gralog.preferences;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Every variable annotated with PreferenceVariable
 * can be overwritten by a preference file via Reflection.
 *
 * TODO: Reference which class makes use of this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PreferenceVariable {

}
