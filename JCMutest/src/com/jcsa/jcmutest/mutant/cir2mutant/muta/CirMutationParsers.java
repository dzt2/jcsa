package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirCoverCount;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo.OAXACirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo.OAXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo.OBXACirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo.OBXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo.OEXACirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo.OLXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.oxxo.ORXNCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refs.RTRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refs.VBRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refs.VCRPCirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.refs.VRRPCirMutationParser;
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
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolBinaryExpression;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirMutationParsers {

private static final Map<MutaClass, CirMutationParser> parsers = new HashMap<>();

	static {
		parsers.put(MutaClass.BTRP, new BTRPCirMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPCirMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPCirMutationParser());
		parsers.put(MutaClass.STRP, new STRPCirMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPCirMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPCirMutationParser());

		parsers.put(MutaClass.SBCR, new SBCRCirMutationParser());
		parsers.put(MutaClass.SWDR, new SWDRCirMutationParser());
		parsers.put(MutaClass.SGLR, new SGLRCirMutationParser());
		parsers.put(MutaClass.STDL, new STDLCirMutationParser());

		parsers.put(MutaClass.UIOD, new UIODCirMutationParser());
		parsers.put(MutaClass.UIOI, new UIOICirMutationParser());
		parsers.put(MutaClass.UIOR, new UIORCirMutationParser());
		parsers.put(MutaClass.VINC, new VINCCirMutationParser());
		parsers.put(MutaClass.UNOI, new UNOICirMutationParser());
		parsers.put(MutaClass.UNOD, new UNODCirMutationParser());

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

		parsers.put(MutaClass.OEAA, new OEXACirMutationParser());
		parsers.put(MutaClass.OEBA, new OEXACirMutationParser());

		parsers.put(MutaClass.OAAA, new OAXACirMutationParser());
		parsers.put(MutaClass.OABA, new OAXACirMutationParser());
		parsers.put(MutaClass.OAEA, new OAXACirMutationParser());

		parsers.put(MutaClass.OBAA, new OBXACirMutationParser());
		parsers.put(MutaClass.OBBA, new OBXACirMutationParser());
		parsers.put(MutaClass.OBEA, new OBXACirMutationParser());

		parsers.put(MutaClass.VBRP, new VBRPCirMutationParser());
		parsers.put(MutaClass.VCRP, new VCRPCirMutationParser());
		parsers.put(MutaClass.VRRP, new VRRPCirMutationParser());
		parsers.put(MutaClass.RTRP, new RTRPCirMutationParser());
	}

	private static Iterable<CirMutation> parse_from(CirTree cir_tree, AstMutation mutation) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else
			return parsers.get(mutation.get_class()).parse(cir_tree, mutation);
	}
	
	private static void divide_conditions_in(SymbolExpression condition, Collection<SymbolExpression> conditions) throws Exception {
		if(condition instanceof SymbolConstant) {
			if(((SymbolConstant) condition).get_bool()) {
				conditions.add(SymbolFactory.sym_constant(Boolean.TRUE));
			}
			else {
				/* ignore false operand in disjunctives */
			}
		}
		else if(condition instanceof SymbolBinaryExpression) {
			COperator op = ((SymbolBinaryExpression) condition).get_operator().get_operator();
			SymbolExpression loperand = ((SymbolBinaryExpression) condition).get_loperand();
			SymbolExpression roperand = ((SymbolBinaryExpression) condition).get_roperand();
			if(op == COperator.logic_or) {
				divide_conditions_in(loperand, conditions);
				divide_conditions_in(roperand, conditions);
			}
			else {
				conditions.add(SymbolFactory.sym_condition(condition, true));
			}
		}
		else {
			conditions.add(SymbolFactory.sym_condition(condition, true));
		}
	}
	
	private static Iterable<CirAttribute> divide_constraints(CirAttribute constraint) throws Exception {
		Set<CirAttribute> constraints = new HashSet<CirAttribute>();
		if(constraint instanceof CirCoverCount) {
			constraints.add(constraint);
		}
		else if(constraint instanceof CirConstraint) {
			CirExecution execution = constraint.get_execution();
			SymbolExpression condition = constraint.get_parameter();
			Set<SymbolExpression> conditions = new HashSet<SymbolExpression>();
			divide_conditions_in(condition.evaluate(null), conditions);
			
			if(!conditions.isEmpty()) {
				for(SymbolExpression sub_condition : conditions) {
					sub_condition = sub_condition.evaluate(null);
					constraints.add(CirAttribute.new_constraint(execution, sub_condition, true));
				}
			}
			else {
				constraints.add(CirAttribute.new_constraint(execution, Boolean.FALSE, true));
			}
		}
		else {
			throw new IllegalArgumentException(constraint.toString());
		}
		return constraints;
	}
	
	private static Iterable<CirMutation> normalize(Iterable<CirMutation> mutations) throws Exception {
		Set<CirMutation> normal_mutations = new HashSet<CirMutation>();
		if(mutations != null) {
			for(CirMutation mutation : mutations) {
				CirAttribute constraint = mutation.get_constraint().optimize();
				CirAttribute init_error = mutation.get_init_error().optimize();
				Iterable<CirAttribute> sub_constraints = divide_constraints(constraint);
				for(CirAttribute sub_constraint : sub_constraints) {
					normal_mutations.add(CirMutations.new_mutation(sub_constraint, init_error));
				}
			}
		}
		return normal_mutations;
	}
	
	/**
	 * @param mutant
	 * @return 	it parses the syntactic mutation to mutation(s) in C-intermediate 
	 * 			representative code (CirMutation).
	 * @throws Exception
	 */
	public static Iterable<CirMutation> parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			/* 1. syntax-directed translation */
			Iterable<CirMutation> init_solutions;
			try {
				init_solutions = parse_from(mutant.get_space().get_cir_tree(), mutant.get_mutation());
			}
			/* 2. protection returning output */
			catch(Exception ex) {
				return new ArrayList<CirMutation>();	
			}
			/* 3. normalization procedure */
			return normalize(init_solutions);
		}
	}
	
}
