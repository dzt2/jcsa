package com.jcsa.jcparse.lang.astree.decl.specifier;

import com.jcsa.jcparse.lang.astree.AstNode;

/**
 * <code>
 * 	Specifier |--> storage_class_specifier | type_qualifier_specifier |
 * 					function_qualifier_specifier | type_keyword_specifier
 * 					| struct_specifier | union_specifier | enum_specifier
 * 					| typedef_name
 * </code>
 *
 * @author yukimula
 */
public interface AstSpecifier extends AstNode {
}
