// tests/ParserTest.java
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    /** Tokeniza e parseia em um passo */
    private String parse(String code) {
        List<Token> tokens = new Scanner(code).tokenize();
        return new Parser(tokens).parse();
    }

    /** Remove indentação para comparar estrutura sem se preocupar com espaços */
    private String normalize(String xml) {
        return xml.lines()
            .map(String::stripLeading)
            .filter(l -> !l.isEmpty())
            .collect(Collectors.joining("\n"));
    }

    // ── Fase 1: class e classVarDec ─────────────────────────────────────

    @Test
    void classe_vazia_gera_tag_class() {
        String xml = parse("class Foo {}");
        assertTrue(xml.contains("<class>"));
        assertTrue(xml.contains("</class>"));
        assertTrue(xml.contains("<keyword> class </keyword>"));
        assertTrue(xml.contains("<identifier> Foo </identifier>"));
    }

    @Test
    void classVarDec_field_gera_tag() {
        String xml = parse("class Foo { field int x; }");
        assertTrue(xml.contains("<classVarDec>"));
        assertTrue(xml.contains("<keyword> field </keyword>"));
        assertTrue(xml.contains("<keyword> int </keyword>"));
        assertTrue(xml.contains("<identifier> x </identifier>"));
    }

    @Test
    void classVarDec_multiplas_variaveis() {
        String xml = parse("class Foo { static boolean a, b, c; }");
        // deve ter 3 identificadores: a, b, c
        long count = xml.lines()
            .filter(l -> l.contains("<identifier>"))
            .count();
        // 1 (Foo) + 3 (a, b, c) = 4 identificadores
        assertEquals(4, count);
    }

    // ── Fase 2: subroutineDec ───────────────────────────────────────────

@Test
void subroutine_function_void() {
    String xml = parse("class Foo { function void main() {} }");
    assertTrue(xml.contains("<subroutineDec>"));
    assertTrue(xml.contains("<keyword> function </keyword>"));
    assertTrue(xml.contains("<keyword> void </keyword>"));
    assertTrue(xml.contains("<parameterList>"));
    assertTrue(xml.contains("<subroutineBody>"));
}

@Test
void parameterList_com_dois_parametros() {
    String xml = parse("class Foo { function void f(int x, boolean y) {} }");
    assertTrue(xml.contains("<keyword> int </keyword>"));
    assertTrue(xml.contains("<keyword> boolean </keyword>"));
}

@Test
void varDec_local() {
    String xml = parse("class Foo { function void f() { var int i; } }");
    assertTrue(xml.contains("<varDec>"));
    assertTrue(xml.contains("<keyword> var </keyword>"));
}
}