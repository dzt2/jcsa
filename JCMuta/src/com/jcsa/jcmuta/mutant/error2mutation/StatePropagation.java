package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmuta.mutant.error2mutation.process.ADDLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.ADDROFProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.ADDROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.ASGRLProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.ArgReturnProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BANLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BANROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BORLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BORROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BXRLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BXRROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.CASTProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.CONDFProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.CONDTProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DEFERProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DEFUSEProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DIVLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DIVROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.EQVLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.EQVROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.FIELDOFProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GRELOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GREROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GRTLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GRTROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.INITBODYProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.LSHLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.LSHROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MODLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MODROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MULLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MULROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NEGProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NEQLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NEQROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NOTProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.RSHLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.RSHROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.RSVProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMELOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMEROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMTLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMTROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SUBLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SUBROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.WAITFORProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirFunctionCallGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirTagStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;
import com.jcsa.jcparse.lopt.models.depend.CDependEdge;
import com.jcsa.jcparse.lopt.models.depend.CDependGraph;
import com.jcsa.jcparse.lopt.models.depend.CDependNode;
import com.jcsa.jcparse.lopt.models.depend.CDependPredicate;
import com.jcsa.jcparse.lopt.models.depend.CDependReference;
import com.jcsa.jcparse.lopt.models.depend.CDependType;

/**
 * Propagation method:<br>
 * 	(1) expression --> expression as parent:<br>
 * 		{defer, address, field, cast, compute, init_body, wait}
 * 	(2) expression as condition --> statement in true branch<br>
 * 	(3) expression as condition --> statement in false branch<br>
 * 	(4) expression as right-val --> left-value in assignment.<br>
 * 	(5) expression as definition --> usage point(s) in other statements.<br>
 * 	(6) expression as argument --> expression as parameter in calling.<br>
 * 	(7) expression as return point --> expression as waiting expression.<br>
 * @author yukimula
 *
 */
public class StatePropagation {
	
	// list of state process instances
	/** parent as CirDeferExpression **/
	private static final StateProcess dereference_process 	= new DEFERProcess();
	/** parent as CirAddressExpression **/
	private static final StateProcess address_of_process 	= new ADDROFProcess();
	/** parent as CirFieldExpression **/
	private static final StateProcess field_of_process 		= new FIELDOFProcess();
	/** parent as CirCastExpression **/
	private static final StateProcess type_cast_process		= new CASTProcess();
	/** parent as CirComputeExpression {+} **/
	private static final StateProcess arith_add_process_l 	= new ADDLOPProcess();
	private static final StateProcess arith_add_process_r 	= new ADDROPProcess();
	/** parent as CirComputeExpression {-} **/
	private static final StateProcess arith_sub_process_l 	= new SUBLOPProcess();
	private static final StateProcess arith_sub_process_r 	= new SUBROPProcess();
	/** parent as CirComputeExpression {*} **/
	private static final StateProcess arith_mul_process_l 	= new MULLOPProcess();
	private static final StateProcess arith_mul_process_r 	= new MULROPProcess();
	/** parent as CirComputeExpression {/} **/
	private static final StateProcess arith_div_process_l 	= new DIVLOPProcess();
	private static final StateProcess arith_div_process_r 	= new DIVROPProcess();
	/** parent as CirComputeExpression {%} **/
	private static final StateProcess arith_mod_process_l 	= new MODLOPProcess();
	private static final StateProcess arith_mod_process_r 	= new MODROPProcess();
	/** parent as CirComputeExpression {&} **/
	private static final StateProcess bitws_and_process_l 	= new BANLOPProcess();
	private static final StateProcess bitws_and_process_r 	= new BANROPProcess();
	/** parent as CirComputeExpression {|} **/
	private static final StateProcess bitws_ior_process_l 	= new BORLOPProcess();
	private static final StateProcess bitws_ior_process_r 	= new BORROPProcess();
	/** parent as CirComputeExpression {^} **/
	private static final StateProcess bitws_xor_process_l 	= new BXRLOPProcess();
	private static final StateProcess bitws_xor_process_r 	= new BXRROPProcess();
	/** parent as CirComputeExpression {<<} **/
	private static final StateProcess bitws_lsh_process_l 	= new LSHLOPProcess();
	private static final StateProcess bitws_lsh_process_r 	= new LSHROPProcess();
	/** parent as CirComputeExpression {>>} **/
	private static final StateProcess bitws_rsh_process_l 	= new RSHLOPProcess();
	private static final StateProcess bitws_rsh_process_r 	= new RSHROPProcess();
	/** parent as CirComputeExpression {>} **/
	private static final StateProcess greater_tn_process_l 	= new GRTLOPProcess();
	private static final StateProcess greater_tn_process_r 	= new GRTROPProcess();
	/** parent as CirComputeExpression {>=} **/
	private static final StateProcess greater_eq_process_l 	= new GRELOPProcess();
	private static final StateProcess greater_eq_process_r 	= new GREROPProcess();
	/** parent as CirComputeExpression {<} **/
	private static final StateProcess smaller_tn_process_l 	= new SMTLOPProcess();
	private static final StateProcess smaller_tn_process_r 	= new SMTROPProcess();
	/** parent as CirComputeExpression {<=} **/
	private static final StateProcess smaller_eq_process_l 	= new SMELOPProcess();
	private static final StateProcess smaller_eq_process_r 	= new SMEROPProcess();
	/** parent as CirComputeExpression {==} **/
	private static final StateProcess equal_with_process_l 	= new EQVLOPProcess();
	private static final StateProcess equal_with_process_r 	= new EQVROPProcess();
	/** parent as CirComputeExpression {!=} **/
	private static final StateProcess not_equals_process_l 	= new NEQLOPProcess();
	private static final StateProcess not_equals_process_r 	= new NEQROPProcess();
	/** parent as CirComputeExpression {negative} **/
	private static final StateProcess arith_neg_process 	= new NEGProcess();
	/** parent as CirComputeExpression {!} **/
	private static final StateProcess logic_not_process 	= new NOTProcess();
	/** parent as CirComputeExpression {~} **/
	private static final StateProcess bitws_rsv_process 	= new RSVProcess();
	/** parent as CirWaitExpression **/
	private static final StateProcess wait_expr_process 	= new WAITFORProcess();
	/** parent as CirInitializerBody **/
	private static final StateProcess initial_body_process 	= new INITBODYProcess();
	/** parent as CirIfStatement or CirCaseStatement **/
	private static final StateProcess condition_true_process = new CONDTProcess();
	private static final StateProcess condition_false_process = new CONDFProcess();
	/** parent as CirAssignStatement **/
	private static final StateProcess rvalue_lvalue_process = new ASGRLProcess();
	/** def-use, arg-param, return-wait **/
	private static final StateProcess define_usage_process 	= new DEFUSEProcess();
	/** argument --> return value **/
	private static final StateProcess call_return_process = new ArgReturnProcess();
	
	/**
	 * set whether the constraints need to be optimized
	 * @param optimize
	 */
	protected static void set_optimization(boolean optimize) {
		if(optimize) {
			StatePropagation.address_of_process.open_optimize_constraint();
			StatePropagation.arith_add_process_l.open_optimize_constraint();
			StatePropagation.arith_add_process_r.open_optimize_constraint();
			StatePropagation.arith_sub_process_l.open_optimize_constraint();
			StatePropagation.arith_sub_process_r.open_optimize_constraint();
			StatePropagation.arith_mul_process_l.open_optimize_constraint();
			StatePropagation.arith_mul_process_r.open_optimize_constraint();
			StatePropagation.arith_div_process_l.open_optimize_constraint();
			StatePropagation.arith_div_process_r.open_optimize_constraint();
			StatePropagation.arith_mod_process_l.open_optimize_constraint();
			StatePropagation.arith_mod_process_r.open_optimize_constraint();
			StatePropagation.bitws_and_process_l.open_optimize_constraint();
			StatePropagation.bitws_and_process_r.open_optimize_constraint();
			StatePropagation.bitws_ior_process_l.open_optimize_constraint();
			StatePropagation.bitws_ior_process_r.open_optimize_constraint();
			StatePropagation.bitws_xor_process_l.open_optimize_constraint();
			StatePropagation.bitws_xor_process_r.open_optimize_constraint();
			StatePropagation.bitws_lsh_process_l.open_optimize_constraint();
			StatePropagation.bitws_lsh_process_r.open_optimize_constraint();
			StatePropagation.bitws_ior_process_l.open_optimize_constraint();
			StatePropagation.bitws_ior_process_r.open_optimize_constraint();
			StatePropagation.arith_neg_process.open_optimize_constraint();
			StatePropagation.bitws_rsv_process.open_optimize_constraint();
			StatePropagation.logic_not_process.open_optimize_constraint();
			StatePropagation.address_of_process.open_optimize_constraint();
			StatePropagation.dereference_process.open_optimize_constraint();
			StatePropagation.field_of_process.open_optimize_constraint();
			StatePropagation.initial_body_process.open_optimize_constraint();
			StatePropagation.wait_expr_process.open_optimize_constraint();
			StatePropagation.type_cast_process.open_optimize_constraint();
			StatePropagation.condition_false_process.open_optimize_constraint();
			StatePropagation.condition_true_process.open_optimize_constraint();
			StatePropagation.rvalue_lvalue_process.open_optimize_constraint();
			StatePropagation.define_usage_process.open_optimize_constraint();
			StatePropagation.call_return_process.open_optimize_constraint();
		}
		else {
			StatePropagation.address_of_process.close_optimize_constraint();;
			StatePropagation.arith_add_process_l.close_optimize_constraint();
			StatePropagation.arith_add_process_r.close_optimize_constraint();
			StatePropagation.arith_sub_process_l.close_optimize_constraint();
			StatePropagation.arith_sub_process_r.close_optimize_constraint();
			StatePropagation.arith_mul_process_l.close_optimize_constraint();
			StatePropagation.arith_mul_process_r.close_optimize_constraint();
			StatePropagation.arith_div_process_l.close_optimize_constraint();
			StatePropagation.arith_div_process_r.close_optimize_constraint();
			StatePropagation.arith_mod_process_l.close_optimize_constraint();
			StatePropagation.arith_mod_process_r.close_optimize_constraint();
			StatePropagation.bitws_and_process_l.close_optimize_constraint();
			StatePropagation.bitws_and_process_r.close_optimize_constraint();
			StatePropagation.bitws_ior_process_l.close_optimize_constraint();
			StatePropagation.bitws_ior_process_r.close_optimize_constraint();
			StatePropagation.bitws_xor_process_l.close_optimize_constraint();
			StatePropagation.bitws_xor_process_r.close_optimize_constraint();
			StatePropagation.bitws_lsh_process_l.close_optimize_constraint();
			StatePropagation.bitws_lsh_process_r.close_optimize_constraint();
			StatePropagation.bitws_ior_process_l.close_optimize_constraint();
			StatePropagation.bitws_ior_process_r.close_optimize_constraint();
			StatePropagation.arith_neg_process.close_optimize_constraint();
			StatePropagation.bitws_rsv_process.close_optimize_constraint();
			StatePropagation.logic_not_process.close_optimize_constraint();
			StatePropagation.address_of_process.close_optimize_constraint();
			StatePropagation.dereference_process.close_optimize_constraint();
			StatePropagation.field_of_process.close_optimize_constraint();
			StatePropagation.initial_body_process.close_optimize_constraint();
			StatePropagation.wait_expr_process.close_optimize_constraint();
			StatePropagation.type_cast_process.close_optimize_constraint();
			StatePropagation.condition_false_process.close_optimize_constraint();
			StatePropagation.condition_true_process.close_optimize_constraint();
			StatePropagation.rvalue_lvalue_process.close_optimize_constraint();
			StatePropagation.define_usage_process.close_optimize_constraint();
			StatePropagation.call_return_process.close_optimize_constraint();
		}
	}
	
	/**
	 * generate the next group of state error from the source in the graph
	 * with the assistance of program dependence relationships.
	 * @param source
	 * @param dgraph
	 * @return
	 * @throws Exception
	 */
	protected static char propgate(StateErrorNode source, CDependGraph dgraph, Collection<StateErrorNode> next_nodes) throws Exception {
		if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(dgraph == null)
			throw new IllegalArgumentException("Invalid dependence graph");
		else if(next_nodes == null)
			throw new IllegalArgumentException("Invalid next_nodes: null");
		else {
			CirNode location = source.get_location();
			Collection<StateErrorEdge> edges = null;
			next_nodes.clear(); char end_tag;
			
			if(location != null) {
				CirNode parent = location.get_parent();
				if(location instanceof CirStatement) { end_tag = 'E'; }
				else if(parent instanceof CirExpression) {
					if(parent instanceof CirDeferExpression) {
						edges = StatePropagation.dereference_process.process(source, parent, true);
					}
					else if(parent instanceof CirFieldExpression) {
						edges = StatePropagation.field_of_process.process(source, parent, true);
					}
					else if(parent instanceof CirAddressExpression) {
						edges = StatePropagation.address_of_process.process(source, parent, true);
					}
					else if(parent instanceof CirCastExpression) {
						edges = StatePropagation.type_cast_process.process(source, parent, true);
					}
					else if(parent instanceof CirInitializerBody) {
						edges = StatePropagation.initial_body_process.process(source, parent, true);
					}
					else if(parent instanceof CirWaitExpression) {
						edges = StatePropagation.wait_expr_process.process(source, parent, true);
					}
					else if(parent instanceof CirComputeExpression) {
						switch(((CirComputeExpression) parent).get_operator()) {
						case negative:		edges = StatePropagation.arith_neg_process.process(source, parent, true); break;
						case bit_not:		edges = StatePropagation.bitws_rsv_process.process(source, parent, true); break;
						case logic_not:		edges = StatePropagation.logic_not_process.process(source, parent, true); break;
						case arith_add:		
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.arith_add_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.arith_add_process_r.process(source, parent, true);
							}
							break;
						case arith_sub:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.arith_sub_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.arith_sub_process_r.process(source, parent, true);
							}
							break;
						case arith_mul:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.arith_mul_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.arith_mul_process_r.process(source, parent, true);
							}
							break;
						case arith_div:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.arith_div_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.arith_div_process_r.process(source, parent, true);
							}
							break;
						case arith_mod:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.arith_mod_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.arith_mod_process_r.process(source, parent, true);
							}
							break;
						case bit_and:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.bitws_and_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.bitws_and_process_r.process(source, parent, true);
							}
							break;
						case bit_or:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.bitws_ior_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.bitws_ior_process_r.process(source, parent, true);
							}
							break;
						case bit_xor:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.bitws_xor_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.bitws_xor_process_r.process(source, parent, true);
							}
							break;
						case left_shift:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.bitws_lsh_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.bitws_lsh_process_r.process(source, parent, true);
							}
							break;
						case righ_shift:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.bitws_rsh_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.bitws_rsh_process_r.process(source, parent, true);
							}
							break;
						case greater_tn:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.greater_tn_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.greater_tn_process_r.process(source, parent, true);
							}
							break;
						case greater_eq:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.greater_eq_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.greater_eq_process_r.process(source, parent, true);
							}
							break;
						case smaller_tn:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.smaller_tn_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.smaller_tn_process_r.process(source, parent, true);
							}
							break;
						case smaller_eq:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.smaller_eq_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.smaller_eq_process_r.process(source, parent, true);
							}
							break;
						case equal_with:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.equal_with_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.equal_with_process_r.process(source, parent, true);
							}
							break;
						case not_equals:
							if(((CirComputeExpression) parent).get_operand(0) == location) {
								edges = StatePropagation.not_equals_process_l.process(source, parent, true);
							}
							else {
								edges = StatePropagation.not_equals_process_r.process(source, parent, true);
							}
							break;
						default: break;
						}
					}
					end_tag = 'I';
				}
				else if(parent instanceof CirAssignStatement) {
					if(((CirAssignStatement) parent).get_rvalue() == location) {
						edges = StatePropagation.rvalue_lvalue_process.process(
								source, ((CirAssignStatement) parent).get_lvalue(), true);
						end_tag = 'I';
					}
					else {
						Collection<CDependNode> dnodes = StatePropagation.get_depend_nodes(dgraph, parent);
						Collection<CirExpression> use_points = StatePropagation.get_usage_points(dnodes);
						
						edges = new ArrayList<StateErrorEdge>();
						for(CirExpression use_point : use_points) {
							edges.addAll(StatePropagation.define_usage_process.process(source, use_point, true));
						}
						
						end_tag = 'C';
					}
				}
				else if(parent instanceof CirIfStatement || parent instanceof CirCaseStatement) {
					Collection<CDependNode> dnodes = StatePropagation.get_depend_nodes(dgraph, parent);
					Collection<CirStatement> t_branch = StatePropagation.get_branch_statements(dnodes, true);
					Collection<CirStatement> f_branch = StatePropagation.get_branch_statements(dnodes, false);
					
					for(CirStatement statement : t_branch) {
						edges = StatePropagation.condition_true_process.process(source, statement, true);
					}
					for(CirStatement statement : f_branch) {
						edges.addAll(StatePropagation.condition_false_process.process(source, statement, true));
					}
					
					end_tag = 'C';
				}
				else if(parent instanceof CirArgumentList) {
					CirCallStatement call_stmt = (CirCallStatement) parent.get_parent();
					CirFunctionCallGraph graph = call_stmt.get_tree().get_function_call_graph();
					CirExecution call_execution = graph.
							get_function(call_stmt).get_flow_graph().get_execution(call_stmt);
					CirExecution wait_execution = graph.get_function(call_stmt).
							get_flow_graph().get_execution(call_execution.get_id() + 1);
					CirWaitAssignStatement wait_statement = 
								(CirWaitAssignStatement) wait_execution.get_statement();
					edges = StatePropagation.call_return_process.process(source, wait_statement.get_lvalue(), true);
					end_tag = 'C';
				}
				else {
					end_tag = 'E';
				}
			}
			else {
				end_tag = 'E';
			}
			
			if(edges != null) {
				for(StateErrorEdge edge : edges) { next_nodes.add(edge.get_target()); }
			}
			
			return end_tag;
		}
	}
	
	private static Collection<CDependNode> get_depend_nodes(CDependGraph dgraph, CirNode source) throws Exception {
		List<CDependNode> nodes = new ArrayList<CDependNode>();
		for(CDependNode node : dgraph.get_nodes()) {
			if(node.get_instance().has_cir_node(source)) nodes.add(node);
		}
		return nodes;
	}
	
	private static Collection<CirStatement> get_branch_statements(
			Collection<CDependNode> dnodes, boolean predicate) throws Exception {
		Set<CirStatement> statements = new HashSet<CirStatement>();
		
		for(CDependNode dnode : dnodes) {
			for(CDependEdge dedge : dnode.get_in_edges()) {
				if(dedge.get_type() == CDependType.predicate_depend) {
					CDependPredicate element = (CDependPredicate) dedge.get_element();
					if(element.get_predicate_value() == predicate) {
						CirStatement statement = dedge.get_source().get_statement();
						if(!(statement instanceof CirTagStatement)) {
							statements.add(statement);
						}
					}
				}
			}
		}
		
		return statements;
	}
	
	private static Collection<CirExpression> get_usage_points(Collection<CDependNode> dnodes) throws Exception {
		Set<CirExpression> expressions = new HashSet<CirExpression>();
		
		for(CDependNode node : dnodes) {
			for(CDependEdge edge : node.get_in_edges()) {
				switch(edge.get_type()) {
				case use_defin_depend:
				case wait_retr_depend:
				{
					CDependReference reference = (CDependReference) edge.get_element();
					expressions.add(reference.get_use());
				}
				break;
				default: break;
				}
			}
		}
		
		return expressions;
	}
	
	/**
	 * generate the propagation flows from the root with maximal distance as given
	 * @param root
	 * @param dgraph
	 * @param distance
	 * @throws Exception
	 */
	protected static void propagate_for(StateErrorNode root, CDependGraph dgraph, int distance) throws Exception {
		if(distance > 0) {
			List<StateErrorNode> children = new ArrayList<StateErrorNode>();
			char end_tag = StatePropagation.propgate(root, dgraph, children);
			
			switch(end_tag) {
			case 'C': distance = distance - 1; break;
			case 'I': break;
			case 'E': return;
			default: throw new IllegalArgumentException("Unknown end tag: " + end_tag);
			}
			
			for(StateErrorNode child : children) propagate_for(child, dgraph, distance);
		}
	}
	
}
