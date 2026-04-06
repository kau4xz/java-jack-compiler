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

    public List<Token> tokenize() {
        while (current < code.length()) {
            skipWhitespace();
            if (current >= code.length()) break;

            char ch = peek();

            // 1. Comentários (DEVE vir antes de verificar símbolos, pois '/' é um símbolo)
            if (ch == '/' && peek(1) == '/') {
                skipLineComment();
                continue;
            } else if (ch == '/' && peek(1) == '*') {
                skipBlockComment();
                continue;
            }
            // 2. Números
            else if (Character.isDigit(ch)) {
                tokens.add(readNumber());
            }
            // 3. Strings
            else if (ch == '"') {
                tokens.add(readString());
            }
            // 4. Identificadores e Keywords
            else if (Character.isLetter(ch) || ch == '_') {
                tokens.add(readIdentifier());
            }
            // 5. Símbolos
            else if (SYMBOLS.containsKey(ch)) {
                tokens.add(new Token(SYMBOLS.get(ch), String.valueOf(ch), line));
                advance();
            }
            // 6. Caracteres não reconhecidos
            else {
                advance(); 
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    // ── FASE 2: Strings ─────────────────────────────────────────────

    /**
     * Lê uma string entre aspas duplas.
     * O lexema NÃO inclui as aspas — só o conteúdo.
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

    // ── FASE 3: Identificadores e Keywords ──────────────────────────

    /**
     * Lê um identificador ou keyword.
     * Ambos seguem o padrão: [a-zA-Z_][a-zA-Z0-9_]*
     * Após ler a palavra, consulta o dicionário KEYWORDS.
     */
    private Token readIdentifier() {
        int start = current;
        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            advance();
        }
        String lexeme = code.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(lexeme, TokenType.IDENT);
        return new Token(type, lexeme, line);
    }

    // ── FASE 5: Comentários ──────────────────────────────────────────

    /**
     * Ignora um comentário de linha (// até \n).
     * Chamado quando peek()=='/'  && peek(1)=='/'
     */
    private void skipLineComment() {
        // Avança até encontrar \n ou fim de arquivo
        while (peek() != '\n' && peek() != '\0') {
            advance();
        }
        // Consome o \n e conta a linha
        if (peek() == '\n') {
            line++;
            advance();
        }
    }

    /**
     * Ignora um comentário de bloco (/* até *\/).
     * Chamado quando peek()=='/' && peek(1)=='*'
     * Suporta múltiplas linhas.
     */
    private void skipBlockComment() {
        advance(); // consome '/'
        advance(); // consome '*'

        while (true) {
            char c = peek();

            if (c == '\0') {
                throw new RuntimeException(
                    "Comentário /* não fechado — fim de arquivo na linha " + line
                );
            }
            if (c == '\n') {
                line++;
                advance();
                continue;
            }
            // Detecta o fechamento '*/'
            if (c == '*' && peek(1) == '/') {
                advance(); // consome '*'
                advance(); // consome '/'
                break;
            }
            advance();
        }
    }
}