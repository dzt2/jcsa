package com.jcsa.jcmuta.mutant.code2mutation;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.mutant.AstMutation;

/**
 * Singletons of code generator for AstMutation
 * @author yukimula
 *
 */
public class MutaCodeGenerators {
	
	private static final Map<MutaClass, MutaCodeGenerator> 
		generators = new HashMap<MutaClass, MutaCodeGenerator>();
	
	static {
		generators.put(MutaClass.BTRP, new BTRPCodeGenerator());
		generators.put(MutaClass.CTRP, new CTRPCodeGenerator());
		generators.put(MutaClass.ETRP, new ETRPCodeGenerator());
		generators.put(MutaClass.STRP, new STRPCodeGenerator());
		generators.put(MutaClass.TTRP, new TTRPCodeGenerator());
		generators.put(MutaClass.VTRP, new VTRPCodeGenerator());
		
		generators.put(MutaClass.OPDL, new OPDLCodeGenerator());
		generators.put(MutaClass.SBCR, new SBCRCodeGenerator());
		generators.put(MutaClass.SBCI, new SBCICodeGenerator());
		generators.put(MutaClass.SWDR, new SWDRCodeGenerator());
		generators.put(MutaClass.SGLR, new SGLRCodeGenerator());
		generators.put(MutaClass.STDL, new STDLCodeGenerator());
		generators.put(MutaClass.SRTR, new SRTRCodeGenerator());
		
		generators.put(MutaClass.UIOR, new UIORCodeGenerator());
		generators.put(MutaClass.UIOI, new UIOICodeGenerator());
		generators.put(MutaClass.UIOD, new UIODCodeGenerator());
		generators.put(MutaClass.VINC, new VINCCodeGenerator());
		generators.put(MutaClass.UNOI, new UNOICodeGenerator());
		generators.put(MutaClass.UNOD, new UNODCodeGenerator());
		
		OXXNCodeGenerator oxxn = new OXXNCodeGenerator();
		generators.put(MutaClass.OAAN, oxxn);
		generators.put(MutaClass.OABN, oxxn);
		generators.put(MutaClass.OALN, oxxn);
		generators.put(MutaClass.OARN, oxxn);
		generators.put(MutaClass.OBAN, oxxn);
		generators.put(MutaClass.OBBN, oxxn);
		generators.put(MutaClass.OBLN, oxxn);
		generators.put(MutaClass.OBRN, oxxn);
		generators.put(MutaClass.OLAN, oxxn);
		generators.put(MutaClass.OLBN, oxxn);
		generators.put(MutaClass.OLLN, oxxn);
		generators.put(MutaClass.OLRN, oxxn);
		generators.put(MutaClass.ORAN, oxxn);
		generators.put(MutaClass.ORBN, oxxn);
		generators.put(MutaClass.ORLN, oxxn);
		generators.put(MutaClass.ORRN, oxxn);
		
		OXXACodeGenerator oxxa = new OXXACodeGenerator();
		generators.put(MutaClass.OEAA, oxxa);
		generators.put(MutaClass.OEBA, oxxa);
		generators.put(MutaClass.OAAA, oxxa);
		generators.put(MutaClass.OABA, oxxa);
		generators.put(MutaClass.OAEA, oxxa);
		generators.put(MutaClass.OBAA, oxxa);
		generators.put(MutaClass.OBBA, oxxa);
		generators.put(MutaClass.OBEA, oxxa);
		
		generators.put(MutaClass.VBRP, new VBRPCodeGenerator());
		generators.put(MutaClass.VCRP, new VCRPCodeGenerator());
		generators.put(MutaClass.VRRP, new VRRPCodeGenerator());
		
		
		generators.put(MutaClass.EQAR, new EQARCodeGenerator());
		generators.put(MutaClass.OSBI, new OSBICodeGenerator());
		generators.put(MutaClass.OIFI, new OIFICodeGenerator());
		generators.put(MutaClass.OIFR, new OIFRCodeGenerator());
		generators.put(MutaClass.ODFI, new ODFICodeGenerator());
		generators.put(MutaClass.ODFR, new ODFRCodeGenerator());
		generators.put(MutaClass.OFLT, new OFLTCodeGenerator());
	}
	
	/***
	 * generate the code and output it to the file
	 * @param mutation
	 * @param type
	 * @param output
	 * @throws Exception
	 */
	public static void generate_code(AstMutation mutation, MutationCodeType type, File output) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(type == null)
			throw new IllegalArgumentException("Invalid type as null");
		else if(output == null)
			throw new IllegalArgumentException("No output is specified");
		else if(!generators.containsKey(mutation.get_mutation_class()))
			throw new IllegalArgumentException("Unsupport: " + mutation);
		else {
			MutaCodeGenerator generator = generators.get(mutation.get_mutation_class());
			String code = generator.generate(mutation, type);
			
			FileWriter writer = new FileWriter(output);
			writer.write(code); writer.close();
		}
	}
	
}
