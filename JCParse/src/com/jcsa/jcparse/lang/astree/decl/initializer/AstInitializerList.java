package com.jcsa.jcparse.lang.astree.decl.initializer;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;

/**
 * <code>field_initializer (, field_initializer)*</code>
 *
 * @author yukimula
 *
 */
public interface AstInitializerList extends AstNode {
	public int number_of_initializer();

	public AstFieldInitializer get_initializer(int k);

	public AstPunctuator get_comma(int k);

	public void append(AstPunctuator comma, AstFieldInitializer init) throws Exception;
}
