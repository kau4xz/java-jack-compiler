@echo off
echo Compilando o projeto...
javac -cp junit.jar src/*.java tests/*.java -d out

echo.
echo Executando os testes...
java -jar junit.jar execute --class-path out --select-class ScannerTest --details=tree --details-theme=unicode