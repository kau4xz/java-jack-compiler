// tests/ScannerTest.java
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {

    private List<Token> tokenize(String code) {
        return new Scanner(code).tokenize();
    }

    private List<Token> semEof(List<Token> tokens) {
        return tokens.stream()
            .filter(t -> t.type != TokenType.EOF)
            .collect(Collectors.toList());
    }

    // ── FASE 1: Números ──────────────────────────────────────────────

    @Test
    void numero_simples_reconhecido() {
        List<Token> tokens = tokenize("289");
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals("289", tokens.get(0).lexeme);
        assertEquals("<integerConstant> 289 </integerConstant>", tokens.get(0).toXml());
    }

    @Test
    void numero_com_espacos_ao_redor() {
        List<Token> tokens = tokenize("  123  ");
        assertEquals("123", tokens.get(0).lexeme);
    }

    @Test
    void zero_reconhecido() {
        List<Token> tokens = tokenize("0");
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals("0", tokens.get(0).lexeme);
    }


    // ── FASE 2: Strings ─────────────────────────────────────────────

    @Test
    void string_simples_sem_aspas_no_lexema() {
        // O lexema NÃO deve ter as aspas
        List<Token> tokens = tokenize("\"hello\"");
        assertEquals(TokenType.STRING, tokens.get(0).type);
        assertEquals("hello", tokens.get(0).lexeme);  // sem aspas!
        assertEquals("<stringConstant> hello </stringConstant>", tokens.get(0).toXml());
    }

    @Test
    void string_com_espacos_internos() {
        List<Token> tokens = tokenize("\"hello world\"");
        assertEquals("hello world", tokens.get(0).lexeme);
    }

    @Test
    void string_nao_fechada_lanca_excecao() {
        // Se a string não fechar, deve lançar RuntimeException
        assertThrows(RuntimeException.class, () -> tokenize("\"aberta"));
    }

    // ── FASE 3: Identificadores e Keywords ──────────────────────────

    @Test
    void identificador_simples() {
        List<Token> tokens = tokenize("minhaVar");
        assertEquals(TokenType.IDENT, tokens.get(0).type);
        assertEquals("<identifier> minhaVar </identifier>", tokens.get(0).toXml());
    }

    @Test
    void identificador_com_numeros_e_underscore() {
        List<Token> tokens = tokenize("var_1");
        assertEquals(TokenType.IDENT, tokens.get(0).type);
        assertEquals("var_1", tokens.get(0).lexeme);
    }

    @Test
    void keyword_class_reconhecida() {
        List<Token> tokens = tokenize("class");
        assertEquals(TokenType.CLASS, tokens.get(0).type);
        assertEquals("<keyword> class </keyword>", tokens.get(0).toXml());
    }

    @Test
    void todas_keywords_reconhecidas() {
        // Garante que nenhuma keyword foi esquecida no mapa KEYWORDS
        String[] palavras = {
            "class","constructor","function","method","field","static",
            "var","int","char","boolean","void","true","false","null",
            "this","let","do","if","else","while","return"
        };
        for (String palavra : palavras) {
            List<Token> tokens = tokenize(palavra);
            assertTrue(
                tokens.get(0).type.isKeyword(),
                "'" + palavra + "' deveria ser keyword"
            );
        }
    }

    // ── FASE 4: Símbolos ─────────────────────────────────────────────

    @Test
    void simbolos_em_expressao() {
        List<Token> tokens = semEof(tokenize("x + y;"));
        String[] esperado = {
            "<identifier> x </identifier>",
            "<symbol> + </symbol>",
            "<identifier> y </identifier>",
            "<symbol> ; </symbol>"
        };
        for (int i = 0; i < esperado.length; i++) {
            assertEquals(esperado[i], tokens.get(i).toXml(),
                "Token " + i + " incorreto");
        }
    }

    @Test
    void simbolo_menor_escapado_no_xml() {
        // '<' é especial no XML — deve virar &lt;
        List<Token> tokens = semEof(tokenize("a < b"));
        assertEquals("<symbol> &lt; </symbol>", tokens.get(1).toXml());
    }

    @Test
    void simbolo_e_comercial_escapado_no_xml() {
        // '&' deve virar &amp;
        List<Token> tokens = semEof(tokenize("a & b"));
        assertEquals("<symbol> &amp; </symbol>", tokens.get(1).toXml());
    }

    // ── FASE 5: Comentários ──────────────────────────────────────────

    @Test
    void comentario_de_linha_ignorado() {
        List<Token> tokens = semEof(tokenize("let x = 5; // isto some"));
        List<String> lexemes = tokens.stream()
            .map(t -> t.lexeme)
            .collect(Collectors.toList());

        assertTrue(lexemes.contains("let"));
        assertTrue(lexemes.contains("x"));
        assertTrue(lexemes.contains("5"));
        assertFalse(lexemes.contains("isto"));
        assertFalse(lexemes.contains("some"));
    }

    @Test
    void comentario_de_bloco_ignorado() {
        List<Token> tokens = semEof(tokenize("x /* removido */ y"));
        assertEquals(2, tokens.size());
        assertEquals("x", tokens.get(0).lexeme);
        assertEquals("y", tokens.get(1).lexeme);
    }

    @Test
    void comentario_bloco_multilinhas() {
        String code = "a\n/* linha 1\n   linha 2 */\nb";
        List<Token> tokens = semEof(tokenize(code));
        assertEquals("a", tokens.get(0).lexeme);
        assertEquals("b", tokens.get(1).lexeme);
    }

    @Test
    void comentario_bloco_nao_fechado_lanca_excecao() {
        assertThrows(RuntimeException.class, () -> tokenize("/* aberto sem fechar"));
    }

    @Test
    void divisao_nao_confundida_com_comentario() {
        // '/' sozinho é operador de divisão, não comentário
        List<Token> tokens = semEof(tokenize("a / b"));
        assertEquals(TokenType.SLASH, tokens.get(1).type);
    }
}