package com.jcsa.jcmutest.mutant.cir2mutant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcparse.lang.irlang.CirTree;

/**
 * AstMutation |==> CirMutation*
 * @author yukimula
 *
 */
public class CirMutationParsers {
	
	private static final Map<MutaClass, CirMutationParser> 
		parsers = new HashMap<MutaClass, CirMutationParser>();
	static {
		parsers.put(MutaClass.BTRP, new BTRPMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPMutationParser());
		parsers.put(MutaClass.STRP, new STRPMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPMutationParser());
		
		parsers.put(MutaClass.SBCR, new SBCRMutationParser());
		parsers.put(MutaClass.SWDR, new SWDRMutationParser());
		parsers.put(MutaClass.SGLR, new SGLRMutationParser());
		parsers.put(MutaClass.STDL, new STDLMutationParser());
		
		parsers.put(MutaClass.UIOR, new UIORMutationParser());
		parsers.put(MutaClass.UIOI, new UIOIMutationParser());
		parsers.put(MutaClass.UIOD, new UIODMutationParser());
		parsers.put(MutaClass.VINC, new VINCMutationParser());
		parsers.put(MutaClass.UNOI, new UNOIMutationParser());
		parsers.put(MutaClass.UNOD, new UNODMutationParser());
		
		parsers.put(MutaClass.VBRP, new VBRPMutationParser());
		parsers.put(MutaClass.VCRP, new VCRPMutationParser());
		parsers.put(MutaClass.VRRP, new VRRPMutationParser());
		parsers.put(MutaClass.RTRP, new RTRPMutationParser());
		
		parsers.put(MutaClass.OAAN, new OXXNMutationParser());
		parsers.put(MutaClass.OABN, new OXXNMutationParser());
		parsers.put(MutaClass.OALN, new OXXNMutationParser());
		parsers.put(MutaClass.OARN, new OXXNMutationParser());
		
		parsers.put(MutaClass.OBAN, new OXXNMutationParser());
		parsers.put(MutaClass.OBBN, new OXXNMutationParser());
		parsers.put(MutaClass.OBLN, new OXXNMutationParser());
		parsers.put(MutaClass.OBRN, new OXXNMutationParser());
		
		parsers.put(MutaClass.OLAN, new OXXNMutationParser());
		parsers.put(MutaClass.OLBN, new OXXNMutationParser());
		parsers.put(MutaClass.OLLN, new OXXNMutationParser());
		parsers.put(MutaClass.OLRN, new OXXNMutationParser());
		
		parsers.put(MutaClass.ORAN, new OXXNMutationParser());
		parsers.put(MutaClass.ORBN, new OXXNMutationParser());
		parsers.put(MutaClass.ORLN, new OXXNMutationParser());
		parsers.put(MutaClass.ORRN, new OXXNMutationParser());
		
		parsers.put(MutaClass.OEAA, new OEXAMutationParser());
		parsers.put(MutaClass.OEBA, new OEXAMutationParser());
		parsers.put(MutaClass.OAEA, new OXEAMutationParser());
		parsers.put(MutaClass.OBEA, new OXEAMutationParser());
		
		parsers.put(MutaClass.OAAA, new OXXAMutationParser());
		parsers.put(MutaClass.OABA, new OXXAMutationParser());
		parsers.put(MutaClass.OBAA, new OXXAMutationParser());
		parsers.put(MutaClass.OBBA, new OXXAMutationParser());
	}
	
	/**
	 * @param tree
	 * @param source
	 * @return 	1. the set of cir-mutations parsed from ast-source mutation
	 * 			2. the empty of cir-mutations for equivalent mutation
	 * 			3. null for syntactically incorrect mutation
	 * @throws Exception
	 */
	public static List<CirMutation> parse(CirTree 
			tree, AstMutation source) throws Exception {
		List<CirMutation> targets = new ArrayList<CirMutation>();
		parsers.get(source.get_class()).parse(tree, source, targets);
		return targets;
	}
	
}
