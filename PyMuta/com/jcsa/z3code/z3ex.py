"""It implements the generation of z3-code and satisfiability module."""


import os
import z3
import com.jcsa.z3code.base as jcbase
import com.jcsa.z3code.code as jccode
import com.jcsa.z3code.muta as jcmuta


class SymbolZ3Parser:
	"""
	It parses the SymbolNode to z3.sexpr model
	"""

	def __init__(self):
		self.charSize = 8
		self.shortSize = 16
		self.intSize = 32
		self.longSize = 64
		self.realSize = 64
		self.addrSize = 64
		self.bodySize = 128
		self.__buffer__ = dict()
		self.maxArgs = 8
		return

	def parse(self, symbol_node: jcmuta.SymbolNode):
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
			return self.__parse_ifte_expression__(symbol_node)
		elif symbol_class == "CallExpression":
			return self.__parse_call_expression__(symbol_node)
		else:
			return self.__parse_list_expression__(symbol_node)

	def __parse_by_type__(self, symbol_node: jcmuta.SymbolNode, name=None):
		data_type = symbol_node.get_data_type()
		if name is None:
			name = "ref_{}_{}".format(symbol_node.get_class_name(), symbol_node.get_class_id())
		if data_type.get_key() == "bool":
			return z3.Bool(name)
		elif data_type.get_key() == "char":
			return z3.BitVec(name, self.charSize)
		elif (data_type.get_key() == "short") or (data_type.get_key() == "ushort"):
			return z3.BitVec(name, self.shortSize)
		elif (data_type.get_key() == "int") or (data_type.get_key() == "uint"):
			return z3.Int(name)
		elif (data_type.get_key() == "long") or (data_type.get_key() == "ulong"):
			return z3.Int(name)
		elif (data_type.get_key() == "llong") or (data_type.get_key() == "ullong"):
			return z3.Int(name)
		elif (data_type.get_key() == "float") or (data_type.get_key() == "double") or (data_type.get_key() == "ldouble"):
			return z3.Real(name)
		elif (data_type.get_key() == "float_x") or (data_type.get_key() == "double_x") or (data_type.get_key() == "ldouble_x"):
			return z3.BitVec(name, self.realSize * 2)
		elif (data_type.get_key() == "float_i") or (data_type.get_key() == "double_i") or (data_type.get_key() == "ldouble_i"):
			return z3.Real(name)
		elif (data_type.get_key() == "array") or (data_type.get_key() == "point"):
			return z3.Array(name, z3.IntSort(), z3.IntSort())
		elif data_type.get_key() == "function":
			return z3.Function(name)
		else:
			return z3.BitVec(name, self.bodySize)

	def __parse_identifier__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse_by_type__(symbol_node, symbol_node.get_content().get_token_value())

	def __parse_constant__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node: class_name == "Constant"
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
			return self.__parse_by_type__(symbol_node)

	def __parse_literal__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse_by_type__(symbol_node)

	def __parse_unary_expression__(self, symbol_node: jcmuta.SymbolNode):
		operator = str(symbol_node.get_content().get_token_value()).strip()
		operand = self.parse(symbol_node.get_child(1))
		if operator == "negative":
			return -operand
		elif operator == "bit_not":
			return ~operand
		elif operator == "logic_not":
			return z3.Not(operand)
		else:
			return self.__parse_by_type__(symbol_node)

	def __parse_arith_expression__(self, symbol_node: jcmuta.SymbolNode):
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.parse(symbol_node.get_child(1))
		roperand = self.parse(symbol_node.get_child(2))
		if operator == "arith_add":
			# print("\t\tDEBUGE: {}\t{}".format(loperand.sexpr(), roperand.sexpr()))
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
		loperand = self.parse(symbol_node.get_child(1))
		roperand = self.parse(symbol_node.get_child(2))
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
		loperand = self.parse(symbol_node.get_child(1))
		roperand = self.parse(symbol_node.get_child(2))
		if operator == "logic_and":
			return z3.And(loperand, roperand)
		elif operator == "logic_or":
			return z3.Or(loperand, roperand)
		else:
			return z3.Implies(loperand, roperand)

	def __parse_relation_expression__(self, symbol_node: jcmuta.SymbolNode):
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.parse(symbol_node.get_child(1))
		roperand = self.parse(symbol_node.get_child(2))
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
		loperand = self.parse(symbol_node.get_child(1))
		roperand = self.parse(symbol_node.get_child(2))
		self.__buffer__[loperand] = roperand
		if operator == "assign":
			return roperand
		else:
			return loperand

	def __parse_cast_expression__(self, symbol_node: jcmuta.SymbolNode):
		return self.parse(symbol_node.get_child(1))

	def __parse_field_expression__(self, symbol_node: jcmuta.SymbolNode):
		return self.__parse_by_type__(symbol_node)

	def __parse_ifte_expression__(self, symbol_node: jcmuta.SymbolNode):
		condition = self.parse(symbol_node.get_child(0))
		loperand = self.parse(symbol_node.get_child(1))
		roperand = self.parse(symbol_node.get_child(2))
		return z3.If(condition, loperand, roperand)

	def __new_function_node__(self, name: str, arguments):
		argType = z3.DeclareSort("T")
		if len(arguments) >= self.maxArgs:
			f = z3.Function(name, argType, argType, argType, argType, argType, argType, argType, argType)
			return f(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6], arguments[7])
		elif len(arguments) == 7:
			f = z3.Function(name, argType, argType, argType, argType, argType, argType, argType)
			return f(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5], arguments[6])
		elif len(arguments) == 6:
			f = z3.Function(name, argType, argType, argType, argType, argType, argType)
			return f(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4], arguments[5])
		elif len(arguments) == 5:
			f = z3.Function(name, argType, argType, argType, argType, argType)
			return f(arguments[0], arguments[1], arguments[2], arguments[3], arguments[4])
		elif len(arguments) == 4:
			f = z3.Function(name, argType, argType, argType, argType)
			return f(arguments[0], arguments[1], arguments[2], arguments[3])
		elif len(arguments) == 3:
			f = z3.Function(name, argType, argType, argType)
			return f(arguments[0], arguments[1], arguments[2])
		elif len(arguments) == 2:
			f = z3.Function(name, argType, argType)
			return f(arguments[0], arguments[1])
		elif len(arguments) == 1:
			f = z3.Function(name, argType)
			return f(arguments[0])
		else:
			f = z3.Function(name)
			return f()

	def __parse_call_expression__(self, symbol_node: jcmuta.SymbolNode):
		function = symbol_node.get_child(0)
		if function.get_class_name() == "Identifier":
			func_name = function.get_content().get_token_value()
		else:
			func_name = "func_{}".format(function.get_class_id())
		arg_list = symbol_node.get_child(1)
		arguments = list()
		for argument in arg_list.get_children():
			arg = self.parse(argument)
			arguments.append(arg)
			if len(arguments) >= self.maxArgs:
				break
		func_node = self.__new_function_node__(func_name, arguments)
		self.__buffer__[self.parse(function)] = func_node
		return func_node

	def __parse_list_expression__(self, symbol_node: jcmuta.SymbolNode):
		elements = list()
		for child in symbol_node.get_children():
			elements.append(self.parse(child))
		if len(elements) > 0:
			return elements[-1]
		else:
			return self.__parse_by_type__(symbol_node)

	@staticmethod
	def sym2z3(symbol_node: jcmuta.SymbolNode, stateBuffer=None):
		"""
		:param symbol_node:
		:param stateBuffer:
		:return:
		"""
		parser = SymbolZ3Parser()
		try:
			result = parser.parse(symbol_node)
		except:
			result = None
		if not (stateBuffer is None):
			stateBuffer: dict
			for key, value in parser.__buffer__.items():
				stateBuffer[key] = value
		return result


def test_z3_parse(project: jcmuta.CProject, out_file: str):
	"""
	:param project:
	:param out_file:
	:return:
	"""
	with open(out_file, 'w') as writer:
		writer.write("Class\tID\tType\tCode\tSExpr\n")
		for symbol_node in project.sym_tree.get_sym_nodes():
			symbol_node: jcmuta.SymbolNode
			symbol_class = symbol_node.get_class_name()
			symbol_id = symbol_node.get_class_id()
			data_type = symbol_node.get_data_type()
			symbol_code = jcbase.strip_text(symbol_node.get_code(), 64)
			expression = SymbolZ3Parser.sym2z3(symbol_node, None)
			if expression is None:
				expr_code = None
			else:
				expr_code = expression.sexpr()
			writer.write("{}\t{}\t{}\t\"{}\"\t{}\n".format(symbol_class, symbol_id, data_type, symbol_code, expr_code))
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext3/features"
	post_path = "/home/dzt2/Development/Data/zext3/debugs"
	for project_name in os.listdir(root_path):
		project_directory = os.path.join(root_path, project_name)
		c_project = jcmuta.CProject(project_directory, project_name)
		out_path = os.path.join(post_path, project_name + ".sep")
		print("Load", c_project.program.name, "with", len(c_project.sym_tree.get_sym_nodes()), "symbol nodes.")
		test_z3_parse(c_project, out_path)
		print()


