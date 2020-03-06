package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstScopeNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstArrayExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.centity.CInstance;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CNameTable;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;

public class VRRPMutationGenerator extends AstMutationGenerator {
	
	private Map<String, CName> cnames = new HashMap<String, CName>();
	
	private void collect_cnames(AstNode location, CType data_type) throws Exception {
		cnames.clear();
		while(location != null) {
			if(location instanceof AstScopeNode) {
				CScope scope = ((AstScopeNode) location).get_scope();
				while(scope != null) {
					
					CNameTable table = scope.get_name_table();
					Iterator<String> names = table.get_names();
					while(names.hasNext()) {
						CName cname = table.get_name(names.next());
						if(!cnames.containsKey(cname.get_name())) {
							CInstance instance;
							if(cname instanceof CInstanceName) {
								instance = ((CInstanceName) cname).get_instance();
							}
							else if(cname instanceof CParameterName) {
								instance = ((CParameterName) cname).get_parameter();
							}
							else instance = null;
							
							if(instance != null) {
								CType vtype = CTypeAnalyzer.get_value_type(instance.get_type());
								if(vtype == data_type) {
									cnames.put(cname.get_name(), cname);
								}
							}
						}
					}
					
					scope = scope.get_parent();
				}
			}
		}
	}
	
	private boolean is_left_reference(AstNode location) throws Exception {
		AstNode parent = location.get_parent();
		if(parent instanceof AstAssignExpression
			|| parent instanceof AstBitwiseAssignExpression
			|| parent instanceof AstArithAssignExpression
			|| parent instanceof AstShiftAssignExpression) {
			AstBinaryExpression expr = (AstBinaryExpression) parent;
			return expr.get_loperand() == location;
		}
		else if(parent instanceof AstIncreUnaryExpression
				|| parent instanceof AstIncrePostfixExpression) {
			return true;
		}
		else if(parent instanceof AstFieldExpression) {
			return ((AstFieldExpression) parent).get_operator().get_punctuator() == CPunctuator.dot;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected void collect_locations(AstNode location, Collection<AstNode> locations) throws Exception {
		if(location instanceof AstIdExpression
			|| location instanceof AstConstant
			|| location instanceof AstArrayExpression
			|| location instanceof AstFieldExpression) {
			if(!this.is_left_reference(location))
				locations.add(location);
		}
		else if(location instanceof AstPointUnaryExpression) {
			if(((AstPointUnaryExpression) location).get_operator().get_operator() == COperator.dereference) {
				locations.add(location);
			}
		}
	}
	
	@Override
	protected void generate_mutations(AstNode location, Collection<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_value_type());
		this.collect_cnames(expression, data_type);
		for(CName replace : this.cnames.values()) {
			if(location instanceof AstIdExpression) {
				if(((AstIdExpression) location).get_name().equals(replace.get_name()))
					continue;
			}
			mutations.add(AstMutation.VRRP(expression, replace));
		}
	}

}
