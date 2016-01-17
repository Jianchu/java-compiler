import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scanner {
    private Reader _in;
    private StringBuilder _sb;  // builds lexemes
    private int _next;          // character read
    private List<Token> _tokens;
    
    private final Map<Character, Runnable> opMap;
    private final Map<Character, TokenType> sepMap;
    private final Map<String, TokenType> idMap;
    
    public Scanner(Reader in) {
        _in = in;
        _sb = new StringBuilder();
        _next = -1;
        _tokens = null;
        
        // Example for organizing functions for operators
        opMap = new HashMap<Character, Runnable>();
        initOpMap();
        
        sepMap = new HashMap<Character, TokenType>();        

        idMap = new HashMap<String, TokenType>();
        idMap.put("null", TokenType.NULL);
        idMap.put("abstract", TokenType.ABSTRACT);
        idMap.put("boolean", TokenType.BOOLEAN);
        idMap.put("break", TokenType.BREAK);
        idMap.put("byte", TokenType.BYTE);
        idMap.put("case", TokenType.CASE);
        idMap.put("catch", TokenType.CATCH);
        idMap.put("char", TokenType.CHAR);
        idMap.put("class", TokenType.CLASS);
        idMap.put("const", TokenType.CONST);
        idMap.put("continue", TokenType.CONTINUE);
        idMap.put("default", TokenType.DEFAULT);
        idMap.put("do", TokenType.DO);
        idMap.put("double", TokenType.DOUBLE);
        idMap.put("else", TokenType.ELSE);
        idMap.put("extends", TokenType.EXTENDS);
        idMap.put("final", TokenType.FINAL);
        idMap.put("finally", TokenType.FINALLY);
        idMap.put("float", TokenType.FLOAT);
        idMap.put("for", TokenType.FOR);
        idMap.put("goto", TokenType.GOTO);
        idMap.put("if", TokenType.IF);
        idMap.put("implements", TokenType.IMPLEMENTS);
        idMap.put("import", TokenType.IMPORT);
        idMap.put("instanceof", TokenType.INSTANCEOF);
        idMap.put("int", TokenType.INT);
        idMap.put("interface", TokenType.INTERFACE);
        idMap.put("long", TokenType.LONG);
        idMap.put("native", TokenType.NATIVE);
        idMap.put("new", TokenType.NEW);
        idMap.put("package", TokenType.PACKAGE);
        idMap.put("private", TokenType.PRIVATE);
        idMap.put("protected", TokenType.PROTECTED);
        idMap.put("public", TokenType.PUBLIC);
        idMap.put("return", TokenType.RETURN);
        idMap.put("short", TokenType.SHORT);
        idMap.put("static", TokenType.STATIC);
        idMap.put("strictfp", TokenType.STRICTFP);
        idMap.put("super", TokenType.SUPER);
        idMap.put("switch", TokenType.SWITCH);
        idMap.put("synchronized", TokenType.SYNCHRONIZED);
        idMap.put("this", TokenType.THIS);
        idMap.put("throw", TokenType.THROW);
        idMap.put("throws", TokenType.THROWS);
        idMap.put("transient", TokenType.TRANSIENT);
        idMap.put("try", TokenType.TRY);
        idMap.put("void", TokenType.VOID);
        idMap.put("volatile", TokenType.VOLATILE);
        idMap.put("while", TokenType.WHILE);
    }

    private void initOpMap() {
        opMap.put('>', scanRangle);
        opMap.put('=', scanAssign);
        opMap.put('<', scanLangle);
        opMap.put('!', scanExclamation);
        opMap.put('&', scanAmpersand);
        opMap.put('|', scanVertical);
        opMap.put('^', scanCaret);
        opMap.put('+', scanPlus);
        opMap.put('-', scanMinus);
        opMap.put('*', scanStar);
        opMap.put('/', ScanSlash);
        opMap.put('%', scanPercent);
    }

    public List<Token> scan() {
        // if scan has already been called, just return the same list
        if (_tokens == null) {
            _tokens = new ArrayList<Token>();
            try {
                // need to use the return value. --Z
                scanStart();
            } catch (IOException ioe) {
                // handle IOException
            }
        }

        return _tokens;
    }

    private void scanStart() throws IOException {
        _next = _in.read();
        for ( ; ; ) {
            if (_next == -1) { //end of file
                break;
            }

            _sb.setLength(0); //clear StringBuilder
            
            if (Character.isLetter(_next)) {
                scanId();
            } else if (sepMap.containsKey((char) _next)) {
            	//find TokenType.
            } else if (opMap.containsKey((char) _next)) {
            	opMap.get((char) _next).run();
            } else {
                throw new RuntimeException("input " + _next + "[" + (char)_next + "] not yet implemented");
            }
        }
    }

    private void scanId() throws IOException {
        for ( ; ; ) {
            _sb.append((char) _next);
            _next = _in.read();
            if (!Character.isLetterOrDigit(_next) && _next != '_' && _next != '$') {
                String lexeme = _sb.toString();
                TokenType type = (idMap.containsKey(lexeme) ? idMap.get(lexeme) : TokenType.ID);
                _tokens.add(new Token(lexeme, type));
                return;
            }
        }
    }

    private Token getLastToken() {
        return _tokens.get(_tokens.size() - 1);
    }

    private void setToken(Token token, String lexeme, TokenType tokenType) {
        token.setLexeme(lexeme);
        token.setTokenType(tokenType);
    }

    private Runnable scanRangle = new Runnable() {

        public void run() {
            Token lastToken = getLastToken();

            switch (lastToken.getTokenType()) {
            case RANGLE:
                setToken(lastToken, ">>", TokenType.DBRANGLE);
                break;
            case DBRANGLE:
                setToken(lastToken, ">>>", TokenType.TPRANGLE);
                break;
            default:
                _tokens.add(new Token(">", TokenType.RANGLE));
                break;
            }
        }
    };

    private Runnable scanAssign = new Runnable() {

        public void run() {
            Token lastToken = getLastToken();

            switch (lastToken.getTokenType()) {
            case ASSIGN:
                setToken(lastToken, "==", TokenType.EQUAL);
                break;
            case RANGLE:
                setToken(lastToken, ">=", TokenType.GEQ);
                break;
            case DBRANGLE:
                setToken(lastToken, ">>=", TokenType.RSHIFT_EQ);
                break;
            case TPRANGLE:
                setToken(lastToken, ">>>=", TokenType.URSHIFT_EQ);
                break;
            default:
                _tokens.add(new Token("=", TokenType.EQUAL));
                break;
            }
        }
    };

    private Runnable scanLangle = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanExclamation = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanAmpersand = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanVertical = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanCaret = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanPlus = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanMinus = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanStar = new Runnable() {

        public void run() {
        }
    };

    private Runnable ScanSlash = new Runnable() {

        public void run() {
        }
    };

    private Runnable scanPercent = new Runnable() {

        public void run() {
        }
    };
}
