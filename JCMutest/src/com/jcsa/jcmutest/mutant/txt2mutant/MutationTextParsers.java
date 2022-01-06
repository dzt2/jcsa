package com.jcsa.jcmutest.mutant.txt2mutant;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaClass;

class MutationTextParsers {

	private static final Map<MutaClass, MutationTextParser>
			parsers = new HashMap<>();
	static {
		parsers.put(MutaClass.BTRP, new BTRPMutationTextParser());
		parsers.put(MutaClass.CTRP, new CTRPMutationTextParser());
		parsers.put(MutaClass.ETRP, new ETRPMutationTextParser());
		parsers.put(MutaClass.STRP, new STRPMutationTextParser());
		parsers.put(MutaClass.TTRP, new TTRPMutationTextParser());
		parsers.put(MutaClass.VTRP, new VTRPMutationTextParser());

		parsers.put(MutaClass.SBCR, new SBCRMutationTextParser());
		parsers.put(MutaClass.SWDR, new SWDRMutationTextParser());
		parsers.put(MutaClass.SGLR, new SGLRMutationTextParser());
		parsers.put(MutaClass.STDL, new STDLMutationTextParser());

		parsers.put(MutaClass.UIOR, new UIORMutationTextParser());
		parsers.put(MutaClass.UIOI, new UIOIMutationTextParser());
		parsers.put(MutaClass.UIOD, new UIODMutationTextParser());
		parsers.put(MutaClass.VINC, new VINCMutationTextParser());
		parsers.put(MutaClass.UNOI, new UNOIMutationTextParser());
		parsers.put(MutaClass.UNOD, new UNODMutationTextParser());

		parsers.put(MutaClass.VBRP, new VBRPMutationTextParser());
		parsers.put(MutaClass.VCRP, new VCRPMutationTextParser());
		parsers.put(MutaClass.VRRP, new VRRPMutationTextParser());
		parsers.put(MutaClass.RTRP, new RTRPMutationTextParser());

		parsers.put(MutaClass.OAAN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OABN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OALN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OARN, new OXXNMutationTextParser());

		parsers.put(MutaClass.OBAN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OBBN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OBLN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OBRN, new OXXNMutationTextParser());

		parsers.put(MutaClass.OLAN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OLBN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OLLN, new OXXNMutationTextParser());
		parsers.put(MutaClass.OLRN, new OXXNMutationTextParser());

		parsers.put(MutaClass.ORAN, new OXXNMutationTextParser());
		parsers.put(MutaClass.ORBN, new OXXNMutationTextParser());
		parsers.put(MutaClass.ORLN, new OXXNMutationTextParser());
		parsers.put(MutaClass.ORRN, new OXXNMutationTextParser());

		parsers.put(MutaClass.OEAA, new OXXAMutationTextParser());
		parsers.put(MutaClass.OEBA, new OXXAMutationTextParser());
		parsers.put(MutaClass.OAEA, new OXXAMutationTextParser());
		parsers.put(MutaClass.OBEA, new OXXAMutationTextParser());

		parsers.put(MutaClass.OAAA, new OXXAMutationTextParser());
		parsers.put(MutaClass.OABA, new OXXAMutationTextParser());
		parsers.put(MutaClass.OBAA, new OXXAMutationTextParser());
		parsers.put(MutaClass.OBBA, new OXXAMutationTextParser());
	}

	protected static TextMutation parse(AstMutation mutation) throws Exception {
		return parsers.get(mutation.get_class()).parse(mutation);
	}

}
