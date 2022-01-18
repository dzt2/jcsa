package com.jcsa.jcmutest.mutant.sta2mutant.muta;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirMConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirNConstrainState;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OAXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OAXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OBXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OBXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OEXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OLXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.ORXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.RTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.VBRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.VCRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.VRRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.SBCRStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.SGLRStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.STDLStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.SWDRStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.BTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.CTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.ETRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.STRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.TTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.VTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UIODStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UIOIStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UIORStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UNODStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UNOIStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.VINCStateMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class StateMutationParsers {
	
	private static final Map<MutaClass, StateMutationParser> parsers = new HashMap<MutaClass, StateMutationParser>();
	
	static {
		parsers.put(MutaClass.BTRP, new BTRPStateMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPStateMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPStateMutationParser());
		parsers.put(MutaClass.STRP, new STRPStateMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPStateMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPStateMutationParser());
		
		parsers.put(MutaClass.UIOD, new UIODStateMutationParser());
		parsers.put(MutaClass.UIOI, new UIOIStateMutationParser());
		parsers.put(MutaClass.UIOR, new UIORStateMutationParser());
		parsers.put(MutaClass.UNOD, new UNODStateMutationParser());
		parsers.put(MutaClass.UNOI, new UNOIStateMutationParser());
		parsers.put(MutaClass.VINC, new VINCStateMutationParser());
		
		parsers.put(MutaClass.SBCR, new SBCRStateMutationParser());
		parsers.put(MutaClass.SGLR, new SGLRStateMutationParser());
		parsers.put(MutaClass.STDL, new STDLStateMutationParser());
		parsers.put(MutaClass.SWDR, new SWDRStateMutationParser());
		
		parsers.put(MutaClass.OAAN, new OAXNStateMutationParser());
		parsers.put(MutaClass.OABN, new OAXNStateMutationParser());
		parsers.put(MutaClass.OALN, new OAXNStateMutationParser());
		parsers.put(MutaClass.OARN, new OAXNStateMutationParser());
		
		parsers.put(MutaClass.OBAN, new OBXNStateMutationParser());
		parsers.put(MutaClass.OBBN, new OBXNStateMutationParser());
		parsers.put(MutaClass.OBLN, new OBXNStateMutationParser());
		parsers.put(MutaClass.OBRN, new OBXNStateMutationParser());
		
		parsers.put(MutaClass.OLAN, new OLXNStateMutationParser());
		parsers.put(MutaClass.OLBN, new OLXNStateMutationParser());
		parsers.put(MutaClass.OLLN, new OLXNStateMutationParser());
		parsers.put(MutaClass.OLRN, new OLXNStateMutationParser());
		
		parsers.put(MutaClass.ORAN, new ORXNStateMutationParser());
		parsers.put(MutaClass.ORBN, new ORXNStateMutationParser());
		parsers.put(MutaClass.ORLN, new ORXNStateMutationParser());
		parsers.put(MutaClass.ORRN, new ORXNStateMutationParser());
		
		parsers.put(MutaClass.OAAA, new OAXAStateMutationParser());
		parsers.put(MutaClass.OABA, new OAXAStateMutationParser());
		parsers.put(MutaClass.OAEA, new OAXAStateMutationParser());
		
		parsers.put(MutaClass.OBAA, new OBXAStateMutationParser());
		parsers.put(MutaClass.OBBA, new OBXAStateMutationParser());
		parsers.put(MutaClass.OBEA, new OBXAStateMutationParser());
		
		parsers.put(MutaClass.OEAA, new OEXAStateMutationParser());
		parsers.put(MutaClass.OEBA, new OEXAStateMutationParser());
		
		parsers.put(MutaClass.RTRP, new RTRPStateMutationParser());
		parsers.put(MutaClass.VBRP, new VBRPStateMutationParser());
		parsers.put(MutaClass.VCRP, new VCRPStateMutationParser());
		parsers.put(MutaClass.VRRP, new VRRPStateMutationParser());
	}
	
	/**
	 * It parses the 
	 * @param cir_tree
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public static Collection<StateMutation> parse(CirTree cir_tree, AstMutation mutation) throws Exception {
		if(cir_tree == null) {
			throw new IllegalArgumentException("Invalid cir_tree: null");
		}
		else if(mutation == null) {
			throw new IllegalArgumentException("Invalid mutation: null");
		}
		else if(parsers.containsKey(mutation.get_class())) {
			/* 1. parse from syntactic mutation to initial state mutations */
			StateMutationParser parser = parsers.get(mutation.get_class());
			Collection<StateMutation> res = parser.parse(cir_tree, mutation);
			
			/* 2. normalize the output state mutations to normalized forms */
			Collection<StateMutation> outputs = new HashSet<StateMutation>();
			for(StateMutation state_mutation : res) {
				CirExecution execution = state_mutation.get_r_execution();
				CirConditionState constraint = state_mutation.get_istate();
				CirAbstErrorState init_error = state_mutation.get_pstate();
				/* TODO may do normalization to init_error and constraint */
				
				if(constraint instanceof CirNConstrainState) {
					SymbolExpression condition = ((CirNConstrainState) constraint).get_condition();
					for(SymbolExpression sub_condition : derive_conditions(condition)) {
						CirConditionState s_constraint = CirAbstractState.eva_cond(execution, sub_condition, true);
						outputs.add(StateMutations.new_mutation(execution, s_constraint, init_error));
					}
				}
				else if(constraint instanceof CirMConstrainState) {
					SymbolExpression condition = ((CirNConstrainState) constraint).get_condition();
					for(SymbolExpression sub_condition : derive_conditions(condition)) {
						CirConditionState s_constraint = CirAbstractState.mus_cond(execution, sub_condition, true);
						outputs.add(StateMutations.new_mutation(execution, s_constraint, init_error));
					}
				}
				else {
					/* proceed on no division case */
					outputs.add(StateMutations.new_mutation(execution, constraint, init_error));
				}
			}
			
			/* 3. return the final output collections */	return outputs;
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + mutation);
		}
	}
	
	/**
	 * @param condition
	 * @return it divides the symbolic condition to a set of sub-conditions in its disjunctive logical form
	 * @throws Exception
	 */
	private static Collection<SymbolExpression> derive_conditions(SymbolExpression condition) throws Exception {
		Collection<SymbolExpression> conditions = new HashSet<SymbolExpression>();
		derive_conditions(condition, conditions);
		if(conditions.isEmpty()) {
			conditions.add(SymbolFactory.sym_constant(Boolean.FALSE));
		}
		return conditions;
	}
	
	/**
	 * it (recursively) divides the symbolic condition to a set of sub-conditions in its disjunctive logical form
	 * @param condition
	 * @param conditions
	 * @throws Exception
	 */
	private static void derive_conditions(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				conditions.clear();
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
			else {
				/* ignore the False operand in disjunctive expressions */
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator operator = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(operator == COperator.logic_or) {
				derive_conditions(loperand, conditions);
				derive_conditions(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(condition, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
	}
	
}
