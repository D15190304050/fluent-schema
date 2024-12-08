package stark.coderaider.fluentschema.annotations;

public @interface Table
{
    String name();
    String engine() default "InnoDB";
    String comment() default "";
}
