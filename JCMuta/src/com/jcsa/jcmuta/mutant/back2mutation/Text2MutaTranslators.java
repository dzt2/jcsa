package com.jcsa.jcmuta.mutant.back2mutation;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;

import __backup__.MutOperator;
import __backup__.TextMutation;

/**
 * Used to translate the TextMutation as AstMutation
 * @author yukimula
 *
 */
public class Text2MutaTranslators {
	
	private static final Map<MutOperator, Text2MutaTranslator> 
		translators = new HashMap<MutOperator, Text2MutaTranslator>();
	static {
		translators.put(MutOperator.STRP, new STRP2MutaTranslator());
		translators.put(MutOperator.STRI, new STRI2MutaTranslator());
		translators.put(MutOperator.STRC, new STRC2MutaTranslator());
		translators.put(MutOperator.SSDL, new SSDL2MutaTranslator());
		translators.put(MutOperator.SBRC, new SBRC2MutaTranslator());
		translators.put(MutOperator.SCRB, new SCRB2MutaTranslator());
		translators.put(MutOperator.SWDD, new SWDD2MutaTranslator());
		translators.put(MutOperator.SDWD, new SDWD2MutaTranslator());
		translators.put(MutOperator.SSWM, new SSWM2MutaTranslator());
		translators.put(MutOperator.SMTC, new SMTC2MutaTranslator());
		
		translators.put(MutOperator.OPPO, new OPPO2MutaTranslator());
		translators.put(MutOperator.OMMO, new OMMO2MutaTranslator());
		translators.put(MutOperator.UIOI, new UIOI2MutaTranslator());
		translators.put(MutOperator.OBNG, new OBNG2MutaTranslator());
		translators.put(MutOperator.OLNG, new OLNG2MutaTranslator());
		translators.put(MutOperator.OCNG, new OCNG2MutaTranslator());
		translators.put(MutOperator.ONDU, new ONDU2MutaTranslator());
		
		translators.put(MutOperator.CRCR, new CRCR2MutaTranslator());
		translators.put(MutOperator.CCCR, new CCCR2MutaTranslator());
		translators.put(MutOperator.CCSR, new CCSR2MutaTranslator());
		translators.put(MutOperator.VARR, new VARR2MutaTranslator());
		translators.put(MutOperator.VSRR, new VSRR2MutaTranslator());
		translators.put(MutOperator.VPRR, new VPRR2MutaTranslator());
		translators.put(MutOperator.VTRR, new VTRR2MutaTranslator());
		
		translators.put(MutOperator.VABS, new VABS2MutaTranslator());
		translators.put(MutOperator.VBCR, new VBCR2MutaTranslator());
		translators.put(MutOperator.VDTR, new VDTR2MutaTranslator());
		translators.put(MutOperator.VTWD, new VTWD2MutaTranslator());
		
		translators.put(MutOperator.OAAN, new OAAN2MutaTranslator());
		translators.put(MutOperator.OABN, new OABN2MutaTranslator());
		translators.put(MutOperator.OALN, new OALN2MutaTranslator());
		translators.put(MutOperator.OARN, new OARN2MutaTranslator());
		translators.put(MutOperator.OASN, new OABN2MutaTranslator());
		
		translators.put(MutOperator.OBAN, new OBAN2MutaTranslator());
		translators.put(MutOperator.OBBN, new OBBN2MutaTranslator());
		translators.put(MutOperator.OBLN, new OBLN2MutaTranslator());
		translators.put(MutOperator.OBRN, new OBRN2MutaTranslator());
		translators.put(MutOperator.OBSN, new OBBN2MutaTranslator());
		
		translators.put(MutOperator.OLAN, new OLAN2MutaTranslator());
		translators.put(MutOperator.OLBN, new OLBN2MutaTranslator());
		translators.put(MutOperator.OLLN, new OLLN2MutaTranslator());
		translators.put(MutOperator.OLRN, new OLRN2MutaTranslator());
		translators.put(MutOperator.OLSN, new OLBN2MutaTranslator());
		
		translators.put(MutOperator.ORAN, new ORAN2MutaTranslator());
		translators.put(MutOperator.ORBN, new ORBN2MutaTranslator());
		translators.put(MutOperator.ORLN, new ORLN2MutaTranslator());
		translators.put(MutOperator.ORRN, new ORRN2MutaTranslator());
		translators.put(MutOperator.ORSN, new ORBN2MutaTranslator());
		
		translators.put(MutOperator.OSAN, new OBAN2MutaTranslator());
		translators.put(MutOperator.OSBN, new OBBN2MutaTranslator());
		translators.put(MutOperator.OSLN, new OBLN2MutaTranslator());
		translators.put(MutOperator.OSRN, new OBRN2MutaTranslator());
		translators.put(MutOperator.OSSN, new OBBN2MutaTranslator());
		
		translators.put(MutOperator.OEAA, new OEAA2MutaTranslator());
		translators.put(MutOperator.OEBA, new OEBA2MutaTranslator());
		translators.put(MutOperator.OESA, new OEBA2MutaTranslator());
		
		translators.put(MutOperator.OAAA, new OAAA2MutaTranslator());
		translators.put(MutOperator.OABA, new OABA2MutaTranslator());
		translators.put(MutOperator.OASA, new OABA2MutaTranslator());
		
		translators.put(MutOperator.OBAA, new OBAA2MutaTranslator());
		translators.put(MutOperator.OBBA, new OBBA2MutaTranslator());
		translators.put(MutOperator.OBSA, new OBBA2MutaTranslator());
		
		translators.put(MutOperator.OSAA, new OBAA2MutaTranslator());
		translators.put(MutOperator.OSBA, new OBBA2MutaTranslator());
		translators.put(MutOperator.OSSA, new OBBA2MutaTranslator());
	}
	
	/**
	 * parse the TextMutation as AstMutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public static AstMutation parse(TextMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(!translators.containsKey(mutation.get_operator()))
			throw new IllegalArgumentException("Unsupport: " + mutation.get_operator());
		else return translators.get(mutation.get_operator()).parse(mutation);
	}
	
}
