package com.jcsa.jcmuta.mutant.ast2mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.AstTree;

/**
 * Used to generate the AST based mutation within specified program of specified operator classes.
 * @author yukimula
 *
 */
public class AstMutationGenerators {
	
	/** the singleton of mutation generator of each class **/
	protected static final Map<MutaClass, AstMutationGenerator> 
		generators = new HashMap<MutaClass, AstMutationGenerator>();
	
	/* operator groups */
	public static final Set<MutaClass> trapping_classes = new HashSet<MutaClass>();
	public static final Set<MutaClass> statement_classes = new HashSet<MutaClass>();
	public static final Set<MutaClass> unary_classes = new HashSet<MutaClass>();
	public static final Set<MutaClass> operator_classes = new HashSet<MutaClass>();
	public static final Set<MutaClass> expression_classes = new HashSet<MutaClass>();
	public static final Set<MutaClass> semantic_classes = new HashSet<MutaClass>();
	
	/** initialization and construction of generators **/
	static {
		/* trapping mutation */
		generators.put(MutaClass.BTRP, new BTRPMutationGenerator());
		generators.put(MutaClass.CTRP, new CTRPMutationGenerator());
		generators.put(MutaClass.ETRP, new ETRPMutationGenerator());
		generators.put(MutaClass.STRP, new STRPMutationGenerator());
		generators.put(MutaClass.TTRP, new TTRPMutationGenerator());
		generators.put(MutaClass.VTRP, new VTRPMutationGenerator());
		
		/* statement mutation */
		generators.put(MutaClass.OPDL, new OPDLMutationGenerator());
		generators.put(MutaClass.SBCR, new SBCRMutationGenerator());
		generators.put(MutaClass.SBCI, new SBCIMutationGenerator());
		generators.put(MutaClass.SWDR, new SWDRMutationGenerator());
		generators.put(MutaClass.SGLR, new SGLRMutationGenerator());
		generators.put(MutaClass.STDL, new STDLMutationGenerator());
		generators.put(MutaClass.SRTR, new SRTRMutationGenerator());
		
		/* unary mutation */
		generators.put(MutaClass.UIOR, new UIORMutationGenerator());
		generators.put(MutaClass.UIOI, new UIOIMutationGenerator());
		generators.put(MutaClass.UIOD, new UIODMutationGenerator());
		generators.put(MutaClass.VINC, new VINCMutationGenerator());
		generators.put(MutaClass.UNOI, new UNOIMutationGenerator());
		generators.put(MutaClass.UNOD, new UNODMutationGenerator());
		
		/* operator mutations */
		generators.put(MutaClass.OAAN, new OAANMutationGenerator());
		generators.put(MutaClass.OABN, new OABNMutationGenerator());
		generators.put(MutaClass.OALN, new OALNMutationGenerator());
		generators.put(MutaClass.OARN, new OARNMutationGenerator());
		generators.put(MutaClass.OBAN, new OBANMutationGenerator());
		generators.put(MutaClass.OBBN, new OBBNMutationGenerator());
		generators.put(MutaClass.OBLN, new OBLNMutationGenerator());
		generators.put(MutaClass.OBRN, new OBRNMutationGenerator());
		generators.put(MutaClass.OLAN, new OLANMutationGenerator());
		generators.put(MutaClass.OLBN, new OLBNMutationGenerator());
		generators.put(MutaClass.OLLN, new OLLNMutationGenerator());
		generators.put(MutaClass.OLRN, new OLRNMutationGenerator());
		generators.put(MutaClass.ORAN, new ORANMutationGenerator());
		generators.put(MutaClass.ORBN, new ORBNMutationGenerator());
		generators.put(MutaClass.ORLN, new ORLNMutationGenerator());
		generators.put(MutaClass.ORRN, new ORRNMutationGenerator());
		generators.put(MutaClass.OEAA, new OEAAMutationGenerator());
		generators.put(MutaClass.OEBA, new OEBAMutationGenerator());
		generators.put(MutaClass.OAAA, new OAAAMutationGenerator());
		generators.put(MutaClass.OABA, new OABAMutationGenerator());
		generators.put(MutaClass.OAEA, new OAEAMutationGenerator());
		generators.put(MutaClass.OBAA, new OBAAMutationGenerator());
		generators.put(MutaClass.OBBA, new OBBAMutationGenerator());
		generators.put(MutaClass.OBEA, new OBEAMutationGenerator());
		
		/* expression mutation */
		generators.put(MutaClass.VBRP, new VBRPMutationGenerator());
		generators.put(MutaClass.VCRP, new VCRPMutationGenerator());
		generators.put(MutaClass.VRRP, new VRRPMutationGenerator());
		
		/* semantic mutations */
		generators.put(MutaClass.EQAR, new EQARMutationGenerator());
		generators.put(MutaClass.OSBI, new OSBIMutationGenerator());
		generators.put(MutaClass.OIFI, new OIFIMutationGenerator());
		generators.put(MutaClass.OIFR, new OIFRMutationGenerator());
		generators.put(MutaClass.ODFI, new ODFIMutationGenerator());
		generators.put(MutaClass.ODFR, new ODFRMutationGenerator());
		generators.put(MutaClass.OFLT, new OFLTMutationGenerator());
		
		/* operator groups construction */
		trapping_classes.add(MutaClass.BTRP);
		trapping_classes.add(MutaClass.CTRP);
		trapping_classes.add(MutaClass.ETRP);
		trapping_classes.add(MutaClass.STRP);
		trapping_classes.add(MutaClass.TTRP);
		trapping_classes.add(MutaClass.VTRP);
		
		statement_classes.add(MutaClass.OPDL);
		statement_classes.add(MutaClass.SBCI);
		statement_classes.add(MutaClass.SBCR);
		statement_classes.add(MutaClass.SWDR);
		statement_classes.add(MutaClass.SGLR);
		statement_classes.add(MutaClass.STDL);
		statement_classes.add(MutaClass.SRTR);
		
		unary_classes.add(MutaClass.UIOI);
		unary_classes.add(MutaClass.UIOR);
		unary_classes.add(MutaClass.UIOD);
		unary_classes.add(MutaClass.VINC);
		unary_classes.add(MutaClass.UNOI);
		unary_classes.add(MutaClass.UNOD);
		
		operator_classes.add(MutaClass.OAAN);
		operator_classes.add(MutaClass.OABN);
		operator_classes.add(MutaClass.OALN);
		operator_classes.add(MutaClass.OARN);
		operator_classes.add(MutaClass.OBAN);
		operator_classes.add(MutaClass.OBBN);
		operator_classes.add(MutaClass.OBLN);
		operator_classes.add(MutaClass.OBRN);
		operator_classes.add(MutaClass.OLAN);
		operator_classes.add(MutaClass.OLBN);
		operator_classes.add(MutaClass.OLLN);
		operator_classes.add(MutaClass.OLRN);
		operator_classes.add(MutaClass.ORAN);
		operator_classes.add(MutaClass.ORBN);
		operator_classes.add(MutaClass.ORLN);
		operator_classes.add(MutaClass.ORRN);
		
		operator_classes.add(MutaClass.OEAA);
		operator_classes.add(MutaClass.OEBA);
		operator_classes.add(MutaClass.OAAA);
		operator_classes.add(MutaClass.OABA);
		operator_classes.add(MutaClass.OAEA);
		operator_classes.add(MutaClass.OBAA);
		operator_classes.add(MutaClass.OBBA);
		operator_classes.add(MutaClass.OBEA);
		
		expression_classes.add(MutaClass.VBRP);
		expression_classes.add(MutaClass.VCRP);
		expression_classes.add(MutaClass.VRRP);
		
		semantic_classes.add(MutaClass.EQAR);
		semantic_classes.add(MutaClass.OSBI);
		semantic_classes.add(MutaClass.OIFI);
		semantic_classes.add(MutaClass.OIFR);
		semantic_classes.add(MutaClass.ODFI);
		semantic_classes.add(MutaClass.ODFR);
		semantic_classes.add(MutaClass.OFLT);
	}
	
	/* generation methods */
	/**
	 * generate the mutations seeded in AST for specified mutation operators
	 * @param ast_tree
	 * @param mutation_classes
	 * @return
	 * @throws Exception
	 */
	public static Collection<AstMutation> generate(AstTree ast_tree, 
			Set<MutaClass> mutation_classes) throws Exception {
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(MutaClass mutation_class : mutation_classes) {
			AstMutationGenerator generator = generators.get(mutation_class);
			Collection<AstMutation> buffer = generator.generate(ast_tree);
			mutations.addAll(buffer);
		}
		return mutations;
	}
	/**
	 * generate the mutants with respect to the given operator
	 * @param ast_tree
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	public static Collection<AstMutation> generate(AstTree ast_tree, MutaClass operator) throws Exception {
		AstMutationGenerator generator = generators.get(operator);
		return generator.generate(ast_tree);
	}
	
}
