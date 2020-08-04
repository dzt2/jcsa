package com.jcsa.jcparse.test.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * Each line in instrumental file is a tuple as (location, state).
 * 
 * @author yukimula
 *
 */
public class InstrumentLine {
	
	/* constructor */
	/** the location on which the line is defined **/
	private AstNode location;
	/** the byte sequence describing the state of the location **/
	private byte[] state;
	/**
	 * @param location the location on which the line is defined
	 * @param state the byte sequence describing the state of the location
	 * @throws IllegalArgumentException
	 */
	private InstrumentLine(AstNode location, 
			byte[] state) throws IllegalArgumentException {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(state == null || state.length == 0)
			throw new IllegalArgumentException("Invalid state: null");
		else { this.location = location; this.state = state; }
	}
	
	/* getters */
	/**
	 * @return the location on which the line is defined 
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the byte sequence describing state of location
	 */
	public byte[] get_state() { return this.state; }
	
	/* parsing method */
	/**
	 * @param tree the abstract syntax tree to interpret the line
	 * @param line the string line to fetch the instrumental line
	 * @return the instrumental line being parsed from file line
	 * @throws IllegalArgumentException
	 */
	private static InstrumentLine parse(AstTree 
			tree, String line) throws IllegalArgumentException {
		if(line == null || line.isBlank())
			throw new IllegalArgumentException("Invalid line: null");
		else {
			String[] items = line.strip().split(" ");
			AstNode location = tree.
					get_node(Integer.parseInt(items[0].strip()));
			if(location instanceof AstExpression || location instanceof AstStatement) {
				byte[] state = new byte[items.length - 1];
				for(int k = 1; k < items.length; k++) {
					state[k - 1] = (byte) Integer.parseInt(items[k].strip());
				}
				return new InstrumentLine(location, state);
			}
			else {
				throw new IllegalArgumentException("Invalid location: " + location);
			}
		}
	}
	/**
	 * @param tree the abstract syntax tree to interpret the line
	 * @param file the instrumental file being parsed
	 * @return the list of instrumental lines parsed from the file
	 * @throws Exception
	 */
	public static List<InstrumentLine> parse(AstTree tree, File file) throws Exception {
		List<InstrumentLine> lines = new ArrayList<InstrumentLine>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			if(!line.isBlank()) {
				lines.add(parse(tree, line.strip()));
			}
		}
		reader.close();
		return lines;
	}
	
}
