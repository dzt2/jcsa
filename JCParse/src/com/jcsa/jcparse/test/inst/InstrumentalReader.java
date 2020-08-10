package com.jcsa.jcparse.test.inst;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

/**
 * It provides the interface to fetch the instrumental unit from the instrumental
 * file in an iteration way.
 * 
 * @author yukimula
 *
 */
public class InstrumentalReader implements Iterator<InstrumentalUnit> {
	
	/* definition */
	/** the abstract syntactic tree to interpret instrumental file **/
	private AstTree tree;
	/** the reader that provides the line from instrumental file **/
	private BufferedReader reader;
	/** the next unit to be parsed from the buffered reader **/
	private InstrumentalUnit unit;
	/**
	 * @param file the instrumental file from which the units are fetched
	 * @throws Exception
	 */
	protected InstrumentalReader(AstTree tree, File file) throws Exception {
		if(file == null || !file.exists())
			throw new IllegalArgumentException("Undefined: " + file);
		else if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else {
			this.tree = tree;
			this.reader = new BufferedReader(new FileReader(file));
			this.update_unit();
		}
	}
	
	/* access methods */
	/**
	 * @param line
	 * @return the instrumental unit parsed from each line in instrumental file
	 * @throws Exception
	 */
	private InstrumentalUnit parse(String line) throws Exception {
		/* extract the original data from the line */
		String[] items = line.strip().split(" ");
		AstNode location = tree.get_node(Integer.parseInt(items[0].strip()));
		byte[] value = new byte[items.length - 1];
		for(int k = 1; k < items.length; k++) {
			value[k - 1] = (byte) Integer.parseInt(items[k].strip());
		}
		if(value.length == 0) {
			throw new IllegalArgumentException("No value provided.");
		}
		
		/* statement parse */
		if(location instanceof AstStatement) {
			boolean is_begin = true;
			for(byte element : value) {
				if(element != 0) {
					is_begin = false;
				}
			}
			if(is_begin) {
				return new InstrumentalUnit(InstrumentalTag.beg, location);
			}
			else {
				return new InstrumentalUnit(InstrumentalTag.end, location);
			}
		}
		/* expression parse */
		else if(location instanceof AstExpression) {
			InstrumentalUnit unit = new InstrumentalUnit(InstrumentalTag.pas, location);
			unit.set_value(value);
			return unit;
		}
		/* invalid case */
		else {
			throw new RuntimeException("Invalid location: " + location);
		}
	}

	/**
	 * update the current unit by parsing the next line
	 * @throws Exception
	 */
	private void update_unit() throws Exception {
		String line;
		while((line = this.reader.readLine()) != null) {
			if(!line.isBlank()) {
				this.unit = this.parse(line);
				return;
			}
		}
		this.unit = null;
	}
	@Override
	public boolean hasNext() {
		return this.unit != null;
	}

	@Override
	public InstrumentalUnit next() {
		InstrumentalUnit unit = this.unit;
		try {
			this.update_unit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return unit;
	}
	
}
