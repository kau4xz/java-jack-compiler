// src/Main.java
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Main {

    private static final String DEFAULT_DIR = "tests/nand2tetris/projects/10/Square";

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            runInteractiveMenu();
            return;
        }

        if (args.length > 2) {
            System.err.println("Uso: java Main [arquivo.jack] [saida.xml]");
            System.exit(1);
        }

        String jackPath = args[0];
        String outputPath = args.length == 2 ? args[1] : null;

        processJackFile(jackPath, outputPath);
    }

    // ── Modo Interativo ──────────────────────────────────────────────

    private static void runInteractiveMenu() {
        File dir = new File(DEFAULT_DIR);
        File[] jackFiles = dir.listFiles((d, name) -> name.endsWith(".jack"));

        if (jackFiles == null || jackFiles.length == 0) {
            System.out.println("❌ Nenhum arquivo .jack encontrado em: " + DEFAULT_DIR);
            return;
        }

        System.out.println("📂 Arquivos disponíveis em '" + dir.getName() + "':");
        for (int i = 0; i < jackFiles.length; i++) {
            System.out.println("  [" + i + "] " + jackFiles[i].getName());
        }

        java.util.Scanner teclado = new java.util.Scanner(System.in);
        System.out.print("\n👉 Digite o número do arquivo para processar: ");

        if (!teclado.hasNextInt()) {
            System.out.println("❌ Entrada inválida. Encerrando.");
            return;
        }

        int escolha = teclado.nextInt();

        if (escolha >= 0 && escolha < jackFiles.length) {
            try {
                System.out.println();
                processJackFile(jackFiles[escolha].getPath(), null);
            } catch (IOException e) {
                System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Índice fora do limite.");
        }
    }

    // ── Lógica Central do Compilador ─────────────────────────────────

    private static void processJackFile(String jackPath, String outputPath) throws IOException {
        Path inputPath = Path.of(jackPath);
        String baseName = inputPath.getFileName().toString().replace(".jack", "");

        // Monta os caminhos de saída automaticamente se não foram informados
        if (outputPath == null) {
            outputPath = "output" + File.separator + baseName + "T.xml";
        }
        // Parser sempre gera o arquivo com sufixo P — ex: MainP.xml
        String parsePath = "output" + File.separator + baseName + "P.xml";

        // 1. Lê o código fonte
        String code = Files.readString(inputPath);

        // ── SCANNER ──────────────────────────────────────────────────

        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.tokenize();

        StringBuilder scannerXml = new StringBuilder();
        scannerXml.append("<tokens>\n");
        for (Token t : tokens) {
            if (t.type != TokenType.EOF) {
                scannerXml.append(t.toXml()).append("\n");
            }
        }
        scannerXml.append("</tokens>\n");

        Path scannerFile = Path.of(outputPath);
        Files.createDirectories(scannerFile.getParent());
        Files.writeString(scannerFile, scannerXml.toString());

        // ── PARSER ───────────────────────────────────────────────────

        Parser parser = new Parser(tokens);
        String parseXml = parser.parse();

        Path parseFile = Path.of(parsePath);
        Files.createDirectories(parseFile.getParent());
        Files.writeString(parseFile, parseXml);

        // ── Relatório ─────────────────────────────────────────────────

        System.out.println("✅ Concluído!");
        System.out.println("📄 Origem:      " + inputPath.getFileName());
        System.out.println("🔢 Tokens:      " + (tokens.size() - 1));
        System.out.println("💾 Scanner XML: " + outputPath);
        System.out.println("💾 Parser  XML: " + parsePath);
    }
}