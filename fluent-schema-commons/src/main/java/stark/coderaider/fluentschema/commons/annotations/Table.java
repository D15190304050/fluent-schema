package stark.coderaider.fluentschema.commons.annotations;

import stark.coderaider.fluentschema.commons.NamingConvention;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface Table
{
    String name() default "";
    NamingConvention namingConvention() default NamingConvention.RAW;
    String engine() default "InnoDB";
    String comment() default "";
}
