package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;

public class SBCRCodeGenerator extends STRPCodeGenerator {

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		String replace;
		
		switch(mutation.get_mutation_operator()) {
		case break_to_continue:	replace = "continue;";	break;
		case continue_to_break:	replace = "break;";		break;
		default: throw new IllegalArgumentException(
				"Invalid operator: " + mutation.get_mutation_operator());
		}
		
		this.replace_muta_code(mutation.get_location(), replace);
	}

}
