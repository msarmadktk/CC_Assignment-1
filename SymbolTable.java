import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public enum Scope {
        GLOBAL, LOCAL
    }

    public static class Symbol {
        private String name;
        private String type;
        private Scope scope;

        public Symbol(String name, String type, Scope scope) {
            this.name = name;
            this.type = type;
            this.scope = scope;
        }

        @Override
        public String toString() {
            return String.format("Symbol[name=%s, type=%s, scope=%s]", name, type, scope);
        }
    }

    private Map<String, Symbol> table = new HashMap<>();

    public void addSymbol(String name, String type, Scope scope) {
        // Only add if it matches our updated identifier regex: ^[a-z][a-zA-Z0-9_]*$
        if (!name.matches("^[a-z][a-zA-Z0-9_]*$")) {
            System.err.println("Invalid identifier: '" + name + "'. Must match ^[a-z][a-zA-Z0-9_]*$");
            return;
        }

        Symbol sym = new Symbol(name, type, scope);
        table.put(name, sym);
    }

    public Symbol getSymbol(String name) {
        return table.get(name);
    }

    public void printSymbolTable() {
        System.out.println("\n---- Symbol Table ----");
        for (Symbol sym : table.values()) {
            System.out.println(sym);
        }
    }
}
