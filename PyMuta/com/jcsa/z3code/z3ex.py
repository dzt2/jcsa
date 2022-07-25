"""This file implements the z3-based expression and mutation equivalence analysis"""


import os
import time
import z3
import com.jcsa.z3code.base as jcbase
import com.jcsa.z3code.code as jccode
import com.jcsa.z3code.muta as jcmuta


class SymbolToZ3Parser:
	"""
	It implements the parsing from SymbolNode to z3.Expression statically.
	"""

	def __init__(self):
		"""
		Initialization of Parser
		"""
		self.__states__ = dict()		## map from a variable to its values
		self.__assume__ = set()			## set of assumption (z3.Expression)
		self.__normal__ = dict()		## map from reference to unique name
		self.__naming__ = "{}_{}{}"		## template of name in normalization
		self.longLength = 64
		self.byteLength = 128
		return

	def __save_state__(self, key, value):
		"""
		:param key:
		:param value:
		:return:
		"""
		self.__states__[str(key)] = value
		return

	def __add_assume__(self, assumption):
		"""
		:param assumption:
		:return:
		"""
		if not (assumption is None):
			self.__assume__.add(assumption)
		return

	def __unique_name__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node: the node of which unique name is created
		:return: the unique name of the symbol_node
		"""
		class_name = symbol_node.get_class_name()
		class_id = symbol_node.get_class_id()
		if class_id < 0:
			class_id = abs(class_id)
			class_flag = 'n'
		else:
			class_flag = 'p'
		return self.__naming__.format(class_name, class_flag, class_id)

	def __normal_name__(self, name: str, data_type: jcbase.CType):
		"""
		:param name:
		:param data_type:
		:return: the normalized name
		"""
		c_type = data_type.get_key()
		if not (name in self.__normal__):
			if (c_type == "void") or (c_type == "bool"):
				self.__normal__[name] = "b#{}".format(len(self.__normal__))
			elif (c_type == "char") or (c_type == "uchar"):
				self.__normal__[name] = "c#{}".format(len(self.__normal__))
			elif (c_type == "short") or (c_type == "int") or (c_type == "long") or (c_type == "llong"):
				self.__normal__[name] = "i#{}".format(len(self.__normal__))
			elif (c_type == "ushort") or (c_type == "uint") or (c_type == "ulong") or (c_type == "ullong"):
				self.__normal__[name] = "u#{}".format(len(self.__normal__))
			elif (c_type == "float") or (c_type == "double") or (c_type == "ldouble"):
				self.__normal__[name] = "f#{}".format(len(self.__normal__))
			elif (c_type == "point") or (c_type == "array"):
				self.__normal__[name] = "p#{}".format(len(self.__normal__))
			else:
				self.__normal__[name] = name
		normal_name = self.__normal__[name]
		normal_name: str
		return normal_name

	def __unsigned_reference__(self, reference):
		"""
		:param reference:
		:return: it adds the assumption of unsigned reference on variable
		"""
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef):
			self.__add_assume__(reference >= 0)
		elif isinstance(reference, z3.BitVecRef) or isinstance(reference, z3.BitVecNumRef):
			self.__add_assume__(reference >= z3.BitVecVal(0, self.longLength))
		return reference

	def __new_reference__(self, name: str, data_type: jcbase.CType):
		"""
		:param name: the name of reference
		:param data_type: data type of variable
		:return:
		"""
		c_type = data_type.get_key()
		if (c_type == "void") or (c_type == "bool"):
			return z3.Bool(name)
		elif (c_type == "char") or (c_type == "uchar"):
			return self.__unsigned_reference__(z3.Int(name))
		elif (c_type == "short") or (c_type == "int") or (c_type == "long") or (c_type == "llong"):
			return z3.Int(name)
		elif (c_type == "ushort") or (c_type == "uint") or (c_type == "ulong") or (c_type == "ullong"):
			return self.__unsigned_reference__(z3.Int(name))
		elif (c_type == "float") or (c_type == "double") or (c_type == "ldouble"):
			return z3.Real(name)
		elif (c_type == "array") or (c_type == "point"):
			return self.__unsigned_reference__(z3.Int(name))
		elif c_type == "function":
			return z3.Function(name)
		else:
			return z3.BitVec(name, self.byteLength)

	def __new_constant__(self, constant):
		"""
		:param constant: bool | int | float
		:return:
		"""
		self.__add_assume__(None)
		if isinstance(constant, bool):
			return z3.BoolVal(constant)
		elif isinstance(constant, int):
			return z3.IntVal(constant)
		else:
			return z3.RealVal(constant)

	def __cast_to_bool__(self, reference):
		"""
		:param reference:
		:return: it casts the expression to boolean
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
		:return: it casts the expression to integer or real
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
		:return: it casts the expression to bitwise
		"""
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef):
			return z3.Int2BV(reference, self.longLength)
		elif isinstance(reference, z3.BoolRef):
			return z3.If(reference, 1, 0)
		else:
			return reference

	def __parse__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: it parses the symbolic expression to z3 model
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

	def __parse_identifier__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return:
		"""
		name = symbol_node.get_content().get_token_value()
		data_type = symbol_node.get_data_type()
		name = self.__normal_name__(name, data_type)
		return self.__new_reference__(name, data_type)

	def __parse_constant__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: constant of z3 expression
		"""
		return self.__new_constant__(symbol_node.get_content())

	def __parse_literal__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: literal of z3.String
		"""
		unique_name = self.__unique_name__(symbol_node)
		return z3.String(unique_name)

	def __parse_unary_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: arith_neg, bitws_rsv, logic_not, address_of, de_reference
		"""
		operator = symbol_node.get_content().get_token_value()
		operand = symbol_node.get_child(1)
		if operator == "negative":
			u_operand = self.__parse__(operand)
			u_operand = self.__cast_to_numb__(u_operand)
			return -u_operand
		elif operator == "bit_not":
			u_operand = self.__parse__(operand)
			u_operand = self.__cast_to_bits__(u_operand)
			return ~u_operand
		elif operator == "logic_not":
			u_operand = self.__parse__(operand)
			u_operand = self.__cast_to_bool__(u_operand)
			return z3.Not(u_operand)
		elif operator == "address_of":
			return z3.Const(self.__unique_name__(symbol_node), z3.IntSort())
		else:
			name = self.__unique_name__(symbol_node)
			name = self.__normal_name__(name, symbol_node.get_data_type())
			return self.__new_reference__(name, symbol_node.get_data_type())

	def __parse_arith_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node: +, -, *, /, %
		:param code:
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
		:param symbol_node:
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
		:param symbol_node:
		:param code:
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
		:param code:
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
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
		self.__save_state__(loperand.sexpr(), roperand)
		if operator == "assign":
			return roperand
		else:
			return loperand

	def __parse_casted_expression__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse__(symbol_node.get_child(1))

	def __parse_field_expression__(self, symbol_node: jcmuta.SymbolNode):
		name = self.__unique_name__(symbol_node)
		name = self.__normal_name__(name, symbol_node.get_data_type())
		return self.__new_reference__(name, symbol_node.get_data_type())

	def __parse_if_else_expression__(self, symbol_node: jcmuta.SymbolNode):
		condition = self.__parse__(symbol_node.get_child(0))
		loperand = self.__parse__(symbol_node.get_child(1))
		roperand = self.__parse__(symbol_node.get_child(2))
		condition = self.__cast_to_bool__(condition)
		return z3.If(condition, loperand, roperand)

	def __parse_list_expression__(self, symbol_node: jcmuta.SymbolNode):
		elements = list()
		for child in symbol_node.get_children():
			elements.append(self.__parse__(child))
		name = self.__unique_name__(symbol_node)
		return self.__new_reference__(name, symbol_node.get_data_type())

	def __parse_call_expression__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		function = symbol_node.get_child(0)
		if function.get_class_name() == "Identifier":
			func_name = function.get_content().get_token_value()
		else:
			func_name = self.__unique_name__(function)
		arguments = symbol_node.get_child(1).get_children()
		for k in range(0, len(arguments)):
			arg = self.__parse__(arguments[k])
			self.__save_state__("{}#{}".format(func_name, k), arg)
		return self.__new_reference__(func_name, symbol_node.get_data_type())

	def parse_to(self, symbol_node: jcmuta.SymbolNode, stateBuffer, assumeLib, clear: bool):
		"""
		:param symbol_node: the node to be parsed
		:param stateBuffer: to preserve the states
		:param assumeLib: 	to preserve the assumption
		:param clear: 		whether to clear the naming system
		:return:
		"""
		self.__states__.clear()
		self.__assume__.clear()
		if clear:
			self.__normal__.clear()
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
			for key, value in self.__states__.items():
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
			expression = parser.parse_to(symbol_node, None, None, True)
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
	print("\t{}:\tPASS = {}\tFAIL = {}\t ({}%)".format(project.program.name, past, fail, ratio))
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features"
	post_path = "/home/dzt2/Development/Data/zexp/results"
	for project_name in os.listdir(root_path):
		project_directory = os.path.join(root_path, project_name)
		c_project = jcmuta.CProject(project_directory, project_name)
		test_symbol_parser(c_project, os.path.join(post_path, project_name + ".sz3"))
		# test_symbol_prover(c_project, os.path.join(post_path, project_name + ".mz3"))
	print("Testing End...")

