// src/Scanner.java
import java.util.*;

public class Scanner {

    private final String code;
    private int current = 0;
    private int line    = 1;
    private final List<Token> tokens = new ArrayList<>();

    private static final Map<Character, TokenType> SYMBOLS  = new HashMap<>();
    private static final Map<String,    TokenType> KEYWORDS = new HashMap<>();

    static {
        SYMBOLS.put('(', TokenType.LPAREN);   SYMBOLS.put(')', TokenType.RPAREN);
        SYMBOLS.put('{', TokenType.LBRACE);   SYMBOLS.put('}', TokenType.RBRACE);
        SYMBOLS.put('[', TokenType.LBRACKET); SYMBOLS.put(']', TokenType.RBRACKET);
        SYMBOLS.put(',', TokenType.COMMA);    SYMBOLS.put(';', TokenType.SEMICOLON);
        SYMBOLS.put('.', TokenType.DOT);      SYMBOLS.put('+', TokenType.PLUS);
        SYMBOLS.put('-', TokenType.MINUS);    SYMBOLS.put('*', TokenType.ASTERISK);
        SYMBOLS.put('/', TokenType.SLASH);    SYMBOLS.put('&', TokenType.AND);
        SYMBOLS.put('|', TokenType.OR);       SYMBOLS.put('~', TokenType.NOT);
        SYMBOLS.put('<', TokenType.LT);       SYMBOLS.put('>', TokenType.GT);
        SYMBOLS.put('=', TokenType.EQ);

        KEYWORDS.put("class",       TokenType.CLASS);
        KEYWORDS.put("constructor", TokenType.CONSTRUCTOR);
        KEYWORDS.put("function",    TokenType.FUNCTION);
        KEYWORDS.put("method",      TokenType.METHOD);
        KEYWORDS.put("field",       TokenType.FIELD);
        KEYWORDS.put("static",      TokenType.STATIC);
        KEYWORDS.put("var",         TokenType.VAR);
        KEYWORDS.put("int",         TokenType.INT);
        KEYWORDS.put("char",        TokenType.CHAR);
        KEYWORDS.put("boolean",     TokenType.BOOLEAN);
        KEYWORDS.put("void",        TokenType.VOID);
        KEYWORDS.put("true",        TokenType.TRUE);
        KEYWORDS.put("false",       TokenType.FALSE);
        KEYWORDS.put("null",        TokenType.NULL);
        KEYWORDS.put("this",        TokenType.THIS);
        KEYWORDS.put("let",         TokenType.LET);
        KEYWORDS.put("do",          TokenType.DO);
        KEYWORDS.put("if",          TokenType.IF);
        KEYWORDS.put("else",        TokenType.ELSE);
        KEYWORDS.put("while",       TokenType.WHILE);
        KEYWORDS.put("return",      TokenType.RETURN);
    }

    public Scanner(String code) {
        this.code = code;
    }

    // ── Navegação ────────────────────────────────────────────────────

    private char peek(int offset) {
        int pos = current + offset;
        return (pos < code.length()) ? code.charAt(pos) : '\0';
    }

    private char peek() { return peek(0); }

    private void advance() {
        if (current < code.length()) current++;
    }

    // ── Espaços em branco ────────────────────────────────────────────

    private void skipWhitespace() {
        while (true) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r') {
                advance();
            } else if (c == '\n') {
                line++;
                advance();
            } else {
                break;
            }
        }
    }

    // ── FASE 1: Números ──────────────────────────────────────────────

    private Token readNumber() {
        int start = current;
        while (Character.isDigit(peek())) {
            advance();
        }
        String lexeme = code.substring(start, current);
        return new Token(TokenType.NUMBER, lexeme, line);
    }

    // ── Loop principal ────────────────────────────────────────────────

    // ── Loop principal ────────────────────────────────────────────────

    public List<Token> tokenize() {
        while (current < code.length()) {
            skipWhitespace();
            if (current >= code.length()) break;

            char ch = peek();

            if (Character.isDigit(ch)) {
                tokens.add(readNumber());
            } else if (ch == '"') {
                tokens.add(readString());
            } else {
                advance(); // fases seguintes vão tratar os outros casos
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    // ── FASE 2: Strings ─────────────────────────────────────────────

/**
 * Lê uma string entre aspas duplas.
 * O lexema NÃO inclui as aspas — só o conteúdo.
 *
 * Antes de chamar este método, peek() == '"'
 */
private Token readString() {
    advance(); // consome a aspa inicial — não faz parte do lexema
    int start = current;

    while (peek() != '"' && peek() != '\0') {
        if (peek() == '\n') {
            throw new RuntimeException(
                "String não fechada na linha " + line + ": quebra de linha não permitida"
            );
        }
        advance();
    }

    if (peek() == '\0') {
        throw new RuntimeException("String não fechada na linha " + line + ": fim de arquivo inesperado");
    }

    String lexeme = code.substring(start, current);
    advance(); // consome a aspa final — não faz parte do lexema
    return new Token(TokenType.STRING, lexeme, line);
}
}