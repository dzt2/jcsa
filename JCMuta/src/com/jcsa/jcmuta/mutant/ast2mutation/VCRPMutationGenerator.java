package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
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
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class VCRPMutationGenerator extends AstMutationGenerator {
	
	private Map<String, AstConstant> constants = new HashMap<String, AstConstant>();
	
	@Override
	protected void open(AstTree ast_tree) throws Exception {
		super.open(ast_tree); constants.clear();
		
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		ast_queue.add(ast_tree.get_ast_root());
		
		while(!ast_queue.isEmpty()) {
			AstNode ast_node = ast_queue.poll();
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				AstNode child = ast_node.get_child(k);
				if(child != null) ast_queue.add(child);
			}
			
			if(ast_node instanceof AstConstant) {
				CConstant constant = ((AstConstant) ast_node).get_constant();
				
				String key;
				switch(constant.get_type().get_tag()) {
				case c_char: case c_uchar:
					key = "" + ((int) constant.get_char());
					break;
				case c_short: case c_ushort:
				case c_int: case c_uint:
					key = constant.get_integer().toString();
					break;
				case c_long: case c_ulong:
				case c_llong: case c_ullong:
					key = constant.get_long().toString();
					break;
				case c_float:
					key = constant.get_float().toString();
					break;
				case c_double:
				case c_ldouble:
					key = constant.get_double().toString();
					break;
				default: throw new IllegalArgumentException("Invalid constant");
				}
				
				this.constants.put(key, (AstConstant) ast_node);
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
		CType data_type = CTypeAnalyzer.
				get_value_type(expression.get_value_type());
		
		if(CTypeAnalyzer.is_integer(data_type)) {
			for(AstConstant replace : this.constants.values()) {
				CType ctype = CTypeAnalyzer.get_value_type(replace.get_value_type());
				if(CTypeAnalyzer.is_integer(ctype)) {
					mutations.add(AstMutation.VCRP(expression, replace.get_constant()));
				}
			}
		}
		else if(CTypeAnalyzer.is_real(data_type)) {
			for(AstConstant replace : this.constants.values()) {
				CType ctype = CTypeAnalyzer.get_value_type(replace.get_value_type());
				if(CTypeAnalyzer.is_real(ctype)) {
					mutations.add(AstMutation.VCRP(expression, replace.get_constant()));
				}
			}
		}
	}

}
