// src/TokenType.java
public enum TokenType {

    // Literais — valores que o programador escreve diretamente
    NUMBER,   // ex: 42, 100, 0
    STRING,   // ex: "hello world"
    IDENT,    // ex: myVar, Square, x

    // Símbolos — caracteres especiais da linguagem
    LPAREN, RPAREN,     // ( )
    LBRACE, RBRACE,     // { }
    LBRACKET, RBRACKET, // [ ]
    COMMA, SEMICOLON,   // , ;
    DOT, PLUS, MINUS,   // . + -
    ASTERISK, SLASH,    // * /
    AND, OR, NOT,       // & | ~
    LT, GT, EQ,         // < > =

    // Keywords — palavras reservadas da linguagem Jack
    CLASS, CONSTRUCTOR, FUNCTION, METHOD,
    FIELD, STATIC, VAR,
    INT, CHAR, BOOLEAN, VOID,
    TRUE, FALSE, NULL, THIS,
    LET, DO, IF, ELSE, WHILE, RETURN,

    EOF; // marcador de fim de arquivo

    // Retorna a string que aparece na tag XML
    public String getCategory() {
        switch (this) {
            case NUMBER: return "integerConstant";
            case STRING: return "stringConstant";
            case IDENT:  return "identifier";
            case EOF:    return "eof";
            default:
                // Todo o resto é keyword ou symbol
                return isKeyword() ? "keyword" : "symbol";
        }
    }

    // Diz se este tipo é uma palavra reservada
    public boolean isKeyword() {
        switch (this) {
            case CLASS: case CONSTRUCTOR: case FUNCTION: case METHOD:
            case FIELD: case STATIC: case VAR: case INT: case CHAR:
            case BOOLEAN: case VOID: case TRUE: case FALSE: case NULL:
            case THIS: case LET: case DO: case IF: case ELSE:
            case WHILE: case RETURN:
                return true;
            default:
                return false;
        }
    }
}