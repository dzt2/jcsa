"""This file implements the proof for expression-level mutation equivalence."""


import os
import time
import z3
import com.jcsa.z3code.libs.base as jcbase
import com.jcsa.z3code.libs.muta as jcmuta


class SymbolToZ3Parser:
	"""
	It parses the internal symbolic node to the expression instance in z3.
	"""

	def __init__(self):
		"""
		It initializes the parameters for parsing.
		"""
		self.__states__ = dict()	# reference(str) --> value(z3.expr)
		self.__assume__ = set()		# set(z3.expr)
		self.__normal__ = dict()	# name(str) --> normal_name(str)
		self.__naming__ = "{}_{}{}"	# used to generate the unique names
		self.byteLength = 96		# used to encode structural values
		self.longLength = 64		# used to encode bitwise value (int)
		self.signFlag = True		# whether to encode int as sign-byte
		return

	## basic methods

	def __save_state__(self, key, value):
		"""
		:param key: the string key of the reference
		:param value: the z3.expression value to bind with
		:return:
		"""
		self.__states__[str(key)] = value
		return

	def __add_assume__(self, assumption):
		"""
		:param assumption: need be z3.expression objects
		:return:
		"""
		if not (assumption is None):
			self.__assume__.add(assumption)
		return

	def __unique_name__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: the unique name of the symbol_node
		"""
		class_name = symbol_node.get_class_name()
		class_id = symbol_node.get_class_id()
		if class_id < 0:
			class_flag = 'n'
			class_id = abs(class_id)
		else:
			class_flag = 'p'
		return self.__naming__.format(class_name, class_flag, class_id)

	def __normal_name__(self, name: str, data_type: jcbase.CType):
		"""
		:param name:
		:param data_type:
		:return:
		"""
		if not (name in self.__normal__):
			c_type = data_type.get_key()
			if (c_type == "void") or (c_type == "bool"):
				self.__normal__[name] = "b#{}".format(len(self.__normal__))		## bool
			elif (c_type == "char") or (c_type == "uchar"):
				self.__normal__[name] = "c#{}".format(len(self.__normal__))		## char
			elif (c_type == "short") or (c_type == "int") or (c_type == "long") or (c_type == "llong"):
				self.__normal__[name] = "i#{}".format(len(self.__normal__))		## int
			elif (c_type == "ushort") or (c_type == "uint") or (c_type == "ulong") or (c_type == "ullong"):
				self.__normal__[name] = "u#{}".format(len(self.__normal__))		## unsigned
			elif (c_type == "float") or (c_type == "double") or (c_type == "ldouble"):
				self.__normal__[name] = "r#{}".format(len(self.__normal__))		## real
			elif (c_type == "point") or (c_type == "array"):
				self.__normal__[name] = "p#{}".format(len(self.__normal__))		## pointer
			elif (c_type.endswith("_x")) or (c_type.endswith("_i")):
				self.__normal__[name] = "x#{}".format(len(self.__normal__))		## complex
			elif c_type == "function":
				self.__normal__[name] = name									## function
			else:
				self.__normal__[name] = "s#{}".format(len(self.__normal__))		## struct|union
		normal_name = self.__normal__[name]
		normal_name: str
		return normal_name

	def __assert_unsigned__(self, expression):
		"""
		:param expression: which should be z3.expression object
		:return: expression itself
		"""
		if isinstance(expression, z3.ArithRef) or isinstance(expression, z3.IntNumRef) or isinstance(expression, z3.RatNumRef):
			self.__add_assume__(expression >= 0)
		elif isinstance(expression, z3.BitVecRef) or isinstance(expression, z3.BitVecNumRef):
			self.__add_assume__(expression >= z3.BitVecVal(0, self.longLength))
		return expression

	def __new_reference__(self, name: str, data_type: jcbase.CType):
		"""
		:param name: the original name of the reference
		:param data_type: the data type of the reference
		:return: z3.variable of the corresponding type with given name
		"""
		c_type = data_type.get_key()
		if (c_type == "void") or (c_type == "bool"):
			return z3.Bool(name)
		elif (c_type == "char") or (c_type == "uchar"):
			return self.__assert_unsigned__(z3.Int(name))
		elif (c_type == "short") or (c_type == "int") or (c_type == "long") or (c_type == "llong"):
			return z3.Int(name)
		elif (c_type == "ushort") or (c_type == "uint") or (c_type == "ulong") or (c_type == "ullong"):
			return self.__assert_unsigned__(z3.Int(name))
		elif (c_type == "float") or (c_type == "double") or (c_type == "ldouble"):
			return z3.Real(name)
		elif (c_type == "array") or (c_type == "point"):
			return self.__assert_unsigned__(z3.Int(name))
		elif c_type == "function":
			return z3.Function(name)
		else:
			return z3.BitVec(name, self.byteLength)

	def __cast_to_bool__(self, expression):
		"""
		:param expression: should be z3.expression object
		:return:
		"""
		if isinstance(expression, z3.ArithRef) or isinstance(expression, z3.RatNumRef) or isinstance(expression, z3.IntNumRef):
			return z3.Not(z3.eq(expression, z3.IntVal(0)))
		elif isinstance(expression, z3.BitVecRef) or isinstance(expression, z3.BitVecNumRef):
			return z3.Not(z3.eq(expression, z3.BitVecVal(0, self.longLength)))
		elif isinstance(expression, int) or isinstance(expression, float):
			return z3.BoolVal(expression != 0)
		else:
			return expression

	def __cast_to_bits__(self, expression):
		"""
		:param expression: should be z3.expression objects
		:return:
		"""
		if isinstance(expression, z3.BoolRef):
			return z3.If(expression, z3.BitVecVal(1, self.longLength), z3.BitVecVal(0, self.longLength))
		elif isinstance(expression, z3.ArithRef) or isinstance(expression, z3.IntNumRef):
			return z3.Int2BV(expression, self.longLength)
		elif isinstance(expression, int):
			return z3.BitVecVal(expression, self.longLength)
		else:
			return expression

	def __cast_to_numb__(self, expression):
		"""
		:param expression: should be z3.expression object
		:return:
		"""
		if isinstance(expression, z3.BoolRef):
			return z3.If(expression, z3.IntVal(1), z3.IntVal(0))
		elif isinstance(expression, int):
			return z3.IntVal(expression)
		elif isinstance(expression, float):
			return z3.RealVal(expression)
		elif isinstance(expression, z3.BitVecRef) or isinstance(expression, z3.BitVecNumRef):
			return z3.BV2Int(expression, self.signFlag)
		else:
			return expression

	## parse methods

	def __parse__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: the z3.expression parsed from input
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
			return self.__parse_cast_expression__(symbol_node)
		elif symbol_class == "FieldExpression":
			return self.__parse_field_expression__(symbol_node)
		elif symbol_class == "IfElseExpression":
			return self.__parse_if_else_expression__(symbol_node)
		elif symbol_class == "CallExpression":
			return self.__parse_call_expression__(symbol_node)
		else:
			return self.__parse_list_expression__(symbol_node)

	def __parse_identifier__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: identifier --> reference
		"""
		name = symbol_node.get_content().get_token_value()
		data_type = symbol_node.get_data_type()
		name = self.__normal_name__(name, data_type)
		return self.__new_reference__(name, data_type)

	def __parse_constant__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: constant --> BoolVal | ArithVal | BitVecVal | RatNumVal ...
		"""
		constant = symbol_node.get_content().get_token_value()
		if isinstance(constant, bool):
			return z3.BoolVal(constant)
		elif isinstance(constant, int):
			return z3.IntVal(constant)
		elif isinstance(constant, float):
			return z3.RealVal(constant)
		else:
			return self.__new_reference__(self.__unique_name__(symbol_node), symbol_node.get_data_type())

	def __parse_literal__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: literal --> z3.String
		"""
		return z3.String(self.__unique_name__(symbol_node))

	def __parse_unary_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node: neg|rsv|not|adr|der
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		operand = symbol_node.get_child(1)
		if operator == "negative":
			u_operand = self.__parse__(operand)
			return -self.__cast_to_numb__(u_operand)
		elif operator == "bit_not":
			u_operand = self.__parse__(operand)
			return ~self.__cast_to_bits__(u_operand)
		elif operator == "logic_not":
			u_operand = self.__parse__(operand)
			return z3.Not(self.__cast_to_bool__(u_operand))
		elif operator == "address_of":
			return self.__assert_unsigned__(z3.Const(self.__unique_name__(symbol_node), z3.IntSort()))
		else:
			name = self.__unique_name__(symbol_node)
			name = self.__normal_name__(name, symbol_node.get_data_type())
			return self.__new_reference__(name, symbol_node.get_data_type())

	def __parse_arith_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node: add|sub|mul|div|mod
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(1)))
		roperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(2)))
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
		:param symbol_node: and|ior|xor|lsh|rsh
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_bits__(self.__parse__(symbol_node.get_child(1)))
		roperand = self.__cast_to_bits__(self.__parse__(symbol_node.get_child(2)))
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
		"""
		:param symbol_node: and|ior|imp
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_bool__(self.__parse__(symbol_node.get_child(1)))
		roperand = self.__cast_to_bool__(self.__parse__(symbol_node.get_child(2)))
		if operator == "logic_and":
			return z3.And(loperand, roperand)
		elif operator == "logic_or":
			return z3.Or(loperand, roperand)
		else:
			return z3.Implies(loperand, roperand)

	def __parse_relation_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(1)))
		roperand = self.__cast_to_numb__(self.__parse__(symbol_node.get_child(2)))
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
		"""
		:param symbol_node: assign|imp_assign
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
		self.__save_state__(loperand.sexpr(), roperand)
		if operator == "assign":
			return roperand
		else:
			return loperand

	def __parse_cast_expression__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse__(symbol_node.get_child(1))

	def __parse_field_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: reference to the instance
		"""
		data_type = symbol_node.get_data_type()
		name = self.__normal_name__(self.__unique_name__(symbol_node), data_type)
		return self.__new_reference__(name, data_type)

	def __parse_if_else_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		condition = self.__parse__(symbol_node.get_child(0))
		l_operand = self.__parse__(symbol_node.get_child(1))
		r_operand = self.__parse__(symbol_node.get_child(2))
		return z3.If(self.__cast_to_bool__(condition), l_operand, r_operand)

	def __parse_list_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: it simulates the comma expression
		"""
		elements = list()
		for child in symbol_node.get_children():
			elements.append(self.__parse__(child))
		name = self.__unique_name__(symbol_node)
		return self.__new_reference__(name, symbol_node.get_data_type())

	def __parse_call_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: call expression
		"""
		## 1. derive the function name and arguments applied
		function = symbol_node.get_child(0)
		if function.get_class_name() == "Identifier":
			func_name = function.get_content().get_token_value()
		else:
			func_name = self.__unique_name__(function)
		arguments = symbol_node.get_child(1).get_children()

		## 2. generate the arguments by parsing-in-state
		for k in range(0, len(arguments)):
			arg = self.__parse__(arguments[k])
			self.__save_state__("{}#{}".format(func_name, k), arg)
		return self.__new_reference__(func_name, symbol_node.get_data_type())

	def parse(self, symbol_node: jcmuta.SymbolNode, stateBuffer, assumeLib, clear=False):
		"""
		:param symbol_node: the symbolic expression to be parsed
		:param stateBuffer: the map from reference to its values
		:param assumeLib: 	the set of assumptions being appended
		:param clear:		whether to clear the name-space
		:return: 			z3.expression instance being parsed
		"""
		## 1. initialization
		self.__states__.clear()
		self.__assume__.clear()
		if clear:
			self.__normal__.clear()

		## 2. parse the node
		try:
			res = self.__parse__(symbol_node)
		except z3.Z3Exception:
			res = None
		except AttributeError:
			res = None
		except TypeError:
			res = None

		## 3. update the states and assumptions
		if not (stateBuffer is None):
			for key, value in self.__states__.items():
				stateBuffer[key] = value
		if not (assumeLib is None):
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
			expression = parser.parse(symbol_node, dict(), set(), True)
			clas = symbol_node.get_class_name()
			data_type = symbol_node.get_data_type()
			content = symbol_node.get_content().get_token_value()
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
	print("\t{}:\tPASS = {}\tFAIL = {}\t ({}%)".format(project.program.name, past, fail, ratio))
	return


class SymbolToZ3Prover:
	"""
	It implements the equivalence proof based expression-level mutations.
	"""

	def __init__(self):
		"""
		It initializes the parameters for proof
		"""
		self.neq_flag = 0	# not-equivalent
		self.ceq_flag = 1	# complete-equivalent
		self.seq_flag = 2	# state-equivalent
		self.veq_flag = 3	# value-equivalent
		self.timeout = 1000
		self.parser = SymbolToZ3Parser()
		self.solutions = dict()  # ContextState --> class_flag
		return

	def __differ__(self, orig_value, muta_value):
		"""
		:param orig_value:
		:param muta_value:
		:return: differential condition
		"""
		self.timeout = self.timeout
		try:
			return z3.Not(z3.eq(orig_value, muta_value))
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
		if not (assumptions is None):
			for assumption in assumptions:
				solver.add(assumption)
		solver.set("timeout", self.timeout)
		if solver.check() == z3.unsat:
			return True
		return False

	def __check_const__(self, condition, assumptions):
		return self.__check__(condition, assumptions)

	def __check_value__(self, orig_value, muta_value):
		"""
		:param orig_value:
		:param muta_value:
		:return:
		"""
		return self.__check__(self.__differ__(orig_value, muta_value), set())

	def __check_state__(self, orig_states: dict, muta_states: dict):
		"""
		:param orig_states:
		:param muta_states:
		:return:
		"""
		for key, muta_value in muta_states.items():
			if key in orig_states:
				orig_value = orig_states[key]
				if self.__check__(self.__differ__(orig_value, muta_value), set()):
					pass
				else:
					return False
			else:
				pass
		# return False
		return True

	def __solve_eva_cond__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: NEQ|CEQ
		"""
		assumeLib = set()
		condition = self.parser.parse(state.get_loperand(), None, assumeLib, True)
		if condition is None:
			return self.neq_flag
		elif self.__check_const__(condition, assumeLib):
			return self.ceq_flag
		else:
			return self.neq_flag

	def __solve_set_expr__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: NEQ|VEQ|BEQ|SEQ|REQ
		"""
		## I. parse and construction
		orig_states, muta_states = dict(), dict()
		loperand = self.parser.parse(state.get_loperand(), orig_states, None, True)
		roperand = self.parser.parse(state.get_roperand(), muta_states, None, False)
		if (loperand is None) or (roperand is None):
			return self.neq_flag

		## II. context-directed proof
		location = state.get_location()
		parent = location.get_parent()
		if (parent is None) or (location.get_child_type() == "evaluate") or (location.get_child_type() == "element"):
			if self.__check_state__(orig_states, muta_states):
				return self.seq_flag
			else:
				return self.neq_flag
		elif parent.get_node_type() == "retr_stmt":
			if self.__check_value__(loperand, roperand):
				return self.veq_flag
			else:
				return self.neq_flag
		else:
			if self.__check_value__(loperand, roperand):
				if self.__check_state__(orig_states, muta_states):
					return self.ceq_flag
				else:
					return self.neq_flag
			else:
				return self.neq_flag

	def __solve__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: It solves the state and produce the class
		"""
		if state.get_category() == "eva_cond":
			return self.__solve_eva_cond__(state)
		elif state.get_category() == "set_expr":
			return self.__solve_set_expr__(state)
		else:
			return self.neq_flag

	def __prove__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: 0 (NEQ); 1 (CEQ); 2 (VEQ); 3 (SEQ); 4 (REQ).
		"""
		if not (state in self.solutions):
			self.solutions[state] = self.__solve__(state)
		solution = self.solutions[state]
		solution: int
		return solution

	def classify(self, project: jcmuta.CProject):
		"""
		:param project: of which mutations are classified
		:return: map{Mutant, (ContextState, int)}
		"""
		## initialization
		output = dict()	# Mutant --> (ContextState, int)
		total, counter, steps = len(project.context_space.get_mutants()), 0, 3000
		alive_number, state_solutions = 0, dict()
		print("\tCollect {} states from {} mutations in {}.".format(
			len(project.context_space.get_states()),
			len(project.context_space.get_mutants()), project.program.name))

		## Proof-Algorithm
		begTime = time.time()
		for mutation in project.context_space.get_mutations():
			## 1. print the procedure
			if counter % steps == 0:
				print("\t\t==> Proceed[{}/{}]".format(counter, total))
			counter += 1
			## 2. account for the alive
			if mutation.get_mutant().get_result().is_killed_in(None):
				continue		## not to account the killed ones
			alive_number = alive_number + 1
			## 3. state-based proof
			for state in mutation.get_states():
				state: jcmuta.ContextState
				__res__ = self.__prove__(state)
				if __res__ != self.neq_flag:
					output[mutation.get_mutant()] = (state, __res__)
					state_solutions[state] = __res__
					break
		endTime = time.time()
		seconds = int(endTime - begTime)

		## report-summary
		equal_number, state_number = len(output), len(state_solutions)
		ceq_number, seq_number, veq_number, ratio = 0, 0, 0, 0.0
		for mutant, state_class in output.items():
			__res__ = state_class[1]
			if __res__ == self.ceq_flag:
				ceq_number += 1
			elif __res__ == self.seq_flag:
				seq_number += 1
			elif __res__ == self.veq_flag:
				veq_number += 1
		ratio = len(output) / (alive_number + 0.001)
		ratio = int(ratio * 10000) / 100.0
		print("\tALV = {}\tEQV = {}\tSTA = {}\tTIM = {} s".format(alive_number, equal_number, state_number, seconds))
		print("\tCEQ = {}\tSEQ = {}\tVEQ = {}\tRAT = {} %".format(ceq_number, seq_number, veq_number, ratio))

		## return the classifier-maps
		return output

	@staticmethod
	def class_name(class_flag: int):
		if class_flag == 1:
			return "CEQ"
		elif class_flag == 2:
			return "SEQ"
		elif class_flag == 3:
			return "VEQ"
		else:
			return "NEQ"


def load_TEQ_results(project: jcmuta.CProject, tce_directory: str):
	"""
	:param project:
	:param tce_directory:
	:return: the set of integer IDs of mutants detected by TCE
	"""
	file_name = project.program.name
	file_path = os.path.join(tce_directory, file_name + ".txt")
	tce_ids = set()
	with open(file_path, 'r') as reader:
		for line in reader:
			if len(line.strip()) > 0:
				items = line.strip().split('\t')
				mid = items[0].strip()
				if mid.isdigit():
					tce_ids.add(int(mid))
	return tce_ids


def write_mutant_class_state(project: jcmuta.CProject, mutant_state_class: dict, tce_ids: set, file_path: str):
	"""
	:param project: the mutation testing project
	:param mutant_state_class: dict[Mutant; (ContextState, int)]
	:param tce_ids: the set of integer IDs of mutants detected by TCE technique
	:param file_path:
	:return:
	"""
	with open(file_path, 'w') as writer:
		writer.write("MID\tCLAS\tOPRT\tLINE\tCODE\tPARM\tTCE\tTYPE\tCATE\tLOCT\tLVAL\tRVAL\n")
		for mutant in project.muta_space.get_mutants():
			## 1. mutation information
			mutant: jcmuta.Mutant
			mid = mutant.get_muta_id()
			mu_class = mutant.get_mutation().get_mutation_class()
			mu_operator = mutant.get_mutation().get_mutation_operator()
			location = mutant.get_mutation().get_location()
			mu_line = location.line_of(False)
			mu_code = location.generate_code(96)
			mu_parameter = str(mutant.get_mutation().get_parameter())
			writer.write("{}\t{}\t{}\t{}\t\"{}\"\t{}\t{}".
						 format(mid, mu_class, mu_operator, mu_line, mu_code, mu_parameter, mid in tce_ids))

			## 2. state-class information
			if mutant in mutant_state_class:
				state = mutant_state_class[mutant][0]
				flags = mutant_state_class[mutant][1]
				state: jcmuta.ContextState
				flags: int
				category = state.get_category()
				location = state.get_location().get_node_type()
				loperand = state.get_loperand().get_code()
				roperand = state.get_roperand().get_code()
				writer.write("\t{}".format(SymbolToZ3Prover.class_name(flags)))
				writer.write("\t{}\t{}\t({})\t({})".format(category, location, loperand, roperand))
			else:
				writer.write("\tNEQ")
			writer.write("\n")
	return


def test_symbol_prover(project: jcmuta.CProject, file_path: str):
	prover = SymbolToZ3Prover()
	tce_ids = load_TEQ_results(project, "/home/dzt2/Development/Data/zexp/TCE")
	print("Testing {} with {} mutants and {} TCE.".
		  format(project.program.name, len(project.muta_space.get_mutants()), len(tce_ids)))
	mutant_state_class = prover.classify(project)
	write_mutant_class_state(project, mutant_state_class, tce_ids, file_path)
	print()
	return


def get_file_names_in(directory: str):
	file_names = list()
	for file_name in os.listdir(directory):
		file_names.append(file_name)
	file_names.sort()
	return file_names


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/featuresAll"
	post_path = "/home/dzt2/Development/Data/zexp/resultsAll"
	for project_name in get_file_names_in(root_path):
		if project_name == "md4":
			continue
		project_directory = os.path.join(root_path, project_name)
		c_project = jcmuta.CProject(project_directory, project_name)
		# test_symbol_parser(c_project, os.path.join(post_path, project_name + ".sz3"))
		test_symbol_prover(c_project, os.path.join(post_path, project_name + ".mz3"))
	print("Testing End...")

