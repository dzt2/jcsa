package com.jcsa.jcmutest.mutant.fil2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class TTRPMutationTextParser extends MutationTextParser {

	@Override
	protected AstNode get_location(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		return statement;
	}

	private String ini_code(int loop_time) {
		return "jcm_init_loop_counter(" + loop_time + ");\n";
	}

	private String mut_body(AstStatement body) throws Exception {
		if(body instanceof AstCompoundStatement) {
			AstCompoundStatement stmt = (AstCompoundStatement) body;
			if(stmt.has_statement_list()) {
				return "{ jcm_decre_loop_counter(); " + stmt.
						get_statement_list().generate_code() + " }";
			}
			else {
				return "jcm_decre_loop_counter();";
			}
		}
		else {
			return "{ jcm_decre_loop_counter(); " + body.generate_code() + " }";
		}
	}

	@Override
	protected String get_muta_code(AstMutation source, AstNode location) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		int loop_time = ((Integer) source.get_parameter()).intValue();
		String init_code = this.ini_code(loop_time), body_code;

		if(statement instanceof AstWhileStatement) {
			AstWhileStatement loop_stmt = (AstWhileStatement) statement;
			String condition = loop_stmt.get_condition().generate_code();
			body_code = this.mut_body(loop_stmt.get_body());
			return init_code + "while(" + condition + ") " + body_code;
		}
		else if(statement instanceof AstDoWhileStatement) {
			AstDoWhileStatement loop_stmt = (AstDoWhileStatement) statement;
			String condition = loop_stmt.get_condition().generate_code();
			body_code = this.mut_body(loop_stmt.get_body());
			return init_code + "do " + body_code + " while(" + condition + ");";
		}
		else if(statement instanceof AstForStatement) {
			AstForStatement loop_stmt = (AstForStatement) statement;
			String initializer = loop_stmt.get_initializer().generate_code();
			String condition = loop_stmt.get_condition().generate_code();
			String increment = "";
			if(loop_stmt.has_increment()) {
				increment = loop_stmt.get_increment().generate_code();
			}
			body_code = this.mut_body(loop_stmt.get_body());
			return init_code + "for(" + initializer + " " +
					condition + " " + increment + ") " + body_code;
		}
		else {
			throw new IllegalArgumentException("Invalid: " + statement);
		}
	}

}
