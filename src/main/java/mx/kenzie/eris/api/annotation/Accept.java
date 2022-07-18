package mx.kenzie.eris.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.LOCAL_VARIABLE, ElementType.TYPE_PARAMETER})
public @interface Accept {
    
    Class<?>[] value() default {};
    
}
