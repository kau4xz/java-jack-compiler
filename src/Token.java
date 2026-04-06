// src/Token.java
public class Token {

    public final TokenType type;
    public final String lexeme;
    public final int line;

    public Token(TokenType type, String lexeme, int line) {
        this.type   = type;
        this.lexeme = lexeme;
        this.line   = line;
    }

    /**
     * Gera a linha XML que representa este token.
     *
     * Formato:  <categoria> valor </categoria>
     * Exemplo:  <keyword> class </keyword>
     *           <integerConstant> 42 </integerConstant>
     *           <symbol> &lt; </symbol>   (note o escape do '<')
     */
    public String toXml() {
        String category = type.getCategory();
        String value    = escapeXml(lexeme);
        return "<" + category + "> " + value + " </" + category + ">";
    }

    /**
     * Escapa caracteres especiais do XML.
     * IMPORTANTE: '&' deve ser escapado primeiro para não duplicar escapes.
     *
     * Exemplo: o símbolo '<' vira '&lt;'
     *          o símbolo '&' vira '&amp;'
     */
    private String escapeXml(String text) {
        return text
            .replace("&",  "&amp;")   // SEMPRE primeiro!
            .replace("<",  "&lt;")
            .replace(">",  "&gt;")
            .replace("\"", "&quot;");
    }

    @Override
    public String toString() {
        return "Token(" + type + ", \"" + lexeme + "\", line=" + line + ")";
    }
}