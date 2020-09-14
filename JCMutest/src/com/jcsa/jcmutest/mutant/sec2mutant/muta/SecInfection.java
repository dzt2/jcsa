package com.jcsa.jcmutest.mutant.sec2mutant.muta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecStateError;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.cons.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt.OAXAInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt.OAXNInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt.OBXAInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt.OBXNInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt.OEXAInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt.OLXNInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.oprt.ORXNInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.refs.RTRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.refs.VBRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.refs.VCRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.refs.VRRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt.SBCRInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt.SGLRInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt.STDLInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.stmt.SWDRInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.trap.BTRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.trap.CTRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.trap.ETRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.trap.STRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.trap.TTRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.trap.VTRPInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.unry.UIODInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.unry.UIOIInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.unry.UIORInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.unry.UNODInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.unry.UNOIInfectionParser;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.unry.VINCInfectionParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SecInfection {
	
	/* attributes */
	/** the mutant on which the infection is needed **/
	private Mutant mutant;
	/** the statement that is executed iff. the mutant is reached **/
	private CirStatement statement;
	/** the set of infection pairs in the module **/
	private List<SecInfectPair> pairs;
	
	/* constructor */
	/**
	 * create an empty infection instance for the mutation
	 * @param mutant
	 * @throws Exception
	 */
	protected SecInfection(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant: null");
		else {
			this.mutant = mutant;
			this.statement = null;
			this.pairs = new LinkedList<SecInfectPair>();
		}
	}
	
	/* mutation-getters */
	/**
	 * @return the mutation on which the infection is required for killing it
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the mutation on which the infection is required for killing it
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	
	/* statement-getters */
	/**
	 * @return whether the statement where the fault is seeded exists
	 */
	public boolean has_statement() { return this.statement != null; }
	/**
	 * @return the statement where the fault is injected
	 */
	public CirStatement get_statement() { return this.statement; }
	
	/* infection-getters */
	/**
	 * @return whether the number of infection pairs are non-zeros
	 */
	public boolean has_infection_pairs() { return !this.pairs.isEmpty(); }
	/**
	 * @return the number of the infection pairs in the module
	 */
	public int number_of_infection_pairs() { return this.pairs.size(); }
	/**
	 * @param k
	 * @return the kth infection pair as {constraint, state_error}
	 * @throws IndexOutOfBoundsException
	 */
	public SecInfectPair get_infection_pair(int k) throws IndexOutOfBoundsException {
		return this.pairs.get(k);
	}
	/**
	 * @return the set of infection pairs in the module
	 */
	public Iterable<SecInfectPair> get_infection_pairs() {
		return this.pairs;
	}
	
	/* setters */
	/**
	 * set the statement where the fault is seeded
	 * @param statement
	 * @throws Exception
	 */
	protected void set_statement(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement");
		else
			this.statement = statement;
	}
	/**
	 * add a infection-pair [constraint, init_error] in the module
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected void add_infection_pair(SecConstraint constraint,
			SecStateError init_error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint.");
		else if(init_error == null)
			throw new IllegalArgumentException("Invalid init_error.");
		else {
			this.pairs.add(new SecInfectPair(constraint, init_error));
		}
	}
	
	/* parsing module */
	private static final Map<MutaClass, SecInfectionParser> 
		parsers = new HashMap<MutaClass, SecInfectionParser>();
	static {
		parsers.put(MutaClass.BTRP, new BTRPInfectionParser());
		parsers.put(MutaClass.CTRP, new CTRPInfectionParser());
		parsers.put(MutaClass.ETRP, new ETRPInfectionParser());
		parsers.put(MutaClass.STRP, new STRPInfectionParser());
		parsers.put(MutaClass.TTRP, new TTRPInfectionParser());
		parsers.put(MutaClass.VTRP, new VTRPInfectionParser());
		
		parsers.put(MutaClass.SBCR, new SBCRInfectionParser());
		parsers.put(MutaClass.SWDR, new SWDRInfectionParser());
		parsers.put(MutaClass.SGLR, new SGLRInfectionParser());
		parsers.put(MutaClass.STDL, new STDLInfectionParser());
		
		parsers.put(MutaClass.UIOI, new UIOIInfectionParser());
		parsers.put(MutaClass.UIOD, new UIODInfectionParser());
		parsers.put(MutaClass.UIOR, new UIORInfectionParser());
		parsers.put(MutaClass.VINC, new VINCInfectionParser());
		parsers.put(MutaClass.UNOI, new UNOIInfectionParser());
		parsers.put(MutaClass.UNOD, new UNODInfectionParser());
		
		parsers.put(MutaClass.VBRP, new VBRPInfectionParser());
		parsers.put(MutaClass.VCRP, new VCRPInfectionParser());
		parsers.put(MutaClass.VRRP, new VRRPInfectionParser());
		parsers.put(MutaClass.RTRP, new RTRPInfectionParser());
		
		parsers.put(MutaClass.OAAN, new OAXNInfectionParser());
		parsers.put(MutaClass.OABN, new OAXNInfectionParser());
		parsers.put(MutaClass.OALN, new OAXNInfectionParser());
		parsers.put(MutaClass.OARN, new OAXNInfectionParser());
		
		parsers.put(MutaClass.OBAN, new OBXNInfectionParser());
		parsers.put(MutaClass.OBBN, new OBXNInfectionParser());
		parsers.put(MutaClass.OBLN, new OBXNInfectionParser());
		parsers.put(MutaClass.OBRN, new OBXNInfectionParser());
		
		parsers.put(MutaClass.OLAN, new OLXNInfectionParser());
		parsers.put(MutaClass.OLBN, new OLXNInfectionParser());
		parsers.put(MutaClass.OLLN, new OLXNInfectionParser());
		parsers.put(MutaClass.OLRN, new OLXNInfectionParser());
		
		parsers.put(MutaClass.ORAN, new ORXNInfectionParser());
		parsers.put(MutaClass.ORBN, new ORXNInfectionParser());
		parsers.put(MutaClass.ORLN, new ORXNInfectionParser());
		parsers.put(MutaClass.ORRN, new ORXNInfectionParser());
		
		parsers.put(MutaClass.OEAA, new OEXAInfectionParser());
		parsers.put(MutaClass.OEBA, new OEXAInfectionParser());
		
		parsers.put(MutaClass.OAAA, new OAXAInfectionParser());
		parsers.put(MutaClass.OABA, new OAXAInfectionParser());
		parsers.put(MutaClass.OAEA, new OAXAInfectionParser());
		
		parsers.put(MutaClass.OBAA, new OBXAInfectionParser());
		parsers.put(MutaClass.OBBA, new OBXAInfectionParser());
		parsers.put(MutaClass.OBEA, new OBXAInfectionParser());
	}
	/**
	 * @param cir_tree
	 * @param mutant
	 * @return the infection module of the mutant in CIR code
	 * @throws Exception
	 */
	public static SecInfection parse(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			return parsers.get(mutant.get_mutation().get_class()).parse(cir_tree, mutant);
		}
	}
	
}
