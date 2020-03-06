package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatementList;
import com.jcsa.jcparse.lang.text.CText;

public class SBCICodeGenerator extends STRPCodeGenerator {
	
	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		AstStatement statement = this.
				find_trapped_statement(mutation.get_location());
		CText text = statement.get_tree().get_source_code();
		
		String replace;
		switch(mutation.get_mutation_operator()) {
		case ins_break:		replace = "break;";	 	break;
		case ins_continue:	replace = "continue;";	break;
		default: throw new IllegalArgumentException("Invalid operator");
		}
		
		if(statement.get_parent() instanceof AstStatementList) {
			int beg = statement.get_location().get_bias();
			for(int k = 0; k < beg; k++) 
				this.buffer.append(text.get_char(k));
			
			this.buffer.append(" ");
			this.buffer.append(replace);
			this.buffer.append(" ");
			
			for(int k = beg; k < text.length(); k++) 
				this.buffer.append(text.get_char(k));
		}
		else {
			int beg = statement.get_location().get_bias();
			int end = beg + statement.get_location().get_length();
			
			for(int k = 0; k < beg; k++) 
				this.buffer.append(text.get_char(k));
			
			this.buffer.append("{ ");
			this.buffer.append(replace);
			this.buffer.append(" ");
			for(int k = beg; k < end; k++)
				this.buffer.append(text.get_char(k));
			this.buffer.append(" }");
			
			for(int k = end; k < text.length(); k++) 
				this.buffer.append(text.get_char(k));
		}
	}

}
