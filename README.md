# JackCompiler

Este repositório contém o desenvolvimento de um compilador para a linguagem Jack, parte do projeto prático de construção de compiladores (abordagem two-tier compilation). O compilador será responsável por traduzir programas escritos em Jack para a linguagem de Máquina Virtual (VM).

## Informações da Equipe

* **Desenvolvedor:** Kauã Ferreira Galeno
* **Modalidade:** Individual

## Linguagem de Programação

* **Linguagem escolhida:** Java
* **Motivação:** Escolhida por oferecer uma excelente estrutura de orientação a objetos e recursos robustos para manipulação e leitura de arquivos, o que facilitará o desenvolvimento do Analisador Léxico (Scanner) e do Analisador Sintático a partir do zero. Nenhuma ferramenta de geração automática (como Lex ou Yacc) será utilizada, conforme os requisitos do projeto.

## Fases do Projeto

- [x] Analisador Léxico (Scanner / Tokenizer)
- [ ] Analisador Sintático (Parser)
- [ ] Geração de Código

---

## Estrutura do Repositório

```
java-jack-compiler/
├── src/
│   ├── Main.java          # Ponto de entrada com menu interativo
│   ├── Scanner.java       # Analisador léxico (tokenizador)
│   ├── Token.java         # Representação de tokens com geração XML
│   └── TokenType.java     # Enum com todos os tipos de tokens
├── tests/
│   ├── ScannerTest.java   # Testes unitários com JUnit 5
│   └── nand2tetris/       # Arquivos oficiais do projeto nand2tetris
│       └── projects/
│           └── 10/
│               └── Square/
│                   ├── Main.jack
│                   ├── MainT.xml
│                   ├── Square.jack
│                   ├── SquareT.xml
│                   ├── SquareGame.jack
│                   └── SquareGameT.xml
├── output/                # XMLs gerados pelo scanner (criado automaticamente)
├── tools/                 # Ferramentas do nand2tetris (TextComparer, etc.)
├── junit.jar              # JUnit 5 — não versionado, ver instruções abaixo
├── run_tests.bat          # Script para compilar e testar no Windows
└── README.md
```

---

## Pré-requisitos

- **Java JDK 11** ou superior instalado e configurado no PATH
- **junit.jar** na raiz do projeto (não está no repositório — baixe pelo link abaixo)
- Arquivos do nand2tetris em `tests/nand2tetris/projects/10/`

### Baixar o JUnit 5

Faça o download do arquivo e salve como `junit.jar` na raiz do projeto:
[junit-platform-console-standalone-1.10.1-all.jar](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1-all.jar)

Ou via terminal:

```bash
curl -L -o junit.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.1/junit-platform-console-standalone-1.10.1-all.jar
```

---

## Atividade 01 — Analisador Léxico

A primeira fase do compilador está concluída. O Scanner lê o código-fonte `.jack`, remove comentários e espaços em branco, e converte o programa em uma lista de *tokens* estruturados, gerando um arquivo `.xml` de saída compatível com o formato esperado pelo nand2tetris.

### Tokens reconhecidos

| Tipo | Exemplos |
|---|---|
| `keyword` | `class`, `function`, `let`, `if`, `while`, `return` |
| `symbol` | `{`, `}`, `(`, `)`, `+`, `-`, `<`, `>`, `&` |
| `identifier` | `Main`, `myVar`, `square_1` |
| `integerConstant` | `0`, `42`, `1000` |
| `stringConstant` | `"hello world"`, `"negative"` |

---

## Como Executar

### 1. Compilar o projeto

**Windows:**
```powershell
mkdir out
javac -cp junit.jar src/*.java tests/*.java -d out
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
```

Resultado esperado:

```
✔ numero_simples_reconhecido
✔ numero_com_espacos_ao_redor
✔ zero_reconhecido
✔ string_simples_sem_aspas_no_lexema
✔ string_com_espacos_internos
✔ string_nao_fechada_lanca_excecao
✔ identificador_simples
✔ identificador_com_numeros_e_underscore
✔ keyword_class_reconhecida
✔ todas_keywords_reconhecidas
✔ simbolos_em_expressao
✔ simbolo_menor_escapado_no_xml
✔ simbolo_e_comercial_escapado_no_xml
✔ comentario_de_linha_ignorado
✔ comentario_de_bloco_ignorado
✔ comentario_bloco_multilinhas
✔ comentario_bloco_nao_fechado_lanca_excecao
✔ divisao_nao_confundida_com_comentario

18 tests successful, 0 failed
```

---

### 3. Gerar o XML a partir de um arquivo Jack

**Modo interativo** — lista os arquivos `.jack` disponíveis para você escolher:

```bash
java -cp out Main
```

Exemplo de saída:
```
Arquivos disponíveis em 'Square':
  [0] Main.jack
  [1] Square.jack
  [2] SquareGame.jack

Digite o número do arquivo para processar: 0

Concluído!
Origem: Main.jack
Salvo em: output/MainT.xml
otal de Tokens: 191
```

**Modo direto** — passa os caminhos como argumento:

```bash
java -cp out Main <arquivo.jack> <saida.xml>
```

Exemplos:

```bash
java -cp out Main tests/nand2tetris/projects/10/Square/Main.jack output/MainT.xml
java -cp out Main tests/nand2tetris/projects/10/Square/Square.jack output/SquareT.xml
java -cp out Main tests/nand2tetris/projects/10/Square/SquareGame.jack output/SquareGameT.xml
```

O XML será salvo na pasta `output/`, criada automaticamente.

---

### 4. Comparar a saída com o gabarito (TextComparer)

Use o **TextComparer** do nand2tetris para verificar se o XML gerado é idêntico ao esperado.

**Windows:**
```powershell
tools\TextComparer.bat output\MainT.xml tests\nand2tetris\projects\10\Square\MainT.xml
tools\TextComparer.bat output\SquareT.xml tests\nand2tetris\projects\10\Square\SquareT.xml
tools\TextComparer.bat output\SquareGameT.xml tests\nand2tetris\projects\10\Square\SquareGameT.xml
```

**Linux / Mac:**
```bash
tools/TextComparer.sh output/MainT.xml tests/nand2tetris/projects/10/Square/MainT.xml
tools/TextComparer.sh output/SquareT.xml tests/nand2tetris/projects/10/Square/SquareT.xml
tools/TextComparer.sh output/SquareGameT.xml tests/nand2tetris/projects/10/Square/SquareGameT.xml
```

Se a mensagem for **`Comparison ended successfully`** nos três arquivos — o scanner está correto. 

Alternativamente, no Linux/Mac você pode usar o `diff` diretamente:

```bash
diff output/MainT.xml tests/nand2tetris/projects/10/Square/MainT.xml
```

Nenhuma saída = arquivos idênticos. 
