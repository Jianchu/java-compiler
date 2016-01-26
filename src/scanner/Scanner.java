package scanner;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import exceptions.IllegalBlockCommentException;
import exceptions.IllegalCharException;
import exceptions.IllegalIDException;
import exceptions.IllegalInputCharException;


public class Scanner {
    private Reader _in;
    private StringBuilder _sb;  // builds lexemes
    private int _next;          // character read
    private List<Token> _tokens;
    
    private final Map<Character, RunnableScan> opMap;
    private final Map<Character, Symbol> sepMap;
    private final Map<String, Symbol> idMap;
    private final Set<Character> ESCAPES;
    
    private interface RunnableScan {
        void run() throws IOException, IllegalInputCharException, IllegalBlockCommentException;
    }

    public Scanner(Reader in) {

        _in = in;
        _sb = new StringBuilder();
        _next = -1;
        _tokens = null;
        
        // Example for organizing functions for operators
        opMap = new HashMap<Character, RunnableScan>();
        initOpMap();
        
        sepMap = new HashMap<Character, Symbol>();        
        initSepMap();
	
        idMap = new HashMap<String, Symbol>();
        initIdMap();
        
        ESCAPES = new TreeSet<Character>();
        for (char e : "btnfr\"\'\\".toCharArray()) {
        	ESCAPES.add(e);
        }
    }

    private void initSepMap() {
    	sepMap.put('(', Symbol.LPAREN);
		sepMap.put(')', Symbol.RPAREN);
		sepMap.put('{', Symbol.LBRACE);
		sepMap.put('}', Symbol.RBRACE);
		sepMap.put('[', Symbol.LBRACKET);
		sepMap.put(']', Symbol.RBRACKET);
		sepMap.put(';', Symbol.SEMICOLON);
		sepMap.put(',', Symbol.COMMA);
		sepMap.put('.', Symbol.DOT);		   
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
        opMap.put('~', scanTilde);
    }

    private void initIdMap() {
    	// technically literals
    	idMap.put("true", Symbol.TRUE);
    	idMap.put("false", Symbol.FALSE);
        idMap.put("null", Symbol.NULL);
        // keywords
        idMap.put("abstract", Symbol.ABSTRACT);
        idMap.put("boolean", Symbol.BOOLEAN);
        idMap.put("break", Symbol.BREAK);
        idMap.put("byte", Symbol.BYTE);
        idMap.put("case", Symbol.CASE);
        idMap.put("catch", Symbol.CATCH);
        idMap.put("char", Symbol.CHAR);
        idMap.put("class", Symbol.CLASS);
        idMap.put("const", Symbol.CONST);
        idMap.put("continue", Symbol.CONTINUE);
        idMap.put("default", Symbol.DEFAULT);
        idMap.put("do", Symbol.DO);
        idMap.put("double", Symbol.DOUBLE);
        idMap.put("else", Symbol.ELSE);
        idMap.put("extends", Symbol.EXTENDS);
        idMap.put("final", Symbol.FINAL);
        idMap.put("finally", Symbol.FINALLY);
        idMap.put("float", Symbol.FLOAT);
        idMap.put("for", Symbol.FOR);
        idMap.put("goto", Symbol.GOTO);
        idMap.put("if", Symbol.IF);
        idMap.put("implements", Symbol.IMPLEMENTS);
        idMap.put("import", Symbol.IMPORT);
        idMap.put("instanceof", Symbol.INSTANCEOF);
        idMap.put("int", Symbol.INT);
        idMap.put("interface", Symbol.INTERFACE);
        idMap.put("long", Symbol.LONG);
        idMap.put("native", Symbol.NATIVE);
        idMap.put("new", Symbol.NEW);
        idMap.put("package", Symbol.PACKAGE);
        idMap.put("private", Symbol.PRIVATE);
        idMap.put("protected", Symbol.PROTECTED);
        idMap.put("public", Symbol.PUBLIC);
        idMap.put("return", Symbol.RETURN);
        idMap.put("short", Symbol.SHORT);
        idMap.put("static", Symbol.STATIC);
        idMap.put("strictfp", Symbol.STRICTFP);
        idMap.put("super", Symbol.SUPER);
        idMap.put("switch", Symbol.SWITCH);
        idMap.put("synchronized", Symbol.SYNCHRONIZED);
        idMap.put("this", Symbol.THIS);
        idMap.put("throw", Symbol.THROW);
        idMap.put("throws", Symbol.THROWS);
        idMap.put("transient", Symbol.TRANSIENT);
        idMap.put("try", Symbol.TRY);
        idMap.put("void", Symbol.VOID);
        idMap.put("volatile", Symbol.VOLATILE);
        idMap.put("while", Symbol.WHILE);
    }

    private int read() throws IOException, IllegalInputCharException {
        int c = _in.read();
        if (c < -1 || c > 127) {
            throw new IllegalInputCharException();
        }
        return c;
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
            } catch (IllegalIDException ide) {
            	ide.printStackTrace();
            } catch (IllegalCharException ice) {
            	ice.printStackTrace();
            } catch (IllegalBlockCommentException ibce) {
                ibce.printStackTrace();
            } catch (IllegalInputCharException iice) {
                iice.printStackTrace();
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return _tokens;
    }
    
    /**
     * For Testing. --Z
     * @return
     * @throws Exception
     */
    public List<Token> scanThrow() throws Exception {
    	_tokens = new ArrayList<Token>();
    	scanStart();
    	return _tokens;
    }

    private void scanStart() throws Exception {
        _next = read();
        for ( ; ; ) {
            /* The loop intentionally does not call read() here--not all tokens are terminated with whitespace;
             * e.g., "scanStart();" needs to read the '(' to find the end of the ID and return.
             * If read() was called in this loop, the LPAREN will be skipped.
             */
            while (Character.isWhitespace(_next)) {
                _next = read();
            }

            if (_next == -1) { //end of file
                break;
            }

            _sb.setLength(0); //clear StringBuilder
            
            if (Character.isLetter(_next)) {
                scanId();
            } else if (Character.isDigit(_next)) {
            	// integer literals
            	scanInteger();
            } else if ('\'' == (char) _next) {
            	// character literals
            	scanChar();
            } else if ('\"' == (char) _next) {
            	scanString();
            } else if (sepMap.containsKey((char) _next)) {
            	//find TokenType.
            	scanSeparators();
            } else if (opMap.containsKey((char) _next)) {
            	opMap.get((char) _next).run();
            } else {
                throw new RuntimeException("input " + (char) _next + "["
                        + (char) _next + "] not yet implemented");
            }
        }
    }

    private void scanId() throws IOException, IllegalInputCharException {
        for ( ; ; ) {
            _sb.append((char) _next);
            _next = read();
            if (!Character.isLetterOrDigit(_next) && _next != '_' && _next != '$') {
                String lexeme = _sb.toString();
                Symbol type = (idMap.containsKey(lexeme) ? idMap.get(lexeme) : Symbol.ID);
                _tokens.add(new Token(lexeme, type));
                return;
            }
        }
    }

    private RunnableScan scanRangle = new RunnableScan() {
        public void run() throws IOException, IllegalInputCharException{
            Symbol tokenType = Symbol.RANGLE;
            _sb.append((char) _next);

            for (;;) {
                _next = read();
                if (_next == '>' && tokenType.equals(Symbol.RANGLE)) {
                    tokenType = Symbol.DBRANGLE;
                } else if (_next == '>' && tokenType.equals(Symbol.DBRANGLE)) {
                    tokenType = Symbol.TPRANGLE;
                } else if (_next == '=' && tokenType.equals(Symbol.RANGLE)) {
                    tokenType = Symbol.GEQ;
                } else if (_next == '=' && tokenType.equals(Symbol.DBRANGLE)) {
                    tokenType = Symbol.RSHIFT_EQ;
                } else if (_next == '=' && tokenType.equals(Symbol.TPRANGLE)) {
                    tokenType = Symbol.URSHIFT_EQ;
                } else {
                    break;
                }
                _sb.append((char) _next);
            }
            _tokens.add(new Token(_sb.toString(), tokenType));
        }
    };

    private RunnableScan scanLangle = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException{
            Symbol tokenType = Symbol.LANGLE;
            _sb.append((char) _next);

            for (;;) {
                _next = read();
                if (_next == '<' && tokenType.equals(Symbol.LANGLE)) {
                    tokenType = Symbol.DBLANGLE;
                } else if (_next == '=' && tokenType.equals(Symbol.LANGLE)) {
                    tokenType = Symbol.LEQ;
                } else if (_next == '=' && tokenType.equals(Symbol.DBLANGLE)) {
                    tokenType = Symbol.LSHIFT_EQ;
                } else {
                    break;
                }
                _sb.append((char) _next);
            }
            _tokens.add(new Token(_sb.toString(), tokenType));
        }
    };

    private void scanTwoOptionsOp(Symbol defaultType, char secondChar,
            Symbol twoCharsType) throws IOException, IllegalInputCharException {
        Symbol tokenType = defaultType;
        _sb.append((char) _next);
        for (;;) {
            _next = read();
            if (_next == secondChar && tokenType.equals(defaultType)) {
                tokenType = twoCharsType;
            } else {
                break;
            }
            _sb.append((char) _next);
        }
        _tokens.add(new Token(_sb.toString(), tokenType));
    }

    private void scanThreeOptionsOp(Symbol defaultType, char firstOption,
            Symbol firstType, char secondOption, Symbol secondType)
            throws IOException, IllegalInputCharException {
        Symbol tokenType = defaultType;
        _sb.append((char) _next);
        for (;;) {
            _next = read();
            if (_next == firstOption && tokenType.equals(defaultType)) {
                tokenType = firstType;
            } else if (_next == secondOption && tokenType.equals(defaultType)) {
                tokenType = secondType;
            } else {
                break;
            }
        _sb.append((char) _next);
        }
        _tokens.add(new Token(_sb.toString(), tokenType));
    }

    private RunnableScan scanAssign = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanTwoOptionsOp(Symbol.ASSIGN, '=', Symbol.EQUAL);
        }
    };

    private RunnableScan scanExclamation = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanTwoOptionsOp(Symbol.NOT, '=', Symbol.NEQ);
        }
    };

    private RunnableScan scanQuestion = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            _sb.append((char) _next);
            _tokens.add(new Token(_sb.toString(), Symbol.QUESTION));
            _next = read();
        }
    };

    private RunnableScan scanTilde = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            _sb.append((char) _next);
            _tokens.add(new Token(_sb.toString(), Symbol.BIT_COMP));
            _next = read();
        }
    };

    private RunnableScan scanColon = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            _sb.append((char) _next);
            _tokens.add(new Token(_sb.toString(), Symbol.COLON));
            _next = read();
        }
    };

    private RunnableScan scanAmpersand = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanThreeOptionsOp(Symbol.BITAND, '&', Symbol.AND, '=',
                    Symbol.AND_EQ);
        }
    };

    private RunnableScan scanVertical = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanThreeOptionsOp(Symbol.BITOR, '|', Symbol.LOR, '=',
                    Symbol.OR_EQ);
        }
    };

    private RunnableScan scanCaret = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanTwoOptionsOp(Symbol.EXOR, '=', Symbol.EXOR_EQ);
        }
    };

    private RunnableScan scanPlus = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanThreeOptionsOp(Symbol.PLUS, '+', Symbol.INCREMENT, '=',
                    Symbol.PLUS_EQ);
        }
    };

    private RunnableScan scanMinus = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanThreeOptionsOp(Symbol.MINUS, '-', Symbol.DECREMENT, '=',
                    Symbol.MINUS_EQ);
        }
    };

    private RunnableScan scanStar = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanTwoOptionsOp(Symbol.STAR, '=', Symbol.STAR_EQ);
        }
    };

    private RunnableScan scanSlash = new RunnableScan() {
        public void run() throws IOException, IllegalInputCharException, IllegalBlockCommentException {
            _next = read();
            if (_next == '/') {         // in-line comment
                do {
                    _next = read();
                } while (_next != '\n' && _next != '\r' && _next != -1);

                // Let scanStart deal with the character(s)
            } else if (_next == '*') {  // block comment
                _next = read();
                for ( ; ; ) {
                    if (_next == -1) {
                        throw new IllegalBlockCommentException();
                    }
                    if (_next == '*') {
                        _next = read();
                        if (_next == '/') {
                            break;
                        }
                    } else { // necessary since "**/" can end a comment
                        _next = read();
                    }
                }

                _next = read();
            } else if (_next == '=') {
                _tokens.add(new Token("/=", Symbol.SLASH_EQ));

                _next = read();
            } else {
                _tokens.add(new Token("/", Symbol.SLASH));
            }
        }
    };

    private RunnableScan scanPercent = new RunnableScan() {

        public void run() throws IOException, IllegalInputCharException {
            scanTwoOptionsOp(Symbol.MOD, '=', Symbol.MOD_EQ);
        }
    };
    
    /**
     * scanning separators
     * @throws IOException
     */
    private void scanSeparators() throws IOException, IllegalInputCharException {
    	_sb.append((char) _next);
        String lexeme = _sb.toString();
        _tokens.add(new Token(lexeme, sepMap.get(lexeme.charAt(0))));
    	_next = read();
    }
    
    /**
     * scanning integer literals
     * NOTE: 
     * 		1. leaving size check for parser.
     * 		2. No Octal, Hex or Long.
     * 		3. Keeping the string as "\'c\'" for now.
     * @throws IOException
     * @throws IllegalIDException 
     */
    private void scanInteger() throws IOException, IllegalInputCharException, IllegalIDException {
    	while (Character.isDigit(_next)) {
    		_sb.append((char) _next);
    		_next = read();
    	}
    	_tokens.add(new Token(_sb.toString(), Symbol.DECIMAL));
    	// A proper integer must be terminated with space, operators or ';'.
    	// check for illegal identifiers.
    	if (Character.isLetter(_next) || _next == '_' || _next == '$') {
    		throw new IllegalIDException(_sb.toString() + (char) _next);
    	}
    }
    
    /**
     * scanning character literals
     * @throws Exception 
     */
    private void scanChar() throws Exception {
    	_sb.append((char) _next);
    	_next = read();
    	runawayCheck();
    	// single character
    	readChar();
    	
    	// terminating literal
    	if ('\'' != _next) {
    		throw new IllegalCharException(_sb.toString() + (char) _next + '\'');
    	}
    	_sb.append((char) _next);
    	_tokens.add(new Token(_sb.toString(), Symbol.CHARACTER));
    	
    	_next = read();
    }
    
    /**
     * scanning string literals
     * @throws Exception 
     */
    private void scanString() throws Exception  {
    	_sb.append((char) _next);
    	_next = read();
    	while ('\"' != _next) {
    		runawayCheck();
    		readChar();
    	}
    	
    	_sb.append((char) _next);
    	_tokens.add(new Token(_sb.toString(), Symbol.STRING));
    	
    	_next = read();
    }
    
    /**
     * Helper for reading _next as a single character and putting it in _sb.
     * Used by scanChar() and scanString(). Not to be confused with scanChar().
     * @throws IOException
     * @throws IllegalCharException
     */
    private void readChar() throws IOException, IllegalInputCharException, IllegalCharException {
    	// single character
    	if ('\\' == _next) {
    		// escape character
    		readEscape();
    	} else {
    		// normal character
    		_sb.append((char) _next);
    		_next = read();
    	}
    }
    
    /**
     * reading escape character. INCLUDING OCTAL ESCAPE!!!
     * Unicode Escape not implemented.
     * @throws IOException
     * @throws IllegalCharException
     */
    private void readEscape() throws IOException, IllegalInputCharException, IllegalCharException {
    	_sb.append((char) _next);
    	_next = read();
    	
    	//octal
    	if (_next >= '0' && _next <= '3') {    		
    		_sb.append((char) _next);
    		_next = read();
    		for (int i = 0; i < 2 && _next >= '0' && _next <= '7'; i++) {
    			_sb.append((char) _next);
    			_next = read();
    		}
    	} else if (_next >= '4' && _next <= '9' ){
    		_sb.append((char) _next);
    		_next = read();
    		if (_next >= '0' && _next <= '7') {
    			_sb.append((char) _next);
    			_next = read();
    		}
    		
    	} else if (ESCAPES.contains((char) _next)) {
        	_sb.append((char) _next);
        	_next = read();
    	} else {
    		throw new IllegalCharException(_sb.toString() + (char) _next + '\'');
    	}
    }
    
    /**
     * Used in String and Character to check that no new line or EOF happen before quoting back.
     * @throws Exception
     */
    private void runawayCheck() throws Exception {
		if (_next == -1) {
			// file terminated before quoting back.
			throw new Exception("EOF with unfinished String.");
		} else if ('\n' == _next) {
			// runaway line
			throw new Exception("New line with unfinished String.");
		}
    }
    
}
