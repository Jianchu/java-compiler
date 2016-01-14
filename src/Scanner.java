import java.io.IOException;
import java.io.Reader;

public class Scanner {
    private Reader _in;
    private StringBuilder _sb;
    private int _next;

    public Scanner(Reader in) {
        _in = in;
        _sb = new StringBuilder();
        _next = -1;
    }

    public void scan() {
        try {
            scanStart();
        } catch (IOException ioe) {
            // handle IOException
        }
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
}
