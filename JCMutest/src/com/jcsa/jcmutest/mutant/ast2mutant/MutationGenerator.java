package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializerBody;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * It provides interface to seed syntactic mutations in source code based on
 * the mutation operators as provided.
 * 
 * @author yukimula
 *
 */
public abstract class MutationGenerator {
	
	/* generation methods */
	/**
	 * initialize the generator state when a new function is put in
	 * @param function
	 * @throws Exception
	 */
	protected abstract void initialize(AstFunctionDefinition function) throws Exception;
	/**
	 * @param location
	 * @return whether the location is available for seeding mutation of specified class in
	 * @throws Exception
	 */
	protected abstract boolean available(AstNode location) throws Exception;
	/**
	 * generate the mutations in available location and put them in the tail of the mutations list
	 * @param location
	 * @param mutations
	 * @throws Exception
	 */
	protected abstract void generate(AstNode location, List<AstMutation> mutations) throws Exception;
	/**
	 * @param function
	 * @param locations the candidates in which mutations are going to be seeded
	 * @return the mutations of specific class seeded within the function's body
	 * @throws Exception
	 */
	protected List<AstMutation> generate(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {
		this.initialize(function);
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(AstNode location : locations) {
			if(this.available(location)) {
				this.generate(location, mutations);
			}
		}
		return mutations;
	}
	
	/* utility methods */
	/**
	 * @param location
	 * @return whether the type of the expression is numeric {bool, char, short, int, long, float,
	 * 		   double, enum} such that it can be used as the 
	 * @throws Exception
	 */
	protected boolean is_numeric_expression(AstNode location) throws Exception {
		if(location instanceof AstInitializerBody) {
			return false;
		}
		else if(location instanceof AstExpression) {
			CType data_type = ((AstExpression) location).get_value_type();
			data_type = CTypeAnalyzer.get_value_type(data_type);
			
			if(data_type instanceof CBasicType) {
				switch(((CBasicType) data_type).get_tag()) {
				case c_bool:
				case c_char:
				case c_uchar:
				case c_short:
				case c_ushort:
				case c_int:
				case c_uint:
				case c_long:
				case c_ulong:
				case c_llong:
				case c_ullong:
				case c_float:
				case c_double:
				case c_ldouble:	return true;
				default: return false;
				}
			}
			else if(data_type instanceof CEnumType) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	// TODO for more utility method used here...
	
	
}
