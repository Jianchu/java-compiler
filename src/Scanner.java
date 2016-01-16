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
    
    public Scanner(Reader in) {
        _in = in;
        _sb = new StringBuilder();
        _next = -1;
        _tokens = null;
        
        // Example for organizing functions for operators
        opMap = new HashMap<Character, Runnable>();
        opMap.put('<', scanLangle);
        
        sepMap = new HashMap<Character, TokenType>();        
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
                //need to add the resulting token to list/structure
                scanId();
            } else if (sepMap.containsKey((char) _next)) {
            	//find TokenType.
            } else if (opMap.containsKey((char) _next)) {
            	opMap.get((char) _next).run();
            } else {
                throw new RuntimeException("not yet implemented");
            }
        }
    }

    private void scanId() throws IOException {
        for ( ; ; ) {
            _sb.append((char) _next);
            _next = _in.read();
            if (!Character.isLetterOrDigit(_next) && _next != '_' && _next != '$') {
                //need to return <TokenType.ID, sb.toString()>
                return;
            }
        }
    }
    
    
    private Runnable scanLangle = new Runnable() {
    	public void run() {
    		//whatever you should do?
    	}
    };
}
