// tests/ParserTest.java
import org.junit.jupiter.api.*;


import java.nio.file.Files;
import java.nio.file.Path;
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

// ── Fase 3: statements ──────────────────────────────────────────────

@Test
void let_simples() {
    String xml = parse("class F { function void f() { let x = 5; } }");
    assertTrue(xml.contains("<letStatement>"));
    assertTrue(xml.contains("<keyword> let </keyword>"));
}

@Test
void if_com_else() {
    String xml = parse("class F { function void f() { if (x) { let y = 1; } else { let y = 2; } } }");
    assertTrue(xml.contains("<ifStatement>"));
    // Deve ter 2 blocos de statements
    long count = xml.lines().filter(l -> l.contains("<statements>")).count();
    assertEquals(3, count); // 1 do subroutineBody + 2 do if/else
}

@Test
void while_basico() {
    String xml = parse("class F { function void f() { while (x) { let x = 0; } } }");
    assertTrue(xml.contains("<whileStatement>"));
}

@Test
void return_sem_expressao() {
    String xml = parse("class F { function void f() { return; } }");
    assertTrue(xml.contains("<returnStatement>"));
}

// ── Fase 4: expression e term ───────────────────────────────────────

@Test
void expression_inteiro() {
    String xml = parse("class F { function void f() { let x = 42; } }");
    assertTrue(xml.contains("<expression>"));
    assertTrue(xml.contains("<term>"));
    assertTrue(xml.contains("<integerConstant> 42 </integerConstant>"));
}

@Test
void expression_com_operador() {
    String xml = parse("class F { function void f() { let x = a + b; } }");
    assertTrue(xml.contains("<symbol> + </symbol>"));
    // dois terms dentro da expression
    long terms = xml.lines().filter(l -> l.strip().equals("<term>")).count();
    assertEquals(2, terms);
}

@Test
void term_acesso_array() {
    String xml = parse("class F { function void f() { let y = a[0]; } }");
    assertTrue(xml.contains("<symbol> [ </symbol>"));
}

@Test
void term_chamada_metodo() {
    String xml = parse("class F { function void f() { do Output.printInt(x); } }");
    assertTrue(xml.contains("<doStatement>"));
    assertTrue(xml.contains("<expressionList>"));
}

@Test
void term_unary_negacao() {
    String xml = parse("class F { function void f() { let x = -y; } }");
    assertTrue(xml.contains("<symbol> - </symbol>"));
}


// ── Validação contra arquivos oficiais ──────────────────────────────

private String normalizeXml(String content) {
    return content.lines()
        .map(String::stripLeading)
        .filter(l -> !l.isEmpty())
        .collect(Collectors.joining("\n"));
}

@Test
void valida_Main_jack_contra_oficial() throws Exception {
    String jackPath    = "tests/nand2tetris/projects/10/Square/Main.jack";
    String expectedPath = "tests/nand2tetris/projects/10/Square/Main.xml";

    String code = Files.readString(Path.of(jackPath));
    List<Token> tokens = new Scanner(code).tokenize();
    String generated = new Parser(tokens).parse();

    String expected  = normalizeXml(Files.readString(Path.of(expectedPath)));
    String actual    = normalizeXml(generated);

    assertEquals(expected, actual, "Saída diferente de Main.xml");
}

@Test
void valida_Square_jack_contra_oficial() throws Exception {
    String jackPath    = "tests/nand2tetris/projects/10/Square/Square.jack";
    String expectedPath = "tests/nand2tetris/projects/10/Square/Square.xml";

    String code = Files.readString(Path.of(jackPath));
    List<Token> tokens = new Scanner(code).tokenize();
    String generated = new Parser(tokens).parse();

    assertEquals(
        normalizeXml(Files.readString(Path.of(expectedPath))),
        normalizeXml(generated),
        "Saída diferente de Square.xml"
    );
}
@Test
void valida_SquareGame_jack_contra_oficial() throws Exception {
    String jackPath     = "tests/nand2tetris/projects/10/Square/SquareGame.jack";
    String expectedPath = "tests/nand2tetris/projects/10/Square/SquareGame.xml";

    String code        = Files.readString(Path.of(jackPath));
    List<Token> tokens = new Scanner(code).tokenize();
    String generated   = new Parser(tokens).parse();

    assertEquals(
        normalizeXml(Files.readString(Path.of(expectedPath))),
        normalizeXml(generated),
        "Saída diferente de SquareGame.xml"
    );
}
}