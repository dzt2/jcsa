package com.jcsa.jcmutest.mutant.sel2mutant.lang;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * <code>
 * 	|--	SelDescription					{statement; keyword}				<br>
 * 	|--	|--	SelConstraint													<br>
 * 	|--	|--	SelStatementError												<br>
 * 	|--	|--	SelExpressionError												<br>
 * 	|--	|--	SelTypedValueError												<br>
 * 	|--	|--	SelDescriptions													<br>
 * </code>
 * @author yukimula
 *
 */
public abstract class SelDescription extends SelNode {
	
	protected SelDescription(CirStatement statement, 
			SelKeywords keyword) throws Exception {
		this.add_child(new SelStatement(statement));
		this.add_child(new SelKeyword(keyword));
	}
	
	/**
	 * @return the statement where the description is made
	 */
	public SelStatement get_statement() { return (SelStatement) this.get_child(0); }
	
	/**
	 * @return keyword that defines the class of the description node
	 */
	public SelKeyword get_keyword() { return (SelKeyword) this.get_child(1); }

	@Override
	public String generate_code() throws Exception {
		return this.get_statement().generate_code() + ":"
				+ this.get_keyword().generate_code() + 
				this.generate_parameters();
	}
	
	/**
	 * @return execution:keyword[parameter]
	 * @throws Exception
	 */
	protected abstract String generate_parameters() throws Exception;
	
}
