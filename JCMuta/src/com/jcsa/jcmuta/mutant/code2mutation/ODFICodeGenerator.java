package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstSwitchStatement;
import com.jcsa.jcparse.lang.text.CText;

public class ODFICodeGenerator extends STRPCodeGenerator {
	
	private static final String trap_default = "\n\tdefault: jcm_traps(); break;\n\t";
	private static final String default_statement = "\n\t\tdefault: /* empty default case */ break;\n\t";

	@Override
	protected void generate_coverage_code(AstMutation mutation) throws Exception {
		AstSwitchStatement statement = (AstSwitchStatement) mutation.get_location();
		AstCompoundStatement body = (AstCompoundStatement) statement.get_body();
		
		int index = body.get_rbrace().get_location().get_bias();
		CText text = statement.get_tree().get_source_code();
		
		for(int k = 0; k < index; k++) buffer.append(text.get_char(k));
		this.buffer.append(trap_default);
		for(int k = index; k < text.length(); k++) buffer.append(text.get_char(k));
	}

	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		this.generate_coverage_code(mutation);
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstSwitchStatement statement = (AstSwitchStatement) mutation.get_location();
		AstCompoundStatement body = (AstCompoundStatement) statement.get_body();
		
		int index = body.get_rbrace().get_location().get_bias();
		CText text = statement.get_tree().get_source_code();
		
		for(int k = 0; k < index; k++) buffer.append(text.get_char(k));
		this.buffer.append(default_statement);
		for(int k = index; k < text.length(); k++) buffer.append(text.get_char(k));
	}

}
