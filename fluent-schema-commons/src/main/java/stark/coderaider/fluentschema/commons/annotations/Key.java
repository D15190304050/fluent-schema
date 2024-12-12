package stark.coderaider.fluentschema.commons.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Key
{
    String name();
    int order() default 0;
}
