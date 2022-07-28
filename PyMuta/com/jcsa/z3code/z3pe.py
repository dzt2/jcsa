"""This file implements the algorithms to determine mutation equivalence by Z3 at expression-level."""


import os
import time
import z3
import com.jcsa.z3code.libs.base as jcbase
import com.jcsa.z3code.libs.code as jccode
import com.jcsa.z3code.libs.muta as jcmuta


class SymbolToZ3Parser:
	"""
	It parses the SymbolNode in our project to z3.expression objects.
	"""

	def __init__(self):
		"""
		It creates a parser for SymbolNode to z3.Expression
		"""
		self.boolLength 	= 1
		self.charLength 	= 8
		self.longLength 	= 64
		self.addrLength 	= 64
		self.bodyLength 	= 128
		self.__memory__ 	= dict()
		self.__assume__		= set()
		self.__normal__ 	= dict()
		self.__i_code__ 	= 2
		self.__s_code__ 	= 3
		self.__naming__ 	= "{}_{}{}"
		return

	def __save_state__(self, key, value):
		"""
		:param key:
		:param value:
		:return:
		"""
		self.__memory__[str(key)] = value
		return

	def __add_assume__(self, assumption):
		"""
		:param assumption:
		:return:
		"""
		if assumption is None:
			return
		self.__assume__.add(assumption)
		return

	def __unique_name__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node: the node of which unique name is created
		:return: the unique name of the symbol_node
		"""
		class_name = symbol_node.get_class_name()
		class_id = symbol_node.get_class_id()
		class_flag = 'p'
		if class_id < 0:
			class_id = abs(class_id)
			class_flag = 'n'
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

	def __new_bool_reference__(self, name: str, code: int):
		self.__add_assume__(None)
		return z3.Bool(name)

	def __new_char_reference__(self, name: str, code: int):
		self.__add_assume__(None)
		return z3.Int(name)

	def __new_int_reference__(self, name: str, code: int):
		if code == self.__s_code__:
			return z3.BitVec(name, self.longLength)
		else:
			return z3.Int(name)

	def __new_real_reference__(self, name: str):
		self.__add_assume__(None)
		return z3.Real(name)

	def __new_point_reference__(self, name: str, code: int):
		self.__add_assume__(None)
		return z3.Int(name)

	def __new_other_reference__(self, name: str):
		return z3.BitVec(name, self.bodyLength)

	def __unsigned_reference__(self, reference):
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef):
			self.__add_assume__(reference >= 0)
		elif isinstance(reference, z3.BitVecRef) or isinstance(reference, z3.BitVecNumRef):
			self.__add_assume__(reference >= z3.BitVecVal(0, self.longLength))
		return reference

	def __new_reference__(self, name: str, data_type: jcbase.CType, code: int):
		"""
		:param name: the name of z3.reference
		:param data_type: data type to decide
		:param code: the code to encode name
		:return: the reference of z3 expression
		"""
		c_type = data_type.get_key()
		if (c_type == "void") or (c_type == "bool"):
			return self.__new_bool_reference__(name, code)
		elif (c_type == "char") or (c_type == "uchar"):
			return self.__unsigned_reference__(self.__new_char_reference__(name, code))
		elif (c_type == "short") or (c_type == "int") or (c_type == "long") or (c_type == "llong"):
			return self.__new_int_reference__(name, code)
		elif (c_type == "ushort") or (c_type == "uint") or (c_type == "ulong") or (c_type == "ullong"):
			return self.__unsigned_reference__(self.__new_int_reference__(name, code))
		elif (c_type == "float") or (c_type == "double") or (c_type == "ldouble"):
			return self.__new_real_reference__(name)
		elif (c_type == "array") or (c_type == "point"):
			return self.__unsigned_reference__(self.__new_point_reference__(name, code))
		elif c_type == "function":
			return z3.Function(name)()
		else:
			return self.__new_other_reference__(name)

	def __new_constant__(self, constant, code: int):
		"""
		:param constant: bool | int | float
		:param code:
		:return:
		"""
		self.__add_assume__(None)
		if isinstance(constant, bool):
			return z3.BoolVal(constant)
		elif isinstance(constant, int):
			return z3.IntVal(constant)
		else:
			return z3.RealVal(constant)

	def __parse_identifier__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return:
		"""
		name = symbol_node.get_content().get_token_value()
		data_type = symbol_node.get_data_type()
		name = self.__normal_name__(name, data_type)
		return self.__new_reference__(name, data_type, code)

	def __parse_constant__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node:
		:param code:
		:return: constant of z3 expression
		"""
		return self.__new_constant__(symbol_node.get_content(), code)

	def __parse_literal__(self, symbol_node: jcmuta.SymbolNode):
		"""
		:param symbol_node:
		:return: literal of z3.String
		"""
		unique_name = self.__unique_name__(symbol_node)
		return z3.String(unique_name)

	def __cast_to_bool__(self, reference):
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef) or isinstance(reference, z3.RatNumRef):
			return reference != 0
		elif isinstance(reference, z3.BitVecRef) or isinstance(reference, z3.BitVecNumRef):
			return reference != z3.BitVecVal(0, self.longLength)
		else:
			return reference

	def __cast_to_numb__(self, reference):
		self.__add_assume__(None)
		if isinstance(reference, z3.BoolRef):
			return z3.If(reference, 1, 0)
		elif isinstance(reference, z3.BitVecRef) or isinstance(reference, z3.BitVecNumRef):
			return z3.BV2Int(reference, True)
		else:
			return reference

	def __cast_to_bits__(self, reference):
		if isinstance(reference, z3.ArithRef) or isinstance(reference, z3.IntNumRef):
			return z3.Int2BV(reference, self.longLength)
		elif isinstance(reference, z3.BoolRef):
			return z3.If(reference, 1, 0)
		else:
			return reference

	def __parse_unary_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node: negative, bit_not, logic_not, address_of, dereference
		:param code:
		:return:
		"""
		operator = symbol_node.get_content().get_token_value()
		operand = symbol_node.get_child(1)
		if operator == "negative":
			u_operand = self.__parse__(operand, code)
			return -self.__cast_to_numb__(u_operand)
		elif operator == "bit_not":
			u_operand = self.__parse__(operand, self.__s_code__)
			return ~self.__cast_to_bits__(u_operand)
		elif operator == "logic_not":
			u_operand = self.__parse__(operand, code)
			return z3.Not(self.__cast_to_bool__(u_operand))
		elif operator == "address_of":
			return z3.Const(self.__unique_name__(symbol_node), z3.IntSort())
		else:
			name = self.__unique_name__(symbol_node)
			name = self.__normal_name__(name, symbol_node.get_data_type())
			return self.__new_reference__(name, symbol_node.get_data_type(), code)

	def __parse_arith_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		"""
		:param symbol_node: +, -, *, /, %
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

	def __parse_assign_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		operator = str(symbol_node.get_content().get_token_value()).strip()
		loperand = self.__parse__(symbol_node.get_child(1), code)
		roperand = self.__parse__(symbol_node.get_child(2), code)
		self.__save_state__(loperand.sexpr(), roperand)
		if operator == "assign":
			return roperand
		else:
			return loperand

	def __parse_casted_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		return self.__parse__(symbol_node.get_child(1), code)

	def __parse_field_expression__(self, symbol_node: jcmuta.SymbolNode, code: int):
		name = self.__unique_name__(symbol_node)
		name = self.__normal_name__(name, symbol_node.get_data_type())
		return self.__new_reference__(name, symbol_node.get_data_type(), code)

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
		name = self.__unique_name__(symbol_node)
		return self.__new_reference__(name, symbol_node.get_data_type(), code)

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
			func_name = self.__unique_name__(function)
		arguments = symbol_node.get_child(1).get_children()
		for k in range(0, len(arguments)):
			arg = self.__parse__(arguments[k], 0)
			self.__save_state__("{}#{}".format(func_name, k), arg)
		return self.__new_reference__(func_name, symbol_node.get_data_type(), code)

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

	def parse_to(self, symbol_node: jcmuta.SymbolNode, stateBuffer, assumeLib, clear: bool):
		"""
		:param assumeLib: to preserve the assumption
		:param symbol_node:
		:param stateBuffer: to preserve translation state
		:param clear: whether to reset normal naming set
		:return: None if transformation failed
		"""
		self.__memory__.clear()
		self.__assume__.clear()
		if clear:
			self.__normal__.clear()
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
			for key, value in self.__memory__.items():
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


class MutationZ3Inputs:
	"""
	It implements the encoding of mutation to z3 constraint.
	"""

	def __init__(self, project: jcmuta.CProject):
		self.project = project
		self.__load__()
		return

	def __load__(self):
		"""
		:return: it loads the feature data into AST-STATE-MUTATION map
		"""
		self.loc_sta = dict()  	# AstCirNode 	--> ContextState
		self.sta_mut = dict()  	# ContextState	-->	Mutant
		self.mut_sta = dict()	# Mutant		-->	ContextState
		for mutation in self.project.context_space.get_mutations():
			mutant = mutation.get_mutant()
			for state in mutation.get_states():
				location = state.get_location()
				if (state.category == "eva_cond") or (state.category == "set_expr"):
					if not (state in self.sta_mut):
						self.sta_mut[state] = set()
					self.sta_mut[state].add(mutant)
					if not (location in self.loc_sta):
						self.loc_sta[location] = set()
					self.loc_sta[location].add(state)
					if not (mutant in self.mut_sta):
						self.mut_sta[mutant] = set()
					self.mut_sta[mutant].add(state)
		return

	def get_locations(self):
		"""
		:return: the set of locations being annotated with features
		"""
		return self.loc_sta.keys()

	def get_states(self):
		"""
		:return: the set of states being encoded
		"""
		return self.sta_mut.keys()

	def get_mutants(self):
		"""
		:return: the set
		"""
		return self.mut_sta.keys()

	def get_states_of_location(self, location: jccode.AstCirNode):
		"""
		:param location:
		:return: the set of states annotated at the location
		"""
		if location in self.loc_sta:
			return self.loc_sta[location]
		return set()

	def get_mutants_of_state(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: the set of mutants connected with the state
		"""
		if state in self.sta_mut:
			return self.sta_mut[state]
		return set()

	def get_states_of_mutant(self, mutant: jcmuta.Mutant):
		"""
		:param mutant:
		:return: the set of states connected with mutant
		"""
		if mutant in self.mut_sta:
			return self.mut_sta[mutant]
		return set()


class MutationZ3Prover:
	"""
	It implements the equivalence proof based on expression.
	"""

	def __init__(self):
		self.neq_class = 0
		self.ceq_class = 1
		self.beq_class = 2
		self.veq_class = 3
		self.seq_class = 4
		self.req_class = 5
		self.timeout = 1000
		self.parser = SymbolToZ3Parser()
		self.solutions = dict()	# ContextState --> class_flag
		return

	def __differ__(self, orig_value, muta_value):
		"""
		:param orig_value:
		:param muta_value:
		:return: differential condition
		"""
		self.timeout = self.timeout
		try:
			# return z3.Not(z3.eq(orig_value, muta_value))
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
		if isinstance(condition, bool):
			condition = z3.BoolVal(condition)
		solver = z3.Solver()
		solver.add(condition)
		for assumption in assumptions:
			solver.add(assumption)
		solver.set("timeout", self.timeout)
		if solver.check() == z3.unsat:
			return True
		return False

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

	def __solve__(self, state: jcmuta.ContextState):
		"""
		:param state:
		:return: It solves the state and produce the class
		"""
		if state.get_category() == "eva_cond":
			assumeLib = set()
			condition = self.parser.parse_to(state.get_loperand(), None, assumeLib, True)
			if condition is None:
				return self.neq_class
			elif self.__check__(condition, assumeLib):
				return self.ceq_class
			else:
				return self.neq_class
		elif state.get_category() == "set_expr":
			## 1. parse
			orig_states, muta_states = dict(), dict()
			loperand = self.parser.parse_to(state.get_loperand(), orig_states, None, True)
			roperand = self.parser.parse_to(state.get_roperand(), muta_states, None, False)
			if (loperand is None) or (roperand is None):
				return self.neq_class

			## 2, syntax-directed analysis
			location = state.get_location()
			parent = location.get_parent()
			if (parent is not None) and (parent.get_node_type() == "retr_stmt"):
				if self.__check_value__(loperand, roperand):
					return self.req_class
				else:
					return self.neq_class
			elif (location.get_child_type() == "evaluate") or (location.get_child_type() == "n_condition") or (location.get_child_type() == "element"):
				if self.__check_state__(orig_states, muta_states):
					return self.seq_class
				else:
					return self.neq_class
			else:
				if self.__check_value__(loperand, roperand):
					if len(muta_states) == 0:
						return self.veq_class
					elif self.__check_state__(orig_states, muta_states):
						return self.beq_class
					else:
						return self.neq_class
				else:
					return self.neq_class
		else:
			return self.neq_class

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

	def prove_all(self, project: jcmuta.CProject):
		"""
		:param project:
		:return: mutant_state_class [Mutant; (ContextState, int)]
		"""
		## initialization
		inputs = MutationZ3Inputs(project)
		output = dict()	# Mutant --> (ContextState, int)
		total, counter, steps = len(inputs.get_mutants()), 0, 3000
		alive_number, state_solutions = 0, dict()
		print("\tCollect {} states from {} mutations in {}.".
			  format(len(inputs.get_states()), len(inputs.get_mutants()), project.program.name))

		## Proof-Process
		begTime = time.time()
		for mutant in inputs.get_mutants():
			## 0. print the procedure
			if counter % steps == 0:
				print("\t\t==> Proceed[{}/{}]".format(counter, total))
			counter += 1
			## 1. account for the alive
			if mutant.get_result().is_killed_in(None):
				continue
			else:
				alive_number = alive_number + 1
			## 2. proof procedure
			for state in inputs.get_states_of_mutant(mutant):
				state: jcmuta.ContextState
				__res__ = self.__prove__(state)
				if __res__ != self.neq_class:
					output[mutant] = (state, __res__)
					state_solutions[state] = __res__
					break
		endTime = time.time()
		seconds = int(endTime - begTime)

		## report-summary
		equal_number, state_number = len(output), len(state_solutions)
		ceq_count, beq_count, seq_count, veq_count, req_count = 0, 0, 0, 0, 0
		for mutant, state_class in output.items():
			state = state_class[0]
			__res__ = state_class[1]
			if __res__ == self.ceq_class:
				ceq_count += 1
			elif __res__ == self.beq_class:
				beq_count += 1
			elif __res__ == self.seq_class:
				seq_count += 1
			elif __res__ == self.veq_class:
				veq_count += 1
			elif __res__ == self.req_class:
				req_count += 1
		ratio = len(output) / (alive_number + 0.001)
		ratio = int(ratio * 10000) / 100.0
		print("\tALV = {}\tEQV = {}\tSTA = {}\tTIM = {}\tRAT = {}%".format(alive_number, equal_number, state_number, seconds, ratio))
		print("\tCEQ = {}\tBEQ = {}\tVEQ = {}\tSEQ = {}\tREQ = {}".format(ceq_count, beq_count, veq_count, seq_count, req_count))
		return output


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
				if flags == 1:
					writer.write("\tCEQ")
				elif flags == 2:
					writer.write("\tBEQ")
				elif flags == 3:
					writer.write("\tSEQ")
				elif flags == 4:
					writer.write("\tVEQ")
				elif flags == 5:
					writer.write("\tREQ")
				else:
					writer.write("\tNEQ")
				writer.write("\t{}\t{}\t({})\t({})".format(category, location, loperand, roperand))
			else:
				writer.write("\tNEQ")

			writer.write("\n")
	return


def test_symbol_prover(project: jcmuta.CProject, file_path: str):
	prover = MutationZ3Prover()
	tce_ids = load_TEQ_results(project, "/home/dzt2/Development/Data/zexp/TCE")
	print("Testing {} with {} mutants and {} TCE.".
		  format(project.program.name, len(project.muta_space.get_mutants()), len(tce_ids)))
	mutant_state_class = prover.prove_all(project)
	write_mutant_class_state(project, mutant_state_class, tce_ids, file_path)
	print()
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/featuresAll"
	post_path = "/home/dzt2/Development/Data/zexp/resultsAll"
	for project_name in os.listdir(root_path):
		project_directory = os.path.join(root_path, project_name)
		c_project = jcmuta.CProject(project_directory, project_name)
		test_symbol_parser(c_project, os.path.join(post_path, project_name + ".sz3"))
		# test_symbol_prover(c_project, os.path.join(post_path, project_name + ".mz3"))
	print("Testing End...")

