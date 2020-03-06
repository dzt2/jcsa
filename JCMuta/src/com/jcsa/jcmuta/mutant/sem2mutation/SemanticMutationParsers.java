package com.jcsa.jcmuta.mutant.sem2mutation;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.sem2mutation.assn.OAXAMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.assn.OBXAMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.assn.OEXAMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.oprt.OXXNMutationParsers;
import com.jcsa.jcmuta.mutant.sem2mutation.stmt.SBCIMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.stmt.SBCRMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.stmt.SGLRMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.stmt.STDLMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.stmt.SWDRMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.trap.BTRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.trap.CTRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.trap.ETRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.trap.STRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.trap.TTRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.trap.VTRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.unry.UIODMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.unry.UIOIMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.unry.UIORMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.unry.UNODMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.unry.UNOIMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.unry.VINCMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.vars.SRTRMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.vars.VBRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.vars.VCRPMutationParser;
import com.jcsa.jcmuta.mutant.sem2mutation.vars.VRRPMutationParser;
import com.jcsa.jcmuta.project.Mutant;

public class SemanticMutationParsers {
	
	private static final Map<MutaClass, SemanticMutationParser> parsers = new HashMap<MutaClass, SemanticMutationParser>();
	static {
		parsers.put(MutaClass.BTRP, new BTRPMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPMutationParser());
		parsers.put(MutaClass.STRP, new STRPMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPMutationParser());
		
		parsers.put(MutaClass.SBCR, new SBCRMutationParser());
		parsers.put(MutaClass.SBCI, new SBCIMutationParser());
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
		parsers.put(MutaClass.SRTR, new SRTRMutationParser());
		
		OXXNMutationParsers oxxn_parser = new OXXNMutationParsers();
		parsers.put(MutaClass.OAAN, oxxn_parser);
		parsers.put(MutaClass.OABN, oxxn_parser);
		parsers.put(MutaClass.OALN, oxxn_parser);
		parsers.put(MutaClass.OARN, oxxn_parser);
		parsers.put(MutaClass.OBAN, oxxn_parser);
		parsers.put(MutaClass.OBBN, oxxn_parser);
		parsers.put(MutaClass.OBLN, oxxn_parser);
		parsers.put(MutaClass.OBRN, oxxn_parser);
		parsers.put(MutaClass.OLAN, oxxn_parser);
		parsers.put(MutaClass.OLBN, oxxn_parser);
		parsers.put(MutaClass.OLLN, oxxn_parser);
		parsers.put(MutaClass.OLRN, oxxn_parser);
		parsers.put(MutaClass.ORAN, oxxn_parser);
		parsers.put(MutaClass.ORBN, oxxn_parser);
		parsers.put(MutaClass.ORLN, oxxn_parser);
		parsers.put(MutaClass.ORRN, oxxn_parser);
		
		parsers.put(MutaClass.OEAA, new OEXAMutationParser());
		parsers.put(MutaClass.OEBA, new OEXAMutationParser());
		parsers.put(MutaClass.OAAA, new OAXAMutationParser());
		parsers.put(MutaClass.OABA, new OAXAMutationParser());
		parsers.put(MutaClass.OAEA, new OAXAMutationParser());
		parsers.put(MutaClass.OBAA, new OBXAMutationParser());
		parsers.put(MutaClass.OBBA, new OBXAMutationParser());
		parsers.put(MutaClass.OBEA, new OBXAMutationParser());
	}
	
	public static SemanticMutation parse(Mutant mutant) throws Exception {
		if(mutant == null)
			throw new IllegalArgumentException("Invalid ast-mutation");
		else if(!parsers.containsKey(mutant.get_mutation().get_mutation_class()))
			throw new IllegalArgumentException("Invalid: " + mutant.get_mutation().get_mutation_class());
		else return parsers.get(mutant.get_mutation().get_mutation_class()).parse(mutant);
	}
	
}
