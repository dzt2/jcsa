package com.jcsa.jcmutest.mutant.ext2mutant;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;

/**
 * It provide interfaces to extend syntactic mutations in C.
 *
 * @author yukimula
 *
 */
public class MutationExtensions {

	/** extensions based on mutation classes **/
	private static Map<MutaClass, MutationExtension>
		extensions = new HashMap<>();

	static {
		extensions.put(MutaClass.BTRP, new BTRPMutationExtension());
		extensions.put(MutaClass.CTRP, new CTRPMutationExtension());
		extensions.put(MutaClass.ETRP, new ETRPMutationExtension());
		extensions.put(MutaClass.STRP, new STRPMutationExtension());
		extensions.put(MutaClass.TTRP, new TTRPMutationExtension());
		extensions.put(MutaClass.VTRP, new VTRPMutationExtension());

		extensions.put(MutaClass.SBCR, new SBCRMutationExtension());
		extensions.put(MutaClass.SWDR, new SWDRMutationExtension());
		extensions.put(MutaClass.SGLR, new SGLRMutationExtension());
		extensions.put(MutaClass.STDL, new STDLMutationExtension());

		extensions.put(MutaClass.UIOR, new UIORMutationExtension());
		extensions.put(MutaClass.UIOI, new UIOIMutationExtension());
		extensions.put(MutaClass.UIOD, new UIODMutationExtension());
		extensions.put(MutaClass.VINC, new VINCMutationExtension());
		extensions.put(MutaClass.UNOI, new UNOIMutationExtension());
		extensions.put(MutaClass.UNOD, new UNODMutationExtension());

		extensions.put(MutaClass.VBRP, new VBRPMutationExtension());
		extensions.put(MutaClass.VCRP, new VCRPMutationExtension());
		extensions.put(MutaClass.VRRP, new VRRPMutationExtension());
		extensions.put(MutaClass.RTRP, new RTRPMutationExtension());

		extensions.put(MutaClass.OAAN, new OXXNMutationExtension());
		extensions.put(MutaClass.OABN, new OXXNMutationExtension());
		extensions.put(MutaClass.OALN, new OXXNMutationExtension());
		extensions.put(MutaClass.OARN, new OXXNMutationExtension());

		extensions.put(MutaClass.OBAN, new OXXNMutationExtension());
		extensions.put(MutaClass.OBBN, new OXXNMutationExtension());
		extensions.put(MutaClass.OBLN, new OXXNMutationExtension());
		extensions.put(MutaClass.OBRN, new OXXNMutationExtension());

		extensions.put(MutaClass.OLAN, new OXXNMutationExtension());
		extensions.put(MutaClass.OLBN, new OXXNMutationExtension());
		extensions.put(MutaClass.OLLN, new OXXNMutationExtension());
		extensions.put(MutaClass.OLRN, new OXXNMutationExtension());

		extensions.put(MutaClass.ORAN, new OXXNMutationExtension());
		extensions.put(MutaClass.ORBN, new OXXNMutationExtension());
		extensions.put(MutaClass.ORLN, new OXXNMutationExtension());
		extensions.put(MutaClass.ORRN, new OXXNMutationExtension());

		extensions.put(MutaClass.OEAA, new OXXAMutationExtension());
		extensions.put(MutaClass.OEBA, new OXXAMutationExtension());
		extensions.put(MutaClass.OAAA, new OXXAMutationExtension());
		extensions.put(MutaClass.OABA, new OXXAMutationExtension());
		extensions.put(MutaClass.OBAA, new OXXAMutationExtension());
		extensions.put(MutaClass.OBBA, new OXXAMutationExtension());
		extensions.put(MutaClass.OAEA, new OXXAMutationExtension());
		extensions.put(MutaClass.OBEA, new OXXAMutationExtension());

	}

	/**
	 * @param source
	 * @return the coverage, weak and strong version of source mutation
	 * @throws Exception
	 */
	public static AstMutation[] extend(AstMutation source) throws Exception {
		if(!extensions.containsKey(source.get_class()))
			throw new IllegalArgumentException(source.toString());
		else {
			MutationExtension extension = extensions.get(source.get_class());
			return extension.extend(source);
		}
	}

}
