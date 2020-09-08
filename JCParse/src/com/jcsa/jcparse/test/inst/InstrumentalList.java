package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

public class InstrumentalList {
	
	/* definitions */
	private List<InstrumentalNode> nodes;
	private InstrumentalList() {
		this.nodes = new ArrayList<InstrumentalNode>();
	}
	
	/* getters */
	/**
	 * @return the number of instrumental nodes within the list
	 */
	public int length() { return this.nodes.size(); }
	/**
	 * @return the sequence of instrumental nodes in this list
	 */
	public Iterable<InstrumentalNode> get_nodes() { return nodes; }
	/**
	 * @param index
	 * @return the instrumental node in the list with specified index
	 * @throws IndexOutOfBoundsException
	 */
	public InstrumentalNode get_node(int index) throws IndexOutOfBoundsException {
		return this.nodes.get(index);
	}
	
	/* setters */
	/**
	 * remove all the nodes in the list
	 */
	private void clear() {
		for(InstrumentalNode node : this.nodes) {
			node.delete();
		}
		this.nodes.clear();
	}
	
	/* parser */
	private InstrumentalNode read_line(InputStream stream, AstTree 
			ast_tree, CRunTemplate sizeof_template) throws Exception {
		ByteBuffer byte_buffer; AstNode location; int length; byte[] value;
		byte_buffer = sizeof_template.read(stream, CBasicTypeImpl.int_type);
		if(byte_buffer != null) {
			location = ast_tree.get_node(byte_buffer.getInt());
			length = sizeof_template.read(stream, CBasicTypeImpl.uint_type).getInt();
			value = new byte[length];
			stream.read(value);
			value = sizeof_template.cast_bytes(value);
			return new InstrumentalNode(this, this.nodes.size(), location, value);
		}
		else {
			return null;
		}
	}
	private void read(CRunTemplate sizeof_template, 
			AstTree ast_tree, File instrument_file) throws Exception {
		this.clear();
		InputStream stream = new FileInputStream(instrument_file);
		InstrumentalNode node;
		while((node = this.read_line(stream, ast_tree, sizeof_template)) != null) {
			this.nodes.add(node);
		}
		stream.close();
	}
	/**
	 * @param sizeof_template
	 * @param ast_tree
	 * @param instrument_file
	 * @return create the instrumental nodes list from reading the file
	 * @throws Exception
	 */
	public static InstrumentalList parse(CRunTemplate sizeof_template, 
			AstTree ast_tree, File instrument_file) throws Exception {
		if(sizeof_template == null)
			throw new IllegalArgumentException("Invalid template");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree");
		else if(instrument_file == null || !instrument_file.exists())
			throw new IllegalArgumentException("Undefined file");
		else {
			InstrumentalList list = new InstrumentalList();
			list.read(sizeof_template, ast_tree, instrument_file);
			return list;
		}
	}
	
}
