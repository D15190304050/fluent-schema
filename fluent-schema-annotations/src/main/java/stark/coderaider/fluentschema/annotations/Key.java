package stark.coderaider.fluentschema.annotations;

public @interface Key
{
    String name();
    int order() default 0;
}
