package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx.OAXACirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx.OAXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx.OBXACirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx.OBXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx.OEXACirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx.OLXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxx.ORXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refr.RTRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refr.VBRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refr.VCRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refr.VRRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt.SBCRCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt.SGLRCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt.STDLCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt.SWDRCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.trap.BTRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.trap.CTRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.trap.ETRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.trap.STRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.trap.TTRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.trap.VTRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.unry.UIODCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.unry.UIOICirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.unry.UIORCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.unry.UNODCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.unry.UNOICirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.unry.VINCCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraintState;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * It implements the parsing from Mutant to CirMutation(s).
 * 
 * @author yukimula
 *
 */
public final class CirMutationParsers {
	
	/* singleton mode */
	private static final Map<MutaClass, CirMutationParser> parsers = new HashMap<MutaClass, CirMutationParser>();
	static {
		parsers.put(MutaClass.BTRP, new BTRPCirMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPCirMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPCirMutationParser());
		parsers.put(MutaClass.STRP, new STRPCirMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPCirMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPCirMutationParser());
		
		parsers.put(MutaClass.UIOD, new UIODCirMutationParser());
		parsers.put(MutaClass.UIOI, new UIOICirMutationParser());
		parsers.put(MutaClass.UIOR, new UIORCirMutationParser());
		parsers.put(MutaClass.UNOD, new UNODCirMutationParser());
		parsers.put(MutaClass.UNOI, new UNOICirMutationParser());
		parsers.put(MutaClass.VINC, new VINCCirMutationParser());
		
		parsers.put(MutaClass.SBCR, new SBCRCirMutationParser());
		parsers.put(MutaClass.SGLR, new SGLRCirMutationParser());
		parsers.put(MutaClass.STDL, new STDLCirMutationParser());
		parsers.put(MutaClass.SWDR, new SWDRCirMutationParser());
		
		parsers.put(MutaClass.OAAN, new OAXNCirMutationParser());
		parsers.put(MutaClass.OABN, new OAXNCirMutationParser());
		parsers.put(MutaClass.OALN, new OAXNCirMutationParser());
		parsers.put(MutaClass.OARN, new OAXNCirMutationParser());
		
		parsers.put(MutaClass.OBAN, new OBXNCirMutationParser());
		parsers.put(MutaClass.OBBN, new OBXNCirMutationParser());
		parsers.put(MutaClass.OBLN, new OBXNCirMutationParser());
		parsers.put(MutaClass.OBRN, new OBXNCirMutationParser());
		
		parsers.put(MutaClass.OLAN, new OLXNCirMutationParser());
		parsers.put(MutaClass.OLBN, new OLXNCirMutationParser());
		parsers.put(MutaClass.OLLN, new OLXNCirMutationParser());
		parsers.put(MutaClass.OLRN, new OLXNCirMutationParser());
		
		parsers.put(MutaClass.ORAN, new ORXNCirMutationParser());
		parsers.put(MutaClass.ORBN, new ORXNCirMutationParser());
		parsers.put(MutaClass.ORLN, new ORXNCirMutationParser());
		parsers.put(MutaClass.ORRN, new ORXNCirMutationParser());
		
		parsers.put(MutaClass.OAAA, new OAXACirMutationParser());
		parsers.put(MutaClass.OABA, new OAXACirMutationParser());
		parsers.put(MutaClass.OAEA, new OAXACirMutationParser());
		
		parsers.put(MutaClass.OBAA, new OBXACirMutationParser());
		parsers.put(MutaClass.OBBA, new OBXACirMutationParser());
		parsers.put(MutaClass.OBEA, new OBXACirMutationParser());
		
		parsers.put(MutaClass.OEAA, new OEXACirMutationParser());
		parsers.put(MutaClass.OEBA, new OEXACirMutationParser());
		
		parsers.put(MutaClass.RTRP, new RTRPCirMutationParser());
		parsers.put(MutaClass.VBRP, new VBRPCirMutationParser());
		parsers.put(MutaClass.VCRP, new VCRPCirMutationParser());
		parsers.put(MutaClass.VRRP, new VRRPCirMutationParser());
	}
	
	/* parsing method */
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
	/**
	 * @param mutant	the syntactic mutation to be translated into mutations in C-intermediate representative form
	 * @return 			the set of C-intermediate representative mutations of the mutant or empty if it fails to parse
	 * @throws Exception
	 */
	public static Collection<CirMutation> parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(parsers.containsKey(mutant.get_mutation().get_class())) {
			/* 1. parse from syntactic mutation to initial state mutations */
			AstMutation mutation = mutant.get_mutation();
			CirMutationParser parser = parsers.get(mutation.get_class());
			Collection<CirMutation> res = parser.parse(mutant);
			
			/* 2. normalize the output state mutations to normalized forms */
			Collection<CirMutation> outputs = new HashSet<CirMutation>();
			for(CirMutation cir_mutation : res) {
				CirExecution execution = cir_mutation.get_execution();
				CirConditionState constraint = cir_mutation.get_i_state();
				CirAbstErrorState init_error = cir_mutation.get_p_state();
				constraint = (CirConditionState) constraint.normalize();
				init_error = (CirAbstErrorState) init_error.normalize();
				
				if(constraint instanceof CirConstraintState) {
					SymbolExpression condition = ((CirConstraintState) constraint).get_condition();
					for(SymbolExpression sub_condition : derive_conditions(condition)) {
						CirConditionState s_constraint = CirAbstractState.eva_need(execution, sub_condition);
						outputs.add(CirMutations.new_mutation(mutant, execution, s_constraint, init_error));
					}
				}
				else {
					outputs.add(CirMutations.new_mutation(mutant, execution, constraint, init_error));
				}
			}
			
			/* 3. return the final output collections */	return outputs;
		}
		else {
			throw new IllegalArgumentException("Unsupport: " + mutant.get_mutation());
		}
	}
	
}
