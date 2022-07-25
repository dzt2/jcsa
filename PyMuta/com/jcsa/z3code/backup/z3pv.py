"""This file implements the z3-translation and equivalence proof"""


import os
import z3
import com.jcsa.z3code.base as jcbase
import com.jcsa.z3code.muta as jcmuta


class SymbolToZ3Parser:
	"""
	It parses from SymbolNode to Z3.sexpr
	"""

	def __init__(self):
		self.boolSize = 1
		self.charSize = 8
		self.shortSize = 16
		self.intSize = 64
		self.longSize = 64
		self.realSize = 64
		self.pointSize = 64
		self.bodySize = 128
		self.__buffer__ = dict()
		self.bitSwitch = False
		self.assumptions = set()
		return

	def __save__(self, key, value):
		"""
		:param key:
		:param value:
		:return: it saves the key-value pair to environment for representing side-effects
		"""
		self.__buffer__[str(key)] = value
		return

	def __assume__(self, assumption):
		if assumption is None:
			return
		self.assumptions.add(assumption)
		return

	def __parse__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: it recursively parses the symbolic node to s-expression in z3
		"""
		symbol_class = symbol_node.get_class_name()
		if symbol_class == "Identifier":
			return self.__parse_identifier__(symbol_node)
		elif symbol_class == "Constant":
			return self.__parse_constant__(symbol_node)
		elif symbol_class == "Literal":
			return self.__parse_literal__(symbol_node)
		elif symbol_class == "UnaryExpression":
			return self.__parse_unary_expression__(symbol_node)
		elif symbol_class == "ArithExpression":
			return self.__parse_arith_expression__(symbol_node)
		elif symbol_class == "BitwsExpression":
			return self.__parse_bitws_expression__(symbol_node)
		elif symbol_class == "LogicExpression":
			return self.__parse_logic_expression__(symbol_node)
		elif symbol_class == "RelationExpression":
			return self.__parse_relation_expression__(symbol_node)
		elif symbol_class == "AssignExpression":
			return self.__parse_assign_expression__(symbol_node)
		elif symbol_class == "CastExpression":
			return self.__parse_casted_expression__(symbol_node)
		elif symbol_class == "FieldExpression":
			return self.__parse_field_expression__(symbol_node)
		elif symbol_class == "IfElseExpression":
			return self.__parse_if_else_expression__(symbol_node)
		elif symbol_class == "CallExpression":
			return self.__parse_call_expression__(symbol_node)
		else:
			return self.__parse_list_expression__(symbol_node)

	def __new_bool_ref__(self, name: str):
		if self.bitSwitch:
			return z3.BitVec(name, self.boolSize)
		else:
			return z3.Bool(name)

	def __new_char_ref__(self, name: str):
		if self.bitSwitch:
			reference = z3.BitVec(name, self.charSize)
		else:
			reference = z3.Int(name)
		self.assumptions.add(reference >= 0)
		return reference

	def __new_short_ref__(self, name: str, unsigned: bool):
		if self.bitSwitch:
			reference = z3.BitVec(name, self.shortSize)
		else:
			reference = z3.Int(name)
		if unsigned:
			self.__assume__(reference >= 0)
		return reference

	def __new_int_ref__(self, name: str, unsigned: bool):
		if self.bitSwitch:
			reference = z3.BitVec(name, self.intSize)
		else:
			reference = z3.Int(name)
		if unsigned:
			self.__assume__(reference >= 0)
		return reference

	def __new_long_ref__(self, name: str, unsigned: bool):
		if self.bitSwitch:
			reference = z3.BitVec(name, self.longSize)
		else:
			reference = z3.Int(name)
		if unsigned:
			self.__assume__(reference >= 0)
		return reference

	def __new_complex_ref__(self, name: str):
		return z3.BitVec(name, self.realSize * 2)

	def __new_address_ref__(self, name: str):
		if self.bitSwitch:
			return z3.BitVec(name, self.pointSize)
		else:
			return z3.Int(name)

	def __new_otherwise_ref__(self, name: str):
		return z3.BitVec(name, self.bodySize)

	def __parse_by_name__(self, name: str, data_type: jcbase.CType):
		"""
		:param name:
		:param data_type:
		:return: it creates a z3-sexpr by using name and data-type to create refereces
		"""
		if data_type is None:
			return self.__new_bool_ref__(name)
		elif (data_type.get_key() == "bool") or (data_type.get_key() == "void"):
			return self.__new_bool_ref__(name)
		elif (data_type.get_key() == "char") or (data_type.get_key() == "uchar"):
			return self.__new_char_ref__(name)
		elif (data_type.get_key() == "short") or (data_type.get_key() == "ushort"):
			return self.__new_short_ref__(name, data_type.get_key() == "ushort")
		elif (data_type.get_key() == "int") or (data_type.get_key() == "uint"):
			return self.__new_int_ref__(name, data_type.get_key() == "uint")
		elif (data_type.get_key() == "long") or (data_type.get_key() == "ulong"):
			return self.__new_long_ref__(name, data_type.get_key() == "ulong")
		elif (data_type.get_key() == "llong") or (data_type.get_key() == "ullong"):
			return self.__new_long_ref__(name, data_type.get_key() == "ullong")
		elif (data_type.get_key() == "float") or (data_type.get_key() == "double") or (data_type.get_key() == "ldouble"):
			return z3.Real(name)
		elif (data_type.get_key() == "float_x") or (data_type.get_key() == "double_x") or (data_type.get_key() == "ldouble_x"):
			return self.__new_complex_ref__(name)
		elif (data_type.get_key() == "float_i") or (data_type.get_key() == "double_i") or (data_type.get_key() == "ldouble_i"):
			return z3.Real(name)
		elif (data_type.get_key() == "array") or (data_type.get_key() == "point"):
			return self.__new_address_ref__(name)
		elif data_type.get_key() == "function":
			return z3.Function(name)
		else:
			return self.__new_otherwise_ref__(name)

	def __parse_identifier__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse_by_name__(symbol_node.get_content().get_token_value(), symbol_node.get_data_type())

	def __parse_constant__(self, symbol_node: jcmuta.SymbolNode):
		constant = symbol_node.get_content().get_token_value()
		if isinstance(constant, bool):
			return z3.BoolVal(constant)
		elif isinstance(constant, int):
			if self.bitSwitch:
				return z3.BitVecVal(constant, self.longSize)
			else:
				return z3.IntVal(constant)
		else:
			return z3.RealVal(constant)

	def __new_default_name__(self, symbol_node: jcmuta.SymbolNode):
		self.__assume__(None)
		return "{}_{}".format(symbol_node.get_class_name(), abs(symbol_node.get_class_id()))

	def __parse_literal__(self, symbol_node: jcmuta.SymbolNode):
		return z3.String(self.__new_default_name__(symbol_node))

	def __cast_to_bool__(self, operand):
		self.__assume__(None)
		if isinstance(operand, z3.IntNumRef) or isinstance(operand, z3.RatNumRef):
			res = operand != 0
		elif isinstance(operand, z3.ArithRef) or isinstance(operand, z3.BitVecRef):
			return operand != 0
		elif isinstance(operand, z3.BitVecNumRef):
			return operand != 0
		else:
			return operand

	def __parse_unary_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: neg, rsv, not, adr, der
		"""
		operator = symbol_node.get_content().get_token_value()
		u_operand = self.__parse__(symbol_node.get_child(1))
		if operator == "negative":
			return -u_operand
		elif operator == "bit_not":
			if isinstance(u_operand, z3.IntNumRef) or isinstance(u_operand, z3.ArithRef):
				return -u_operand - 1
			else:
				return ~u_operand
		elif operator == "logic_not":
			return z3.Not(self.__cast_to_bool__(u_operand))
		elif operator == "address_of":
			return z3.Const(self.__new_default_name__(symbol_node), z3.IntSort())
		else:
			return self.__parse_by_name__(self.__new_default_name__(symbol_node), symbol_node.get_data_type())

	def __parse_arith_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
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
		operator = str(symbol_node.get_content().get_token_value()).strip()
		self.bitSwitch = True
		loperand = self.__parse__(symbol_node.get_child(1))
		self.bitSwitch = True
		roperand = self.__parse__(symbol_node.get_child(2))
		self.bitSwitch = False
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

	def __parse_logic_expression__(self, symbol_node: jcmuta.SymbolNode):
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
		loperand = self.__cast_to_bool__(loperand)
		roperand = self.__cast_to_bool__(roperand)
		if operator == "logic_and":
			return z3.And(loperand, roperand)
		else:
			return z3.Or(loperand, roperand)

	def __parse_relation_expression__(self, symbol_node: jcmuta.SymbolNode):
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
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

	def __parse_assign_expression__(self, symbol_node: jcmuta.SymbolNode):
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
		self.__save__(loperand.sexpr(), roperand)
		if operator == "assign":
			return roperand
		else:
			return loperand

	def __parse_casted_expression__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse__(symbol_node.get_child(1))

	def __parse_field_expression__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse_by_name__(self.__new_default_name__(symbol_node), symbol_node.get_data_type())

	def __parse_if_else_expression__(self, symbol_node: jcmuta.SymbolNode):
		condition = self.__parse__(symbol_node.get_child(0))
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
		return z3.If(condition, loperand, roperand)

	def __parse_list_expression__(self, symbol_node: jcmuta.SymbolNode):
		elements = list()
		for child in symbol_node.get_children():
			elements.append(self.__parse__(child))
		return self.__parse_by_name__(self.__new_default_name__(symbol_node), symbol_node.get_data_type())

	def __parse_call_expression__(self, symbol_node: jcmuta.SymbolNode):
		function = symbol_node.get_child(0)
		if function.get_class_name() == "Identifier":
			func_name = function.get_content().get_token_value()
		else:
			func_name = self.__new_default_name__(function)
		arguments = symbol_node.get_child(1).get_children()
		for k in range(0, len(arguments)):
			arg = self.__parse__(arguments[k])
			self.__save__("{}#{}".format(func_name, k), arg)
		return self.__parse_by_name__("{}_{}".format(func_name, symbol_node.get_class_id()), symbol_node.get_data_type())

	def parse_to(self, symbol_node: jcmuta.SymbolNode, stateBuffer=None, assumeLib=None):
		"""
		:param assumeLib: to preserve the assumption
		:param symbol_node:
		:param stateBuffer: to preserve translation state
		:return: None if transformation failed
		"""
		self.__buffer__.clear()
		self.assumptions.clear()
		self.bitSwitch = False
		# res = self.__parse__(symbol_node)
		try:
			res = self.__parse__(symbol_node)
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
			for assumption in self.assumptions:
				assumeLib.add(assumption)
		return res


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


def test_symbol_prover(project: jcmuta.CProject, file_path: str):
	prover = SymbolToZ3Prover()
	prover.prove(project, file_path)
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext3/featuresBIG"
	post_path = "/home/dzt2/Development/Data/zext3/resultsBIG"
	for project_name in os.listdir(root_path):
		if project_name == "md4":
			continue
		project_directory = os.path.join(root_path, project_name)
		c_project = jcmuta.CProject(project_directory, project_name)
		print("Testing on", project_name, "for", len(c_project.muta_space.mutants),
			  "mutants and", len(c_project.context_space.get_states()), "states.")
		# test_symbol_parser(c_project, os.path.join(post_path, project_name + ".sz3"))
		test_symbol_prover(c_project, os.path.join(post_path, project_name + ".zeq"))
		print()

