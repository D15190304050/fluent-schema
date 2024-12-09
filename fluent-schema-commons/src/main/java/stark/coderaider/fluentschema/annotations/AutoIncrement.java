package stark.coderaider.fluentschema.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface AutoIncrement
{
    int begin() default 1;
    int increment() default 1;
}
