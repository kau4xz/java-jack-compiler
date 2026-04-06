// tests/ScannerTest.java
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes do Scanner do Jack Compiler.
 *
 * Cada método de teste:
 *   1. Prepara uma entrada (código Jack como String)
 *   2. Executa o scanner
 *   3. Verifica o resultado com assertions
 *
 * Convenção de nome: test_<oQueEstaTendoTestado>_<situacao>
 */
public class ScannerTest {

    // ── Helpers ──────────────────────────────────────────────────────

    /** Cria um scanner e executa, retornando a lista de tokens. */
    private List<Token> tokenize(String code) {
        return new Scanner(code).tokenize();
    }

    /** Filtra o token EOF da lista (não aparece no XML final). */
    private List<Token> semEof(List<Token> tokens) {
        return tokens.stream()
            .filter(t -> t.type != TokenType.EOF)
            .collect(Collectors.toList());
    }

    // Os testes serão adicionados nas próximas fases...
}