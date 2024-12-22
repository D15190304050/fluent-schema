package stark.coderaider.fluentschema.codegen;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.util.HashMap;
import java.util.Map;

public final class CodeFormatter
{
    private static final org.eclipse.jdt.core.formatter.CodeFormatter formatter;

    static
    {
        Map<String, String> options = new HashMap<>();
        options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_PACKAGE, "1");
        options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_IMPORTS, "1");
        options.put("org.eclipse.jdt.core.formatter.tabulation.char", "space"); // 使用空格替代Tab
        options.put("org.eclipse.jdt.core.formatter.tabulation.size", "4");

        formatter = ToolFactory.createCodeFormatter(options);
    }

    public static String formatCode(String code) throws BadLocationException, MojoExecutionException
    {
        TextEdit edit = formatter.format(org.eclipse.jdt.core.formatter.CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);

        if (edit != null)
        {
            Document document = new Document(code);
            edit.apply(document);
            return document.get();
        }

        // Normally, this line of code is never executed.
        throw new MojoExecutionException("Unable to format code.");
    }
}
