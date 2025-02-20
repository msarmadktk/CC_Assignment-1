import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    // Keyword regex: match any of the keywords exactly (case-sensitive)
    private static final String RE_KEYWORD = "^(Ginti|PointWalaNumber|SachGhoot|harf|dekhao)";
    private static final Pattern PATTERN_KEYWORD = Pattern.compile(RE_KEYWORD);

    // Identifier: must start with a lowercase letter then letters/digits/underscore
    private static final String RE_IDENTIFIER = "^[a-z][a-zA-Z0-9_]*";
    private static final Pattern PATTERN_IDENTIFIER = Pattern.compile(RE_IDENTIFIER);

    // Number: integer or decimal with up to five decimal places
    private static final String RE_NUMBER = "^\\d+(\\.\\d{1,5})?";
    private static final Pattern PATTERN_NUMBER = Pattern.compile(RE_NUMBER);

    // Operator: +, -, *, /, %, ^, =
    private static final String RE_OPERATOR = "^[\\+\\-\\*/%\\^=]";
    private static final Pattern PATTERN_OPERATOR = Pattern.compile(RE_OPERATOR);

    // Separator: ( ) { } [ ] ; ,
    private static final String RE_SEPARATOR = "^[\\(\\)\\{\\}\\[\\];,]";
    private static final Pattern PATTERN_SEPARATOR = Pattern.compile(RE_SEPARATOR);

    // Comments
    private static final String RE_COMMENT_SINGLE = "^//.*";
    private static final Pattern PATTERN_COMMENT_S = Pattern.compile(RE_COMMENT_SINGLE);
    private static final String RE_COMMENT_MULTI = "^/\\*(.|\\R)*?\\*/";
    private static final Pattern PATTERN_COMMENT_M = Pattern.compile(RE_COMMENT_MULTI);

    // String literal: double quotes
    private static final String RE_STRING = "^\"(.*?)\"";
    private static final Pattern PATTERN_STRING = Pattern.compile(RE_STRING);

    // Character literal: single quotes
    private static final String RE_CHARACTER = "^'(.)'";
    private static final Pattern PATTERN_CHARACTER = Pattern.compile(RE_CHARACTER);

    private List<Token> tokens = new ArrayList<>();
    private ErrorHandler errorHandler = new ErrorHandler();

    public List<Token> tokenize(String source) {
        tokens.clear();
        String[] lines = source.split("\\R");
        int lineNum = 1;

        for (String line : lines) {
            int index = 0;
            line = line.trim();

            while (index < line.length()) {
                char current = line.charAt(index);

                // Skip whitespace
                if (Character.isWhitespace(current)) {
                    index++;
                    continue;
                }

                // Single-line comment
                Matcher mCommentSingle = PATTERN_COMMENT_S.matcher(line.substring(index));
                if (mCommentSingle.find()) {
                    tokens.add(new Token(Token.TokenType.COMMENT, mCommentSingle.group(), lineNum, index + 1));
                    break; // rest of line is comment
                }

                // Multi-line comment (for simplicity, same line)
                Matcher mCommentMulti = PATTERN_COMMENT_M.matcher(line.substring(index));
                if (mCommentMulti.find()) {
                    tokens.add(new Token(Token.TokenType.COMMENT, mCommentMulti.group(), lineNum, index + 1));
                    index += mCommentMulti.group().length();
                    continue;
                }

                // String literal
                if (current == '"') {
                    Matcher mString = PATTERN_STRING.matcher(line.substring(index));
                    if (mString.find()) {
                        String str = mString.group();
                        tokens.add(new Token(Token.TokenType.STRING, str, lineNum, index + 1));
                        index += str.length();
                        continue;
                    } else {
                        errorHandler.reportError("Unterminated string literal", lineNum, index + 1);
                        break;
                    }
                }

                // Character literal
                if (current == '\'') {
                    Matcher mChar = PATTERN_CHARACTER.matcher(line.substring(index));
                    if (mChar.find()) {
                        String ch = mChar.group();
                        tokens.add(new Token(Token.TokenType.CHARACTER, ch, lineNum, index + 1));
                        index += ch.length();
                        continue;
                    } else {
                        errorHandler.reportError("Invalid character literal", lineNum, index + 1);
                        break;
                    }
                }

                // Number
                Matcher mNumber = PATTERN_NUMBER.matcher(line.substring(index));
                if (mNumber.find()) {
                    String num = mNumber.group();
                    tokens.add(new Token(Token.TokenType.NUMBER, num, lineNum, index + 1));
                    index += num.length();
                    continue;
                }

                // Operator
                Matcher mOp = PATTERN_OPERATOR.matcher(line.substring(index));
                if (mOp.find()) {
                    String op = mOp.group();
                    tokens.add(new Token(Token.TokenType.OPERATOR, op, lineNum, index + 1));
                    index += op.length();
                    continue;
                }

                // Separator
                Matcher mSep = PATTERN_SEPARATOR.matcher(line.substring(index));
                if (mSep.find()) {
                    String sep = mSep.group();
                    tokens.add(new Token(Token.TokenType.SEPARATOR, sep, lineNum, index + 1));
                    index += sep.length();
                    continue;
                }

                // Check for keyword first (to prevent keywords from being misclassified as identifiers)
                Matcher mKeyword = PATTERN_KEYWORD.matcher(line.substring(index));
                if (mKeyword.find()) {
                    String kw = mKeyword.group();
                    tokens.add(new Token(Token.TokenType.KEYWORD, kw, lineNum, index + 1));
                    index += kw.length();
                    continue;
                }

                // Identifier
                Matcher mId = PATTERN_IDENTIFIER.matcher(line.substring(index));
                if (mId.find()) {
                    String id = mId.group();
                    tokens.add(new Token(Token.TokenType.IDENTIFIER, id, lineNum, index + 1));
                    index += id.length();
                    continue;
                }

                // Unrecognized token
                errorHandler.reportError("Unrecognized token: " + current, lineNum, index + 1);
                tokens.add(new Token(Token.TokenType.UNKNOWN, String.valueOf(current), lineNum, index + 1));
                index++;
            }
            lineNum++;
        }
        return tokens;
    }

    public void printTokens() {
        System.out.println("---- Token List ----");
        for (Token token : tokens) {
            System.out.println(token);
        }
        System.out.println("Total tokens: " + tokens.size());
    }

    public void printDFAStates() {
        System.out.println("\n---- DFA State Simulation for Each Token ----");
        for (Token token : tokens) {
            String lex = token.getLexeme();
            if (lex.trim().isEmpty())
                continue;
            System.out.print("Token '" + lex + "': ");
            int state = 1;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lex.length(); i++) {
                char c = lex.charAt(i);
                sb.append(String.format("State%d(%c)", state, c));
                if (i < lex.length() - 1)
                    sb.append(" -> ");
                state++;
            }
            sb.append(" -> Accept");
            System.out.println(sb);
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<Token> tokenizeAndPrintAll(String source) {
        tokenize(source);
        printTokens();
        printDFAStates();
        return tokens;
    }

    public void printRegexInfo() {
        System.out.println("---- Regular Expressions Used ----");
        System.out.println("Keyword:            " + RE_KEYWORD);
        System.out.println("Identifier:         " + RE_IDENTIFIER);
        System.out.println("Number:             " + RE_NUMBER);
        System.out.println("Operator:           " + RE_OPERATOR);
        System.out.println("Separator:          " + RE_SEPARATOR);
        System.out.println("Single-line Comment:" + RE_COMMENT_SINGLE);
        System.out.println("Multi-line Comment: " + RE_COMMENT_MULTI);
        System.out.println("String Literal:     " + RE_STRING);
        System.out.println("Character Literal:  " + RE_CHARACTER);
        System.out.println("----------------------------------\n");
    }
}
