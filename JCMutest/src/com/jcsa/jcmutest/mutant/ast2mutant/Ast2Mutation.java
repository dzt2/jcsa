package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.MutaClass;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt.OAXAMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt.OAXNMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt.OBXAMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt.OBXNMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt.OEXAMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt.OLXNMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.oprt.ORXNMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.refs.RTRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.refs.VBRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.refs.VCRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.refs.VRRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.stmt.SBCRMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.stmt.SGLRMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.stmt.STDLMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.stmt.SWDRMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.trap.BTRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.trap.CTRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.trap.ETRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.trap.STRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.trap.TTRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.trap.VTRPMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.unary.UIODMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.unary.UIOIMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.unary.UIORMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.unary.UNODMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.unary.UNOIMutationGenerator;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.unary.VINCMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.astree.unit.AstExternalUnit;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.astree.unit.AstTranslationUnit;

/**
 * It provides interface to generate mutations by seeding syntactic faults
 * in the source code based on its abstract syntax structure.
 * 
 * @author yukimula
 *
 */
public class Ast2Mutation {
	
	/** mutation generator based on mutation operator class **/
	private static final Map<MutaClass, AstMutationGenerator> 
		generators = new HashMap<MutaClass, AstMutationGenerator>();
	
	private static final List<MutaClass> trap_classes = new ArrayList<MutaClass>();
	private static final List<MutaClass> unary_classes = new ArrayList<MutaClass>();
	private static final List<MutaClass> stmt_classes = new ArrayList<MutaClass>();
	private static final List<MutaClass> oprt_classes = new ArrayList<MutaClass>();
	private static final List<MutaClass> assign_classes = new ArrayList<MutaClass>();
	private static final List<MutaClass> refers_classes = new ArrayList<MutaClass>();
	
	static {
		/* trapping class */
		generators.put(MutaClass.BTRP, new BTRPMutationGenerator());
		generators.put(MutaClass.CTRP, new CTRPMutationGenerator());
		generators.put(MutaClass.ETRP, new ETRPMutationGenerator());
		generators.put(MutaClass.STRP, new STRPMutationGenerator());
		generators.put(MutaClass.TTRP, new TTRPMutationGenerator());
		generators.put(MutaClass.VTRP, new VTRPMutationGenerator());
		trap_classes.add(MutaClass.BTRP);
		trap_classes.add(MutaClass.CTRP);
		trap_classes.add(MutaClass.ETRP);
		trap_classes.add(MutaClass.STRP);
		trap_classes.add(MutaClass.TTRP);
		trap_classes.add(MutaClass.VTRP);
		
		/* statement class */
		generators.put(MutaClass.SBCR, new SBCRMutationGenerator());
		generators.put(MutaClass.SWDR, new SWDRMutationGenerator());
		generators.put(MutaClass.SGLR, new SGLRMutationGenerator());
		generators.put(MutaClass.STDL, new STDLMutationGenerator());
		stmt_classes.add(MutaClass.SBCR);
		stmt_classes.add(MutaClass.SWDR);
		stmt_classes.add(MutaClass.SGLR);
		stmt_classes.add(MutaClass.STDL);
		
		/* unary operator class */
		generators.put(MutaClass.UIOR, new UIORMutationGenerator());
		generators.put(MutaClass.UIOI, new UIOIMutationGenerator());
		generators.put(MutaClass.UIOD, new UIODMutationGenerator());
		generators.put(MutaClass.VINC, new VINCMutationGenerator());
		generators.put(MutaClass.UNOI, new UNOIMutationGenerator());
		generators.put(MutaClass.UNOD, new UNODMutationGenerator());
		unary_classes.add(MutaClass.UIOR);
		unary_classes.add(MutaClass.UIOI);
		unary_classes.add(MutaClass.UIOD);
		unary_classes.add(MutaClass.VINC);
		unary_classes.add(MutaClass.UNOI);
		unary_classes.add(MutaClass.UNOD);
		
		/* binary operator class */
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
		oprt_classes.add(MutaClass.OAAN);
		oprt_classes.add(MutaClass.OABN);
		oprt_classes.add(MutaClass.OALN);
		oprt_classes.add(MutaClass.OARN);
		oprt_classes.add(MutaClass.OBAN);
		oprt_classes.add(MutaClass.OBBN);
		oprt_classes.add(MutaClass.OBLN);
		oprt_classes.add(MutaClass.OBRN);
		oprt_classes.add(MutaClass.OLAN);
		oprt_classes.add(MutaClass.OLBN);
		oprt_classes.add(MutaClass.OLLN);
		oprt_classes.add(MutaClass.OLRN);
		oprt_classes.add(MutaClass.ORAN);
		oprt_classes.add(MutaClass.ORBN);
		oprt_classes.add(MutaClass.ORLN);
		oprt_classes.add(MutaClass.ORRN);
		
		/* assign operator class */
		generators.put(MutaClass.OEAA, new OEXAMutationGenerator());
		generators.put(MutaClass.OEBA, new OEXAMutationGenerator());
		generators.put(MutaClass.OAEA, new OAXAMutationGenerator());
		generators.put(MutaClass.OAAA, new OAXAMutationGenerator());
		generators.put(MutaClass.OABA, new OAXAMutationGenerator());
		generators.put(MutaClass.OBEA, new OBXAMutationGenerator());
		generators.put(MutaClass.OBAA, new OBXAMutationGenerator());
		generators.put(MutaClass.OBBA, new OBXAMutationGenerator());
		assign_classes.add(MutaClass.OEAA);
		assign_classes.add(MutaClass.OEBA);
		assign_classes.add(MutaClass.OAAA);
		assign_classes.add(MutaClass.OABA);
		assign_classes.add(MutaClass.OAEA);
		assign_classes.add(MutaClass.OBAA);
		assign_classes.add(MutaClass.OBBA);
		assign_classes.add(MutaClass.OBEA);
		
		/* reference class */
		generators.put(MutaClass.VBRP, new VBRPMutationGenerator());
		generators.put(MutaClass.VCRP, new VCRPMutationGenerator());
		generators.put(MutaClass.VRRP, new VRRPMutationGenerator());
		generators.put(MutaClass.RTRP, new RTRPMutationGenerator());
		refers_classes.add(MutaClass.VBRP);
		refers_classes.add(MutaClass.VCRP);
		refers_classes.add(MutaClass.VRRP);
		refers_classes.add(MutaClass.RTRP);
	}
	
	public static Collection<MutaClass> trap_mutation_classes() { return trap_classes; }
	public static Collection<MutaClass> unary_mutation_classes() { return unary_classes; }
	public static Collection<MutaClass> statement_mutation_classes() { return stmt_classes; }
	public static Collection<MutaClass> operator_mutation_classes() { return oprt_classes; }
	public static Collection<MutaClass> assign_mutation_classes() { return assign_classes; }
	public static Collection<MutaClass> reference_mutation_classes() { return refers_classes; }
	
	/**
	 * @param function
	 * @return the nodes in function body
	 * @throws Exception
	 */
	private static Iterable<AstNode> get_locations(AstFunctionDefinition function) throws Exception {
		List<AstNode> locations = new ArrayList<AstNode>();
		Queue<AstNode> queue = new LinkedList<AstNode>();
		queue.add(function);
		while(!queue.isEmpty()) {
			AstNode node = queue.poll();
			locations.add(node);
			for(int k = 0; k < node.number_of_children(); k++) {
				queue.add(node.get_child(k));
			}
		}
		return locations;
	}
	
	/**
	 * @param function
	 * @param mutation_classes
	 * @return seed mutations in one function w.r.t. the 
	 * @throws Exception
	 */
	private static Collection<AstMutation> seed(AstFunctionDefinition 
			function, Iterable<MutaClass> mutation_classes) throws Exception {
		Set<String> mutation_keys = new HashSet<String>();
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		Iterable<AstNode> locations = get_locations(function);
		for(MutaClass mutation_class : mutation_classes) {
			AstMutationGenerator generator = generators.get(mutation_class);
			Iterable<AstMutation> local_mutations = generator.generate(locations);
			for(AstMutation mutation : local_mutations) {
				String key = mutation.toString();
				if(!mutation_keys.contains(key)) {
					mutation_keys.add(key);
					mutations.add(mutation);
				}
			}
		}
		return mutations;
	}
	
	/**
	 * @param tree the abstract syntax tree in which mutations are seeded
	 * @param mutation_classes
	 * @return 
	 * @throws Exception
	 */
	public static Collection<AstMutation> seed(AstTree tree, 
			Iterable<MutaClass> mutation_classes) throws Exception {
		List<AstMutation> mutations = new ArrayList<AstMutation>();
		AstTranslationUnit ast_root = tree.get_ast_root();
		for(int k = 0; k < ast_root.number_of_units(); k++) {
			AstExternalUnit unit = ast_root.get_unit(k);
			if(unit instanceof AstFunctionDefinition) {
				Collection<AstMutation> local_mutations = seed(
						(AstFunctionDefinition) unit, mutation_classes);
				mutations.addAll(local_mutations);
			}
		}
		return mutations;
	}
	
	/**
	 * @param mutation
	 * @return the string that preserves the state of the mutation information
	 * @throws Exception
	 */
	public static String mutation2string(AstMutation mutation) throws Exception {
		return AstMutations.mutation2string(mutation);
	}
	
	/**
	 * @param ast_tree
	 * @param mut_str
	 * @return the mutation generated from the text line by interpreting the
	 * 			abstract syntactic tree node.
	 * @throws Exception
	 */
	public static AstMutation string2mutation(AstTree tree, String line) throws Exception {
		return AstMutations.string2mutation(tree, line);
	}
	
}
