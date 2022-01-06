package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class VCRPMutationGenerator extends MutationGenerator {

	private Map<String, CConstant> constants = new HashMap<>();

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {
		this.constants.clear();
		for(AstNode location : locations) {
			if(location instanceof AstConstant) {
				CConstant constant = this.standard_constant(
						((AstConstant) location).get_constant());
				constants.put(constant.toString(), constant);
			}
		}
	}

	@Override
	protected boolean available(AstNode location) throws Exception {
		return location instanceof AstConstant && this.is_valid_context(location);
	}

	private CConstant standard_constant(CConstant source) throws Exception {
		CConstant target = new CConstant();
		switch(source.get_type().get_tag()) {
		case c_bool:
		{
			if(source.get_bool()) {
				target.set_long(1);
			}
			else {
				target.set_long(0);
			}
			break;
		}
		case c_char:
		case c_uchar:
		{
			target.set_long(source.get_char().charValue());
			break;
		}
		case c_short:
		case c_ushort:
		case c_int:
		case c_uint:
		{
			target.set_long(source.get_integer().longValue());
			break;
		}
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
		{
			target.set_long(source.get_long().longValue());
			break;
		}
		case c_float:
		{
			target.set_double(source.get_float().doubleValue());
			break;
		}
		case c_double:
		case c_ldouble:
		{
			target.set_double(source.get_double().doubleValue());
			break;
		}
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
		return target;
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstConstant source = (AstConstant) location;
		for(CConstant target : this.constants.values()) {
			if(CTypeAnalyzer.is_integer(source.get_value_type())
				&& CTypeAnalyzer.is_integer(target.get_type())) {
				long source_value = this.standard_constant(source.get_constant()).get_long();
				if(source_value != target.get_long().longValue()) {
					mutations.add(AstMutations.VCRP(source, target.get_long().longValue()));
				}
			}
			else if(CTypeAnalyzer.is_real(source.get_value_type())
				&& CTypeAnalyzer.is_real(target.get_type())) {
				double source_value = this.standard_constant(source.get_constant()).get_double();
				if(source_value != target.get_double().doubleValue()) {
					mutations.add(AstMutations.VCRP(source, target.get_double().doubleValue()));
				}
			}
		}
	}

}
