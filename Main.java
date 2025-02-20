import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Read from 'source.txt'
        String filePath = "source.txt"; // Adjust if needed
        String sourceCode = "";
        try {
            sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
            return;
        }

        // Create a LexicalAnalyzer and tokenize the code
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        lexer.printRegexInfo();
        List<Token> tokenList = lexer.tokenizeAndPrintAll(sourceCode);

        // Create and populate a symbol table (demo)
        SymbolTable symbolTable = new SymbolTable();

        // Simple scope tracking: GLOBAL outside, LOCAL inside {}
        SymbolTable.Scope currentScope = SymbolTable.Scope.GLOBAL;
        boolean expectingIdentifier = false;
        String currentType = null;

        for (Token t : tokenList) {
            // Scope changes
            if (t.getType() == Token.TokenType.SEPARATOR) {
                String lex = t.getLexeme();
                if (lex.equals("{"))
                    currentScope = SymbolTable.Scope.LOCAL;
                else if (lex.equals("}"))
                    currentScope = SymbolTable.Scope.GLOBAL;
            }

            // If token is a keyword for data types, mark the next identifier
            if (t.getType() == Token.TokenType.KEYWORD) {
                switch (t.getLexeme()) {
                    case "Ginti":
                    case "PointWalaNumber":
                    case "SachGhoot":
                    case "harf":
                        currentType = t.getLexeme();
                        expectingIdentifier = true;
                        break;
                    default:
                        currentType = null;
                        expectingIdentifier = false;
                        break;
                }
            } else if (expectingIdentifier && t.getType() == Token.TokenType.IDENTIFIER) {
                symbolTable.addSymbol(t.getLexeme(), currentType, currentScope);
                expectingIdentifier = false;
                currentType = null;
            }
        }

        // Print the Symbol Table
        symbolTable.printSymbolTable();
    }
}
