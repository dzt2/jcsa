package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDimension;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterBody;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstPointer;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class AstAbsDeclaratorImpl extends AstFixedNode implements AstAbsDeclarator {

	private DeclaratorProduction production;

	public AstAbsDeclaratorImpl(AstPointer pointer) throws Exception {
		super(1);

		this.set_child(0, pointer);
		production = DeclaratorProduction.pointer_declarator;
	}

	public AstAbsDeclaratorImpl(AstPointer pointer, AstAbsDeclarator declarator) throws Exception {
		super(2);

		this.set_child(0, pointer);
		this.set_child(1, declarator);
		production = DeclaratorProduction.pointer_declarator;
	}

	public AstAbsDeclaratorImpl(AstDimension dimension) throws Exception {
		super(1);

		this.set_child(0, dimension);
		production = DeclaratorProduction.declarator_dimension;
	}

	public AstAbsDeclaratorImpl(AstAbsDeclarator declarator, AstDimension dimension) throws Exception {
		super(2);

		this.set_child(1, dimension);
		this.set_child(0, declarator);
		production = DeclaratorProduction.declarator_dimension;
	}

	public AstAbsDeclaratorImpl(AstParameterBody parambody) throws Exception {
		super(1);

		this.set_child(0, parambody);
		production = DeclaratorProduction.declarator_parambody;
	}

	public AstAbsDeclaratorImpl(AstAbsDeclarator declarator, AstParameterBody parambody) throws Exception {
		super(2);

		this.set_child(1, parambody);
		this.set_child(0, declarator);
		production = DeclaratorProduction.declarator_parambody;
	}

	public AstAbsDeclaratorImpl(AstPunctuator lparanth, AstAbsDeclarator declarator, AstPunctuator rparanth)
			throws Exception {
		super(3);

		if (lparanth == null || lparanth.get_punctuator() != CPunctuator.left_paranth)
			throw new IllegalArgumentException("Invalid lparanth: not left-paranth");
		else if (rparanth == null || rparanth.get_punctuator() != CPunctuator.right_paranth)
			throw new IllegalArgumentException("Invalid rparanth: not right-paranth");
		this.set_child(0, lparanth);
		this.set_child(1, declarator);
		this.set_child(2, rparanth);
		production = DeclaratorProduction.lp_declarator_rp;
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
			return (AstDimension) children[children.length - 1];
	}

	@Override
	public AstParameterBody get_parameter_body() {
		if (production != DeclaratorProduction.declarator_parambody)
			throw new IllegalArgumentException("Invaid access: no-parambody");
		else
			return (AstParameterBody) children[children.length - 1];
	}

	@Override
	public AstPunctuator get_lparanth() {
		if (production != DeclaratorProduction.lp_declarator_rp)
			throw new IllegalArgumentException("Invalid access: no left-paranth");
		else
			return (AstPunctuator) children[0];
	}

	@Override
	public AstPunctuator get_rparanth() {
		if (production != DeclaratorProduction.lp_declarator_rp)
			throw new IllegalArgumentException("Invalid access: no right-paranth");
		else
			return (AstPunctuator) children[2];
	}

	@Override
	public AstAbsDeclarator get_declarator() {
		switch (production) {
		case pointer_declarator:
			if (this.number_of_children() == 2)
				return (AstAbsDeclarator) children[1];
			else
				return null;
		case declarator_dimension:
			if (this.number_of_children() == 2)
				return (AstAbsDeclarator) children[0];
			else
				return null;
		case declarator_parambody:
			if (this.number_of_children() == 2)
				return (AstAbsDeclarator) children[0];
			else
				return null;
		case lp_declarator_rp:
			return (AstAbsDeclarator) children[1];
		default:
			throw new IllegalArgumentException("Fail to access deep declarator");
		}
	}

}
