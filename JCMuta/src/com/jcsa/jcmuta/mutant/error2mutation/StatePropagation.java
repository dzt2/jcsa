package com.jcsa.jcmuta.mutant.error2mutation;

import com.jcsa.jcmuta.mutant.error2mutation.process.ADDLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.ADDROFProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.ADDROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.ASGRLProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BANLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BANROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BORLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BORROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BXRLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.BXRROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.CASTProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.CONDFProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.CONDTProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DEFERProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DEFUSEProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DIVLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.DIVROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.EQVLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.EQVROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.FIELDOFProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GRELOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GREROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GRTLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.GRTROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.INITBODYProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.LSHLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.LSHROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MODLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MODROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MULLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.MULROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NEGProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NEQLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NEQROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.NOTProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.RSHLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.RSHROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.RSVProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMELOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMEROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMTLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SMTROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SUBLOPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.SUBROPProcess;
import com.jcsa.jcmuta.mutant.error2mutation.process.WAITFORProcess;

/**
 * Propagation method:<br>
 * 	(1) expression --> expression as parent:<br>
 * 		{defer, address, field, cast, compute, init_body, wait}
 * 	(2) expression as condition --> statement in true branch<br>
 * 	(3) expression as condition --> statement in false branch<br>
 * 	(4) expression as right-val --> left-value in assignment.<br>
 * 	(5) expression as definition --> usage point(s) in other statements.<br>
 * 	(6) expression as argument --> expression as parameter in calling.<br>
 * 	(7) expression as return point --> expression as waiting expression.<br>
 * 
 * TODO implement this class!
 * @author yukimula
 *
 */
public class StatePropagation {
	
	// list of state process instances
	private static final StateProcess dereference_process 	= new DEFERProcess();
	private static final StateProcess address_of_process 	= new ADDROFProcess();
	private static final StateProcess field_of_process 		= new FIELDOFProcess();
	private static final StateProcess type_cast_process		= new CASTProcess();
	private static final StateProcess arith_add_process_l 	= new ADDLOPProcess();
	private static final StateProcess arith_add_process_r 	= new ADDROPProcess();
	private static final StateProcess arith_sub_process_l 	= new SUBLOPProcess();
	private static final StateProcess arith_sub_process_r 	= new SUBROPProcess();
	private static final StateProcess arith_mul_process_l 	= new MULLOPProcess();
	private static final StateProcess arith_mul_process_r 	= new MULROPProcess();
	private static final StateProcess arith_div_process_l 	= new DIVLOPProcess();
	private static final StateProcess arith_div_process_r 	= new DIVROPProcess();
	private static final StateProcess arith_mod_process_l 	= new MODLOPProcess();
	private static final StateProcess arith_mod_process_r 	= new MODROPProcess();
	private static final StateProcess bitws_and_process_l 	= new BANLOPProcess();
	private static final StateProcess bitws_and_process_r 	= new BANROPProcess();
	private static final StateProcess bitws_ior_process_l 	= new BORLOPProcess();
	private static final StateProcess bitws_ior_process_r 	= new BORROPProcess();
	private static final StateProcess bitws_xor_process_l 	= new BXRLOPProcess();
	private static final StateProcess bitws_xor_process_r 	= new BXRROPProcess();
	private static final StateProcess bitws_lsh_process_l 	= new LSHLOPProcess();
	private static final StateProcess bitws_lsh_process_r 	= new LSHROPProcess();
	private static final StateProcess bitws_rsh_process_l 	= new RSHLOPProcess();
	private static final StateProcess bitws_rsh_process_r 	= new RSHROPProcess();
	private static final StateProcess greater_tn_process_l 	= new GRTLOPProcess();
	private static final StateProcess greater_tn_process_r 	= new GRTROPProcess();
	private static final StateProcess greater_eq_process_l 	= new GRELOPProcess();
	private static final StateProcess greater_eq_process_r 	= new GREROPProcess();
	private static final StateProcess smaller_tn_process_l 	= new SMTLOPProcess();
	private static final StateProcess smaller_tn_process_r 	= new SMTROPProcess();
	private static final StateProcess smaller_eq_process_l 	= new SMELOPProcess();
	private static final StateProcess smaller_eq_process_r 	= new SMEROPProcess();
	private static final StateProcess equal_with_process_l 	= new EQVLOPProcess();
	private static final StateProcess equal_with_process_r 	= new EQVROPProcess();
	private static final StateProcess not_equals_process_l 	= new NEQLOPProcess();
	private static final StateProcess not_equals_process_r 	= new NEQROPProcess();
	private static final StateProcess arith_neg_process 	= new NEGProcess();
	private static final StateProcess logic_not_process 	= new NOTProcess();
	private static final StateProcess bitws_rsv_process 	= new RSVProcess();
	private static final StateProcess wait_expr_process 	= new WAITFORProcess();
	private static final StateProcess initial_body_process 	= new INITBODYProcess();
	private static final StateProcess condition_true_process = new CONDTProcess();
	private static final StateProcess condition_false_process = new CONDFProcess();
	private static final StateProcess rvalue_lvalue_process = new ASGRLProcess();
	private static final StateProcess define_usage_process 	= new DEFUSEProcess();
	
}
