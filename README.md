# JackCompiler

Este repositório contém o desenvolvimento de um compilador para a linguagem Jack, parte do projeto prático de construção de compiladores (abordagem two-tier compilation). O compilador será responsável por traduzir programas escritos em Jack para a linguagem de Máquina Virtual (VM).

## Informações da Equipe

* **Desenvolvedor:** Kauã Ferreira Galeno
* **Matrícula:** 20250013603
* **Modalidade:** Individual

## Linguagem de Programação

* **Linguagem escolhida:** Java
* **Motivação:** Escolhida por oferecer uma excelente estrutura de orientação a objetos e recursos robustos para manipulação e leitura de arquivos, o que facilitará o desenvolvimento do Analisador Léxico (Scanner) e do Analisador Sintático a partir do zero. Nenhuma ferramenta de geração automática (como Lex ou Yacc) será utilizada, conforme os requisitos do projeto.

## Fases do Projeto

- [x] Analisador Léxico (Scanner / Tokenizer)
- [x] Analisador Sintático (Parser)
- [ ] Geração de Código

---

## Estrutura do Repositório
java-jack-compiler/
├── src/
│   ├── Main.java          # Ponto de entrada com menu interativo
│   ├── Parser.java        # Analisador sintático (recursive descent)
│   ├── Scanner.java       # Analisador léxico (tokenizador)
│   ├── Token.java         # Representação de tokens com geração XML
│   └── TokenType.java     # Enum com todos os tipos de tokens
├── tests/
│   ├── ScannerTest.java   # Testes unitários do scanner (JUnit 5)
│   ├── ParserTest.java    # Testes unitários do parser (JUnit 5)
│   └── nand2tetris/       # Arquivos oficiais do projeto nand2tetris
│       └── projects/
│           └── 10/
│               └── Square/
│                   ├── Main.jack
│                   ├── Main.xml
│                   ├── MainT.xml
│                   ├── Square.jack
│                   ├── Square.xml
│                   ├── SquareT.xml
│                   ├── SquareGame.jack
│                   ├── SquareGame.xml
│                   └── SquareGameT.xml
├── output/                # XMLs gerados (criado automaticamente)
├── tools/                 # Ferramentas do nand2tetris (TextComparer, etc.)
├── junit.jar              # JUnit 5 — não versionado, ver instruções abaixo
├── run_tests.bat          # Script para compilar e testar no Windows
└── README.md

---

## Pré-requisitos

- **Java JDK 11** ou superior instalado e configurado no PATH
- **junit.jar** na raiz do projeto (não está no repositório — baixe pelo link abaixo)
- Arquivos do nand2tetris em `tests/nand2tetris/projects/10/`

### Baixar o JUnit 5

Faça o download e salve como `junit.jar` na raiz do projeto:
[junit-platform-console-standalone-1.10.1-all.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1-all.jar)

Ou via terminal:

```bash
curl -L -o junit.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1-all.jar
```

---

## Atividade 01 — Analisador Léxico

A primeira fase do compilador está concluída. O Scanner lê o código-fonte `.jack`, remove comentários e espaços em branco, e converte o programa em uma lista de *tokens* estruturados, gerando um arquivo `*T.xml` de saída compatível com o formato esperado pelo nand2tetris.

### Tokens reconhecidos

| Tipo | Exemplos |
|---|---|
| `keyword` | `class`, `function`, `let`, `if`, `while`, `return` |
| `symbol` | `{`, `}`, `(`, `)`, `+`, `-`, `<`, `>`, `&` |
| `identifier` | `Main`, `myVar`, `square_1` |
| `integerConstant` | `0`, `42`, `1000` |
| `stringConstant` | `"hello world"`, `"negative"` |

---

## Atividade 02 — Analisador Sintático

A segunda fase do compilador está concluída. O Parser consome a lista de tokens produzida pelo Scanner, verifica a conformidade com a gramática da linguagem Jack e gera um arquivo `*P.xml` representando a árvore de análise sintática.

### Abordagem

Implementado como um **recursive descent parser** — uma função por não-terminal da gramática Jack. Os não-terminais cobertos são: `class`, `classVarDec`, `subroutineDec`, `parameterList`, `subroutineBody`, `varDec`, `statements`, `letStatement`, `ifStatement`, `whileStatement`, `doStatement`, `returnStatement`, `expression`, `term` e `expressionList`.

### Arquivo de saída do parser

O parser gera um arquivo com sufixo `P.xml` na pasta `output/`:

| Entrada | Saída do Scanner | Saída do Parser |
|---|---|---|
| `Main.jack` | `output/MainT.xml` | `output/MainP.xml` |
| `Square.jack` | `output/SquareT.xml` | `output/SquareP.xml` |
| `SquareGame.jack` | `output/SquareGameT.xml` | `output/SquareGameP.xml` |

### Status da validação

| Arquivo | Scanner (T.xml) | Parser (P.xml) |
|---|---|---|
| `Main.jack` | ✅ Idêntico ao gabarito | ✅ Idêntico ao gabarito |
| `Square.jack` | ✅ Idêntico ao gabarito | ✅ Idêntico ao gabarito |
| `SquareGame.jack` | ✅ Idêntico ao gabarito | ✅ Idêntico ao gabarito |

Validação realizada com **18/18 testes JUnit passando** no Scanner e **18/18 testes JUnit passando** no Parser, incluindo comparação direta com os arquivos oficiais do nand2tetris.

---

## Como Executar

### 1. Compilar o projeto

**Windows (PowerShell):**
```powershell
javac -cp junit.jar (Get-ChildItem src/*.java, tests/*.java | % { $_.FullName }) -d out
```

**Linux / Mac:**
```bash
mkdir -p out
javac -cp junit.jar src/*.java tests/*.java -d out
```

---

### 2. Rodar os testes automatizados (JUnit)

**Windows:**
```powershell
.\run_tests.bat
```

**Linux / Mac:**
```bash
java -jar junit.jar --class-path out --select-class ScannerTest --details=verbose
java -jar junit.jar --class-path out --select-class ParserTest --details=verbose
```

Resultado esperado: **18 testes bem-sucedidos** em cada suíte, 0 falhas.

---

### 3. Gerar os XMLs a partir de um arquivo Jack

**Modo interativo** — lista os arquivos `.jack` disponíveis:

```powershell
java -cp out Main
```

**Modo direto** — passa o caminho como argumento:

```powershell
java -cp out Main tests\nand2tetris\projects\10\Square\Main.jack
java -cp out Main tests\nand2tetris\projects\10\Square\Square.jack
java -cp out Main tests\nand2tetris\projects\10\Square\SquareGame.jack
```

Cada execução gera automaticamente os dois arquivos na pasta `output/`:
- `MainT.xml` — saída do scanner
- `MainP.xml` — saída do parser

---

### 4. Comparar a saída com o gabarito

**Windows:**
```powershell
tools\TextComparer.bat output\MainT.xml tests\nand2tetris\projects\10\Square\MainT.xml
tools\TextComparer.bat output\SquareT.xml tests\nand2tetris\projects\10\Square\SquareT.xml
tools\TextComparer.bat output\SquareGameT.xml tests\nand2tetris\projects\10\Square\SquareGameT.xml
```

**Linux / Mac:**
```bash
diff output/MainP.xml tests/nand2tetris/projects/10/Square/Main.xml
diff output/SquareP.xml tests/nand2tetris/projects/10/Square/Square.xml
diff output/SquareGameP.xml tests/nand2tetris/projects/10/Square/SquareGame.xml
```

Nenhuma saída no `diff` = arquivos idênticos ao gabarito. ✅

---

## Desafios Enfrentados na Unidade

**Conflito de nomes com `java.util.Scanner`**
Durante a implementação dos testes, o arquivo `ScannerTest.java` incluía o import `java.util.Scanner`, que conflitava diretamente com a classe `Scanner.java` do compilador. O Java ficava confuso sobre qual `Scanner` usar, causando um erro de compilação difícil de identificar à primeira vista. A solução foi simplesmente remover o import desnecessário — o `Scanner` do compilador é resolvido automaticamente por estar no mesmo classpath.

**Ordem do escape XML no `Token.java`**
Ao gerar a saída XML, os caracteres especiais precisam ser escapados. O problema é que o `&` deve ser substituído por `&amp;` *antes* de qualquer outra substituição. Se o `<` fosse escapado primeiro, ele viraria `&lt;` — e então o `&` dessa string seria escapado novamente, gerando `&amp;lt;` no lugar do correto `&lt;`. Um bug silencioso que só aparecia na comparação direta com o gabarito.
