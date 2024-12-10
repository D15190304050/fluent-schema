package stark.coderaider.fluentschema.commons;

import junit.framework.TestCase;
import org.junit.Test;

public class NamingConverterTest extends TestCase
{
    public void testToRaw()
    {
        assertEquals("foo", NamingConverter.toRaw("foo"));
        assertEquals("Case", NamingConverter.toRaw("Case"));
    }

    public void testToPascalCase()
    {
        assertEquals("ThisIsPower", NamingConverter.toPascalCase("thisIsPower"));
        assertEquals("_thisIsPower", NamingConverter.toPascalCase("_thisIsPower"));
        assertEquals("ThisIs_Power", NamingConverter.toPascalCase("thisIs_Power"));
        assertEquals("ThisIs_Power", NamingConverter.toPascalCase("ThisIs_Power"));
        assertEquals("ThisIsPower", NamingConverter.toPascalCase("ThisIsPower"));
        assertEquals("Simple_table", NamingConverter.toPascalCase("simple_table"));
    }

    public void testToCamelCase()
    {
        assertEquals("thisIsPower", NamingConverter.toCamelCase("thisIsPower"));
        assertEquals("_thisIsPower", NamingConverter.toCamelCase("_thisIsPower"));
        assertEquals("thisIs_Power", NamingConverter.toCamelCase("thisIs_Power"));
        assertEquals("thisIs_Power", NamingConverter.toCamelCase("ThisIs_Power"));
        assertEquals("thisIsPower", NamingConverter.toCamelCase("ThisIsPower"));
        assertEquals("simple_table", NamingConverter.toCamelCase("simple_table"));
    }

    public void testToUpperCaseWithUnderscore()
    {
        assertEquals("This_Is_Power", NamingConverter.toUpperCaseWithUnderscore("thisIsPower"));
        assertEquals("_This_Is_Power", NamingConverter.toUpperCaseWithUnderscore("_thisIsPower"));
        assertEquals("This_Is_Power", NamingConverter.toUpperCaseWithUnderscore("thisIs_Power"));
        assertEquals("This_Is_Power", NamingConverter.toUpperCaseWithUnderscore("ThisIs_Power"));
        assertEquals("This_Is_Power", NamingConverter.toUpperCaseWithUnderscore("ThisIsPower"));
        assertEquals("Simple_Table", NamingConverter.toUpperCaseWithUnderscore("simple_table"));
    }

    public void testToLowerCaseWithUnderscore()
    {
        assertEquals("this_is_power", NamingConverter.toLowerCaseWithUnderscore("thisIsPower"));
        assertEquals("_this_is_power", NamingConverter.toLowerCaseWithUnderscore("_thisIsPower"));
        assertEquals("this_is_power", NamingConverter.toLowerCaseWithUnderscore("thisIs_Power"));
        assertEquals("this_is_power", NamingConverter.toLowerCaseWithUnderscore("ThisIs_Power"));
        assertEquals("this_is_power", NamingConverter.toLowerCaseWithUnderscore("ThisIsPower"));
        assertEquals("simple_table", NamingConverter.toLowerCaseWithUnderscore("simple_table"));
    }
}