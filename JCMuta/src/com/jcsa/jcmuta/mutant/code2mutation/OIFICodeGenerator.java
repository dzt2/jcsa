package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.stmt.AstIfStatement;
import com.jcsa.jcparse.lang.text.CText;

public class OIFICodeGenerator extends STRPCodeGenerator {
	
	private static final String delete_block = " /* deleted block */ ";
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstIfStatement statement = (AstIfStatement) mutation.get_location();
		AstIfStatement parent = (AstIfStatement) statement.get_parent();
		CText text = statement.get_tree().get_source_code();
		
		int index1 = parent.get_else().get_location().get_bias();
		int index2 = parent.get_else().get_location().get_length() + index1;
		
		for(int k = 0; k < index1; k++) buffer.append(text.get_char(k));
		this.buffer.append(delete_block);
		for(int k = index2; k < text.length(); k++) buffer.append(text.get_char(k));
	}

}
