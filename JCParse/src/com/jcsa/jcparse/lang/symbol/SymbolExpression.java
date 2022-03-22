package com.jcsa.jcparse.lang.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;

/**
 * 	<code>
 * 	|--	SymbolExpression					[data_type: CType]					<br>
 * 	|--	|--	SymbolBasicExpression			(basic expression as the leaf node)	<br>
 * 	|--	|--	|--	SymbolIdentifier			[name: String; scope: Object;]		<br>
 * 	|--	|--	|--	SymbolConstant				[constant: CConstant]				<br>
 * 	|--	|--	|--	SymbolLiteral				[literal: String]					<br>
 * 	|--	|--	SymbolCompositeExpression		(comp_expr --> operator expression)	<br>
 * 	|--	|--	|--	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(relate)[grt, gre, smt, sme, neq...]<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(assign)[ass, pss]					<br>
 * 	|--	|--	SymbolSpecialExpression												<br>
 * 	|--	|--	|--	SymbolCastExpression		(cast_expr --> {type_name} expr)	<br>
 * 	|--	|--	|--	SymbolCallExpression		(call_expr --> expr arg_list)		<br>
 * 	|--	|--	|--	SymbolConditionExpression	(cond_expr --> expr ? expr : expr)	<br>
 * 	|--	|--	|--	SymbolInitializerList		(init_list --> {expr (, expr)*})	<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public abstract class SymbolExpression extends SymbolNode {
	
	/** data type of the expression value **/
	private	CType data_type;
	
	/**
	 * General constructor for symbolic expression with specified data type
	 * @param _class
	 * @param type
	 * @throws Exception
	 */
	protected SymbolExpression(SymbolClass _class, CType type) throws Exception {
		super(_class);
		this.data_type = SymbolFactory.get_type(type);
	}
	
	/**
	 * @return the data type of expression value
	 */
	public CType get_data_type() { return this.data_type; }
	
	/**
	 * @param in_state		the state-context to provide the inputs
	 * @param ou_state		the state-context to preserve an output
	 * @return				the resulting expression from the input
	 * @throws Exception
	 */
	public SymbolExpression	evaluate(SymbolProcess in_state, SymbolProcess ou_state) throws Exception {
		return SymbolEvaluator.evaluate(this, in_state, ou_state);
	}
	
	/**
	 * @return the set of reference-expressions used in this expression
	 */
	public List<SymbolExpression> get_variables() {
		Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
		List<SymbolExpression> variables = new ArrayList<SymbolExpression>();
		queue.add(this);
		while(!queue.isEmpty()) {
			SymbolNode parent = queue.poll();
			if(parent.is_refer_type() && parent instanceof SymbolExpression) {
				variables.add((SymbolExpression) parent);
			}
			for(SymbolNode child : parent.get_children()) {
				queue.add(child);
			}
		}
		return variables;
	}
	
	/**
	 * It replaces the variables used in this expression by the given values 
	 * @param name_value_map	map from source expression to the value that replaces with it.
	 * @return					the expression after replacing the specified variables in map.
	 * @throws Exception
	 */
	protected abstract SymbolExpression symb_replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception;
	
	/**
	 * It replaces the variables used in this expression by the given values 
	 * @param name_value_map	map from source expression to the value that replaces with it.
	 * @return					the expression after replacing the specified variables in map.
	 * @throws Exception
	 */
	public SymbolExpression replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception {
		if(name_value_map == null || name_value_map.isEmpty()) {
			return (SymbolExpression) this.clone();
		}
		else {
			return this.symb_replace(name_value_map);
		}
	}
	
	/**
	 * @param variable
	 * @return the normalized version of variable
	 * @throws Exception
	 */
	private	SymbolExpression normalize(SymbolExpression variable, Set<String> names) throws Exception {
		if(variable == null || !variable.is_refer_type()) {
			throw new IllegalArgumentException("Invalid variable: null");
		}
		else {
			if(variable instanceof SymbolIdentifier) {
				String name = ((SymbolIdentifier) variable).get_name();
				if(SymbolFactory.special_names.contains(name)) {
					return variable;
				}
			}
			
			CType type = SymbolFactory.get_type(variable.get_data_type());
			String name;
			if(type instanceof CBasicType) {
				name = ((CBasicType) type).get_tag().name();
				if(name.startsWith("c_")) {
					name = name.substring(2).strip();
				}
			}
			else if(type instanceof CArrayType) {
				name = "array";
			}
			else if(type instanceof CPointerType) {
				name = "point";
			}
			else if(type instanceof CStructType) {
				name = ((CStructType) type).get_name();
				if(name == null) {
					name = "struct";
				}
				else if(name.startsWith("struct")) {
					name = name.substring(6).strip();
				}
				if(name.isEmpty()) {
					name = "struct";
				}
			}
			else if(type instanceof CUnionType) {
				name = ((CStructType) type).get_name();
				if(name == null) {
					name = "union";
				}
				else if(name.startsWith("union")) {
					name = name.substring(5).strip();
				}
				if(name.isEmpty()) {
					name = "union";
				}
			}
			else if(type instanceof CEnumType) {
				name = "int";
			}
			else {
				name = "auto";
			}
			
			for(int code = 0; code < Integer.MAX_VALUE; code++) {
				String identifier = name + "#" + code;
				if(!names.contains(identifier)) {
					names.add(identifier);
					return SymbolFactory.identifier(type, name, code);
				}
			}
			throw new IllegalArgumentException("Out-of-Name-Range");
		}
	}
	
	/**
	 * @return the normalized version of the expression
	 * @throws Exception
	 */
	public SymbolExpression	normalize() throws Exception {
		/* 1. derive the variables and their values */
		List<SymbolExpression> variables = this.get_variables();
		Map<SymbolExpression, SymbolExpression> name_value_map = 
				new HashMap<SymbolExpression, SymbolExpression>();
		Set<String> identifiers = new HashSet<String>();
		for(SymbolExpression variable : variables) {
			if(!name_value_map.containsKey(variable)) {
				SymbolExpression norm_variable = this.normalize(variable, identifiers);
				name_value_map.put(variable, norm_variable);
			}
		}
		
		/* 2. it performs the name-value-replacement */
		return this.replace(name_value_map);
	}
	
}
