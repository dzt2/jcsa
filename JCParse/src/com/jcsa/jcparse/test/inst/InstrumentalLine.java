package com.jcsa.jcparse.test.inst;

import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * Each line in instrumental file is a tuple, describing the state hold
 * by the location in C source code, including:<br>
 * 
 * 	(1) <code>location: AstNode</code>: the statement or expression being
 * 		instrumented during testing process.<br>
 * 
 * 	(2)	<code>value: Object</code>: the Java-Object describes the value
 * 		of the location (as expression) being fetched in testing, or
 * 		boolean for statement to tag its beginning or end time.<br>
 * 
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* definition */
	/** the location in which the instrumental method is injected **/
	private AstNode location;
	/** the Java-Object describes the value hold by the location as
	 *  an expression or the boolean false as the beginning of the
	 *  statement or true to its end **/
	private Object value;
	/**
	 * create an instrumental line w.r.t. the location and value
	 * @param location
	 * @param value
	 * @throws IllegalArgumentException
	 */
	private InstrumentalLine(AstNode location, Object value) throws IllegalArgumentException {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(value == null)
			throw new IllegalArgumentException("Invalid value as null");
		else {
			this.location = location;
			this.value = value;
		}
	}
	
	/* getters */
	/**
	 * @return the location in which the instrumental method is injected
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the Java-Object describes the value hold by the location as
	 *  	   an expression or the boolean false as the beginning of the
	 *  	   statement or true to its end point.
	 */
	public Object get_value() { return this.value; }
	
	/* parser */
	/**
	 * @param template
	 * @param ast_tree
	 * @param stream
	 * @return read the next line from the stream of the instrumental 
	 * 		   file or null when stream reaches the end of file (EOF)
	 * @throws Exception
	 */
	public static InstrumentalLine read(CRunTemplate template,
			AstTree ast_tree, InputStream stream) throws Exception {
		if(template == null)
			throw new IllegalArgumentException("Invalid template: null");
		else if(ast_tree == null)
			throw new IllegalArgumentException("Invalid ast_tree: null");
		else if(stream == null)
			throw new IllegalArgumentException("Invalid stream as null");
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
			
			/* 4.1. generate line for (statement, boolean) */
			if(location instanceof AstStatement) {
				return new InstrumentalLine(location, template.
						generate_value(CBasicTypeImpl.bool_type, content));
			}
			/* 4.2. generate line for (expression, value) */
			else if(location instanceof AstExpression) {
				CType type = CTypeAnalyzer.get_value_type(
						((AstExpression) location).get_value_type());
				return new InstrumentalLine(location, 
						template.generate_value(type, content));
			}
			/* 4.3. otherwise, throw exception to the users */
			else {
				throw new IllegalArgumentException(location.toString());
			}
		}
	}
	
}
