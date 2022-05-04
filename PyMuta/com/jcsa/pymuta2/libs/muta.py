""" This file defines the model to describe mutant and test results """


import os
import random
from collections import deque

import com.jcsa.pymuta2.libs.base as jcbase
import com.jcsa.pymuta2.libs.code as jccode


class CProject:
	"""
	It represents the mutation test project for analysis and execution.
	"""

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory: the directory where xxx.tst, xxx.stc, xxx.mut and xxx.res are generated
		:param file_name: xxx to obtian feature files
		"""
		self.program = jccode.CProgram(directory, file_name)
		self.states = dict()
		tst_file = os.path.join(directory, file_name + ".tst")
		mut_file = os.path.join(directory, file_name + ".mut")
		res_file = os.path.join(directory, file_name + ".res")
		sym_file = os.path.join(directory, file_name + ".sym")
		ctx_file = os.path.join(directory, file_name + ".ctx")
		self.test_space = TestCaseSpace(self, tst_file)
		self.muta_space = MutantSpace(self, mut_file, res_file)
		self.sym_tree = SymbolTree(sym_file)
		self.context_tree = ContextMutationTree(self, ctx_file)
		return

	def get_state(self, text: str):
		"""
		:param text: category$location$loperand$roperand
		:return:
		"""
		items = text.strip().split('$')
		if len(items) > 0:
			category = items[0].strip()
			loct_id = jcbase.CToken.parse(items[1].strip()).get_token_value()
			location = self.program.ast_cir_tree.get_node(loct_id)
			loperand = self.sym_tree.get_sym_node(items[2].strip())
			roperand = self.sym_tree.get_sym_node(items[3].strip())
			state = ContextMutationState(category, location, loperand, roperand)
			if not (str(state) in self.states):
				self.states[str(state)] = state
			state = self.states[str(state)]
			state: ContextMutationState
			return state
		return None


class TestCase:
	"""
	Each test case is defined as a tuple of [id, parameter] where id is integer identifier and parameter
	is the String command parameter used.
	"""

	def __init__(self, space, test_id: int, parameter: str):
		"""
		:param space: the space of test cases where it is defined
		:param test_id: the unique integer of the test case used
		:param parameter: the parameter to execute the test case
		"""
		space: TestCaseSpace
		self.space = space
		self.test_id = test_id
		self.parameter = parameter.strip()
		return

	def get_space(self):
		"""
		:return: the space of test cases where it is defined
		"""
		return self.space

	def get_test_id(self):
		"""
		:return: the unique integer of the test case used
		"""
		return self.test_id

	def get_parameter(self):
		"""
		:return: the parameter to execute the test case
		"""
		return self.parameter

	def __str__(self):
		return "test@{}".format(self.test_id)


class TestCaseSpace:
	"""
	The space of test cases defined and used in execution.
	"""

	def __init__(self, project: CProject, tst_file_path: str):
		"""
		:param project:
		:param tst_file_path: file that provides definition of each test case in space
		"""
		self.project = project
		self.test_cases = list()
		self.__parse__(tst_file_path)
		return

	def get_project(self):
		"""
		:return: The mutation testing project where the space is created
		"""
		return self.project

	def get_test_cases(self):
		"""
		:return: the set of all test cases defined in the space
		"""
		return self.test_cases

	def get_test_case(self, test_id: int):
		"""
		:param test_id:
		:return: the test case w.r.t. the unique identified in the space
		"""
		test_case = self.test_cases[test_id]
		test_case: TestCase
		return test_case

	def __parse__(self, tst_file: str):
		"""
		:param tst_file:
		:return:
		"""
		self.test_cases.clear()
		test_dict = dict()
		with open(tst_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					test_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					parameter = jcbase.CToken.parse(items[1].strip()).get_token_value()
					test_case = TestCase(self, test_id, parameter)
					test_dict[test_case.get_test_id()] = test_case
		for test_id in range(0, len(test_dict)):
			test_case = test_dict[test_id]
			self.test_cases.append(test_case)
		return


class Mutation:
	"""
	The syntactic mutation seeded in source code is defined as:
		{mutant, class, operator, location, parameter}
	which is the definition body of a given mutant.
	"""

	def __init__(self, mutation_class: str, mutation_operator: str, location: jccode.AstNode, parameter):
		"""
		:param mutation_class: the class name of mutation operator applied to generate this one
		:param mutation_operator: the name of mutation operator applied to generate this mutant
		:param location: the location in AST where the mutation is injected
		:param parameter: the parameter might be None if no parameter is necessary for definition
		"""
		self.__category__ = mutation_class
		self.__operator__ = mutation_operator
		self.__location__ = location
		self.__parameter__ = parameter
		return

	def get_mutation_class(self):
		"""
		:return: the class name of mutation operator applied to generate this one
		"""
		return self.__category__

	def get_mutation_operator(self):
		"""
		:return: the name of mutation operator applied to generate this mutant
		"""
		return self.__operator__

	def get_location(self):
		"""
		:return: the location in AST where the mutation is injected
		"""
		return self.__location__

	def get_parameter(self):
		"""
		:return: the parameter might be None if no parameter is necessary for definition
		"""
		return self.__parameter__

	def has_parameter(self):
		"""
		:return: False if no parameter is needed for defining this mutation
		"""
		return not(self.__parameter__ is None)


class Mutant:
	"""
	A mutant instance for being seeded, analyzed or executed
	"""

	def __init__(self, space, muta_id: int, mutation: Mutation):
		"""
		:param space: the mutation space where the mutant is defined
		:param muta_id: unique integer ID to tag this mutant in space
		:param mutation: the syntactic mutation to define this mutant
		"""
		self.space = space
		self.muta_id = muta_id
		self.mutation = mutation
		self.result = MutationResult(self, "")
		self.c_mutant = None
		self.w_mutant = None
		self.s_mutant = None
		return

	def get_space(self):
		"""
		:return: the mutation space where the mutant is defined
		"""
		return self.space

	def get_muta_id(self):
		"""
		:return: unique integer ID to tag this mutant in space
		"""
		return self.muta_id

	def get_mutation(self):
		"""
		:return: the syntactic mutation to define this mutant
		"""
		return self.mutation

	def get_result(self):
		"""
		:return: test result of this mutant during execution
		"""
		return self.result

	def get_c_mutant(self):
		"""
		:return: Mutant of which killing ensures the coverage of this mutant
		"""
		self.c_mutant: Mutant
		return self.c_mutant

	def get_w_mutant(self):
		"""
		:return: Mutant of which killing ensures the infection of this mutant
		"""
		self.w_mutant: Mutant
		return self.w_mutant

	def get_s_mutant(self):
		"""
		:return: Mutant of which killing ensures the killing of this mutant
		"""
		self.s_mutant: Mutant
		return self.s_mutant

	def __str__(self):
		return "mut@{}".format(self.muta_id)


class MutationResult:
	"""
	It records the result in form of bit-string to describe of which test case(s) kill the target mutant.
	"""

	def __init__(self, mutant: Mutant, result: str):
		"""
		:param mutant: the mutant for being killed by the test results
		:param result: the bit-string of the test cases for killing it
		"""
		self.mutant = mutant
		self.result = result
		return

	def get_mutant(self):
		return self.mutant

	def is_killed_by(self, test):
		"""
		:param test: TestCase or int
		:return: True if the test kills the mutant
		"""
		if isinstance(test, TestCase):
			tid = test.get_test_id()
		else:
			test: int
			tid = test
		if tid < 0 or tid >= len(self.result):
			return False
		else:
			return self.result[tid] == '1'

	def is_killed_in(self, tests=None):
		"""
		:param tests: collection of test cases or their integer IDs or None to represent all of tests in project
		:return: whether mutant is killed by any test in the set
		"""
		if tests is None:
			return '1' in self.result
		else:
			for test in tests:
				if self.is_killed_by(test):
					return True
			return False

	def get_killing_set(self, tests=None):
		"""
		:param tests: the set of test cases selected in which tests are selected for killing it or None if
					  the entire test cases in the space of the project are under considerations.
		:return: set of test cases (ID) that kill the target mutant
		"""
		killing_set = list()
		if tests is None:
			for test_id in range(0, len(self.result)):
				if self.result[test_id] == '1':
					killing_set.append(test_id)
		else:
			for test in tests:
				if isinstance(test, TestCase):
					test_id = test.get_test_id()
				else:
					test: int
					test_id = test
				if self.result[test_id] == '1':
					killing_set.append(test_id)
		return killing_set


class MutantSpace:
	"""
	The space where the mutants are defined and under consideration.
	"""

	def __init__(self, project: CProject, mut_file: str, res_file: str):
		self.project = project
		self.mutants = list()
		self.__parse__(mut_file, res_file)
		return

	def get_project(self):
		return self.project

	def get_mutants(self):
		return self.mutants

	def get_mutant(self, muta_id: int):
		mutant = self.mutants[muta_id]
		mutant: Mutant
		return mutant

	def __parse__(self, mut_file: str, res_file: str):
		"""
		:param mut_file: file to provide definition of mutations
		:param res_file: file to provide test results of mutants
		:return:
		"""
		mutant_dict = dict()
		with open(mut_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					muta_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					m_class = items[1].strip()
					m_operator = items[2].strip()
					loct_id = jcbase.CToken.parse(items[3].strip()).get_token_value()
					location = self.project.program.ast_tree.get_ast_node(loct_id)
					parameter = jcbase.CToken.parse(items[4].strip()).get_token_value()
					mutation = Mutation(m_class, m_operator, location, parameter)
					mutant = Mutant(self, muta_id, mutation)
					mutant_dict[mutant.get_muta_id()] = mutant

		self.mutants.clear()
		for muta_id in range(0, len(mutant_dict)):
			mutant = mutant_dict[muta_id]
			self.mutants.append(mutant)

		with open(mut_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = jcbase.CToken.parse(items[0].strip()).get_token_value()
					cid = jcbase.CToken.parse(items[5].strip()).get_token_value()
					wid = jcbase.CToken.parse(items[6].strip()).get_token_value()
					sid = jcbase.CToken.parse(items[7].strip()).get_token_value()
					mutant = mutant_dict[mid]
					mutant.c_mutant = mutant_dict[cid]
					mutant.w_mutant = mutant_dict[wid]
					mutant.s_mutant = mutant_dict[sid]

		with open(res_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					mid = jcbase.CToken.parse(items[0].strip()).get_token_value()
					mutant = self.mutants[mid]
					mutant: Mutant
					mutant.get_result().result = items[1].strip()
		return


class MutationEvaluation:
	def __init__(self, project: CProject):
		self.project = project
		return

	def select_mutants_by_classes(self, class_names):
		mutants = list()
		for mutant in self.project.muta_space.get_mutants():
			mutant: Mutant
			if mutant.get_mutation().get_mutation_class() in class_names:
				mutants.append(mutant)
		return mutants

	def __random_test_for__(self, mutant):
		"""
		:param mutant: None for random test case
		:return: a random test for killing target mutant
		"""
		if mutant is None:
			number = len(self.project.test_space.test_cases)
			return random.randint(0, number - 1)
		else:
			mutant: Mutant
			tests = mutant.get_result().get_killing_set(None)
			if len(tests) > 0:
				count = random.randint(0, len(tests) - 1)
				for test in tests:
					if count <= 0:
						return test
					count = count - 1
				return tests[-1]
			else:
				return None

	def select_tests_for_mutants(self, mutants):
		"""
		:param mutants:
		:return: set of tests that kill the target mutants
		"""
		remain, remove = set(), set()
		for mutant in mutants:
			mutant: Mutant
			remain.add(mutant)
		tests = set()
		while len(remain) > 0:
			select_mutant = None
			for mutant in remain:
				select_mutant = mutant
				break
			if select_mutant is None:
				break
			remove.add(select_mutant)
			test = self.__random_test_for__(select_mutant)
			if not(test is None):
				tests.add(test)
				for mutant in remain:
					if mutant.get_result().is_killed_by(test):
						remove.add(mutant)
			for mutant in remove:
				if mutant in remain:
					remain.remove(mutant)
		return tests

	def select_tests_for_random(self, min_number):
		tests = set()
		while len(tests) < min_number:
			test = self.__random_test_for__(None)
			if not(test is None):
				test: int
				tests.add(test)
		return tests

	def measure_score(self, mutants=None, tests=None):
		"""
		:param mutants:
		:param tests:
		:return: total_mutants, killed_mutants, score
		"""
		if mutants is None:
			mutants = self.project.muta_space.get_mutants()
		killed_mutants = list()
		for mutant in mutants:
			mutant: Mutant
			if mutant.get_result().is_killed_in(tests):
				killed_mutants.append(mutant)
		if len(killed_mutants) > 0:
			score = len(killed_mutants) / len(mutants)
		else:
			score = 0.0
		return int(score * 1000000) / 10000.0


class SymbolNode:
	"""
	class ID source{Token as AstNode, CirNode, Execution, Constant or Nullptr} data_type content code parent children
	"""
	def __init__(self, class_name: str, class_id: int, source: jcbase.CToken, data_type: jcbase.CToken, content: jcbase.CToken, code: str):
		"""
		:param class_name: class of symbolic node
		:param class_id: unique ID of symbolic node
		:param source: [AstNode, CirNode, Execution, Constant or None]
		:param data_type: code of data type
		:param content: Token as String, Operator, Constant or None
		:param code: simplified code to describe the symbolic node
		"""
		self.class_name = class_name
		self.class_id = class_id
		self.source = source
		self.data_type = data_type
		self.content = content
		self.code = code
		self.parent = None
		self.children = list()
		return

	def get_class_name(self):
		return self.class_name

	def get_class_id(self):
		return self.class_id

	def get_source(self):
		return self.source

	def get_data_type(self):
		return self.data_type

	def get_content(self):
		return self.content

	def get_code(self):
		return self.code

	def get_parent(self):
		return self.parent

	def get_children(self):
		return self.children

	def __str__(self):
		return "sym@{}@{}".format(self.class_name, self.class_id)

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def add_child(self, child):
		child: SymbolNode
		child.parent = self
		self.children.append(child)
		return


class SymbolTree:
	"""
	It manages all the symbolic nodes and their structure read from xxx.sym file
	"""
	def __init__(self, sym_file_path: str):
		"""
		:param sym_file_path:
		"""
		self.sym_nodes = dict()		# string --> SymNode
		self.__parse__(sym_file_path)
		return

	def get_sym_nodes(self):
		return self.sym_nodes.values()

	def get_sym_node(self, key: str):
		"""
		:param key: sym@class@id
		:return:
		"""
		node = self.sym_nodes[key]
		node: SymbolNode
		return node

	def __parse__(self, sym_file_path: str):
		"""
		:param sym_file_path:
		:return:
		"""
		self.sym_nodes.clear()
		with open(sym_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					key = items[0].strip()
					key_token = jcbase.CToken.parse(key).get_token_value()
					class_name = items[1].strip()
					class_id = key_token[1]
					source = jcbase.CToken.parse(items[2].strip())
					data_type = jcbase.CToken.parse(items[3].strip())
					content = jcbase.CToken.parse(items[4].strip())
					code = jcbase.CToken.parse(items[5].strip())
					sym_node = SymbolNode(class_name, class_id, source, data_type, content, code.get_token_value())
					self.sym_nodes[key] = sym_node
		with open(sym_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					parent = self.sym_nodes[items[0].strip()]
					parent: SymbolNode
					children_items = items[6].strip().split(' ')
					for k in range(1, len(children_items) - 1):
						child_key = children_items[k].strip()
						child = self.sym_nodes[child_key]
						parent.add_child(child)
		return


class ContextMutationState:
	"""
	category, location, loperand, roperand
	"""

	def __init__(self, category: str, location: jccode.AstCirNode, loperand: SymbolNode, roperand: SymbolNode):
		"""
		:param category: the category of the execution state
		:param location: the location of the state evaluated
		:param loperand: the left-operand to define the state
		:param roperand: the right-operand to define the state
		"""
		self.category = category
		self.location = location
		self.loperand = loperand
		self.roperand = roperand
		return

	def get_category(self):
		return self.category

	def get_location(self):
		return self.location

	def get_loperand(self):
		return self.loperand

	def get_roperand(self):
		return self.roperand

	def __str__(self):
		return "{}${}${}${}".format(self.category, self.location, self.loperand, self.roperand)


class ContextMutationTree:
	"""
	It defines the contextual mutation state trees.
	"""

	def __init__(self, project: CProject, file_path: str):
		self.project = project
		self.__load_nodes__(file_path)
		self.__load_links__(file_path)
		return

	def __load_nodes__(self, file_path: str):
		node_dict = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					if items[0].strip() == "[NODE]":
						node_id = int(items[1].strip())
						state = self.project.get_state(items[2].strip())
						annotations = set()
						for k in range(3, len(items)):
							annotations.add(self.project.get_state(items[k].strip()))
						node = ContextMutationNode(self, node_id, state, annotations)
						node_dict[node.get_node_id()] = node
		self.nodes = list()
		for node_id in range(0, len(node_dict)):
			self.nodes.append(node_dict[node_id])
		return

	def __load_links__(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		self.index = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					if items[0].strip() == "[EDGE]":
						source = self.nodes[int(items[1].strip())]
						for k in range(2, len(items)):
							target = self.nodes[int(items[k].strip())]
							source.connect(target)
					elif items[0].strip() == "[LINK]":
						mutant_id = jcbase.CToken.parse(items[1].strip()).get_token_value()
						mutant = self.project.muta_space.get_mutant(mutant_id)
						node = self.nodes[int(items[2].strip())]
						self.index[mutant] = node
		return

	def get_project(self):
		return self.project

	def __len__(self):
		return len(self.nodes)

	def get_nodes(self):
		return self.nodes

	def get_mutants(self):
		return self.index.keys()

	def get_roots(self):
		"""
		:return: the root referring to the mutations
		"""
		return self.index.values()

	def get_node(self, nid: int):
		return self.nodes[nid]

	def get_root(self, mutant: Mutant):
		if mutant in self.index:
			return self.index[mutant]
		else:
			return None

	def get_nodes_of(self, mutant: Mutant):
		"""
		:param mutant:
		:return: all the nodes connected with the mutation
		"""
		nodes = set()
		if mutant in self.index:
			root = self.index[mutant]
			queue = deque()
			queue.append(root)
			while len(queue) > 0:
				node = queue.popleft()
				node: ContextMutationNode
				nodes.add(node)
				for ou_node in node.get_ou_nodes():
					ou_node: ContextMutationNode
					if not (ou_node in nodes):
						queue.append(ou_node)
		return nodes


class ContextMutationNode:
	"""
	It models a contextual mutation node in state transition tree.
	"""

	def __init__(self, tree: ContextMutationTree, node_id: int, state: ContextMutationState, annotations):
		"""
		:param tree:
		:param node_id:
		:param state:
		:param annotations:
		"""
		self.tree = tree
		self.node_id = node_id
		self.state = state
		self.annotations = list()
		for annotation in annotations:
			annotation: ContextMutationState
			self.annotations.append(annotation)
		self.in_nodes = list()
		self.ou_nodes = list()
		return

	def get_tree(self):
		return self.tree

	def get_node_id(self):
		return self.node_id

	def get_state(self):
		return self.state

	def get_annotations(self):
		return self.annotations

	def get_in_nodes(self):
		return self.in_nodes

	def get_ou_nodes(self):
		return self.ou_nodes

	def connect(self, target):
		"""
		:param target:
		:return:
		"""
		target: ContextMutationNode
		target.in_nodes.append(self)
		self.ou_nodes.append(target)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zext2/features"
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_project = CProject(directory, file_name)
		print(file_name, "loads", len(c_project.muta_space.get_mutants()), "mutations",
			  "and", len(c_project.test_space.get_test_cases()), "test cases.")
		out_file = os.path.join("/home/dzt2/Development/Data/zext2/trash", file_name + ".txt")
		with open(out_file, 'w') as writer:
			for mutant in c_project.context_tree.get_mutants():
				mid = mutant.get_muta_id()
				res = mutant.get_result().is_killed_in(None)
				if res:
					result = "Killed"
				else:
					result = "Alive"
				m_class = mutant.get_mutation().get_mutation_class()
				m_oprt = mutant.get_mutation().get_mutation_operator()
				m_line = mutant.get_mutation().get_location().line_of(False)
				m_code = mutant.get_mutation().get_location().generate_code(64)
				m_parameter = mutant.get_mutation().get_parameter()
				writer.write("MID#{}\t{}\t{}\t{}\t#{}\t\"{}\"\t[{}]\n".format(mid, result, m_class, m_oprt, m_line,
																			  m_code, m_parameter))
				for node in c_project.context_tree.get_nodes_of(mutant):
					state = node.get_state()
					writer.write("[{}]\t{}@{}\t#{}\t\"{}\"\t({})\t({})\n".format(state.get_category(),
																				 state.get_location().get_node_type(),
																				 state.get_location().get_node_id(),
																				 state.get_location().get_ast_source().line_of(False),
																				 state.get_location().get_ast_source().generate_code(64),
																				 state.get_loperand().get_code(),
																				 state.get_roperand().get_code()))
					for annotation in node.get_annotations():
						writer.write("\t[{}]\t{}@{}\t#{}\t\"{}\"\t({})\t({})\n".format(annotation.get_category(),
																					   annotation.get_location().get_node_type(),
																					   annotation.get_location().get_node_id(),
																					   annotation.get_location().get_ast_source().line_of(False),
																					   annotation.get_location().get_ast_source().generate_code(64),
																					   annotation.get_loperand().get_code(),
																					   annotation.get_roperand().get_code()))
				writer.write("\n")
	print("Testing ends for all.")
