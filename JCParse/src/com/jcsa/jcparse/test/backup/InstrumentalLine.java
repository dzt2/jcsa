package com.jcsa.jcparse.test.backup;

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
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * Each line in instrumental file refers to an InstrumentalUnit that describes the
 * location and its state during testing process, as well as the time-point when
 * the location being evaluated.
 * 
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* definition */
	/** true for after being evaluated and false for before evaluated **/
	private boolean flag;
	/** unit of the instrumental data **/
	private InstrumentalUnit unit;
	private InstrumentalLine(boolean flag, AstNode location) throws IllegalArgumentException {
		this.flag = flag;
		this.unit = new InstrumentalUnit(location);
	}
	
	/* getters */
	/**
	 * @return whether the line corresponds to the beginning of the node
	 */
	public boolean is_beg() { return !this.flag; }
	/**
	 * @return whether the line corresponds to the end of the nodes
	 */
	public boolean is_end() { return this.flag; }
	/**
	 * @return the data unit from instrumental file
	 */
	public InstrumentalUnit get_unit() { return this.unit; }
	/**
	 * @return the type of the location being instrumented
	 */
	public InstrumentalType get_type() { return this.unit.get_type(); }
	/**
	 * @return the location being instrumented
	 */
	public AstNode get_location() { return this.unit.get_location(); }
	/**
	 * @return whether the instrumental unit is evaluated
	 */
	public boolean has_value() { return this.unit.has_value(); }
	/**
	 * @return the value hold at that point during instrumentation
	 */
	public Object get_value() { return this.unit.get_value(); }
	/**
	 * set the value hold by the instrumental line
	 * @param value
	 */
	public void set_value(Object value) { this.unit.set_value(value); }
	
	/* reader */
	/**
	 * @param content
	 * @return whether the content is all of zeros
	 */
	private static boolean is_zero(byte[] content) {
		for(byte element : content) {
			if(element != 0) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @param template
	 * @param ast_tree
	 * @param stream
	 * @return read a line from instrumental file with time flag and data unit.
	 * @throws Exception
	 */
	protected static InstrumentalLine read(CRunTemplate template,
			AstTree ast_tree, InputStream stream) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree");
		else if(stream == null)
			throw new IllegalArgumentException("Invalid stream: null");
		else {
			/* 0. declarations */
			AstNode location; byte[] content; ByteBuffer buffer; int length;
			
			/* 1. read the identifier of AstNode */
			buffer = template.read(stream, CBasicTypeImpl.int_type);
			if(buffer == null) return null;	/* end-of-file */
			location = ast_tree.get_node(buffer.getInt());
			
			/* 2. read the length of content from file */
			buffer = template.read(stream, CBasicTypeImpl.uint_type);
			length = buffer.getInt();
			
			/* 3. read the byte content sequence from file */
			content = new byte[length]; stream.read(content);
			
			/* 4.1. generate the line from statement content */
			if(location instanceof AstStatement) {
				if(is_zero(content)) {
					return new InstrumentalLine(false, location);
				}
				else {
					return new InstrumentalLine(true, location);
				}
			}
			/* 4.2. generate the line from expression value */
			else if(location instanceof AstExpression) {
				content = template.cast_bytes(content);
				CType type = ((AstExpression) location).get_value_type();
				InstrumentalLine line = new InstrumentalLine(true, location);
				line.set_value(template.generate_value(type, content));
				return line;
			}
			/* 4.3. otherwise, invalid case for instrumental file */
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
	}
	/**
	 * @param template
	 * @param ast_tree
	 * @param instrumental_file
	 * @return read all the lines from the entire instrumental file
	 * @throws Exception
	 */
	public static List<InstrumentalLine> read(CRunTemplate template,
			AstTree ast_tree, File instrumental_file) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree");
		else if(instrumental_file == null)
			throw new IllegalArgumentException("Invalid stream: null");
		else {
			List<InstrumentalLine> lines = new ArrayList<InstrumentalLine>();
			FileInputStream stream = new FileInputStream(instrumental_file);
			InstrumentalLine line;
			while((line = read(template, ast_tree, stream)) != null) 
				lines.add(line);
			return lines;
		}
	}
	
}
