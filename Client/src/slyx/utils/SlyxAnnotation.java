package slyx.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Antoine Janvier
 * on 07/09/17.
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SlyxAnnotation {
    String todo() default "";
    public enum Type {DEFAULT, COMMUNICATION, CSS, DESIGN, SCENE, EXCEPTION, SOUND, VALIDATOR, CLASS};
    Type type() default Type.DEFAULT;
}
