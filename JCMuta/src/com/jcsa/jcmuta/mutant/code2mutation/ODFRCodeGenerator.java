package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;

public class ODFRCodeGenerator extends STRPCodeGenerator {
	
	private static final String replacement = "default: /* mutated line */";
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		this.replace_muta_code(mutation.get_location(), replacement);
	}

}
