package __backup__;

/**
 * Definition of mutation operators implemented in this tool<br>
 * <br>
 * <b>statement mutation (9)</b><br>
 * 	|-- STRP : <code>trap_on_statement()</code><br>
 * 	|-- STRI : <code>trap_on_true(v)</code><br>
 * 	|-- SSDL : <code>blocks |--> {}</code><br>
 * 	|-- SBRC : <code>break |--> continue</code><br>
 * 	|-- SCRB : <code>continue |--> break</code><br>
 * 	|-- SWDD : <code>while |--> do...while</code><br>
 * 	|-- SDWD : <code>do...while |--> while</code><br>
 * 	|-- SSWM : <code>trap_on_case(expr, 'a')</code><br>
 * 	|-- SMTC : <code>trap_after_times(p, number)</code><br>
 * <br>
 * <b>operator mutation (36 + 30)</b><br>
 * 	|-- OPPO : <code>x++ |--> x-- | ++x</code><br>
 * 	|-- OMMO : <code>--x |--> ++x | x--</code><br>
 * 	|-- UIOI : <code>x |--> x++ | ++x | x-- | --x</code><br>
 * 	|-- OLNG : <code>logic_expr |--> !(logic_expr)</code><br>
 * 	|-- OCNG : <code>if|while|for(!c)</code><br>
 * 	|-- OBNG : <code>bit_expr |--> ~bit_expr</code><br>
 * 	|-- OAAN; OABN; OALN; OARN; OASN;<br>
 * 	|-- OBAN; OBBN; OBLN; OBRN; OBSN;<br>
 * 	|-- OLAN; OLBN; OLLN; OLRN; OLSN;<br>
 * 	|-- ORAN; ORBN; ORLN; ORRN; ORSN;<br>
 * 	|-- OSAN; OSBN; OSLN; OSRN; OSSN;<br>
 * 	|-- OEAN; OEBN; OELN; OERN; OESN;<br>
 * <br>
 * <b>reference mutation (9)</b><br>
 * 	|-- VSRR: scalar reference replace<br>
 * 	|-- VARR: array reference replace<br>
 * 	|-- VIDR: array index replace<br>
 * 	|-- VPRR: pointer reference replace<br>
 * 	|-- VTRR: structure reference replace<br>
 * 	|-- VTFR: structure field replace<br>
 * 	|-- CCCR: constant |--> constant-occurrence<br>
 * 	|-- CCSR: constant |--> scalar reference<br>
 * 	|-- CRCR: constant |--> {0,1,-1,min,max}<br>
 * <br>
 * <b>value mutation (4)</b><br>
 * 	|-- VDTR: trap_on_zero(reference)<br>
 * 	|-- VTWD: succ|pred(reference)<br>
 * 	|-- VABS: abs_value(reference)<br>
 * 	|-- VBCR: bool_expr |--> {true | false}<br>
 * <br>
 * @author yukimula
 */
public enum MutOperator {
	
	/* statement mutation */
	/** trap_on_statement **/			STRP,
	/** trap_on_false(pr) **/			STRI,
	/** trap_on_false(cd) **/			STRC,
	/** block |--> {} **/				SSDL,
	/** break |--> continue **/			SBRC,
	/** continue |--> break **/			SCRB,
	/** while |--> do.while **/			SWDD,
	/** do.while |--> while **/			SDWD,
	/** trap_on_case(e,val) **/			SSWM,
	/** trap_after_times(pr, n) **/		SMTC,
	
	/* operator mutation */
	/** x++ --> ++x | x-- **/			OPPO,
	/** x-- --> --x | x++ **/			OMMO,
	/** x --> ++x | --x | x++ | x-- **/	UIOI,
	/** bit_expr |--> ~(bit_expr) **/	OBNG,
	/** pr_expr |--> !(pr_expr) **/		OCNG,
	/** log_expr |--> !(log_expr) **/	OLNG,
	/** (-, ~, !)expr |--> expr **/		ONDU,
	
	OAAN, OABN, OALN, OARN, OASN,
	OBAN, OBBN, OBLN, OBRN, OBSN,
	OLAN, OLBN, OLLN, OLRN, OLSN,
	ORAN, ORBN, ORLN, ORRN, ORSN,
	OSAN, OSBN, OSLN, OSRN, OSSN,
	OEAA, OEBA, OESA, 
	OAAA, OABA, OASA, 
	OBAA, OBBA, OBSA, 
	OSAA, OSBA, OSSA, 
	
	/* reference mutation */
	/** cst --> {0,1,-1,c+1,c-1,-c} **/		CRCR,
	/** cst --> cst' in const_pool **/		CCCR,
	/** cst --> var in scalar_reference **/	CCSR,
	/** array --> another-array **/			VARR,
	/** ptr --> ptr' **/					VPRR,
	/** scalar-ref --> scalar-ref **/		VSRR,	// unable to parse
	/** st.field --> st.field2 **/			VSFR,
	/** st --> st' **/						VTRR,
	
	/* value reference */
	/** abs_value(ref) **/					VABS,
	/** bool_expr |--> true | false **/		VBCR,
	/** trap_on_zero(ref) **/				VDTR,
	/** succ(ref) | pred(ref) **/			VTWD,
}
