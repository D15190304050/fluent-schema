package stark.coderaider.fluentschema.commons.annotations;

import java.lang.annotation.*;

/**
 * Constraints:
 * <ol>
 *     <li>Only for integer types.</li>
 *     <li>Only 1 AUTO_INCREMENT column in 1 table.</li>
 *     <li>Only for PRIMARY KEY or UNIQUE column.</li>
 * </ol>
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface AutoIncrement
{
    int begin() default 1;
    int increment() default 1;
}
