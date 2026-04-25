// src/Parser.java
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;              // posição atual na lista
    private final StringBuilder xml; // saída sendo construída
    private int indent = 0;          // nível de indentação atual

    public Parser(List<Token> tokens) {
        // Filtra o EOF — não faz parte da gramática
        this.tokens = tokens.stream()
            .filter(t -> t.type != TokenType.EOF)
            .collect(Collectors.toList());
        this.xml = new StringBuilder();
    }

    // ── Navegação ──────────────────────────────────────────────────────

    /** Retorna o token atual sem consumir */
    private Token peek() {
        if (pos < tokens.size()) return tokens.get(pos);
        return new Token(TokenType.EOF, "", -1);
    }

    /** Consome e retorna o token atual */
    private Token advance() {
        Token t = peek();
        pos++;
        return t;
    }

    /** Verifica se o token atual é do tipo esperado */
    private boolean check(TokenType type) {
        return peek().type == type;
    }

    /** Verifica se o lexema atual é o esperado (para keywords e símbolos) */
    private boolean checkLexeme(String lexeme) {
        return peek().lexeme.equals(lexeme);
    }

    /**
     * Consome o token atual e escreve no XML.
     * Lança erro se o tipo não bater.
     */
    private void consume(TokenType expected) {
        Token t = peek();
        if (t.type != expected) {
            throw new RuntimeException(
                "Linha " + t.line + ": esperado " + expected +
                " mas encontrado '" + t.lexeme + "' (" + t.type + ")"
            );
        }
        writeToken(advance());
    }

    /**
     * Consome um token com lexema específico (keywords e símbolos).
     * Uso: consumeLexeme("class"), consumeLexeme("{")
     */
    private void consumeLexeme(String lexeme) {
        Token t = peek();
        if (!t.lexeme.equals(lexeme)) {
            throw new RuntimeException(
                "Linha " + t.line + ": esperado '" + lexeme +
                "' mas encontrado '" + t.lexeme + "'"
            );
        }
        writeToken(advance());
    }

    // ── Geração de XML ──────────────────────────────────────────────────

    private String indentStr() {
        return "  ".repeat(indent);
    }

    /** Escreve a tag de abertura de um não-terminal */
    private void openTag(String tag) {
        xml.append(indentStr()).append("<").append(tag).append(">\n");
        indent++;
    }

    /** Escreve a tag de fechamento de um não-terminal */
    private void closeTag(String tag) {
        indent--;
        xml.append(indentStr()).append("</").append(tag).append(">\n");
    }

    /** Escreve um token terminal (já formatado pelo Token.toXml()) */
    private void writeToken(Token t) {
        xml.append(indentStr()).append(t.toXml()).append("\n");
    }

    // ── Ponto de entrada ────────────────────────────────────────────────

    public String parse() {
        parseClass();
        return xml.toString();
    }
}