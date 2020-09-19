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
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * Each line read from instrumental file is defined as a tuple {location, value}
 * in which location is seeded with instrumental method while value describes the
 * value hold by that point if the location is an expression or boolean when the
 * location is a statement or function.
 * 
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* definitions */
	/** the location where the instrumentation occurs **/
	private AstNode location;
	/** the value hold by the expression or boolean for statement | function **/
	private Object value;
	
	/* constructor */
	/**
	 * create an instrumental line with the template
	 * @param template
	 * @param location
	 * @param byte_buffer
	 * @throws Exception
	 */
	protected InstrumentalLine(AstNode location, Object value) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(value == null)
			throw new IllegalArgumentException("Invalid value: null");
		else {
			this.location = location;
			this.value = value;
		}
	}
	
	/* getters */
	/**
	 * @return the location where instrumentation is seeded
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return value hold by the location if it is expression
	 * 		   or Boolean for function or statement to define
	 * 		   the start or end point of the execution.
	 */
	public Object get_value() { return this.value; }
	
	/* read from the input-stream of the instrumental file */
	/**
	 * @param template
	 * @param ast_tree
	 * @param stream
	 * @return read the next line from instrumental file or null when
	 * 		   it reaches the end of file (EOF).
	 * @throws Exception
	 */
	public static InstrumentalLine read_from(CRunTemplate template,
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
				return new InstrumentalLine(location, template.
						generate_value(CBasicTypeImpl.bool_type, content));
			}
			/* 4.2. generate the line from expression value */
			else if(location instanceof AstExpression) {
				content = template.cast_bytes(content);
				CType type = ((AstExpression) location).get_value_type();
				return new InstrumentalLine(
						location, template.generate_value(type, content));
			}
			/* 4.3. otherwise, invalid case for instrumental file */
			else {
				throw new IllegalArgumentException("Unsupport: " + location);
			}
		}
	}
	/**
	 * @param ast_tree
	 * @param instrumental_file
	 * @param template
	 * @return the sequence of instrumental lines read from the file
	 * @throws Exception 
	 */
	public static List<InstrumentalLine> read_from(AstTree ast_tree, File 
			instrumental_file, CRunTemplate template) throws Exception {
		if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(instrumental_file == null || !instrumental_file.exists())
			throw new IllegalArgumentException("Invalid instrumental file");
		else if(template == null)
			throw new IllegalArgumentException("No template is provided");
		else {
			List<InstrumentalLine> lines = new ArrayList<InstrumentalLine>();
			FileInputStream stream = new FileInputStream(instrumental_file);
			InstrumentalLine line;
			while((line = read_from(template, ast_tree, stream)) != null) {
				lines.add(line);
			}
			stream.close();
			return lines;
		}
	}
	
}
