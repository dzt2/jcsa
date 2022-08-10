"""This file implements the equivalence proof at expression-level using z3 theorem prover."""


import os, time, z3, datetime
from typing import Tuple, Set

import com.jcsa.z3proof.libs.base as jcbase
import com.jcsa.z3proof.libs.code as jccode
import com.jcsa.z3proof.libs.muta as jcmuta


##	initialization functions


def get_file_names_in(directory: str):
	"""
	:param directory:	the directory in which the file names will be derived
	:return: 			the sorted list of file names be derived in directory
	"""
	file_names = list()
	for file_name in os.listdir(directory):
		file_names.append(file_name)
	file_names.sort()
	return file_names


def clear_mutant_results(mutants):
	"""
	:param mutants:	the set of Mutant(s) or ContextMutation(s) being cleared
	:return: 		the set of Mutant(s) of which test results will be clear
	"""
	clear_mutants = set()
	for mutant in mutants:
		if isinstance(mutant, jcmuta.ContextMutation):
			target = mutant.get_mutant()
		elif isinstance(mutant, jcmuta.Mutant):
			target = mutant
		else:
			continue
		target.get_result().reset("")
		clear_mutants.add(target)
	return clear_mutants


def read_TCE_mutants_in(tce_directory: str, project: jcmuta.CProject):
	"""
	:param tce_directory:	the directory where the TCE result files are preserved
	:param project: 		the mutation testing project to read the tce mutations
	:return: 				the set of Mutant(s) being detected using TCE approach
	"""
	program_name = project.program.name
	file_path = os.path.join(tce_directory, program_name + ".txt")
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


##	symbolic node to z3 parser


class SymbolToZ3Parser:
	"""
	It implements the parse from SymbolNode to z3.Expression using different format.
	"""

	def __init__(self):
		"""
		initialization of parser
		"""
		self.__state_map__ = dict()			##	string --> z3.expr
		self.__args_dict__ = dict()			##	fun#id --> list[z3.expr]
		self.__assumeLib__ = set()			##	the set of z3.expr
		self.__name_temp__ = "{}_{}{}"  	## 	generate unique name
		self.__name_dict__ = dict()			##	string --> normal name
		self.boolBytes = 1					##	the number of bits to bool
		self.charBytes = 8					##	the number of bits to char
		self.shortBytes = 16				##	the number of bits to short
		self.intBytes = 32					##	the number of bits to int
		self.longBytes = 64					##	the number of bits to int
		self.realBytes = 64					## 	the number of bits to float
		self.addrBytes = 64					##	the number of bits to address
		self.byteBytes = 128				##	the length of struct | union
		self.signFlag = True				## 	used in bitwise-arith transform
		return

	##	internal methods

	def __save_state__(self, key, value):
		"""
		:param key: 	the string key of the reference
		:param value: 	the z3.expr to denote the value
		:return:
		"""
		self.__state_map__[str(key)] = value
		return

	def __add_assume__(self, assumption):
		"""
		:param assumption: the z3.expr to denote a background assumption
		:return:
		"""
		if not (assumption is None):
			self.__assumeLib__.add(assumption)
		return

	def __unique_name__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node: the symbolic node to be encoded with a unique name
		:return: the unique name of the symbolic node to be encoded in the way
		"""
		class_name = symbol_node.get_class_name()
		class_id = symbol_node.get_class_id()
		if class_id < 0:
			class_flag = 'n'
			class_id = abs(class_id)
		else:
			class_flag = 'p'
		return self.__name_temp__.format(class_name, class_flag, class_id)

	def __put_argList__(self, func_key: str, arg_list: list):
		"""
		:param func_key: 	the symbolic node to specify the function identifier
		:param arg_list: 	the list of arguments to call the function specifier
		:return: 			it will update internal states of self.__args_dict__
		"""
		self.__args_dict__[func_key] = arg_list
		return

	def __set_domain__(self, expression: z3.ExprRef, min_value: z3.ExprRef, max_value: z3.ExprRef):
		"""
		:param expression:
		:param min_value:
		:param max_value:
		:return:
		"""
		if isinstance(expression, z3.ArithRef):
			self.__add_assume__(expression.__ge__(min_value))
			self.__add_assume__(expression.__le__(max_value))
		elif isinstance(expression, z3.BitVecRef):
			self.__add_assume__(expression.__ge__(min_value))
			self.__add_assume__(expression.__le__(max_value))
		elif isinstance(expression, z3.FPRef):
			self.__add_assume__(expression.__ge__(min_value))
			self.__add_assume__(expression.__le__(max_value))
		return expression

	@staticmethod
	def __get_domain__(sign: bool, bits: int):
		"""
		:param sign:
		:param bits:
		:return: min_value, max_value
		"""
		if sign:
			min_value = -(2 << (bits - 1))
			max_value = (2 << (bits - 1) - 1)
		else:
			min_value = 0
			max_value = (2 << bits)
		return z3.IntVal(min_value), z3.IntVal(max_value)

	##	basic methods

	def __new_reference__(self, name: str, data_type: jcbase.CType):
		"""
		:param name: 		the name of the reference in z3
		:param data_type: 	the type of the reference in z3
		:return:
		"""
		c_type = data_type.get_key()
		if (c_type == "void") or (c_type == "bool"):
			return z3.Bool(name)
		elif (c_type == "char") or (c_type == "uchar"):
			min_value, max_value = SymbolToZ3Parser.__get_domain__(False, self.charBytes)
			return self.__set_domain__(z3.Int(name), min_value, max_value)
		elif (c_type == "short") or (c_type == "ushort"):
			min_value, max_value = SymbolToZ3Parser.__get_domain__(c_type == "short", self.shortBytes)
			return self.__set_domain__(z3.Int(name), min_value, max_value)
		elif (c_type == "int") or (c_type == "uint"):
			min_value, max_value = SymbolToZ3Parser.__get_domain__(c_type == "int", self.intBytes)
			return self.__set_domain__(z3.Int(name), min_value, max_value)
		elif (c_type == "long") or (c_type == "ulong"):
			min_value, max_value = SymbolToZ3Parser.__get_domain__(c_type == "long", self.longBytes)
			return self.__set_domain__(z3.Int(name), min_value, max_value)
		elif (c_type == "llong") or (c_type == "ullong"):
			min_value, max_value = SymbolToZ3Parser.__get_domain__(c_type == "llong", self.longBytes)
			return self.__set_domain__(z3.Int(name), min_value, max_value)
		elif (c_type == "float") or (c_type == "double") or (c_type == "ldouble"):
			return z3.Real(name)
		elif (c_type == "array") or (c_type == "point"):
			min_value, max_value = SymbolToZ3Parser.__get_domain__(False, self.addrBytes)
			return self.__set_domain__(z3.Int(name), min_value, max_value)
		elif c_type == "function":
			return z3.Function(name)
		else:
			return z3.BitVec(name, self.byteBytes)

	def __cast_to_logic__(self, expression: z3.ExprRef):
		"""
		:param expression:
		:return:
		"""
		self.__add_assume__(None)
		if isinstance(expression, z3.BoolRef):
			return expression
		elif isinstance(expression, z3.ArithRef):
			return expression.__ne__(z3.IntVal(0))
		elif isinstance(expression, z3.BitVecRef):
			return expression.__ne__(z3.BitVecVal(0, expression.size()))
		elif isinstance(expression, z3.FPRef):
			return expression.__ne__(0)
		elif isinstance(expression, int) or isinstance(expression, float):
			return z3.BoolVal(expression != 0)
		else:
			return expression

	def __cast_to_arith__(self, expression: z3.ExprRef):
		"""
		:param expression:
		:return:
		"""
		if isinstance(expression, z3.BoolRef):
			return z3.If(expression, z3.IntVal(1), z3.IntVal(0))
		elif isinstance(expression, z3.ArithRef):
			return expression
		elif isinstance(expression, z3.BitVecRef):
			return z3.BV2Int(expression, self.signFlag)
		elif isinstance(expression, bool):
			return z3.If(z3.BoolVal(expression), z3.IntVal(1), z3.IntVal(0))
		elif isinstance(expression, int):
			return z3.IntVal(expression)
		elif isinstance(expression, float):
			return z3.RealVal(expression)
		else:
			return expression

	def __cast_to_bitws__(self, expression: z3.ExprRef):
		"""
		:param expression:
		:return:
		"""
		if isinstance(expression, z3.BoolRef):
			return z3.If(expression, z3.BitVecVal(1, self.longBytes), z3.BitVecVal(0, self.longBytes))
		elif isinstance(expression, z3.ArithRef):
			return z3.Int2BV(expression, self.longBytes)
		elif isinstance(expression, z3.BitVecRef):
			return expression
		elif isinstance(expression, bool):
			return z3.If(z3.BoolVal(expression), z3.BitVecVal(1, self.longBytes), z3.BitVecVal(0, self.longBytes))
		elif isinstance(expression, int):
			return z3.BitVecVal(expression, self.longBytes)
		else:
			return expression

	##	recursive parse

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
		:return:
		"""
		name = symbol_node.get_content().get_token_value()
		data_type = symbol_node.get_data_type()
		return self.__new_reference__(name, data_type)

	def __parse_constant__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
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
		:return:
		"""
		return z3.String(self.__unique_name__(symbol_node))

	def __parse_unary_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		operand = symbol_node.get_child(1)
		if operator == "negative":
			u_operand = self.__parse__(operand)
			u_operand = self.__cast_to_arith__(u_operand)
			return u_operand.__neg__()
		elif operator == "bit_not":
			u_operand = self.__parse__(operand)
			u_operand = self.__cast_to_bitws__(u_operand)
			return u_operand.__invert__()
		elif operator == "logic_not":
			u_operand = self.__parse__(operand)
			u_operand = self.__cast_to_logic__(u_operand)
			return z3.Not(u_operand)
		elif operator == "address_of":
			min_value, max_value = SymbolToZ3Parser.__get_domain__(False, self.addrBytes)
			u_operand = self.__new_reference__(self.__unique_name__(symbol_node), symbol_node.get_data_type())
			return self.__set_domain__(u_operand, min_value, max_value)
		else:
			return self.__new_reference__(self.__unique_name__(symbol_node), symbol_node.get_data_type())

	def __parse_arith_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_arith__(self.__parse__(symbol_node.get_child(1)))
		roperand = self.__cast_to_arith__(self.__parse__(symbol_node.get_child(2)))
		if operator == "arith_add":
			return loperand.__add__(roperand)
		elif operator == "arith_sub":
			return loperand.__sub__(roperand)
		elif operator == "arith_mul":
			return loperand.__mul__(roperand)
		elif operator == "arith_div":
			return loperand.__div__(roperand)
		else:
			return loperand.__mod__(roperand)

	def __parse_bitws_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_bitws__(self.__parse__(symbol_node.get_child(1)))
		roperand = self.__cast_to_bitws__(self.__parse__(symbol_node.get_child(2)))
		if operator == "bit_and":
			return loperand.__and__(roperand)
		elif operator == "bit_or":
			return loperand.__or__(roperand)
		elif operator == "bit_xor":
			return loperand.__xor__(roperand)
		elif operator == "left_shift":
			return loperand.__lshift__(roperand)
		else:
			return loperand.__rshift__(roperand)

	def __parse_logic_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		loperand = self.__cast_to_logic__(self.__parse__(symbol_node.get_child(1)))
		roperand = self.__cast_to_logic__(self.__parse__(symbol_node.get_child(2)))
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
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
		if operator == "greater_tn":
			loperand = self.__cast_to_arith__(loperand)
			roperand = self.__cast_to_arith__(roperand)
			return loperand.__gt__(roperand)
		elif operator == "greater_eq":
			loperand = self.__cast_to_arith__(loperand)
			roperand = self.__cast_to_arith__(roperand)
			return loperand.__ge__(roperand)
		elif operator == "smaller_tn":
			loperand = self.__cast_to_arith__(loperand)
			roperand = self.__cast_to_arith__(roperand)
			return loperand.__lt__(roperand)
		elif operator == "smaller_eq":
			loperand = self.__cast_to_arith__(loperand)
			roperand = self.__cast_to_arith__(roperand)
			return loperand.__le__(roperand)
		elif operator == "equal_with":
			if isinstance(loperand, z3.BoolRef) or isinstance(roperand, z3.BoolRef):
				loperand = self.__cast_to_logic__(loperand)
				roperand = self.__cast_to_logic__(roperand)
			else:
				loperand = self.__cast_to_arith__(loperand)
				roperand = self.__cast_to_arith__(roperand)
			return loperand.__eq__(roperand)
		else:
			if isinstance(loperand, z3.BoolRef) or isinstance(roperand, z3.BoolRef):
				loperand = self.__cast_to_logic__(loperand)
				roperand = self.__cast_to_logic__(roperand)
			else:
				loperand = self.__cast_to_arith__(loperand)
				roperand = self.__cast_to_arith__(roperand)
			return loperand.__ne__(roperand)

	def __parse_cast_expression__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse__(symbol_node.get_child(1))

	def __parse_field_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: reference to the instance
		"""
		data_type = symbol_node.get_data_type()
		name = self.__unique_name__(symbol_node)
		return self.__new_reference__(name, data_type)

	def __parse_if_else_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		condition = self.__parse__(symbol_node.get_child(0))
		l_operand = self.__parse__(symbol_node.get_child(1))
		r_operand = self.__parse__(symbol_node.get_child(2))
		return z3.If(self.__cast_to_logic__(condition), l_operand, r_operand)

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
		arg_list = list()
		for k in range(0, len(arguments)):
			arg_list.append(self.__parse__(arguments[k]))
		func_var = self.__new_reference__(func_name, symbol_node.get_data_type())
		self.__put_argList__(func_var.sexpr(), arg_list)
		return func_var

	##	interface

	def parse(self, symbol_node: jcmuta.SymbolNode, stateMap: dict, assumeLib: set, argDict: dict):
		"""
		:param symbol_node:	the symbolic expression to be parsed to z3.expr
		:param stateMap:	to preserve reference to its value in z3 format
		:param assumeLib:	the set of z3.expr as the background assumption
		:param argDict:		to preserve the function to its actual argument
		:return:			z3.expr or None if failed
		"""
		##	1. initialization
		self.__state_map__.clear()
		self.__assumeLib__.clear()
		self.__args_dict__.clear()
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
		if not (stateMap is None):
			for key, value in self.__state_map__.items():
				stateMap[key] = value
		if not (assumeLib is None):
			for assumption in self.__assumeLib__:
				assumeLib.add(assumption)
		if not (argDict is None):
			for f_key, arguments in self.__args_dict__.items():
				argDict[f_key] = arguments
		##	4. transformation and normalization
		return res

	def differ(self, loperand: z3.ExprRef, roperand: z3.ExprRef):
		"""
		:param loperand: z3.expr
		:param roperand: z3.expr
		:return:
		"""
		try:
			if isinstance(loperand, z3.BoolRef) or isinstance(roperand, z3.BoolRef):
				loperand = self.__cast_to_logic__(loperand)
				roperand = self.__cast_to_logic__(roperand)
			elif isinstance(loperand, z3.ArithRef) or isinstance(roperand, z3.ArithRef):
				loperand = self.__cast_to_arith__(loperand)
				roperand = self.__cast_to_arith__(roperand)
			return loperand.__ne__(roperand)
		except z3.Z3Exception:
			return None
		except AttributeError:
			return None
		except TypeError:
			return None


def test_symbol_z3_parser(project: jcmuta.CProject, file_path: str):
	parser = SymbolToZ3Parser()
	passed, failed, ratio = 0, 0, 0.0
	with open(file_path, 'w') as writer:
		writer.write("CATE\tLOCT\tLOPD\tROPD\tSEXP\n")
		for state in project.context_space.get_states():
			category = state.get_category()
			location = state.get_location().get_node_type()
			loperand = state.get_loperand().get_code()
			roperand = state.get_roperand().get_code()
			if category == "set_expr":
				l_value = parser.parse(state.get_loperand(), dict(), set(), dict())
				r_value = parser.parse(state.get_roperand(), dict(), set(), dict())
				if (l_value is not None) and (r_value is not None):
					expression = parser.differ(l_value, r_value)
				else:
					expression = None
			else:
				expression = parser.parse(state.get_loperand(), dict(), set(), dict())
			if not (expression is None):
				scode = str(expression.sexpr())
				passed += 1
			else:
				scode = "#Error"
				failed += 1
			loperand = jcbase.strip_text(loperand, 128)
			roperand = jcbase.strip_text(roperand, 128)
			scode = jcbase.strip_text(scode, 128)
			writer.write("{}\t{}\t{}\t{}\t{}\n".format(category, location, loperand, roperand, scode))
	ratio = passed / (passed + failed)
	ratio = int(ratio * 10000) / 100.0
	print("\t[Sym-Z3]:\tPASS = {}\tFAIL = {}\t ({}%)".format(passed, failed, ratio))
	return


##	expression-level proof


class SymbolToZ3Prover:
	"""
	It implements the equivalence proof at expression-level using z3 theorem prover.
	"""

	##	constructor

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
		self.class_names = ["NEQ","VEQ","BEQ","SEQ"]		# names of equivalence class
		self.timeout = 1000									# maximal timeout
		return

	def get_class_name_of(self, class_flag: int):
		"""
		:param class_flag: 	the integer ID of the equivalence class
		:return: 			string of "NEQ", "VEQ", "BEQ" and "SEQ"
		"""
		if (class_flag < 0) or (class_flag >= len(self.class_names)):
			return self.class_names[self.neq_class]
		else:
			return self.class_names[class_flag]

	##	z3 internal

	def __check_timeout__(self, solver: z3.Solver):
		"""
		:param solver:
		:return: True if non-satisfiable or timeout
		"""
		solver.set("timeout", self.timeout)
		begTime = datetime.datetime.now()
		result = solver.check()
		endTime = datetime.datetime.now()
		timeDiff = endTime - begTime
		m_seconds = int(timeDiff.total_seconds() * 1000)
		if m_seconds >= self.timeout * 0.95:
			result = z3.unsat
		return result == z3.unsat

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
		# return self.__check_timeout__(solver)
		solver.set("timeout", self.timeout)
		return solver.check() == z3.unsat

	def __check_equal__(self, orig_value, muta_value, assumptions):
		"""
		:param orig_value: the z3.Expression to denote the original version
		:param muta_value: the z3.Expression to denote the mutation version
		:param assumptions: the background assumptions used to prove this
		:return: True if the two expressions are proved as being equivalent
		"""
		condition = self.parser.differ(orig_value, muta_value)
		if condition is None:
			return False
		return self.__check_unsat__(condition, assumptions)

	## constraint

	def __prove_constraint__(self, constrain: jcmuta.SymbolNode):
		"""
		:param constrain: the symbolic constrain being verified
		:return: True if the input constrain is non-satisfiable
		"""
		assumptions = set()
		condition = self.parser.parse(constrain, dict(), assumptions, dict())
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

	def __compare_values__(self, orig_value, muta_value, assumptions: set):
		"""
		:param orig_value: 	the original value to be compared
		:param muta_value: 	the mutation value to be compared
		:param assumptions: the background assumptions to verify
		:return:
		"""
		return self.__check_equal__(orig_value, muta_value, assumptions)

	def __compare_states__(self, orig_states: dict, muta_states: dict, assumptions: set):
		"""
		:param orig_states: the map from string key to z3.Expression(s)
		:param muta_states: the map from string key to z3.Expression(s)
		:param assumptions: the background assumptions for verification
		:return: 			whether states are equivalent to each other
		"""
		if len(orig_states) == len(muta_states):
			for key, orig_state in orig_states.items():
				if key in muta_states:
					muta_state = muta_states[key]
					if not self.__check_equal__(orig_state, muta_state, assumptions):
						return False
			return True
		else:
			return False

	def __compare_arguments__(self, orig_args: dict, muta_args: dict, assumptions: set):
		"""
		:param orig_args:	the map from string key to z3.Expression(s)
		:param muta_args:	the map from string key to z3.Expression(s)
		:param assumptions: the background assumptions for verification
		:return:			whether the argument are equivalent function
		"""
		if len(orig_args) == len(muta_args):
			for key, orig_arguments in orig_args.items():
				if key in muta_args:
					muta_arguments = muta_args[key]
					for k in range(0, len(orig_arguments)):
						orig_argument = orig_arguments[k]
						muta_argument = muta_arguments[k]
						if not self.__check_equal__(orig_argument, muta_argument, assumptions):
							return False
			return True
		else:
			return False

	def __prove_difference__(self, orig_expression: jcmuta.SymbolNode, muta_expression: jcmuta.SymbolNode):
		"""
		:param orig_expression: the symbolic expression to denote the original version
		:param muta_expression: the symbolic expression to denote the mutation version
		:return: value_equal, state_equal, args_equal, has_effects, call_funcs
		"""
		## parse and evaluate
		assumptions, orig_states, muta_states = set(), dict(), dict()
		orig_arguments, muta_arguments = dict(), dict()
		orig_value = self.parser.parse(orig_expression, orig_states, assumptions, orig_arguments)
		muta_value = self.parser.parse(muta_expression, muta_states, assumptions, muta_arguments)
		if (orig_value is None) or (muta_value is None):
			value_equal, state_equal, args_equal = False, False, False
		else:
			value_equal = self.__compare_values__(orig_value, muta_value, assumptions)
			state_equal = self.__compare_states__(orig_states, muta_states, assumptions)
			args_equal = self.__compare_arguments__(orig_arguments, muta_arguments, assumptions)
		has_effects = len(orig_states) > 0 or len(muta_states) > 0
		call_funcs = len(orig_arguments) > 0 or len(muta_arguments) > 0
		return value_equal, state_equal, args_equal, has_effects, call_funcs

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
		solution: Tuple[bool, bool, bool, bool, bool]
		value_equal = solution[0]
		state_equal = solution[1]
		args_equal = solution[2]
		has_effects = solution[3]
		call_funcs = solution[4]
		return value_equal, state_equal, args_equal, has_effects, call_funcs

	## set-expression

	@staticmethod
	def __is_top_expression__(location: jccode.AstCirNode):
		"""
		:param location:
		:return: whether the location is top-level expression
		"""
		parent = location.get_parent()
		child_type = location.get_child_type()
		return (parent is None) or (child_type == "evaluate")

	def prove_set_expression(self, location: jccode.AstCirNode,
							 orig_expression: jcmuta.SymbolNode,
							 muta_expression: jcmuta.SymbolNode):
		"""
		:param location: 		location where expression is mutated
		:param orig_expression:	original expression in symbolic form
		:param muta_expression:	mutation expression in symbolic form
		:return:				class_flag of: NEQ | VEQ | BEQ | SEQ
		"""
		value_equal, state_equal, args_equal, has_effects, call_funcs = \
					self.prove_difference(orig_expression, muta_expression)
		if has_effects:
			if state_equal:
				if self.__is_top_expression__(location) and args_equal:
					return self.seq_class
				elif value_equal and args_equal:
					return self.beq_class
				else:
					return self.neq_class
			elif location.get_parent().get_node_type() == "retr_stmt":
				if value_equal and args_equal:
					return self.veq_class
				else:
					return self.neq_class
			else:
				return self.neq_class
		else:
			if value_equal and args_equal:
				return self.veq_class
			elif self.__is_top_expression__(location) and args_equal:
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

	def cluster_mutations(self, source: jcmuta.ContextMutation, targets):
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


##	equivalence classifier


def classify_mutant_equivalences(project: jcmuta.CProject):
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
	print("\t[Mut-Pv]:\tStart to prove equivalence for {} mutants...".format(total))
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
		ratio = equal_number / (total + 0.0)
	ratio = int(ratio * 10000) / 100.0
	print("\t[Eqv-Cs]:\tALV = {};\tEQV = {};\tSTA = {};\tTIM = {} s.".
		  format(alive_number, equal_number, len(equal_states), seconds))
	print("\t[Mut-Tg]:\tVEQ = {};\tBEQ = {};\tSEQ = {};\tRAT = {} %.".
		  format(veq_number, beq_number, seq_number, ratio))
	return output


def write_mutant_classification(project: jcmuta.CProject, tce_mutants: set,
								mutant_state_dict: dict, file_path: str):
	"""
	:param project:				mutation testing project of which mutants are printed
	:param tce_mutants: 		the set of mutants being detected by trivial compiler
	:param mutant_state_dict: 	the dict from Mutants to (ContextState, int) classes
	:param file_path:			the path of output file to write
	:return:					MID CLAS OPRT PARM FUNC LINE CODE TCE MEX CATE LOCT LCOD LOPD ROPD
	"""
	with open(file_path, 'w') as writer:
		theorem_prover = SymbolToZ3Prover()
		writer.write("ID\tCLAS\tOPRT\tPARM\tFUNC\tLINE\tCODE\tTCE\tMEX\tCATE\tLOCT\tLOPD\tROPD\n")
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
			##	MEX CATE LOCT LOPD ROPD
			if mutant in mutant_state_dict:
				state = mutant_state_dict[mutant][0]
				flags = mutant_state_dict[mutant][1]
				class_name = theorem_prover.get_class_name_of(flags)
				state: jcmuta.ContextState
				category = state.get_category()
				location = "{}#{}".format(state.get_location().get_node_type(), state.get_location().get_node_id())
				loperand = state.get_loperand().get_code()
				roperand = state.get_roperand().get_code()
				writer.write("\t{}\t{}\t{}\t({})\t({})".format(class_name, category, location, loperand, roperand))
			else:
				writer.write("\t{}".format(theorem_prover.get_class_name_of(theorem_prover.neq_class)))
			writer.write("\n")
		writer.write("\n")
	return


def test_symbol_z3_classify(project: jcmuta.CProject, tce_mutants: Set[jcmuta.Mutant], file_path: str):
	"""
	:param project:
	:param tce_mutants:
	:param file_path:
	:return:
	"""
	mutant_state_class = classify_mutant_equivalences(project)
	write_mutant_classification(project, tce_mutants, mutant_state_class, file_path)
	exp_mutants = set()
	for mutant in mutant_state_class.keys():
		exp_mutants.add(mutant)
	print("\t[Mut-Un]:\tTCE*MEX = {};\tTCE-MEX = {};\tMEX-TCE = {};\tMEX|TCE = {};".
		  format(len(tce_mutants & exp_mutants), len(tce_mutants - exp_mutants),
				 len(exp_mutants - tce_mutants), len(exp_mutants | tce_mutants)))
	return mutant_state_class


##	duplication clustering


def cluster_expression_duplication(project: jcmuta.CProject, tce_mutants: set, exp_mutants):
	"""
	:param project:
	:param tce_mutants: the equivalent mutants being detected using TCE technique
	:param exp_mutants: the equivalent mutants being detected at expression-level
	:return:	set[set[Mutant]]
	"""
	## initialization
	theorem_prover, neq_mutations = SymbolToZ3Prover(), set()
	for mutation in project.context_space.get_mutations():
		if (mutation.get_mutant() in tce_mutants) or (mutation.get_mutant() in exp_mutants):
			continue
		else:
			neq_mutations.add(mutation)
	counter, total, steps, clusters = 0, len(neq_mutations), 3000, list()

	## duplicate-clustering
	print("\t[Mut-Cl]:\tCluster {} uncovered mutants".format(len(neq_mutations)))
	while len(neq_mutations) > 0:
		source_mutation = next(iter(neq_mutations))
		neq_mutations.remove(source_mutation)
		new_cluster = theorem_prover.cluster_mutations(source_mutation, neq_mutations)
		neq_mutations = neq_mutations - new_cluster
		clusters.append(new_cluster)
		counter += len(new_cluster)
		if counter >= steps:
			print("\t\tCluster[{}/{}]\t{} mutants.".format(counter, total, len(new_cluster)))
			steps += 3000
	all_number, cluster_number = total, len(clusters)
	ratio = (all_number - cluster_number) / (all_number + 0.0)
	ratio = int(ratio * 10000) / 100.0
	print("\t[Mut-Rd]:\tALL = {};\tCST = {};\tDUP = {};\tRED = {}%".
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


def write_mutant_clustering(project: jcmuta.CProject, mutant_clusters: dict, file_path: str):
	"""
	:param project:			Mutation testing project
	:param mutant_clusters:	the dict from Mutant to integer ID of cluster
	:param file_path:		the path of output file
	:return:
	"""
	with open(file_path, 'w') as writer:
		writer.write("CLUS\tID\tCLAS\tOPRT\tPARM\tFUNC\tLINE\tCODE\n")
		for mutant in project.muta_space.get_mutants():
			mutant: jcmuta.Mutant
			cid = 0
			if mutant in mutant_clusters:
				cid = mutant_clusters[mutant]
			mid = mutant.get_muta_id()
			mu_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.get_mutation().get_mutation_operator()
			location = mutant.get_mutation().get_location()
			code_line = location.line_of(False)
			code_text = location.generate_code(96)
			func_name = location.get_function_name()
			parameter = str(mutant.get_mutation().get_parameter())
			writer.write("{}\t{}\t{}\t{}\t{}\t{}\t{}\t\"{}\"\n".
						 format(cid, mid, mu_class, operator, parameter, func_name, code_line, code_text))
		writer.write("\n")
	return


def test_symbol_z3_clusters(project: jcmuta.CProject, tce_mutants, mutant_state_dict: dict, file_path: str):
	"""
	:param project:
	:param tce_mutants:
	:param mutant_state_dict:
	:param file_path:
	:return:
	"""
	mutant_clusters = cluster_expression_duplication(project, tce_mutants, mutant_state_dict)
	write_mutant_clustering(project, mutant_clusters, file_path)
	return mutant_clusters


##	main scripts


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/featuresAll"
	post_path = "/home/dzt2/Development/Data/zexp/resultsAll"
	tce_path = "/home/dzt2/Development/Data/zexp/TCE"
	project_names = get_file_names_in(root_path)
	for index in range(0, len(project_names)):
		project_name = project_names[index]
		print("{}.\tTesting on project {}.".format(index + 1, project_name))
		if project_name != "md4":
			project_directory = os.path.join(root_path, project_name)
			c_project = jcmuta.CProject(project_directory, project_name)
			tce_set = read_TCE_mutants_in(tce_path, c_project)
			#test_symbol_z3_parser(c_project, os.path.join(post_path, project_name + ".sz3"))
			exp_class = test_symbol_z3_classify(c_project, tce_set, os.path.join(post_path, project_name + ".eqv"))
			#test_symbol_z3_clusters(c_project, tce_set, exp_class, os.path.join(post_path, project_name + ".cus"))
		print()
	print("Testing end for all.")

