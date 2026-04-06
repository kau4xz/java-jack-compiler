// src/Scanner.java — VERSÃO COMPLETA (todas as fases)
import java.util.*;

public class Scanner {

    // ── Estado ───────────────────────────────────────────────────────
    private final String code;   // código fonte completo
    private int current = 0;     // posição atual (o "dedo")
    private int line    = 1;     // linha atual (para mensagens de erro)
    private final List<Token> tokens = new ArrayList<>();

    // ── Tabelas de consulta ──────────────────────────────────────────

    private static final Map<Character, TokenType> SYMBOLS  = new HashMap<>();
    private static final Map<String,    TokenType> KEYWORDS = new HashMap<>();

    static {
        // Símbolos — cada caractere mapeia para seu TokenType
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

        // Keywords — o texto exato da palavra reservada
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

    // ── Construtor ───────────────────────────────────────────────────

    public Scanner(String code) {
        this.code = code;
    }

    // ── Navegação ────────────────────────────────────────────────────

    /**
     * Espia o caractere na posição atual + offset, SEM avançar.
     * Retorna '\0' (caractere nulo) se ultrapassar o fim do código.
     *
     * Exemplos:
     *   peek()   → caractere atual
     *   peek(1)  → próximo caractere (útil para detectar // e /*)
     */
    private char peek(int offset) {
        int pos = current + offset;
        return (pos < code.length()) ? code.charAt(pos) : '\0';
    }

    private char peek() {
        return peek(0);
    }

    /**
     * Avança o cursor uma posição.
     * Não controla contagem de linhas aqui — isso fica em skipWhitespace
     * e skipBlockComment, onde os '\n' são processados.
     */
    private void advance() {
        if (current < code.length()) current++;
    }

    // ── Ignorar espaços em branco ────────────────────────────────────

    /**
     * Avança enquanto o caractere atual for espaço, tab ou quebra de linha.
     * Contabiliza as linhas para mensagens de erro corretas.
     */
    private void skipWhitespace() {
        while (true) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r') {
                advance();
            } else if (c == '\n') {
                line++;       // conta a linha ANTES de avançar
                advance();
            } else {
                break;        // caractere não-branco: para aqui
            }
        }
    }

    // ── Loop principal (ainda vazio) ─────────────────────────────────

    public List<Token> tokenize() {
        // Fases serão adicionadas aqui nos próximos passos
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }
}