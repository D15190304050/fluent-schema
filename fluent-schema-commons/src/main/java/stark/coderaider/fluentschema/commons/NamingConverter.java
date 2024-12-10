package stark.coderaider.fluentschema.commons;

import java.util.ArrayList;
import java.util.List;

public final class NamingConverter
{
    private NamingConverter()
    {
    }

    public static String toClassLikeName(String name)
    {
        String classLikeName = "";

        if (name != null)
        {
            List<Character> charsToKeep = getCharsToKeep(name);
            classLikeName = convertToString(charsToKeep);
            classLikeName = Character.toUpperCase(classLikeName.charAt(0)) + classLikeName.substring(1);
        }

        return classLikeName;
    }

    private static String convertToString(List<Character> charsToKeep)
    {
        String classLikeName;
        char[] charsToKeepArray = new char[charsToKeep.size()];
        for (int i = 0; i < charsToKeepArray.length; i++)
            charsToKeepArray[i] = charsToKeep.get(i);
        classLikeName = new String(charsToKeepArray);
        return classLikeName;
    }

    private static List<Character> getCharsToKeep(String objectName)
    {
        List<Character> charsToKeep = new ArrayList<>();
        for (char c : objectName.trim().toCharArray())
        {
            if (isAcceptableChar(c))
                charsToKeep.add(c);
        }
        return charsToKeep;
    }

    public static boolean isAcceptableChar(char c)
    {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    public static String applyConvention(String name, NamingConvention convention)
    {
        return switch (convention)
        {
            case RAW -> name;
            case LOWER_CASE_WITH_UNDERSCORE -> toLowerCaseWithUnderscore(name);
            case UPPER_CASE_WITH_UNDERSCORE -> toUpperCaseWithUnderscore(name);
            case CAMEL_CASE -> toCamelCase(name);
            case PASCAL_CASE -> toPascalCase(name);
        };
    }

    public static void validateCharAcceptance(String name)
    {
        for (char c : name.toCharArray())
        {
            if (!isAcceptableChar(c))
                throw new IllegalArgumentException("Name contains illegal character: " + c);
        }
    }

    public static String toRaw(String name)
    {
        validateCharAcceptance(name);
        return name;
    }

    public static String toPascalCase(String name)
    {
        validateCharAcceptance(name);
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public static String toCamelCase(String name) throws IllegalArgumentException
    {
        validateCharAcceptance(name);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public static String toUpperCaseWithUnderscore(String name)
    {
        StringBuilder result = new StringBuilder();

        boolean isFirstChar = true;
        for (char c : name.toCharArray())
        {
            if (!isAcceptableChar(c))
                throw new IllegalArgumentException("Name contains illegal character: " + c);

            char lastChar = result.isEmpty() ? '\0' : result.charAt(result.length() - 1);
            boolean lastCharIsUnderscore = lastChar == '_';

            if (lastCharIsUnderscore)
                result.append(Character.toUpperCase(c));
            else if (Character.isUpperCase(c) && !isFirstChar)
            {
                result.append("_").append(c);
            }
            else
                result.append(c);

            isFirstChar = false;
        }

        return Character.toUpperCase(result.toString().charAt(0)) + result.substring(1);
    }

    public static String toLowerCaseWithUnderscore(String name)
    {
        StringBuilder result = new StringBuilder();

        boolean isFirstChar = true;
        for (char c : name.toCharArray())
        {
            if (!isAcceptableChar(c))
                throw new IllegalArgumentException("Name contains illegal character: " + c);

            char lastChar = result.isEmpty() ? '\0' : result.charAt(result.length() - 1);
            boolean lastCharIsUnderscore = lastChar == '_';

            if (lastCharIsUnderscore)
                result.append(Character.toLowerCase(c));
            else if (Character.isUpperCase(c) && !isFirstChar)
            {
                result.append("_").append(Character.toLowerCase(c));
            }
            else
                result.append(c);

            isFirstChar = false;
        }

        return Character.toLowerCase(result.toString().charAt(0)) + result.substring(1);
    }
}
