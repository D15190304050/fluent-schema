package stark.coderaider.fluentschema.annotations;

public @interface AutoIncrement
{
    int begin() default 1;
    int increment() default 1;
}
