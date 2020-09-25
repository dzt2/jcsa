package com.jcsa.jcmutest.backups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * The mutation on program state describes of which statements are first
 * and final point being executed and the constraints w.r.t. the initial
 * state errors provided for executing.
 * 
 * @author yukimula
 *
 */
public class StateMutation {
	
	/* definitions */
	/** the mutant that infect errors w.r.t. constraint **/
	private Mutant mutant;
	/** the first execution being executed for reaching **/
	private CirExecution beg_execution;
	/** the final execution being executed for checking **/
	private CirExecution end_execution;
	/** mapping from the initial error to the constraint **/
	private List<StateErrorPair> pairs;
	
	/* constructor */
	/**
	 * create a state mutation w.r.t. the mutant without infection errors
	 * @param mutant
	 * @throws IllegalArgumentException
	 */
	protected StateMutation(Mutant mutant) throws IllegalArgumentException {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.beg_execution = null;
			this.end_execution = null;
			this.pairs = new ArrayList<StateErrorPair>();
		}
	}
	
	/* getters */
	/**
	 * @return the mutant to which the mutation corresponds
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the mutation that causes the state error in infection
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return whether there is a point for reaching the faulty statement
	 */
	public boolean has_beg_execution() { return this.beg_execution != null; }
	/**
	 * @return whether there is a point for checking the faulty state
	 */
	public boolean has_end_execution() { return this.end_execution != null; }
	/**
	 * @return the first statement for reaching the faulty statement
	 */
	public CirExecution get_beg_execution() { return this.beg_execution; }
	/**
	 * @return the final statement for checking the faulty statement
	 */
	public CirExecution get_end_execution() { return this.end_execution; }
	/**
	 * @return the pairs of constraint-errors for infecting state
	 */
	public Iterable<StateErrorPair> get_pairs() { return this.pairs; }
	/**
	 * @return whether the pairs are of empty
	 */
	public boolean has_pairs() { return !this.pairs.isEmpty(); }
	
	/* setters */
	/**
	 * set the first statement for reaching faulty statement
	 * @param execution
	 */
	protected void set_beg_execution(CirExecution execution) {
		this.beg_execution = execution;
	}
	/**
	 * set the final statement for checking faulty statement
	 * @param execution
	 */
	public void set_end_execution(CirExecution execution) {
		this.end_execution = execution;
	}
	/**
	 * add the constraint-error pair to the mutation
	 * @param constraint
	 * @param state_error
	 * @throws IllegalArgumentException
	 */
	protected void add_state_pair(SecConstraint constraint, 
			SecStateError state_error) throws IllegalArgumentException {
		this.pairs.add(new StateErrorPair(constraint, state_error));
	}
	
	/* parser */
	/** mapping from mutation operators to parsers of state mutation **/
	protected static final Map<MutaClass, StateMutationParser> 
			parsers = new HashMap<MutaClass, StateMutationParser>();
	static {
		parsers.put(MutaClass.BTRP, new BTRPStateMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPStateMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPStateMutationParser());
		parsers.put(MutaClass.STRP, new STRPStateMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPStateMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPStateMutationParser());
		
		parsers.put(MutaClass.SBCR, new SBCRStateMutationParser());
		parsers.put(MutaClass.SWDR, new SWDRStateMutationParser());
		parsers.put(MutaClass.SGLR, new SGLRStateMutationParser());
		parsers.put(MutaClass.STDL, new STDLStateMutationParser());
		
		parsers.put(MutaClass.UIOI, new UIOIStateMutationParser());
		parsers.put(MutaClass.UIOD, new UIODStateMutationParser());
		parsers.put(MutaClass.UIOR, new UIORStateMutationParser());
		parsers.put(MutaClass.VINC, new VINCStateMutationParser());
		parsers.put(MutaClass.UNOI, new UNOIStateMutationParser());
		parsers.put(MutaClass.UNOD, new UNODStateMutationParser());
		
		parsers.put(MutaClass.VBRP, new VBRPStateMutationParser());
		parsers.put(MutaClass.VCRP, new VCRPStateMutationParser());
		parsers.put(MutaClass.VRRP, new VRRPStateMutationParser());
		parsers.put(MutaClass.RTRP, new RTRPStateMutationParser());
		
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
		
		parsers.put(MutaClass.OEAA, new OEXAStateMutationParser());
		parsers.put(MutaClass.OEBA, new OEXAStateMutationParser());
		
		parsers.put(MutaClass.OAAA, new OAXAStateMutationParser());
		parsers.put(MutaClass.OABA, new OAXAStateMutationParser());
		parsers.put(MutaClass.OAEA, new OAXAStateMutationParser());
		
		parsers.put(MutaClass.OBAA, new OBXAStateMutationParser());
		parsers.put(MutaClass.OBBA, new OBXAStateMutationParser());
		parsers.put(MutaClass.OBEA, new OBXAStateMutationParser());
	}
	/**
	 * @param cir_tree
	 * @param mutant
	 * @return parse the mutant into state-mutation
	 * @throws Exception
	 */
	public static StateMutation parse(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			return parsers.get(mutant.get_mutation().get_class()).parse(cir_tree, mutant);
		}
	}
	
}
