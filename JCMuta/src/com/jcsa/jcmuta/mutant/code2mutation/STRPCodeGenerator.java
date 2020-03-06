package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCaseStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDefaultStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstLabeledStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.text.CText;

public class STRPCodeGenerator extends MutaCodeGenerator {
	
	/** to present the trap_on_statement code segment **/
	protected static final String trap_on_invocation = 
			" " + MutaCodeGenerator.mutant_line_comment + 
			" " + MutaCodeTemplates.trap_on_stmt_template
			+ " ";
	
	/**
	 * find the location to be inserted with trap_on_statement();
	 * especially for statement in for-loops
	 * @param location
	 * @return
	 * @throws Exception
	 */
	protected AstStatement find_trapped_statement(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		if(parent instanceof AstForStatement) {
			if(((AstForStatement) parent).get_initializer() == location)
				return (AstStatement) parent;
			else if(((AstForStatement) parent).get_condition() == location)
				return (AstStatement) parent;
			else return (AstStatement) location;
		}
		else return (AstStatement) location;
	}
	
	/**
	 * insert jcm_traps() in the compound statement.
	 * @param location
	 * @throws Exception
	 */
	protected void trap_in_compound_statement(AstCompoundStatement location) throws Exception {
		int beg = location.get_lbrace().get_location().get_bias();
		beg += location.get_lbrace().get_location().get_length();
		int end = location.get_rbrace().get_location().get_bias();
		CText text = location.get_tree().get_source_code(); 
		
		/** from head until { (included) **/
		for(int k = 0; k < beg; k++) {
			this.buffer.append(text.get_char(k));
		}
		
		/** trap(); statement_list **/
		this.buffer.append(STRPCodeGenerator.trap_on_invocation);
		for(int k = beg; k < end; k++) {
			this.buffer.append(text.get_char(k));
		}
		
		/** from } (included) until EOF **/
		for(int k = end; k < text.length(); k++) {
			this.buffer.append(text.get_char(k));
		}
	}
	
	/**
	 * whether the statement is an element in { ... }
	 * @param statement
	 * @return
	 */
	protected boolean is_compound_element(AstStatement statement) {
		return statement.get_parent() instanceof AstCompoundStatement;
	}
	
	/**
	 * whether the statement is labeled | case | default
	 * @param statement
	 * @return
	 */
	protected boolean is_labeled_statement(AstStatement statement) {
		return statement instanceof AstLabeledStatement
				|| statement instanceof AstCaseStatement
				|| statement instanceof AstDefaultStatement;
	}
	
	/**
	 * insert jcm_traps() before or after the statement
	 * @param location
	 * @throws Exception
	 */
	protected void trap_in_normal_statement(AstStatement location) throws Exception {
		int beg = location.get_location().get_bias();
		int end = beg + location.get_location().get_length();
		CText text = location.get_tree().get_source_code(); 
		
		for(int k = 0; k < beg; k++) {
			this.buffer.append(text.get_char(k));
		}
		
		if(!this.is_compound_element(location))
			this.buffer.append("{");
		
		if(this.is_labeled_statement(location)) {
			for(int k = beg; k < end; k++) {
				this.buffer.append(text.get_char(k));
			}
			this.buffer.append(STRPCodeGenerator.trap_on_invocation);
		}
		else {
			this.buffer.append(STRPCodeGenerator.trap_on_invocation);
			for(int k = beg; k < end; k++) {
				this.buffer.append(text.get_char(k));
			}
		}
		
		if(!this.is_compound_element(location))
			this.buffer.append("}");
		
		for(int k = end; k < text.length(); k++) {
			this.buffer.append(text.get_char(k));
		}
		
	}

	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstStatement statement = this.find_trapped_statement(mutation.get_location());
		
		if(statement instanceof AstCompoundStatement) {
			this.trap_in_compound_statement((AstCompoundStatement) statement);
		}
		else {
			this.trap_in_normal_statement(statement);
		}
	}
	
	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		this.generate_coverage_code(mutation);
	}
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		this.generate_coverage_code(mutation);
	}

}
