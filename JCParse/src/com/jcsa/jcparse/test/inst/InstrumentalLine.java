package com.jcsa.jcparse.test.inst;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.CRunTemplate;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;

/**
 * The original line read from the instrumental data, which derives
 * the tag, location and value of the instrumentation point.
 * 
 * @author yukimula
 *
 */
public class InstrumentalLine {
	
	/* definitions */
	/** type of the instrumental point **/
	private InstrumentalType type;
	/** the location in AST being instrumented **/
	private AstNode location;
	/** value hold by expression for end_expr **/
	private Object value;
	
	/* constructor */
	/**
	 * @param location where the instrumentation is seeded
	 * @param content the content following id:length:content
	 * @throws IllegalArgumentException
	 */
	protected InstrumentalLine(AstNode location, byte[] content, 
			CRunTemplate template) throws Exception {
		if(location == null)
			throw new IllegalArgumentException("Invalid location: null");
		else if(content == null || content.length == 0)
			throw new IllegalArgumentException("No content is provided");
		else if(template == null)
			throw new IllegalArgumentException("Invalid sizeof template");
		else if(location instanceof AstExpression) {
			/* decode the value from byte content */
			content = template.cast_bytes(content);
			CType type = ((AstExpression) location).get_value_type();
			type = CTypeAnalyzer.get_value_type(type);
			
			/* initialize the structure of expression line */
			this.type = InstrumentalType.end_expr;
			this.location = location;
			this.value = template.generate_value(type, content);
		}
		else if(location instanceof AstStatement) {
			/* determine whether it is start of statement */
			boolean beginning = true;
			for(byte element : content) {
				if(element != 0) {
					beginning = false;
					break;
				}
			}
			
			/* determine the type */
			if(location.get_parent() instanceof AstFunctionDefinition) {
				if(beginning) {
					this.type = InstrumentalType.beg_func;
				}
				else {
					this.type = InstrumentalType.end_func;
				}
				this.location = location.get_parent();
			}
			else {
				if(beginning) {
					this.type = InstrumentalType.beg_stmt;
				}
				else {
					this.type = InstrumentalType.end_stmt;
				}
				this.location = location;
			}
			
			/* ignore the value part */	this.value = null;
		}
		else {
			throw new IllegalArgumentException(location.getClass().getSimpleName());
		}
	}
	
	/* getters */
	/**
	 * @return the type of the instrumental point
	 */
	public InstrumentalType get_type() { return this.type; }
	/**
	 * @return the location where instrumentation is seeded
	 */
	public AstNode get_location() { return this.location; }
	/**
	 * @return the line contains value iff. it is end_expr
	 */
	public boolean has_value() { return this.value != null; }
	/**
	 * @return value hold by the location if it is end_expr
	 */
	public Object get_value() { return this.value; }
	
	/* reading methods */
	/**
	 * @param ast_tree
	 * @param instrumental_file
	 * @return the instrumental lines read from the file based on AST-tree
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
			/* declarations */
			List<InstrumentalLine> lines = new ArrayList<InstrumentalLine>();
			FileInputStream stream = new FileInputStream(instrumental_file);
			AstNode location; byte[] content; ByteBuffer buffer; int length;
			
			while(true) {
				/* 1. read the identifier of AstNode */
				buffer = template.read(stream, CBasicTypeImpl.int_type);
				if(buffer == null) break;	/* end-of-file */
				location = ast_tree.get_node(buffer.getInt());
				
				/* 2. read the length of content from file */
				buffer = template.read(stream, CBasicTypeImpl.uint_type);
				length = buffer.getInt();
				
				/* 3. read the byte content sequence from file */
				content = new byte[length];
				stream.read(content);
				
				/* 4. append the instrumental-line to the list */
				lines.add(new InstrumentalLine(location, content, template));
			}
			
			/* close the stream and return lines */
			stream.close();
			return lines;
		}
	}
	
}
