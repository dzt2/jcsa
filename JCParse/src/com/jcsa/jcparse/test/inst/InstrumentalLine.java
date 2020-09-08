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
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * The data of the instrumental data file.
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* definitions */
	/** type of the instrumental line **/
	private InstrumentalType type;
	/** location where the instrument is injected **/
	private AstNode location;
	/** the bytes recording the value of expression **/
	private byte[] value;
	
	/* getters */
	/**
	 * @return the type of the instrumental line
	 */
	public InstrumentalType get_type() { return this.type; }
	/**
	 * @return the location where the instrument is seeded
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return whether the line contains value of some expression
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return the bytes recording the value of expression or null
	 */
	public byte[] get_value() { return this.value; }
	
	/* constructor */
	/**
	 * create a data line fetched from instrumental data file.
	 * @param location
	 * @param value
	 * @throws Exception
	 */
	private InstrumentalLine(AstNode location, byte[] value) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(value == null || value.length == 0)
			throw new IllegalArgumentException("Invalid value: null");
		else if(location instanceof AstExpression) {
			this.type = InstrumentalType.evaluate;
			this.location = location;
			this.value = value;
		}
		else if(location instanceof AstStatement) {
			this.type = InstrumentalType.beg_stmt;
			for(byte element : value) {
				if(element != 0) {
					this.type = InstrumentalType.end_stmt;
					break;
				}
			}
			this.location = location;
			this.value = null;
		}
		else
			throw new IllegalArgumentException(location.getClass().getName());
	}
	/**
	 * @param ast_tree
	 * @param stream
	 * @param sizeof_template
	 * @return the next line from input stream or null when it reaches EOF.
	 * @throws Exception
	 */
	private static InstrumentalLine read_line(AstTree ast_tree, InputStream 
			stream, CRunTemplate sizeof_template) throws Exception {
		ByteBuffer byte_buffer; int length; byte[] value; AstNode location;
		byte_buffer = sizeof_template.read(stream, CBasicTypeImpl.int_type);
		if(byte_buffer != null) {
			location = ast_tree.get_node(byte_buffer.getInt());
			length = sizeof_template.read(stream, CBasicTypeImpl.uint_type).getInt();
			value = new byte[length]; stream.read(value);
			return new InstrumentalLine(location, value);
		}
		return null;
	}
	/**
	 * @param instrumental_file
	 * @param ast_tree
	 * @param sizeof_template
	 * @return the list of instrumental line
	 * @throws Exception
	 */
	public static List<InstrumentalLine> read(File instrumental_file, 
			AstTree ast_tree, CRunTemplate sizeof_template) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(sizeof_template == null)
			throw new IllegalArgumentException("Invalid sizeof_template");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException(instrumental_file.getName());
		else {
			List<InstrumentalLine> lines = new ArrayList<InstrumentalLine>();
			InstrumentalLine line;
			InputStream stream = new FileInputStream(instrumental_file);
			while((line = read_line(ast_tree, stream, sizeof_template)) != null) {
				lines.add(line);
			}
			return lines;
		}
	}
	
}
