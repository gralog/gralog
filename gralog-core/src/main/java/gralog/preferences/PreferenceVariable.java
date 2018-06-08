package gralog.preferences;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Every variable annotated with PreferenceVariable
 * can be overwritten by a preference file via Reflection.
 *
 * TODO: Reference which class makes use of this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PreferenceVariable {

}
