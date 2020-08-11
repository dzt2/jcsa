package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;

public class MutationGenerators {
	
	/** mutation generators to seed mutants in source code **/
	private static final Map<MutaClass, MutationGenerator> 
		generators = new HashMap<MutaClass, MutationGenerator>();
	private static final List<MutaClass> tc = new ArrayList<MutaClass>();
	private static final List<MutaClass> sc = new ArrayList<MutaClass>();
	private static final List<MutaClass> uc = new ArrayList<MutaClass>();
	private static final List<MutaClass> oc = new ArrayList<MutaClass>();
	private static final List<MutaClass> ac = new ArrayList<MutaClass>();
	private static final List<MutaClass> rc = new ArrayList<MutaClass>();
	
	/* CONSTRUCTION */
	static {
		generators.put(MutaClass.BTRP, new BTRPMutationGenerator());
		generators.put(MutaClass.CTRP, new CTRPMutationGenerator());
		generators.put(MutaClass.ETRP, new ETRPMutationGenerator());
		generators.put(MutaClass.STRP, new STRPMutationGenerator());
		generators.put(MutaClass.TTRP, new TTRPMutationGenerator());
		generators.put(MutaClass.VTRP, new VTRPMutationGenerator());
		tc.add(MutaClass.BTRP); 
		tc.add(MutaClass.CTRP);
		tc.add(MutaClass.ETRP);
		tc.add(MutaClass.STRP);
		tc.add(MutaClass.TTRP);
		tc.add(MutaClass.VTRP);
		
		generators.put(MutaClass.SBCR, new SBCRMutationGenerator());
		generators.put(MutaClass.SWDR, new SWDRMutationGenerator());
		generators.put(MutaClass.SGLR, new SGLRMutationGenerator());
		generators.put(MutaClass.STDL, new STDLMutationGenerator());
		sc.add(MutaClass.SBCR);
		sc.add(MutaClass.SWDR);
		sc.add(MutaClass.SGLR);
		sc.add(MutaClass.STDL);
		
		generators.put(MutaClass.UIOR, new UIORMutationGenerator());
		generators.put(MutaClass.UIOI, new UIOIMutationGenerator());
		generators.put(MutaClass.UIOD, new UIODMutationGenerator());
		generators.put(MutaClass.VINC, new VINCMutationGenerator());
		generators.put(MutaClass.UNOI, new UNOIMutationGenerator());
		generators.put(MutaClass.UNOD, new UNODMutationGenerator());
		uc.add(MutaClass.UIOR);
		uc.add(MutaClass.UIOI);
		uc.add(MutaClass.UIOD);
		uc.add(MutaClass.VINC);
		uc.add(MutaClass.UNOI);
		uc.add(MutaClass.UNOD);
		
		generators.put(MutaClass.VBRP, new VBRPMutationGenerator());
		generators.put(MutaClass.VCRP, new VCRPMutationGenerator());
		generators.put(MutaClass.VRRP, new VRRPMutationGenerator());
		generators.put(MutaClass.RTRP, new RTRPMutationGenerator());
		rc.add(MutaClass.VBRP);
		rc.add(MutaClass.VCRP);
		rc.add(MutaClass.VRRP);
		rc.add(MutaClass.RTRP);
		
		generators.put(MutaClass.OAAN, new OAXNMutationGenerator());
		generators.put(MutaClass.OABN, new OAXNMutationGenerator());
		generators.put(MutaClass.OALN, new OAXNMutationGenerator());
		generators.put(MutaClass.OARN, new OAXNMutationGenerator());
		generators.put(MutaClass.OBAN, new OBXNMutationGenerator());
		generators.put(MutaClass.OBBN, new OBXNMutationGenerator());
		generators.put(MutaClass.OBLN, new OBXNMutationGenerator());
		generators.put(MutaClass.OBRN, new OBXNMutationGenerator());
		generators.put(MutaClass.OLAN, new OLXNMutationGenerator());
		generators.put(MutaClass.OLBN, new OLXNMutationGenerator());
		generators.put(MutaClass.OLLN, new OLXNMutationGenerator());
		generators.put(MutaClass.OLRN, new OLXNMutationGenerator());
		generators.put(MutaClass.ORAN, new ORXNMutationGenerator());
		generators.put(MutaClass.ORBN, new ORXNMutationGenerator());
		generators.put(MutaClass.ORLN, new ORXNMutationGenerator());
		generators.put(MutaClass.ORRN, new ORXNMutationGenerator());
		oc.add(MutaClass.OAAN);
		oc.add(MutaClass.OABN);
		oc.add(MutaClass.OALN);
		oc.add(MutaClass.OARN);
		oc.add(MutaClass.OBAN);
		oc.add(MutaClass.OBBN);
		oc.add(MutaClass.OBLN);
		oc.add(MutaClass.OBRN);
		oc.add(MutaClass.OLAN);
		oc.add(MutaClass.OLBN);
		oc.add(MutaClass.OLLN);
		oc.add(MutaClass.OLRN);
		oc.add(MutaClass.ORAN);
		oc.add(MutaClass.ORBN);
		oc.add(MutaClass.ORLN);
		oc.add(MutaClass.ORRN);
		
		generators.put(MutaClass.OEAA, new OEXAMutationGenerator());
		generators.put(MutaClass.OEBA, new OEXAMutationGenerator());
		generators.put(MutaClass.OAAA, new OAXAMutationGenerator());
		generators.put(MutaClass.OABA, new OAXAMutationGenerator());
		generators.put(MutaClass.OAEA, new OAXAMutationGenerator());
		generators.put(MutaClass.OBAA, new OBXAMutationGenerator());
		generators.put(MutaClass.OBBA, new OBXAMutationGenerator());
		generators.put(MutaClass.OBEA, new OBXAMutationGenerator());
		ac.add(MutaClass.OEAA);
		ac.add(MutaClass.OEBA);
		ac.add(MutaClass.OAAA);
		ac.add(MutaClass.OABA);
		ac.add(MutaClass.OAEA);
		ac.add(MutaClass.OBAA);
		ac.add(MutaClass.OBBA);
		ac.add(MutaClass.OBEA);
	}
	
	/**
	 * @param function
	 * @return the locations in the function to be seeded
	 * @throws Exception
	 */
	private static Iterable<AstNode> get_locations(AstFunctionDefinition function) throws Exception {
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function.get_body());
		List<AstNode> locations = new ArrayList<AstNode>();
		while(!queue.isEmpty()) {
			AstNode location = queue.poll();
			locations.add(location);
			for(int k = 0; k < location.number_of_children(); k++) {
				queue.add(location.get_child(k));
			}
		}
		return locations;
	}
	/**
	 * @param function
	 * @param mutation_classes
	 * @return mutations generated for seeding in the function
	 * @throws Exception
	 */
	private static List<AstMutation> generate(AstFunctionDefinition function,
			Iterable<MutaClass> mutation_classes) throws Exception {
		Iterable<AstNode> locations = get_locations(function);
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		for(MutaClass mutation_class : mutation_classes) {
			MutationGenerator generator = generators.get(mutation_class);
			mutations.addAll(generator.generate(function, locations));
		}
		return mutations;
	}
	/**
	 * @param ast_tree
	 * @param mutation_classes
	 * @return mutations generated for seeding the program
	 * @throws Exception
	 */
	public static List<AstMutation> generate(AstTree ast_tree,
			Iterable<MutaClass> mutation_classes) throws Exception {
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		AstTranslationUnit root = ast_tree.get_ast_root();
		for(int k = 0; k < root.number_of_children(); k++) {
			AstExternalUnit unit = root.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				AstFunctionDefinition function = (AstFunctionDefinition) unit;
				mutations.addAll(generate(function, mutation_classes));
			}
		}
		return mutations;
	}
	
	/* getters */
	public static List<MutaClass> trapping_classes() { return tc; }
	public static List<MutaClass> statement_classes() { return sc; }
	public static List<MutaClass> unary_classes() { return uc; }
	public static List<MutaClass> operator_classes() { return oc; }
	public static List<MutaClass> assign_classes() { return ac; }
	public static List<MutaClass> reference_classes() { return rc; }
	
}
