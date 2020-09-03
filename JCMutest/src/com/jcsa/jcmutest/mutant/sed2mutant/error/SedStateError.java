package com.jcsa.jcmutest.mutant.sed2mutant.error;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.stmt.SedStatement;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.token.SedLabel;
import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * It provides a well-defined structural description of the state errors 
 * occurs in the program during testing in form of SedNode.<br>
 * <br>
 * <code>
 * 	+-----------------------------------------------------------------+	<br>
 * 	SedStateError						{location{stmt|expr, loca_type}	<br>
 * 	|--	SedStatementError				{orig_statement: SedLabel}		<br>
 * 	|--	|--	SedAddStatementError		add_stmt(stmt)					<br>
 * 	|--	|--	SedDelStatementError		del_stmt(stmt)					<br>
 * 	|--	|--	SedSetStatementError		set_stmt(stmt, stmt)			<br>
 * 	|--	SedExpressionError				{orig_expression: SetExpr}		<br>
 * 	|--	|--	SedAbstractExpressionError									<br>
 * 	|--	|--	|--	SedInsExpressionError	ins_expr(expr, oprt)			<br>
 * 	|--	|--	|--	SedAppExpressionError	app_expr(e, o, e)				<br>
 * 	|--	|--	|--	SedMutExpressionError	mut_expr(expr, expr)			<br>
 * 	|--	|--	SedConcreteExpressionError									<br>
 * 	|--	|--	|--	SedNegExpressionError	neg_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedRsvExpressionError	rsv_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedAddExpressionError	add_{char|sign|usign|real|addr}	<br>
 * 	|--	|--	|--	SedMulExpressionError 	mul_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedAndExpressionError	and_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedIorExpressionError	ior_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedXorExpressionError	xor_{char|sign|usign}			<br>
 * 	|--	|--	|--	SedIncExpressionError	inc_{char|sign|usign|real|addr}	<br>
 * 	|--	|--	|--	SedDecExpressionError	dec_{char|sign|usign|real|addr}	<br>
 * 	|--	|--	|--	SedExtExpressionError	ext_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedShkExpressionError	shk_{char|sign|usign|real}		<br>
 * 	|--	|--	|--	SedSetExpressionError	set_{bool|char|sign..|addr|list}<br>
 * 	|--	|--	|--	SedChgExpressionError	chg_{bool|char|sign..|addr|list}<br>
 * 	+-----------------------------------------------------------------+	<br>
 * </code>
 * <br>
 * @author dzt2
 *
 */
public abstract class SedStateError {
	
	private static final String StateErrorHead = "seed#%s::%s";
	
	/* definitions */
	/** the statement where the state error occurs **/
	private SedLabel statement;
	/**
	 * @param statement
	 * @return the sed-description of the statement with executional node
	 * @throws Exception
	 */
	protected SedLabel get_sed_statement(CirStatement statement) throws Exception {
		if(statement == null)
			throw new IllegalArgumentException("Invalid statement: null");
		else {
			SedLabel location = new SedLabel(null, statement);
			if(!location.has_cir_execution())
				throw new IllegalArgumentException("Not executional node");
			else
				return location;
		}
	}
	/**
	 * create a state error seeded in the specified statement
	 * @param statement
	 * @throws Exception
	 */
	protected SedStateError(CirStatement statement) throws Exception {
		this.statement = this.get_sed_statement(statement);
	}
	
	/* getters */
	/**
	 * @return the statement where the state error occurs
	 */
	public SedLabel get_statement() { return this.statement; }
	@Override
	public String toString() {
		try {
			return this.generate_code();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @return the code generated to describe the state error
	 * @throws Exception
	 */
	public String generate_code() throws Exception {
		return String.format(StateErrorHead, this.statement.generate_code(), this.generate_content());
	}
	/**
	 * @return the content of the core of the state error
	 * @throws Exception
	 */
	protected abstract String generate_content() throws Exception;
	
	/* location type generation */
	/**
	 * @param node
	 * @return the type of the location where the error being seeded
	 * @throws Exception
	 */
	public static SedLocationType location_type(SedNode node) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(node instanceof SedLabel || node instanceof SedStatement)
			return SedLocationType.cstmt;
		else if(node instanceof SedExpression) {
			CType data_type = ((SedExpression) node).get_data_type();
			if(data_type == null) {
				return SedLocationType.cvoid;
			}
			else {
				data_type = CTypeAnalyzer.get_value_type(data_type);
				if(data_type instanceof CBasicType) {
					switch(((CBasicType) data_type).get_tag()) {
					case c_void:		return SedLocationType.cvoid;
					case c_bool:		return SedLocationType.cbool;
					case c_char:
					case c_uchar:		return SedLocationType.cchar;
					case c_short:
					case c_int:
					case c_long:
					case c_llong:		return SedLocationType.csign;
					case c_ushort:
					case c_uint:
					case c_ulong:
					case c_ullong:		return SedLocationType.usign;
					case c_float:
					case c_double:
					case c_ldouble:		return SedLocationType.creal;
					default: 			return SedLocationType.clist;
					}
				}
				else if(data_type instanceof CArrayType
						|| data_type instanceof CPointerType) {
					return SedLocationType.caddr;
				}
				else if(data_type instanceof CFunctionType) {
					return SedLocationType.cfunc;
				}
				else if(data_type instanceof CStructType
						|| data_type instanceof CUnionType) {
					return SedLocationType.clist;
				}
				else {
					throw new IllegalArgumentException(data_type.generate_code());
				}
			}
		}
		else
			throw new IllegalArgumentException("Unsupport: " + node);
	}
	
}
