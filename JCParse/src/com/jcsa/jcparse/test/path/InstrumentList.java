package com.jcsa.jcparse.test.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * 	list of instrumental lines.
 * 	@author yukimula
 *
 */
public class InstrumentList {
	
	/* constructor */
	/** the sequence of instrumental lines in the file **/
	private List<InstrumentLine> lines;
	/**
	 * create an empty instrumental lines of list
	 */
	private InstrumentList() {
		this.lines = new ArrayList<InstrumentLine>();
	}
	
	/* getters */
	/**
	 * @return the number of instrumental lines
	 */
	public int length() { return this.lines.size(); }
	/**
	 * @param k
	 * @return the kth instrumental line in the list
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentLine get_line(int k) throws IndexOutOfBoundsException {
		return this.lines.get(k);
	}
	/**
	 * @return the list of instrumental lines
	 */
	public Iterable<InstrumentLine> get_lines() { return this.lines; }
	
	/* factory */
	/**
	 * @param ast_tree
	 * @param instrumental_file
	 * @return the instrumental list of lines read from the instrumental file
	 * @throws Exception
	 */
	public static InstrumentList parse(AstTree ast_tree, 
			File instrumental_file) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(instrumental_file == null)
			throw new IllegalArgumentException("Invalid instrument file");
		else {
			InstrumentList list = new InstrumentList(); String line;
			
			BufferedReader reader = new BufferedReader(new FileReader(instrumental_file));
			while((line = reader.readLine()) != null) {
				if(!line.isBlank()) {
					String[] items = line.strip().split(" ");
					AstNode location = ast_tree.get_node(Integer.parseInt(items[0].strip()));
					byte[] state = new byte[items.length - 1];
					for(int k = 1; k < items.length; k++) {
						state[k - 1] = (byte) Integer.parseInt(items[k].strip());
					}
					InstrumentLine node = new InstrumentLine(list, list.length(), location, state);
					list.lines.add(node);
				}
			}
			reader.close();
			
			return list;
		}
	}
	
}
