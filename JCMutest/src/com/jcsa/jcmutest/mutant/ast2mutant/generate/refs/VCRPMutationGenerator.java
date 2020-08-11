package com.jcsa.jcmutest.mutant.ast2mutant.generate.refs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class VCRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstConstant || 
				location instanceof AstFunctionDefinition;
	}
	
	/** the constants in local function body **/
	private Map<String, AstConstant> local_constants = new HashMap<String, AstConstant>();
	
	/**
	 * @param constant
	 * @return key of the numeric constant
	 * @throws Exception
	 */
	private String constant_key(CConstant constant) throws Exception {
		switch(constant.get_type().get_tag()) {
		case c_bool:
		{
			if(constant.get_bool().booleanValue()) {
				return "1";
			}
			else {
				return "0";
			}
		}
		case c_char:
		case c_uchar:
		{
			Integer value = Integer.valueOf(constant.get_char().charValue());
			return value.toString();
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			return constant.get_integer().toString();
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			return constant.get_long().toString();
		}
		case c_float:
		{
			return constant.get_float().toString();
		}
		case c_double:
		case c_ldouble:
		{
			return constant.get_double().toString();
		}
		default: throw new IllegalArgumentException("Invalid type: " + constant);
		}
	}
	
	/**
	 * @param function
	 * @return all the constants in function's body
	 * @throws Exception
	 */
	private void update_local_constants(AstFunctionDefinition function) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function.get_body());
		this.local_constants.clear();
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			if(node instanceof AstConstant) {
				AstConstant constant = (AstConstant) node;
				this.local_constants.put(
						this.constant_key(constant.get_constant()), 
						constant);
			}
		}
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		if(location instanceof AstFunctionDefinition) {
			this.update_local_constants((AstFunctionDefinition) location);
		}
		else {
			AstConstant source = (AstConstant) location;
			String skey = this.constant_key(source.get_constant());
			CBasicType stype = source.get_constant().get_type();
			for(String tkey : this.local_constants.keySet()) {
				AstConstant target = this.local_constants.get(tkey);
				if(!skey.equals(tkey)) {
					CBasicType ttype = target.get_constant().get_type();
					if(CTypeAnalyzer.is_character(stype)) {
						switch(ttype.get_tag()) {
						case c_char:
						case c_uchar:
							mutations.add(AstMutations.VCRP(source, target.
									get_constant().get_char().charValue()));
						default: break;
						}
					}
					else if(CTypeAnalyzer.is_integer(stype)) {
						switch(ttype.get_tag()) {
						case c_short:
						case c_ushort:
						case c_int:
						case c_uint:
						{
							mutations.add(AstMutations.VCRP(source, target.
									get_constant().get_integer().intValue()));
							break;
						}
						case c_long:
						case c_llong:
						case c_ulong:
						{
							mutations.add(AstMutations.VCRP(source, target.
									get_constant().get_long().longValue()));
						}
						default: break;
						}
					}
					else if(CTypeAnalyzer.is_real(stype)) {
						switch(ttype.get_tag()) {
						case c_float:
						{
							mutations.add(AstMutations.VCRP(source, target.
									get_constant().get_float().doubleValue()));
							break;
						}
						case c_double:
						case c_ldouble:
						{
							mutations.add(AstMutations.VCRP(source, target.
									get_constant().get_double().doubleValue()));
							break;
						}
						default: break;
						}
					}
				}
			}
		}
	}

}
