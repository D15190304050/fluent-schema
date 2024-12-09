package stark.coderaider.fluentschema.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Column
{
    String name() default "";
    String type() default "";
    boolean nullable() default true;
    String comment() default "";
    String defaultValue() default "";
    String onUpdate() default "";
}
