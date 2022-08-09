"""This file implements the equivalence proof for expression-level mutations."""


import os, time, z3
from typing import Tuple

import com.jcsa.z3proof.libs.base as jcbase
import com.jcsa.z3proof.libs.code as jccode
import com.jcsa.z3proof.libs.muta as jcmuta


##	initialization algorithms


def get_file_names_in(directory: str):
	"""
	:param directory: the directory where the file names are defined
	:return: 		  the sorted list of file names in the directory
	"""
	file_names = list()
	for file_name in os.listdir(directory):
		file_names.append(file_name)
	file_names.sort()
	return file_names


def reset_mutation_results(mutants, reset_result=""):
	"""
	:param mutants: the set of Mutant or ContextMutation of which result will be reset
	:param reset_result: the string of test result to be reset to the input mutations
	:return: the set of Mutant(s) of which results are reset as the given reset result
	"""
	reset_mutants = set()
	for mutant in mutants:
		if isinstance(mutant, jcmuta.ContextMutation):
			reset_mutants.add(mutant.get_mutant())
		elif isinstance(mutant, jcmuta.Mutant):
			reset_mutants.add(mutant)
		else:
			pass
	for reset_mutant in reset_mutants:
		reset_mutant.get_result().result = reset_result
	return reset_mutants


def read_TCE_mutants_in(tce_directory: str, project: jcmuta.CProject):
	"""
	:param tce_directory: the directory where the TCE results are preserved
	:param project: the mutation test project where the mutants are derived
	:return: the set of mutants detected by TCE approach in the given input
	"""
	file_path = os.path.join(tce_directory, project.program.name + ".txt")
	tce_mutants = set()
	with open(file_path, 'r') as reader:
		for line in reader:
			if len(line.strip()) > 0:
				items = line.strip().split('\t')
				header = items[0].strip()
				if header.isdigit():
					mutant = project.muta_space.get_mutant(int(header))
					tce_mutants.add(mutant)
	return tce_mutants


## parse SymbolNode to z3.Expression


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


def test_symbol_z3_parser(project: jcmuta.CProject, file_path: str):
	parser = SymbolToZ3Parser()
	passed, failed, ratio = 0, 0, 0.0
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
				passed += 1
			else:
				writer.write("???")
				failed += 1
			writer.write("\n")
	ratio = passed / (passed + failed)
	ratio = int(ratio * 10000) / 100.0
	print("\t[Sym-Z3]:\tPASS = {}\tFAIL = {}\t ({}%)".format(passed, failed, ratio))
	return


## 	prove expression-level equivalence by z3


class SymbolToZ3Prover:
	"""
	It implements the equivalence proof for expression mutation using Z3.
	"""

	## constructor

	def __init__(self):
		"""
		initialization
		"""
		self.parser = SymbolToZ3Parser()					# symbol-z3 parser
		self.solutions = dict()								# string --> xxx
		self.neq_class = 0									# non-equivalent
		self.veq_class = 1									# value-equivalent
		self.beq_class = 2									# all-equivalent
		self.seq_class = 3									# state-equivalent
		self.class_names = ["NEQ", "VEQ", "BEQ", "SEQ"]		# names of equivalence class
		self.timeout = 1000									# maximal timeout
		return

	## internal z3

	def get_class_name(self, class_flag: int):
		if (class_flag < 0) or (class_flag >= len(self.class_names)):
			return "NEQ"
		else:
			return self.class_names[class_flag]

	def __check_unsat__(self, condition, assumptions):
		"""
		:param condition: 	the z3.Expression to denote the symbolic condition being verified
		:param assumptions: the set of z3.Expressions used to the verification as backgrounds
		:return: 			True if the condition is proved as unsatisfiable
		"""
		solver = z3.Solver()
		solver.add(condition)
		if not (assumptions is None):
			for assumption in assumptions:
				solver.add(assumption)
		solver.set("timeout", self.timeout)
		return solver.check() == z3.unsat

	def __check_equal__(self, orig_value, muta_value):
		"""
		:param orig_value: the z3.Expression to denote the original version
		:param muta_value: the z3.Expression to denote the mutation version
		:return: True if the two expressions are proved as being equivalent
		"""
		try:
			condition = z3.Not(z3.eq(orig_value, muta_value))
		except z3.z3types.Z3Exception:
			return False
		except AttributeError:
			return False
		except TypeError:
			return False
		return self.__check_unsat__(condition, set())

	## constraint

	def __prove_constraint__(self, constrain: jcmuta.SymbolNode):
		"""
		:param constrain: the symbolic constrain being verified
		:return: True if the input constrain is non-satisfiable
		"""
		assumptions = set()
		condition = self.parser.parse(constrain, None, assumptions, True)
		if condition is None:
			return False
		return self.__check_unsat__(condition, assumptions)

	def prove_constraint(self, constrain: jcmuta.SymbolNode):
		"""
		:param constrain: the symbolic constrain being verified
		:return: True if the input constrain is non-satisfiable
		"""
		key = constrain.get_code()
		if not (key in self.solutions):
			self.solutions[key] = self.__prove_constraint__(constrain)
		result = self.solutions[key]
		result: bool
		return result

	## difference

	def __prove_difference__(self, orig_expression: jcmuta.SymbolNode, muta_expression: jcmuta.SymbolNode):
		"""
		:param orig_expression: the symbolic expression to denote the original version
		:param muta_expression: the symbolic expression to denote the mutation version
		:return: value_equal, state_equal, has_effects
		"""
		## parse and evaluate
		orig_states, muta_states = dict(), dict()
		orig_value = self.parser.parse(orig_expression, orig_states, None, True)
		muta_value = self.parser.parse(muta_expression, muta_states, None, False)
		if (orig_value is None) or (muta_value is None):
			return False, False, len(orig_states) > 0 or len(muta_states) > 0
		## equivalence proof
		value_equal = self.__check_equal__(orig_value, muta_value)
		state_equal = True
		for key, muta_state in muta_states.items():
			if key in orig_states:
				orig_state = orig_states[key]
				if not self.__check_equal__(orig_state, muta_state):
					state_equal = False
					break
			else:
				state_equal = False
				break
		return value_equal, state_equal, len(orig_states) > 0 or len(muta_states) > 0

	def prove_difference(self, orig_expression: jcmuta.SymbolNode, muta_expression: jcmuta.SymbolNode):
		"""
		:param orig_expression: the symbolic expression to denote the original version
		:param muta_expression: the symbolic expression to denote the mutation version
		:return: 				value_equal, state_equal, has_effects
		"""
		key = "({})@({})".format(orig_expression.get_code(), muta_expression.get_code())
		if not (key in self.solutions):
			self.solutions[key] = self.__prove_difference__(orig_expression, muta_expression)
		solution = self.solutions[key]
		solution: Tuple[bool, bool, bool]
		value_equal = solution[0]
		state_equal = solution[1]
		has_effects = solution[2]
		return value_equal, state_equal, has_effects

	## set-expression

	@staticmethod
	def __is_top_expression__(location: jccode.AstCirNode):
		"""
		:param location:
		:return: whether the location is top-level expression
		"""
		parent = location.get_parent()
		child_type = location.get_child_type()
		return (parent is None) or (child_type == "evaluate") or (child_type == "element") or (child_type == "n_condition")

	def prove_set_expression(self, location: jccode.AstCirNode,
							 orig_expression: jcmuta.SymbolNode,
							 muta_expression: jcmuta.SymbolNode):
		"""
		:param location: 		the location in which the expression will be mutated
		:param orig_expression:	original expression in symbolic form
		:param muta_expression:	mutation expression in symbolic form
		:return:				NEQ | VEQ | BEQ | SEQ
		"""
		value_equal, state_equal, has_effects = self.prove_difference(orig_expression, muta_expression)
		if has_effects:
			if state_equal:
				if value_equal:
					return self.beq_class
				elif SymbolToZ3Prover.__is_top_expression__(location):
					return self.seq_class
				else:
					return self.neq_class
			elif location.get_parent().get_node_type() == "retr_stmt":
				if value_equal:
					return self.veq_class
				else:
					return self.neq_class
			else:
				return self.neq_class
		else:
			if value_equal:
				return self.veq_class
			elif self.__is_top_expression__(location):
				return self.seq_class
			else:
				return self.neq_class

	## state-based

	def __prove_eva_cond__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: NEQ | VEQ
		"""
		if self.prove_constraint(state.get_loperand()):
			return self.veq_class
		else:
			return self.neq_class

	def __prove_set_expr__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return:
		"""
		return self.prove_set_expression(state.get_location(), state.get_loperand(), state.get_roperand())

	def __prove_state__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: NEQ | VEQ | BEQ | SEQ
		"""
		if state.get_category() == "eva_cond":
			return self.__prove_eva_cond__(state)
		elif state.get_category() == "set_expr":
			return self.__prove_set_expr__(state)
		else:
			return self.neq_class

	def prove_state(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return:
		"""
		key = str(state)
		if not (key in self.solutions):
			self.solutions[key] = self.__prove_state__(state)
		solution = self.solutions[key]
		solution: int
		return solution

	## mutation-based

	def prove_mutation(self, mutation: jcmuta.ContextMutation):
		"""
		:param mutation:
		:return: result, state, class(int)
		"""
		result, equal_state, class_flag = False, None, self.neq_class
		for state in mutation.get_states():
			__result__ = self.prove_state(state)
			if __result__ != self.neq_class:
				result = True
				equal_state = state
				class_flag = __result__
				break
		return result, equal_state, class_flag

	def do_clustering(self, source: jcmuta.ContextMutation, targets):
		"""
		:param source: the source mutation to be compared with
		:param targets: the set of target mutations to compare
		:return: the set of mutation clustered with the source
		"""
		source_expressions = dict()
		for state in source.get_states():
			if state.get_category() == "set_expr":
				source_expressions[state.get_location()] = state.get_roperand()
		new_cluster = set()
		new_cluster.add(source)
		if (len(source_expressions) > 0) and (len(targets) > 0):
			for target in targets:
				target: jcmuta.ContextMutation
				for state in target.get_states():
					if state.get_category() == "set_expr":
						location = state.get_location()
						if location in source_expressions:
							source_expression = source_expressions[location]
							target_expression = state.get_roperand()
							result = self.prove_set_expression(location, source_expression, target_expression)
							if result != self.neq_class:
								new_cluster.add(target)
								break
		return new_cluster


def classify_expression_equivalence(project: jcmuta.CProject):
	"""
	:param project: mutation testing project where the mutants are classified
	:return: dict[Mutant |-> (ContextState, int)]
	"""
	## 	1. initialization
	output, theorem_prover = dict(), SymbolToZ3Prover()
	alive_number, equal_number, equal_states = 0, 0, set()
	veq_number, seq_number, beq_number, ratio = 0, 0, 0, 0.0
	counter, total, steps = 0, len(project.context_space.get_mutations()), 3000

	##	2.	proof algorithm
	beg_time = time.time()
	print("\t[Mut-Pv]\tStart to prove equivalence for {} mutants...".format(total))
	for mutation in project.context_space.get_mutations():
		## 2-A. report the current proceeding and mutation information
		if counter % steps == 0:
			mutant = mutation.get_mutant()
			mu_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.get_mutation().get_mutation_operator()
			location = mutant.get_mutation().get_location()
			print("\t\t==> Proof[{}/{}]\t{}\t{}\t\"{}\"".
				  format(counter, total, mu_class, operator, location.generate_code(32)))
		counter += 1
		is_equal, state, result = theorem_prover.prove_mutation(mutation)

		## 2-B. account for the equivalence counters
		if not mutation.get_mutant().get_result().is_killed_in(None):
			alive_number += 1
		if is_equal:
			state: jcmuta.ContextState
			result: int
			equal_number += 1
			equal_states.add(state)
			if result == theorem_prover.veq_class:
				veq_number += 1
			elif result == theorem_prover.seq_class:
				seq_number += 1
			else:
				beq_number += 1
			output[mutation.get_mutant()] = (state, result)
	end_time = time.time()
	seconds = int(end_time - beg_time)

	## 3. report the classification results
	if equal_number > 0:
		ratio = equal_number / (alive_number + 0.0)
	ratio = int(ratio * 10000) / 100.0
	print("\t[Eqv-Ct]\tALV = {};\tEQV = {};\tSTA = {};\tRAT = {} %".
		  format(alive_number, equal_number, len(equal_states), ratio))
	print("\t[Mut-Cs]\tVEQ = {};\tBEQ = {};\tSEQ = {};\tTIM = {} s".
		  format(veq_number, beq_number, seq_number, seconds))
	return output


def cluster_expression_duplication(project: jcmuta.CProject, tce_mutants: set, exp_mutants):
	"""
	:param project:
	:param tce_mutants: the equivalent mutants being detected using TCE technique
	:param exp_mutants: the equivalent mutants being detected at expression-level
	:return:	set[set[Mutant]]
	"""
	## initialization
	theorem_prover, nonclass_mutations = SymbolToZ3Prover(), set()
	for mutation in project.context_space.get_mutations():
		if (mutation.get_mutant() in tce_mutants) or (mutation.get_mutant() in exp_mutants):
			continue
		else:
			nonclass_mutations.add(mutation)
	counter, total, steps, clusters = 0, len(nonclass_mutations), 3000, list()

	## duplicate-clustering
	print("\t[Mut-Cl]\tCluster {} uncovered mutants".format(len(nonclass_mutations)))
	while len(nonclass_mutations) > 0:
		source_mutation = next(iter(nonclass_mutations))
		nonclass_mutations.remove(source_mutation)
		new_cluster = theorem_prover.do_clustering(source_mutation, nonclass_mutations)
		nonclass_mutations = nonclass_mutations - new_cluster
		clusters.append(new_cluster)
		counter += len(new_cluster)
		if counter >= steps:
			print("\t\tCluster[{}/{}]\tCluster\t{}".format(counter, total, len(new_cluster)))
			steps += 3000
	all_number, cluster_number = total, len(clusters)
	ratio = (all_number - cluster_number) / (all_number + 0.0)
	ratio = int(ratio * 10000) / 100.0
	print("\t[Mut-Rd]\tALL = {};\tCST = {};\tDUP = {};\tRED = {}%".
		  format(all_number, cluster_number, all_number - cluster_number, ratio))
	return encode_mutation_clusters(clusters)


def encode_mutation_clusters(clusters):
	"""
	:param clusters:
	:return: dict[Mutant --> int]
	"""
	output = dict()
	key = 0
	for cluster in clusters:
		for mutation in cluster:
			mutation: jcmuta.ContextMutation
			output[mutation.get_mutant()] = key
		key += 1
	return output


def write_mutant_classification(project: jcmuta.CProject, tce_mutants: set,
								mutant_state_dict: dict, mutant_cluster: dict,
								file_path: str):
	"""
	:param project:				mutation testing project of which mutants are printed
	:param tce_mutants: 		the set of mutants being detected by trivial compiler
	:param mutant_state_dict: 	the dict from Mutants to (ContextState, int) classes
	:param mutant_cluster:		the dict from Mutant to integer key of its cluster
	:param file_path:			the path of output file to write
	:return:					MID CLAS OPRT PARM FUNC LINE CODE TCE CUT MEX CATE LOCT LCOD LOPD ROPD
	"""
	with open(file_path, 'w') as writer:
		theorem_prover = SymbolToZ3Prover()
		writer.write("ID\tCLAS\tOPRT\tPARM\tFUNC\tLINE\tCODE\tTCE\tCUT\tMEX\tCATE\tLOCT\tLOPD\tROPD\n")
		for mutant in project.muta_space.get_mutants():
			##	ID CLAS OPRT PARM FUNC LINE CODE TCE
			mutant: jcmuta.Mutant
			mid = mutant.get_muta_id()
			mu_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.get_mutation().get_mutation_operator()
			location = mutant.get_mutation().get_location()
			parameter = str(mutant.get_mutation().get_parameter())
			func_name = location.get_function_name()
			code_line = location.line_of(False)
			code_text = location.generate_code(64)
			has_tce = mutant in tce_mutants
			writer.write("{}\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}".format(
				mid, mu_class, operator, parameter, func_name, code_line, code_text, has_tce))
			if mutant in mutant_cluster:
				writer.write("\t{}".format(mutant_cluster[mutant]))
			else:
				writer.write("\t ")
			##	MEX CATE LOCT LOPD ROPD
			if mutant in mutant_state_dict:
				state = mutant_state_dict[mutant][0]
				flags = mutant_state_dict[mutant][1]
				class_name = theorem_prover.get_class_name(flags)
				state: jcmuta.ContextState
				category = state.get_category()
				location = "{}#{}".format(state.get_location().get_node_type(), state.get_location().get_node_id())
				loperand = state.get_loperand().get_code()
				roperand = state.get_roperand().get_code()
				writer.write("\t{}\t{}\t{}\t({})\t({})".format(class_name, category, location, loperand, roperand))
			else:
				writer.write("\t{}".format(theorem_prover.get_class_name(-1)))
			writer.write("\n")
		writer.write("\n")
	return


def test_symbol_z3_prover(project: jcmuta.CProject, tce_mutants, file_path: str):
	"""
	:param project:
	:param tce_mutants:
	:param file_path:
	:return:
	"""
	mutant_state_class = classify_expression_equivalence(project)
	mutant_key_cluster = dict()
	# mutant_key_cluster = cluster_expression_duplication(project, tce_mutants, mutant_state_class)
	write_mutant_classification(project, tce_mutants, mutant_state_class, mutant_key_cluster, file_path)
	return mutant_state_class, mutant_key_cluster


## main script


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/featuresAll"
	post_path = "/home/dzt2/Development/Data/zexp/resultsAll"
	# root_path = "/home/dzt2/Development/Data/zexp/features"
	# post_path = "/home/dzt2/Development/Data/zexp/results"
	tce_path = "/home/dzt2/Development/Data/zexp/TCE"
	index, project_names = 0, get_file_names_in(root_path)
	for project_name in project_names:
		index += 1
		print("{}.\tTesting on project {}.".format(index, project_name))
		if project_name != "md4":
			project_directory = os.path.join(root_path, project_name)
			c_project = jcmuta.CProject(project_directory, project_name)
			tce_set = read_TCE_mutants_in(tce_path, c_project)
			test_symbol_z3_parser(c_project, os.path.join(post_path, project_name + ".sz3"))
			test_symbol_z3_prover(c_project, tce_set, os.path.join(post_path, project_name + ".epv"))
			# test_mutation_patterns(c_project, None, 2, 0.60, tce_mutants, os.path.join(post_path, project_name + ".mpt"))
		print()
	print("Testing end for all.")

