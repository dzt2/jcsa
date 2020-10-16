"""
This file defines the models for describing the structural program models, including:
	CSourceCode     --- It manages the access to characters in source code in .c file.
	AstTree         --- It presents the structural model of abstract syntactic tree of C program.
	CirTree         --- It describes the structural model of C-intermediate representation code.
	CirFunctionCallGraph    --- It defines the structural control flow graph for program analysis.
	CProgram        --- It provides access to these structural descriptions of C source code.
"""

import src.jcmuta.base as base


class CProgram:
	def __init__(self):
		self.source_code = None
		self.ast_tree = None
		self.cir_tree = None
		self.function_call_graph = None
		self.test_space = None
		self.mutant_space = None
		return


# TODO implement the following classes: CSourceCode, AstTree & CirTree


















