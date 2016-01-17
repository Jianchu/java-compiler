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
    
    private final Map<Character, Function> opMap;
    private final Map<Character, TokenType> sepMap;
    private final Map<String, TokenType> idMap;
    
    private interface Function {
        void run() throws IOException;
    }

    public Scanner(Reader in) {
        _in = in;
        _sb = new StringBuilder();
        _next = -1;
        _tokens = null;
        
        // Example for organizing functions for operators
        opMap = new HashMap<Character, Function>();
        initOpMap();
        
        sepMap = new HashMap<Character, TokenType>();        

        idMap = new HashMap<String, TokenType>();
        initIdMap();
    }

    private void initOpMap() {
        opMap.put('>', scanRangle);
        opMap.put('=', scanAssign);
        opMap.put('<', scanLangle);
        opMap.put('!', scanExclamation);
        opMap.put('?', scanQuestion);
        opMap.put(':', scanColon);
        opMap.put('&', scanAmpersand);
        opMap.put('|', scanVertical);
        opMap.put('^', scanCaret);
        opMap.put('+', scanPlus);
        opMap.put('-', scanMinus);
        opMap.put('*', scanStar);
        opMap.put('/', scanSlash);
        opMap.put('%', scanPercent);
    }

    private void initIdMap() {
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
            /* The loop intentionally does not call _in.read() here--not all tokens are terminated with whitespace;
             * e.g., "scanStart();" needs to read the '(' to find the end of the ID and return.
             * If _in.read() was called in this loop, the LPAREN will be skipped.
             */
            while (Character.isWhitespace(_next)) {
                _next= _in.read();
            }

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

    private Function scanRangle = new Function() {

        public void run() throws IOException {
            TokenType tokenType = TokenType.RANGLE;
            _sb.append(">");
            for (;;) {
                _next = _in.read();
                if (_next == '>' && tokenType.equals(TokenType.RANGLE)) {
                    tokenType = TokenType.DBRANGLE;
                } else if (_next == '>' && tokenType.equals(TokenType.DBRANGLE)) {
                    tokenType = TokenType.TPRANGLE;
                } else if (_next == '=' && tokenType.equals(TokenType.RANGLE)) {
                    tokenType = TokenType.GEQ;
                } else if (_next == '=' && tokenType.equals(TokenType.DBRANGLE)) {
                    tokenType = TokenType.RSHIFT_EQ;
                } else if (_next == '=' && tokenType.equals(TokenType.TPRANGLE)) {
                    tokenType = TokenType.URSHIFT_EQ;
                } else {
                    break;
                }
                _sb.append((char) _next);
            }
            _tokens.add(new Token(_sb.toString(), tokenType));
        }
    };

    private Function scanAssign = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '=':
                _tokens.add(new Token("==", TokenType.EQUAL));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("=", TokenType.ASSIGN));
                break;
            }
        }
    };

    private Function scanLangle = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '=':
                _tokens.add(new Token("<=", TokenType.LEQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("<", TokenType.LANGLE));
                break;
            }
        }
    };

    private Function scanExclamation = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '=':
                _tokens.add(new Token("!=", TokenType.NEQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("!", TokenType.NOT));
                break;
            }
        }
    };

    private Function scanQuestion = new Function() {

        public void run() throws IOException {
            _tokens.add(new Token("?", TokenType.QUESTION));
            _next = _in.read();
        }
    };

    private Function scanColon = new Function() {

        public void run() throws IOException {
            _tokens.add(new Token(":", TokenType.COLON));
            _next = _in.read();
        }
    };

    private Function scanAmpersand = new Function() {

        public void run() throws IOException {
            _tokens.add(new Token("&", TokenType.BITAND));
            _next = _in.read();
        }
    };

    private Function scanVertical = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '|':
                _tokens.add(new Token("||", TokenType.LOR));
                _next = _in.read();
                break;
            case '=':
                _tokens.add(new Token("|=", TokenType.OR_EQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("|", TokenType.BITOR));
                break;
            }
        }
    };

    private Function scanCaret = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '=':
                _tokens.add(new Token("^=", TokenType.EXOR_EQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("^", TokenType.EXOR));
                break;
            }
        }
    };

    private Function scanPlus = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '+':
                _tokens.add(new Token("++", TokenType.INCREMENT));
                _next = _in.read();
                break;
            case '=':
                _tokens.add(new Token("+=", TokenType.PLUS_EQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("+", TokenType.PLUS));
                break;
            }
        }
    };

    private Function scanMinus = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '-':
                _tokens.add(new Token("--", TokenType.DECREMENT));
                _next = _in.read();
                break;
            case '=':
                _tokens.add(new Token("-=", TokenType.MINUS_EQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("-", TokenType.MINUS));
                break;
            }
        }
    };

    private Function scanStar = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '=':
                _tokens.add(new Token("*=", TokenType.STAR_EQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("*", TokenType.STAR));
                break;
            }
        }
    };

    private Function scanSlash = new Function() {
        public void run() throws IOException {
            _next = _in.read();
            if (_next == '/') {         // in-line comment
                do {
                    _next = _in.read();
                } while (_next != '\n' && _next != '\r');
                // Note that if the line terminator is \r\n, scanStart will ignore the \n

                _next = _in.read();
            } else if (_next == '*') {  // block comment
                _next = _in.read();
                for ( ; ; ) {
                    if (_next == '*') {
                        _next = _in.read();
                        if (_next == '/') {
                            break;
                        }
                    } else { // necessary since "**/" can end a comment
                        _next = _in.read();
                    }
                }

                _next = _in.read();
            } else if (_next == '=') {
                _tokens.add(new Token("/=", TokenType.SLASH_EQ));

                _next = _in.read();
            } else {
                _tokens.add(new Token("/", TokenType.SLASH));
            }
        }
    };

    private Function scanPercent = new Function() {

        public void run() throws IOException {
            _next = _in.read();
            switch (_next) {
            case '=':
                _tokens.add(new Token("%=", TokenType.MOD_EQ));
                _next = _in.read();
                break;
            default:
                _tokens.add(new Token("%", TokenType.MOD));
                break;
            }
        }
    };
}
