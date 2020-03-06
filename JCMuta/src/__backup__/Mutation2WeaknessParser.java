package __backup__;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.expr.othr.AstFieldExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CType;

/**
 * Parse the strong mutation to weak mutation
 * @author yukimula
 */
public class Mutation2WeaknessParser {
	
	protected StringBuilder buffer;
	public Mutation2WeaknessParser() { buffer = new StringBuilder(); }
	
	/* main parser */
	/**
	 * Parse the strong mutation to the weak mutation 
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public TextMutation parse(TextMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			String replace;
			switch(mutation.get_operator()) {
			case STRP:	replace = this.parse_STRP(mutation);	break;
			case STRI:	replace = this.parse_STRI(mutation);	break;
			case STRC: 	replace = this.parse_STRC(mutation);	break;
			case SSDL:	replace = this.parse_SSDL(mutation);	break;
			case SBRC:	replace = this.parse_SBRC(mutation);	break;
			case SCRB:	replace = this.parse_SCRB(mutation);	break;
			case SWDD:	replace = this.parse_SWDD(mutation);	break;
			case SDWD:	replace = this.parse_SDWD(mutation);	break;
			case SSWM:	replace = this.parse_SSWM(mutation);	break;
			case SMTC:	replace = this.parse_SMTC(mutation);	break;
			
			case OPPO:	replace = this.parse_OPPO(mutation);	break;
			case OMMO:	replace = this.parse_OMMO(mutation);	break;
			case UIOI:	replace = this.parse_UIOI(mutation);	break;
			case OBNG:	replace = this.parse_OBNG(mutation);	break;
			case OCNG:	replace = this.parse_OCNG(mutation);	break;
			case OLNG:	replace = this.parse_OLNG(mutation);	break;
			case ONDU:	replace = this.parse_ONDU(mutation);	break;
			
			case OAAN:	replace = this.parse_OAAN(mutation);	break;
			case OABN:	replace = this.parse_OABN(mutation);	break;
			case OALN:	replace = this.parse_OALN(mutation);	break;
			case OARN:	replace = this.parse_OARN(mutation);	break;
			case OASN:	replace = this.parse_OASN(mutation);	break;
				
			case OBAN:	replace = this.parse_OBAN(mutation);	break;
			case OBBN:	replace = this.parse_OBBN(mutation);	break;
			case OBLN:	replace = this.parse_OBLN(mutation);	break;
			case OBRN:	replace = this.parse_OBRN(mutation);	break;
			case OBSN:	replace = this.parse_OBSN(mutation);	break;
				
			case OLAN:	replace = this.parse_OLAN(mutation);	break;
			case OLBN:	replace = this.parse_OLBN(mutation);	break;
			case OLLN:	replace = this.parse_OLLN(mutation);	break;
			case OLRN:	replace = this.parse_OLRN(mutation);	break;
			case OLSN:	replace = this.parse_OLSN(mutation);	break;
				
			case ORAN:	replace = this.parse_ORAN(mutation);	break;
			case ORBN:	replace = this.parse_ORBN(mutation);	break;
			case ORLN:	replace = this.parse_ORLN(mutation);	break;
			case ORRN:	replace = this.parse_ORRN(mutation);	break;
			case ORSN:	replace = this.parse_ORSN(mutation);	break;
				
			case OSAN:	replace = this.parse_OSAN(mutation);	break;
			case OSBN:	replace = this.parse_OSBN(mutation);	break;
			case OSLN:	replace = this.parse_OSLN(mutation);	break;
			case OSRN:	replace = this.parse_OSRN(mutation);	break;
			case OSSN:	replace = this.parse_OSSN(mutation);	break;
				
			case OEAA:	replace = this.parse_OEAA(mutation);	break;
			case OEBA:	replace = this.parse_OEBA(mutation);	break;
			case OESA:	replace = this.parse_OESA(mutation);	break;
				
			case OAAA:	replace = this.parse_OAAA(mutation);	break;
			case OABA:	replace = this.parse_OABA(mutation);	break;
			case OASA:	replace = this.parse_OASA(mutation);	break;
				
			case OBAA:	replace = this.parse_OBAA(mutation);	break;
			case OBBA:	replace = this.parse_OBBA(mutation);	break;
			case OBSA:	replace = this.parse_OBSA(mutation);	break;
				
			case OSAA:	replace = this.parse_OSAA(mutation);	break;
			case OSBA:	replace = this.parse_OSBA(mutation);	break;
			case OSSA:	replace = this.parse_OSSA(mutation);	break;
				
			case CCCR:	replace = this.parse_CCCR(mutation);	break;
			case CCSR:	replace = this.parse_CCSR(mutation);	break;
			case CRCR:	replace = this.parse_CRCR(mutation);	break;
				
			case VARR:	replace = this.parse_VARR(mutation);	break;
			case VPRR:	replace = this.parse_VPRR(mutation);	break;
			case VSRR:	replace = this.parse_VSRR(mutation);	break;
			case VSFR:	replace = this.parse_VSFR(mutation);	break;	
			case VTRR:	replace = this.parse_VTRR(mutation);	break;
				
			case VABS:	replace = this.parse_VABS(mutation);	break;
			case VBCR:	replace = this.parse_VBCR(mutation);	break;
			case VDTR:	replace = this.parse_VDTR(mutation);	break;
			case VTWD:	replace = this.parse_VTWD(mutation);	break;
				
			default: throw new IllegalArgumentException("Unsupported operator: " + mutation.get_operator());
			}
			
			return this.gen_mutation(mutation, replace);
		}
	}
	
	/* parser methods */
	// for statement mutation translation
	/**
	 * same as strong mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_STRP(TextMutation mutation) throws Exception {
		return mutation.get_replace();
	}
	/**
	 * same as strong mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_STRI(TextMutation mutation) throws Exception {
		return mutation.get_replace();
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_SSDL(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( );";
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_SBRC(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( );";
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_SCRB(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( );";
	}
	/**
	 * <code>
	 * 	while ( condition ) {		<br>
	 * 		_JCM_count_times(1);	<br>
	 * 		// Body as following	<br>
	 * 	}							<br>
	 * 	_JCM_trap_on_zero(
	 * 		_JCM_count_times(0)
	 * 	);<br>
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_SWDD(TextMutation mutation) throws Exception {
		AstWhileStatement stmt = (AstWhileStatement) mutation.get_origin();
		
		buffer.setLength(0);
		buffer.append("while ( ");
		buffer.append(stmt.get_condition().get_location().read());
		buffer.append(" ) {\n\t");
		buffer.append(MutaCode.Count_Loop_Times).append("(1);");
		buffer.append(stmt.get_body().get_location().read());
		buffer.append(" }\n");
		buffer.append(MutaCode.Trap_Zero).append("( ");
		buffer.append(MutaCode.Count_Loop_Times).append("(0) );");
		
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * 	<code>
	 * 		while ( condition ) {		<br>
	 * 			_JCM_count_times(1);	<br>
	 * 			// Body as following	<br>
	 * 		}							<br>
	 * 		_JCM_trap_on_zero(
	 * 		_JCM_count_times(0)
	 * 	);<br>
	 * 	</code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_SDWD(TextMutation mutation) throws Exception {
		AstDoWhileStatement stmt = (AstDoWhileStatement) mutation.get_origin();
		
		buffer.setLength(0);
		buffer.append("while ( ");
		buffer.append(stmt.get_condition().get_location().read());
		buffer.append(" ) {\n\t");
		buffer.append(MutaCode.Count_Loop_Times).append("(1);");
		buffer.append(stmt.get_body().get_location().read());
		buffer.append(" }\n");
		buffer.append(MutaCode.Trap_Zero).append("( ");
		buffer.append(MutaCode.Count_Loop_Times).append("(0) );");
		
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * same as strong mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_SSWM(TextMutation mutation) throws Exception {
		return mutation.get_replace();
	}
	/**
	 * same as strong mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_SMTC(TextMutation mutation) throws Exception {
		return mutation.get_replace();
	}
	/**
	 * same as strong mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_STRC(TextMutation mutation) throws Exception {
		return mutation.get_replace();
	}
	
	// for unary expression mutation
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OPPO(TextMutation mutation) throws Exception {
		String expr = mutation.get_origin().get_location().read();
		if(JC_Classifier.is_left_operand((AstExpression) mutation.get_origin())) {
			return "( *( " + MutaCode.Trap_Statement + "(), " + "&(" + expr + ") ) )";
		}
		else {
			return "( " + MutaCode.Trap_Statement + "(), " + "(" + expr + ") )";
		}
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OMMO(TextMutation mutation) throws Exception {
		String expr = mutation.get_origin().get_location().read();
		if(JC_Classifier.is_left_operand((AstExpression) mutation.get_origin())) {
			return "( *( " + MutaCode.Trap_Statement + "(), " + "&(" + expr + ") ) )";
		}
		else {
			return "( " + MutaCode.Trap_Statement + "(), " + "(" + expr + ") )";
		}
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_UIOI(TextMutation mutation) throws Exception {
		String expr = mutation.get_origin().get_location().read();
		if(JC_Classifier.is_left_operand((AstExpression) mutation.get_origin())) {
			return "( *( " + MutaCode.Trap_Statement + "(), " + "&(" + expr + ") ) )";
		}
		else {
			return "( " + MutaCode.Trap_Statement + "(), " + "(" + expr + ") )";
		}
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBNG(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( )";
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OCNG(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( )";
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OLNG(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( )";
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_ONDU(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( )";
	}
	
	// for binary expression mutation
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_add_type, _JCM_sub_type);
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OAAN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else if(JC_Classifier.is_address_type(type)) 
			throw new IllegalArgumentException("Unsupported type: " + type);
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		// arithmetic addition to others
		case ADD_SUB:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case ADD_MUL:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case ADD_DIV:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case ADD_MOD:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		// arithmetic subtraction to others
		case SUB_ADD:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case SUB_MUL:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case SUB_DIV:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case SUB_MOD:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		// arithmetic multiplication to others
		case MUL_ADD:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case MUL_SUB:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case MUL_DIV:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case MUL_MOD:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		// arithmetic division to others
		case DIV_ADD:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case DIV_SUB:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case DIV_MUL:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case DIV_MOD:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		// arithmetic MOD to others
		case MOD_ADD:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case MOD_SUB:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case MOD_MUL:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case MOD_DIV:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		// not OAAN operator mode
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_add_type, _JCM_bor_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OABN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else if(JC_Classifier.is_address_type(type)) 
			throw new IllegalArgumentException("Unsupported type: " + type);
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		// arithmetic addition 
		case ADD_BAN:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case ADD_BOR:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case ADD_BXR:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		// arithmetic subtract
		case SUB_BAN:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case SUB_BOR:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case SUB_BXR:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		// arithmetic multiplication
		case MUL_BAN:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case MUL_BOR:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case MUL_BXR:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		// arithmetic division
		case DIV_BAN:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case DIV_BOR:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case DIV_BXR:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		// arithmetic mod
		case MOD_BAN:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case MOD_BOR:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case MOD_BXR:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		// not OARN operator mode
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_add_type, _JCM_lor_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OALN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else if(JC_Classifier.is_address_type(type)) 
			throw new IllegalArgumentException("Unsupported type: " + type);
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		// addition
		case ADD_LAN:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case ADD_LOR:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		// subtract 
		case SUB_LAN:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case SUB_LOR:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		// multiply
		case MUL_LAN:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case MUL_LOR:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		// division
		case DIV_LAN:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case DIV_LOR:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		// MOD
		case MOD_LAN:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case MOD_LOR:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		// invalid mutation mode
		default: throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_add_type, _JCM_grt_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OARN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else if(JC_Classifier.is_address_type(type)) 
			throw new IllegalArgumentException("Unsupported type: " + type);
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		// addition
		case ADD_EQV:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case ADD_NEQ:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case ADD_GRT:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case ADD_GRE:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case ADD_SMT:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case ADD_SME:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		// subtract
		case SUB_EQV:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case SUB_NEQ:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case SUB_GRT:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case SUB_GRE:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case SUB_SMT:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case SUB_SME:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		// multiply
		case MUL_EQV:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case MUL_NEQ:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case MUL_GRT:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case MUL_GRE:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case MUL_SMT:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case MUL_SME:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		// division
		case DIV_EQV:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case DIV_NEQ:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case DIV_GRT:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case DIV_GRE:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case DIV_SMT:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case DIV_SME:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		// MOD
		case MOD_EQV:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case MOD_NEQ:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case MOD_GRT:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case MOD_GRE:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case MOD_SMT:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case MOD_SME:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		// invalid mutation operator
		default: throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_add_type, _JCM_lsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OASN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else if(JC_Classifier.is_address_type(type)) 
			throw new IllegalArgumentException("Unsupported type: " + type);
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		// addition
		case ADD_LSH:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case ADD_RSH:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		// subtract
		case SUB_LSH:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case SUB_RSH:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		// multiply
		case MUL_LSH:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case MUL_RSH:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		// division
		case DIV_LSH:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case DIV_RSH:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		// MOD
		case MOD_LSH:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case MOD_RSH:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		// otherwise
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_ban_type, _JCM_mul_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBAN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		// &
		case BAN_ADD:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case BAN_SUB:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case BAN_MUL:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case BAN_DIV:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case BAN_MOD:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		// |
		case BOR_ADD:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case BOR_SUB:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case BOR_MUL:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case BOR_DIV:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case BOR_MOD:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		// ^
		case BXR_ADD:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case BXR_SUB:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case BXR_MUL:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case BXR_DIV:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case BXR_MOD:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		// otherwise
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_ban_type, _JCM_bor_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBBN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case BAN_BOR:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case BAN_BXR:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case BOR_BAN:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case BOR_BXR:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case BXR_BAN:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case BXR_BOR:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_ban_type, _JCM_lan_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBLN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case BAN_LAN:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case BAN_LOR:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case BOR_LAN:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Logic_And_Prefix;	break;
		case BOR_LOR:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case BXR_LAN:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case BXR_LOR:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_ban_type, _JCM_grt_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBRN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case BAN_EQV:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case BAN_NEQ:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case BAN_GRT:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case BAN_GRE:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case BAN_SMT:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case BAN_SME:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case BOR_EQV:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case BOR_NEQ:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case BOR_GRT:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case BOR_GRE:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case BOR_SMT:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case BOR_SME:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case BXR_EQV:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case BXR_NEQ:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case BXR_GRT:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case BXR_GRE:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case BXR_SMT:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case BXR_SME:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_ban_type, _JCM_rsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBSN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case BAN_LSH:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case BAN_RSH:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case BOR_LSH:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case BOR_RSH:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case BXR_LSH:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case BXR_RSH:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lan_type, _JCM_mod_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OLAN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LAN_ADD:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case LAN_SUB:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case LAN_MUL:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case LAN_DIV:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case LAN_MOD:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case LOR_ADD:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case LOR_SUB:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case LOR_MUL:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case LOR_DIV:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case LOR_MOD:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lan_type, _JCM_ban_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OLBN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LAN_BAN:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case LAN_BOR:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case LAN_BXR:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case LOR_BAN:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case LOR_BOR:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case LOR_BXR:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lan_type, _JCM_lor_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OLLN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LAN_LOR:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		case LOR_LAN:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lan_type, _JCM_sme_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OLRN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LAN_EQV:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case LAN_NEQ:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case LAN_GRT:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case LAN_GRE:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case LAN_SMT:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case LAN_SME:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case LOR_EQV:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case LOR_NEQ:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case LOR_GRT:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case LOR_GRE:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case LOR_SMT:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case LOR_SME:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lan_type, _JCM_rsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OLSN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LAN_LSH:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case LAN_RSH:	orig_operator = MutaCode.Logic_And_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case LOR_LSH:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case LOR_RSH:	orig_operator = MutaCode.Logic_Or_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_eqv_type, _JCM_add_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_ORAN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type + "\n\t" + expr.get_location().read());
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case EQV_ADD:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case EQV_SUB:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case EQV_MUL:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case EQV_DIV:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case EQV_MOD:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case NEQ_ADD:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case NEQ_SUB:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case NEQ_MUL:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case NEQ_DIV:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case NEQ_MOD:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case GRT_ADD:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case GRT_SUB:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case GRT_MUL:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case GRT_DIV:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case GRT_MOD:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case GRE_ADD:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case GRE_SUB:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case GRE_MUL:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case GRE_DIV:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case GRE_MOD:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case SMT_ADD:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case SMT_SUB:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case SMT_MUL:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case SMT_DIV:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case SMT_MOD:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
	
		case SME_ADD:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case SME_SUB:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case SME_MUL:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case SME_DIV:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case SME_MOD:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_eqv_type, _JCM_bxr_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_ORBN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case EQV_BAN:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case EQV_BOR:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case EQV_BXR:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case NEQ_BAN:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case NEQ_BOR:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case NEQ_BXR:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case GRT_BAN:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case GRT_BOR:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case GRT_BXR:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case GRE_BAN:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case GRE_BOR:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case GRE_BXR:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case SMT_BAN:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case SMT_BOR:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case SMT_BXR:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case SME_BAN:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case SME_BOR:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case SME_BXR:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_eqv_type, _JCM_lan_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_ORLN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else if(JC_Classifier.is_address_type(type)) {
			operand_type = MutaCode.Type_Pointers;	result_type = MutaCode.Type_void_ptr;
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case EQV_LAN:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case EQV_LOR:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case NEQ_LAN:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case NEQ_LOR:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case GRT_LAN:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case GRT_LOR:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case GRE_LAN:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case GRE_LOR:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case SMT_LAN:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case SMT_LOR:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case SME_LAN:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case SME_LOR:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_eqv_type, _JCM_sme_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_ORRN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else if(JC_Classifier.is_address_type(type)) {
			operand_type = MutaCode.Type_Pointers;	result_type = MutaCode.Type_void_ptr;
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case EQV_NEQ:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case EQV_GRT:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case EQV_GRE:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case EQV_SMT:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case EQV_SME:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case NEQ_EQV:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case NEQ_GRT:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case NEQ_GRE:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case NEQ_SMT:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case NEQ_SME:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case GRT_EQV:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case GRT_NEQ:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case GRT_GRE:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case GRT_SMT:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case GRT_SME:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case GRE_EQV:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case GRE_NEQ:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case GRE_GRT:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case GRE_SMT:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case GRE_SME:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case SMT_EQV:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case SMT_NEQ:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case SMT_GRT:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case SMT_GRE:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case SMT_SME:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case SME_EQV:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case SME_NEQ:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case SME_GRT:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case SME_GRE:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case SME_SMT:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_eqv_type, _JCM_rsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_ORSN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		if(!JC_Classifier.is_real_type(type))
			type = JC_Classifier.get_value_type(E2.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case EQV_LSH:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case EQV_RSH:	orig_operator = MutaCode.Relation_Eqv_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case NEQ_LSH:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case NEQ_RSH:	orig_operator = MutaCode.Relation_Neq_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case GRT_LSH:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case GRT_RSH:	orig_operator = MutaCode.Relation_Grt_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
	
		case GRE_LSH:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case GRE_RSH:	orig_operator = MutaCode.Relation_Gre_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case SMT_LSH:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case SMT_RSH:	orig_operator = MutaCode.Relation_Smt_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case SME_LSH:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case SME_RSH:	orig_operator = MutaCode.Relation_Sme_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lsh_type, _JCM_add_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSAN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_ADD:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case LSH_SUB:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case LSH_MUL:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case LSH_DIV:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case LSH_MOD:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case RSH_ADD:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case RSH_SUB:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case RSH_MUL:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case RSH_DIV:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case RSH_MOD:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lsh_type, _JCM_ban_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSBN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_BAN:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix; break;
		case LSH_BOR:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;	 break;
		case LSH_BXR:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix; break;
		
		case RSH_BAN:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix; break;
		case RSH_BOR:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;	 break;
		case RSH_BXR:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix; break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lsh_type, _JCM_lan_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSLN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_LAN:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case LSH_LOR:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		case RSH_LAN:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Logic_And_Prefix;	break;
		case RSH_LOR:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Logic_Or_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lsh_type, _JCM_grt_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSRN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_EQV:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case LSH_NEQ:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case LSH_GRT:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case LSH_GRE:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case LSH_SMT:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case LSH_SME:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		case RSH_EQV:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Relation_Eqv_Prefix;	break;
		case RSH_NEQ:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Relation_Neq_Prefix;	break;
		case RSH_GRT:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Relation_Grt_Prefix;	break;
		case RSH_GRE:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Relation_Gre_Prefix;	break;
		case RSH_SMT:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Relation_Smt_Prefix;	break;
		case RSH_SME:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Relation_Sme_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_value_type(x, y, _JCM_lsh_type, _JCM_lsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSSN(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type;
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_RSH:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		case RSH_LSH:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* generate mutation code segment */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(loperand).append(", ").append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	
	// for assignment mutation
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_arg_type, _JCM_sub_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OEAA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	cast_type = "float"; 				break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; cast_type = "double"; 				break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;cast_type = "long double"; 			break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case ASG_ADD:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case ASG_SUB:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case ASG_MUL:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case ASG_DIV:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case ASG_MOD:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_arg_type, _JCM_ban_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OEBA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case ASG_BAN:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case ASG_BOR:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case ASG_BXR:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode()); 
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_arg_type, _JCM_ban_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OESA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	cast_type = "float"; 				break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; cast_type = "double"; 				break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;cast_type = "long double"; 			break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case ASG_LSH:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case ASG_RSH:	orig_operator = MutaCode.Argument_1_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode()); 
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_add_type, _JCM_sub_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OAAA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	cast_type = "float"; 				break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; cast_type = "double"; 				break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;cast_type = "long double"; 			break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case ADD_SUB_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case ADD_MUL_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case ADD_DIV_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case ADD_MOD_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case SUB_ADD_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case SUB_MUL_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case SUB_DIV_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case SUB_MOD_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case MUL_ADD_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case MUL_SUB_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case MUL_DIV_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case MUL_MOD_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case DIV_ADD_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case DIV_SUB_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case DIV_MUL_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case DIV_MOD_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case MOD_ADD_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case MOD_SUB_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case MOD_MUL_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case MOD_DIV_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_add_type, _JCM_ban_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OABA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case ADD_BAN_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case ADD_BOR_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case ADD_BXR_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case SUB_BAN_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case SUB_BOR_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case SUB_BXR_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case MUL_BAN_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case MUL_BOR_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case MUL_BXR_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case DIV_BAN_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case DIV_BOR_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case DIV_BXR_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case MOD_BAN_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case MOD_BOR_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case MOD_BXR_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_add_type, _JCM_lsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OASA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case ADD_LSH_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case ADD_RSH_A:	orig_operator = MutaCode.Arith_Add_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case SUB_LSH_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case SUB_RSH_A:	orig_operator = MutaCode.Arith_Sub_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case MUL_LSH_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case MUL_RSH_A:	orig_operator = MutaCode.Arith_Mul_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case DIV_LSH_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case DIV_RSH_A:	orig_operator = MutaCode.Arith_Div_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case MOD_LSH_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case MOD_RSH_A:	orig_operator = MutaCode.Arith_Mod_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_ban_type, _JCM_sub_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBAA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case BAN_ADD_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case BAN_SUB_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case BAN_MUL_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case BAN_DIV_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case BAN_MOD_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case BOR_ADD_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case BOR_SUB_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case BOR_MUL_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case BOR_DIV_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case BOR_MOD_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case BXR_ADD_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case BXR_SUB_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case BXR_MUL_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case BXR_DIV_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case BXR_MOD_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_ban_type, _JCM_bor_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBBA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case BAN_BOR_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case BAN_BXR_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case BOR_BAN_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case BOR_BXR_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case BXR_BAN_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case BXR_BOR_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_ban_type, _JCM_rsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OBSA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case BAN_LSH_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case BAN_RSH_A:	orig_operator = MutaCode.Bitwise_And_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case BOR_LSH_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case BOR_RSH_A:	orig_operator = MutaCode.Bitwise_Or_Prefix;		muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		case BXR_LSH_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		case BXR_RSH_A:	orig_operator = MutaCode.Bitwise_Xor_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_lsh_type, _JCM_add_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSAA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_ADD_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case LSH_SUB_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case LSH_MUL_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case LSH_DIV_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case LSH_MOD_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		case RSH_ADD_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Add_Prefix;	break;
		case RSH_SUB_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Sub_Prefix;	break;
		case RSH_MUL_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Mul_Prefix;	break;
		case RSH_DIV_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Div_Prefix;	break;
		case RSH_MOD_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Arith_Mod_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_lsh_type, _JCM_ban_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSBA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_BAN_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case LSH_BOR_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case LSH_BXR_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		case RSH_BAN_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_And_Prefix;	break;
		case RSH_BOR_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_Or_Prefix;		break;
		case RSH_BXR_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_Xor_Prefix;	break;
		
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assign_type( (type *)(&(E1)), (E2), _JCM_lsh_type, _JCM_rsh_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_OSSA(TextMutation mutation) throws Exception {
		/* declarations */
		AstBinaryExpression expr = (AstBinaryExpression) mutation.get_origin();
		AstExpression E1 = expr.get_loperand(), E2 = expr.get_roperand();
		String loperand = "( " + E1.get_location().read() + " )";
		String roperand = "( " + E2.get_location().read() + " )";
		String orig_operator, muta_operator, operand_type, result_type, cast_type;
		CType type = JC_Classifier.get_value_type(E1.get_value_type());
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	cast_type = "char"; 				break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	cast_type = "unsigned char"; 		break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	cast_type = "short"; 				break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; cast_type = "unsigned short"; 		break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	cast_type = "int"; 					break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint; 	cast_type = "unsigned int"; 		break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	cast_type = "long"; 				break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	cast_type = "unsigned long"; 		break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	cast_type = "long long"; 			break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; cast_type = "unsigned long long"; 	break;
			default: throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Invalid type: " + type);
		
		/* operator determination */
		switch(mutation.get_mode()) {
		case LSH_RSH_A:	orig_operator = MutaCode.Bitwise_Lsh_Prefix;	muta_operator = MutaCode.Bitwise_Rsh_Prefix;	break;
		case RSH_LSH_A:	orig_operator = MutaCode.Bitwise_Rsh_Prefix;	muta_operator = MutaCode.Bitwise_Lsh_Prefix;	break;
		default:		throw new IllegalArgumentException("Unsupported mutation mode: " + mutation.get_mode());
		}
		
		/* mutation code generation */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Assign_Prefix).append(result_type);
		buffer.append("( ");
		buffer.append("( (").append(cast_type).append("*) ").append(loperand).append(" ), ");
		buffer.append(roperand).append(", ");
		buffer.append(orig_operator).append(operand_type).append(", ");
		buffer.append(muta_operator).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	
	// constant mutation
	/**
	 * same as coverage
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_CCCR(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( )";
	}
	/**
	 * same as coverage
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_CRCR(TextMutation mutation) throws Exception {
		return MutaCode.Trap_Statement + "( )";
	}
	/**
	 * <code>
	 * 	_JCM_value_type(X, val, _JCM_larg_type, _JCM_rarg_type)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_CCSR(TextMutation mutation) throws Exception {
		AstExpression expr = (AstExpression) mutation.get_origin();
		CType type = JC_Classifier.get_value_type(expr.get_value_type());
		String variable = "( " + expr.get_location().read() + " )";
		String constant = "( " + mutation.get_replace() + " )";
		String operand_type, result_type;
		
		/* type determination */
		if(type instanceof CBasicType) {
			switch(((CBasicType) type).get_tag()) {
			case c_bool:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_char:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Char; 	break;
			case c_uchar:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uchar; 	break;
			case c_short:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Short; 	break;
			case c_ushort:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ushort; break;
			case c_int:		operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Int; 	break;
			case c_uint:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Uint;	break;
			case c_long:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Long; 	break;
			case c_ulong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ulong; 	break;
			case c_llong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Llong; 	break;
			case c_ullong:	operand_type = MutaCode.Type_Integers;	result_type = MutaCode.Type_Ullong; break;
			case c_float:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Float; 	break;
			case c_double:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Double; break;
			case c_ldouble:	operand_type = MutaCode.Type_Reals;		result_type = MutaCode.Type_Ldouble;break;
			default: 		throw new IllegalArgumentException("Unsupported type: " + type);
			}
		}
		else throw new IllegalArgumentException("Unsupported type: " + type);
		
		/* generate mutation code */
		buffer.setLength(0);
		buffer.append(MutaCode.Value_Getter_Prefix).append(result_type);
		buffer.append("( ").append(variable).append(", ").append(constant).append(", ");
		buffer.append(MutaCode.Argument_1_Prefix).append(operand_type).append(", ");
		buffer.append(MutaCode.Argument_2_Prefix).append(operand_type).append(" )");
		
		/* return */
		String text = buffer.toString(); buffer.setLength(0); return text;
	}
	
	// reference mutation
	/**
	 * <code>
	 * 	_JCM_assert_objects(x, y)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VARR(TextMutation mutation) throws Exception {
		AstExpression E = (AstExpression) mutation.get_origin();
		String A = E.get_location().read(), B = mutation.get_replace();
		return MutaCode.Equal_Object + "( (" + A + "), (" + B + ") )";
	}
	/**
	 * <code>
	 * 	_JCM_assert_objects(x, y)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VPRR(TextMutation mutation) throws Exception {
		AstExpression E = (AstExpression) mutation.get_origin();
		String A = E.get_location().read(), B = mutation.get_replace();
		return MutaCode.Equal_Object + "( (" + A + "), (" + B + ") )";
	}
	/**
	 * <code>
	 * 	_JCM_assert_objects(x, y)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VSRR(TextMutation mutation) throws Exception {
		AstExpression E = (AstExpression) mutation.get_origin();
		String A = E.get_location().read(), B = mutation.get_replace();
		return MutaCode.Equal_Object + "( (" + A + "), (" + B + ") )";
	}
	/**
	 * <code>
	 * 	_JCM_assert_objects(X.f1, X.f2)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VSFR(TextMutation mutation) throws Exception {
		/* declarations */
		AstFieldExpression expr = (AstFieldExpression) 
					mutation.get_origin().get_parent();
		String body = expr.get_body().get_location().read();
		String oprt = expr.get_operator().get_location().read();
		String F1 = expr.get_field().get_name();
		String F2 = mutation.get_replace();
		
		/* mutation code */
		buffer.setLength(0);
		buffer.append(MutaCode.Equal_Object).append("( (");
		buffer.append(body).append(oprt).append(F1);
		buffer.append("), (");
		buffer.append(body).append(oprt).append(F2);
		buffer.append(") )");
		
		/* return */
		String text = buffer.toString();
		buffer.setLength(0); return text;
	}
	/**
	 * <code>
	 * 	_JCM_assert_objects(x, y)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VTRR(TextMutation mutation) throws Exception {
		AstExpression E = (AstExpression) mutation.get_origin();
		String A = E.get_location().read(), B = mutation.get_replace();
		return MutaCode.Equal_Object + "( (" + A + "), (" + B + ") )";
	}
	
	// special mutation
	/**
	 * <code>
	 * 	trap_on_negative(x)
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VABS(TextMutation mutation) throws Exception {
		AstExpression expr = (AstExpression) mutation.get_origin();
		String E = expr.get_location().read();
		return MutaCode.Trap_Negative + "( " + E + " )";
	}
	/**
	 * <code>
	 * 	trap_on_true( E )
	 * </code>
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VBCR(TextMutation mutation) throws Exception {
		String expr = mutation.get_origin().get_location().read();
		
		String function;
		switch(mutation.get_mode()) {
		case MUT_TRUE:	function = MutaCode.Trap_False;	break;
		case MUT_FALSE:	function = MutaCode.Trap_True;	break;
		default: throw new IllegalArgumentException("Invalid mutation: " + mutation.get_mode());
		}
		
		return function + "( " + expr + " )";
	}
	/**
	 * same as strong mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VDTR(TextMutation mutation) throws Exception {
		return mutation.get_replace();
	}
	/**
	 * same as coverage mutation
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	protected String parse_VTWD(TextMutation mutation) throws Exception {
		return "( " + MutaCode.Trap_Statement + "(), " + mutation.get_replace() + " )";
	}
	
	// avoid case expression 
	protected TextMutation gen_mutation(TextMutation mutation, String replace) throws Exception {
		/* modified for switch-case expression seeding point */
		AstNode origin = mutation.get_origin();
		if(origin instanceof AstField) {
			origin = origin.get_parent();
		}
		
		/* generate the weak mutation */
		if(mutation instanceof ContextMutation) {
			return ContextMutation.produce(
					mutation.get_operator(), 
					mutation.get_mode(), 
					origin, replace, 
					((ContextMutation) mutation).get_callee(), 
					((ContextMutation) mutation).get_muta_function());
		}
		else {
			return TextMutation.produce(
					mutation.get_operator(), 
					mutation.get_mode(), 
					origin, replace);
		}
		
	}
}
