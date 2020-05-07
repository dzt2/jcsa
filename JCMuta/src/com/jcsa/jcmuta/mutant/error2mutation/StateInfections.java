package com.jcsa.jcmuta.mutant.error2mutation;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmuta.MutaClass;
import com.jcsa.jcmuta.MutaOperator;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UIODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UIOIInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UIORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UNODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.UNOIInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.incr.VINCInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.ADDSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.DIVSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MODSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.MULSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaan.SUBMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.ADDRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.DIVRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MODRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.MULRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oabn.SUBRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.ADDLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.ADDLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.DIVLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.DIVLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MODLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MODLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MULLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.MULLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.SUBLANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oaln.SUBLORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGBANInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGBORInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGBXRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGLSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGRSHInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.oexa.ASGSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANADDInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANDIVInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANMODInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANMULInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.olan.LANSUBInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SBCIInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SBCRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SGLRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SRTRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.STDLInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.stmt.SWDRInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.BTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.CTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.ETRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.STRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.TTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.trap.VTRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.vars.VBRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.vars.VCRPInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.vars.VRRPInfection;

/**
 * Collect all the state infection machine for each mutation operator.
 * 
 * @author yukimula
 *
 */
public class StateInfections {
	
	private static final Map<Object, StateInfection> infections = new HashMap<Object, StateInfection>();
	
	static {
		/** TRAP-CLASS **/
		infections.put(MutaClass.BTRP, new BTRPInfection());
		infections.put(MutaClass.CTRP, new CTRPInfection());
		infections.put(MutaClass.ETRP, new ETRPInfection());
		infections.put(MutaClass.STRP, new STRPInfection());
		infections.put(MutaClass.TTRP, new TTRPInfection());
		infections.put(MutaClass.VTRP, new VTRPInfection());
		
		/** STMT-CLASS **/
		infections.put(MutaClass.SBCR, new SBCRInfection());
		infections.put(MutaClass.SBCI, new SBCIInfection());
		infections.put(MutaClass.SWDR, new SWDRInfection());
		infections.put(MutaClass.SGLR, new SGLRInfection());
		infections.put(MutaClass.STDL, new STDLInfection());
		
		/** INCR-CLASS **/
		infections.put(MutaClass.UIOR, new UIORInfection());
		infections.put(MutaClass.UIOI, new UIOIInfection());
		infections.put(MutaClass.UIOD, new UIODInfection());
		infections.put(MutaClass.VINC, new VINCInfection());
		infections.put(MutaClass.UNOI, new UNOIInfection());
		infections.put(MutaClass.UNOD, new UNODInfection());
		
		/** VARS-CLASS **/
		infections.put(MutaClass.VBRP, new VBRPInfection());
		infections.put(MutaClass.VCRP, new VCRPInfection());
		infections.put(MutaClass.VRRP, new VRRPInfection());
		infections.put(MutaClass.SRTR, new SRTRInfection());
		
		/** OEXA **/
		infections.put(MutaOperator.assign_to_arith_add_assign, new ASGADDInfection());
		infections.put(MutaOperator.assign_to_arith_sub_assign, new ASGSUBInfection());
		infections.put(MutaOperator.assign_to_arith_mul_assign, new ASGMULInfection());
		infections.put(MutaOperator.assign_to_arith_div_assign, new ASGDIVInfection());
		infections.put(MutaOperator.assign_to_arith_mod_assign, new ASGMODInfection());
		infections.put(MutaOperator.assign_to_bitws_and_assign, new ASGBANInfection());
		infections.put(MutaOperator.assign_to_bitws_ior_assign, new ASGBORInfection());
		infections.put(MutaOperator.assign_to_bitws_xor_assign, new ASGBXRInfection());
		infections.put(MutaOperator.assign_to_bitws_lsh_assign, new ASGLSHInfection());
		infections.put(MutaOperator.assign_to_bitws_rsh_assign, new ASGRSHInfection());
		
		/** OAAN **/
		infections.put(MutaOperator.arith_add_to_arith_sub, new ADDSUBInfection());
		infections.put(MutaOperator.arith_add_to_arith_mul, new ADDMULInfection());
		infections.put(MutaOperator.arith_add_to_arith_div, new ADDDIVInfection());
		infections.put(MutaOperator.arith_add_to_arith_mod, new ADDMODInfection());
		infections.put(MutaOperator.arith_sub_to_arith_add, new SUBADDInfection());
		infections.put(MutaOperator.arith_sub_to_arith_mul, new SUBMULInfection());
		infections.put(MutaOperator.arith_sub_to_arith_div, new SUBDIVInfection());
		infections.put(MutaOperator.arith_sub_to_arith_mod, new SUBMODInfection());
		infections.put(MutaOperator.arith_mul_to_arith_add, new MULADDInfection());
		infections.put(MutaOperator.arith_mul_to_arith_sub, new MULSUBInfection());
		infections.put(MutaOperator.arith_mul_to_arith_div, new MULDIVInfection());
		infections.put(MutaOperator.arith_mul_to_arith_mod, new MULMODInfection());
		infections.put(MutaOperator.arith_div_to_arith_add, new DIVADDInfection());
		infections.put(MutaOperator.arith_div_to_arith_sub, new DIVSUBInfection());
		infections.put(MutaOperator.arith_div_to_arith_mul, new DIVMULInfection());
		infections.put(MutaOperator.arith_div_to_arith_mod, new DIVMODInfection());
		infections.put(MutaOperator.arith_mod_to_arith_add, new MODADDInfection());
		infections.put(MutaOperator.arith_mod_to_arith_sub, new MODSUBInfection());
		infections.put(MutaOperator.arith_mod_to_arith_mul, new MODMULInfection());
		infections.put(MutaOperator.arith_mod_to_arith_div, new MODDIVInfection());
		
		/** OABN **/
		infections.put(MutaOperator.arith_add_to_bitws_and, new ADDBANInfection());
		infections.put(MutaOperator.arith_add_to_bitws_ior, new ADDBORInfection());
		infections.put(MutaOperator.arith_add_to_bitws_xor, new ADDBXRInfection());
		infections.put(MutaOperator.arith_add_to_bitws_lsh, new ADDLSHInfection());
		infections.put(MutaOperator.arith_add_to_bitws_rsh, new ADDRSHInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_and, new SUBBANInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_ior, new SUBBORInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_xor, new SUBBXRInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_lsh, new SUBLSHInfection());
		infections.put(MutaOperator.arith_sub_to_bitws_rsh, new SUBRSHInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_and, new MULBANInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_ior, new MULBORInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_xor, new MULBXRInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_lsh, new MULLSHInfection());
		infections.put(MutaOperator.arith_mul_to_bitws_rsh, new MULRSHInfection());
		infections.put(MutaOperator.arith_div_to_bitws_and, new DIVBANInfection());
		infections.put(MutaOperator.arith_div_to_bitws_ior, new DIVBORInfection());
		infections.put(MutaOperator.arith_div_to_bitws_xor, new DIVBXRInfection());
		infections.put(MutaOperator.arith_div_to_bitws_lsh, new DIVLSHInfection());
		infections.put(MutaOperator.arith_div_to_bitws_rsh, new DIVRSHInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_and, new MODBANInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_ior, new MODBORInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_xor, new MODBXRInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_lsh, new MODLSHInfection());
		infections.put(MutaOperator.arith_mod_to_bitws_rsh, new MODRSHInfection());
		
		/** OALN **/
		infections.put(MutaOperator.arith_add_to_logic_and, new ADDLANInfection());
		infections.put(MutaOperator.arith_sub_to_logic_and, new SUBLANInfection());
		infections.put(MutaOperator.arith_mul_to_logic_and, new MULLANInfection());
		infections.put(MutaOperator.arith_div_to_logic_and, new DIVLANInfection());
		infections.put(MutaOperator.arith_mod_to_logic_and, new MODLANInfection());
		infections.put(MutaOperator.arith_add_to_logic_ior, new ADDLORInfection());
		infections.put(MutaOperator.arith_sub_to_logic_ior, new SUBLORInfection());
		infections.put(MutaOperator.arith_mul_to_logic_ior, new MULLORInfection());
		infections.put(MutaOperator.arith_div_to_logic_ior, new DIVLORInfection());
		infections.put(MutaOperator.arith_mod_to_logic_ior, new MODLORInfection());
		
		/** OARN **/
		
	}
	
}
