package stark.coderaider.fluentschema.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface Table
{
    String name();
    String engine() default "InnoDB";
    String comment() default "";
}
