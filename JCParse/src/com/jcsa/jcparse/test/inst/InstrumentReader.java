package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * It provides an iteration way to read the line from instrumental file.
 * 
 * @author yukimula
 *
 */
public class InstrumentReader {
	
	/* definitions */
	/** the stream that reads the instrumental file **/
	private InputStream stream;
	/** the template to decode the data in the file **/
	private CRunTemplate sizeof_template;
	/** syntax tree on which instruments are seeded **/
	private AstTree ast_tree;
	/**
	 * create the reader to read lines from instrumental file in
	 * an iteration way.
	 * @param sizeof_template
	 * @param ast_tree
	 * @param instrumental_file
	 * @throws Exception
	 */
	public InstrumentReader(CRunTemplate sizeof_template, AstTree 
				ast_tree, File instrumental_file) throws Exception {
		if(sizeof_template == null)
			throw new IllegalArgumentException("Invalid sizeof_template");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Undefined instrumental");
		else {
			this.sizeof_template = sizeof_template;
			this.ast_tree = ast_tree;
			this.stream = new FileInputStream(instrumental_file);
		}
	}
	
	/** 
	 * @return the next integer from the byte buffer
	 * @throws Exception
	 */
	private Integer next_int() throws Exception {
		ByteBuffer buffer = this.sizeof_template.read(this.stream, CBasicTypeImpl.int_type);
		if(buffer == null) {
			return null;
		}
		else {
			return buffer.getInt();
		}
	}
	/**
	 * @param length
	 * @return read n bytes from the stream or null
	 * @throws Exception
	 */
	private byte[] next_bytes(int length) throws Exception {
		byte[] buffer = new byte[length];
		length = this.stream.read(buffer);
		if(length < 0) {
			return null;
		}
		else {
			return buffer;
		}
	}
	/**
	 * @return the next instrumental line from file or null
	 * @throws Exception
	 */
	public InstrumentalLine next() throws Exception {
		Integer id = this.next_int();
		if(id == null) {
			return null;
		}
		else {
			AstNode location = this.ast_tree.get_node(id.intValue());
			int length = this.next_int().intValue();
			byte[] value = this.next_bytes(length);
			value = this.sizeof_template.cast_bytes(value);
			return new InstrumentalLine(location, value);
		}
	}
	
}
