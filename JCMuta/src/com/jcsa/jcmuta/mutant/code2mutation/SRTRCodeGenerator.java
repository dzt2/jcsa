package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class SRTRCodeGenerator extends STRPCodeGenerator {
	
	private AstStatement get_statement(AstNode location) throws Exception {
		while(location != null) {
			if(location instanceof AstStatement)
				return (AstStatement) location;
			else location = location.get_parent();
		}
		throw new IllegalArgumentException("Invalid location");
	}
	
	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstStatement statement = this.get_statement(mutation.get_location());
		this.trap_in_normal_statement(statement);
	}

	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		AstExpression source = (AstExpression) mutation.get_location();
		AstExpression target = (AstExpression) mutation.get_parameter();
		
		String replace = String.format(MutaCodeTemplates.
				trap_if_different_template, 
				source.get_location().read(), 
				target.get_location().read());
		this.replace_muta_code(source, replace);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstExpression source = (AstExpression) mutation.get_location();
		AstExpression target = (AstExpression) mutation.get_parameter();
		this.replace_muta_code(source, target.get_location().read());
	}

}
