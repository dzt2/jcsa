package __backup__;

/**
 * A mutant is a mutated code to a source file
 * of the given mutant space
 * @author yukimula
 */
public class Mutant {
	
	protected MutantSpace space;
	protected int mutant_id;
	protected TextMutation mutation;
	
	/**
	 * constructor
	 * @param mutation
	 * @throws Exception
	 */
	protected Mutant(MutantSpace space, int id, TextMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(space == null)
			throw new IllegalArgumentException("Invalid space: null");
		else { this.space = space; this.mutant_id = id; this.mutation = mutation; }
	}
	
	/* getters */
	/**
	 * get the mutation for text code in the mutant
	 * @return
	 */
	public TextMutation get_mutation() {return mutation;}
	/**
	 * get the space where mutant is defined
	 * @return
	 */
	public MutantSpace get_space() {return space;}
	/**
	 * get the id of this mutant in space
	 * @return
	 */
	public int get_mutant_id() {return mutant_id;}

	/* reversed methods */
	/**
	 * parse the operator name to corresponding operator
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static MutOperator get_operator_by(String text) throws Exception {
		if(text == null) throw new IllegalArgumentException("invalid text: null");
		/* statement mutation */
		else if(MutOperator.STRP.toString().equals(text)) return MutOperator.STRP;
		else if(MutOperator.STRI.toString().equals(text)) return MutOperator.STRI;
		else if(MutOperator.STRC.toString().equals(text)) return MutOperator.STRC;
		else if(MutOperator.SSDL.toString().equals(text)) return MutOperator.SSDL;
		else if(MutOperator.SDWD.toString().equals(text)) return MutOperator.SDWD;
		else if(MutOperator.SWDD.toString().equals(text)) return MutOperator.SWDD;
		else if(MutOperator.SBRC.toString().equals(text)) return MutOperator.SBRC;
		else if(MutOperator.SCRB.toString().equals(text)) return MutOperator.SCRB;
		else if(MutOperator.SSWM.toString().equals(text)) return MutOperator.SSWM;
		else if(MutOperator.SMTC.toString().equals(text)) return MutOperator.SMTC;
		/* unary operator */
		else if(MutOperator.OPPO.toString().equals(text)) return MutOperator.OPPO;
		else if(MutOperator.OMMO.toString().equals(text)) return MutOperator.OMMO;
		else if(MutOperator.UIOI.toString().equals(text)) return MutOperator.UIOI;
		else if(MutOperator.OBNG.toString().equals(text)) return MutOperator.OBNG;
		else if(MutOperator.OCNG.toString().equals(text)) return MutOperator.OCNG;
		else if(MutOperator.OLNG.toString().equals(text)) return MutOperator.OLNG;
		else if(MutOperator.ONDU.toString().equals(text)) return MutOperator.ONDU;
		/* binary operator */
		else if(MutOperator.OAAN.toString().equals(text)) return MutOperator.OAAN;
		else if(MutOperator.OABN.toString().equals(text)) return MutOperator.OABN;
		else if(MutOperator.OALN.toString().equals(text)) return MutOperator.OALN;
		else if(MutOperator.OARN.toString().equals(text)) return MutOperator.OARN;
		else if(MutOperator.OASN.toString().equals(text)) return MutOperator.OASN;
		else if(MutOperator.OBAN.toString().equals(text)) return MutOperator.OBAN;
		else if(MutOperator.OBBN.toString().equals(text)) return MutOperator.OBBN;
		else if(MutOperator.OBLN.toString().equals(text)) return MutOperator.OBLN;
		else if(MutOperator.OBRN.toString().equals(text)) return MutOperator.OBRN;
		else if(MutOperator.OBSN.toString().equals(text)) return MutOperator.OBSN;
		else if(MutOperator.OLAN.toString().equals(text)) return MutOperator.OLAN;
		else if(MutOperator.OLBN.toString().equals(text)) return MutOperator.OLBN;
		else if(MutOperator.OLLN.toString().equals(text)) return MutOperator.OLLN;
		else if(MutOperator.OLRN.toString().equals(text)) return MutOperator.OLRN;
		else if(MutOperator.OLSN.toString().equals(text)) return MutOperator.OLSN;
		else if(MutOperator.ORAN.toString().equals(text)) return MutOperator.ORAN;
		else if(MutOperator.ORBN.toString().equals(text)) return MutOperator.ORBN;
		else if(MutOperator.ORLN.toString().equals(text)) return MutOperator.ORLN;
		else if(MutOperator.ORRN.toString().equals(text)) return MutOperator.ORRN;
		else if(MutOperator.ORSN.toString().equals(text)) return MutOperator.ORSN;
		else if(MutOperator.OSAN.toString().equals(text)) return MutOperator.OSAN;
		else if(MutOperator.OSBN.toString().equals(text)) return MutOperator.OSBN;
		else if(MutOperator.OSLN.toString().equals(text)) return MutOperator.OSLN;
		else if(MutOperator.OSRN.toString().equals(text)) return MutOperator.OSRN;
		else if(MutOperator.OSSN.toString().equals(text)) return MutOperator.OSSN;
		else if(MutOperator.OEAA.toString().equals(text)) return MutOperator.OEAA;
		else if(MutOperator.OEBA.toString().equals(text)) return MutOperator.OEBA;
		else if(MutOperator.OESA.toString().equals(text)) return MutOperator.OESA;
		else if(MutOperator.OAAA.toString().equals(text)) return MutOperator.OAAA;
		else if(MutOperator.OABA.toString().equals(text)) return MutOperator.OABA;
		else if(MutOperator.OASA.toString().equals(text)) return MutOperator.OASA;
		else if(MutOperator.OBAA.toString().equals(text)) return MutOperator.OBAA;
		else if(MutOperator.OBBA.toString().equals(text)) return MutOperator.OBBA;
		else if(MutOperator.OBSA.toString().equals(text)) return MutOperator.OBSA;
		else if(MutOperator.OSAA.toString().equals(text)) return MutOperator.OSAA;
		else if(MutOperator.OSBA.toString().equals(text)) return MutOperator.OSBA;
		else if(MutOperator.OSSA.toString().equals(text)) return MutOperator.OSSA;
		/* value mutations */
		else if(MutOperator.VABS.toString().equals(text)) return MutOperator.VABS;
		else if(MutOperator.VBCR.toString().equals(text)) return MutOperator.VBCR;
		else if(MutOperator.VDTR.toString().equals(text)) return MutOperator.VDTR;
		else if(MutOperator.VTWD.toString().equals(text)) return MutOperator.VTWD;
		/* constant mutations */
		else if(MutOperator.CCCR.toString().equals(text)) return MutOperator.CCCR;
		else if(MutOperator.CCSR.toString().equals(text)) return MutOperator.CCSR;
		else if(MutOperator.CRCR.toString().equals(text)) return MutOperator.CRCR;
		/* reference mutations */
		else if(MutOperator.VARR.toString().equals(text)) return MutOperator.VARR;
		else if(MutOperator.VPRR.toString().equals(text)) return MutOperator.VPRR;
		else if(MutOperator.VSRR.toString().equals(text)) return MutOperator.VSRR;
		else if(MutOperator.VTRR.toString().equals(text)) return MutOperator.VTRR;
		else if(MutOperator.VSFR.toString().equals(text)) return MutOperator.VSFR;
		/* unsupported case */
		else throw new IllegalArgumentException("Unsupported operator: " + text);
	}
	/**
	 * parse the mode text to mutation mode
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public static MutationMode get_mode_from(MutOperator operator, String text) throws Exception {
		if(text == null) throw new IllegalArgumentException("invalid text: null");
		else if(operator == null) throw new IllegalArgumentException("invalid op: null");
		else {
			switch(operator) {
			/* statement mutations */
			case STRP:	
				return MutationMode.TRAP_ON_STMT;
			case STRI:	
				if(MutationMode.TRAP_ON_TRUE.toString().equals(text)) 
					 return MutationMode.TRAP_ON_TRUE;
				else if(MutationMode.TRAP_ON_FALSE.toString().equals(text)) 
					return MutationMode.TRAP_ON_FALSE;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case STRC:
				if(MutationMode.TRAP_ON_TRUE.toString().equals(text)) 
					 return MutationMode.TRAP_ON_TRUE;
				else if(MutationMode.TRAP_ON_FALSE.toString().equals(text)) 
					return MutationMode.TRAP_ON_FALSE;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case SSDL:
				return MutationMode.DELETE_STATEMENT;
			case SBRC:
				return MutationMode.REP_BY_CONTINUE;
			case SCRB:
				return MutationMode.REP_BY_BREAK;
			case SWDD:
				return MutationMode.REP_BY_DO_WHILE;
			case SDWD:
				return MutationMode.REP_BY_WHILE;
			case SSWM:
				return MutationMode.VTRAP_ON_CASE;
			case SMTC:
				return MutationMode.VTRAP_FOR_TIMES;
			/* unary operator mutations */
			case OPPO:
				if(MutationMode.POST_PREV_INC.toString().equals(text)) 
					return MutationMode.POST_PREV_INC;
				else if(MutationMode.PREV_POST_INC.toString().equals(text)) 
					return MutationMode.PREV_POST_INC;
				else if(MutationMode.POST_INC_DEC.toString().equals(text)) 
					return MutationMode.POST_INC_DEC;
				else if(MutationMode.PREV_INC_DEC.toString().equals(text)) 
					return MutationMode.PREV_INC_DEC;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OMMO:
				if(MutationMode.POST_PREV_DEC.toString().equals(text)) 
					return MutationMode.POST_PREV_DEC;
				else if(MutationMode.PREV_POST_DEC.toString().equals(text)) 
					return MutationMode.PREV_POST_DEC;
				else if(MutationMode.POST_DEC_INC.toString().equals(text)) 
					return MutationMode.POST_DEC_INC;
				else if(MutationMode.PREV_DEC_INC.toString().equals(text)) 
					return MutationMode.PREV_DEC_INC;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case UIOI:
				if(MutationMode.PREV_INC_INS.toString().equals(text)) 
					return MutationMode.PREV_INC_INS;
				else if(MutationMode.PREV_DEC_INS.toString().equals(text)) 
					return MutationMode.PREV_DEC_INS;
				else if(MutationMode.POST_INC_INS.toString().equals(text)) 
					return MutationMode.POST_INC_INS;
				else if(MutationMode.POST_DEC_INS.toString().equals(text)) 
					return MutationMode.POST_DEC_INS;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OBNG:	return MutationMode.NEG_BITWISE;
			case OCNG: 	return MutationMode.NEG_BOOLEAN;
			case OLNG:	return MutationMode.NEG_BOOLEAN;
			case ONDU:
				if(MutationMode.ANG_DELETE.toString().equals(text))
					return MutationMode.ANG_DELETE;
				else if(MutationMode.BNG_DELETE.toString().equals(text))
					return MutationMode.BNG_DELETE;
				else if(MutationMode.LNG_DELETE.toString().equals(text))
					return MutationMode.LNG_DELETE;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			/* binary operator mutations */
			case OAAN:
				if(MutationMode.ADD_SUB.toString().equals(text))
					return MutationMode.ADD_SUB;
				else if(MutationMode.ADD_MUL.toString().equals(text))
					return MutationMode.ADD_MUL;
				else if(MutationMode.ADD_DIV.toString().equals(text))
					return MutationMode.ADD_DIV;
				else if(MutationMode.ADD_MOD.toString().equals(text))
					return MutationMode.ADD_MOD;
				else if(MutationMode.SUB_ADD.toString().equals(text))
					return MutationMode.SUB_ADD;
				else if(MutationMode.SUB_MUL.toString().equals(text))
					return MutationMode.SUB_MUL;
				else if(MutationMode.SUB_DIV.toString().equals(text))
					return MutationMode.SUB_DIV;
				else if(MutationMode.SUB_MOD.toString().equals(text))
					return MutationMode.SUB_MOD;
				else if(MutationMode.MUL_ADD.toString().equals(text))
					return MutationMode.MUL_ADD;
				else if(MutationMode.MUL_SUB.toString().equals(text))
					return MutationMode.MUL_SUB;
				else if(MutationMode.MUL_DIV.toString().equals(text))
					return MutationMode.MUL_DIV;
				else if(MutationMode.MUL_MOD.toString().equals(text))
					return MutationMode.MUL_MOD;
				else if(MutationMode.DIV_ADD.toString().equals(text))
					return MutationMode.DIV_ADD;
				else if(MutationMode.DIV_SUB.toString().equals(text))
					return MutationMode.DIV_SUB;
				else if(MutationMode.DIV_MUL.toString().equals(text))
					return MutationMode.DIV_MUL;
				else if(MutationMode.DIV_MOD.toString().equals(text))
					return MutationMode.DIV_MOD;
				else if(MutationMode.MOD_ADD.toString().equals(text))
					return MutationMode.MOD_ADD;
				else if(MutationMode.MOD_SUB.toString().equals(text))
					return MutationMode.MOD_SUB;
				else if(MutationMode.MOD_MUL.toString().equals(text))
					return MutationMode.MOD_MUL;
				else if(MutationMode.MOD_DIV.toString().equals(text))
					return MutationMode.MOD_DIV;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OABN:
				if(MutationMode.ADD_BAN.toString().equals(text))
					return MutationMode.ADD_BAN;
				else if(MutationMode.ADD_BOR.toString().equals(text))
					return MutationMode.ADD_BOR;
				else if(MutationMode.ADD_BXR.toString().equals(text))
					return MutationMode.ADD_BXR;
				else if(MutationMode.SUB_BAN.toString().equals(text))
					return MutationMode.SUB_BAN;
				else if(MutationMode.SUB_BOR.toString().equals(text))
					return MutationMode.SUB_BOR;
				else if(MutationMode.SUB_BXR.toString().equals(text))
					return MutationMode.SUB_BXR;
				else if(MutationMode.MUL_BAN.toString().equals(text))
					return MutationMode.MUL_BAN;
				else if(MutationMode.MUL_BOR.toString().equals(text))
					return MutationMode.MUL_BOR;
				else if(MutationMode.MUL_BXR.toString().equals(text))
					return MutationMode.MUL_BXR;
				else if(MutationMode.DIV_BAN.toString().equals(text))
					return MutationMode.DIV_BAN;
				else if(MutationMode.DIV_BOR.toString().equals(text))
					return MutationMode.DIV_BOR;
				else if(MutationMode.DIV_BXR.toString().equals(text))
					return MutationMode.DIV_BXR;
				else if(MutationMode.MOD_BAN.toString().equals(text))
					return MutationMode.MOD_BAN;
				else if(MutationMode.MOD_BOR.toString().equals(text))
					return MutationMode.MOD_BOR;
				else if(MutationMode.MOD_BXR.toString().equals(text))
					return MutationMode.MOD_BXR;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OALN:
				if(MutationMode.ADD_LAN.toString().equals(text))
					return MutationMode.ADD_LAN;
				else if(MutationMode.SUB_LAN.toString().equals(text))
					return MutationMode.SUB_LAN;
				else if(MutationMode.MUL_LAN.toString().equals(text))
					return MutationMode.MUL_LAN;
				else if(MutationMode.DIV_LAN.toString().equals(text))
					return MutationMode.DIV_LAN;
				else if(MutationMode.MOD_LAN.toString().equals(text))
					return MutationMode.MOD_LAN;
				else if(MutationMode.ADD_LOR.toString().equals(text))
					return MutationMode.ADD_LOR;
				else if(MutationMode.SUB_LOR.toString().equals(text))
					return MutationMode.SUB_LOR;
				else if(MutationMode.MUL_LOR.toString().equals(text))
					return MutationMode.MUL_LOR;
				else if(MutationMode.DIV_LOR.toString().equals(text))
					return MutationMode.DIV_LOR;
				else if(MutationMode.MOD_LOR.toString().equals(text))
					return MutationMode.MOD_LOR;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OARN:
				if(MutationMode.ADD_GRT.toString().equals(text))
					return MutationMode.ADD_GRT;
				else if(MutationMode.SUB_GRT.toString().equals(text))
					return MutationMode.SUB_GRT;
				else if(MutationMode.MUL_GRT.toString().equals(text))
					return MutationMode.MUL_GRT;
				else if(MutationMode.DIV_GRT.toString().equals(text))
					return MutationMode.DIV_GRT;
				else if(MutationMode.MOD_GRT.toString().equals(text))
					return MutationMode.MOD_GRT;
				else if(MutationMode.ADD_GRE.toString().equals(text))
					return MutationMode.ADD_GRE;
				else if(MutationMode.SUB_GRE.toString().equals(text))
					return MutationMode.SUB_GRE;
				else if(MutationMode.MUL_GRE.toString().equals(text))
					return MutationMode.MUL_GRE;
				else if(MutationMode.DIV_GRE.toString().equals(text))
					return MutationMode.DIV_GRE;
				else if(MutationMode.MOD_GRE.toString().equals(text))
					return MutationMode.MOD_GRE;
				else if(MutationMode.ADD_EQV.toString().equals(text))
					return MutationMode.ADD_EQV;
				else if(MutationMode.SUB_EQV.toString().equals(text))
					return MutationMode.SUB_EQV;
				else if(MutationMode.MUL_EQV.toString().equals(text))
					return MutationMode.MUL_EQV;
				else if(MutationMode.DIV_EQV.toString().equals(text))
					return MutationMode.DIV_EQV;
				else if(MutationMode.MOD_EQV.toString().equals(text))
					return MutationMode.MOD_EQV;
				else if(MutationMode.ADD_NEQ.toString().equals(text))
					return MutationMode.ADD_NEQ;
				else if(MutationMode.SUB_NEQ.toString().equals(text))
					return MutationMode.SUB_NEQ;
				else if(MutationMode.MUL_NEQ.toString().equals(text))
					return MutationMode.MUL_NEQ;
				else if(MutationMode.DIV_NEQ.toString().equals(text))
					return MutationMode.DIV_NEQ;
				else if(MutationMode.MOD_NEQ.toString().equals(text))
					return MutationMode.MOD_NEQ;
				else if(MutationMode.ADD_SMT.toString().equals(text))
					return MutationMode.ADD_SMT;
				else if(MutationMode.SUB_SMT.toString().equals(text))
					return MutationMode.SUB_SMT;
				else if(MutationMode.MUL_SMT.toString().equals(text))
					return MutationMode.MUL_SMT;
				else if(MutationMode.DIV_SMT.toString().equals(text))
					return MutationMode.DIV_SMT;
				else if(MutationMode.MOD_SMT.toString().equals(text))
					return MutationMode.MOD_SMT;
				else if(MutationMode.ADD_SME.toString().equals(text))
					return MutationMode.ADD_SME;
				else if(MutationMode.SUB_SME.toString().equals(text))
					return MutationMode.SUB_SME;
				else if(MutationMode.MUL_SME.toString().equals(text))
					return MutationMode.MUL_SME;
				else if(MutationMode.DIV_SME.toString().equals(text))
					return MutationMode.DIV_SME;
				else if(MutationMode.MOD_SME.toString().equals(text))
					return MutationMode.MOD_SME;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OASN:
				if(MutationMode.ADD_LSH.toString().equals(text))
					return MutationMode.ADD_LSH;
				else if(MutationMode.SUB_LSH.toString().equals(text))
					return MutationMode.SUB_LSH;
				else if(MutationMode.MUL_LSH.toString().equals(text))
					return MutationMode.MUL_LSH;
				else if(MutationMode.DIV_LSH.toString().equals(text))
					return MutationMode.DIV_LSH;
				else if(MutationMode.MOD_LSH.toString().equals(text))
					return MutationMode.MOD_LSH;
				else if(MutationMode.ADD_RSH.toString().equals(text))
					return MutationMode.ADD_RSH;
				else if(MutationMode.SUB_RSH.toString().equals(text))
					return MutationMode.SUB_RSH;
				else if(MutationMode.MUL_RSH.toString().equals(text))
					return MutationMode.MUL_RSH;
				else if(MutationMode.DIV_RSH.toString().equals(text))
					return MutationMode.DIV_RSH;
				else if(MutationMode.MOD_RSH.toString().equals(text))
					return MutationMode.MOD_RSH;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OBAN:
				if(MutationMode.BAN_ADD.toString().equals(text))
					return MutationMode.BAN_ADD;
				else if(MutationMode.BOR_ADD.toString().equals(text))
					return MutationMode.BOR_ADD;
				else if(MutationMode.BXR_ADD.toString().equals(text))
					return MutationMode.BXR_ADD;
				else if(MutationMode.BAN_SUB.toString().equals(text))
					return MutationMode.BAN_SUB;
				else if(MutationMode.BOR_SUB.toString().equals(text))
					return MutationMode.BOR_SUB;
				else if(MutationMode.BXR_SUB.toString().equals(text))
					return MutationMode.BXR_SUB;
				else if(MutationMode.BAN_MUL.toString().equals(text))
					return MutationMode.BAN_MUL;
				else if(MutationMode.BOR_MUL.toString().equals(text))
					return MutationMode.BOR_MUL;
				else if(MutationMode.BXR_MUL.toString().equals(text))
					return MutationMode.BXR_MUL;
				else if(MutationMode.BAN_DIV.toString().equals(text))
					return MutationMode.BAN_DIV;
				else if(MutationMode.BOR_DIV.toString().equals(text))
					return MutationMode.BOR_DIV;
				else if(MutationMode.BXR_DIV.toString().equals(text))
					return MutationMode.BXR_DIV;
				else if(MutationMode.BAN_MOD.toString().equals(text))
					return MutationMode.BAN_MOD;
				else if(MutationMode.BOR_MOD.toString().equals(text))
					return MutationMode.BOR_MOD;
				else if(MutationMode.BXR_MOD.toString().equals(text))
					return MutationMode.BXR_MOD;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OBBN:
				if(MutationMode.BAN_BOR.toString().equals(text))
					return MutationMode.BAN_BOR;
				else if(MutationMode.BAN_BXR.toString().equals(text))
					return MutationMode.BAN_BXR;
				else if(MutationMode.BOR_BAN.toString().equals(text))
					return MutationMode.BOR_BAN;
				else if(MutationMode.BOR_BXR.toString().equals(text))
					return MutationMode.BOR_BXR;
				else if(MutationMode.BXR_BAN.toString().equals(text))
					return MutationMode.BXR_BAN;
				else if(MutationMode.BXR_BOR.toString().equals(text))
					return MutationMode.BXR_BOR;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OBLN:
				if(MutationMode.BAN_LAN.toString().equals(text))
					return MutationMode.BAN_LAN;
				else if(MutationMode.BOR_LAN.toString().equals(text))
					return MutationMode.BOR_LAN;
				else if(MutationMode.BXR_LAN.toString().equals(text))
					return MutationMode.BXR_LAN;
				else if(MutationMode.BAN_LOR.toString().equals(text))
					return MutationMode.BAN_LOR;
				else if(MutationMode.BOR_LOR.toString().equals(text))
					return MutationMode.BOR_LOR;
				else if(MutationMode.BXR_LOR.toString().equals(text))
					return MutationMode.BXR_LOR;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OBRN:
				if(MutationMode.BAN_GRT.toString().equals(text))
					return MutationMode.BAN_GRT;
				else if(MutationMode.BOR_GRT.toString().equals(text))
					return MutationMode.BOR_GRT;
				else if(MutationMode.BXR_GRT.toString().equals(text))
					return MutationMode.BXR_GRT;
				else if(MutationMode.BAN_GRE.toString().equals(text))
					return MutationMode.BAN_GRE;
				else if(MutationMode.BOR_GRE.toString().equals(text))
					return MutationMode.BOR_GRE;
				else if(MutationMode.BXR_GRE.toString().equals(text))
					return MutationMode.BXR_GRE;
				else if(MutationMode.BAN_EQV.toString().equals(text))
					return MutationMode.BAN_EQV;
				else if(MutationMode.BOR_EQV.toString().equals(text))
					return MutationMode.BOR_EQV;
				else if(MutationMode.BXR_EQV.toString().equals(text))
					return MutationMode.BXR_EQV;
				else if(MutationMode.BAN_NEQ.toString().equals(text))
					return MutationMode.BAN_NEQ;
				else if(MutationMode.BOR_NEQ.toString().equals(text))
					return MutationMode.BOR_NEQ;
				else if(MutationMode.BXR_NEQ.toString().equals(text))
					return MutationMode.BXR_NEQ;
				else if(MutationMode.BAN_SMT.toString().equals(text))
					return MutationMode.BAN_SMT;
				else if(MutationMode.BOR_SMT.toString().equals(text))
					return MutationMode.BOR_SMT;
				else if(MutationMode.BXR_SMT.toString().equals(text))
					return MutationMode.BXR_SMT;
				else if(MutationMode.BAN_SME.toString().equals(text))
					return MutationMode.BAN_SME;
				else if(MutationMode.BOR_SME.toString().equals(text))
					return MutationMode.BOR_SME;
				else if(MutationMode.BXR_SME.toString().equals(text))
					return MutationMode.BXR_SME;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OBSN:
				if(MutationMode.BAN_LSH.toString().equals(text))
					return MutationMode.BAN_LSH;
				else if(MutationMode.BOR_LSH.toString().equals(text))
					return MutationMode.BOR_LSH;
				else if(MutationMode.BXR_LSH.toString().equals(text))
					return MutationMode.BXR_LSH;
				else if(MutationMode.BAN_RSH.toString().equals(text))
					return MutationMode.BAN_RSH;
				else if(MutationMode.BOR_RSH.toString().equals(text))
					return MutationMode.BOR_RSH;
				else if(MutationMode.BXR_RSH.toString().equals(text))
					return MutationMode.BXR_RSH;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OLAN:
				if(MutationMode.LAN_ADD.toString().equals(text))
					return MutationMode.LAN_ADD;
				else if(MutationMode.LOR_ADD.toString().equals(text))
					return MutationMode.LOR_ADD;
				else if(MutationMode.LAN_SUB.toString().equals(text))
					return MutationMode.LAN_SUB;
				else if(MutationMode.LOR_SUB.toString().equals(text))
					return MutationMode.LOR_SUB;
				else if(MutationMode.LAN_MUL.toString().equals(text))
					return MutationMode.LAN_MUL;
				else if(MutationMode.LOR_MUL.toString().equals(text))
					return MutationMode.LOR_MUL;
				else if(MutationMode.LAN_DIV.toString().equals(text))
					return MutationMode.LAN_DIV;
				else if(MutationMode.LOR_DIV.toString().equals(text))
					return MutationMode.LOR_DIV;
				else if(MutationMode.LAN_MOD.toString().equals(text))
					return MutationMode.LAN_MOD;
				else if(MutationMode.LOR_MOD.toString().equals(text))
					return MutationMode.LOR_MOD;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OLBN:
				if(MutationMode.LAN_BAN.toString().equals(text))
					return MutationMode.LAN_BAN;
				else if(MutationMode.LOR_BAN.toString().equals(text))
					return MutationMode.LOR_BAN;
				else if(MutationMode.LAN_BOR.toString().equals(text))
					return MutationMode.LAN_BOR;
				else if(MutationMode.LOR_BOR.toString().equals(text))
					return MutationMode.LOR_BOR;
				else if(MutationMode.LAN_BXR.toString().equals(text))
					return MutationMode.LAN_BXR;
				else if(MutationMode.LOR_BXR.toString().equals(text))
					return MutationMode.LOR_BXR;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OLLN:
				if(MutationMode.LAN_LOR.toString().equals(text))
					return MutationMode.LAN_LOR;
				else if(MutationMode.LOR_LAN.toString().equals(text))
					return MutationMode.LOR_LAN;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case OLRN:
				if(MutationMode.LAN_GRT.toString().equals(text))
					return MutationMode.LAN_GRT;
				else if(MutationMode.LOR_GRT.toString().equals(text))
					return MutationMode.LOR_GRT;
				else if(MutationMode.LAN_GRE.toString().equals(text))
					return MutationMode.LAN_GRE;
				else if(MutationMode.LOR_GRE.toString().equals(text))
					return MutationMode.LOR_GRE;
				else if(MutationMode.LAN_EQV.toString().equals(text))
					return MutationMode.LAN_EQV;
				else if(MutationMode.LOR_EQV.toString().equals(text))
					return MutationMode.LOR_EQV;
				else if(MutationMode.LAN_NEQ.toString().equals(text))
					return MutationMode.LAN_NEQ;
				else if(MutationMode.LOR_NEQ.toString().equals(text))
					return MutationMode.LOR_NEQ;
				else if(MutationMode.LAN_SMT.toString().equals(text))
					return MutationMode.LAN_SMT;
				else if(MutationMode.LOR_SMT.toString().equals(text))
					return MutationMode.LOR_SMT;
				else if(MutationMode.LAN_SME.toString().equals(text))
					return MutationMode.LAN_SME;
				else if(MutationMode.LOR_SME.toString().equals(text))
					return MutationMode.LOR_SME;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OLSN:
				if(MutationMode.LAN_LSH.toString().equals(text))
					return MutationMode.LAN_LSH;
				else if(MutationMode.LOR_LSH.toString().equals(text))
					return MutationMode.LOR_LSH;
				else if(MutationMode.LAN_RSH.toString().equals(text))
					return MutationMode.LAN_RSH;
				else if(MutationMode.LOR_RSH.toString().equals(text))
					return MutationMode.LOR_RSH;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case ORAN:
				if(MutationMode.GRT_ADD.toString().equals(text))
					return MutationMode.GRT_ADD;
				else if(MutationMode.GRE_ADD.toString().equals(text))
					return MutationMode.GRE_ADD;
				else if(MutationMode.EQV_ADD.toString().equals(text))
					return MutationMode.EQV_ADD;
				else if(MutationMode.NEQ_ADD.toString().equals(text))
					return MutationMode.NEQ_ADD;
				else if(MutationMode.SMT_ADD.toString().equals(text))
					return MutationMode.SMT_ADD;
				else if(MutationMode.SME_ADD.toString().equals(text))
					return MutationMode.SME_ADD;
				else if(MutationMode.GRT_SUB.toString().equals(text))
					return MutationMode.GRT_SUB;
				else if(MutationMode.GRE_SUB.toString().equals(text))
					return MutationMode.GRE_SUB;
				else if(MutationMode.EQV_SUB.toString().equals(text))
					return MutationMode.EQV_SUB;
				else if(MutationMode.NEQ_SUB.toString().equals(text))
					return MutationMode.NEQ_SUB;
				else if(MutationMode.SMT_SUB.toString().equals(text))
					return MutationMode.SMT_SUB;
				else if(MutationMode.SME_SUB.toString().equals(text))
					return MutationMode.SME_SUB;
				else if(MutationMode.GRT_MUL.toString().equals(text))
					return MutationMode.GRT_MUL;
				else if(MutationMode.GRE_MUL.toString().equals(text))
					return MutationMode.GRE_MUL;
				else if(MutationMode.EQV_MUL.toString().equals(text))
					return MutationMode.EQV_MUL;
				else if(MutationMode.NEQ_MUL.toString().equals(text))
					return MutationMode.NEQ_MUL;
				else if(MutationMode.SMT_MUL.toString().equals(text))
					return MutationMode.SMT_MUL;
				else if(MutationMode.SME_MUL.toString().equals(text))
					return MutationMode.SME_MUL;
				else if(MutationMode.GRT_DIV.toString().equals(text))
					return MutationMode.GRT_DIV;
				else if(MutationMode.GRE_DIV.toString().equals(text))
					return MutationMode.GRE_DIV;
				else if(MutationMode.EQV_DIV.toString().equals(text))
					return MutationMode.EQV_DIV;
				else if(MutationMode.NEQ_DIV.toString().equals(text))
					return MutationMode.NEQ_DIV;
				else if(MutationMode.SMT_DIV.toString().equals(text))
					return MutationMode.SMT_DIV;
				else if(MutationMode.SME_DIV.toString().equals(text))
					return MutationMode.SME_DIV;
				else if(MutationMode.GRT_MOD.toString().equals(text))
					return MutationMode.GRT_MOD;
				else if(MutationMode.GRE_MOD.toString().equals(text))
					return MutationMode.GRE_MOD;
				else if(MutationMode.EQV_MOD.toString().equals(text))
					return MutationMode.EQV_MOD;
				else if(MutationMode.NEQ_MOD.toString().equals(text))
					return MutationMode.NEQ_MOD;
				else if(MutationMode.SMT_MOD.toString().equals(text))
					return MutationMode.SMT_MOD;
				else if(MutationMode.SME_MOD.toString().equals(text))
					return MutationMode.SME_MOD;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case ORBN:
				if(MutationMode.GRT_BAN.toString().equals(text))
					return MutationMode.GRT_BAN;
				else if(MutationMode.GRE_BAN.toString().equals(text))
					return MutationMode.GRE_BAN;
				else if(MutationMode.EQV_BAN.toString().equals(text))
					return MutationMode.EQV_BAN;
				else if(MutationMode.NEQ_BAN.toString().equals(text))
					return MutationMode.NEQ_BAN;
				else if(MutationMode.SMT_BAN.toString().equals(text))
					return MutationMode.SMT_BAN;
				else if(MutationMode.SME_BAN.toString().equals(text))
					return MutationMode.SME_BAN;
				else if(MutationMode.GRT_BOR.toString().equals(text))
					return MutationMode.GRT_BOR;
				else if(MutationMode.GRE_BOR.toString().equals(text))
					return MutationMode.GRE_BOR;
				else if(MutationMode.EQV_BOR.toString().equals(text))
					return MutationMode.EQV_BOR;
				else if(MutationMode.NEQ_BOR.toString().equals(text))
					return MutationMode.NEQ_BOR;
				else if(MutationMode.SMT_BOR.toString().equals(text))
					return MutationMode.SMT_BOR;
				else if(MutationMode.SME_BOR.toString().equals(text))
					return MutationMode.SME_BOR;
				else if(MutationMode.GRT_BXR.toString().equals(text))
					return MutationMode.GRT_BXR;
				else if(MutationMode.GRE_BXR.toString().equals(text))
					return MutationMode.GRE_BXR;
				else if(MutationMode.EQV_BXR.toString().equals(text))
					return MutationMode.EQV_BXR;
				else if(MutationMode.NEQ_BXR.toString().equals(text))
					return MutationMode.NEQ_BXR;
				else if(MutationMode.SMT_BXR.toString().equals(text))
					return MutationMode.SMT_BXR;
				else if(MutationMode.SME_BXR.toString().equals(text))
					return MutationMode.SME_BXR;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case ORLN:
				if(MutationMode.GRT_LAN.toString().equals(text))
					return MutationMode.GRT_LAN;
				else if(MutationMode.GRE_LAN.toString().equals(text))
					return MutationMode.GRE_LAN;
				else if(MutationMode.EQV_LAN.toString().equals(text))
					return MutationMode.EQV_LAN;
				else if(MutationMode.NEQ_LAN.toString().equals(text))
					return MutationMode.NEQ_LAN;
				else if(MutationMode.SMT_LAN.toString().equals(text))
					return MutationMode.SMT_LAN;
				else if(MutationMode.SME_LAN.toString().equals(text))
					return MutationMode.SME_LAN;
				else if(MutationMode.GRT_LOR.toString().equals(text))
					return MutationMode.GRT_LOR;
				else if(MutationMode.GRE_LOR.toString().equals(text))
					return MutationMode.GRE_LOR;
				else if(MutationMode.EQV_LOR.toString().equals(text))
					return MutationMode.EQV_LOR;
				else if(MutationMode.NEQ_LOR.toString().equals(text))
					return MutationMode.NEQ_LOR;
				else if(MutationMode.SMT_LOR.toString().equals(text))
					return MutationMode.SMT_LOR;
				else if(MutationMode.SME_LOR.toString().equals(text))
					return MutationMode.SME_LOR;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case ORRN:
				if(MutationMode.GRE_GRT.toString().equals(text))
					return MutationMode.GRE_GRT;
				else if(MutationMode.EQV_GRT.toString().equals(text))
					return MutationMode.EQV_GRT;
				else if(MutationMode.NEQ_GRT.toString().equals(text))
					return MutationMode.NEQ_GRT;
				else if(MutationMode.SMT_GRT.toString().equals(text))
					return MutationMode.SMT_GRT;
				else if(MutationMode.SME_GRT.toString().equals(text))
					return MutationMode.SME_GRT;
				else if(MutationMode.GRT_GRE.toString().equals(text))
					return MutationMode.GRT_GRE;
				else if(MutationMode.EQV_GRE.toString().equals(text))
					return MutationMode.EQV_GRE;
				else if(MutationMode.NEQ_GRE.toString().equals(text))
					return MutationMode.NEQ_GRE;
				else if(MutationMode.SMT_GRE.toString().equals(text))
					return MutationMode.SMT_GRE;
				else if(MutationMode.SME_GRE.toString().equals(text))
					return MutationMode.SME_GRE;
				else if(MutationMode.GRT_EQV.toString().equals(text))
					return MutationMode.GRT_EQV;
				else if(MutationMode.GRE_EQV.toString().equals(text))
					return MutationMode.GRE_EQV;
				else if(MutationMode.NEQ_EQV.toString().equals(text))
					return MutationMode.NEQ_EQV;
				else if(MutationMode.SMT_EQV.toString().equals(text))
					return MutationMode.SMT_EQV;
				else if(MutationMode.SME_EQV.toString().equals(text))
					return MutationMode.SME_EQV;
				else if(MutationMode.GRT_NEQ.toString().equals(text))
					return MutationMode.GRT_NEQ;
				else if(MutationMode.GRE_NEQ.toString().equals(text))
					return MutationMode.GRE_NEQ;
				else if(MutationMode.EQV_NEQ.toString().equals(text))
					return MutationMode.EQV_NEQ;
				else if(MutationMode.SMT_NEQ.toString().equals(text))
					return MutationMode.SMT_NEQ;
				else if(MutationMode.SME_NEQ.toString().equals(text))
					return MutationMode.SME_NEQ;
				else if(MutationMode.GRT_SMT.toString().equals(text))
					return MutationMode.GRT_SMT;
				else if(MutationMode.GRE_SMT.toString().equals(text))
					return MutationMode.GRE_SMT;
				else if(MutationMode.EQV_SMT.toString().equals(text))
					return MutationMode.EQV_SMT;
				else if(MutationMode.NEQ_SMT.toString().equals(text))
					return MutationMode.NEQ_SMT;
				else if(MutationMode.SME_SMT.toString().equals(text))
					return MutationMode.SME_SMT;
				else if(MutationMode.GRT_SME.toString().equals(text))
					return MutationMode.GRT_SME;
				else if(MutationMode.GRE_SME.toString().equals(text))
					return MutationMode.GRE_SME;
				else if(MutationMode.EQV_SME.toString().equals(text))
					return MutationMode.EQV_SME;
				else if(MutationMode.NEQ_SME.toString().equals(text))
					return MutationMode.NEQ_SME;
				else if(MutationMode.SMT_SME.toString().equals(text))
					return MutationMode.SMT_SME;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case ORSN:
				if(MutationMode.GRT_LSH.toString().equals(text))
					return MutationMode.GRT_LSH;
				else if(MutationMode.GRE_LSH.toString().equals(text))
					return MutationMode.GRE_LSH;
				else if(MutationMode.EQV_LSH.toString().equals(text))
					return MutationMode.EQV_LSH;
				else if(MutationMode.NEQ_LSH.toString().equals(text))
					return MutationMode.NEQ_LSH;
				else if(MutationMode.SMT_LSH.toString().equals(text))
					return MutationMode.SMT_LSH;
				else if(MutationMode.SME_LSH.toString().equals(text))
					return MutationMode.SME_LSH;
				else if(MutationMode.GRT_RSH.toString().equals(text))
					return MutationMode.GRT_RSH;
				else if(MutationMode.GRE_RSH.toString().equals(text))
					return MutationMode.GRE_RSH;
				else if(MutationMode.EQV_RSH.toString().equals(text))
					return MutationMode.EQV_RSH;
				else if(MutationMode.NEQ_RSH.toString().equals(text))
					return MutationMode.NEQ_RSH;
				else if(MutationMode.SMT_RSH.toString().equals(text))
					return MutationMode.SMT_RSH;
				else if(MutationMode.SME_RSH.toString().equals(text))
					return MutationMode.SME_RSH;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OSAN:
				if(MutationMode.LSH_ADD.toString().equals(text))
					return MutationMode.LSH_ADD;
				else if(MutationMode.RSH_ADD.toString().equals(text))
					return MutationMode.RSH_ADD;
				else if(MutationMode.LSH_SUB.toString().equals(text))
					return MutationMode.LSH_SUB;
				else if(MutationMode.RSH_SUB.toString().equals(text))
					return MutationMode.RSH_SUB;
				else if(MutationMode.LSH_MUL.toString().equals(text))
					return MutationMode.LSH_MUL;
				else if(MutationMode.RSH_MUL.toString().equals(text))
					return MutationMode.RSH_MUL;
				else if(MutationMode.LSH_DIV.toString().equals(text))
					return MutationMode.LSH_DIV;
				else if(MutationMode.RSH_DIV.toString().equals(text))
					return MutationMode.RSH_DIV;
				else if(MutationMode.LSH_MOD.toString().equals(text))
					return MutationMode.LSH_MOD;
				else if(MutationMode.RSH_MOD.toString().equals(text))
					return MutationMode.RSH_MOD;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OSBN:
				if(MutationMode.LSH_BAN.toString().equals(text))
					return MutationMode.LSH_BAN;
				else if(MutationMode.RSH_BAN.toString().equals(text))
					return MutationMode.RSH_BAN;
				else if(MutationMode.LSH_BOR.toString().equals(text))
					return MutationMode.LSH_BOR;
				else if(MutationMode.RSH_BOR.toString().equals(text))
					return MutationMode.RSH_BOR;
				else if(MutationMode.LSH_BXR.toString().equals(text))
					return MutationMode.LSH_BXR;
				else if(MutationMode.RSH_BXR.toString().equals(text))
					return MutationMode.RSH_BXR;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OSLN:
				if(MutationMode.LSH_LAN.toString().equals(text))
					return MutationMode.LSH_LAN;
				else if(MutationMode.RSH_LAN.toString().equals(text))
					return MutationMode.RSH_LAN;
				else if(MutationMode.LSH_LOR.toString().equals(text))
					return MutationMode.LSH_LOR;
				else if(MutationMode.RSH_LOR.toString().equals(text))
					return MutationMode.RSH_LOR;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OSRN:
				if(MutationMode.LSH_GRT.toString().equals(text))
					return MutationMode.LSH_GRT;
				else if(MutationMode.RSH_GRT.toString().equals(text))
					return MutationMode.RSH_GRT;
				else if(MutationMode.LSH_GRE.toString().equals(text))
					return MutationMode.LSH_GRE;
				else if(MutationMode.RSH_GRE.toString().equals(text))
					return MutationMode.RSH_GRE;
				else if(MutationMode.LSH_EQV.toString().equals(text))
					return MutationMode.LSH_EQV;
				else if(MutationMode.RSH_EQV.toString().equals(text))
					return MutationMode.RSH_EQV;
				else if(MutationMode.LSH_NEQ.toString().equals(text))
					return MutationMode.LSH_NEQ;
				else if(MutationMode.RSH_NEQ.toString().equals(text))
					return MutationMode.RSH_NEQ;
				else if(MutationMode.LSH_SMT.toString().equals(text))
					return MutationMode.LSH_SMT;
				else if(MutationMode.RSH_SMT.toString().equals(text))
					return MutationMode.RSH_SMT;
				else if(MutationMode.LSH_SME.toString().equals(text))
					return MutationMode.LSH_SME;
				else if(MutationMode.RSH_SME.toString().equals(text))
					return MutationMode.RSH_SME;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OSSN:
				if(MutationMode.LSH_RSH.toString().equals(text))
					return MutationMode.LSH_RSH;
				else if(MutationMode.RSH_LSH.toString().equals(text))
					return MutationMode.RSH_LSH;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OEAA:
				if(MutationMode.ASG_ADD.toString().equals(text))
					return MutationMode.ASG_ADD;
				else if(MutationMode.ASG_SUB.toString().equals(text))
					return MutationMode.ASG_SUB;
				else if(MutationMode.ASG_MUL.toString().equals(text))
					return MutationMode.ASG_MUL;
				else if(MutationMode.ASG_DIV.toString().equals(text))
					return MutationMode.ASG_DIV;
				else if(MutationMode.ASG_MOD.toString().equals(text))
					return MutationMode.ASG_MOD;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OEBA:
				if(MutationMode.ASG_BAN.toString().equals(text))
					return MutationMode.ASG_BAN;
				else if(MutationMode.ASG_BOR.toString().equals(text))
					return MutationMode.ASG_BOR;
				else if(MutationMode.ASG_BXR.toString().equals(text))
					return MutationMode.ASG_BXR;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OESA:
				if(MutationMode.ASG_LSH.toString().equals(text))
					return MutationMode.ASG_LSH;
				else if(MutationMode.ASG_RSH.toString().equals(text))
					return MutationMode.ASG_RSH;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			/* arith-assign mutation */
			case OAAA:
				if(MutationMode.ADD_SUB_A.toString().equals(text))
					return MutationMode.ADD_SUB_A;
				else if(MutationMode.ADD_MUL_A.toString().equals(text))
					return MutationMode.ADD_MUL_A;
				else if(MutationMode.ADD_DIV_A.toString().equals(text))
					return MutationMode.ADD_DIV_A;
				else if(MutationMode.ADD_MOD_A.toString().equals(text))
					return MutationMode.ADD_MOD_A;
				else if(MutationMode.SUB_ADD_A.toString().equals(text))
					return MutationMode.SUB_ADD_A;
				else if(MutationMode.SUB_MUL_A.toString().equals(text))
					return MutationMode.SUB_MUL_A;
				else if(MutationMode.SUB_DIV_A.toString().equals(text))
					return MutationMode.SUB_DIV_A;
				else if(MutationMode.SUB_MOD_A.toString().equals(text))
					return MutationMode.SUB_MOD_A;
				else if(MutationMode.MUL_ADD_A.toString().equals(text))
					return MutationMode.MUL_ADD_A;
				else if(MutationMode.MUL_SUB_A.toString().equals(text))
					return MutationMode.MUL_SUB_A;
				else if(MutationMode.MUL_DIV_A.toString().equals(text))
					return MutationMode.MUL_DIV_A;
				else if(MutationMode.MUL_MOD_A.toString().equals(text))
					return MutationMode.MUL_MOD_A;
				else if(MutationMode.DIV_ADD_A.toString().equals(text))
					return MutationMode.DIV_ADD_A;
				else if(MutationMode.DIV_SUB_A.toString().equals(text))
					return MutationMode.DIV_SUB_A;
				else if(MutationMode.DIV_MUL_A.toString().equals(text))
					return MutationMode.DIV_MUL_A;
				else if(MutationMode.DIV_MOD_A.toString().equals(text))
					return MutationMode.DIV_MOD_A;
				else if(MutationMode.MOD_ADD_A.toString().equals(text))
					return MutationMode.MOD_ADD_A;
				else if(MutationMode.MOD_SUB_A.toString().equals(text))
					return MutationMode.MOD_SUB_A;
				else if(MutationMode.MOD_MUL_A.toString().equals(text))
					return MutationMode.MOD_MUL_A;
				else if(MutationMode.MOD_DIV_A.toString().equals(text))
					return MutationMode.MOD_DIV_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OABA:
				if(MutationMode.ADD_BAN_A.toString().equals(text))
					return MutationMode.ADD_BAN_A;
				else if(MutationMode.ADD_BOR_A.toString().equals(text))
					return MutationMode.ADD_BOR_A;
				else if(MutationMode.ADD_BXR_A.toString().equals(text))
					return MutationMode.ADD_BXR_A;
				else if(MutationMode.SUB_BAN_A.toString().equals(text))
					return MutationMode.SUB_BAN_A;
				else if(MutationMode.SUB_BOR_A.toString().equals(text))
					return MutationMode.SUB_BOR_A;
				else if(MutationMode.SUB_BXR_A.toString().equals(text))
					return MutationMode.SUB_BXR_A;
				else if(MutationMode.MUL_BAN_A.toString().equals(text))
					return MutationMode.MUL_BAN_A;
				else if(MutationMode.MUL_BOR_A.toString().equals(text))
					return MutationMode.MUL_BOR_A;
				else if(MutationMode.MUL_BXR_A.toString().equals(text))
					return MutationMode.MUL_BXR_A;
				else if(MutationMode.DIV_BAN_A.toString().equals(text))
					return MutationMode.DIV_BAN_A;
				else if(MutationMode.DIV_BOR_A.toString().equals(text))
					return MutationMode.DIV_BOR_A;
				else if(MutationMode.DIV_BXR_A.toString().equals(text))
					return MutationMode.DIV_BXR_A;
				else if(MutationMode.MOD_BAN_A.toString().equals(text))
					return MutationMode.MOD_BAN_A;
				else if(MutationMode.MOD_BOR_A.toString().equals(text))
					return MutationMode.MOD_BOR_A;
				else if(MutationMode.MOD_BXR_A.toString().equals(text))
					return MutationMode.MOD_BXR_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OASA:
				if(MutationMode.ADD_LSH_A.toString().equals(text))
					return MutationMode.ADD_LSH_A;
				else if(MutationMode.ADD_RSH_A.toString().equals(text))
					return MutationMode.ADD_RSH_A;
				else if(MutationMode.SUB_LSH_A.toString().equals(text))
					return MutationMode.SUB_LSH_A;
				else if(MutationMode.SUB_RSH_A.toString().equals(text))
					return MutationMode.SUB_RSH_A;
				else if(MutationMode.MUL_LSH_A.toString().equals(text))
					return MutationMode.MUL_LSH_A;
				else if(MutationMode.MUL_RSH_A.toString().equals(text))
					return MutationMode.MUL_RSH_A;
				else if(MutationMode.DIV_LSH_A.toString().equals(text))
					return MutationMode.DIV_LSH_A;
				else if(MutationMode.DIV_RSH_A.toString().equals(text))
					return MutationMode.DIV_RSH_A;
				else if(MutationMode.MOD_LSH_A.toString().equals(text))
					return MutationMode.MOD_LSH_A;
				else if(MutationMode.MOD_RSH_A.toString().equals(text))
					return MutationMode.MOD_RSH_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			/* bitwise-assign */
			case OBAA:
				if(MutationMode.BAN_ADD_A.toString().equals(text))
					return MutationMode.BAN_ADD_A;
				else if(MutationMode.BAN_SUB_A.toString().equals(text))
					return MutationMode.BAN_SUB_A;
				else if(MutationMode.BAN_MUL_A.toString().equals(text))
					return MutationMode.BAN_MUL_A;
				else if(MutationMode.BAN_MUL_A.toString().equals(text))
					return MutationMode.BAN_MUL_A;
				else if(MutationMode.BAN_MUL_A.toString().equals(text))
					return MutationMode.BAN_MUL_A;
				else if(MutationMode.BOR_ADD_A.toString().equals(text))
					return MutationMode.BOR_ADD_A;
				else if(MutationMode.BOR_SUB_A.toString().equals(text))
					return MutationMode.BOR_SUB_A;
				else if(MutationMode.BOR_MUL_A.toString().equals(text))
					return MutationMode.BOR_MUL_A;
				else if(MutationMode.BOR_MUL_A.toString().equals(text))
					return MutationMode.BOR_MUL_A;
				else if(MutationMode.BOR_MUL_A.toString().equals(text))
					return MutationMode.BOR_MUL_A;
				else if(MutationMode.BXR_ADD_A.toString().equals(text))
					return MutationMode.BXR_ADD_A;
				else if(MutationMode.BXR_SUB_A.toString().equals(text))
					return MutationMode.BXR_SUB_A;
				else if(MutationMode.BXR_MUL_A.toString().equals(text))
					return MutationMode.BXR_MUL_A;
				else if(MutationMode.BXR_MUL_A.toString().equals(text))
					return MutationMode.BXR_MUL_A;
				else if(MutationMode.BXR_MUL_A.toString().equals(text))
					return MutationMode.BXR_MUL_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OBBA:
				if(MutationMode.BAN_BOR_A.toString().equals(text))
					return MutationMode.BAN_BOR_A;
				else if(MutationMode.BAN_BXR_A.toString().equals(text))
					return MutationMode.BAN_BXR_A;
				else if(MutationMode.BOR_BAN_A.toString().equals(text))
					return MutationMode.BOR_BAN_A;
				else if(MutationMode.BOR_BXR_A.toString().equals(text))
					return MutationMode.BOR_BXR_A;
				else if(MutationMode.BXR_BAN_A.toString().equals(text))
					return MutationMode.BXR_BAN_A;
				else if(MutationMode.BXR_BOR_A.toString().equals(text))
					return MutationMode.BXR_BOR_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OBSA:
				if(MutationMode.BAN_LSH_A.toString().equals(text))
					return MutationMode.BAN_LSH_A;
				else if(MutationMode.BAN_RSH_A.toString().equals(text))
					return MutationMode.BAN_RSH_A;
				else if(MutationMode.BOR_LSH_A.toString().equals(text))
					return MutationMode.BOR_LSH_A;
				else if(MutationMode.BOR_RSH_A.toString().equals(text))
					return MutationMode.BOR_RSH_A;
				else if(MutationMode.BXR_LSH_A.toString().equals(text))
					return MutationMode.BXR_LSH_A;
				else if(MutationMode.BXR_RSH_A.toString().equals(text))
					return MutationMode.BXR_RSH_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			/* shifting-assignment */
			case OSAA:
				if(MutationMode.LSH_ADD_A.toString().equals(text))
					return MutationMode.LSH_ADD_A;
				else if(MutationMode.LSH_SUB_A.toString().equals(text))
					return MutationMode.LSH_SUB_A;
				else if(MutationMode.LSH_MUL_A.toString().equals(text))
					return MutationMode.LSH_MUL_A;
				else if(MutationMode.LSH_DIV_A.toString().equals(text))
					return MutationMode.LSH_DIV_A;
				else if(MutationMode.LSH_MOD_A.toString().equals(text))
					return MutationMode.LSH_MOD_A;
				else if(MutationMode.RSH_ADD_A.toString().equals(text))
					return MutationMode.RSH_ADD_A;
				else if(MutationMode.RSH_SUB_A.toString().equals(text))
					return MutationMode.RSH_SUB_A;
				else if(MutationMode.RSH_MUL_A.toString().equals(text))
					return MutationMode.RSH_MUL_A;
				else if(MutationMode.RSH_DIV_A.toString().equals(text))
					return MutationMode.RSH_DIV_A;
				else if(MutationMode.RSH_MOD_A.toString().equals(text))
					return MutationMode.RSH_MOD_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OSBA:
				if(MutationMode.LSH_BAN_A.toString().equals(text))
					return MutationMode.LSH_BAN_A;
				else if(MutationMode.LSH_BOR_A.toString().equals(text))
					return MutationMode.LSH_BOR_A;
				else if(MutationMode.LSH_BXR_A.toString().equals(text))
					return MutationMode.LSH_BXR_A;
				else if(MutationMode.RSH_BAN_A.toString().equals(text))
					return MutationMode.RSH_BAN_A;
				else if(MutationMode.RSH_BOR_A.toString().equals(text))
					return MutationMode.RSH_BOR_A;
				else if(MutationMode.RSH_BXR_A.toString().equals(text))
					return MutationMode.RSH_BXR_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
			case OSSA:
				if(MutationMode.LSH_RSH_A.toString().equals(text))
					return MutationMode.LSH_RSH_A;
				else if(MutationMode.RSH_LSH_A.toString().equals(text))
					return MutationMode.RSH_LSH_A;
				else throw new IllegalArgumentException("Invalid mode: " + text); 
				
			/* value mutations */
			case VABS:	return MutationMode.ABS_VAL;
			case VBCR:	
				if(MutationMode.MUT_TRUE.toString().equals(text))
					return MutationMode.MUT_TRUE;
				else return MutationMode.MUT_FALSE;
			case VDTR:
				if(MutationMode.TRAP_ON_POS.toString().equals(text))
					return MutationMode.TRAP_ON_POS;
				else if(MutationMode.TRAP_ON_NEG.toString().equals(text))
					return MutationMode.TRAP_ON_NEG;
				else if(MutationMode.TRAP_ON_ZRO.toString().equals(text))
					return MutationMode.TRAP_ON_ZRO;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			case VTWD:
				if(MutationMode.SUCC_VAL.toString().equals(text))
					return MutationMode.SUCC_VAL;
				else if(MutationMode.PRED_VAL.toString().equals(text))
					return MutationMode.PRED_VAL;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			/* constant mutations */
			case CCCR:	return MutationMode.CST_TOT_CST;
			case CCSR:	return MutationMode.CST_TOT_REF;
			case CRCR:	
				if(MutationMode.CST_TOT_ZRO.toString().equals(text))
					return MutationMode.CST_TOT_ZRO;
				else if(MutationMode.CST_POS_ONE.toString().equals(text))
					return MutationMode.CST_POS_ONE;
				else if(MutationMode.CST_NEG_ONE.toString().equals(text))
					return MutationMode.CST_NEG_ONE;
				else if(MutationMode.CST_NEG_CST.toString().equals(text))
					return MutationMode.CST_NEG_CST;
				else if(MutationMode.CST_INC_ONE.toString().equals(text))
					return MutationMode.CST_INC_ONE;
				else if(MutationMode.CST_DEC_ONE.toString().equals(text))
					return MutationMode.CST_DEC_ONE;
				else throw new IllegalArgumentException("Invalid mode: " + text);
			/* reference mutations */
			case VARR:	return MutationMode.REF_TOT_REF;
			case VPRR:	return MutationMode.REF_TOT_REF;
			case VSRR:	return MutationMode.REF_TOT_REF;
			case VTRR:	return MutationMode.REF_TOT_REF;
			case VSFR:	return MutationMode.FLD_TOT_FLD;
			default: throw new IllegalArgumentException("Unsupported operator: " + operator);
				
			}
		}
	}
	
}
