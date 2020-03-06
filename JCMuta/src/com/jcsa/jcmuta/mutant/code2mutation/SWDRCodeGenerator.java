package com.jcsa.jcmuta.mutant.code2mutation;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.text.CText;

public class SWDRCodeGenerator extends STRPCodeGenerator {

	@Override
	protected void generate_weakness_code(AstMutation mutation) throws Exception {
		/* 1. get the condition to trap in weak mutation */
		AstNode location = mutation.get_location();
		AstExpression condition;
		CText text = location.get_tree().get_source_code();
		if(location instanceof AstWhileStatement) 
			condition = ((AstWhileStatement) location).get_condition();
		else 
			condition = ((AstDoWhileStatement) location).get_condition();
		condition = CTypeAnalyzer.get_expression_of(condition);
		
		/* 2. previous code segment */
		int index1 = location.get_location().get_bias();
		for(int k = 0; k < index1; k++) this.buffer.append(text.get_char(k));
		this.buffer.append(String.format(MutaCodeTemplates.ini_trap_timer_template, 0));
		this.buffer.append(" ");
		
		/* 3. from while to condition */
		int index2 = condition.get_location().get_bias();
		for(int k = index1; k < index2; k++) buffer.append(text.get_char(k));
		
		/* 4. replace the condition */
		String expr_code = condition.get_location().read();
		String replace = String.format(MutaCodeTemplates.
				trap_on_condition1_template, expr_code, 1);
		this.buffer.append(replace);
		
		/* 5. following the condition */
		int index3 = index2 + condition.get_location().get_length();
		for(int k = index3; k < text.length(); k++) buffer.append(text.get_char(k));
	}

	@Override
	protected void generate_stronger_code(AstMutation mutation) throws Exception {
		CText text = mutation.get_location().get_tree().get_source_code();
		int beg = mutation.get_location().get_location().get_bias();
		int end = beg + mutation.get_location().get_location().get_length();
		
		/* before loops */
		for(int k = 0; k < beg; k++) buffer.append(text.get_char(k));
		
		/* while --> do...while */
		if(mutation.get_location() instanceof AstWhileStatement) {
			AstWhileStatement statement = (AstWhileStatement) mutation.get_location();
			String condition = statement.get_condition().get_location().read();
			String body = statement.get_body().get_location().read();
			
			this.buffer.append(" do ");
			this.buffer.append(body);
			this.buffer.append(" while(");
			this.buffer.append(condition);
			this.buffer.append(");");
		}
		/* do...while ==> while */
		else {
			AstDoWhileStatement statement = (AstDoWhileStatement) mutation.get_location();
			String condition = statement.get_condition().get_location().read();
			String body = statement.get_body().get_location().read();
			
			this.buffer.append(" while(");
			this.buffer.append(condition);
			this.buffer.append(") ");
			this.buffer.append(body);
		}
		
		/* after the loops */
		for(int k = end; k < text.length(); k++) buffer.append(text.get_char(k));
	}
	
	
	
}
