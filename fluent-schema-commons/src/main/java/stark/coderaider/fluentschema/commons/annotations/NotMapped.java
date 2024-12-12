package stark.coderaider.fluentschema.commons.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface NotMapped
{
}
