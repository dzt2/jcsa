package com.jcsa.jcmuta.mutant.orig2mutation;

import java.util.LinkedList;
import java.util.Queue;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;

import __backup__.TextMutation;

public class CCCR2MutaTranslator implements Text2MutaTranslator {
	
	private CConstant get_parameter(TextMutation mutation) throws Exception {
		String replace = mutation.get_replace();
		int beg = replace.lastIndexOf('(');
		int end = replace.lastIndexOf(')');
		String parameter = replace.substring(beg + 1, end).strip();
		
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		ast_queue.add(mutation.get_origin().get_tree().get_ast_root());
		
		while(!ast_queue.isEmpty()) {
			AstNode ast_node = ast_queue.poll();
			if(ast_node instanceof AstConstant) {
				String key = ast_node.get_location().read();
				if(key.equals(parameter)) 
					return ((AstConstant) ast_node).get_constant();
			}
			
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				AstNode child = ast_node.get_child(k);
				if(child != null) ast_queue.add(child); 
			}
		}
		
		throw new IllegalArgumentException("Unable to find: " + parameter);
	}
	
	@Override
	public AstMutation parse(TextMutation mutation) throws Exception {
		AstExpression expression = (AstExpression) mutation.get_origin();
		expression = CTypeAnalyzer.get_expression_of(expression);
		CConstant parameter = this.get_parameter(mutation);
		return AstMutation.VCRP(expression, parameter);
	}

}
