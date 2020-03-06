package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.stmt.AstLabel;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class SGLRCodeGenerator extends STRPCodeGenerator {

	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstStatement statement = (AstStatement) mutation.get_location().get_parent();
		this.trap_in_normal_statement(statement);
	}

	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		this.generate_coverage_code(mutation);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstLabel parameter = (AstLabel) mutation.get_parameter();
		this.replace_muta_code(mutation.
				get_location(), parameter.get_location().read());
	}

}
