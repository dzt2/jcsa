package com.jcsa.jcparse.test.path.read;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * It provides interface to read one line each time from the instrumental file.
 * 
 * @author yukimula
 *
 */
public class InstrumentalReader {
	
	/* definition */
	/** used to interpret the instrumental lines **/
	private AstTree ast_tree;
	/** the reader to fetch each line from file **/
	private BufferedReader reader;
	
	/* constructor */
	/**
	 * @param ast_tree used to interpret the instrumental lines
	 * @param file source to fetch lines from instrumental file
	 * @throws Exception
	 */
	public InstrumentalReader(AstTree ast_tree, File file) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(file == null || !file.exists())
			throw new IllegalArgumentException("No input file is specified");
		else {
			this.ast_tree = ast_tree;
			this.reader = new BufferedReader(new FileReader(file));
		}
	}
	
	/* getters */
	/**
	 * @return the next instrumental line from the file or null if it reaches
	 * 		   the end of the file (EOF).
	 * @throws Exception
	 */
	public InstrumentalLine next_line() throws Exception {
		String line;
		while((line = this.reader.readLine()) != null) {
			if(!line.isBlank()) {
				return this.parse(line);
			}
		}
		return null;
	}
	/**
	 * @param line
	 * @return the instrumental line parsed from the text-line
	 * @throws Exception
	 */
	private InstrumentalLine parse(String line) throws Exception {
		String[] items = line.strip().split(" ");
		AstNode location = this.ast_tree.get_node(
				Integer.parseInt(items[0].strip()));
		byte[] value = new byte[items.length - 1];
		for(int k = 1; k < items.length; k++) {
			value[k - 1] = (byte) Integer.parseInt(items[k].strip());
		}
		return new InstrumentalLine(location, value);
	}
	
}
