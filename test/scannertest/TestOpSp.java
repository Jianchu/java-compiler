package scannertest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import scanner.Token;


@RunWith(Parameterized.class)
public class TestOpSp {

    private String input;
    private String expectedResult;
    ScannerTest scannerTest;

    @Before
    public void setUp() throws Exception {
        scannerTest = new ScannerTest();
    }

    public TestOpSp(String input, String expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection testSingleOp() {
        return Arrays.asList(new Object[][] {
                { "==", "<==, EQUAL>" },
                { "=", "<=, ASSIGN>" }, 
                { ">", "<>, RANGLE>" },
                { ">>", "<>>, DBRANGLE>" },
                { ">>>", "<>>>, TPRANGLE>" }, 
                { ">=", "<>=, GEQ>" },
                { ">>=", "<>>=, RSHIFT_EQ>" },
                { ">>>=", "<>>>=, URSHIFT_EQ>" },
                { "<", "<<, LANGLE>" },
                { "<<", "<<<, DBLANGLE>" },
                { "<=", "<<=, LEQ>" },
                { "<<=", "<<<=, LSHIFT_EQ>" },
                { "~", "<~, BIT_COMP>" },
                { "!", "<!, NOT>" },
                { "!=", "<!=, NEQ>" },
                { "?", "<?, QUESTION>" },
                { ":", "<:, COLON>" },
                { "&", "<&, BITAND>" },
                { "&&", "<&&, AND>" },
                { "&=", "<&=, AND_EQ>" },
                { "|", "<|, BITOR>" },
                { "|=", "<|=, OR_EQ>" },
                { "||", "<||, LOR>" },
                { "+", "<+, PLUS>" },
                { "++", "<++, INCREMENT>" },
                { "+=", "<+=, PLUS_EQ>" },
                { "-", "<-, MINUS>" },
                { "--", "<--, DECREMENT>" },
                { "-=", "<-=, MINUS_EQ>" },
                { "%", "<%, MOD>" },
                { "%=", "<%=, MOD_EQ>" },
                { "*", "<*, STAR>" },
                { "*=", "<*=, STAR_EQ>" },
                { "/", "</, SLASH>" },
                { "/=", "</=, SLASH_EQ>" },
                { "(", "<(, LPAREN>" },
                { ")", "<), RPAREN>" },
                { "{", "<{, LBRACE>" },
                { "}", "<}, RBRACE>" },
                { "[", "<[, LBRACKET>" },
                { "]", "<], RBRACKET>" },
                { ";", "<;, SEMICOLON>" },
                { ",", "<,, COMMA>" },
                { ".", "<., DOT>" },
                
        });
    }

    @Test
    public void testOperators() {
        List<Token> tokens = scannerTest.inputSetUp(input);
        assertEquals(expectedResult, tokens.get(0).toString());
    }
}
