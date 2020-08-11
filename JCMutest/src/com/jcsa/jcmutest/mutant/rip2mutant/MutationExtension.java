package com.jcsa.jcmutest.mutant.rip2mutant;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;

public class MutationExtension {
	
	/** extenders used to standardize the mutations **/
	private static final Map<MutaClass, MutationExtender> 
		extenders = new HashMap<MutaClass, MutationExtender>();
	
	static {
		extenders.put(MutaClass.BTRP, new BTRPMutationExtender());
		extenders.put(MutaClass.CTRP, new CTRPMutationExtender());
		extenders.put(MutaClass.ETRP, new ETRPMutationExtender());
		extenders.put(MutaClass.STRP, new STRPMutationExtender());
		extenders.put(MutaClass.TTRP, new TTRPMutationExtender());
		extenders.put(MutaClass.VTRP, new VTRPMutationExtender());
		
		extenders.put(MutaClass.SBCR, new SBCRMutationExtender());
		extenders.put(MutaClass.SWDR, new SWDRMutationExtender());
		extenders.put(MutaClass.SGLR, new SGLRMutationExtender());
		extenders.put(MutaClass.STDL, new STDLMutationExtender());
		
		extenders.put(MutaClass.UIOR, new UIORMutationExtender());
		extenders.put(MutaClass.UIOI, new UIOIMutationExtender());
		extenders.put(MutaClass.UIOD, new UIODMutationExtender());
		extenders.put(MutaClass.VINC, new VINCMutationExtender());
		extenders.put(MutaClass.UNOI, new UNOIMutationExtender());
		extenders.put(MutaClass.UNOD, new UNODMutationExtender());
		
		extenders.put(MutaClass.VBRP, new VBRPMutationExtender());
		extenders.put(MutaClass.VCRP, new VCRPMutationExtender());
		extenders.put(MutaClass.VRRP, new VRRPMutationExtender());
		extenders.put(MutaClass.RTRP, new RTRPMutationExtender());
		
		extenders.put(MutaClass.OAAN, new OXXNMutationExtender());
		extenders.put(MutaClass.OABN, new OXXNMutationExtender());
		extenders.put(MutaClass.OALN, new OXXNMutationExtender());
		extenders.put(MutaClass.OARN, new OXXNMutationExtender());
		
		extenders.put(MutaClass.OBAN, new OXXNMutationExtender());
		extenders.put(MutaClass.OBBN, new OXXNMutationExtender());
		extenders.put(MutaClass.OBLN, new OXXNMutationExtender());
		extenders.put(MutaClass.OBRN, new OXXNMutationExtender());
		
		extenders.put(MutaClass.OLAN, new OXXNMutationExtender());
		extenders.put(MutaClass.OLBN, new OXXNMutationExtender());
		extenders.put(MutaClass.OLLN, new OXXNMutationExtender());
		extenders.put(MutaClass.OLRN, new OXXNMutationExtender());
		
		extenders.put(MutaClass.ORAN, new OXXNMutationExtender());
		extenders.put(MutaClass.ORBN, new OXXNMutationExtender());
		extenders.put(MutaClass.ORLN, new OXXNMutationExtender());
		extenders.put(MutaClass.ORRN, new OXXNMutationExtender());
		
		extenders.put(MutaClass.OEAA, new OXXAMutationExtender());
		extenders.put(MutaClass.OEBA, new OXXAMutationExtender());
		extenders.put(MutaClass.OAAA, new OXXAMutationExtender());
		extenders.put(MutaClass.OABA, new OXXAMutationExtender());
		extenders.put(MutaClass.OAEA, new OXXAMutationExtender());
		extenders.put(MutaClass.OBAA, new OXXAMutationExtender());
		extenders.put(MutaClass.OBBA, new OXXAMutationExtender());
		extenders.put(MutaClass.OBEA, new OXXAMutationExtender());
	}
	
	/**
	 * @param mutation
	 * @return [ coverage_mutation, weak_mutation, strong_mutation ]
	 * @throws Exception
	 */
	public static AstMutation[] extend(AstMutation mutation) throws Exception {
		AstMutation[] mutations = new AstMutation[3];
		
		MutationExtender extender = extenders.get(mutation.get_class());
		mutations[0] = extender.coverage_mutation(mutation);
		mutations[1] = extender.weak_mutation(mutation);
		mutations[2] = extender.strong_mutation(mutation);
		
		return mutations;
	}
	
}
