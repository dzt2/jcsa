package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.text.CText;

public class OIFRCodeGenerator extends STRPCodeGenerator {
	
	private static final String delete_block = " /* deleted block */ ";
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstIfStatement statement = (AstIfStatement) mutation.get_location();
		int beg = statement.get_location().get_bias();
		int end = statement.get_true_branch().get_location().get_bias();
		CText text = statement.get_tree().get_source_code();
		
		for(int k = 0; k < beg; k++) buffer.append(text.get_char(k));
		this.buffer.append(delete_block);
		for(int k = end; k < end; k++) buffer.append(text.get_char(k));
	}

}
