// src/Parser.java
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    private final List<Token> tokens;
    private int pos = 0;
    private final StringBuilder xml;
    private int indent = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens.stream()
            .filter(t -> t.type != TokenType.EOF)
            .collect(Collectors.toList());
        this.xml = new StringBuilder();
    }

    // ── Navegação ──────────────────────────────────────────────────────

    private Token peek() {
        if (pos < tokens.size()) return tokens.get(pos);
        return new Token(TokenType.EOF, "", -1);
    }

    private Token advance() {
        Token t = peek();
        pos++;
        return t;
    }

    private boolean check(TokenType type) {
        return peek().type == type;
    }

    private boolean checkLexeme(String lexeme) {
        return peek().lexeme.equals(lexeme);
    }

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

    private void openTag(String tag) {
        xml.append(indentStr()).append("<").append(tag).append(">\n");
        indent++;
    }

    private void closeTag(String tag) {
        indent--;
        xml.append(indentStr()).append("</").append(tag).append(">\n");
    }

    private void writeToken(Token t) {
        xml.append(indentStr()).append(t.toXml()).append("\n");
    }

    // ── Ponto de entrada ────────────────────────────────────────────────

    public String parse() {
        parseClass();
        return xml.toString();
    }

    // ── Fase 1: class e classVarDec ─────────────────────────────────────

    private void parseClass() {
        openTag("class");

        consumeLexeme("class");
        consume(TokenType.IDENT);
        consumeLexeme("{");

        while (checkLexeme("static") || checkLexeme("field")) {
            parseClassVarDec();
        }

        while (checkLexeme("constructor") || checkLexeme("function") || checkLexeme("method")) {
            parseSubroutineDec();
        }

        consumeLexeme("}");

        closeTag("class");
    }

    private void parseClassVarDec() {
        openTag("classVarDec");

        writeToken(advance());
        parseType();
        consume(TokenType.IDENT);

        while (checkLexeme(",")) {
            consumeLexeme(",");
            consume(TokenType.IDENT);
        }

        consumeLexeme(";");

        closeTag("classVarDec");
    }

    private void parseType() {
        if (checkLexeme("int") || checkLexeme("char") || checkLexeme("boolean")) {
            writeToken(advance());
        } else {
            consume(TokenType.IDENT);
        }
    }

    // ── Fase 2: subroutineDec ───────────────────────────────────────────

    private void parseSubroutineDec() {
        openTag("subroutineDec");

        writeToken(advance());

        if (checkLexeme("void")) {
            writeToken(advance());
        } else {
            parseType();
        }

        consume(TokenType.IDENT);
        consumeLexeme("(");
        parseParameterList();
        consumeLexeme(")");
        parseSubroutineBody();

        closeTag("subroutineDec");
    }

    private void parseParameterList() {
        openTag("parameterList");

        if (!checkLexeme(")")) {
            parseType();
            consume(TokenType.IDENT);

            while (checkLexeme(",")) {
                consumeLexeme(",");
                parseType();
                consume(TokenType.IDENT);
            }
        }

        closeTag("parameterList");
    }

    private void parseSubroutineBody() {
        openTag("subroutineBody");

        consumeLexeme("{");

        while (checkLexeme("var")) {
            parseVarDec();
        }

        parseStatements();

        consumeLexeme("}");

        closeTag("subroutineBody");
    }

    private void parseVarDec() {
        openTag("varDec");

        consumeLexeme("var");
        parseType();
        consume(TokenType.IDENT);

        while (checkLexeme(",")) {
            consumeLexeme(",");
            consume(TokenType.IDENT);
        }

        consumeLexeme(";");

        closeTag("varDec");
    }

    // ── Fase 3: statements ──────────────────────────────────────────────

    private void parseStatements() {
        openTag("statements");

        while (checkLexeme("let") || checkLexeme("if") ||
               checkLexeme("while") || checkLexeme("do") ||
               checkLexeme("return")) {

            if      (checkLexeme("let"))    parseLet();
            else if (checkLexeme("if"))     parseIf();
            else if (checkLexeme("while"))  parseWhile();
            else if (checkLexeme("do"))     parseDo();
            else                            parseReturn();
        }

        closeTag("statements");
    }

    private void parseLet() {
        openTag("letStatement");

        consumeLexeme("let");
        consume(TokenType.IDENT);

        if (checkLexeme("[")) {
            consumeLexeme("[");
            parseExpression();
            consumeLexeme("]");
        }

        consumeLexeme("=");
        parseExpression();
        consumeLexeme(";");

        closeTag("letStatement");
    }

    private void parseIf() {
        openTag("ifStatement");

        consumeLexeme("if");
        consumeLexeme("(");
        parseExpression();
        consumeLexeme(")");
        consumeLexeme("{");
        parseStatements();
        consumeLexeme("}");

        if (checkLexeme("else")) {
            consumeLexeme("else");
            consumeLexeme("{");
            parseStatements();
            consumeLexeme("}");
        }

        closeTag("ifStatement");
    }

    private void parseWhile() {
        openTag("whileStatement");

        consumeLexeme("while");
        consumeLexeme("(");
        parseExpression();
        consumeLexeme(")");
        consumeLexeme("{");
        parseStatements();
        consumeLexeme("}");

        closeTag("whileStatement");
    }

    private void parseDo() {
        openTag("doStatement");

        consumeLexeme("do");
        parseSubroutineCall();
        consumeLexeme(";");

        closeTag("doStatement");
    }

    private void parseReturn() {
        openTag("returnStatement");

        consumeLexeme("return");

        if (!checkLexeme(";")) {
            parseExpression();
        }

        consumeLexeme(";");

        closeTag("returnStatement");
    }

    // ── Fase 4: expression e term ───────────────────────────────────────

    private static final java.util.Set<String> OPS =
        java.util.Set.of("+", "-", "*", "/", "&", "|", "<", ">", "=");

    private void parseExpression() {
        openTag("expression");

        parseTerm();

        while (OPS.contains(peek().lexeme)) {
            writeToken(advance());
            parseTerm();
        }

        closeTag("expression");
    }

    private void parseTerm() {
        openTag("term");

        Token t = peek();

        if (t.type == TokenType.NUMBER) {
            writeToken(advance());

        } else if (t.type == TokenType.STRING) {
            writeToken(advance());

        } else if (t.lexeme.equals("true")  || t.lexeme.equals("false") ||
                   t.lexeme.equals("null")  || t.lexeme.equals("this")) {
            writeToken(advance());

        } else if (t.lexeme.equals("(")) {
            consumeLexeme("(");
            parseExpression();
            consumeLexeme(")");

        } else if (t.lexeme.equals("-") || t.lexeme.equals("~")) {
            writeToken(advance());
            parseTerm();

        } else if (t.type == TokenType.IDENT) {
            Token next = (pos + 1 < tokens.size()) ? tokens.get(pos + 1) : peek();

            if (next.lexeme.equals("[")) {
                consume(TokenType.IDENT);
                consumeLexeme("[");
                parseExpression();
                consumeLexeme("]");

            } else if (next.lexeme.equals("(") || next.lexeme.equals(".")) {
                parseSubroutineCall();

            } else {
                consume(TokenType.IDENT);
            }

        } else {
            throw new RuntimeException(
                "Linha " + t.line + ": termo inesperado '" + t.lexeme + "'"
            );
        }

        closeTag("term");
    }

    private void parseSubroutineCall() {
        consume(TokenType.IDENT);

        if (checkLexeme(".")) {
            consumeLexeme(".");
            consume(TokenType.IDENT);
        }

        consumeLexeme("(");
        parseExpressionList();
        consumeLexeme(")");
    }

    private void parseExpressionList() {
        openTag("expressionList");

        if (!checkLexeme(")")) {
            parseExpression();

            while (checkLexeme(",")) {
                consumeLexeme(",");
                parseExpression();
            }
        }

        closeTag("expressionList");
    }
}