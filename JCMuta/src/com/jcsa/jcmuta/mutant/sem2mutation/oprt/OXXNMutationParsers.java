package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OXXNMutationParsers extends SemanticMutationParser {
	
	private Map<COperator, OXXNMutationParser> parser;
	
	public OXXNMutationParsers() {
		this.parser = new HashMap<COperator, OXXNMutationParser>();
		
		this.parser.put(COperator.arith_add, 	new ADDXMutationParser());
		this.parser.put(COperator.arith_sub, 	new SUBXMutationParser());
		this.parser.put(COperator.arith_mul, 	new MULXMutationParser());
		this.parser.put(COperator.arith_div, 	new DIVXMutationParser());
		this.parser.put(COperator.arith_mod, 	new MODXMutationParser());
		
		this.parser.put(COperator.bit_and, 		new BANXMutationParser());
		this.parser.put(COperator.bit_or, 		new BORXMutationParser());
		this.parser.put(COperator.bit_xor, 		new BXRXMutationParser());
		this.parser.put(COperator.left_shift, 	new LSHXMutationParser());
		this.parser.put(COperator.righ_shift, 	new RSHXMutationParser());
		
		this.parser.put(COperator.logic_and, 	new LANXMutationParser());
		this.parser.put(COperator.logic_or, 	new LORXMutationParser());
		
		this.parser.put(COperator.greater_tn, 	new GRTXMutationParser());
		this.parser.put(COperator.greater_eq, 	new GREXMutationParser());
		this.parser.put(COperator.smaller_tn, 	new SMTXMutationParser());
		this.parser.put(COperator.smaller_eq, 	new SMEXMutationParser());
		this.parser.put(COperator.equal_with, 	new EQVXMutationParser());
		this.parser.put(COperator.not_equals, 	new NEQXMutationParser());
	}
	
	private OXXNMutationParser get_parser(AstMutation ast_mutation) throws Exception {
		AstBinaryExpression expression = (AstBinaryExpression) CTypeAnalyzer.
				get_expression_of((AstExpression) ast_mutation.get_location());
		
		COperator operator = expression.get_operator().get_operator();
		if(!this.parser.containsKey(operator)) {
			throw new IllegalArgumentException("Undefined operator: " + operator);
		}
		else { return this.parser.get(operator); }
	}

	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		OXXNMutationParser parser = this.get_parser(ast_mutation);
		parser.set_cir_tree(this.cir_tree);
		return parser.get_statement(ast_mutation);
	}

	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		OXXNMutationParser parser = this.get_parser(ast_mutation);
		parser.set_sem_mutation(this.sem_mutation);
		parser.generate_infections(ast_mutation);
	}

}
