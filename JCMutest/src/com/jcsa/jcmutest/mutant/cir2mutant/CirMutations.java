package com.jcsa.jcmutest.mutant.cir2mutant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParsers;
import com.jcsa.jcmutest.mutant.cir2mutant.utils.CirStateExtender;
import com.jcsa.jcmutest.mutant.cir2mutant.utils.CirStateFeatureWriter;
import com.jcsa.jcmutest.mutant.cir2mutant.utils.CirStateNormalizer;
import com.jcsa.jcmutest.project.MuTestProjectCodeFile;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionEdge;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionPath;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;
import com.jcsa.jcparse.lang.symbol.SymbolIdentifier;
import com.jcsa.jcparse.lang.symbol.SymbolNode;
import com.jcsa.jcparse.lang.symbol.eval.SymbolContext;
import com.jcsa.jcparse.test.file.TestInput;

/**
 * It provides interfaces to support analysis and parsing on the CirMutation.
 * 
 * @author yukimula
 *
 */
public final class CirMutations {
	
	/* definitions */
	/** {true, false} **/
	public static final SymbolExpression bool_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@BoolValue");
	/** true **/
	public static final SymbolExpression true_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@TrueValue");
	/** false **/
	public static final SymbolExpression fals_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@FalsValue");
	/** integer or double **/
	public static final SymbolExpression numb_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NumbValue");
	/** { x | x > 0 } **/
	public static final SymbolExpression post_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@PostValue");
	/** { x | x < 0 } **/
	public static final SymbolExpression negt_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NegtValue");
	/** 0 **/
	public static final SymbolExpression zero_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@ZeroValue");
	/** { x | x <= 0 } **/
	public static final SymbolExpression npos_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NposValue");
	/** { x | x >= 0 } **/
	public static final SymbolExpression nneg_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnegValue");
	/** { x | x != 0 } **/
	public static final SymbolExpression nzro_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NzroValue");
	/** address value in pointer **/
	public static final SymbolExpression addr_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@AddrValue");
	/** null **/
	public static final SymbolExpression null_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NullValue");
	/** {p | p != null} **/
	public static final SymbolExpression nnul_value = SymbolFactory.variable(CBasicTypeImpl.long_type, "@NnulValue");
	/** abstract value of the exception **/
	public static final SymbolExpression trap_value = SymbolFactory.variable(CBasicTypeImpl.bool_type, "@Exception");	
	
	/* exception-included symbolic computation */
	/**
	 * @param root
	 * @return whether the node has trap_value among it.
	 */
	private static boolean has_trap_value(SymbolNode root) {
		Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
		queue.add(root); SymbolNode parent;
		while(!queue.isEmpty()) {
			parent = queue.poll();
			if(parent.is_leaf()) {
				if(parent instanceof SymbolIdentifier
					&& parent.equals(trap_value))
				return true;
			}
			else {
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
			}
		}
		return false;
	}
	/**
	 * @param expression
	 * @param context
	 * @return trap_value iff. arithmetic operation occurs
	 * @throws Exception
	 */
	private static SymbolExpression compute(SymbolExpression expression, SymbolContext context) throws Exception {
		if(expression == null) {												/* input-validate */
			throw new IllegalArgumentException("Invalid expression: null");
		}
		else if(has_trap_value(expression)) {									/* trap at this point */
			return trap_value;
		}
		else {																	/* otherwise, compute */
			try {
				expression = expression.evaluate(context, null);
			}
			catch(ArithmeticException ex) {
				expression = trap_value;
			}
			return expression;
		}
	}
	/**
	 * @param expression
	 * @param context
	 * @return optimized expression from the context or trap_value
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression, SymbolContext context) throws Exception {
		return compute(expression, context);
	}
	/**
	 * @param expression
	 * @return optimized expression from the context or trap_value
	 * @throws Exception
	 */
	public static SymbolExpression evaluate(SymbolExpression expression) throws Exception {
		return compute(expression, null);
	}
	/**
	 * @param expression
	 * @return whether the symbolic expression contains trapping exception results
	 * @throws Exception
	 */
	public static boolean is_trap_value(SymbolExpression expression) throws Exception { return has_trap_value(expression); }
	/**
	 * @param expression
	 * @return whether the expression uses any abstract value within
	 * @throws Exception
	 */
	private static boolean is_abst_value(SymbolExpression expression) throws Exception {
		if(expression == null) {
			return false;
		}
		else {
			return expression.equals(bool_value) || expression.equals(true_value) || expression.equals(fals_value)
					|| expression.equals(post_value) || expression.equals(negt_value) || expression.equals(zero_value)
					|| expression.equals(npos_value) || expression.equals(nneg_value) || expression.equals(nzro_value)
					|| expression.equals(numb_value) || expression.equals(null_value) || expression.equals(nnul_value)
					|| expression.equals(addr_value);
		}
	}
	/**
	 * @param root
	 * @return whether it contains the abstract values defined
	 * @throws Exception
	 */
	public static boolean has_abst_value(SymbolNode root) throws Exception {
		if(root == null) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root); SymbolNode parent;
			while(!queue.isEmpty()) {
				parent = queue.poll();
				if(parent.is_leaf()) {
					if(parent instanceof SymbolIdentifier
						&& is_abst_value((SymbolExpression) parent))
					return true;
				}
				else {
					for(SymbolNode child : parent.get_children()) {
						queue.add(child);
					}
				}
			}
			return false;
		}
	}
	
	/* data type classification */
	/**
	 * @param data_type
	 * @return null if the original data type is invalid
	 */ 
	private static CType get_normalized_type(CType data_type) {
		if(data_type == null) {
			return CBasicTypeImpl.void_type;	
		}
		else {
			try {
				return CTypeAnalyzer.get_value_type(data_type);
			}
			catch(Exception ex) {
				return CBasicTypeImpl.void_type;
			}
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is void
	 */
	public static boolean is_void(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_void:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is boolean
	 */
	public static boolean is_boolean(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_bool:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return true iff. {uchar|ushort|uint|ulong}
	 */
	public static boolean is_usigned(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_uchar:
			case c_ushort:
			case c_uint:
			case c_ulong:	return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return {char|uchar|short|ushort|int|uint|long|ulong|llong|ullong|enum}
	 */
	public static boolean is_integer(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_char:
			case c_uchar:
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return true;
			default:		return false;
			}
		}
		else if(data_type instanceof CEnumType) {
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return {char|uchar|short|ushort|int|uint|long|ulong|llong|ullong|enum}
	 */
	public static boolean is_doubles(CType data_type) {
		data_type = get_normalized_type(data_type);
		if(data_type instanceof CBasicType) {
			switch(((CBasicType) data_type).get_tag()) {
			case c_float:
			case c_double:
			case c_ldouble: return true;
			default:		return false;
			}
		}
		else {
			return false;
		}
	}
	/**
	 * @param data_type
	 * @return whether the data type is integer or real
	 */
	public static boolean is_numeric(CType data_type) {
		return is_integer(data_type) || is_doubles(data_type);
	}
	/**
	 * @param data_type
	 * @return whether the data type is a address pointer
	 */
	public static boolean is_address(CType data_type) {
		data_type = get_normalized_type(data_type);
		return data_type instanceof CArrayType || 
				data_type instanceof CPointerType;
	}
	
	/* location classifier */
	/**
	 * @param expression
	 * @return
	 */
	public static boolean is_void(CirExpression expression) {
		return is_void(expression.get_data_type());
	}
	/**
	 * @param location
	 * @return whether the expression is a boolean
	 */
	public static boolean is_boolean(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(is_boolean(expression.get_data_type())) {
			return true;
		}
		else if(expression.get_parent() instanceof CirStatement) {
			CirStatement statement = expression.statement_of();
			return statement instanceof CirIfStatement || statement instanceof CirCaseStatement;
		}
		else {
			return false;
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a unsigned integer
	 */
	public static boolean is_usigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_usigned(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is an integer
	 */
	public static boolean is_integer(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real
	 */
	public static boolean is_doubles(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_doubles(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a real or integer
	 */
	public static boolean is_numeric(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_integer(expression) || is_doubles(expression);
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a pointer
	 */
	public static boolean is_address(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else {
			return is_address(expression.get_data_type());
		}
	}
	/**
	 * @param expression
	 * @return whether the expression is a reference defined in left-side of assignment
	 */
	public static boolean is_assigned(CirExpression expression) {
		if(expression == null) {
			return false;
		}
		else if(expression.get_parent() instanceof CirAssignStatement) {
			CirAssignStatement statement = (CirAssignStatement) expression.get_parent();
			return statement.get_lvalue() == expression;
		}
		else {
			return false;
		}
	}
	
	/* decidable path finders */
	/**
	 * @param target		the final execution point that the path reaches to
	 * @param across_branch	True if the decidable path should get across
	 * @return				the decidable previous path to the target within the
	 * 						internal procedure part.
	 * @throws Exception
	 */
	private static CirExecutionPath inner_previous_path(CirExecution 
					target, boolean across_branch) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else {
			/* 1. initialization */
			CirExecutionPath path = new CirExecutionPath(target);
			CirExecution execution, source; CirExecutionFlow flow;
			
			/* 2. decidable traversal */
			while(true) {
				execution = path.get_source();
				if(execution.get_in_degree() == 1) {
					flow = execution.get_in_flow(0);
					if(flow.get_type() == CirExecutionFlowType.retr_flow) {
						source = execution.get_graph().get_execution(execution.get_id() - 1);
						flow = CirExecutionFlow.virtual_flow(source, execution);
						path.insert(flow);	/* across the function calling */
					}
					else if(flow.get_type() == CirExecutionFlowType.true_flow
							|| flow.get_type() == CirExecutionFlowType.fals_flow) {
						if(across_branch) {
							path.insert(flow);
						}
						else {
							break;			/* not across the branch flows */
						}
					}
					else {
						path.insert(flow);	/* decidable normal flow is in */
					}
				}
				else {					
					/* reach the undecidable conjunction */	break;			
				}
			}
			
			/* 3. return previous decidable path */	return path;
		}
	}
	/**
	 * @param target
	 * @return decidable previous path until the target without across any branch
	 * @throws Exception
	 */
	public static CirExecutionPath inblock_prev_path(CirExecution target) throws Exception {
		return inner_previous_path(target, false);
	}
	/**
	 * @param target
	 * @return decidable previous path until the target that may across branches
	 * @throws Exception
	 */
	public static CirExecutionPath oublock_prev_path(CirExecution target) throws Exception {
		return inner_previous_path(target, true);
	}
	/**
	 * @param source
	 * @return the path from source to following using decidable path in the internal procedure
	 * @throws Exception
	 */
	public static CirExecutionPath oublock_post_path(CirExecution source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else {
			/* 1. declaration and initialization */
			CirExecutionPath path = new CirExecutionPath(source);
			CirExecution execution,target; CirExecutionFlow flow; 
			
			/* 2. decidable path traversal */
			while(true) {
				execution = path.get_target();
				if(execution.get_ou_degree() == 1) {							/* decidable branch */
					flow = execution.get_ou_flow(0);
					if(flow.get_type() == CirExecutionFlowType.call_flow) {		/* skip the call-block */
						target = execution.get_graph().get_execution(execution.get_id() + 1);
						path.append(CirExecutionFlow.virtual_flow(execution, target));
					}
					else if(flow.get_type() == CirExecutionFlowType.retr_flow) {/* only reach the return */
						path.append(flow); break;
					}
					else {														/* skip the decidable flow */
						path.append(flow);
					}
				}
				else {															/* true|false branch flows */
					break;
				}
			}
			
			/* 3. return next-postfix decidable path */	return path;
		}
	}
	
	/* static data-flow finder */
	/**
	 * @param root
	 * @return	the set of reference expressions specified under the root node
	 * @throws Exception
	 */
	private static Collection<SymbolExpression> get_references(SymbolNode root) throws Exception {
		if(root == null) {
			throw new IllegalArgumentException("Invalid root: null");
		}
		else {
			Set<SymbolExpression> references = new HashSet<SymbolExpression>();
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
				if(parent.is_reference()) {
					references.add((SymbolExpression) parent);
				}
			}
			return references;
		}
	}
	/**
	 * @param root
	 * @param references
	 * @return whether any reference in {references} set is contained in the root
	 * @throws Exception
	 */
	private static boolean has_references(SymbolNode root, Collection<SymbolExpression> references) throws Exception {
		if(root == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			Queue<SymbolNode> queue = new LinkedList<SymbolNode>();
			queue.add(root);
			while(!queue.isEmpty()) {
				SymbolNode parent = queue.poll();
				for(SymbolNode child : parent.get_children()) {
					queue.add(child);
				}
				if(references.contains(parent)) {
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * @param execution
	 * @param references
	 * @return whether any reference in the set is defined in the given execution
	 * @throws Exception
	 */
	public static boolean is_defined_at(CirExecution execution, Collection<SymbolExpression> references) throws Exception {
		if(execution == null) {
			return false;
		}
		else if(references == null || references.isEmpty()) {
			return false;
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirIfStatement) {
				CirExpression condition = ((CirIfStatement) statement).get_condition();
				return has_references(SymbolFactory.sym_expression(condition), references);
			}
			else if(statement instanceof CirCaseStatement) {
				CirExpression condition = ((CirIfStatement) statement).get_condition();
				return has_references(SymbolFactory.sym_expression(condition), references);
			}
			else if(statement instanceof CirAssignStatement) {
				SymbolExpression root = SymbolFactory.sym_expression(
						((CirAssignStatement) statement).get_lvalue());
				return references.contains(root);
			}
			else {
				return false;
			}
		}
	}
	/**
	 * @param prev_path	the path to the current execution where the condition is directly evaluated
	 * @param condition	the symbolic condition to be evaluated at the improved path
	 * @return			the execution point where the condition is improved until
	 * @throws Exception
	 */
	public static CirExecution find_checkpoint(CirExecutionPath prev_path, SymbolExpression condition) throws Exception {
		if(prev_path == null) {
			throw new IllegalArgumentException("Invalid prev_path: null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			Collection<SymbolExpression> references = get_references(condition);
			Iterator<CirExecutionEdge> iterator = prev_path.get_iterator(true);
			while(iterator.hasNext()) {
				CirExecutionEdge edge = iterator.next();
				if(is_defined_at(edge.get_source(), references)) {
					return edge.get_target();
				}
			}
			return prev_path.get_source();
		}
	}
	/**
	 * It derives the set of expressions that use the value of reference under the root node
	 * @param root				the root node under which use expressions are derived
	 * @param reference			the reference of which value is expected to be used in
	 * @param use_expressions	to preserve the set of used expressions under the root
	 * @throws Exception
	 */
	private static void derive_use_expressions(CirNode root, SymbolExpression 
			reference, Collection<CirExpression> use_expressions) throws Exception {
		if(root == null) {
			throw new IllegalArgumentException("Invalid root: null");
		}
		else if(reference == null || !reference.is_reference()) {
			throw new IllegalArgumentException("Invalid reference.");
		}
		else if(use_expressions == null) {
			throw new IllegalArgumentException("No output is preserved");
		}
		else {
			/* 1. initialization and declarations */
			Queue<CirNode> queue = new LinkedList<CirNode>(); 
			queue.add(root);
			
			/* 2. BFS-traversal to derive use-set */
			while(!queue.isEmpty()) {
				CirNode parent = queue.poll();
				for(CirNode child : parent.get_children()) {
					queue.add(child);
				}
				
				if(parent instanceof CirExpression) {
					SymbolExpression use = SymbolFactory.sym_expression(parent);
					if(use.equals(reference)) { 
						use_expressions.add((CirExpression) parent); 
					}
				}
			}
		}
	}
	/**
	 * @param execution			the statement under which the used expressions are derived
	 * @param reference			the reference of which value is expected to be used in the point
	 * @param use_expressions	to preserve the set of expressions used under the statement
	 * @throws Exception
	 */
	public static void derive_use_expressions(CirExecution execution, SymbolExpression 
				reference, Collection<CirExpression> use_expressions) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(reference == null || !reference.is_reference()) {
			throw new IllegalArgumentException("Invalid reference: null");
		}
		else if(use_expressions == null) {
			throw new IllegalArgumentException("No output is preserved");
		}
		else {
			CirStatement statement = execution.get_statement();
			if(statement instanceof CirIfStatement) {
				derive_use_expressions(((CirIfStatement) statement).
						get_condition(), reference, use_expressions);
			}
			else if(statement instanceof CirCaseStatement) {
				derive_use_expressions(((CirCaseStatement) statement).
						get_condition(), reference, use_expressions);
			}
			else if(statement instanceof CirAssignStatement) {
				derive_use_expressions(((CirAssignStatement) statement).
						get_lvalue(), reference, use_expressions);
				derive_use_expressions(((CirAssignStatement) statement).
						get_rvalue(), reference, use_expressions);
				use_expressions.remove(((CirAssignStatement) statement).get_lvalue());
			}
			else if(statement instanceof CirCallStatement) {
				derive_use_expressions(((CirCallStatement) statement).
						get_arguments(), reference, use_expressions);
			}
			else { /* empty for none-value-statement */	}
		}
	}
	/**
	 * @param source	the execution point where the reference is defined
	 * @param reference	the reference to be used in the following of source
	 * @return			the set of expressions used after source is executed
	 * @throws Exception
	 */
	public static Collection<CirExpression> find_use_expressions(CirExecution source, SymbolExpression reference) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("Invalid source: null");
		}
		else if(reference == null || reference.is_reference()) {
			throw new IllegalArgumentException("Invalid reference: null");
		}
		else {
			CirExecutionPath path = oublock_post_path(source);
			Iterator<CirExecutionEdge> iterator = path.get_iterator();
			Set<SymbolExpression> references = new HashSet<SymbolExpression>();
			references.add(reference);
			Set<CirExpression> use_expressions = new HashSet<CirExpression>();
			
			while(iterator.hasNext()) {
				CirExecution target = iterator.next().get_target();
				derive_use_expressions(target, reference, use_expressions);
				if(is_defined_at(target, references)) { break; }
			}
			return use_expressions;
		}
	}
	
	/* constructor and parsing */
	/**
	 * @param mutant	the mutant it represents
	 * @param execution	the statement to reach
	 * @param i_state	the condition to infect
	 * @param p_state	the error to propagation
	 * @return it creates a CirMutation instance
	 * @throws Exception
	 */
	public static CirMutation new_mutation(Mutant mutant, CirExecution execution, 
			CirConditionState i_state, CirAbstErrorState p_state) throws Exception {
		return new CirMutation(mutant, execution, i_state, p_state);
	}
	/**
	 * @param mutant	the syntactic mutation to be translated into mutations in C-intermediate representative form
	 * @return 			the set of C-intermediate representative mutations of the mutant or empty if it fails to parse
	 * @throws Exception
	 */
	public static Collection<CirMutation> parse(Mutant mutant) throws Exception {
		try {
			return CirMutationParsers.parse(mutant);
		}
		catch(Exception ex) {
			return new ArrayList<CirMutation>();
		}
	}
	
	/* normalization-evaluation and subsume-extension */
	/**
	 * It normalizes the input state to corresponding standard structural form.
	 * @param state		the state to be normalized
	 * @param context	the context in which the state is normalized
	 * @return			the normalized structural form of input state
	 * @throws Exception
	 */
	public static CirAbstractState normalize(CirAbstractState state, SymbolContext context) throws Exception {
		return CirStateNormalizer.normalize(state, context);
	}
	/**
	 * It normalizes the input state to corresponding standard structural form.
	 * @param state		the state to be normalized
	 * @return			the normalized structural form of input state
	 * @throws Exception
	 */
	public static CirAbstractState normalize(CirAbstractState state) throws Exception {
		return CirStateNormalizer.normalize(state, null);
	}
	/**
	 * It evaluates the state to a boolean value according to its category and the 
	 * given symbolic computational context.
	 * @param state		the state to be evaluated by this method
	 * @param context	the context in which the state is evaluated
	 * @return			True {passed}; False {fail}; null {Unknown}
	 * @throws Exception
	 */
	public static Boolean evaluate(CirAbstractState state, SymbolContext context) throws Exception {
		return CirStateNormalizer.evaluate(state, context);
	}
	/**
	 * It evaluates the state to a boolean value according to its category and the 
	 * given symbolic computational context.
	 * @param state		the state to be evaluated by this method
	 * @return			True {passed}; False {fail}; null {Unknown}
	 * @throws Exception
	 */
	public static Boolean evaluate(CirAbstractState state) throws Exception {
		return CirStateNormalizer.evaluate(state, null);
	}
	/**
	 * It extends the input state to extended states directly subsumed by input
	 * @param state
	 * @return the set of directly subsumed states that are extended by input
	 * @throws Exception
	 */
	public static Collection<CirAbstractState> extend_one(CirAbstractState state) throws Exception {
		Set<CirAbstractState> outputs = new HashSet<CirAbstractState>();
		CirStateExtender.extend(state, outputs, false); return outputs;
	}
	/**
	 * It extends the input state to extended states locally subsumed by input
	 * @param state
	 * @return the set of directly all subsumed states that are extended by input
	 * @throws Exception
	 */
	public static Collection<CirAbstractState> extend_all(CirAbstractState state) throws Exception {
		Set<CirAbstractState> outputs = new HashSet<CirAbstractState>();
		CirStateExtender.extend(state, outputs, true); return outputs;
	}
	/**
	 * @param state
	 * @param context	CDependGraph | CirExecutionPath | CStatePath | null
	 * @return the set of states directly subsumed by the input under the given context
	 * @throws Exception 
	 */
	public static Collection<CirAbstractState> subsume(CirAbstractState state, Object context) throws Exception {
		Set<CirAbstractState> outputs = new HashSet<CirAbstractState>();
		CirStateExtender.subsume(state, outputs, context); return outputs;
	}
	/**
	 * It write code, test, dependence and state information under given context
	 * @param source_cfile
	 * @param ou_directory
	 * @param max_distance
	 * @param context		CDependGraph | CStatePath | CirExecutionPath | null
	 * @throws Exception
	 */
	public static void write_features(MuTestProjectCodeFile source_cfile, File ou_directory, 
			int max_distance, Collection<TestInput> test_cases) throws Exception {
		CirStateFeatureWriter.write_features(source_cfile, ou_directory, max_distance, test_cases);
	}
	
}
