package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDimension;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterBody;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstPointer;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstDeclaratorImpl extends AstFixedNode implements AstDeclarator {

	private DeclaratorProduction production;

	public AstDeclaratorImpl(AstPointer pointer, AstDeclarator declarator) throws Exception {
		super(2);
		this.set_child(0, pointer);
		this.set_child(1, declarator);
		this.production = DeclaratorProduction.pointer_declarator;
	}

	public AstDeclaratorImpl(AstDeclarator declarator, AstDimension dimension) throws Exception {
		super(2);
		this.set_child(0, declarator);
		this.set_child(1, dimension);
		this.production = DeclaratorProduction.declarator_dimension;
	}

	public AstDeclaratorImpl(AstDeclarator declarator, AstParameterBody parambody) throws Exception {
		super(2);
		this.set_child(0, declarator);
		this.set_child(1, parambody);
		this.production = DeclaratorProduction.declarator_parambody;
	}

	public AstDeclaratorImpl(AstPunctuator lparanth, AstDeclarator declarator, AstPunctuator rparanth)
			throws Exception {
		super(3);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: not-left-paranth");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: not-right-paranth");
		else {
			this.set_child(0, lparanth);
			this.set_child(1, declarator);
			this.set_child(2, rparanth);
			this.production = DeclaratorProduction.lp_declarator_rp;
		}
	}

	public AstDeclaratorImpl(AstName name) throws Exception {
		super(1);
		this.set_child(0, name);
		production = DeclaratorProduction.identifier;
	}

	@Override
	public DeclaratorProduction get_production() {
		return production;
	}

	@Override
	public AstPointer get_pointer() {
		if (production != DeclaratorProduction.pointer_declarator)
			throw new IllegalArgumentException("Invalid access: no-pointer");
		else
			return (AstPointer) children[0];
	}

	@Override
	public AstDimension get_dimension() {
		if (production != DeclaratorProduction.declarator_dimension)
			throw new IllegalArgumentException("Invalid access: no-dimension");
		else
			return (AstDimension) children[1];
	}

	@Override
	public AstParameterBody get_parameter_body() {
		if (production != DeclaratorProduction.declarator_parambody)
			throw new IllegalArgumentException("Invalid access: no-parambody");
		else
			return (AstParameterBody) children[1];
	}

	@Override
	public AstPunctuator get_lparanth() {
		if (production != DeclaratorProduction.lp_declarator_rp)
			throw new IllegalArgumentException("Invalid access: no-paranth");
		else
			return (AstPunctuator) children[0];
	}

	@Override
	public AstPunctuator get_rparanth() {
		if (production != DeclaratorProduction.lp_declarator_rp)
			throw new IllegalArgumentException("Invalid access: no-paranth");
		else
			return (AstPunctuator) children[2];
	}

	@Override
	public AstName get_identifier() {
		if (production != DeclaratorProduction.identifier)
			throw new IllegalArgumentException("Invalid access: no-identifier");
		else
			return (AstName) children[0];
	}

	@Override
	public AstDeclarator get_declarator() {
		switch (production) {
		case pointer_declarator:
			return (AstDeclarator) children[1];
		case declarator_dimension:
			return (AstDeclarator) children[0];
		case declarator_parambody:
			return (AstDeclarator) children[0];
		case lp_declarator_rp:
			return (AstDeclarator) children[1];
		case identifier:
			return null;
		default:
			throw new IllegalArgumentException("Unknown production: " + production);
		}
	}

}
