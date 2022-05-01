package com.jcsa.jcmutest.mutant.ctx2mutant.muta;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutation;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx.OAXAContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx.OAXNContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx.OBXAContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx.OBXNContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx.OEXAContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx.OLXNContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.oxxx.ORXNContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr.RTRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr.VBRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr.VCRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.refr.VRRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt.SBCRContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt.SGLRContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt.STDLContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.stmt.SWDRContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap.BTRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap.CTRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap.ETRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap.STRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap.TTRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.trap.VTRPContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry.UIODContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry.UIOIContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry.UIORContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry.UNODContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry.UNOIContextMutationParser;
import com.jcsa.jcmutest.mutant.ctx2mutant.muta.unry.VINCContextMutationParser;

/**
 * 	It implements the parse from AstMutation to ContextMutation
 * 	
 * 	@author yukimula
 *
 */
public final class ContextMutationParsers {
	
	
	private static final Map<MutaClass, ContextMutationParser> parsers = 
						new HashMap<MutaClass, ContextMutationParser>();
	
	static {
		parsers.put(MutaClass.BTRP, new BTRPContextMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPContextMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPContextMutationParser());
		parsers.put(MutaClass.STRP, new STRPContextMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPContextMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPContextMutationParser());
		
		parsers.put(MutaClass.SBCR, new SBCRContextMutationParser());
		parsers.put(MutaClass.SGLR, new SGLRContextMutationParser());
		parsers.put(MutaClass.STDL, new STDLContextMutationParser());
		parsers.put(MutaClass.SWDR, new SWDRContextMutationParser());
		
		parsers.put(MutaClass.UIOD, new UIODContextMutationParser());
		parsers.put(MutaClass.UIOI, new UIOIContextMutationParser());
		parsers.put(MutaClass.UIOR, new UIORContextMutationParser());
		parsers.put(MutaClass.UNOD, new UNODContextMutationParser());
		parsers.put(MutaClass.UNOI, new UNOIContextMutationParser());
		parsers.put(MutaClass.VINC, new VINCContextMutationParser());
		
		parsers.put(MutaClass.RTRP, new RTRPContextMutationParser());
		parsers.put(MutaClass.VBRP, new VBRPContextMutationParser());
		parsers.put(MutaClass.VCRP, new VCRPContextMutationParser());
		parsers.put(MutaClass.VRRP, new VRRPContextMutationParser());
		
		parsers.put(MutaClass.OAAN, new OAXNContextMutationParser());
		parsers.put(MutaClass.OABN, new OAXNContextMutationParser());
		parsers.put(MutaClass.OALN, new OAXNContextMutationParser());
		parsers.put(MutaClass.OARN, new OAXNContextMutationParser());
		
		parsers.put(MutaClass.OBAN, new OBXNContextMutationParser());
		parsers.put(MutaClass.OBBN, new OBXNContextMutationParser());
		parsers.put(MutaClass.OBLN, new OBXNContextMutationParser());
		parsers.put(MutaClass.OBRN, new OBXNContextMutationParser());
		
		parsers.put(MutaClass.OLAN, new OLXNContextMutationParser());
		parsers.put(MutaClass.OLBN, new OLXNContextMutationParser());
		parsers.put(MutaClass.OLLN, new OLXNContextMutationParser());
		parsers.put(MutaClass.OLRN, new OLXNContextMutationParser());
		
		parsers.put(MutaClass.ORAN, new ORXNContextMutationParser());
		parsers.put(MutaClass.ORBN, new ORXNContextMutationParser());
		parsers.put(MutaClass.ORLN, new ORXNContextMutationParser());
		parsers.put(MutaClass.ORRN, new ORXNContextMutationParser());
		
		parsers.put(MutaClass.OEAA, new OEXAContextMutationParser());
		parsers.put(MutaClass.OEBA, new OEXAContextMutationParser());
		
		parsers.put(MutaClass.OAAA, new OAXAContextMutationParser());
		parsers.put(MutaClass.OABA, new OAXAContextMutationParser());
		parsers.put(MutaClass.OAEA, new OAXAContextMutationParser());
		
		parsers.put(MutaClass.OBAA, new OBXAContextMutationParser());
		parsers.put(MutaClass.OBBA, new OBXAContextMutationParser());
		parsers.put(MutaClass.OBEA, new OBXAContextMutationParser());
	}
	
	public static ContextMutation parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			MutaClass muta_class = mutant.get_mutation().get_class();
			if(parsers.containsKey(muta_class)) {
				return parsers.get(muta_class).parse(mutant);
			}
			else {
				throw new IllegalArgumentException("Undefined: " + muta_class);
			}
		}
	}
	
}
