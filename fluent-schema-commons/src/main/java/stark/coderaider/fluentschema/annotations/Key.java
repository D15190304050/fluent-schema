package stark.coderaider.fluentschema.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Key
{
    String name();
    int order() default 0;
}
