package com.jcsa.jcmutest.mutant.ctx2mutant.oprt;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcparse.lang.lexical.COperator;

public class CirOperatorMutationParsers {
	
	private static final Map<COperator, CirOperatorMutationParser> parsers = new HashMap<COperator, CirOperatorMutationParser>();
	static {
		parsers.put(COperator.arith_add, 	new CirArithAddMutationParser());
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
