package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;

public class STDLCodeGenerator extends STRPCodeGenerator {
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		this.replace_muta_code(mutation.get_location(), ";");
	}

}
