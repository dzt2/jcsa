package __backup__;

/**
 * To parse the <code>Mutation</code> based on AST-node 
 * to the <code>FirstOrder_TextMutation</code>.
 * @author yukimula
 */
public class Mutation2TextParser {
	
	/* statement mutation parser */
	protected MutTranslator STRP_parser;
	protected MutTranslator STRI_parser;
	protected MutTranslator STRC_parser;
	protected MutTranslator SSDL_parser;
	protected MutTranslator SBRC_parser;
	protected MutTranslator SCRB_parser;
	protected MutTranslator SDWD_parser;
	protected MutTranslator SWDD_parser;
	protected MutTranslator SSWM_parser;
	protected MutTranslator SMTC_parser;
	/* unary mutation parser */
	protected MutTranslator OPPO_parser;
	protected MutTranslator OMMO_parser;
	protected MutTranslator UIOI_parser;
	protected MutTranslator OBNG_parser;
	protected MutTranslator OCNG_parser;
	protected MutTranslator OLNG_parser;
	protected MutTranslator ONDU_parser;
	/* arithmetic mutation parser  */
	protected MutTranslator OAAN_parser;
	protected MutTranslator OABN_parser;
	protected MutTranslator OALN_parser;
	protected MutTranslator OARN_parser;
	protected MutTranslator OASN_parser;
	/* bitwise mutation parser  */
	protected MutTranslator OBAN_parser;
	protected MutTranslator OBBN_parser;
	protected MutTranslator OBLN_parser;
	protected MutTranslator OBRN_parser;
	protected MutTranslator OBSN_parser;
	/* logical mutation parser  */
	protected MutTranslator OLAN_parser;
	protected MutTranslator OLBN_parser;
	protected MutTranslator OLLN_parser;
	protected MutTranslator OLRN_parser;
	protected MutTranslator OLSN_parser;
	/* relation mutation parser  */
	protected MutTranslator ORAN_parser;
	protected MutTranslator ORBN_parser;
	protected MutTranslator ORLN_parser;
	protected MutTranslator ORRN_parser;
	protected MutTranslator ORSN_parser;
	/* shifting mutation parser  */
	protected MutTranslator OSAN_parser;
	protected MutTranslator OSBN_parser;
	protected MutTranslator OSLN_parser;
	protected MutTranslator OSRN_parser;
	protected MutTranslator OSSN_parser;
	/*  assignment mutation parser  */
	protected MutTranslator OEAA_parser;
	protected MutTranslator OEBA_parser;
	protected MutTranslator OESA_parser;
	/* assignment mutation parser */
	protected MutTranslator OAAA_parser;
	protected MutTranslator OABA_parser;
	protected MutTranslator OASA_parser;
	protected MutTranslator OBAA_parser;
	protected MutTranslator OBBA_parser;
	protected MutTranslator OBSA_parser;
	protected MutTranslator OSAA_parser;
	protected MutTranslator OSBA_parser;
	protected MutTranslator OSSA_parser;
	/* value mutation parser */
	protected MutTranslator VABS_parser;
	protected MutTranslator VBCR_parser;
	protected MutTranslator VDTR_parser;
	protected MutTranslator VTWD_parser;
	/* reference mutation parser */
	protected MutTranslator CCCR_parser;
	protected MutTranslator CCSR_parser;
	protected MutTranslator CRCR_parser;
	protected MutTranslator VARR_parser;
	protected MutTranslator VPRR_parser;
	protected MutTranslator VSRR_parser;
	protected MutTranslator VTRR_parser;
	protected MutTranslator VSFR_parser;
	
	/* constructor */
	public Mutation2TextParser() {
		
		/* statement mutations */
		STRP_parser = new STRP_Translator();
		STRI_parser = new STRI_Translator();
		STRC_parser = new STRC_Translator();
		SSDL_parser = new SSDL_Translator();
		SBRC_parser = new SBRC_Translator();
		SCRB_parser = new SCRB_Translator();
		SDWD_parser = new SDWD_Translator();
		SWDD_parser = new SWDD_Translator();
		SSWM_parser = new SSWM_Translator();
		SMTC_parser = new SMTC_Translator();
		
		/* unary mutations */
		OPPO_parser = new OPPO_Translator();
		OMMO_parser = new OMMO_Translator();
		UIOI_parser = new UIOI_Translator();
		OBNG_parser = new OBNG_Translator();
		OCNG_parser = new OCNG_Translator();
		OLNG_parser = new OLNG_Translator();
		ONDU_parser = new ONDU_Translator();
		
		/* arithmetic mutations */
		OAAN_parser = new OAAN_Translator();
		OABN_parser = new OABN_Translator();
		OALN_parser = new OALN_Translator();
		OARN_parser = new OARN_Translator();
		OASN_parser = new OASN_Translator();
		/* bitwise mutations */
		OBAN_parser = new OBAN_Translator();
		OBBN_parser = new OBBN_Translator();
		OBLN_parser = new OBLN_Translator();
		OBRN_parser = new OBRN_Translator();
		OBSN_parser = new OBSN_Translator();
		/* logical mutations */
		OLAN_parser = new OLAN_Translator();
		OLBN_parser = new OLBN_Translator();
		OLLN_parser = new OLLN_Translator();
		OLRN_parser = new OLRN_Translator();
		OLSN_parser = new OLSN_Translator();
		/* relation mutations */
		ORAN_parser = new ORAN_Translator();
		ORBN_parser = new ORBN_Translator();
		ORLN_parser = new ORLN_Translator();
		ORRN_parser = new ORRN_Translator();
		ORSN_parser = new ORSN_Translator();
		/* shifting mutations */
		OSAN_parser = new OSAN_Translator();
		OSBN_parser = new OSBN_Translator();
		OSLN_parser = new OSLN_Translator();
		OSRN_parser = new OSRN_Translator();
		OSSN_parser = new OSSN_Translator();
		/* assignment mutations */
		OEAA_parser = new OEAA_Translator();
		OEBA_parser = new OEBA_Translator();
		OESA_parser = new OESA_Translator();
		/* assignment-mutations */
		OAAA_parser = new OAAA_Translator();
		OABA_parser = new OABA_Translator();
		OASA_parser = new OASA_Translator();
		OBAA_parser = new OBAA_Translator();
		OBBA_parser = new OBBA_Translator();
		OBSA_parser = new OBSA_Translator();
		OSAA_parser = new OSAA_Translator();
		OSBA_parser = new OSBA_Translator();
		OSSA_parser = new OSSA_Translator();
		
		/* value mutations */
		VABS_parser = new VABS_Translator();
		VBCR_parser = new VBCR_Translator();
		VDTR_parser = new VDTR_Translator();
		VTWD_parser = new VTWD_Translator();
		
		/* constant mutations */
		CCCR_parser = new CCCR_Translator();
		CCSR_parser = new CCSR_Translator();
		CRCR_parser = new CRCR_Translator();
		
		/* reference mutations */
		VARR_parser = new VARR_Translator();
		VPRR_parser = new VPRR_Translator();
		VSRR_parser = new VSRR_Translator();
		VTRR_parser = new VTRR_Translator();
		VSFR_parser = new VSFR_Translator();
		
	}
	
	/**
	 * to parse a <code>Mutation</code> of AST-node to
	 * the <code>TextMutation</code> to mutate code text
	 * @param mutation
	 * @return
	 * @throws Exception
	 */
	public TextMutation parse(Mutation mutation) throws Exception {
		switch(mutation.get_operator()) {
		/* statement mutation */
		case STRP:	return STRP_parser.parse(mutation);
		case STRI:	return STRI_parser.parse(mutation);
		case STRC:	return STRC_parser.parse(mutation);
		case SSDL:	return SSDL_parser.parse(mutation);
		case SBRC:	return SBRC_parser.parse(mutation);
		case SCRB:	return SCRB_parser.parse(mutation);
		case SDWD:	return SDWD_parser.parse(mutation);
		case SWDD:	return SWDD_parser.parse(mutation);
		case SSWM:	return SSWM_parser.parse(mutation);
		case SMTC:	return SMTC_parser.parse(mutation);
		/* unary mutations */
		case OPPO:	return OPPO_parser.parse(mutation);
		case OMMO:	return OMMO_parser.parse(mutation);
		case UIOI:	return UIOI_parser.parse(mutation);
		case OBNG:	return OBNG_parser.parse(mutation);
		case OCNG:	return OCNG_parser.parse(mutation);
		case OLNG:	return OLNG_parser.parse(mutation);
		case ONDU: 	return ONDU_parser.parse(mutation);
		/* arithmetic mutations */
		case OAAN:	return OAAN_parser.parse(mutation);
		case OABN:	return OABN_parser.parse(mutation);
		case OALN:	return OALN_parser.parse(mutation);
		case OARN:	return OARN_parser.parse(mutation);
		case OASN:	return OASN_parser.parse(mutation);
		/* bitwise mutations */
		case OBAN:	return OBAN_parser.parse(mutation);
		case OBBN:	return OBBN_parser.parse(mutation);
		case OBLN:	return OBLN_parser.parse(mutation);
		case OBRN:	return OBRN_parser.parse(mutation);
		case OBSN:	return OBSN_parser.parse(mutation);
		/* logical mutations */
		case OLAN:	return OLAN_parser.parse(mutation);
		case OLBN:	return OLBN_parser.parse(mutation);
		case OLLN:	return OLLN_parser.parse(mutation);
		case OLRN:	return OLRN_parser.parse(mutation);
		case OLSN:	return OLSN_parser.parse(mutation);
		/* relation mutations */
		case ORAN:	return ORAN_parser.parse(mutation);
		case ORBN:	return ORBN_parser.parse(mutation);
		case ORLN:	return ORLN_parser.parse(mutation);
		case ORRN:	return ORRN_parser.parse(mutation);
		case ORSN:	return ORSN_parser.parse(mutation);
		/* shifting mutations */
		case OSAN:	return OSAN_parser.parse(mutation);
		case OSBN:	return OSBN_parser.parse(mutation);
		case OSLN:	return OSLN_parser.parse(mutation);
		case OSRN:	return OSRN_parser.parse(mutation);
		case OSSN:	return OSSN_parser.parse(mutation);
		/* assignment mutations */
		case OEAA:	return OEAA_parser.parse(mutation);
		case OEBA:	return OEBA_parser.parse(mutation);
		case OESA:	return OESA_parser.parse(mutation);
		/* arithmetic assignment */
		case OAAA:	return OAAA_parser.parse(mutation);
		case OABA:	return OABA_parser.parse(mutation);
		case OASA:	return OASA_parser.parse(mutation);
		/* bitwise assignment */
		case OBAA:	return OBAA_parser.parse(mutation);
		case OBBA:	return OBBA_parser.parse(mutation);
		case OBSA:	return OBSA_parser.parse(mutation);
		/* shifting assignment */
		case OSAA:	return OSAA_parser.parse(mutation);
		case OSBA:	return OSBA_parser.parse(mutation);
		case OSSA:	return OSSA_parser.parse(mutation);
		/* value mutations */
		case VABS:	return VABS_parser.parse(mutation);
		case VBCR:	return VBCR_parser.parse(mutation);
		case VDTR:	return VDTR_parser.parse(mutation);
		case VTWD:	return VTWD_parser.parse(mutation);
		/* constant mutations */
		case CCCR:	return CCCR_parser.parse(mutation);
		case CCSR:	return CCSR_parser.parse(mutation);
		case CRCR:	return CRCR_parser.parse(mutation);
		/* reference mutations */
		case VARR:	return VARR_parser.parse(mutation);
		case VPRR:	return VPRR_parser.parse(mutation);
		case VSRR:	return VSRR_parser.parse(mutation);
		case VTRR:	return VTRR_parser.parse(mutation);
		case VSFR:	return VSFR_parser.parse(mutation);
		/* unsupported */
		default: throw new IllegalArgumentException("Unsupported mutation: " + mutation.get_mode());
		}
	}
	
}
