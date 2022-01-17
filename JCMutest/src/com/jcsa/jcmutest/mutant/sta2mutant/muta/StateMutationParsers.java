package com.jcsa.jcmutest.mutant.sta2mutant.muta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.sta2mutant.StateMutation;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OAXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OAXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OBXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OBXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OEXAStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.OLXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.oxxx.ORXNStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.RTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.VBRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.VCRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.refr.VRRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.SBCRStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.SGLRStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.STDLStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.stmt.SWDRStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.BTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.CTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.ETRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.STRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.TTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.trap.VTRPStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UIODStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UIOIStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UIORStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UNODStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.UNOIStateMutationParser;
import com.jcsa.jcmutest.mutant.sta2mutant.muta.unry.VINCStateMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;

public class StateMutationParsers {
	
	private static final Map<MutaClass, StateMutationParser> parsers = new HashMap<MutaClass, StateMutationParser>();
	
	static {
		parsers.put(MutaClass.BTRP, new BTRPStateMutationParser());
		parsers.put(MutaClass.CTRP, new CTRPStateMutationParser());
		parsers.put(MutaClass.ETRP, new ETRPStateMutationParser());
		parsers.put(MutaClass.STRP, new STRPStateMutationParser());
		parsers.put(MutaClass.TTRP, new TTRPStateMutationParser());
		parsers.put(MutaClass.VTRP, new VTRPStateMutationParser());
		
		parsers.put(MutaClass.UIOD, new UIODStateMutationParser());
		parsers.put(MutaClass.UIOI, new UIOIStateMutationParser());
		parsers.put(MutaClass.UIOR, new UIORStateMutationParser());
		parsers.put(MutaClass.UNOD, new UNODStateMutationParser());
		parsers.put(MutaClass.UNOI, new UNOIStateMutationParser());
		parsers.put(MutaClass.VINC, new VINCStateMutationParser());
		
		parsers.put(MutaClass.SBCR, new SBCRStateMutationParser());
		parsers.put(MutaClass.SGLR, new SGLRStateMutationParser());
		parsers.put(MutaClass.STDL, new STDLStateMutationParser());
		parsers.put(MutaClass.SWDR, new SWDRStateMutationParser());
		
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
		
		parsers.put(MutaClass.OAAA, new OAXAStateMutationParser());
		parsers.put(MutaClass.OABA, new OAXAStateMutationParser());
		parsers.put(MutaClass.OAEA, new OAXAStateMutationParser());
		
		parsers.put(MutaClass.OBAA, new OBXAStateMutationParser());
		parsers.put(MutaClass.OBBA, new OBXAStateMutationParser());
		parsers.put(MutaClass.OBEA, new OBXAStateMutationParser());
		
		parsers.put(MutaClass.OEAA, new OEXAStateMutationParser());
		parsers.put(MutaClass.OEBA, new OEXAStateMutationParser());
		
		parsers.put(MutaClass.RTRP, new RTRPStateMutationParser());
		parsers.put(MutaClass.VBRP, new VBRPStateMutationParser());
		parsers.put(MutaClass.VCRP, new VCRPStateMutationParser());
		parsers.put(MutaClass.VRRP, new VRRPStateMutationParser());
	}
	
	/**
	 * @param cir_tree		C-intermediate representatic syntactic tree for state-mutation transformation
	 * @param mutation		abstract syntactic mutation
	 * @return				the collection of state mutations parsed from the AST based mutation
	 * @throws Exception
	 */
	private static Iterable<StateMutation> parse_from(CirTree cir_tree, AstMutation mutation) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else
			return parsers.get(mutation.get_class()).parse(cir_tree, mutation);
	}
	
	/**
	 * @param mutant
	 * @return empty if the transformation failed
	 * @throws Exception
	 */
	public static Iterable<StateMutation> parse(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			try {
				return parse_from(mutant.get_space().get_cir_tree(), mutant.get_mutation());
			}
			catch(Exception ex) {
				return new ArrayList<StateMutation>();
			}
		}
	}
	
}
