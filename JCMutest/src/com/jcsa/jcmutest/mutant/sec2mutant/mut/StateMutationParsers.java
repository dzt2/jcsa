package com.jcsa.jcmutest.mutant.sec2mutant.mut;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt.OAXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt.OAXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt.OBXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt.OBXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt.OEXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt.OLXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.oprt.ORXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.refs.RTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.refs.VBRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.refs.VCRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.refs.VRRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.stmt.SBCRStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.stmt.SGLRStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.stmt.STDLStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.stmt.SWDRStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.trap.BTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.trap.CTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.trap.ETRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.trap.STRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.trap.TTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.trap.VTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.unry.UIODStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.unry.UIOIStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.unry.UIORStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.unry.UNODStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.unry.UNOIStateMutationParser;
import com.jcsa.jcmutest.mutant.sec2mutant.mut.unry.VINCStateMutationParser;

class StateMutationParsers {
	
	protected static final Map<MutaClass, StateMutationParser> parsers = 
							new HashMap<MutaClass, StateMutationParser>();
	
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
}
