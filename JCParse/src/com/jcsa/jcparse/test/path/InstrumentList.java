package com.jcsa.jcparse.test.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * The list of the instrumental file
 * @author yukimula
 *
 */
public class InstrumentList {
	
	/** the sequence of instrumental nodes **/
	private List<InstrumentNode> nodes;
	/**
	 * @param tree the abstract syntax tree that interprets the instrumental results
	 * @param instrument_file the file from which the instrumental nodes are fetched
	 * @throws Exception
	 */
	private InstrumentList(AstTree tree, File instrument_file) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(instrument_file == null || !instrument_file.exists())
			throw new IllegalArgumentException("Invalid file: null");
		else {
			this.nodes = new ArrayList<InstrumentNode>(); String line;
			
			BufferedReader reader = new 
					BufferedReader(new FileReader(instrument_file));
			while((line = reader.readLine()) != null) {
				this.parse(tree, line.strip());
			}
			reader.close();
		}
	}
	/**
	 * @param tree
	 * @param line
	 * @throws Exception
	 */
	private void parse(AstTree tree, String line) throws Exception {
		if(!line.isEmpty()) {
			String[] items = line.split(" ");
			AstNode location = tree.get_node(Integer.parseInt(items[0].strip()));
			byte[] status = new byte[items.length - 1];
			for(int k = 1; k < status.length; k++) {
				status[k - 1] = (byte) Integer.parseInt(items[k].strip());
			}
			
			InstrumentNode node = new InstrumentNode(this, nodes.size(), location, status);
			this.nodes.add(node);
		}
	}
	
	/* getters */
	/**
	 * @return the number of nodes in the list
	 */
	public int length() { return this.nodes.size(); }
	/**
	 * @return the sequence of instrumental nodes
	 */
	public Iterable<InstrumentNode> get_nodes() { return this.nodes; }
	/**
	 * @param k
	 * @return the kth node in the list
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentNode get_node(int k) throws IndexOutOfBoundsException {
		return this.nodes.get(k);
	}
	
	/* parsing */
	/**
	 * @param tree
	 * @param instrument_file
	 * @return the instrumental list by reading the file or null if the file is not defined
	 * @throws Exception
	 */
	public static InstrumentList list(AstTree tree, File instrument_file) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(instrument_file == null)
			throw new IllegalArgumentException("Invalid file: null");
		else if(instrument_file.exists())
			return new InstrumentList(tree, instrument_file);
		else
			return null;
	}
	
}
