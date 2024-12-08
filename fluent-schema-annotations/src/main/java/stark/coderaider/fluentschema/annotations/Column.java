package stark.coderaider.fluentschema.annotations;

public @interface Column
{
    String name() default "";
    String type() default "";
    boolean nullable() default true;
    String comment() default "";
    String defaultValue() default "";
    String onUpdate() default "";
}
