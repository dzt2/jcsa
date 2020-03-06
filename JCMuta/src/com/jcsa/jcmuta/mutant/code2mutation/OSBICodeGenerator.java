package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.text.CText;

public class OSBICodeGenerator extends STRPCodeGenerator {
	
	protected static final String inserted_code = "{ /* inserted block */ }";

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstStatement statement = (AstStatement) mutation.get_location();
		int index = statement.get_location().get_bias();
		CText text = statement.get_tree().get_source_code();
		
		for(int k = 0; k < index; k++) buffer.append(text.get_char(k));
		
		this.buffer.append(inserted_code).append("\n\t");
		
		for(int k = index; k < text.length(); k++) buffer.append(text.get_char(k));
	}

}
