"""It implements the generation of z3-code and satisfiability module."""


import os
import z3
import com.jcsa.z3code.base as jcbase
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
		self.bitSwitch = False
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

	def __save_buffer__(self, key, value):
		self.__buffer__[str(key)] = value
		return

	def __parse_by_type__(self, symbol_node: jcmuta.SymbolNode, name=None):
		data_type = symbol_node.get_data_type()
		if name is None:
			name = "ref_{}_{}".format(symbol_node.get_class_name(), symbol_node.get_class_id())
		if data_type.get_key() == "bool":
			return z3.Bool(name)
		elif data_type.get_key() == "char":
			if self.bitSwitch:
				return z3.BitVec(name, self.charSize)
			return z3.Int(name)
		elif (data_type.get_key() == "short") or (data_type.get_key() == "ushort"):
			if self.bitSwitch:
				return z3.BitVec(name, self.shortSize)
			return z3.Int(name)
		elif (data_type.get_key() == "int") or (data_type.get_key() == "uint"):
			if self.bitSwitch:
				return z3.BitVec(name, self.intSize)
			return z3.Int(name)
		elif (data_type.get_key() == "long") or (data_type.get_key() == "ulong"):
			if self.bitSwitch:
				return z3.BitVec(name, self.longSize)
			return z3.Int(name)
		elif (data_type.get_key() == "llong") or (data_type.get_key() == "ullong"):
			if self.bitSwitch:
				return z3.BitVec(name, self.longSize)
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
			if self.bitSwitch:
				return z3.BitVecVal(constant, self.intSize)
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
		self.bitSwitch = True
		loperand = self.parse(symbol_node.get_child(1))
		self.bitSwitch = True
		roperand = self.parse(symbol_node.get_child(2))
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
		self.__save_buffer__(loperand.sexpr(), roperand)
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

	def __parse_call_expression__(self, symbol_node: jcmuta.SymbolNode):
		function = symbol_node.get_child(0)
		if function.get_class_name() == "Identifier":
			func_name = function.get_content().get_token_value()
		else:
			func_name = "func_{}".format(function.get_class_id())
		arg_list = symbol_node.get_child(1)
		arguments = list()
		for argument in arg_list.get_children():
			argument: jcmuta.SymbolNode
			arg = self.parse(argument)
			arguments.append(arg)
			if len(arguments) >= self.maxArgs:
				break
			else:
				self.__save_buffer__(argument.get_code(), arg)
		return z3.Function(func_name)

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


class SymbolZ3Prover:
	"""
	It checks the equivalence of corresponding mutants
	"""

	def __init__(self):
		self.parser = SymbolZ3Parser()
		self.timeout = 2000
		return

	def __parse__(self, symbol_node: jcmuta.SymbolNode, buffer=None):
		"""
		:param symbol_node:
		:param buffer:		to preserve the state or not
		:return:
		"""
		self.parser = None
		res = SymbolZ3Parser.sym2z3(symbol_node, buffer)
		return res

	def __check__(self, condition):
		"""
		:param condition:
		:return: True if condition is not satisfiable
		"""
		solver = z3.Solver()
		solver.set("timeout", self.timeout)
		solver.add(condition)
		if solver.check() == z3.unsat:
			return True
		return False

	def __classify__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: ceq, veq, seq, unk
		"""
		if state.get_category() == "eva_cond":
			try:
				condition = self.__parse__(state.get_loperand())
				if self.__check__(condition):
					return 1
				else:
					return 0
			except:
				return 0
		elif state.get_category() == "set_expr":
			try:
				orig_maps, muta_maps = dict(), dict()
				loperand = self.__parse__(state.get_loperand(), orig_maps)
				roperand = self.__parse__(state.get_roperand(), muta_maps)
				for key, muta_value in muta_maps.items():
					if key in orig_maps:
						orig_value = orig_maps[key]
						if not self.__check__(orig_value != muta_value):
							return 0
					else:
						return 0
				location = state.get_location()
				if (location.get_child_type() == "evaluate") or (location.get_child_type() == "n_condition"):
					return 3
				elif self.__check__(loperand != roperand):
					return 2
				else:
					return 0
			except:
				return 0
		else:
			return 0

	def __solve__(self, project: jcmuta.CProject):
		"""
		:param project:
		:return: dict[state, class{unk, ceq, veq, seq}]
		"""
		state_class_dict, states = dict(), set()
		for mutant in project.context_space.get_mutants():
			mutant: jcmuta.Mutant
			if mutant.get_result().is_killed_in(None):
				continue
			else:
				for state in project.context_space.get_mutation(mutant).get_states():
					states.add(state)
		for state in states:
			flag = self.__classify__(state)
			state_class_dict[state] = flag
		return state_class_dict

	def solve(self, project: jcmuta.CProject, file_path: str):
		"""
		:param project:
		:param file_path:
		:return: alive, equiv, ceq, veq, seq
		"""
		alive, eqv, ceq, veq, seq = 0, 0, 0, 0, 0
		with open(file_path, 'w') as writer:
			writer.write("ID\tClass\tOprt\tLine\tCode\tParam\tEquiv\tCategory\tLocation\tLoperand\tRoperand\n")
			for mutant in project.context_space.get_mutants():
				if mutant.get_result().is_killed_in(None):
					continue
				else:
					alive += 1
					equiv_state, equiv_flag = None, 0
					for state in project.context_space.get_mutation(mutant).get_states():
						flag = self.__classify__(state)
						if flag != 0:
							equiv_state = state
							equiv_flag = flag
							eqv += 1
							break
					mid = mutant.get_muta_id()
					mclass = mutant.get_mutation().get_mutation_class()
					moprt = mutant.get_mutation().get_mutation_operator()
					mline = mutant.get_mutation().get_location().line_of(False)
					mcode = mutant.get_mutation().get_location().get_code(True)
					mcode = jcbase.strip_text(mcode, 96)
					param = mutant.get_mutation().get_parameter()
					writer.write("{}\t{}\t{}\t{}\t\"{}\"\t{}\t".format(mid, mclass, moprt, mline, mcode, param))
					if equiv_flag != 0:
						equiv_state: jcmuta.ContextState
						flag = equiv_flag
						if flag == 1:
							ceq += 1
							flag_string = "CEQ"
						elif flag == 2:
							veq += 1
							flag_string = "VEQ"
						else:
							seq += 1
							flag_string = "SEQ"
						category = equiv_state.get_category()
						location = equiv_state.get_location()
						loc_code = location.get_ast_source().get_code(True)
						loc_code = jcbase.strip_text(loc_code, 96)
						loperand = equiv_state.get_loperand().get_code()
						roperand = equiv_state.get_roperand().get_code()
						loperand = jcbase.strip_text(loperand, 96)
						roperand = jcbase.strip_text(roperand, 96)
						writer.write("{}\t{}\t\"{}\"\t({})\t({})".format(flag_string, category, loc_code, loperand, roperand))
					else:
						writer.write("???")
					writer.write("\n")
		return alive, eqv, ceq, veq, seq


def test_z3_parse(project: jcmuta.CProject, out_file: str):
	"""
	:param project:
	:param out_file:
	:return:
	"""
	past, fail = 0, 0
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
				expr_code = ""
				fail += 1
			else:
				expr_code = expression.sexpr()
				past += 1
			expr_code = jcbase.strip_text(expr_code, len(expr_code) + 16)
			writer.write("{}\t{}\t{}\t\"{}\"\t{}\n".format(symbol_class, symbol_id, data_type, symbol_code, expr_code))
	past_rate = past / (past + fail)
	past_rate = int(past_rate * 10000) / 100.0
	print("\t==> Pass = {}\t Fail = {}\tRate = {}%".format(past, fail, past_rate))
	return


def check_un_satisfiability(condition):
	solver = z3.Solver()
	solver.add(condition)
	solver.set("timeout", 3000)
	if solver.check() == z3.unsat:
		return True
	return False


def check_equivalence_by_state(state: jcmuta.ContextState):
	"""
	:param state:
	:return: unk,
	"""
	if state.get_category() == "eva_cond":
		try:
			condition = SymbolZ3Parser.sym2z3(state.get_loperand())
			if check_un_satisfiability(condition):
				return True
		except:
			return False
	elif state.get_category() == "set_expr":
		orig_maps, muta_maps = dict(), dict()
		try:
			loperand = SymbolZ3Parser.sym2z3(state.get_loperand(), orig_maps)
			roperand = SymbolZ3Parser.sym2z3(state.get_roperand(), muta_maps)
			for key, muta_value in muta_maps.items():
				if key in orig_maps:
					orig_value = orig_maps[key]
					if not check_un_satisfiability(orig_value != muta_value):
						return False
				else:
					return False
			location = state.get_location()
			if (location.get_child_type() == "evaluate") or (location.get_child_type() == "n_condition"):
				return True
			else:
				return check_un_satisfiability(loperand != roperand)
		except:
			return False
	else:
		return False


def check_equivalence(mutant: jcmuta.Mutant):
	"""
	:param mutant:
	:return: True {equivalence}; False {non-equivalence}.
	"""
	if mutant.get_result().is_killed_in(None):
		return False, mutant, None
	else:
		context_space = mutant.get_space().get_project().context_space
		for state in context_space.get_mutation(mutant).get_states():
			if check_equivalence_by_state(state):
				return True, mutant, state
		return False, mutant, None


def detect_equivalence(project: jcmuta.CProject, out_file: str):
	"""
	:param project:
	:param out_file:
	:return:
	"""
	alive, equiv, total = 0, 0, len(project.muta_space.get_mutants())
	with open(out_file, 'w') as writer:
		writer.write("ID\tEquivalent\tClass\tOperator\tLine\tCode\tParam\n")
		for mutant in project.context_space.get_mutants():
			if not (mutant.get_result().is_killed_in(None)):
				mid = mutant.get_muta_id()
				mclass = mutant.get_mutation().get_mutation_class()
				moprt = mutant.get_mutation().get_mutation_operator()
				mline = mutant.get_mutation().get_location().line_of(False)
				mcode = mutant.get_mutation().get_location().generate_code(96)
				alive += 1
				is_equiv, mutant, state = check_equivalence(mutant)
				if is_equiv:
					is_equiv = "EQV"
					equiv += 1
				else:
					is_equiv = "UNK"
				parameter = mutant.get_mutation().get_parameter()
				writer.write("{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n".format(
					mid, is_equiv, mclass, moprt, mline, mcode, parameter))
				if not (state is None):
					state: jcmuta.ContextState
					state_class = state.get_category()
					state_line = state.get_location().get_ast_source().line_of(False)
					state_code = state.get_location().get_ast_source().get_code(True)
					loperand = state.get_loperand().get_code()
					roperand = state.get_roperand().get_code()
					state_code = jcbase.strip_text(state_code, 96)
					loperand = jcbase.strip_text(loperand, 96)
					roperand = jcbase.strip_text(roperand, 96)
					writer.write("\t==>\t{}\t{}\t\"{}\"\t({})\t({})\n".format(
						state_class, state_line, state_code, loperand, roperand))
		ratio = equiv / (alive + 0.001)
		ratio = int(ratio * 10000) / 100.0
		writer.write("\nSum\tAlive\t{}\tEquiv\t{}\tRatio\t{}%\n".format(alive, equiv, ratio))
		writer.write("\n")
	ratio = equiv / (alive + 0.001)
	ratio = int(ratio * 10000) / 100.0
	print("\t\tAlive = {}\tEquiv = {}\tRatio = {}%".format(alive, equiv, ratio))
	return


def test_equivalent_detection(project: jcmuta.CProject, out_file: str):
	prover = SymbolZ3Prover()
	print("Load", project.program.name, "with", len(project.muta_space.get_mutants()), "mutants.")
	alive, eqv, ceq, veq, seq = prover.solve(project, out_file)
	ratio = eqv / (alive + 0.00001)
	ratio = int(ratio * 10000) / 100.0
	print("\tALV = {}\tEQV = {} ({}%)\tCEQ = {}\tVEQ = {}\tSEQ = {}".format(alive, eqv, ratio, ceq, veq, seq))
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext3/features"
	post_path = "/home/dzt2/Development/Data/zext3/debugs"
	start = True
	for project_name in os.listdir(root_path):
		project_directory = os.path.join(root_path, project_name)
		c_project = jcmuta.CProject(project_directory, project_name)
		test_equivalent_detection(c_project, os.path.join(post_path, project_name + ".eqv"))
		# if True:
		# project_directory = os.path.join(root_path, project_name)
		# c_project = jcmuta.CProject(project_directory, project_name)
		# print("Load", c_project.program.name, "with", len(c_project.muta_space.get_mutants()), "mutants.")
		# detect_equivalence(c_project, os.path.join(post_path, project_name + ".eqv"))
		print()

