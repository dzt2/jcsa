package com.jcsa.jcparse.test.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;

public class InstrumentList {
	
	/* attribute */
	/** the sequence of nodes fetched from instrumental results **/
	private List<InstrumentNode> nodes;
	
	/* getters */
	/**
	 * @return whether the list is empty
	 */
	public boolean isEmpty() { return this.nodes.isEmpty(); }
	/**
	 * @return the length of the list in instrumental analysis result
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @param k 
	 * @return the kth node in the list
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentNode get_node(int k) throws IndexOutOfBoundsException {
		return this.nodes.get(k);
	}
	/**
	 * @return the sequence of instrumental nodes in this list
	 */
	public Iterable<InstrumentNode> get_nodes() { return this.nodes; }
	/**
	 * @return the first node in the list or null
	 */
	public InstrumentNode get_head_node() {
		if(this.nodes.isEmpty())
			return null;
		else
			return this.nodes.get(0);
	}
	/**
	 * @return the first node in the list or null
	 */
	public InstrumentNode get_tail_node() {
		if(this.nodes.isEmpty())
			return null;
		else
			return this.nodes.get(this.nodes.size() - 1);
	}
	
	/* setters */
	/**
	 * @param ast_location
	 * @param bytes_status
	 * @return the node created by the arguments in the tail of the list
	 * @throws Exception
	 */
	private InstrumentNode append(AstNode ast_location, byte[] bytes_status) throws Exception {
		InstrumentNode node = new InstrumentNode(this, 
				this.nodes.size(), ast_location, bytes_status);
		InstrumentNode prev = this.get_tail_node();
		
		node.prev = prev; node.next = null;
		if(prev != null) prev.next = node;
		this.nodes.add(node); return node;
	}
	/**
	 * parse one line in instrumental result file to a node in the list
	 * @param ast_tree
	 * @param line
	 * @throws Exception
	 */
	private void parse(AstTree ast_tree, String line) throws Exception {
		if(!line.isBlank()) {
			String[] items = line.strip().split(" ");
			AstNode ast_location = ast_tree.get_node(Integer.parseInt(items[0].strip()));
			byte[] bytes_status = new byte[items.length - 1];
			for(int k = 1; k < items.length; k++) {
				bytes_status[k - 1] = (byte) Integer.parseInt(items[k].strip());
			}
			this.append(ast_location, bytes_status);
		}
	}
	/**
	 * @param ast_tree the abstract sytax tree that interprets the instrumental results
	 * @param instrument_file the file that records the instrumental analysis result.
	 * @throws Exception
	 */
	private InstrumentList(AstTree ast_tree, File instrument_file) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(instrument_file == null)
			throw new IllegalArgumentException("Invalid instrument file");
		else {
			this.nodes = new ArrayList<InstrumentNode>();
			
			BufferedReader reader = new BufferedReader(new FileReader(instrument_file));
			String line;
			while((line = reader.readLine()) != null) {
				this.parse(ast_tree, line.strip());
			}
			reader.close();
		}
	}
	/**
	 * @param ast_tree the abstract sytax tree that interprets the instrumental results
	 * @param instrument_file the file that records the instrumental analysis result.
	 * @return the list of instrumental analysis nodes
	 * @throws Exception
	 */
	public static InstrumentList list(AstTree ast_tree, File instrument_file) throws Exception {
		return new InstrumentList(ast_tree, instrument_file);
	}
	
}
