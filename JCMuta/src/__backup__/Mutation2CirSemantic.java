package __backup__;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.decl.initializer.AstInitializer;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstParanthExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstExpressionStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstForStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.AstCirPair;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirFunction;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCall;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * Translate from the AST based TextMutation to the CirSemanticMutation.
 * 
 * @author yukimula
 *
 */
public class Mutation2CirSemantic {
	
	/* singleton, constructor and attribute */
	/** the tree of CIR based program **/
	private CirTree cir_tree;
	/** constructor **/
	public Mutation2CirSemantic() { }
	
	/* parsing methods */
	/**
	 * start the translator by setting the CIR program to be seeded
	 * @param cir_tree
	 * @throws Exception
	 */
	public void open(CirTree cir_tree) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree as null");
		else this.cir_tree = cir_tree;
	}
	/**
	 * translate the AST based text mutation into semantic based mutation
	 * @param mutation
	 * @return null if the mutation is proven as either syntax error or semantic equivalence.
	 * @throws Exception
	 */
	public CirSemanticMutation translate(TextMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(this.cir_tree == null)
			throw new IllegalArgumentException("No CIR program specified");
		else {
			switch(mutation.get_operator()) {
			/* traping mutation operators */
			case STRP:	return this.parse_STRP(mutation);
			case STRI:	return this.parse_STRI(mutation);
			case STRC:	return this.parse_STRC(mutation);
			case SSWM:	return this.parse_SSWM(mutation);
			case SMTC:	return this.parse_SMTC(mutation);
			case VDTR:	return this.parse_VDTR(mutation);
			/* statement mutation operators */
			case SBRC:	return this.parse_SBRC(mutation);
			case SCRB:	return this.parse_SCRB(mutation);
			case SWDD:	return this.parse_SWDD(mutation);
			case SDWD:	return this.parse_SDWD(mutation);
			case SSDL:	return this.parse_SSDL(mutation);
			/* increment mutation operators */
			case OPPO:	return this.parse_OPPO(mutation);
			case OMMO:	return this.parse_OMMO(mutation);
			case UIOI:	return this.parse_UIOI(mutation);
			case VTWD:	return this.parse_VTWD(mutation);
			/* negative mutation operators */
			case OBNG:	return this.parse_OBNG(mutation);
			case OCNG:	return this.parse_OLNG(mutation);
			case OLNG:	return this.parse_OLNG(mutation);
			case ONDU:	return this.parse_ONDU(mutation);
			case VABS:	return this.parse_VABS(mutation);
			/* value mutation operators */
			case VBCR:	return this.parse_VBCR(mutation);
			case CCCR:	return this.parse_CCCR(mutation);
			case CRCR:	return this.parse_CRCR(mutation);
			case CCSR:	return this.parse_CCSR(mutation);
			/* reference mutation operators */
			case VARR:	return this.parse_VARR(mutation);
			case VPRR:	return this.parse_VPRR(mutation);
			case VSRR:	return this.parse_VSRR(mutation);
			case VSFR:	return this.parse_VSFR(mutation);
			case VTRR:	return this.parse_VTRR(mutation);
			/* operator mutation operators */
			case OAAN:
			case OABN:
			case OALN:
			case OARN:
			case OASN:	return this.parse_OAXN(mutation);
			case OBAN:
			case OBBN:
			case OBLN:
			case OBRN:
			case OBSN:	return this.parse_OBXN(mutation);
			case OSAN:
			case OSBN:
			case OSLN:
			case OSRN:
			case OSSN:	return this.parse_OBXN(mutation);
			case OLAN:
			case OLBN:
			case OLLN:
			case OLRN:
			case OLSN:	return this.parse_OLXN(mutation);
			case ORAN:
			case ORBN:
			case ORLN:
			case ORRN:
			case ORSN:	return this.parse_ORXN(mutation);
			/* assignment mutation operators */
			case OEAA:
			case OEBA:
			case OESA:	return this.parse_OEXA(mutation);
			case OAAA:
			case OABA:
			case OASA:	return this.parse_OAXA(mutation);
			case OBAA:	
			case OBBA:	
			case OBSA:	
			case OSAA:	
			case OSBA:	
			case OSSA:	return this.parse_OBXA(mutation);
			default: throw new IllegalArgumentException("Unsupport: " + mutation.get_operator());
			}
		}
	}
	/**
	 * stop the translator by setting the program being seeded as null.
	 */
	public void close() { this.cir_tree = null; }
	
	/* basic methods */
	/* constant proceeding */
	/**
	 * translate the string to the integer it describes
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private int get_integer(String code) throws Exception {
		if(code.startsWith("\'")) {
			code = code.substring(1, code.lastIndexOf('\''));
			return code.charAt(0);
		}
		else {
			return this.decode_integer(code);
		}
	}
	/**
	 * get the integer from code
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private int decode_integer(String code) throws Exception {
		if(code.startsWith("0x") || code.startsWith("0X")) {
			int number = 0;
			for(int k = 2; k < code.length(); k++) {
				char ch = code.charAt(k);
				int key;
				if(ch >= '0' && ch <= '9') {
					key = ch - '0';
				}
				else if(ch >= 'a' && ch <= 'f') {
					key = ch - 'a' + 10;
				}
				else if(ch >= 'A' && ch <= 'F') {
					key = ch - 'A' + 10;
				}
				else throw new IllegalArgumentException("Unknown code: " + ch);
				number = number * 16 + key;
			}
			return number;
		}
		else if(code.startsWith("0")) {
			int number = 0;
			for(int k = 2; k < code.length(); k++) {
				char ch = code.charAt(k);
				int key;
				if(ch >= '0' && ch <= '7') {
					key = ch - '0';
				}
				else throw new IllegalArgumentException("Unknown code: " + ch);
				number = number * 8 + key;
			}
			return number;
		}
		else return Integer.parseInt(code);
	}
	/**
	 * Get the constant of the code represents
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private Object get_constant(String code) throws Exception {
		if(code.startsWith("\'")) {
			code = code.substring(1, code.lastIndexOf('\''));
			return (int) code.charAt(0);
		}
		else {
			try {
				return this.decode_integer(code);
			}
			catch(Exception ex) {
				return Double.parseDouble(code);
			}
		}
	}
	/**
	 * get the constant that the expression represents.
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	private Object get_constant(CirExpression expression) throws Exception {
		if(expression instanceof CirConstExpression) {
			CConstant constant = ((CirConstExpression) expression).get_constant();
			switch(constant.get_type().get_tag()) {
			case c_char:
			case c_uchar:	return constant.get_char();
			case c_short:
			case c_ushort:
			case c_int:
			case c_uint:	return constant.get_integer();
			case c_long:
			case c_ulong:
			case c_llong:
			case c_ullong:	return constant.get_long();
			case c_float:	return constant.get_float();
			case c_double:
			case c_ldouble:	return constant.get_double();
			default: throw new IllegalArgumentException("Unknown constant");
			}
		}
		else { return null; }
	}
	/* abstract syntax proceeding */
	/**
	 * get the index of the child under its parent in AST
	 * @param node
	 * @return -1 if the node itself is a root
	 */
	private int get_child_index(AstNode child) {
		AstNode parent = child.get_parent();
		if(parent == null)	return -1;
		else {
			for(int k = 0; k < parent.number_of_children(); k++) {
				if(parent.get_child(k) == child) return k;
			}
			return -1;
		}
	}
	/**
	 * get the CIR-code range with respect to the given AST node
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private AstCirPair get_cir_range(AstNode node) throws Exception {
		if(!this.cir_tree.has_cir_range(node)) return null;
		else return this.cir_tree.get_cir_range(node);
	}
	/**
	 * get the location that matches the required class
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private AstNode find_ast_location(AstNode location, Class<?> ast_type) throws Exception {
		while(location != null) {
			if(ast_type.isInstance(location)) {
				if(location instanceof AstConstExpression) {
					location = CTypeAnalyzer.get_expression_of((AstExpression) location);
				}
				else if(location instanceof AstParanthExpression) {
					location = CTypeAnalyzer.get_expression_of((AstExpression) location);
				}
				else if(location instanceof AstInitializer) {
					if(((AstInitializer) location).is_body())
						location = ((AstInitializer) location).get_body();
					else location = ((AstInitializer) location).get_expression();
					location = CTypeAnalyzer.get_expression_of((AstExpression) location);
				}
				else if(location instanceof AstExpressionStatement) {
					if(((AstExpressionStatement) location).has_expression())
						location = CTypeAnalyzer.get_expression_of(((AstExpressionStatement) location).get_expression());
				}
				return location;
			}
			else location = location.get_parent();
		}
		throw new IllegalArgumentException("Invalid location");
	}
	/* C-like intermediate proceeding */
	/**
	 * Find the first CIR node with respect to the type in the range of the location
	 * @param location
	 * @param cir_type
	 * @return
	 * @throws Exception
	 */
	private CirNode get_cir_location(AstNode location, Class<?> cir_type) throws Exception {
		Iterable<CirNode> cir_nodes = this.cir_tree.get_cir_nodes(location);
		for(CirNode cir_node : cir_nodes) {
			if(cir_type.isInstance(cir_node)) return cir_node;
		}
		return null;
	}
	/**
	 * find the real statement of the given location in AST node
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private CirStatement find_real_statement_at(AstNode location) throws Exception {
		while(location != null) {
			AstCirPair ast_cir_range = this.get_cir_range(location);
			if(ast_cir_range != null && ast_cir_range.executional()) {
				return ast_cir_range.get_beg_statement();
			}
			else {
				AstNode parent = location.get_parent();
				int child_index = this.get_child_index(location);
				if(child_index >= 0) {
					/* find the adjacent statement prior to the current location */
					for(int k = 0; k < child_index; k++) {
						ast_cir_range = this.get_cir_range(parent.get_child(k));
						if(ast_cir_range != null && ast_cir_range.executional()) {
							return ast_cir_range.get_end_statement();
						}
					}
					/* find the adjacent statement next to the current location */
					for(int k = child_index + 1; k < parent.number_of_children(); k++) {
						ast_cir_range = this.get_cir_range(parent.get_child(k));
						if(ast_cir_range != null && ast_cir_range.executional()) {
							return ast_cir_range.get_beg_statement();
						}
					}
				}
				location = parent;
			}
		}
		return null;
	}
	/**
	 * find the real expression that the AST node represents
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private CirExpression find_real_expression_of(AstNode location) throws Exception {
		AstCirPair ast_cir_range = this.get_cir_range(location);
		if(ast_cir_range == null || !ast_cir_range.computational()) return null;
		else {
			CirExpression expression = ast_cir_range.get_result();
			if(expression.statement_of() == null) {
				for(CirNode cir_node : this.cir_tree.get_cir_nodes(location)) {
					if(cir_node instanceof CirExpression) {
						if(((CirExpression) cir_node).statement_of() != null) {
							return (CirExpression) cir_node;
						}
					}
				}
				return null;
			}
			else return expression;
		}
	}
	/**
	 * collect all the statements of CIR program within the range of the AST location
	 * @param location
	 * @return
	 * @throws Exception
	 */
	private Collection<CirStatement> collect_statements_in(AstNode location) throws Exception {
		Queue<AstNode> ast_queue = new LinkedList<AstNode>();
		Set<CirStatement> statements = new HashSet<CirStatement>();
		
		ast_queue.add(location);
		while(!ast_queue.isEmpty()) {
			AstNode ast_node = ast_queue.poll();
			for(int k = 0; k < ast_node.number_of_children(); k++) {
				if(ast_node.get_child(k) != null)
					ast_queue.add(ast_node.get_child(k));
			}
			
			AstCirPair ast_cir_range = this.get_cir_range(ast_node);
			if(ast_cir_range != null && ast_cir_range.executional()) {
				statements.add(ast_cir_range.get_beg_statement());
				statements.add(ast_cir_range.get_end_statement());
			}
		}
		
		return statements;
	}
	/* simple data flow analysis */
	/**
	 * collect all the variables or references used within the expression with respect to
	 * the name as user provides.
	 * @param expression
	 * @param name
	 * @throws Exception
	 */
	private void collect_variables(CirExpression expression, String name, 
			Collection<CirReferExpression> references) throws Exception {
		/** 1. collect the current node if it matches with the name **/
		if(expression instanceof CirReferExpression) {
			if(expression.generate_code().equals(name)) {
				references.add((CirReferExpression) expression);
			}
		}
		
		/** 2. syntax-directed translation **/
		if(expression instanceof CirDeferExpression) {
			this.collect_variables(((CirDeferExpression) expression).get_address(), name, references);
		}
		else if(expression instanceof CirFieldExpression) {
			this.collect_variables(((CirFieldExpression) expression).get_body(), name, references);
		}
		else if(expression instanceof CirAddressExpression) {
			this.collect_variables(((CirAddressExpression) expression).get_operand(), name, references);
		}
		else if(expression instanceof CirCastExpression) {
			this.collect_variables(((CirCastExpression) expression).get_operand(), name, references);
		}
		else if(expression instanceof CirWaitExpression) {
			this.collect_variables(((CirWaitExpression) expression).get_function(), name, references);
		}
		else if(expression instanceof CirComputeExpression) {
			for(int k = 0; k < expression.number_of_children(); k++) {
				this.collect_variables(((CirComputeExpression) expression).get_operand(k), name, references);
			}
		}
		else if(expression instanceof CirInitializerBody) {
			for(int k = 0; k < expression.number_of_children(); k++) {
				this.collect_variables(((CirInitializerBody) expression).get_element(k), name, references);
			}
		}
	}
	/**
	 * collect all the usage points of the variable of which name is provided
	 * within the statement as specified.
	 * @param statement
	 * @param name
	 * @param use_points
	 * @throws Exception
	 */
	private void collect_use_points(CirStatement statement, String name, 
			Collection<CirReferExpression> use_points) throws Exception {
		if(statement instanceof CirAssignStatement) {
			this.collect_variables(((CirAssignStatement) statement).get_lvalue(), name, use_points);
			this.collect_variables(((CirAssignStatement) statement).get_rvalue(), name, use_points);
			use_points.remove(((CirAssignStatement) statement).get_lvalue());	// remove definition
		}
		else if(statement instanceof CirIfStatement) {
			this.collect_variables(((CirIfStatement) statement).get_condition(), name, use_points);
		}
		else if(statement instanceof CirCaseStatement) {
			this.collect_variables(((CirCaseStatement) statement).get_condition(), name, use_points);
		}
		else if(statement instanceof CirCallStatement) {
			this.collect_variables(((CirCallStatement) statement).get_function(), name, use_points);
			
			CirArgumentList arguments = ((CirCallStatement) statement).get_arguments();
			for(int k = 0; k < arguments.number_of_arguments(); k++) {
				this.collect_variables(arguments.get_argument(k), name, use_points);
			}
		}
	}
	/**
	 * whether the statement defines or assigns value to the specified variable with provided name
	 * @param statement
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private boolean is_defined_by(CirStatement statement, String name) throws Exception {
		if(statement instanceof CirAssignStatement) {
			return name.equals(((CirAssignStatement) statement).get_lvalue().generate_code());
		}
		else return false;
	}
	/**
	 * collect all the usage points since the statement (included) with respect to the specific variable
	 * @param head
	 * @param variable
	 * @return
	 * @throws Exception
	 */
	private Collection<CirReferExpression> get_usage_points_after(CirStatement head, CirReferExpression variable) throws Exception {
		/** 1. declarations **/
		Set<CirReferExpression> use_points = new HashSet<CirReferExpression>();
		CirFunctionCallGraph fun_call_graph = this.cir_tree.get_function_call_graph();
		CirFunction function = fun_call_graph.get_function(head);
		CirExecutionFlowGraph flow_graph = function.get_flow_graph();
		
		if(flow_graph.has_execution(head)) {
			/** initialize the brand-first-traversal buffers and name **/
			Queue<CirExecution> queue = new LinkedList<CirExecution>();
			Set<CirExecution> visited = new HashSet<CirExecution>();
			String name = variable.generate_code();
			
			/** push the statements following the head into queue for traversal **/
			CirExecution start = flow_graph.get_execution(head);
			for(CirExecutionFlow flow : start.get_ou_flows()) {
				CirExecution target;
				switch(flow.get_type()) {
				case call_flow:	
					CirFunctionCall call = fun_call_graph.get_calling(flow);
					target = call.get_wait_execution(); break;
				case retr_flow:	target = null; break;
				default: target = flow.get_target(); break;
				}
				
				if(target == null || this.is_defined_by(target.get_statement(), name)) {
					continue;
				}
				else { queue.add(target); visited.add(target); }
			}
			
			/** BFS-algorithm to collect the avaibale usage points of x **/
			while(!queue.isEmpty()) {
				/* 1. get next execution of statement and collect its usage points */
				CirExecution execution = queue.poll();
				this.collect_use_points(execution.get_statement(), name, use_points);
				
				/* 2. when the variable is defined by the statement, stop traversal */
				if(this.is_defined_by(execution.get_statement(), name)) continue;
				
				/* 3. update the next statements being traversed in queue buffer */
				for(CirExecutionFlow flow : execution.get_ou_flows()) {
					// a. determine the next statement from the flow
					CirExecution target;
					switch(flow.get_type()) {
					case call_flow:
						CirFunctionCall call = fun_call_graph.get_calling(flow);
						target = call.get_wait_execution(); break;
					case retr_flow:	target = null; break;
					default: target = flow.get_target(); break;
					}
					
					// b. add the target to queue for further traversal
					if(target != null && !visited.contains(target)) {
						visited.add(target); queue.add(target);
					}
				}
			}
		}
		
		return use_points;
	}
	/* program semantic inference */
	/**
	 * connect the inference based on reachability to the state error node
	 * @param reach_node
	 * @param infect_node
	 * @param error_node
	 * @throws Exception
	 */
	private void infer(CirSemanticNode reach_node, CirSemanticNode error_node) throws Exception {
		CirSemanticFactory.infer(reach_node, error_node);
	}
	/**
	 * connect the inference based on reachability and infection to the state error node
	 * @param reach_node
	 * @param infect_node
	 * @param error_node
	 * @throws Exception
	 */
	private void infer(CirSemanticNode reach_node, CirSemanticNode infect_node, CirSemanticNode error_node) throws Exception {
		CirSemanticFactory.infer(new CirSemanticNode[] { reach_node, infect_node }, error_node);
	}
	/**
	 * connect the inference from reachability and infection constraints to the state error node
	 * @param reach_node
	 * @param infect_nodes
	 * @param error_node
	 * @throws Exception
	 */
	private void infer(CirSemanticNode reach_node, CirSemanticNode[] infect_nodes, CirSemanticNode error_node) throws Exception {
		CirSemanticNode[] causes = new CirSemanticNode[1 + infect_nodes.length];
		causes[0] = reach_node;
		for(int k = 0; k < infect_nodes.length; k++) {
			causes[k + 1] = infect_nodes[k];
		}
		CirSemanticFactory.infer(causes, error_node);
	}
	/**
	 * connect the inference based on reachability and infection to a set of state error nodes
	 * @param reach_node
	 * @param infect_node
	 * @param error_nodes
	 * @throws Exception
	 */
	private void infer(CirSemanticNode reach_node, CirSemanticNode infect_node, CirSemanticNode[] error_nodes) throws Exception {
		CirSemanticFactory.infer(new CirSemanticNode[] { reach_node, infect_node }, error_nodes);
	}
	/**
	 * connect the inference based on reachability and infection to a set of state error nodes
	 * @param reach_node
	 * @param infect_nodes
	 * @param error_nodes
	 * @throws Exception
	 */
	private void infer(CirSemanticNode reach_node, CirSemanticNode[] infect_nodes, CirSemanticNode[] error_nodes) throws Exception {
		CirSemanticNode[] causes = new CirSemanticNode[1 + infect_nodes.length];
		causes[0] = reach_node;
		for(int k = 0; k < infect_nodes.length; k++) {
			causes[k + 1] = infect_nodes[k];
		}
		CirSemanticFactory.infer(causes, error_nodes);
	}
	/**
	 * connect the inference from reachability to a set of state errors nodes
	 * @param reach_node
	 * @param error_nodes
	 * @throws Exception
	 */
	private void infer(CirSemanticNode reach_node, Collection<CirSemanticNode> error_nodes) throws Exception {
		CirSemanticNode[] errors = new CirSemanticNode[error_nodes.size()];
		int k = 0;
		for(CirSemanticNode error_node : error_nodes) 
			errors[k++] = error_node;
		CirSemanticFactory.infer(reach_node, errors);
	}
	/**
	 * connect the inference based on reachability and infection to a set of state error nodes
	 * @param reach_node
	 * @param infect_node
	 * @param error_nodes
	 * @throws Exception
	 */
	private void infer(CirSemanticNode reach_node, CirSemanticNode infect_node, Collection<CirSemanticNode> error_nodes) throws Exception {
		CirSemanticNode[] errors = new CirSemanticNode[error_nodes.size()];
		int k = 0;
		for(CirSemanticNode error_node : error_nodes) 
			errors[k++] = error_node;
		CirSemanticFactory.infer(new CirSemanticNode[] { reach_node, infect_node }, errors);
	}
	/**
	 * get the semantic mutation based on CIR code from reachability constraint node
	 * @param reach_node
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation cir_mutation(CirSemanticNode reach_node) throws Exception {
		return new CirSemanticMutation(reach_node);
	}
	
	/* trapping mutation operators */
	/**
	 * 	STRP(statement):
	 * 	{
	 * 		REACH(statement);
	 * 		NULL;
	 * 		TRAP_AT(statement);
	 * 	}
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_STRP(TextMutation mutation) throws Exception {
		/** 1. find the real statement point in AST where the STRP mutation is injected. **/
		AstNode statement = this.find_ast_location(mutation.get_origin(), AstStatement.class);
		
		/** 2. search for the real statement being seeded **/
		CirStatement cir_statement = this.find_real_statement_at(statement);
		if(cir_statement == null)	return null;
		
		/** 3. construct the cir-mutation from the statement **/
		CirSemanticNode reach_node = CirSemanticFactory.cover_statement(cir_statement);
		CirSemanticNode traps_node = CirSemanticFactory.traping(cir_statement);
		this.infer(reach_node, traps_node); return this.cir_mutation(reach_node);
	}
	/**
	 * 	TRAP_ON_TRUE(expression)
	 * 	{
	 * 		REACH(head_statement);
	 * 		ASSERT_AS(expression, true);
	 * 		TRAP_AT(expression.statement);
	 * 	}
	 * 
	 * 	TRAP_ON_FALSE(expression)
	 * 	{
	 * 		REACH(head_statement);
	 * 		ASSERT_AS(expression, false);
	 * 		TRAP_AT(expression.statement);
	 * 	}
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_STRI(TextMutation mutation) throws Exception {
		/** 1. get the real expression that the AST location represents **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		CirExpression expression = this.find_real_expression_of(location);
		
		/** 2. equivalent mutation because there is no available point **/
		if(expression == null)	return null;
		/** 3. construct the semantic mutation based on reachability and infection **/
		else {
			boolean value;
			switch(mutation.get_mode()) {
			case TRAP_ON_TRUE:	value = true;	break;
			case TRAP_ON_FALSE:	value = false;	break;
			default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
			}
			
			CirSemanticNode reach_node = CirSemanticFactory.cover_statement(expression.statement_of());
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(expression, value);
			CirSemanticNode error_node = CirSemanticFactory.traping(expression.statement_of());
			this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
		}
	}
	/**
	 * 	TRAP_ON_TRUE(expression)
	 * 	{
	 * 		REACH(head_statement);
	 * 		ASSERT_AS(expression, true);
	 * 		TRAP_AT(expression.statement);
	 * 	}
	 * 
	 * 	TRAP_ON_FALSE(expression)
	 * 	{
	 * 		REACH(head_statement);
	 * 		ASSERT_AS(expression, false);
	 * 		TRAP_AT(expression.statement);
	 * 	}
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_STRC(TextMutation mutation) throws Exception {
		/** 1. get the real expression that the AST location represents **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		CirExpression expression = this.find_real_expression_of(location);
		
		/** 2. equivalent mutation because there is no available point **/
		if(expression == null)	return null;
		/** 3. construct the semantic mutation based on reachability and infection **/
		else {
			boolean value;
			switch(mutation.get_mode()) {
			case TRAP_ON_TRUE:	value = true;	break;
			case TRAP_ON_FALSE:	value = false;	break;
			default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
			}
			
			CirSemanticNode reach_node = CirSemanticFactory.cover_statement(expression.statement_of());
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(expression, value);
			CirSemanticNode error_node = CirSemanticFactory.traping(expression.statement_of());
			this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
		}
	}
	/**
	 * trap_on_case(expression, value):
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	EQUAL_WITH(expression, value);
	 * 	TRAPING(expression.STATEMENT);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_SSWM(TextMutation mutation) throws Exception {
		/** 1. get the real expression that the AST location represents **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		CirExpression expression = this.find_real_expression_of(location);
		
		/** 2. equivalent mutation because there is no available point **/
		if(expression == null)	return null;
		/** 3. construct the semantic mutation based on reachability and infection **/
		else {
			/** get the integer value to be matched **/
			String replace = mutation.get_replace();
			int begindex = replace.lastIndexOf(',') + 1;
			int endindex = replace.lastIndexOf(')');
			String int_code = replace.substring(begindex, endindex);
			int value = this.get_integer(int_code.strip());
			
			/** construct the reachability and infection constraints **/
			CirSemanticNode reach_node = CirSemanticFactory.cover_statement(expression.statement_of());
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(expression, value);
			CirSemanticNode error_node = CirSemanticFactory.traping(expression.statement_of());
			this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
		}
	}
	/**
	 * trap_on_times(loop_statement, loop_times)
	 * {
	 * 	COVER(statement)
	 * 	REPEAT(statement, loop_times)
	 * 	TRAPING(statement)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_SMTC(TextMutation mutation) throws Exception {
		/** 1. find the if-statement in CIR code with respect to the loop statement **/
		AstNode location = mutation.get_origin();
		CirIfStatement if_statement = null;
		while(location != null) {
			if(location instanceof AstDoWhileStatement) {
				if_statement = (CirIfStatement) this.get_cir_location(location, CirIfStatement.class);
				break;
			}
			else if(location instanceof AstWhileStatement) {
				if_statement = (CirIfStatement) this.get_cir_location(location, CirIfStatement.class);
				break;
			}
			else if(location instanceof AstForStatement) {
				if_statement = (CirIfStatement) this.get_cir_location(location, CirIfStatement.class);
				break;
			}
			else location = location.get_parent();
		}
		
		/** 2. the mutation is equivalent because it is seeded in no looping! **/
		if(if_statement == null) { return null; }
		/** 3. otherwise, construct the semantic mutation based on constraints **/
		else {
			/* determine the times need to repeat the looping statement */
			String replace = mutation.get_replace();
			int begindex = replace.indexOf('(') + 1;
			int endindex = replace.indexOf(')');
			String int_code = replace.substring(begindex, endindex);
			int loop_times = this.get_integer(int_code.strip());
			
			CirSemanticNode reach_node = CirSemanticFactory.cover_statement(if_statement);
			CirSemanticNode infect_node = CirSemanticFactory.repeat_statement(if_statement, loop_times);
			CirSemanticNode error_node = CirSemanticFactory.traping(if_statement);
			this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
		}
	}
	/**
	 * trap_on_pos(expression):
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	GREATER_TN(expression, 0);
	 * 	TRAPING(expression.STATEMENT);
	 * }
	 * 
	 * trap_on_neg(expression):
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	SMALLER_TN(expression, 0);
	 * 	TRAPING(expression.STATEMENT);
	 * }
	 * 
	 * trap_on_zro(expression):
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	EQUAL_WITH(expression, 0);
	 * 	TRAPING(expression.STATEMENT);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VDTR(TextMutation mutation) throws Exception {
		/** 1. get the expression where the mutation is seeded **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		CirExpression expression = this.find_real_expression_of(location);
		
		/** 2. equivalent mutation because there is no available point **/
		if(expression == null)	return null;
		/** 3. construct the semantic mutation based on reachability and infection **/
		else {
			CirSemanticNode reach_node = CirSemanticFactory.cover_statement(expression.statement_of());
			CirSemanticNode infect_node;
			switch(mutation.get_mode()) {
			case TRAP_ON_POS:
				infect_node = CirSemanticFactory.greater_tn(expression, 0); break;
			case TRAP_ON_NEG:
				infect_node = CirSemanticFactory.smaller_tn(expression, 0); break;
			case TRAP_ON_ZRO:
				infect_node = CirSemanticFactory.equal_with(expression, 0); break;
			default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
			}
			CirSemanticNode error_node = CirSemanticFactory.traping(expression.statement_of());
			this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
		}
	}
	/* statement mutation operators */
	/**
	 * break; ==> continue;
	 * {
	 * 	COVER(statement)
	 * 	[EXECUTE(loop.statement)]*
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_SBRC(TextMutation mutation) throws Exception {
		/** 1. find the goto statement of the break; **/
		AstNode location = find_ast_location(mutation.get_origin(), AstBreakStatement.class);
		CirGotoStatement statement = 
				(CirGotoStatement) this.get_cir_location(location, CirGotoStatement.class);
		
		/** 2. the mutation is syntax error because it does not refer to any statement **/
		if(statement == null) { return null; }
		/** 3. otherwise, create semantic mutation and errors for executing all in loops **/
		else {
			/* a. find the body in looping statement */
			AstNode loop_body = null;
			while(location != null) {
				if(location instanceof AstWhileStatement) {
					loop_body = ((AstWhileStatement) location).get_body();
					break;
				}
				else if(location instanceof AstForStatement) {
					loop_body = ((AstForStatement) location).get_body();
					break;
				}
				else if(location instanceof AstDoWhileStatement) {
					loop_body = ((AstDoWhileStatement) location).get_body();
					break;
				}
				else { location = location.get_parent(); }
			}
			
			/* b. the mutation is invalid because no loops are found */
			if(loop_body == null) return null;
			/* c. otherwise, create error nodes for each statement in body */
			else {
				/** collect all the statements within the body of the loops **/
				Collection<CirStatement> loop_statements = this.collect_statements_in(loop_body);
				
				/** the mutation is equivalent because no statements executed **/
				if(loop_statements.isEmpty()) { return null; }
				/** otherwise, create reach-errors mutation in semantic layer **/
				else {
					CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
					List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
					for(CirStatement loop_statement : loop_statements) {
						if(!(loop_statement instanceof CirTagStatement)) {
							CirSemanticNode error_node;
							error_node = CirSemanticFactory.execute_statement(loop_statement);
							error_nodes.add(error_node);
						}
					}
					this.infer(reach_node, error_nodes); return this.cir_mutation(reach_node);
				}
			}
		}
	}
	/**
	 * continue; ==> break;
	 * {
	 * 	COVER(statement)
	 * 	[NON_EXECUTE(loop.statement)]*
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_SCRB(TextMutation mutation) throws Exception {
		/** 1. find the goto statement of the break; **/
		AstNode location = find_ast_location(mutation.get_origin(), AstContinueStatement.class);
		CirGotoStatement statement = 
				(CirGotoStatement) this.get_cir_location(location, CirGotoStatement.class);
		
		/** 2. the mutation is syntax error because it does not refer to any statement **/
		if(statement == null) { return null; }
		/** 3. otherwise, create semantic mutation and errors for executing all in loops **/
		else {
			/* a. find the body in looping statement */
			AstNode loop_body = null;
			while(location != null) {
				if(location instanceof AstWhileStatement) {
					loop_body = ((AstWhileStatement) location).get_body();
					break;
				}
				else if(location instanceof AstForStatement) {
					loop_body = ((AstForStatement) location).get_body();
					break;
				}
				else if(location instanceof AstDoWhileStatement) {
					loop_body = ((AstDoWhileStatement) location).get_body();
					break;
				}
				else { location = location.get_parent(); }
			}
			
			/* b. the mutation is invalid because no loops are found */
			if(loop_body == null) return null;
			/* c. otherwise, create error nodes for each statement in body */
			else {
				/** collect all the statements within the body of the loops **/
				Collection<CirStatement> loop_statements = this.collect_statements_in(loop_body);
				
				/** the mutation is equivalent because no statements executed **/
				if(loop_statements.isEmpty()) { return null; }
				/** otherwise, create reach-errors mutation in semantic layer **/
				else {
					CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
					List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
					for(CirStatement loop_statement : loop_statements) {
						if(!(loop_statement instanceof CirTagStatement)) {
							CirSemanticNode error_node;
							error_node = CirSemanticFactory.execute_statement(loop_statement);
							error_nodes.add(error_node);
						}
					}
					this.infer(reach_node, error_nodes); return this.cir_mutation(reach_node);
				}
			}
		}
	}
	/**
	 * while ==> do...while
	 * {
	 * 	COVER(loop_statement.FIRST_STATEMENT)
	 * 	EQUAL_WITH(loop_statement.CONDITION, false)
	 * 	EXECUTE(loop_statement.body)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_SWDD(TextMutation mutation) throws Exception {
		/** 1. get the while statement to be mutated as do..while **/
		AstWhileStatement location = 
				(AstWhileStatement) this.find_ast_location(mutation.get_origin(), AstWhileStatement.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		CirIfStatement if_statement = (CirIfStatement) this.get_cir_location(location, CirIfStatement.class);
		
		/** 2. construct the reachability and infection constraints **/
		CirSemanticNode reach_node = CirSemanticFactory.cover_statement(ast_cir_range.get_beg_statement());
		CirSemanticNode infect_node = CirSemanticFactory.equal_with(if_statement.get_condition(), false);
		
		/** 3. collect all the statements within the loop's body **/
		Collection<CirStatement> loop_statements = this.collect_statements_in(location.get_body());
		List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
		for(CirStatement loop_statement : loop_statements) {
			if(!(loop_statement instanceof CirTagStatement))
				error_nodes.add(CirSemanticFactory.execute_statement(loop_statement));
		}
		
		/** 4. return the reach-infect-errors mutation **/
		if(error_nodes.isEmpty()) { return null; }	// equivalent
		else {
			this.infer(reach_node, infect_node, error_nodes); 
			return this.cir_mutation(reach_node);
		}
	}
	/**
	 * do...while ==> while
	 * {
	 * 	COVER(loop_statement.FIRST_STATEMENT)
	 * 	EQUAL_WITH(loop_statement.CONDITION, false)
	 * 	NON_EXECUTE(loop_statement.body)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_SDWD(TextMutation mutation) throws Exception {
		/** 1. get the while statement to be mutated as do..while **/
		AstDoWhileStatement location = 
				(AstDoWhileStatement) this.find_ast_location(mutation.get_origin(), AstDoWhileStatement.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		CirIfStatement if_statement = (CirIfStatement) this.get_cir_location(location, CirIfStatement.class);
		
		/** 2. construct the reachability and infection constraints **/
		CirSemanticNode reach_node = CirSemanticFactory.cover_statement(ast_cir_range.get_beg_statement());
		CirSemanticNode infect_node = CirSemanticFactory.equal_with(if_statement.get_condition(), false);
		
		/** 3. collect all the statements within the loop's body **/
		Collection<CirStatement> loop_statements = this.collect_statements_in(location.get_body());
		List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
		for(CirStatement loop_statement : loop_statements) {
			if(!(loop_statement instanceof CirTagStatement))
				error_nodes.add(CirSemanticFactory.non_execute_statement(loop_statement));
		}
		
		/** 4. return the reach-infect-errors mutation **/
		if(error_nodes.isEmpty()) { return null; }	// equivalent
		else {
			this.infer(reach_node, infect_node, error_nodes); 
			return this.cir_mutation(reach_node);
		}
	}
	/**
	 * delete(statement)
	 * {
	 * 	COVER(statement.FIRST_STATEMENT);
	 * 	NON_EXECUTE(statement.STATEMENTS);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_SSDL(TextMutation mutation) throws Exception {
		/** 1. find the statement being deleted and its range **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstStatement.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. the mutation is equivalent because no statements are deleted **/
		if(ast_cir_range == null || !ast_cir_range.executional()) { return null; }
		/** 3. otherwise, collect all the statements being deleted and create mutation **/
		else {
			/** collect all the statements being deleted and create error nodes **/
			Collection<CirStatement> in_statements = this.collect_statements_in(location);
			List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
			for(CirStatement in_statement : in_statements) {
				if(!(in_statement instanceof CirTagStatement)) {
					error_nodes.add(CirSemanticFactory.non_execute_statement(in_statement));
				}
			}
			
			/** construct the semantic mutations as [reach, errors*] **/
			if(error_nodes.isEmpty()) { return null; }	// no statements are deleted
			else {
				CirSemanticNode reach_node = 
						CirSemanticFactory.cover_statement(ast_cir_range.get_beg_statement());
				this.infer(reach_node, error_nodes); return this.cir_mutation(reach_node);
			}
		}
	}
	/* increment mutation operators */
	/**
	 * {
	 * 	COVER(expression.FIRST_STATEMENT)
	 * 	INC_VAL(expression.VARIABLE, DIFF)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OPPO(TextMutation mutation) throws Exception {
		/** 1. get the statement and create reachable node **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		CirStatement first_statement = (CirStatement) this.get_cir_location(location, CirStatement.class);
		CirSemanticNode reach_node = CirSemanticFactory.cover_statement(first_statement);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. create the increment error node based on operator **/
		CirSemanticNode error_node;
		switch(mutation.get_mode()) {
		/** x++ ==> ++x 
		 * 	{
		 * 		#temp = x;	|	#temp = x + 1;	<== inc_val{x, 1} if not used
		 * 		x = x + 1;	|	x = x + 1;
		 * 		use{#temp}?	|	use{#temp + 1}?	<== inc_val{#temp, 1} if used
		 * 	}
		 * **/
		case POST_PREV_INC:
		{
			CirExpression use_temp = ast_cir_range.get_result();
			if(use_temp == null || use_temp.statement_of() == null) {
				CirAssignStatement save_stmt = (CirAssignStatement) 
						this.get_cir_location(location, CirSaveAssignStatement.class);
				use_temp = save_stmt.get_rvalue();
			}
			error_node = CirSemanticFactory.inc_val(use_temp, 1);
		}
		break;
		/**
		 *	x++ ==> x--
		 * 	{
		 * 		#temp = x;	|	#temp = x;
		 * 		x = x + 1;	|	x = x - 1;	<== inc_val({x + 1}, -2)
		 * 		use{#temp}?	|	use{#temp}?
		 * 	}
		 */
		case POST_INC_DEC:
		{
			CirAssignStatement inc_stmt = (CirAssignStatement) 
					this.get_cir_location(location, CirIncreAssignStatement.class);
			error_node = CirSemanticFactory.inc_val(inc_stmt.get_rvalue(), -2);
		}
		break;
		/**
		 * 	++x ==> x++
		 * 	{
		 * 		x = x + 1;	|	x = x + 1;
		 * 		use{x}?		|	use{x - 1}?	<== inc_val{x, -1} if used
		 * 	}
		 */
		case PREV_POST_INC:
		{
			CirExpression use_point = ast_cir_range.get_result();
			if(use_point == null || use_point.statement_of() == null) {
				error_node = null;	// equivalent because not used at all
			}
			else {
				error_node = CirSemanticFactory.inc_val(use_point, -1);
			}
		}
		break;
		/**
		 * 	++x ==> --x
		 * 	{
		 * 		x = x + 1;	|	x = x - 1;	<== inc_val{x + 1, -2}
		 * 		use{x}?		|	use{x}?
		 * 	}
		 */
		case PREV_INC_DEC:
		{
			CirAssignStatement inc_stmt = (CirAssignStatement) 
					this.get_cir_location(location, CirIncreAssignStatement.class);
			error_node = CirSemanticFactory.inc_val(inc_stmt.get_rvalue(), -2);
		}
		break;
		/** invalid operator not supported here... **/
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_mode());
		}
		
		/** 3. the mutation is equivalent because no effects are made **/
		if(error_node == null) { return null; }
		/** 4. otherwise, create the increment mutation in semantic layer **/
		else {
			this.infer(reach_node, error_node); 
			return this.cir_mutation(reach_node);
		}
	}	
	/**
	 * {
	 * 	COVER(expression.FIRST_STATEMENT)
	 * 	INC_VAL(expression.VARIABLE, DIFF)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OMMO(TextMutation mutation) throws Exception {
		/** 1. get the statement and create reachable node **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		CirStatement first_statement = (CirStatement) this.get_cir_location(location, CirStatement.class);
		CirSemanticNode reach_node = CirSemanticFactory.cover_statement(first_statement);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. create the increment error node based on operator **/
		CirSemanticNode error_node;
		switch(mutation.get_mode()) {
		/** 
		 * 	x-- ==> --x
		 * 	{
		 * 		#temp = x;	|	#temp = x - 1;	<== inc_val(x, -1) if not used
		 * 		x = x - 1;	|	x = x - 1;
		 * 		use{#temp}?	|	use{#temp - 1}?	<== inc_val(#temp, -1) if used
		 * 	}
		 *  **/
		case POST_PREV_DEC:
		{
			CirExpression use_temp = ast_cir_range.get_result();
			if(use_temp == null || use_temp.statement_of() == null) {
				CirAssignStatement save_stmt = (CirAssignStatement) 
						this.get_cir_location(location, CirSaveAssignStatement.class);
				use_temp = save_stmt.get_rvalue();
			}
			error_node = CirSemanticFactory.inc_val(use_temp, -1);
		}
		break;
		/**
		 * 	x-- ==> x++;
		 * 	{
		 * 		#temp = x;	|	#temp = x;
		 * 		x = x - 1;	|	x = x + 1;	<== inc_val{x - 1, 2}
		 * 		use{#temp}?	|	use{#temp}?
		 * 	}
		 */
		case POST_DEC_INC:
		{
			CirAssignStatement inc_stmt = (CirAssignStatement) 
					this.get_cir_location(location, CirIncreAssignStatement.class);
			error_node = CirSemanticFactory.inc_val(inc_stmt.get_rvalue(), 2);
		}
		break;
		/**
		 * 	--x ==> x--;
		 * 	{
		 * 		x = x - 1;	|	x = x - 1;
		 * 		use{#x}?	|	use{#x + 1}	<== inc_val(#x, 1) if used
		 * 	}
		 */
		case PREV_POST_DEC:
		{
			CirExpression use_point = ast_cir_range.get_result();
			if(use_point == null || use_point.statement_of() == null) {
				error_node = null;	// equivalent because not used at all
			}
			else {
				error_node = CirSemanticFactory.inc_val(use_point, 1);
			}
		}
		break;
		/**
		 * 	--x ==> ++x;
		 * 	{
		 * 		x = x - 1;	|	x = x + 1;	<== inc_val(x - 1, 2)
		 * 		use{x}?		|	use{x}?
		 * 	}
		 */
		case PREV_DEC_INC:
		{
			CirAssignStatement inc_stmt = (CirAssignStatement) 
					this.get_cir_location(location, CirIncreAssignStatement.class);
			error_node = CirSemanticFactory.inc_val(inc_stmt.get_rvalue(), 2);
		}
		break;
		/** unsupported mutation operator found within the OMMO mutation **/
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_mode());
		}
		
		/** 3. the mutation is equivalent because no effects are made **/
		if(error_node == null) { return null; }
		/** 4. otherwise, create the increment mutation in semantic layer **/
		else {
			this.infer(reach_node, error_node);
			return this.cir_mutation(reach_node);
		}
	}
	/**
	 * {
	 * 	COVER(expression.LAST_STATEMENT)
	 * 	INC_VAL(expression.USE_POINTS, 1|-1)*
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_UIOI(TextMutation mutation) throws Exception {
		/** 1. get the reference being increased **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. construct the semantic mutation based on its usage points **/
		else {
			/** 3.1. get the last statement as the head of the traversal **/
			if(ast_cir_range.get_result() instanceof CirReferExpression) {
				CirReferExpression variable = (CirReferExpression) ast_cir_range.get_result();
				CirStatement head_statement = null;
				if(head_statement == null) head_statement = variable.statement_of();
				if(head_statement == null) head_statement = ast_cir_range.get_end_statement();
				if(head_statement == null) head_statement = this.find_real_statement_at(location);
				
				/** 3.2. collect all the usage points since the head statement in flow graph **/
				Collection<CirReferExpression> use_points = get_usage_points_after(head_statement, variable);
				
				/** 3.3. determine the difference and usage points based on mutation operator **/
				int difference;
				switch(mutation.get_mode()) {
				case PREV_INC_INS:	difference = 1;  use_points.add(variable); break;
				case POST_INC_INS:	difference = 1;  break;
				case PREV_DEC_INS:	difference = -1; use_points.add(variable); break;
				case POST_DEC_INS:	difference = -1; break;
				default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_mode());
				}
				
				/** 3.4. the mutation is equivalent because it's not used at all **/
				if(use_points.isEmpty()) { return null; }
				/** 3.5. otherwise, create the  **/
				else {
					
					CirSemanticNode reach_node = CirSemanticFactory.cover_statement(head_statement);
					List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
					for(CirReferExpression use_point : use_points) {
						error_nodes.add(CirSemanticFactory.inc_val(use_point, difference));
					}
					this.infer(reach_node, error_nodes); return this.cir_mutation(reach_node);
				}
			}
			else return null;	// invalid syntax
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	INC_VAL(expression, 1 | -1)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VTWD(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for VTWD **/
			else {
				/* (1) determine the value for increasing the target expression */
				int value;
				switch(mutation.get_mode()) {
				case SUCC_VAL:	value = 1;	break;
				case PRED_VAL:	value =-1;	break;
				default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_mode());
				}
				
				/* (2) construct the reachable and error nodes for semantic mutation */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.inc_val(expression, value);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/* negative mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	NOT_EQUALS(expression, 0)
	 * 	NEG_VAL(expression, -)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OANG(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OANG **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode infect_node = CirSemanticFactory.not_equals(expression, 0);
				CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.negative);
				this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	NOT_EQUALS(expression, 0)
	 * 	NEG_VAL(expression, ~)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OBNG(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OBNG **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode infect_node = CirSemanticFactory.not_equals(expression, 0);
				CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.bit_not);
				this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	NEG_VAL(expression, !)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OLNG(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OLNG **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * ONDU ==> OANG|OBNG|OLNG
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_ONDU(TextMutation mutation) throws Exception {
		switch(mutation.get_mode()) {
		case ANG_DELETE:	return this.parse_OANG(mutation);
		case BNG_DELETE:	return this.parse_OBNG(mutation);
		case LNG_DELETE:	return this.parse_OLNG(mutation);
		default: throw new IllegalArgumentException("Invalid operator: " + mutation.get_mode());
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	SMALLER_TN(expression, 0)
	 * 	NEG_VAL(expression, -)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VABS(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for VABS **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode infect_node = CirSemanticFactory.smaller_tn(expression, 0);
				CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.negative);
				this.infer(reach_node, infect_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/* constant mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	EQUAL_WITH(expression, false|true)
	 * 	SET_VAL(expression, true|false) + NEG_VAL(expression, !)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VBCR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for VBCR **/
			else {
				/* a. determine the value to be set to the mutated expression */
				boolean value;
				switch(mutation.get_mode()) {
				case MUT_TRUE:	value = true;	break;
				case MUT_FALSE:	value = false;	break;
				default: throw new IllegalArgumentException("Unsupport operator: " + mutation.get_mode());
				}
				
				/* b. create the reachability, infection and error nodes */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode infect_node = CirSemanticFactory.equal_with(expression, !value);
				List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
				error_nodes.add(CirSemanticFactory.set_val(expression, value));
				error_nodes.add(CirSemanticFactory.neg_val(expression, COperator.logic_not));
				this.infer(reach_node, infect_node, error_nodes); 
				return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	SET_VAL(expression, value) + INC_VAL|NEG_VAL?
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_CRCR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for VBCR **/
			else {
				/* a. construct the reachability node */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				List<CirSemanticNode> error_nodes = new ArrayList<CirSemanticNode>();
				Object constant = this.get_constant(expression);
				
				/* b. the mutation does not refer to any constant in mutation */
				if(constant == null) { return null; }
				
				/* c. construct the state error nodes based on operators */
				switch(mutation.get_mode()) {
				/** expression <== 0 **/
				case CST_TOT_ZRO:	
					error_nodes.add(CirSemanticFactory.set_val(expression, 0)); break;
				/** expression <== 1 **/
				case CST_POS_ONE:	
					error_nodes.add(CirSemanticFactory.set_val(expression, 1)); break;
				/** expression <== -1 **/
				case CST_NEG_ONE:
					error_nodes.add(CirSemanticFactory.set_val(expression,-1)); break;
				/** expression <== -expression **/
				case CST_NEG_CST:
				{
					Object value;
					if(constant instanceof Character) {
						value = -((Character) constant).charValue();
					}
					else if(constant instanceof Integer) {
						value = -((Integer) constant).intValue();
					}
					else if(constant instanceof Long) {
						value = -((Long) constant).longValue();
					}
					else if(constant instanceof Float) {
						value = -((Float) constant).floatValue();
					}
					else {
						value = -((Double) constant).doubleValue();
					}
					error_nodes.add(CirSemanticFactory.set_val(expression, value));
					error_nodes.add(CirSemanticFactory.neg_val(expression, COperator.negative));
				}
				break;
				/** expression <== expression + 1 **/
				case CST_INC_ONE:
				{
					Object value;
					if(constant instanceof Character) {
						value = ((Character) constant).charValue() + 1;
					}
					else if(constant instanceof Integer) {
						value = ((Integer) constant).intValue() + 1;
					}
					else if(constant instanceof Long) {
						value = ((Long) constant).longValue() + 1;
					}
					else if(constant instanceof Float) {
						value = ((Float) constant).floatValue() + 1;
					}
					else {
						value = ((Double) constant).doubleValue() + 1;
					}
					error_nodes.add(CirSemanticFactory.set_val(expression, value));
					error_nodes.add(CirSemanticFactory.inc_val(expression, 1));
				}
				break;
				/** expression <== expression - 1 **/
				case CST_DEC_ONE:
				{
					Object value;
					if(constant instanceof Character) {
						value = ((Character) constant).charValue() - 1;
					}
					else if(constant instanceof Integer) {
						value = ((Integer) constant).intValue() - 1;
					}
					else if(constant instanceof Long) {
						value = ((Long) constant).longValue() - 1;
					}
					else if(constant instanceof Float) {
						value = ((Float) constant).floatValue() - 1;
					}
					else {
						value = ((Double) constant).doubleValue() - 1;
					}
					error_nodes.add(CirSemanticFactory.set_val(expression, value));
					error_nodes.add(CirSemanticFactory.inc_val(expression, -1));
				}
				break;
				default: throw new IllegalArgumentException("Invalid mode: " + mutation.get_mode());
				}
				
				/* d. construct the semantic mutation based on nodes */
				this.infer(reach_node, error_nodes); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	SET_VAL(expression, value);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_CCCR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			String replace = mutation.get_replace();
			int begindex = replace.indexOf('(') + 1;
			int endindex = replace.lastIndexOf(')');
			String code = replace.substring(begindex, endindex);
			Object value = this.get_constant(code.strip());
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for CCCR **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.set_val(expression, value);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/* reference mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	CHG_VAL(expression, ?)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_CCSR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for CCSR **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	CHG_VAL(expression, ?)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VARR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for CCSR **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	CHG_VAL(expression, ?)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VPRR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for CCSR **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	CHG_VAL(expression, ?)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VSRR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for CCSR **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	CHG_VAL(expression, ?)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VSFR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for CCSR **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/**
	 * {
	 * 	COVER(expression.STATEMENT)
	 * 	CHG_VAL(expression, ?)
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_VTRR(TextMutation mutation) throws Exception {
		/** 1. get the range of the seeded location and result **/
		AstNode location = this.find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for CCSR **/
			else {
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
				this.infer(reach_node, error_node); return this.cir_mutation(reach_node);
			}
		}
	}
	/* arith mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	......
	 * 	CHG_VAL(expression, ?);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OAXN(TextMutation mutation) throws Exception {
		/** 1. get the binary expression to which the mutation refers to **/
		AstBinaryExpression location = (AstBinaryExpression) this.
				find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location); 
		COperator operator = location.get_operator().get_operator();
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirComputeExpression expression = 
					(CirComputeExpression) ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OAxN **/
			else {
				/* a. construct the reachability node for the expression mutation */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				
				/* b. construct the reachability graph based on operator */
				MutationMode mode = mutation.get_mode();
				switch(operator) {
				case arith_add:	this.parse_arith_add_to_other(reach_node, expression, mode); break;
				case arith_sub:	this.parse_arith_sub_to_other(reach_node, expression, mode); break;
				case arith_mul:	this.parse_arith_mul_to_other(reach_node, expression, mode); break;
				case arith_div:	this.parse_arith_div_to_other(reach_node, expression, mode); break;
				case arith_mod:	this.parse_arith_mod_to_other(reach_node, expression, mode); break;
				default: throw new IllegalArgumentException("Invalid mode: " + operator);
				}
				
				/* c. return the CIR based mutation */	
				if(reach_node.get_ou_degree() > 0)
					return this.cir_mutation(reach_node);
				else return null;
			}
		}
	}
	private void parse_arith_add_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {y != 0} ==> [error] **/
		case ADD_SUB:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} or {y != 0} **/
		case ADD_MUL:
		case ADD_BAN:
		case ADD_LSH:
		case ADD_RSH:
		case ADD_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y = 0} or {y != 0} **/
		case ADD_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {y = 0} or {y < 0, x not in (2y, y]} or {y > 0} **/
		case ADD_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(loperand, "(2y, y]");
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node2, infect_node3 }, error_node2);
			this.infer(reach_node, infect_node4, error_node2);
		}
		break;
		/** {x & y != 0} **/
		case ADD_BOR:
		case ADD_BXR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bit_intersc(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x, y not in boolean.range} **/
		case ADD_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_in_range(loperand, "@boolean");
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(roperand, "@boolean");
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {any} **/
		case ADD_GRT:
		case ADD_GRE:
		case ADD_SMT:
		case ADD_SME:
		case ADD_EQV:
		case ADD_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_sub_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {y != 0} ==> [error] **/
		case SUB_ADD:
		case SUB_BOR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} or {y != 0} **/
		case SUB_MUL:
		case SUB_BAN:
		case SUB_LAN:
		case SUB_LSH:
		case SUB_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y = 0} or {y != 0} **/
		case SUB_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {y = 0} or {y > 0, x not in [y, 2y)} or {y < 0}**/
		case SUB_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(loperand, "[y, 2y)");
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node2, infect_node3 }, error_node2);
			this.infer(reach_node, infect_node4, error_node2);
		}
		break;
		/** {x & y != y} **/
		case SUB_BXR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bno_include(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {any} **/
		case SUB_LOR:
		case SUB_GRT:
		case SUB_GRE:
		case SUB_SMT:
		case SUB_SME:
		case SUB_EQV:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x, y not in boolean.range} **/
		case SUB_NEQ:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_in_range(loperand, "@boolean");
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(roperand, "@boolean");
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_mul_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0} or {y != 0} **/
		case MUL_ADD:
		case MUL_SUB:
		case MUL_BOR:
		case MUL_BXR:
		case MUL_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y = 0} or {x != 0} **/
		case MUL_DIV:
		case MUL_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0} and {y != 0} **/
		case MUL_BAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {x != 0} **/
		case MUL_LSH:
		case MUL_RSH:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x, y not in boolean.range} **/
		case MUL_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_in_range(loperand, "@boolean");
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(roperand, "@boolean");
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {any} **/
		case MUL_GRT:
		case MUL_GRE:
		case MUL_SMT:
		case MUL_SME:
		case MUL_EQV:
		case MUL_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_div_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {any} **/
		case DIV_ADD:
		case DIV_SUB:
		case DIV_MOD:
		case DIV_BOR:
		case DIV_BXR:
		case DIV_LOR:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x != 0} **/
		case DIV_MUL:
		case DIV_BAN:
		case DIV_LSH:
		case DIV_RSH:
		case DIV_LAN:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {y < 0} and {x in range (2y, 0]} **/
		case DIV_SMT:
		case DIV_SME:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.in_range(loperand, "(2y, 0]");
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {y > 0} and {x in range [0, 2y)} **/
		case DIV_GRT:
		case DIV_GRE:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.in_range(loperand, "[0, 2y)");
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {x > y and y > 0} or {x < y and y < 0} **/
		case DIV_EQV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
			this.infer(reach_node, new CirSemanticNode[] { infect_node3, infect_node4 }, error_node);
		}
		break;
		/** {y > 0 and x in [0, y)} or {y < 0 and x in (y, 0]}**/
		case DIV_NEQ:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.in_range(loperand, "[0, y)");
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.in_range(loperand, "(y, 0]");
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
			this.infer(reach_node, new CirSemanticNode[] { infect_node3, infect_node4 }, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_mod_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {y < 0, x not in (2y, y]} or {y > 0} **/
		case MOD_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(loperand, "(2y, y]");
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
			this.infer(reach_node, infect_node3, error_node);
		}
		break;
		/** {y > 0, x not in [y, 2y)} or {y < 0} **/
		case MOD_SUB:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(loperand, "[y, 2y)");
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
			this.infer(reach_node, infect_node3, error_node);
		}
		break;
		/** {x != 0} **/
		case MOD_MUL:
		case MOD_DIV:
		case MOD_BAN:
		case MOD_LSH:
		case MOD_RSH:
		case MOD_LAN:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {any} **/
		case MOD_BOR:
		case MOD_BXR:
		case MOD_LOR:
		case MOD_GRT:
		case MOD_GRE:
		case MOD_SMT:
		case MOD_SME:
		case MOD_EQV:
		case MOD_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	/* bitws mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	......
	 * 	CHG_VAL(expression, ?);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OBXN(TextMutation mutation) throws Exception {
		/** 1. get the binary expression to which the mutation refers to **/
		AstBinaryExpression location = (AstBinaryExpression) this.
				find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location); 
		COperator operator = location.get_operator().get_operator();
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirComputeExpression expression = 
					(CirComputeExpression) ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OAxN **/
			else {
				/* a. construct the reachability node for the expression mutation */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				
				/* b. construct the reachability graph based on operator */
				MutationMode mode = mutation.get_mode();
				switch(operator) {
				case bit_and:	this.parse_bitws_and_to_other(reach_node, expression, mode); break;	
				case bit_or:	this.parse_bitws_ior_to_other(reach_node, expression, mode); break;		
				case bit_xor:	this.parse_bitws_xor_to_other(reach_node, expression, mode); break;		
				case left_shift:this.parse_bitws_lsh_to_other(reach_node, expression, mode); break;	
				case righ_shift:this.parse_bitws_rsh_to_other(reach_node, expression, mode); break;		
				default: throw new IllegalArgumentException("Invalid mode: " + operator);
				}
				
				/* c. return the CIR based mutation */	
				if(reach_node.get_ou_degree() > 0)
					return this.cir_mutation(reach_node);
				else return null;
			}
		}
	}
	private void parse_bitws_and_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0} or {y != 0} **/
		case BAN_ADD:
		case BAN_SUB:
		case BAN_BOR:
		case BAN_BXR:
		case BAN_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0, y != 0} **/
		case BAN_MUL:
		case BAN_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {y = 0} or {x != 0} **/
		case BAN_DIV:
		case BAN_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0} **/
		case BAN_LSH:
		case BAN_RSH:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {any} **/
		case BAN_GRT:
		case BAN_GRE:
		case BAN_SMT:
		case BAN_SME:
		case BAN_EQV:
		case BAN_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_ior_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0, y != 0} **/
		case BOR_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {y != 0} **/
		case BOR_SUB:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} or {y != 0} **/
		case BOR_MUL:
		case BOR_BAN:
		case BOR_LSH:
		case BOR_RSH:
		case BOR_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y = 0} or {y != 0} **/
		case BOR_DIV:
		case BOR_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x & y != 0} **/
		case BOR_BXR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bit_intersc(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x, y not in boolean.range} **/
		case BOR_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_in_range(loperand, "@boolean");
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(roperand, "@boolean");
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** any **/
		case BOR_GRT:
		case BOR_GRE:
		case BOR_SMT:
		case BOR_SME:
		case BOR_EQV:
		case BOR_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_xor_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0, y != 0} **/
		case BXR_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] { infect_node1, infect_node2 }, error_node);
		}
		break;
		/** {x & y != y} **/
		case BXR_SUB:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bno_include(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} or {y != 0} **/
		case BXR_MUL:
		case BXR_BAN:
		case BXR_LSH:
		case BXR_RSH:
		case BXR_LAN:
		case BXR_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y = 0} or {y != 0} **/
		case BXR_DIV:
		case BXR_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x & y != 0} **/
		case BXR_BOR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bit_intersc(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {any} **/
		case BXR_GRT:
		case BXR_GRE:
		case BXR_SMT:
		case BXR_SME:
		case BXR_EQV:
		case BXR_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_lsh_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0} or {y != 0} **/
		case LSH_ADD:
		case LSH_SUB:
		case LSH_BOR:
		case LSH_BXR:
		case LSH_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0} **/
		case LSH_MUL:
		case LSH_BAN:
		case LSH_LAN:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {y != 0} or {y = 0} **/
		case LSH_DIV:
		case LSH_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {any} **/
		case LSH_GRT:
		case LSH_GRE:
		case LSH_SMT:
		case LSH_SME:
		case LSH_EQV:
		case LSH_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x != 0, y != 0} **/
		case LSH_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_rsh_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0} or {y != 0} **/
		case RSH_ADD:
		case RSH_SUB:
		case RSH_BOR:
		case RSH_BXR:
		case RSH_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0} **/
		case RSH_MUL:
		case RSH_BAN:
		case RSH_LAN:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {y != 0} or {y = 0} **/
		case RSH_DIV:
		case RSH_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {any} **/
		case RSH_GRT:
		case RSH_GRE:
		case RSH_SMT:
		case RSH_SME:
		case RSH_EQV:
		case RSH_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x != 0, y != 0} **/
		case RSH_LSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	/* logic mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	......
	 * 	NEG_VAL(expression, logic_not) + SET_VAL(expression, true|false)?
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OLXN(TextMutation mutation) throws Exception {
		/** 1. get the binary expression to which the mutation refers to **/
		AstBinaryExpression location = (AstBinaryExpression) this.
				find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location); 
		COperator operator = location.get_operator().get_operator();
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirExpression expression = ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OLxN **/
			else {
				/* a. construct the reachability node for the expression mutation */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				
				/* b. get the left and right operand used in C-based intermediate representation */
				AstCirPair loperand_location = this.get_cir_range(CTypeAnalyzer.get_expression_of(location.get_loperand()));
				AstCirPair roperand_location = this.get_cir_range(CTypeAnalyzer.get_expression_of(location.get_roperand()));
				CirExpression loperand = loperand_location.get_result(), roperand = roperand_location.get_result();
				
				/* b. construct the reachability graph based on operator */
				MutationMode mode = mutation.get_mode();
				switch(operator) {
				case logic_and:	this.parse_logic_and_to_other(reach_node, expression, loperand, roperand, mode); break;
				case logic_or:	this.parse_logic_ior_to_other(reach_node, expression, loperand, roperand, mode); break;
				default: throw new IllegalArgumentException("Invalid mode: " + operator);
				}
				
				/* c. return the CIR based mutation */	
				if(reach_node.get_ou_degree() > 0)
					return this.cir_mutation(reach_node);
				else return null;
			}
		}
	}
	private void parse_logic_and_to_other(CirSemanticNode reach_node, CirExpression expression, 
			CirExpression loperand, CirExpression roperand, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != y} ==> [true] **/
		case LAN_ADD:
		case LAN_BOR:
		case LAN_LOR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node2 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node, new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {x = true} or {y = true} **/
		case LAN_SUB:
		case LAN_BXR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, true);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** equivalent mutation **/
		case LAN_MUL:	
		case LAN_BAN:	
		break;
		/** {y = false} **/
		case LAN_DIV:
		case LAN_MOD:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(roperand, false);
			CirSemanticNode error_node = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x = true}[not] **/
		case LAN_LSH:
		case LAN_RSH:
		case LAN_GRT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, true);
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {y = false}[true] **/
		case LAN_GRE:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(roperand, false);
			CirSemanticNode error_node1 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node2 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {y = true}[not] **/
		case LAN_SMT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x = false}[true] **/
		case LAN_SME:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, false);
			CirSemanticNode error_node1 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node2 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x = false, y = false} [true] **/
		case LAN_EQV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, false);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, false);
			CirSemanticNode error_node1 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node2 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {x = true} or {y = true} [not] **/
		case LAN_NEQ:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, true);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_logic_ior_to_other(CirSemanticNode reach_node, CirExpression expression, 
			CirExpression loperand, CirExpression roperand, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** equivalent mutation **/
		case LOR_ADD: break;
		case LOR_BOR: break;
		/** {x = true, y = true} [false] **/
		case LOR_SUB:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, true);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {x != y} [false] **/
		case LOR_MUL:
		case LOR_BAN:
		case LOR_LAN:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {y = false} [trap] | {x = false} [false] **/
		case LOR_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, false);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, false);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node3);
			this.infer(reach_node, infect_node2, new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {y = false}[trap] or {x = true, y = true} [false] **/
		case LOR_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, false);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, true);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node2 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node2, infect_node3}, 
					new CirSemanticNode[] {error_node2, error_node3});
		}
		break;
		/** {x = true, y = true} [false] **/
		case LOR_BXR:
		case LOR_NEQ:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, true);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {x = false, y = true} [false] **/
		case LOR_LSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, false);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {y = true} [false] **/
		case LOR_RSH:
		case LOR_GRT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(roperand, true);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x = false} [not] **/
		case LOR_GRE:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, false);
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x = true} [false] **/
		case LOR_SMT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, true);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {y = false} [not] **/
		case LOR_SME:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(roperand, false);
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x = false} or {y = false} [not] **/
		case LOR_EQV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, false);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, false);
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	/* relation mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	......
	 * 	NEG_VAL(expression, logic_not) + SET_VAL(expression, true|false)?
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_ORXN(TextMutation mutation) throws Exception {
		/** 1. get the binary expression to which the mutation refers to **/
		AstBinaryExpression location = (AstBinaryExpression) this.
				find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location); 
		COperator operator = location.get_operator().get_operator();
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirComputeExpression expression = 
					(CirComputeExpression) ast_cir_range.get_result();
			CirStatement statement = expression.statement_of();
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OLxN **/
			else {
				/* a. construct the reachability node for the expression mutation */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				
				/* b. construct the reachability graph based on operator */
				MutationMode mode = mutation.get_mode();
				switch(operator) {
				case greater_tn: this.parse_greater_tn_to_other(reach_node, expression, mode); break;
				case greater_eq: this.parse_greater_eq_to_other(reach_node, expression, mode); break;
				case smaller_tn: this.parse_smaller_tn_to_other(reach_node, expression, mode); break;
				case smaller_eq: this.parse_smaller_eq_to_other(reach_node, expression, mode); break;
				case equal_with: this.parse_equal_with_to_other(reach_node, expression, mode); break;
				case not_equals: this.parse_not_equals_to_other(reach_node, expression, mode); break;
				default: throw new IllegalArgumentException("Invalid mode: " + operator);
				}
				
				/* c. return the CIR based mutation */	
				if(reach_node.get_ou_degree() > 0)
					return this.cir_mutation(reach_node);
				else return null;
			}
		}
	}
	private void parse_greater_tn_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x > 0, x = -y} [false] | {x <= y} [true] **/
		case GRT_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.is_negative(loperand, roperand);
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x < y} [true] **/
		case GRT_SUB:
		case GRT_BXR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x = 0, y < 0}[false] | {x > 0, y = 0}[false] | {x <= y}[true] **/
		case GRT_MUL:
		case GRT_BAN:
		case GRT_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_tn(loperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node5 = CirSemanticFactory.smaller_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] { infect_node1, infect_node2 }, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] { infect_node3, infect_node4 }, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = 0, y < 0}[false] | {y = 0}[trap] | {x <= y}[true] **/
		case GRT_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = ky, y > 0}[false] | {y = 0}[trap] | {x <= y}[true] **/
		case GRT_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.is_multiply(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x <= y}[true] **/
		case GRT_BOR:
		case GRT_LOR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.smaller_eq(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {x = 0, y < 0}[false] | {x > y, y > 8}[false] | {x <= y}[true] **/
		case GRT_LSH:
		case GRT_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(roperand, 8);
			CirSemanticNode infect_node5 = CirSemanticFactory.smaller_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] { infect_node1, infect_node2 }, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] { infect_node3, infect_node4 }, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = y} [true] **/
		case GRT_GRE:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x > y}[false] | {x < y}[true] **/
		case GRT_SMT:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {any} [not] **/
		case GRT_SME:
		{
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x = y}[true] | {x > y}[false] **/
		case GRT_EQV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x < y} [true] **/
		case GRT_NEQ:
		{
			CirSemanticNode infect_node = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_greater_eq_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x = -y, x >= y}[false] | {x < y}[true] **/
		case GRE_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.is_negative(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_eq(loperand, roperand);
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = y} [false] | {x < y} [true] **/
		case GRE_SUB:
		case GRE_BXR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = 0, y <= 0}[false] | {x >= 0, y = 0}[false] | {x < y} [true] **/
		case GRE_MUL:
		case GRE_BAN:
		case GRE_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_eq(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_eq(loperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node5 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = 0, y < 0}[false] | {y = 0} [trap] | {x < y} [true] **/
		case GRE_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = ky, y < 0}[false] | {y = 0}[trap] | {x < y}[true] **/
		case GRE_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.is_multiply(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = 0, y = 0}[false] | {x < y}[true] **/
		case GRE_BOR:
		case GRE_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = 0, y <= 0}[false] | {x >= y, y > 8}[false] | {x < y}[true] **/
		case GRE_LSH:
		case GRE_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_eq(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_eq(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(roperand, 8);
			CirSemanticNode infect_node5 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] { infect_node1, infect_node2 }, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] { infect_node3, infect_node4 }, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x == y}[false] **/
		case GRE_GRT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {any} [not] **/
		case GRE_SMT:
		{
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x > y}[false] | {x < y}[true] **/
		case GRE_SME:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x > y}[false] **/
		case GRE_EQV:
		{
			CirSemanticNode infect_node = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x == y}[false] | {x < y}[true] **/
		case GRE_NEQ:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_smaller_tn_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x = -y, x < 0}[false] | {x >= y}[true] **/
		case SMT_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.is_negative(loperand, roperand);
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x > y} [true] **/
		case SMT_SUB:
		case SMT_BXR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x = 0, y > 0}[false] | {y = 0, x < 0}[false] | {x >= y}[true] **/
		case SMT_MUL:
		case SMT_BAN:
		case SMT_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_tn(loperand, 0);
			CirSemanticNode infect_node5 = CirSemanticFactory.greater_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = 0, y > 0}[false] | {y = 0}[trap] | {x >= y}[true] **/
		case SMT_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = ky, y < 0}[false] | {y = 0}[trap] | {x > y}[true] **/
		case SMT_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.is_multiply(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x >= y}[true] **/
		case SMT_BOR:
		case SMT_LOR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.greater_eq(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x = 0, y > 0}[false] | {y > 8, x < y}[false] | {x >= y}[true] **/
		case SMT_LSH:
		case SMT_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(roperand, 8);
			CirSemanticNode infect_node5 = CirSemanticFactory.greater_eq(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = y} [true] **/
		case SMT_SME:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x < y}[false] | {x > y}[true] **/
		case SMT_GRT:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** any [not] **/
		case SMT_GRE:
		{
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x < y}[false] | {x = y}[true] **/
		case SMT_EQV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x > y}[true] **/
		case SMT_NEQ:
		{
			CirSemanticNode infect_node = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_smaller_eq_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x = -y, x <= 0}[false] | {x > y}[true] **/
		case SME_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_eq(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.is_negative(loperand, roperand);
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = y}[false] | {x > y}[true] **/
		case SME_SUB:
		case SME_BXR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = 0, y >= 0}[false] | {y = 0, x <= 0}[false] | {x > y}[true] **/
		case SME_MUL:
		case SME_BAN:
		case SME_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_eq(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.smaller_eq(loperand, 0);
			CirSemanticNode infect_node5 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = 0, y < 0}[false] | {y = 0}[trap] | {x > y}[true] **/
		case SME_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = ky, y < 0}[false] | {y = 0}[trap] | {x > y}[true] **/
		case SME_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.is_multiply(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.smaller_tn(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = 0, y = 0}[false] | {x > y}[true] **/
		case SME_BOR:
		case SME_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] {error_node1, error_node2});
			this.infer(reach_node, infect_node3, new CirSemanticNode[] {error_node1, error_node3});
		}
		break;
		/** {x = 0, 0 <= y}[false] | {y > 8, x <= y}[false] | {x > y}[true] **/
		case SME_LSH:
		case SME_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_eq(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.smaller_eq(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(roperand, 8);
			CirSemanticNode infect_node5 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node5, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = y} [false] **/
		case SME_SMT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {any} [not] **/
		case SME_GRT:
		{
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x < y} [false] | {x > y} [true] **/
		case SME_GRE:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x < y} [false] **/
		case SME_EQV:
		{
			CirSemanticNode infect_node = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x = y} [false] | {x > y} [true] **/
		case SME_NEQ:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_equal_with_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x = 0, y = 0}[false] | {x != y, x != -y} [true] **/
		case EQV_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.not_negative(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {any} [not] **/
		case EQV_SUB:
		case EQV_BXR:
		{
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x = 0, y = 0}[false] | {x != 0, y != 0, x != y}[true] **/
		case EQV_MUL:
		case EQV_BAN:
		case EQV_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node5 = CirSemanticFactory.not_equals(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4, infect_node5}, 
					new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {y = 0}[trap] | {x != 0, y != 0, x != y}[true] **/
		case EQV_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.equal_with(roperand, 0);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node3 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2, infect_node3}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node4, error_node3);
		}
		break;
		/** {x = ky, y != 0}[false] | {y = 0}[trap] | {x != y}[true] **/
		case EQV_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.is_multiply(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node4 = CirSemanticFactory.not_equals(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, error_node4);
			this.infer(reach_node, infect_node4, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = 0, y = 0}[false] | {x != y}[true] **/
		case EQV_BOR:
		case EQV_LOR:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.not_equals(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = 0, y = 0}[false] | {x = y, y > 8}[false] | {x != y, y <= 8}[true] **/
		case EQV_LSH:
		case EQV_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(roperand, 8);
			CirSemanticNode infect_node5 = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode infect_node6 = CirSemanticFactory.smaller_eq(roperand, 8);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node,
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node,
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node,
					new CirSemanticNode[] {infect_node5, infect_node6}, 
					new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** [any] {not} **/
		case EQV_NEQ:
		{
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, error_node);
		}
		break;
		/** {x > y}[true] | {x = y}[false] **/
		case EQV_GRT:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node3 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x > y}[true] **/
		case EQV_GRE:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x < y}[true] | {x = y}[false] **/
		case EQV_SMT:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node3 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x < y}[true] **/
		case EQV_SME:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_not_equals_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x = y}[true] | {x != y, x = -y}[false] **/
		case NEQ_ADD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode infect_node3 = CirSemanticFactory.is_negative(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node3 });
			this.infer(reach_node,
					new CirSemanticNode[] {infect_node2, infect_node3}, 
					new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** equivalent mutation for impossible constraint **/
		case NEQ_SUB:
		case NEQ_BXR:
		break;
		/** {x = 0}[false] | {y = 0}[false] | {x = y}[true] **/
		case NEQ_MUL:
		case NEQ_BAN:
		case NEQ_LAN:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x = 0}[false] | {y = 0}[trap] | {x = y}[true] **/
		case NEQ_DIV:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(loperand, roperand);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
			this.infer(reach_node, infect_node2, error_node4);
		}
		break;
		/** {x = ky, y != 0}[false] | {x = y}[true] | {y = 0}[trap] **/
		case NEQ_MOD:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.is_multiply(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.equal_with(roperand, 0);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			CirSemanticNode error_node4 = CirSemanticFactory.traping(statement);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node3, new CirSemanticNode[] { error_node1, error_node3 });
			this.infer(reach_node, infect_node4, error_node4);
		}
		break;
		/** {x = y}[true] **/
		case NEQ_BOR:
		case NEQ_LOR:
		{
			CirSemanticNode infect_node = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node, new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x = 0, y != 0}[false] | {x != y, y > 8}[false] **/
		case NEQ_LSH:
		case NEQ_RSH:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node3 = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode infect_node4 = CirSemanticFactory.greater_tn(roperand, 8);
			
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node1, infect_node2}, 
					new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, 
					new CirSemanticNode[] {infect_node3, infect_node4}, 
					new CirSemanticNode[] { error_node1, error_node2 });
		}
		break;
		/** {x < y}[false] **/
		case NEQ_GRT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {x < y}[false] | {x = y}[true] **/
		case NEQ_GRE:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.smaller_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x > y}[false] | {x = y}[true] **/
		case NEQ_SME:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			CirSemanticNode error_node3 = CirSemanticFactory.set_val(expression, true);
			this.infer(reach_node, infect_node1, new CirSemanticNode[] { error_node1, error_node2 });
			this.infer(reach_node, infect_node2, new CirSemanticNode[] { error_node1, error_node3 });
		}
		break;
		/** {x > y}[false] **/
		case NEQ_SMT:
		{
			CirSemanticNode infect_node = CirSemanticFactory.greater_tn(loperand, roperand);
			CirSemanticNode error_node1 = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			CirSemanticNode error_node2 = CirSemanticFactory.set_val(expression, false);
			this.infer(reach_node, infect_node, new CirSemanticNode[] {error_node1, error_node2});
		}
		break;
		/** {any}[not] **/
		case NEQ_EQV:
		{
			CirSemanticNode error_node = CirSemanticFactory.neg_val(expression, COperator.logic_not);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	/* assign mutation operators */
	/**
	 * {
	 * 	COVER(expression.STATEMENT);
	 * 	......
	 * 	CHG_VAL(expression.RVALUE);
	 * }
	 * @param mutation
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OEXA(TextMutation mutation) throws Exception {
		/** 1. get the binary expression to which the mutation refers to **/
		AstBinaryExpression location = (AstBinaryExpression) this.
				find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirAssignStatement statement = (CirAssignStatement) this.
					get_cir_location(location, CirAssignStatement.class);
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OLxN **/
			else {
				/* p. get the key nodes for constructing errors and constraints */
				CirExpression loperand = statement.get_lvalue();
				CirExpression roperand = statement.get_rvalue();
				CirExpression expression = statement.get_rvalue();
				
				/* a. construct the reachability node for the expression mutation */
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				
				/* b. construct the reachability graph based on operator */
				MutationMode mode = mutation.get_mode();
				switch(mode) {
				/** {x != 0, y != 0}[error] **/
				case ASG_ADD:
				case ASG_BOR:
				case ASG_BXR:
				{
					CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
					CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
					CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
					this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
				}
				break;
				/** {y != 0}[error] **/
				case ASG_SUB:
				{
					CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
					CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
					this.infer(reach_node, infect_node, error_node);
				}
				break;
				/** {y != 1}[error] **/
				case ASG_MUL:
				case ASG_BAN:
				{
					CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 1);
					CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
					this.infer(reach_node, infect_node, error_node);
				}
				break;
				/** {y != 1}[error] | {y = 0}[trap] **/
				case ASG_DIV:
				{
					CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(roperand, 1);
					CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
					CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
					CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
					this.infer(reach_node, infect_node1, error_node1);
					this.infer(reach_node, infect_node2, error_node2);
				}
				break;
				/** {y = 0}[trap] | {y != 0}[error] **/
				case ASG_MOD:
				{
					CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(roperand, 0);
					CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
					CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
					CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
					this.infer(reach_node, infect_node1, error_node1);
					this.infer(reach_node, infect_node2, error_node2);
				}
				break;
				/** {x != 0}[error] **/
				case ASG_LSH:
				case ASG_RSH:
				{
					CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
					CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
					this.infer(reach_node, infect_node, error_node);
				}
				break;
				/** invalid operator for OExA **/
				default: throw new IllegalArgumentException("Unsupport operator: " + mode);
				}
				
				/* c. return the CIR based mutation */	
				if(reach_node.get_ou_degree() > 0)
					return this.cir_mutation(reach_node);
				else return null;
			}
		}
	}
	/**
	 * {
	 * 	COVER(assignment.STATEMENT);
	 * 	......
	 * 	CHG_VAL(assignment.RVALUE);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OAXA(TextMutation mutation) throws Exception {
		/** 1. declarations **/
		AstBinaryExpression location = (AstBinaryExpression) this.
				find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		COperator operator = location.get_operator().get_operator();
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirAssignStatement statement = (CirAssignStatement) this.
					get_cir_location(location, CirAssignStatement.class);
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OAxA **/
			else {
				/* a. create the reaching node for constructing the semantic mutation */
				MutationMode mode = mutation.get_mode();
				CirComputeExpression expression = (CirComputeExpression) statement.get_rvalue();
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				
				/* b. parsing the text mutation based operator class */
				switch(operator) {
				case arith_add_assign:
					this.parse_arith_add_assign_to_other(reach_node, expression, mode);
					break;
				case arith_sub_assign:
					this.parse_arith_sub_assign_to_other(reach_node, expression, mode);
					break;
				case arith_mul_assign:
					this.parse_arith_mul_assign_to_other(reach_node, expression, mode);
					break;
				case arith_div_assign:
					this.parse_arith_div_assign_to_other(reach_node, expression, mode);
					break;
				case arith_mod_assign:
					this.parse_arith_mod_assign_to_other(reach_node, expression, mode);
					break;
				default: throw new IllegalArgumentException("Invalid " + operator);
				}
				
				/* c. return the CIR based mutation */	
				if(reach_node.get_ou_degree() > 0)
					return this.cir_mutation(reach_node);
				else return null;
			}
		}
	}
	private void parse_arith_add_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {y != 0}[error] **/
		case ADD_SUB_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 2} | {y != 2} **/
		case ADD_MUL_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 2);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 2);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y = 0}[trap] | {y != 0}[error] **/
		case ADD_DIV_A:
		case ADD_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0} | {y != 0} **/
		case ADD_BAN_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0, y != 0} **/
		case ADD_BOR_A:
		case ADD_BXR_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** {any} **/
		case ADD_LSH_A:
		case ADD_RSH_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_sub_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {y != 0} **/
		case SUB_ADD_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);	
		}
		break;
		/** {any} **/
		case SUB_MUL_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);	
		}
		break;
		/** {y = 0}[trap] | {y != 0}[error] **/
		case SUB_DIV_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {y = 0}[trap] | {x not in range [y, 2y)}[error] **/
		case SUB_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_in_range(loperand, "[y,2y)");
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0} | {y != 0} **/
		case SUB_BAN_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y != 0} **/
		case SUB_BOR_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != y} **/
		case SUB_BXR_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** any **/
		case SUB_LSH_A:
		case SUB_RSH_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);	
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_mul_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 2} | {y != 2} **/
		case MUL_ADD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 2);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 2);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {any} **/
		case MUL_SUB_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);	
		}
		break;
		/** {y = 0}[trap] | {x != 0, y != 1}[error] | {x != 0}[error] **/
		case MUL_DIV_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 1);
			CirSemanticNode infect_node3 = CirSemanticFactory.not_equals(loperand, 0);
			
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, new CirSemanticNode[] {infect_node2, infect_node3}, error_node2);
		}
		break;
		/** {y = 0}[trap] | {x != 0}[error] **/
		case MUL_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand,0);
			
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0, y != 0} **/
		case MUL_BAN_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand,0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand,0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** {x != 0} | {y != 0} **/
		case MUL_BOR_A:
		case MUL_BXR_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand,0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {any} **/
		case MUL_LSH_A:
		case MUL_RSH_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);	
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_div_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {any} **/
		case DIV_ADD_A:
		case DIV_SUB_A:
		case DIV_BOR_A:
		case DIV_BXR_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);	
		}
		break;
		/** {x != 0, y != 1} **/
		case DIV_MUL_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 1);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** {x != 0} **/
		case DIV_MOD_A:
		case DIV_BAN_A:
		case DIV_LSH_A:
		case DIV_RSH_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_arith_mod_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {any} **/
		case MOD_ADD_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);	
		}
		break;
		/** {x != y} **/
		case MOD_SUB_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_in_range(loperand, "[y,2y)");
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} **/
		case MOD_MUL_A:
		case MOD_DIV_A:
		case MOD_BAN_A:
		case MOD_LSH_A:
		case MOD_RSH_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {any} **/
		case MOD_BOR_A:
		case MOD_BXR_A:
		{
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	/**
	 * {
	 * 	COVER(assignment.STATEMENT);
	 * 	......
	 * 	CHG_VAL(assignment.RVALUE);
	 * }
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	private CirSemanticMutation parse_OBXA(TextMutation mutation) throws Exception {
		/** 1. declarations **/
		AstBinaryExpression location = (AstBinaryExpression) this.
				find_ast_location(mutation.get_origin(), AstExpression.class);
		AstCirPair ast_cir_range = this.get_cir_range(location);
		COperator operator = location.get_operator().get_operator();
		
		/** 2. mutation is equivalent because it is not used at all **/
		if(ast_cir_range == null || !ast_cir_range.computational()) { return null; }
		/** 3. otherwise, construct the semantic mutation on result **/
		else {
			/** 3.1. get the result and its statement **/
			CirAssignStatement statement = (CirAssignStatement) this.
					get_cir_location(location, CirAssignStatement.class);
			
			/** 3.2. the mutation is equivalent because it is not reachable **/
			if(statement == null) { return null; }
			/** 3.3. otherwise, construct the semantic mutation for OBxA **/
			else {
				/* a. create the reaching node for constructing the semantic mutation */
				MutationMode mode = mutation.get_mode();
				CirComputeExpression expression = (CirComputeExpression) statement.get_rvalue();
				CirSemanticNode reach_node = CirSemanticFactory.cover_statement(statement);
				
				/* b. parsing the text mutation based operator class */
				switch(operator) {
				case bit_and_assign:	this.parse_bitws_and_assign_to_other(reach_node, expression, mode); break;
				case bit_or_assign:		this.parse_bitws_ior_assign_to_other(reach_node, expression, mode); break;
				case bit_xor_assign:	this.parse_bitws_xor_assign_to_other(reach_node, expression, mode); break;
				case left_shift_assign:	this.parse_bitws_lsh_assign_to_other(reach_node, expression, mode); break;
				case righ_shift_assign:	this.parse_bitws_rsh_assign_to_other(reach_node, expression, mode); break;
				default: throw new IllegalArgumentException("Invalid " + operator);
				}
				
				/* c. return the CIR based mutation */	
				if(reach_node.get_ou_degree() > 0)
					return this.cir_mutation(reach_node);
				else return null;
			}
		}
	}
	private void parse_bitws_and_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0} {y != 0} **/
		case BAN_ADD_A:
		case BAN_SUB_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0, y != 0} **/
		case BAN_MUL_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** {x != 0}[error] | {y = 0}[trap] **/
		case BAN_DIV_A:
		case BAN_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != y} **/
		case BAN_BOR_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} {y != 0} **/
		case BAN_BXR_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0} **/
		case BAN_LSH_A:
		case BAN_RSH_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_ior_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x != 0, y != 0} **/
		case BOR_ADD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** {y != 0} **/
		case BOR_SUB_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} {y != 0} **/
		case BOR_MUL_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y != 0}[error] | {y = 0}[trap] **/
		case BOR_DIV_A:
		case BOR_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != y} **/
		case BOR_BAN_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x & y != 0} **/
		case BOR_BXR_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bit_intersc(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} {y != 0} **/
		case BOR_LSH_A:
		case BOR_RSH_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_xor_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {x & y != 0} **/
		case BXR_ADD_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bit_intersc(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != y} **/
		case BXR_SUB_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} {y != 0} **/
		case BXR_MUL_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {y = 0}[trap] | {y != 0}[error] **/
		case BXR_DIV_A:
		case BXR_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.chg_val(expression);
			CirSemanticNode error_node2 = CirSemanticFactory.traping(statement);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0} {y != 0} **/
		case BXR_BAN_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x & y != 0} **/
		case BXR_BOR_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.bit_intersc(loperand, roperand);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {y != 0} **/
		case BXR_LSH_A:
		case BXR_RSH_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_lsh_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {y != 0} **/
		case LSH_ADD_A:
		case LSH_SUB_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} **/
		case LSH_MUL_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {y = 0}[trap] | {x != 0}[error] **/
		case LSH_DIV_A:
		case LSH_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0} **/
		case LSH_BAN_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} {y != 0} **/
		case LSH_BOR_A:
		case LSH_BXR_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0, y != 0} **/
		case LSH_RSH_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	private void parse_bitws_rsh_assign_to_other(CirSemanticNode reach_node, 
			CirComputeExpression expression, MutationMode mode) throws Exception {
		/** 1. declarations **/
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		CirStatement statement = expression.statement_of();
		
		/** 2. construct the semantic links based on operator **/
		switch(mode) {
		/** {y != 0} **/
		case RSH_ADD_A:
		case RSH_SUB_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(roperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} **/
		case RSH_MUL_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {y = 0}[trap] | {x != 0}[error] **/
		case RSH_DIV_A:
		case RSH_MOD_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node1 = CirSemanticFactory.traping(statement);
			CirSemanticNode error_node2 = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node1);
			this.infer(reach_node, infect_node2, error_node2);
		}
		break;
		/** {x != 0} **/
		case RSH_BAN_A:
		{
			CirSemanticNode infect_node = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node, error_node);
		}
		break;
		/** {x != 0} {y != 0} **/
		case RSH_BOR_A:
		case RSH_BXR_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, infect_node1, error_node);
			this.infer(reach_node, infect_node2, error_node);
		}
		break;
		/** {x != 0, y != 0} **/
		case RSH_LSH_A:
		{
			CirSemanticNode infect_node1 = CirSemanticFactory.equal_with(roperand, 0);
			CirSemanticNode infect_node2 = CirSemanticFactory.not_equals(loperand, 0);
			CirSemanticNode error_node = CirSemanticFactory.chg_val(expression);
			this.infer(reach_node, new CirSemanticNode[] {infect_node1, infect_node2}, error_node);
		}
		break;
		/** invalid mutation operator for X + Y case **/
		default: throw new IllegalArgumentException("Invalid mode: " + mode);
		}
	}
	
}
