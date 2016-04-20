package parser;

import java.util.Map;
import java.util.TreeMap;

import scanner.Symbol;

public class ParseActions {
    Map<Symbol, Action> actions;

    public ParseActions() {
        actions = new TreeMap<Symbol, Action>();
    }

    public void addParseAction(String symbol, String action, String num) throws Exception {
        actions.put(Symbol.valueOf(symbol), new Action(action, num));
    }

    public void addParseAction(String[] tokens) throws Exception {
        addParseAction(tokens[0], tokens[1], tokens[2]);
    }

    public Action getAction(Symbol symbol) throws Exception {
        Action action = actions.get(symbol);
        if (action != null) {
            return action;
        } else {
            throw new Exception("can't find such action. Next symbol: " + symbol);
        }
    }
}
