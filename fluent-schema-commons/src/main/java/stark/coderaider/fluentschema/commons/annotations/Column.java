package stark.coderaider.fluentschema.commons.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Column
{
    String name() default "";
    String type() default "";
    boolean nullable() default true;
    boolean unique() default false;
    String comment() default "";
    String defaultValue() default "";
    String onUpdate() default "";
}
