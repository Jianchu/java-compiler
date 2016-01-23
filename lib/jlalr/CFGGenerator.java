package jlalr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;

public class CFGGenerator {
	
	public static void main(String[] args) {
		String projPath = System.getProperty("user.dir");

		try {
			File f = new File(projPath + "/data/grammar.txt");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			
			Set<String> nonTerm = new TreeSet<String>();
			Set<String> term = new TreeSet<String>();
			String productions = "";
			int productionsCount = 0;
			while ((line = br.readLine()) != null) {				
				if (line.trim().equals("") || line.charAt(0) == '#') {
					// skip comment lines
					continue;
				}
				
				productions = productions + line + "\n";
				productionsCount ++;
				
				String[] symbols = line.split(" ");
				// LHS is non terminal
				nonTerm.add(symbols[0]);
				
				// Put everything in term for now
				for (String s : symbols)
					term.add(s);
			}
			term.removeAll(nonTerm);	//remove all non terms to get term
			
			br.close();
			
			File out = new File(projPath + "/data/gen/input.cfg");
			FileWriter fw = new FileWriter(out);
			BufferedWriter bw = new BufferedWriter(fw);
			
			//write terminal 
			bw.write(term.size() + "\n");
			for (String s : term){
				bw.write(s + "\n");
			}
			
			//nonterminal
			bw.write(nonTerm.size() + "\n");
			for (String s : nonTerm) {
				bw.write(s + "\n");
			}
			
			//Always assume start symbol S.
			bw.write("S\n");
			
			bw.write(productionsCount + "\n");
			bw.write(productions);
			
			bw.close();
			
			System.setIn(new FileInputStream(new File(projPath + "/data/gen/input.cfg")));
			System.setOut(new PrintStream(new File(projPath + "/data/gen/grammar.lr1")));

			Jlr1.main(null);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		

			
	}
}
