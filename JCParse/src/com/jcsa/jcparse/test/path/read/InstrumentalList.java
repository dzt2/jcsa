package com.jcsa.jcparse.test.path.read;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * The sequence of instrumental lines fetched from instrumental file.
 * 
 * @author yukimula
 *
 */
public class InstrumentalList {
	
	/* attributes and constructor */
	private List<InstrumentalLine> lines;
	private InstrumentalList() {
		this.lines = new ArrayList<InstrumentalLine>();
	}
	
	/* getters */
	/**
	 * @return the number of instrumenta lines in the list
	 */
	public int size() { return this.lines.size(); }
	/**
	 * @param k
	 * @return the kth instrumental line
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentalLine get_line(int k) throws IndexOutOfBoundsException {
		return this.lines.get(k);
	}
	/**
	 * @return the sequence of instrumental lines from the file
	 */
	public Iterable<InstrumentalLine> get_lines() { return this.lines; }
	
	/* factory */
	/**
	 * @param tree used to interpret the instrumental data in the file
	 * @param file the instrumental file generated from the testing
	 * @return the instrumental lines fetched from the specified file
	 * @throws Exception
	 */
	public static InstrumentalList list(AstTree tree, File file) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(file == null || !file.exists())
			throw new IllegalArgumentException("Undefined: " + file);
		else {
			InstrumentalList list = new InstrumentalList();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				if(!line.isBlank()) {
					String[] items = line.strip().split(" ");
					AstNode location = tree.get_node(Integer.parseInt(items[0].strip()));
					byte[] value = new byte[items.length - 1];
					for(int k = 1; k < items.length; k++) {
						value[k - 1] = (byte) Integer.parseInt(items[k].strip());
					}
					InstrumentalLine iline = new InstrumentalLine(location, value);
					list.lines.add(iline);
				}
			}
			reader.close();
			return list;
		}
	}
	
}
