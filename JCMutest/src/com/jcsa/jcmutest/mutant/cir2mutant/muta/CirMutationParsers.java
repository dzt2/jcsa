package com.jcsa.jcmutest.mutant.cir2mutant.muta;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
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
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcparse.lang.irlang.CirTree;

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

	public static Iterable<CirMutation> parse(CirTree cir_tree, AstMutation mutation) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else
			return parsers.get(mutation.get_class()).parse(cir_tree, mutation);
	}

}
