"""This file implements the equivalence checking based on Z3 theorem prover."""


import os
import z3
import com.jcsa.z3code.base as jcbase
import com.jcsa.z3code.muta as jcmuta


class SymbolToZ3Parser:
	"""
	It implements the parse from SymbolNode to z3.SExpression
	"""

	def __init__(self):
		self.boolLength = 1
		self.charLength = 8
		self.shortLength = 16
		self.intLength = 64
		self.longLength = 64
		self.realLength = 64
		self.pointLength = 64
		self.bodyLength = 128
		self.__buffer__ = dict()
		self.__assume__ = set()
		self.__b_code__ = 1
		self.__i_code__ = 2
		self.__s_code__ = 3
		self.__naming__ = "{}_{}{}"
		return

	def __save_state__(self, key, value):
		"""
		It saves the value to the key reference in state buffer
		:param key:
		:param value:
		:return:
		"""
		self.__buffer__[str(key)] = value
		return

	def __add_assume__(self, assumption):
		"""
		It adds the assumption to the set
		:param assumption:
		:return:
		"""
		if not (assumption is None):
			self.__assume__.add(assumption)
		return

	def __parse__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return: it recursively parses the symbolic node to z3.sexpr
		"""
		symbol_class = symbol_node.get_class_name()
		if symbol_class == "Identifier":
			return self.__parse_identifier__(symbol_node, code)
		elif symbol_class == "Constant":
			return self.__parse_constant__(symbol_node, code)
		elif symbol_class == "Literal":
			return self.__parse_literal__(symbol_node)
		elif symbol_class == "UnaryExpression":
			return self.__parse_unary_expression__(symbol_node, code)
		elif symbol_class == "ArithExpression":
			return self.__parse_arith_expression__(symbol_node, code)
		elif symbol_class == "BitwsExpression":
			return self.__parse_bitws_expression__(symbol_node)
		elif symbol_class == "LogicExpression":
			return self.__parse_logic_expression__(symbol_node, code)
		elif symbol_class == "RelationExpression":
			return self.__parse_relation_expression__(symbol_node, code)
		elif symbol_class == "AssignExpression":
			return self.__parse_assign_expression__(symbol_node, code)
		elif symbol_class == "CastExpression":
			return self.__parse_casted_expression__(symbol_node, code)
		elif symbol_class == "FieldExpression":
			return self.__parse_field_expression__(symbol_node, code)
		elif symbol_class == "IfElseExpression":
			return self.__parse_if_else_expression__(symbol_node, code)
		elif symbol_class == "CallExpression":
			return self.__parse_call_expression__(symbol_node, code)
		else:
			return self.__parse_list_expression__(symbol_node, code)

	def __new_bool_reference__(self, name: str, code: int):
		"""
		:param name:
		:param code:
		:return: bool-reference encoded by z3
		"""
		if code == self.__s_code__:
			return z3.BitVec(name, self.boolLength)
		else:
			return z3.Bool(name)

	def __new_char_reference__(self, name: str, code: int):
		"""
		:param name:
		:param code:
		:return: character reference
		"""
		if code == self.__s_code__:
			return z3.BitVec(name, self.charLength)
		else:
			return z3.Int(name)

	def __new_short_reference__(self, name: str, code: int):
		"""
		:param name:
		:param code:
		:return: short integer reference
		"""
		if code == self.__s_code__:
			return z3.BitVec(name, self.shortLength)
		else:
			return z3.Int(name)

	def __new_int_reference__(self, name: str, code: int):
		"""
		:param name:
		:param code:
		:return: integer reference
		"""
		if code == self.__s_code__:
			return z3.BitVec(name, self.intLength)
		else:
			return z3.Int(name)

	def __new_long_reference__(self, name: str, code: int):
		"""
		:param name:
		:param code:
		:return: long integer reference
		"""
		if code == self.__s_code__:
			return z3.BitVec(name, self.longLength)
		else:
			return z3.Int(name)

	def __new_real_reference__(self, name: str):
		"""
		:param name:
		:return: the float reference integer
		"""
		self.__add_assume__(None)
		return z3.Real(name)

	def __new_point_reference__(self, name: str, code: int):
		"""
		:param name:
		:param code:
		:return: pointer reference
		"""
		if code == self.__s_code__:
			return z3.BitVec(name, self.pointLength)
		else:
			return z3.Int(name)

	def __new_other_reference__(self, name: str):
		"""
		:param name:
		:return: the struct type reference
		"""
		return z3.BitVec(name, self.bodyLength)

	def __new_unsigned_reference__(self, reference):
		"""
		:param reference:
		:return: it adds the {reference >= 0} to assumption
		"""
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef):
			self.__add_assume__(reference >= 0)
		elif isinstance(reference, z3.BitVecRef) or isinstance(reference, z3.BitVecNumRef):
			self.__add_assume__(reference >= z3.BitVecVal(0, self.longLength))
		return reference

	def __new_reference__(self, name: str, data_type: jcbase.CType, code: int):
		"""
		:param name: the name of the reference
		:param data_type: the type of the name
		:param code: the integer code to parse the reference
		:return: the z3.reference of the creation
		"""
		c_type = data_type.get_key()
		if (c_type == "void") or (c_type == "bool"):
			return self.__new_bool_reference__(name, code)
		elif (c_type == "char") or (c_type == "uchar"):
			return self.__new_unsigned_reference__(self.__new_char_reference__(name, code))
		elif c_type == "short":
			return self.__new_short_reference__(name, code)
		elif c_type == "ushort":
			return self.__new_unsigned_reference__(self.__new_short_reference__(name, code))
		elif c_type == "int":
			return self.__new_int_reference__(name, code)
		elif c_type == "uint":
			return self.__new_unsigned_reference__(self.__new_int_reference__(name, code))
		elif (c_type == "long") or (c_type == "llong"):
			return self.__new_long_reference__(name, code)
		elif (c_type == "ulong") or (c_type == "ullong"):
			return self.__new_unsigned_reference__(self.__new_long_reference__(name, code))
		elif (c_type == "float") or (c_type == "double") or (c_type == "ldouble"):
			return self.__new_real_reference__(name)
		elif (c_type == "array") or (c_type == "point"):
			return self.__new_unsigned_reference__(self.__new_point_reference__(name, code))
		elif c_type == "function":
			return z3.Function(name)
		else:
			return self.__new_other_reference__(name)

	def __new_constant__(self, constant, code: int):
		"""
		:param constant: bool | int | float
		:param code:
		:return:
		"""
		if isinstance(constant, bool):
			return z3.BoolVal(constant)
		elif isinstance(constant, int):
			if code == self.__s_code__:
				return z3.BitVecVal(constant, self.longLength)
			else:
				return z3.IntVal(constant)
		else:
			return z3.RealVal(constant)

	def __new_symbol_name__(self, symbol_node: jcmuta.SymbolNode):
		symbol_class = symbol_node.get_class_name()
		symbol_flag = "p"
		symbol_id = symbol_node.get_class_id()
		if symbol_id < 0:
			symbol_flag = "n"
			symbol_id = abs(symbol_id)
		return self.__naming__.format(symbol_class, symbol_flag, symbol_id)

	def __parse_identifier__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return: it parses the symbolic identifier to z3 reference
		"""
		name = symbol_node.get_content().get_token_value()
		data_type = symbol_node.get_data_type()
		return self.__new_reference__(name, data_type, code)

	def __parse_constant__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		return self.__new_constant__(symbol_node.get_content(), code)

	def __parse_literal__(self, symbol_node: jcmuta.SymbolNode):
		return z3.String(self.__new_symbol_name__(symbol_node))

	def __cast_to_bool__(self, reference):
		"""
		:param reference:
		:return:
		"""
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef) or isinstance(reference, z3.RatNumRef):
			return reference != 0
		elif isinstance(reference, z3.BitVecRef) or isinstance(reference, z3.BitVecNumRef):
			return reference != z3.BitVecVal(0, self.longLength)
		else:
			return reference

	def __cast_to_numb__(self, reference):
		"""
		:param reference:
		:return:
		"""
		self.__add_assume__(None)
		if isinstance(reference, z3.BoolRef):
			return z3.If(reference, 1, 0)
		elif isinstance(reference, z3.BitVecRef) or isinstance(reference, z3.BitVecNumRef):
			return z3.BV2Int(reference, True)
		else:
			return reference

	def __cast_to_bits__(self, reference):
		"""
		:param reference:
		:return:
		"""
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef):
			return z3.Int2BV(reference, self.longLength)
		elif isinstance(reference, z3.BoolRef):
			return z3.If(reference, 1, 0)
		else:
			return reference

	def __parse_unary_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return: negative, bit_not, logic_not, address_of, de_reference
		"""
		operator = symbol_node.get_content().get_token_value()
		operand = symbol_node.get_child(1)
		if operator == "negative":
			u_operand = self.__parse__(operand, self.__i_code__)
			return -self.__cast_to_numb__(u_operand)
		elif operator == "bit_not":
			u_operand = self.__parse__(operand, self.__s_code__)
			return ~self.__cast_to_bits__(u_operand)
		elif operator == "logic_not":
			u_operand = self.__parse__(operand, self.__b_code__)
			return z3.Not(self.__cast_to_bool__(u_operand))
		elif operator == "address_of":
			return z3.Const(self.__new_symbol_name__(symbol_node), z3.IntSort())
		else:
			return self.__new_reference__(self.__new_symbol_name__(symbol_node), symbol_node.get_data_type(), code)

	def __parse_arith_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(1), code))
		roperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(2), code))
		if operator == "arith_add":
			return loperand + roperand
		elif operator == "arith_sub":
			return loperand - roperand
		elif operator == "arith_mul":
			return loperand * roperand
		elif operator == "arith_div":
			return loperand / roperand
		else:
			return loperand % roperand

	def __parse_bitws_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_bits__(self.__parse__(symbol_node.get_child(1), self.__s_code__))
		roperand = self.__cast_to_bits__(self.__parse__(symbol_node.get_child(2), self.__s_code__))
		if operator == "bit_and":
			return loperand & roperand
		elif operator == "bit_or":
			return loperand | roperand
		elif operator == "bit_xor":
			return loperand ^ roperand
		elif operator == "left_shift":
			return loperand << roperand
		else:
			return loperand >> roperand

	def __parse_logic_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_bool__(self.__parse__(symbol_node.get_child(1), code))
		roperand = self.__cast_to_bool__(self.__parse__(symbol_node.get_child(2), code))
		if operator == "logic_and":
			return z3.And(loperand, roperand)
		elif operator == "logic_or":
			return z3.Or(loperand, roperand)
		else:
			return z3.Implies(loperand, roperand)

	def __parse_relation_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(1), code))
		roperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(2), code))
		if operator == "greater_tn":
			return loperand > roperand
		elif operator == "greater_eq":
			return loperand >= roperand
		elif operator == "smaller_tn":
			return loperand < roperand
		elif operator == "smaller_eq":
			return loperand <= roperand
		elif operator == "equal_with":
			return loperand == roperand
		else:
			return loperand != roperand

	def __parse_casted_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		return self.__parse__(symbol_node.get_child(1), code)

	def __parse_field_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		return self.__new_reference__(self.__new_symbol_name__(symbol_node), symbol_node.get_data_type(), code)

	def __parse_if_else_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		condition = self.__parse__(symbol_node.get_child(0), code)
		loperand = self.__parse__(symbol_node.get_child(1), code)
		roperand = self.__parse__(symbol_node.get_child(2), code)
		condition = self.__cast_to_bool__(condition)
		return z3.If(condition, loperand, roperand)

	def __parse_list_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		elements = list()
		for child in symbol_node.get_children():
			elements.append(self.__parse__(child, code))
		return self.__new_reference__(self.__new_symbol_name__(symbol_node), symbol_node.get_data_type(), code)

	def __parse_call_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		function = symbol_node.get_child(0)
		if function.get_class_name() == "Identifier":
			func_name = function.get_content().get_token_value()
		else:
			func_name = self.__new_symbol_name__(function)
		arguments = symbol_node.get_child(1).get_children()
		for k in range(0, len(arguments)):
			arg = self.__parse__(arguments[k], 0)
			self.__save_state__("{}#{}".format(func_name, k), arg)
		return self.__new_reference__(func_name, symbol_node.get_data_type(), code)

	def __parse_assign_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.__parse__(symbol_node.get_child(1), code)
		roperand = self.__parse__(symbol_node.get_child(2), code)
		self.__save_state__(loperand.sexpr(), roperand)
		if operator == "assign":
			return roperand
		else:
			return loperand

	def parse_to(self, symbol_node: jcmuta.SymbolNode, stateBuffer=None, assumeLib=None):
		"""
		:param assumeLib: to preserve the assumption
		:param symbol_node:
		:param stateBuffer: to preserve translation state
		:return: None if transformation failed
		"""
		self.__buffer__.clear()
		self.__assume__.clear()
		# res = self.__parse__(symbol_node)
		try:
			res = self.__parse__(symbol_node, 0)
		except z3.Z3Exception:
			res = None
		except AttributeError:
			res = None
		except TypeError:
			res = None
		if not (stateBuffer is None):
			stateBuffer: dict
			for key, value in self.__buffer__.items():
				stateBuffer[key] = value
		if not (assumeLib is None):
			assumeLib: set
			for assumption in self.__assume__:
				assumeLib.add(assumption)
		return res


def test_symbol_parser(project: jcmuta.CProject, file_path: str):
	parser = SymbolToZ3Parser()
	past, fail = 0, 0
	with open(file_path, 'w') as writer:
		writer.write("CLAS\tTYPE\tCONTENT\tCODE\tSEXPR\n")
		for symbol_node in project.sym_tree.get_sym_nodes():
			symbol_node: jcmuta.SymbolNode
			expression = parser.parse_to(symbol_node)

			clas = symbol_node.get_class_name()
			data_type = symbol_node.get_data_type()
			content = symbol_node.get_content()
			code = symbol_node.get_code()
			code = jcbase.strip_text(code, 96)
			writer.write("{}\t{}\t{}\t{}\t".format(clas, data_type, content, code))
			if not (expression is None):
				scode = str(expression.sexpr())
				scode = jcbase.strip_text(scode, 96)
				writer.write(scode)
				past += 1
			else:
				writer.write("???")
				fail += 1
			writer.write("\n")
	ratio = past / (past + fail + 0.0001)
	ratio = int(ratio * 10000) / 100.0
	print("\t==> PASS = {}\tFAIL = {}\t ({}%)".format(past, fail, ratio))
	return


class SymbolToZ3Prover:
	"""
	It proves the equivalence of mutation based on its state
	"""

	def __init__(self):
		self.parser = SymbolToZ3Parser()
		self.timeout = 5000
		self.neq = 0
		self.ceq = 1
		self.veq = 2
		self.seq = 3
		self.solutions = dict()
		return

	def __parse__(self, symbol_node: jcmuta.SymbolNode, stateBuffer=None, assumeLib=None):
		return self.parser.parse_to(symbol_node, stateBuffer, assumeLib)

	def __differ__(self, orig_value, muta_value):
		self.timeout = self.timeout
		try:
			return orig_value != muta_value
		except z3.z3types.Z3Exception:
			return z3.BoolVal(True)
		except AttributeError:
			return z3.BoolVal(True)
		except TypeError:
			return z3.BoolVal(True)

	def __check__(self, condition, assumptions):
		"""
		:param condition:
		:return: whether condition is infeasible
		"""
		solver = z3.Solver()
		solver.add(condition)
		for assumption in assumptions:
			solver.add(assumption)
		solver.set("timeout", self.timeout)
		if solver.check() == z3.unsat:
			return True
		return False

	def __prove__(self, state: jcmuta.ContextState):
		"""
		It decides whether the state is equivalence
		:param state:
		:return:	0	-- 	Non_Equivalent
					1	-- 	Conditional
					2 	-- 	Value-based
					3 	--	State-based
		"""
		if state.get_category() == "eva_cond":
			assumeLib = set()
			condition = self.__parse__(state.get_loperand(), None, assumeLib)
			if condition is None:
				return self.neq
			elif self.__check__(condition, assumeLib):
				return self.ceq
			else:
				return self.neq
		elif state.get_category() == "set_expr":
			## 1. parse
			orig_states, muta_states = dict(), dict()
			loperand = self.__parse__(state.get_loperand(), orig_states, None)
			roperand = self.__parse__(state.get_roperand(), muta_states, None)
			if (loperand is None) or (roperand is None):
				return self.neq

			## 2. state-compare
			for key, muta_value in muta_states.items():
				if key in orig_states:
					orig_value = orig_states[key]
					if not self.__check__(self.__differ__(orig_value, muta_value), set()):
						return self.neq

			## 3. context-based value compare
			location = state.get_location()
			if (location.get_child_type() == "evaluate") or (location.get_child_type() == "n_condition") or (location.get_child_type() == "element"):
				return self.seq
			elif self.__check__(self.__differ__(loperand, roperand), set()):
				return self.veq
			else:
				return self.neq
		else:
			return self.neq

	def __resolve__(self, state: jcmuta.ContextState):
		if not (state in self.solutions):
			self.solutions[state] = self.__prove__(state)
		return self.solutions[state]

	def prove(self, project: jcmuta.CProject, file_path: str):
		"""
		:param project:
		:param file_path:
		:return: alv, eqv, ceq, veq, seq
		"""
		self.solutions.clear()
		anum, xnum, cnum, vnum, snum = 0, 0, 0, 0, 0
		with open(file_path, 'w') as writer:
			writer.write("ID\tCLAS\tOPRT\tLINE\tCODE\tPARM\tTYPE\tCATE\tLOCT\tLOPR\tROPR\n")
			for mutant in project.context_space.get_mutants():
				if not mutant.get_result().is_killed_in(None):
					mid = mutant.get_muta_id()
					mclas = mutant.get_mutation().get_mutation_class()
					moprt = mutant.get_mutation().get_mutation_operator()
					mline = mutant.get_mutation().get_location().line_of(False)
					param = mutant.get_mutation().get_parameter()
					mcode = mutant.get_mutation().get_location().get_code(True)
					mcode = jcbase.strip_text(mcode, 64)
					writer.write("{}\t{}\t{}\t{}\t{}\t{}".format(mid, mclas, moprt, mline, mcode, param))

					states = project.context_space.get_mutation(mutant).get_states()
					for state in states:
						state: jcmuta.ContextState
						flag = self.__resolve__(state)
						if flag == self.neq:
							continue
						elif flag == self.ceq:
							flag_string = "CEQ"
							cnum += 1
						elif flag == self.veq:
							flag_string = "VEQ"
							vnum += 1
						else:
							flag_string = "SEQ"
							snum += 1
						xnum += 1
						category = state.get_category()
						location = state.get_location()
						loct_code = location.get_ast_source().get_code(True)
						loct_code = jcbase.strip_text(loct_code, 64)
						loperand = state.get_loperand().get_code()
						roperand = state.get_roperand().get_code()
						loperand = jcbase.strip_text(loperand, len(loperand) + 16)
						roperand = jcbase.strip_text(roperand, len(roperand) + 16)
						writer.write("\t{}\t{}\t{}\t{}\t{}".format(flag_string, category, loct_code, loperand, roperand))
						break

					writer.write("\n")
					anum += 1
		ratio = xnum / (anum + 0.0001)
		ratio = int(ratio * 10000) / 100.0
		print("\tALV = {}\tEQV = {} ({}%)\tCEQ = {}\tVEQ = {}\tSEQ = {}".format(anum, xnum, ratio, cnum, vnum, snum))
		return anum, xnum, cnum, vnum, snum


def test_symbol_prover(project: jcmuta.CProject, file_path: str):
	prover = SymbolToZ3Prover()
	prover.prove(project, file_path)
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext3/featuresBIG"
	post_path = "/home/dzt2/Development/Data/zext3/resultsBIG"
	for project_name in os.listdir(root_path):
		project_directory = os.path.join(root_path, project_name)
		c_project = jcmuta.CProject(project_directory, project_name)
		print("Testing on", project_name, "for", len(c_project.muta_space.mutants),
			  "mutants and", len(c_project.context_space.get_states()), "states.")
		# test_symbol_parser(c_project, os.path.join(post_path, project_name + ".sz3"))
		test_symbol_prover(c_project, os.path.join(post_path, project_name + ".zeq"))
		print()

