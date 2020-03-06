package com.jcsa.jcparse.lang.parse.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.decl.AstDeclaration;
import com.jcsa.jcparse.lang.astree.decl.AstTypeName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstAbsDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDimension;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstIdentifierList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclarator;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstInitDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstName;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterBody;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterDeclaration;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstParameterTypeList;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstPointer;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstDeclarator.DeclaratorProduction;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstDeclarationSpecifiers;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumerator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstEnumeratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstFunctionQualifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstSpecifierQualifierList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStorageClass;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaration;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarationList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclarator;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructDeclaratorList;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructSpecifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStructUnionBody;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeQualifier;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypedefName;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstUnionSpecifier;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.stmt.AstDeclarationStatement;
import com.jcsa.jcparse.lang.astree.unit.AstDeclarationList;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.centity.CInstance;
import com.jcsa.jcparse.lang.centity.impl.CEntityFactory;
import com.jcsa.jcparse.lang.ctype.CBasicType;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.ctype.CFunctionType;
import com.jcsa.jcparse.lang.ctype.CParameterTypeList;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.ctype.impl.CTypeFactory;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CStorageClass;
import com.jcsa.jcparse.lang.lexical.CTypeQualifier;
import com.jcsa.jcparse.lang.scope.CEnumTypeName;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CFieldName;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CStructTypeName;
import com.jcsa.jcparse.lang.scope.CTypedefName;
import com.jcsa.jcparse.lang.scope.CUnionTypeName;
import com.jcsa.jcparse.lang.text.CLocation;

/**
 * To analyze the declaration in C program and derive types from them
 * 
 * @author yukimula
 */
public class CTypeBuilder {
	
	/** to construct type for each declaration **/
	protected CTypeFactory factory;
	/** to construct entity for each type **/
	protected CEntityFactory efactory;
	/** to evaluate values of const-expression **/
	protected CConstantEvaluator evaluator;

	/**
	 * constructor
	 */
	public CTypeBuilder(CConstantEvaluator evaluator) {
		if (evaluator == null)
			throw new IllegalArgumentException("Invalid evaluator: null");

		factory = new CTypeFactory();
		efactory = new CEntityFactory();
		this.evaluator = evaluator;
		type_keywords = new ArrayList<AstTypeKeyword>();
	}

	/**
	 * to build up basic type from the sequence of type keywords
	 * 
	 * @param keywords
	 * @return : null if the type keywords are incorrect
	 * @throws Exception
	 */
	protected CBasicType build_up(Collection<AstTypeKeyword> keywords) throws Exception {
		CKeyword[] kw_list = new CKeyword[4];
		for (int i = 0; i < 4; i++)
			kw_list[i] = null;

		for (AstTypeKeyword keyword : keywords) {
			CKeyword kw = keyword.get_keyword().get_keyword();
			switch (kw) {
			case c89_void:
			case c99_bool:
			case c89_char:
			case c89_int:
			case c89_float:
			case c89_double:
			case gnu_builtin_va_list:
				if (kw_list[0] == null) {
					kw_list[0] = kw;
					break;
				} else
					throw new RuntimeException("Duplicated type keyword at line " + this.line_of(keyword) + " : \""
							+ this.code_of(keyword) + "\"");
			case c89_short:
				if (kw_list[1] == null) {
					kw_list[1] = kw;
					break;
				} else
					throw new RuntimeException("Invalid type keyword at line " + this.line_of(keyword) + " : \""
							+ this.code_of(keyword) + "\"");
			case c89_long:
				if (kw_list[1] == null) {
					kw_list[1] = kw;
					break;
				} else if (kw_list[2] == null) {
					kw_list[2] = kw;
					break;
				} else
					throw new RuntimeException("Too many type keyword at line " + this.line_of(keyword) + " : \""
							+ this.code_of(keyword) + "\"");
			case c89_signed:
			case c89_unsigned:
				if (kw_list[3] == null) {
					kw_list[3] = kw;
					break;
				} else
					throw new RuntimeException("Duplicated type keyword at line " + this.line_of(keyword) + " : \""
							+ this.code_of(keyword) + "\"");
			case c99_complex:
			case c99_imaginary:
				if (kw_list[3] == null) {
					kw_list[3] = kw;
					break;
				} else
					throw new RuntimeException("Duplicated type keyword at line " + this.line_of(keyword) + " : \""
							+ this.code_of(keyword) + "\"");
			default:
				break;
			}
		}

		CBasicType type;
		if (kw_list[0] == CKeyword.c89_void) {
			if (kw_list[1] == null && kw_list[2] == null && kw_list[3] == null)
				type = CBasicTypeImpl.void_type;
			else
				type = null;
		} else if (kw_list[0] == CKeyword.c99_bool) {
			if (kw_list[1] == null && kw_list[2] == null && kw_list[3] == null)
				type = CBasicTypeImpl.bool_type;
			else
				type = null;
		} else if (kw_list[0] == CKeyword.gnu_builtin_va_list) {
			if (kw_list[1] == null && kw_list[2] == null && kw_list[3] == null)
				type = CBasicTypeImpl.gnu_va_list_type;
			else
				type = null;
		} else if (kw_list[0] == CKeyword.c89_char) {
			if (kw_list[1] == null && kw_list[2] == null) {
				if (kw_list[3] == CKeyword.c89_unsigned)
					type = CBasicTypeImpl.uchar_type;
				else
					type = CBasicTypeImpl.char_type;
			} else
				type = null;
		} else if (kw_list[0] == CKeyword.c89_float) {
			if (kw_list[2] == null && kw_list[1] == null) {
				if (kw_list[3] == CKeyword.c99_complex)
					type = CBasicTypeImpl.float_complex_type;
				else if (kw_list[1] == CKeyword.c99_imaginary)
					type = CBasicTypeImpl.float_imaginary_type;
				else
					type = CBasicTypeImpl.float_type;
			} else
				type = null;
		} else if (kw_list[0] == CKeyword.c89_double) {
			if (kw_list[2] == null && kw_list[1] == null) {
				if (kw_list[3] == CKeyword.c99_complex)
					type = CBasicTypeImpl.double_complex_type;
				else if (kw_list[1] == CKeyword.c99_imaginary)
					type = CBasicTypeImpl.double_imaginary_type;
				else
					type = CBasicTypeImpl.double_type;
			} else if (kw_list[2] == null && kw_list[1] == CKeyword.c89_long) {
				if (kw_list[3] == CKeyword.c99_complex)
					type = CBasicTypeImpl.ldouble_complex_type;
				else if (kw_list[1] == CKeyword.c99_imaginary)
					type = CBasicTypeImpl.ldouble_imaginary_type;
				else
					type = CBasicTypeImpl.ldouble_type;
			} else
				type = null;
		} else {
			if (kw_list[3] == CKeyword.c89_unsigned) {
				if (kw_list[1] == CKeyword.c89_short) {
					if (kw_list[2] == null)
						type = CBasicTypeImpl.ushort_type;
					else
						type = null;
				} else if (kw_list[1] == CKeyword.c89_long) {
					if (kw_list[2] == CKeyword.c89_long)
						type = CBasicTypeImpl.ullong_type;
					else
						type = CBasicTypeImpl.ulong_type;
				} else
					type = CBasicTypeImpl.uint_type;
			} else {
				if (kw_list[1] == CKeyword.c89_short) {
					if (kw_list[2] == null)
						type = CBasicTypeImpl.short_type;
					else
						type = null;
				} else if (kw_list[1] == CKeyword.c89_long) {
					if (kw_list[2] == CKeyword.c89_long)
						type = CBasicTypeImpl.llong_type;
					else
						type = CBasicTypeImpl.long_type;
				} else
					type = CBasicTypeImpl.int_type;
			}
		}

		if (type == null) {
			AstNode head = keywords.iterator().next();
			throw new RuntimeException("Invalid specifiers at line " + this.line_of(head));
		} else
			return type;
	}

	/**
	 * to build up struct-type from struct specifier
	 * 
	 * @param specifier
	 * @return : with empty body if not defined | original instance if declared
	 *         | updated if defined
	 * @throws Exception
	 */
	protected CStructType build_up(AstStructSpecifier specifier) throws Exception {
		/* get children of the specifier */
		AstName name = null;
		AstStructUnionBody body = null;
		if (specifier.has_name())
			name = specifier.get_name();
		if (specifier.has_body())
			body = specifier.get_body();

		/* get the type from name and update scope cname */
		CStructType type;
		if (name != null) {
			CStructTypeName cname = (CStructTypeName) name.get_cname();
			if (cname.get_type() != null)
				type = (CStructType) cname.get_type();
			else {
				type = factory.get_struct_type(name.get_name());
				cname.set_type(type);
			}
		} 
		else type = factory.get_struct_type(null);

		/* implement the type expression */
		if (body != null) {
			if (name != null && (name != name.get_cname().get_source() || type.is_defined()))
				throw new RuntimeException("Duplicated definition at line : " + this.line_of(body));
			else if (body.has_declaration_list()) {
				AstStructDeclarationList dlist = body.get_declaration_list();
				int n = dlist.number_of_declarations();
				for (int i = 0; i < n; i++)
					this.build_up(dlist.get_declaration(i), type);
			}
		}

		return type; // return
	}

	/**
	 * to build up union-type for union specifier
	 * 
	 * @param specifier
	 * @return : with empty body if not defined | original instance if declared
	 *         | updated if defined
	 * @throws Exception
	 */
	protected CUnionType build_up(AstUnionSpecifier specifier) throws Exception {
		/* get children of the specifier */
		AstName name = null;
		AstStructUnionBody body = null;
		if (specifier.has_name())
			name = specifier.get_name();
		if (specifier.has_body())
			body = specifier.get_body();

		/* get the type from name and update scope cname */
		CUnionType type;
		if (name != null) {
			CUnionTypeName cname = (CUnionTypeName) name.get_cname();
			if (cname.get_type() != null)
				type = (CUnionType) cname.get_type();
			else {
				type = factory.get_union_type(name.get_name());
				cname.set_type(type);
			}
		} 
		else
			type = factory.get_union_type(null);

		/* implement the type expression */
		if (body != null) {
			if (name != null && (name != name.get_cname().get_source() || type.is_defined()))
				throw new RuntimeException("Duplicated definition at line : " + this.line_of(body));
			else if (body.has_declaration_list()) {
				AstStructDeclarationList dlist = body.get_declaration_list();
				int n = dlist.number_of_declarations();
				for (int i = 0; i < n; i++)
					this.build_up(dlist.get_declaration(i), type);
			}
		}

		return type; // return
	}

	/**
	 * to build up enum-type based on enum specifier
	 * 
	 * @param specifier
	 * @return
	 * @throws Exception
	 */
	protected CEnumType build_up(AstEnumSpecifier specifier) throws Exception {
		/* get the children of specifier node */
		AstName name = null;
		AstEnumeratorBody body = null;
		if (specifier.has_name())
			name = specifier.get_name();
		if (specifier.has_body())
			body = specifier.get_body();

		/* get | create enum type for the specifier */
		CEnumType type;
		if (name != null) {
			CEnumTypeName cname = (CEnumTypeName) name.get_cname();
			if (cname.get_type() != null)
				type = (CEnumType) cname.get_type();
			else {
				type = factory.get_enum_type(name.get_name());
				cname.set_type(type);
			}
		} else
			type = factory.get_enum_type(null);

		/* compute the enum-type from body */
		if (body != null) {
			if (name != null && (name != name.get_cname().get_source() || type.is_defined()))
				throw new RuntimeException("Duplicated definition at line : " + this.line_of(body));
			else {
				AstEnumeratorList elist = body.get_enumerator_list();
				int n = elist.number_of_enumerators();
				for (int i = 0; i < n; i++)
					this.build_up(elist.get_enumerator(i), type);
			}
		}

		return type; // return
	}

	/**
	 * to get the type of typedef-name
	 * 
	 * @param specifier
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstTypedefName specifier) throws Exception {
		CTypedefName cname = (CTypedefName) specifier.get_cname();
		if (cname == null || cname.get_type() == null)
			throw new RuntimeException(
					"Undefined typedef-name at line " + this.line_of(specifier) + " : " + this.code_of(specifier));
		else
			return cname.get_type();
	}

	/** list of type keywords **/
	protected List<AstTypeKeyword> type_keywords;

	/**
	 * to build up type for the specifiers before declarator(s), where
	 * storage-class specifier is not considered.
	 * 
	 * @param specifiers
	 * @return : [CType; CStorageClass]
	 * @throws Exception
	 */
	protected Object[] build_up(AstDeclarationSpecifiers specifiers) throws Exception {
		/* initialization */
		boolean is_const = false, is_volatile = false;
		boolean is_restrict = false, is_inline = false;
		AstStorageClass storage_specifier = null;
		type_keywords.clear();

		/* collecting specifiers */
		CType base = null;
		int n = specifiers.number_of_specifiers();
		for (int i = 0; i < n; i++) {
			AstSpecifier specifier = specifiers.get_specifier(i);
			if (specifier instanceof AstTypeKeyword) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifier) + "\"");
				else
					type_keywords.add((AstTypeKeyword) specifier);
			} else if (specifier instanceof AstStructSpecifier) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstStructSpecifier) specifier);
			} else if (specifier instanceof AstUnionSpecifier) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstUnionSpecifier) specifier);
			} else if (specifier instanceof AstEnumSpecifier) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstEnumSpecifier) specifier);
			} else if (specifier instanceof AstTypedefName) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstTypedefName) specifier);
			} else if (specifier instanceof AstTypeQualifier) {
				switch (((AstTypeQualifier) specifier).get_keyword().get_keyword()) {
				case c89_const:
					is_const = true;
					break;
				case c89_volatile:
					is_volatile = true;
					break;
				case c99_restrict:
					is_restrict = true;
					break;
				default:
					throw new RuntimeException("Invalid type qualifier at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				}
			} else if (specifier instanceof AstFunctionQualifier) {
				switch (((AstFunctionQualifier) specifier).get_keyword().get_keyword()) {
				case c99_inline:
					is_inline = true;
					break;
				default:
					throw new RuntimeException("Invalid type qualifier at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				}
			} else if (specifier instanceof AstStorageClass) {
				if (storage_specifier != null)
					throw new RuntimeException("Duplicated storage-class at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					storage_specifier = (AstStorageClass) specifier;
			} else
				throw new RuntimeException("Invalid specifier: " + specifier.getClass().getSimpleName());
		}
		if (base == null)
			base = this.build_up(type_keywords);

		/* qualifier collecting */
		if (is_const)
			base = factory.get_qualifier_type(CTypeQualifier.c_const, base);
		if (is_volatile)
			base = factory.get_qualifier_type(CTypeQualifier.c_volatile, base);
		if (is_restrict)
			base = factory.get_qualifier_type(CTypeQualifier.c_restrict, base);
		if (is_inline)
			base = factory.get_qualifier_type(CTypeQualifier.c_inline, base);

		/* storage classification */
		CStorageClass storage = null;
		if (storage_specifier != null) {
			switch (storage_specifier.get_keyword().get_keyword()) {
			case c89_auto:
				storage = CStorageClass.c_auto;
				break;
			case c89_register:
				storage = CStorageClass.c_register;
				break;
			case c89_static:
				storage = CStorageClass.c_static;
				break;
			case c89_extern:
				storage = CStorageClass.c_extern;
				break;
			case c89_typedef:
				storage = CStorageClass.c_typedef;
				break;
			default:
				throw new RuntimeException("Invalid storage-class at line " + this.line_of(storage_specifier) + " : \""
						+ this.code_of(specifiers) + "\"");
			}
		}

		/* return */
		Object[] ans = new Object[2];
		ans[0] = base;
		ans[1] = storage;
		return ans;
	}

	/**
	 * build up the type for specifier qualifier list in AST
	 * 
	 * @param specifiers
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstSpecifierQualifierList specifiers) throws Exception {
		/* initialization */
		boolean is_const = false, is_volatile = false;
		boolean is_restrict = false, is_inline = false;
		type_keywords.clear();

		/* collecting specifiers */
		CType base = null;
		int n = specifiers.number_of_specifiers();
		for (int i = 0; i < n; i++) {
			AstSpecifier specifier = specifiers.get_specifier(i);
			if (specifier instanceof AstTypeKeyword) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifier) + "\"");
				else
					type_keywords.add((AstTypeKeyword) specifier);
			} else if (specifier instanceof AstStructSpecifier) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstStructSpecifier) specifier);
			} else if (specifier instanceof AstUnionSpecifier) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstUnionSpecifier) specifier);
			} else if (specifier instanceof AstEnumSpecifier) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstEnumSpecifier) specifier);
			} else if (specifier instanceof AstTypedefName) {
				if (base != null)
					throw new RuntimeException("Duplicated type definition at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				else
					base = this.build_up((AstTypedefName) specifier);
			} else if (specifier instanceof AstTypeQualifier) {
				switch (((AstTypeQualifier) specifier).get_keyword().get_keyword()) {
				case c89_const:
					is_const = true;
					break;
				case c89_volatile:
					is_volatile = true;
					break;
				case c99_restrict:
					is_restrict = true;
					break;
				default:
					throw new RuntimeException("Invalid type qualifier at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				}
			} else if (specifier instanceof AstFunctionQualifier) {
				switch (((AstFunctionQualifier) specifier).get_keyword().get_keyword()) {
				case c99_inline:
					is_inline = true;
					break;
				default:
					throw new RuntimeException("Invalid type qualifier at line " + this.line_of(specifier) + " : \""
							+ this.code_of(specifiers) + "\"");
				}
			} else
				throw new RuntimeException("Invalid specifier: " + specifier.getClass().getSimpleName());
		}
		if (base == null)
			base = this.build_up(type_keywords);

		/* qualifier collecting */
		if (is_const)
			base = factory.get_qualifier_type(CTypeQualifier.c_const, base);
		if (is_volatile)
			base = factory.get_qualifier_type(CTypeQualifier.c_volatile, base);
		if (is_restrict)
			base = factory.get_qualifier_type(CTypeQualifier.c_restrict, base);
		if (is_inline)
			base = factory.get_qualifier_type(CTypeQualifier.c_inline, base);

		/* return */ return base;
	}

	/**
	 * build up the type from specifier type and pointer in declarator
	 * 
	 * @param pointer
	 * @param base
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstPointer pointer, CType base) throws Exception {
		int n = pointer.number_of_keywords();
		for (int i = 0; i < n; i++) {
			AstNode child = pointer.get_specifier(i);
			if (child instanceof AstPunctuator) {
				switch (((AstPunctuator) child).get_punctuator()) {
				case ari_mul:
					base = factory.get_pointer_type(base);
					break;
				default:
					throw new RuntimeException("Invalid pointer at line " + this.line_of(pointer));
				}
			} else if (child instanceof AstKeyword) {
				switch (((AstKeyword) child).get_keyword()) {
				case c89_const:
					base = factory.get_qualifier_type(CTypeQualifier.c_const, base);
					break;
				case c89_volatile:
					base = factory.get_qualifier_type(CTypeQualifier.c_volatile, base);
					break;
				case c99_restrict:
					base = factory.get_qualifier_type(CTypeQualifier.c_restrict, base);
					break;
				default:
					throw new RuntimeException("Invalid pointer at line " + this.line_of(pointer));
				}
			} else
				throw new RuntimeException("Invalid pointer at line " + this.line_of(pointer));
		}
		return base;
	}

	/**
	 * build up the type from dimension in declarator
	 * 
	 * @param dimension
	 * @param base
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstDimension dimension, CType base) throws Exception {
		CType type;
		if (dimension.has_expression()) {
			int length = this.evaluate_int(dimension.get_expression());
			type = factory.get_array_type(base, length);
		} else
			type = factory.get_pointer_type(base);

		return type;
	}

	/**
	 * build up the type from parameter body in declarator
	 * 
	 * @param parameters
	 * @param base
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstParameterBody parameters, CType base) throws Exception {
		CFunctionType type;

		if (parameters.has_identifier_list()) // delay to further analysis
			type = factory.get_fixed_function_type(base);
		else if (parameters.has_parameter_type_list()) {
			AstParameterTypeList ptlist = parameters.get_parameter_type_list();
			if (ptlist.has_ellipsis())
				type = factory.get_variable_function_type(base);
			else
				type = factory.get_fixed_function_type(base);

			AstParameterList plist = ptlist.get_parameter_list();
			int n = plist.number_of_parameters();
			for (int i = 0; i < n; i++)
				this.build_up(plist.get_parameter(i), type);
		} else
			type = factory.get_fixed_function_type(base);

		return type;
	}

	/**
	 * build up the type for name among declarator and update the name's cname's
	 * instance (not only the type!)
	 * 
	 * @param declarator
	 * @param base
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstDeclarator declarator, CType base, CStorageClass storage) throws Exception {
		/* compute declarator type */
		AstName name = null;
		CType type = base;
		while (declarator != null) {
			switch (declarator.get_production()) {
			case pointer_declarator:
				type = this.build_up(declarator.get_pointer(), type);
				break;
			case declarator_dimension:
				type = this.build_up(declarator.get_dimension(), type);
				break;
			case declarator_parambody:
				type = this.build_up(declarator.get_parameter_body(), type);
				break;
			case lp_declarator_rp:
				break;
			case identifier:
				name = declarator.get_identifier();
				break;
			default:
				throw new RuntimeException("Invalid declarator at line " + this.line_of(declarator) + " : \""
						+ this.code_of(declarator) + "\"");
			}
			declarator = declarator.get_declarator();
		}

		/* update the type for cname in scope */
		CName cname = name.get_cname();
		if (cname instanceof CTypedefName) {
			if (((CTypedefName) cname).get_type() == null)
				((CTypedefName) cname).set_type(type);
			else
				throw new RuntimeException(
						"Duplicated definition at line " + this.line_of(name) + " : " + name.get_name());
		} else if (cname instanceof CParameterName) {
			if (((CParameterName) cname).get_parameter() == null) {
				CInstance instance;
				if (storage == null)
					instance = efactory.get_instance_of(type);
				else
					instance = efactory.get_instance_of(storage, type);
				((CParameterName) cname).set_parameter(instance);
			} else
				throw new RuntimeException(
						"Duplicated definition at line " + this.line_of(name) + " : " + name.get_name());
		} else if (cname instanceof CInstanceName) {
			if (((CInstanceName) cname).get_instance() == null) {
				CInstance instance;
				if (storage == null)
					instance = efactory.get_instance_of(type);
				else
					instance = efactory.get_instance_of(storage, type);
				((CInstanceName) cname).set_instance(instance);
			}
			// else throw new RuntimeException("Duplicated definition at line "
			// + this.line_of(name) + " : " + name.get_name());
		} else
			throw new RuntimeException(
					"Invalid cname at line " + this.line_of(name) + " : " + cname.getClass().getSimpleName());

		return type; /* return */
	}

	/**
	 * build up the type for name among struct-declarator
	 * 
	 * @param declarator
	 * @param base
	 * @param storage
	 * @param body
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstStructDeclarator declarator, CType type, CStructType body) throws Exception {
		/* derive name and type from declarator */
		String name;
		AstName ast_name = null;
		if (declarator.has_declarator()) {
			AstDeclarator decl = declarator.get_declarator();
			while (decl != null) {
				switch (decl.get_production()) {
				case pointer_declarator:
					type = this.build_up(decl.get_pointer(), type);
					break;
				case declarator_dimension:
					type = this.build_up(decl.get_dimension(), type);
					break;
				case declarator_parambody:
					type = this.build_up(decl.get_parameter_body(), type);
					break;
				case lp_declarator_rp:
					break;
				case identifier:
					ast_name = decl.get_identifier();
					break;
				default:
					throw new RuntimeException(
							"Invalid declarator at line " + this.line_of(decl) + " : \"" + this.code_of(decl) + "\"");
				}
				decl = decl.get_declarator();
			}

			name = ast_name.get_name();
			if (body.get_fields().has_field(name))
				throw new RuntimeException("Duplicated field-name at " + this.line_of(declarator) + " : " + name);
		} else {
			name = CField.Unknown_Prefix;
			int i;
			for (i = 0; body.get_fields().has_field(name + i); i++)
				;
			name = name + i;
			ast_name = null;
		}

		/* create field in the struct-type */
		if (declarator.has_expression()) {
			int bitsize = this.evaluate_int(declarator.get_expression());
			factory.new_field(body, name, type, bitsize);
		} else
			factory.new_field(body, name, type);

		/* update cfield in the field-name */
		if (ast_name != null) {
			CFieldName cname = (CFieldName) ast_name.get_cname();
			cname.set_field(body.get_fields().get_field(name));
		}

		/* return */ return type;
	}

	/**
	 * build up the type for name among union-declarator
	 * 
	 * @param declarator
	 * @param base
	 * @param storage
	 * @param body
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstStructDeclarator declarator, CType type, CUnionType body) throws Exception {
		/* derive name and type from declarator */
		String name;
		AstName ast_name = null;
		if (declarator.has_declarator()) {
			AstDeclarator decl = declarator.get_declarator();
			while (decl != null) {
				switch (decl.get_production()) {
				case pointer_declarator:
					type = this.build_up(decl.get_pointer(), type);
					break;
				case declarator_dimension:
					type = this.build_up(decl.get_dimension(), type);
					break;
				case declarator_parambody:
					type = this.build_up(decl.get_parameter_body(), type);
					break;
				case lp_declarator_rp:
					break;
				case identifier:
					ast_name = decl.get_identifier();
					break;
				default:
					throw new RuntimeException(
							"Invalid declarator at line " + this.line_of(decl) + " : \"" + this.code_of(decl) + "\"");
				}
				decl = decl.get_declarator();
			}

			name = ast_name.get_name();
			if (body.get_fields().has_field(name))
				throw new RuntimeException("Duplicated field-name at " + this.line_of(declarator) + " : " + name);
		} else {
			name = CField.Unknown_Prefix;
			int i;
			for (i = 0; body.get_fields().has_field(name + i); i++)
				;
			name = name + i;
			ast_name = null;
		}

		/* create field in the struct-type */
		if (declarator.has_expression()) {
			int bitsize = this.evaluate_int(declarator.get_expression());
			factory.new_field(body, name, type, bitsize);
		} else
			factory.new_field(body, name, type);

		/* update cfield in the field-name */
		if (ast_name != null) {
			CFieldName cname = (CFieldName) ast_name.get_cname();
			cname.set_field(body.get_fields().get_field(name));
		}

		/* return */ return type;
	}

	/**
	 * build up the type for abstract declarator in function-declaration or
	 * type-name
	 * 
	 * @param declarator
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected CType build_up(AstAbsDeclarator declarator, CType type) throws Exception {
		/* compute the type (without name) */
		while (declarator != null) {
			switch (declarator.get_production()) {
			case pointer_declarator:
				type = this.build_up(declarator.get_pointer(), type);
				break;
			case declarator_dimension:
				type = this.build_up(declarator.get_dimension(), type);
				break;
			case declarator_parambody:
				type = this.build_up(declarator.get_parameter_body(), type);
				break;
			case lp_declarator_rp:
				break;
			default:
				throw new RuntimeException("Invalid abstract-declarator at line " + this.line_of(declarator) + " : "
						+ this.code_of(declarator));
			}
			declarator = declarator.get_declarator();
		}
		return type;
	}

	/**
	 * build up the field body for each declaration in struct body
	 * 
	 * @param decl
	 * @param body
	 * @return
	 * @throws Exception
	 */
	protected boolean build_up(AstStructDeclaration decl, CStructType type) throws Exception {
		CType base = this.build_up(decl.get_specifiers());
		AstStructDeclaratorList dlist = decl.get_declarators();
		int n = dlist.number_of_declarators();
		for (int i = 0; i < n; i++)
			this.build_up(dlist.get_declarator(i), base, type);
		return true;
	}

	/**
	 * build up the field body for each declaration in union body
	 * 
	 * @param decl
	 * @param body
	 * @return
	 * @throws Exception
	 */
	protected boolean build_up(AstStructDeclaration decl, CUnionType type) throws Exception {
		CType base = this.build_up(decl.get_specifiers());
		AstStructDeclaratorList dlist = decl.get_declarators();
		int n = dlist.number_of_declarators();
		for (int i = 0; i < n; i++)
			this.build_up(dlist.get_declarator(i), base, type);
		return true;
	}

	/**
	 * build up the enumerators for each enumerator node in list of
	 * enum-specifier
	 * 
	 * @param enumerator
	 * @param elist
	 * @return
	 * @throws Exception
	 */
	protected boolean build_up(AstEnumerator enumerator, CEnumType body) throws Exception {
		/* get the name of enumerator */
		AstName name = enumerator.get_name();
		if (body.get_enumerator_list().has_enumerator(name.get_name()))
			throw new RuntimeException(
					"Duplicated definition at line " + this.line_of(name) + " : \"" + this.code_of(name) + "\"");

		/* create enumerator in type */
		if (enumerator.has_expression()) {
			int value = this.evaluate_int(enumerator.get_expression());
			factory.new_enumerator(body, name.get_name(), value);
		} else
			factory.new_enumerator(body, name.get_name());

		/* update cname in name node */
		CEnumeratorName cname = (CEnumeratorName) name.get_cname();
		if (cname.get_enumerator() != null)
			throw new RuntimeException(
					"Duplicated definition at line  " + this.line_of(enumerator) + " : " + this.code_of(enumerator));
		cname.set_enumerator(body.get_enumerator_list().get_enumerator(name.get_name()));

		/* return */ return true;
	}

	/**
	 * update the parameter type list by type of the parameter declaration;
	 * update the instance (not only the type) for cname in paramter's
	 * declarator's name
	 * 
	 * @param param
	 * @param list
	 * @return
	 * @throws Exception
	 */
	protected boolean build_up(AstParameterDeclaration param, CFunctionType ftype) throws Exception {
		/* compute the base-type from specifiers */
		AstDeclarationSpecifiers specifiers = param.get_specifiers();
		Object[] ans = this.build_up(specifiers);

		/* storage class validation */
		CStorageClass storage = (CStorageClass) ans[1];
		if (storage != null) {
			switch (storage) {
			case c_auto:
			case c_register:
				break;
			default:
				throw new RuntimeException(
						"Invalid storage-class at line " + this.line_of(specifiers) + " : " + this.code_of(specifiers));
			}
		}

		/* compute the complete type from declarator */
		CType type = (CType) ans[0];
		if (param.has_declarator())
			type = this.build_up(param.get_declarator(), type, storage);
		else if (param.has_abs_declarator())
			type = this.build_up(param.get_abs_declarator(), type);

		/* update the function-type by adding new parameter-type */
		ftype.get_parameter_types().add_parameter_type(type);
		return true;
	}

	/**
	 * update the scope for declaration and its name (cname)
	 * 
	 * @param declaration
	 * @return
	 * @throws Exception
	 */
	public boolean build_up(AstDeclaration declaration) throws Exception {
		Object[] ans = this.build_up(declaration.get_specifiers());
		CStorageClass storage = (CStorageClass) ans[1];
		CType base = (CType) ans[0];

		if (declaration.has_declarator_list()) {
			AstInitDeclaratorList ilist = declaration.get_declarator_list();
			for (int i = 0; i < ilist.number_of_init_declarators(); i++) {
				AstInitDeclarator init_declarator = ilist.get_init_declarator(i);
				this.build_up(init_declarator.get_declarator(), base, storage);
			}
		}

		return true;
	}

	/**
	 * update the type in type-name (used in expression)
	 * 
	 * @param typename
	 * @return
	 * @throws Exception
	 */
	public boolean build_up(AstTypeName typename) throws Exception {
		CType type = this.build_up(typename.get_specifiers());
		if (typename.has_declarator()) {
			AstAbsDeclarator declarator = typename.get_declarator();
			type = this.build_up(declarator, type);
		}
		typename.set_type(type);
		return false;
	}

	/**
	 * update the type for function definition's name
	 * 
	 * @param definition
	 * @return
	 * @throws Exception
	 */
	public boolean build_up(AstFunctionDefinition definition) throws Exception {
		/* derive type from specifiers */
		Object[] ans = this.build_up(definition.get_specifiers());
		CStorageClass storage = (CStorageClass) ans[1];
		CType base = (CType) ans[0];

		/* derive function type from declarator */
		AstDeclarator declarator = definition.get_declarator();
		CType type = this.build_up(declarator, base, storage);
		CFunctionType ftype = (CFunctionType) type;
		CParameterTypeList plist = ftype.get_parameter_types();

		/* update the function type by id-list */
		if (definition.has_declaration_list()) {
			AstDeclarationList dlist = definition.get_declaration_list();
			AstIdentifierList ilist = this.derive_id_list(declarator);

			int n = dlist.number_of_declarations();
			for (int i = 0; i < n; i++) {
				AstDeclarationStatement stmt = dlist.get_declaration(i);
				this.build_up(stmt.get_declaration());
			}
			Map<String, CName> types = new HashMap<String, CName>();
			this.process_definition(dlist, types);

			n = ilist.number_of_identifiers();
			for (int i = 0; i < n; i++) {
				AstName id = ilist.get_identifier(i);
				if (types.containsKey(id.get_name())) {
					CInstanceName cname = (CInstanceName) types.get(id.get_name());
					id.set_cname(cname);
					plist.add_parameter_type(cname.get_instance().get_type());
				} else
					throw new RuntimeException("At line " + this.line_of(ilist) + " : \"" + this.code_of(id)
							+ "\"\n\tUndefined parameter");
			}
		}

		/* update the cname and scope */
		AstName fname = this.get_name_of(declarator);
		CInstanceName cname = (CInstanceName) fname.get_cname();
		CInstance instance = efactory.get_instance_of(ftype);
		cname.set_instance(instance);
		return true;
	}

	/**
	 * extract identifier list from declarator
	 * 
	 * @param declarator
	 * @return
	 * @throws Exception
	 */
	private AstIdentifierList derive_id_list(AstDeclarator declarator) throws Exception {
		while (declarator != null) {
			if (declarator.get_production() == DeclaratorProduction.declarator_parambody)
				return declarator.get_parameter_body().get_identifier_list();
			else
				declarator = declarator.get_declarator();
		}
		throw new IllegalArgumentException("No identifier list in \"" + this.code_of(declarator) + "\"");
	}

	/**
	 * put the names' type into map
	 * 
	 * @param declaration
	 * @param types
	 * @throws Exception
	 */
	protected void process_definition(AstDeclarationList dlist, Map<String, CName> types) throws Exception {
		types.clear();

		int n = dlist.number_of_declarations();
		for (int i = 0; i < n; i++) {
			AstDeclaration declaration = dlist.get_declaration(i).get_declaration();

			if (declaration.has_declarator_list()) {
				AstInitDeclaratorList ilist = declaration.get_declarator_list();
				int m = ilist.number_of_init_declarators();
				for (int j = 0; j < m; j++) {
					AstDeclarator declarator = ilist.get_init_declarator(j).get_declarator();
					AstName name = this.get_name_of(declarator);
					CInstanceName cname = (CInstanceName) name.get_cname();

					if (types.containsKey(name.get_name()))
						throw new RuntimeException(
								"Duplicated definition at line " + this.line_of(name) + " : " + this.code_of(name));
					else
						types.put(name.get_name(), cname);
				}
			}
		}
	}

	/**
	 * get the name among declarator
	 * 
	 * @param declarator
	 * @return
	 * @throws Exception
	 */
	private AstName get_name_of(AstDeclarator declarator) throws Exception {
		while (declarator != null) {
			if (declarator.get_production() == DeclaratorProduction.identifier)
				return declarator.get_identifier();
			else
				declarator = declarator.get_declarator();
		}
		throw new IllegalArgumentException("Invalid declarator: null");
	}

	/**
	 * get the line of the node in source text
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private int line_of(AstNode node) throws Exception {
		CLocation loc = node.get_location();
		return loc.get_source().line_of(loc.get_bias());
	}

	/***
	 * get the code of the node in source text
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private String code_of(AstNode node) throws Exception {
		CLocation loc = node.get_location();
		return loc.read();
	}

	/**
	 * evaluate the integer value (expected as integer) from const-expression
	 * 
	 * @param expr
	 * @return
	 * @throws Exception
	 */
	private int evaluate_int(AstConstExpression expr) throws Exception {
		CConstant constant = evaluator.evaluate(expr);
		switch (constant.get_type().get_tag()) {
		case c_char:
		case c_uchar:
			char ch = constant.get_char();
			return ch;
		case c_int:
		case c_uint:
			int itv = constant.get_integer();
			return itv;
		case c_long:
		case c_ulong:
		case c_llong:
		case c_ullong:
			long lgv = constant.get_long();
			return (int) lgv;
		default:
			throw new RuntimeException("Invalid const-expression (expected as integer) at line " + this.line_of(expr)
					+ " : \"" + this.code_of(expr) + "\"");
		}
	}

}
