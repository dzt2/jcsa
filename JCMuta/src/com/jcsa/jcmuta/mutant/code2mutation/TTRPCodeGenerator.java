package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.text.CText;

public class TTRPCodeGenerator extends MutaCodeGenerator {
	
	/**
	 * generate code until jcm_init_trap_timer(times)
	 * @param statement
	 * @param times
	 * @throws Exception
	 */
	private void init_trap_timmer(AstStatement statement, int times) throws Exception {
		int index = statement.get_location().get_bias();
		CText text = statement.get_tree().get_source_code();
		
		for(int k = 0; k < index; k++) {
			this.buffer.append(text.get_char(k));
		}
		
		this.buffer.append(" ");
		this.buffer.append(MutaCodeGenerator.mutant_line_comment);
		this.buffer.append(" ");
		this.buffer.append(String.format(MutaCodeTemplates.ini_trap_timer_template, times));
		this.buffer.append(" ");
	}
	
	/**
	 * append the code between body and loop statement
	 * @param statement
	 * @param body
	 * @throws Exception
	 */
	private void append_condition(AstStatement statement, AstStatement body) throws Exception {
		int index1 = statement.get_location().get_bias();
		int index2 = body.get_location().get_bias();
		CText text = statement.get_tree().get_source_code();
		
		for(int k = index1; k < index2; k++) {
			this.buffer.append(text.get_char(k));
		}
	}
	
	/**
	 * generate the code since body including jcm_incre_trap_timmer();
	 * @param body
	 * @throws Exception
	 */
	private void insert_trap_timmer(AstStatement body) throws Exception {
		AstNode statement_body;
		if(body instanceof AstCompoundStatement) {
			if(((AstCompoundStatement) body).has_statement_list()) {
				statement_body = ((AstCompoundStatement) body).get_statement_list();
			}
			else {
				statement_body = null;
			}
		}
		else statement_body = body;
		
		this.buffer.append("{ ");
		this.buffer.append(MutaCodeGenerator.mutant_line_comment);
		this.buffer.append(" ");
		this.buffer.append(MutaCodeTemplates.inc_trap_timer_template);
		if(statement_body != null)
			this.buffer.append(statement_body.get_location().read());
		this.buffer.append(" }");
		
		CText text = body.get_tree().get_source_code();
		int end = body.get_location().get_bias();
		end = end + body.get_location().get_length();
		for(int k = end; k < text.length(); k++) {
			this.buffer.append(text.get_char(k));
		}
	}
	
	private void generate_trap_timmer(AstStatement statement, int times) throws Exception {
		AstStatement body;
		if(statement instanceof AstWhileStatement) {
			body = ((AstWhileStatement) statement).get_body();
		}
		else if(statement instanceof AstDoWhileStatement) {
			body = ((AstDoWhileStatement) statement).get_body();
		}
		else if(statement instanceof AstForStatement) {
			body = ((AstForStatement) statement).get_body();
		}
		else throw new IllegalArgumentException(statement.toString());
		
		this.init_trap_timmer(statement, times);
		this.append_condition(statement, body);
		this.insert_trap_timmer(body);
	}

	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstStatement statement = (AstStatement) mutation.get_location();
		this.generate_trap_timmer(statement, 1);
	}

	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		AstStatement statement = (AstStatement) mutation.get_location();
		this.generate_trap_timmer(statement, (int) mutation.get_parameter());
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		this.generate_weakness_code(mutation);
	}

}
