package com.jcsa.jcparse.lang.astree.impl.unit;

import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.stmt.AstCompoundStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.scope.CScope;

public class AstFunctionDefinitionImpl extends AstFixedNode implements AstFunctionDefinition {

	public AstFunctionDefinitionImpl(AstDeclarationSpecifiers specifiers, AstDeclarator declarator,
			AstDeclarationList dlist, AstCompoundStatement body) throws Exception {
		super(4);

		this.set_child(0, specifiers);
		this.set_child(1, declarator);
		this.set_child(2, dlist);
		this.set_child(3, body);
	}

	public AstFunctionDefinitionImpl(AstDeclarationSpecifiers specifiers, AstDeclarator declarator,
			AstCompoundStatement body) throws Exception {
		super(3);

		this.set_child(0, specifiers);
		this.set_child(1, declarator);
		this.set_child(2, body);
	}

	@Override
	public AstDeclarationSpecifiers get_specifiers() {
		return (AstDeclarationSpecifiers) children[0];
	}

	@Override
	public AstDeclarator get_declarator() {
		return (AstDeclarator) children[1];
	}

	@Override
	public AstDeclarationList get_declaration_list() {
		if (children.length != 4)
			throw new IllegalArgumentException("Invalid access: no-declaration-list");
		else
			return (AstDeclarationList) children[2];
	}

	@Override
	public AstCompoundStatement get_body() {
		return (AstCompoundStatement) children[children.length - 1];
	}

	@Override
	public boolean has_declaration_list() {
		return children.length == 4;
	}

	protected CScope scope;

	@Override
	public CScope get_scope() {
		return scope;
	}

	@Override
	public void set_scope(CScope scope) {
		this.scope = scope;
	}
}
