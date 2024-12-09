package stark.coderaider.fluentschema.commons;

import java.util.ArrayList;
import java.util.List;

public final class NamingConvention
{
    private NamingConvention()
    {
    }

    public static String convertToClassLikeName(String objectName)
    {
        String classLikeName = "";

        if (objectName != null)
        {
            List<Character> charsToKeep = getCharsToKeep(objectName);
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
            if (Character.isLetterOrDigit(c) || c == '_')
                charsToKeep.add(c);
        }
        return charsToKeep;
    }
}
