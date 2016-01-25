package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ParseTableReader {
	
	List<String []> productions;
	int stateSize;
	ParseActions[] parseTable;
	
	public ParseTableReader(File f) throws Exception {
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		
		productions = new ArrayList<String[]>();
		
		String line = null;
		br.readLine(); 	//number of terminals
		
		
		for (int i = 0; i < 3; i++) {
			while ((line = br.readLine()) != null && !isNumeric(line.trim())) {
				if (i == 0) {
					// terminals, do nothing
				} else if (i == 1) {
					// non- terminals, do nothing
				} else {
					// production rules
					productions.add(line.split(" "));
				}
			}
		}
		
		stateSize = Integer.parseInt(line.trim());
		int actionSize = Integer.parseInt(br.readLine().trim());
		parseTable = new ParseActions[actionSize];

		while ((line = br.readLine()) != null) {
			String[] action = line.split(" ");
			int curr = Integer.parseInt(action[0]);
			ParseActions actions;
			if (parseTable[curr] == null) {
				actions = new ParseActions();
				parseTable[curr] = actions;
			} else {
				actions = parseTable[curr];
			}
			actions.addParseAction(action[1], action[2], action[3]);
		}
		
		br.close();
	}
	
	public int getNumStates() {
		return stateSize;
	}
	
	public ParseActions[] getParseActions() {
		return parseTable;
	}
	
	public List<String []> getProductionRules() {
		return productions;
	}
	
	private boolean isNumeric(String n) {
		try {
			Integer.parseInt(n);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Example here
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File(System.getProperty("user.dir") + "/data/test.lr1");
		ParseTableReader ptr = null;
		try {
			ptr = new ParseTableReader(f);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		ParseActions[] pt = ptr.getParseActions();
		try {
			String action = pt[9].getAction("-").toString();
			System.out.println(action);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


class ParseActions {
	Map<String, Action> actions;
	
	public ParseActions() {
		actions = new TreeMap<String, Action>();
	}
	

	
	public void addParseAction(String symbol, String action, String num) throws Exception {
		actions.put(symbol, new Action(action, num));
	}
	
	public void addParseAction(String[] tokens) throws Exception {
		addParseAction(tokens[0], tokens[1], tokens[2]);
	}
	
	public Action getAction(String symbol) throws Exception {
		Action action = actions.get(symbol);
		if (action != null) {
			return action;
		} else {
			throw new Exception("can't find such action.");
		}
	}
}

class Action {
	public ShiftReduce act;
	public int actNum;
	
	public Action(String action, String num) throws Exception {
		if (action.equals("shift")) {
			act = ShiftReduce.SHIFT;
		} else if (action.equals("reduce")) {
			act = ShiftReduce.REDUCE;
		} else {
			throw new Exception("unexpected input.");
		}
		
		actNum = Integer.parseInt(num);
	}
	
	@Override
	public String toString() {
		return act.toString().toLowerCase() + " " + actNum;
	}
}

enum ShiftReduce {
	SHIFT, REDUCE
}

