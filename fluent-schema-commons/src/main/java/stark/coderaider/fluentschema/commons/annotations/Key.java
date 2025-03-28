package stark.coderaider.fluentschema.commons.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
@Repeatable(Keys.class)
public @interface Key
{
    String name();
    int order() default 0;
}
