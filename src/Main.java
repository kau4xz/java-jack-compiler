// src/Main.java
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class Main {

    // Pasta padrão para o modo interativo
    private static final String DEFAULT_DIR = "tests/nand2tetris/projects/10/Square";

    public static void main(String[] args) throws IOException {
        // Se não passar argumentos, abre o modo interativo
        if (args.length == 0) {
            runInteractiveMenu();
            return;
        }

        // Caso contrário, continua funcionando como antes (útil para scripts e automação)
        if (args.length > 2) {
            System.err.println("Uso: java Main [arquivo.jack] [saida.xml]");
            System.exit(1);
        }

        String jackPath = args[0];
        String outputPath = null;

        if (args.length == 2) {
            outputPath = args[1];
        }

        processJackFile(jackPath, outputPath);
    }

    // ── Modo Interativo ──────────────────────────────────────────────

    private static void runInteractiveMenu() {
        File dir = new File(DEFAULT_DIR);
        
        // Filtra para pegar apenas os arquivos .jack
        File[] jackFiles = dir.listFiles((d, name) -> name.endsWith(".jack"));

        if (jackFiles == null || jackFiles.length == 0) {
            System.out.println("❌ Nenhum arquivo .jack encontrado na pasta: " + DEFAULT_DIR);
            return;
        }

        System.out.println("📂 Arquivos disponíveis em '" + dir.getName() + "':");
        for (int i = 0; i < jackFiles.length; i++) {
            System.out.println("  [" + i + "] " + jackFiles[i].getName());
        }

        // Usando o nome completo para não conflitar com o seu Scanner do compilador
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
        
        if (outputPath == null) {
            String fileName = inputPath.getFileName().toString().replace(".jack", "T.xml");
            outputPath = "output" + File.separator + fileName;
        }

        // 1. Lê o código fonte
        String code = Files.readString(inputPath);

        // 2. Tokeniza (Usando o SEU Scanner)
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.tokenize();

        // 3. Gera XML
        StringBuilder xml = new StringBuilder();
        xml.append("<tokens>\n");
        for (Token t : tokens) {
            if (t.type != TokenType.EOF) {
                xml.append(t.toXml()).append("\n");
            }
        }
        xml.append("</tokens>\n");

        // 4. Cria diretório e salva
        Path outputFile = Path.of(outputPath);
        Files.createDirectories(outputFile.getParent());
        Files.writeString(outputFile, xml.toString());

        System.out.println("✅ Concluído!");
        System.out.println("📄 Origem: " + inputPath.getFileName());
        System.out.println("💾 Salvo em: " + outputPath);
        System.out.println("🔢 Total de Tokens: " + (tokens.size() - 1));
    }
}