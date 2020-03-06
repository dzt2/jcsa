package __backup__;

/**
 * Code base for mutation in JC-Muta
 * @author yukimula
 */
public class MutaCode {
	// trap function names 
	public static final String Trap_Statement 	= "_JCM_trap_on_statement";
	public static final String Trap_True 		= "_JCM_trap_on_true";
	public static final String Trap_False		= "_JCM_trap_on_false";
	public static final String Trap_Case		= "_JCM_trap_on_case";
	public static final String Trap_Times		= "_JCM_trap_on_times";
	// special functions for trap and VTWD
	public static final String Abs_Value 		= "_JCM_mut_abs";
	public static final String True_Value		= "_JCM_true_value";
	public static final String False_Value		= "_JCM_false_value";
	public static final String Trap_Positive	= "_JCM_trap_on_positive";
	public static final String Trap_Zero		= "_JCM_trap_on_zero";
	public static final String Trap_Nzero		= "_JCM_trap_on_nzero";
	public static final String Trap_Negative	= "_JCM_trap_on_negative";
	public static final String Succ_Value		= "_JCM_succ";
	public static final String Pred_Value 		= "_JCM_pred";
	// tokens for generated mutation code
	public static final String Muta_Prefix 		= "_JC_MUTA_";
	public static final String Muta_Header		= "__jcmuta__.h";
	public static final String Muta_Inform		= 
			"\n/*\n"
			+ " *\t Mutant-ID: \t%d\n"
			+ " *\t Operator: \t%s\n"
			+ " *\t Change-Mod: \t%s\n"
			+ " */\n\n";
	// loop times counter
	public static final String Count_Loop_Times 	= "_JCM_count_times";
	// argument method token
	public static final String Argument_1_Prefix	= "_JCM_larg_";
	public static final String Argument_2_Prefix 	= "_JCM_rarg_";
	// computational method token
	public static final String Arith_Add_Prefix 	= "_JCM_add_";
	public static final String Arith_Sub_Prefix 	= "_JCM_sub_";
	public static final String Arith_Mul_Prefix 	= "_JCM_mul_";
	public static final String Arith_Div_Prefix 	= "_JCM_div_";
	public static final String Arith_Mod_Prefix 	= "_JCM_mod_";
	public static final String Bitwise_And_Prefix 	= "_JCM_ban_";
	public static final String Bitwise_Or_Prefix 	= "_JCM_bor_";
	public static final String Bitwise_Xor_Prefix 	= "_JCM_bxr_";
	public static final String Bitwise_Lsh_Prefix 	= "_JCM_lsh_";
	public static final String Bitwise_Rsh_Prefix 	= "_JCM_rsh_";
	public static final String Logic_And_Prefix 	= "_JCM_lan_";
	public static final String Logic_Or_Prefix  	= "_JCM_lor_";
	public static final String Relation_Eqv_Prefix	= "_JCM_eqv_";
	public static final String Relation_Neq_Prefix	= "_JCM_neq_";
	public static final String Relation_Grt_Prefix	= "_JCM_grt_";
	public static final String Relation_Gre_Prefix	= "_JCM_gre_";
	public static final String Relation_Smt_Prefix	= "_JCM_smt_";
	public static final String Relation_Sme_Prefix	= "_JCM_sme_";
	// result derive method token
	public static final String Value_Getter_Prefix	= "_JCM_value_";
	public static final String Value_Assign_Prefix 	= "_JCM_assign_";
	// type postfix token
	public static final String Type_Integers		= "integers";
	public static final String Type_Reals			= "reals";
	public static final String Type_Pointers 		= "pointers";
	public static final String Type_Char			= "char";
	public static final String Type_Uchar			= "uchar";
	public static final String Type_Short			= "short";
	public static final String Type_Ushort			= "ushort";
	public static final String Type_Int				= "int";
	public static final String Type_Uint			= "uint";
	public static final String Type_Long			= "long";
	public static final String Type_Ulong			= "ulong";
	public static final String Type_Llong			= "llong";
	public static final String Type_Ullong			= "ullong";
	public static final String Type_Float			= "float";
	public static final String Type_Double			= "double";
	public static final String Type_Ldouble			= "ldouble";
	public static final String Type_void_ptr		= "pointers";
	// assert method token
	public static final String Equal_Object			= "_JCM_assert_objects";
	
}
