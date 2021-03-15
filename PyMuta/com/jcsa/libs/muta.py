"""
This file defines the mutation project data
"""


import os
import random
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jcparse


class CProject:
	"""
	Mutation Testing Project
	"""

	def __init__(self, directory: str, file_name: str):
		self.program = jcparse.CProgram(directory, file_name)
		tst_file_path = os.path.join(directory, file_name + ".tst")
		mut_file_path = os.path.join(directory, file_name + ".mut")
		res_file_path = os.path.join(directory, file_name + ".res")
		sym_file_path = os.path.join(directory, file_name + ".sym")
		self.test_space = TestSpace(self, tst_file_path)
		self.sym_tree = jcbase.SymTree(sym_file_path)
		self.mutant_space = MutantSpace(self, mut_file_path, res_file_path)
		self.evaluation = MutationTestEvaluation(self)
		return

	def load_documents(self, post: str):
		"""
		:param post:
		:return: document w.r.t. given postfix
		"""
		document = SymDocument(self)
		directory = self.program.directory
		for file_name in os.listdir(directory):
			if file_name.endswith(post):
				file_path = os.path.join(directory, file_name)
				document.__loading__(file_path)
		return document


class TestCase:
	"""
	test case with parameter
	"""
	def __init__(self, space, test_id: int, parameter: str):
		space: TestSpace
		self.space = space
		self.test_id = test_id
		self.parameter = parameter.strip()
		return

	def get_space(self):
		return self.space

	def get_test_id(self):
		return self.test_id

	def get_parameter(self):
		return self.parameter

	def get_killing_mutants(self, mutants=None):
		"""
		:param mutants: the set of mutants or None for all the mutants in project
		:return: the set of mutants killed by this test
		"""
		if mutants is None:
			mutants = self.space.get_project().mutant_space.get_mutants()
		killed_mutants = set()
		for mutant in mutants:
			mutant: Mutant
			if mutant.get_result().is_killed_by(self):
				killed_mutants.add(mutant)
		return killed_mutants


class TestSpace:
	def __init__(self, project: CProject, tst_file_path: str):
		self.project = project
		self.test_cases = list()
		self.__parse__(tst_file_path)
		return

	def get_project(self):
		return self.project

	def get_test_cases(self):
		return self.test_cases

	def get_test_case(self, test_id: int):
		"""
		:param test_id:
		:return:
		"""
		test_case = self.test_cases[test_id]
		test_case: TestCase
		return test_case

	def __parse__(self, tst_file_path: str):
		test_case_dict = dict()
		with open(tst_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					test_id = int(items[0].strip())
					parameter = jcbase.CToken.parse(items[1].strip()).get_token_value()
					test_case = TestCase(self, test_id, parameter)
					test_case_dict[test_case.get_test_id()] = test_case
		self.test_cases.clear()
		for k in range(0, len(test_case_dict)):
			self.test_cases.append(test_case_dict[k])
		return


class Mutation:
	"""
	syntactic mutation
	"""
	def __init__(self, mutant, mutation_class: str, mutation_operator: str, location: jcparse.AstNode,
				 parameter: jcbase.CToken):
		"""
		:param mutant:
		:param mutation_class:
		:param mutation_operator:
		:param location:
		:param parameter:
		"""
		mutant: Mutant
		self.mutant = mutant
		self.mutation_class = mutation_class
		self.mutation_operator = mutation_operator
		self.location = location
		self.parameter = parameter
		return

	def get_mutant(self):
		"""
		:return: mutant that is defined by this mutation
		"""
		return self.mutant

	def get_mutation_class(self):
		return self.mutation_class

	def get_mutation_operator(self):
		return self.mutation_operator

	def get_location(self):
		return self.location

	def get_parameter(self):
		return self.parameter


class Mutant:
	"""
	space, ID, mutation, result, coverage, weak, strong
	"""
	def __init__(self, space, mut_id: int, mutation: Mutation):
		space: MutantSpace
		self.space = space
		self.mut_id = mut_id
		self.mutation = mutation
		self.c_mutant = None		# coverage mutation
		self.w_mutant = None		# weak mutation
		self.s_mutant = None		# strong mutation
		self.result = MutationResult(self)
		return

	def get_space(self):
		return self.space

	def get_mut_id(self):
		return self.mut_id

	def get_mutation(self):
		return self.mutation

	def has_result(self):
		return not(self.result is None)

	def get_result(self):
		self.result: MutationResult
		return self.result

	def get_coverage_mutant(self):
		self.c_mutant: Mutant
		return self.c_mutant

	def get_weak_mutant(self):
		self.w_mutant: Mutant
		return self.w_mutant

	def get_strong_mutant(self):
		self.s_mutant: Mutant
		return self.s_mutant

	def get_killing_tests(self, tests=None):
		"""
		:param tests: set of test cases or None for all the tests in space
		:return: test cases that kill this mutant
		"""
		test_cases = set()
		if tests is None:
			tests = self.space.get_project().test_space.get_test_cases()
		for test in tests:
			test: TestCase
			if self.result.is_killed_by(test):
				test_cases.add(test)
		return test_cases


class MutationResult:
	"""
	It preserves the test results for each mutant.
	"""
	def __init__(self, mutant: Mutant):
		"""
		create an empty test result for mutant
		:param mutant:
		"""
		self.mutant = mutant
		self.kill_string = ""
		return

	def get_mutant(self):
		return self.mutant

	def get_length(self):
		return len(self.kill_string)

	def is_killed_by(self, test):
		"""
		:param test: TestCase or its Integer ID
		:return:
		"""
		if isinstance(test, TestCase):
			test: TestCase
			test_id = test.get_test_id()
		else:
			test: int
			test_id = test
		if test_id < len(self.kill_string):
			return self.kill_string[test_id] == '1'
		return False

	def is_killed_in(self, tests):
		"""
		:param tests: collection of TestCase(s) or their Integer ID
		:return: whether be killed by any test in the inputs collection
		"""
		for test in tests:
			if self.is_killed_by(test):
				return True
		return False

	def is_killable(self):
		"""
		:return: whether killed by any tests in the space
		"""
		return '1' in self.kill_string

	def __str__(self):
		return self.kill_string

	def set_result(self, kill_string: str):
		"""
		:param kill_string:
		:return: set the results of the instance
		"""
		self.kill_string = kill_string
		return


class MutantSpace:
	def __init__(self, project: CProject, mut_file_path: str, res_file_path: str):
		"""
		:param project:
		:param mut_file_path:
		:param res_file_path:
		"""
		self.project = project
		self.mutants = list()
		self.results = dict()	# String --> String, Set[MutationResult]
		self.__parse__(mut_file_path)
		self.__loads__(res_file_path)
		return

	def get_project(self):
		return self.project

	def get_mutants(self):
		return self.mutants

	def get_mutant(self, mut_id: int):
		mutant = self.mutants[mut_id]
		mutant: Mutant
		return mutant

	def __parse__(self, mut_file_path: str):
		mutant_dict = dict()
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mut_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					mutation_class = items[1].strip()
					mutation_operator = items[2].strip()
					ast_id = jcbase.CToken.parse(items[3].strip()).get_token_value()
					location = self.project.program.ast_tree.get_ast_node(ast_id)
					parameter = jcbase.CToken.parse(items[4].strip())
					mutation = Mutation(None, mutation_class, mutation_operator, location, parameter)
					mutant = Mutant(self, mut_id, mutation)
					mutation.mutant = mutant
					mutant_dict[mutant.get_mut_id()] = mutant
		with open(mut_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mut_id = jcbase.CToken.parse(items[0].strip()).get_token_value()
					mutant = mutant_dict[mut_id]
					cov_id = jcbase.CToken.parse(items[5].strip()).get_token_value()
					wek_id = jcbase.CToken.parse(items[6].strip()).get_token_value()
					sto_id = jcbase.CToken.parse(items[7].strip()).get_token_value()
					mutant.c_mutant = mutant_dict[cov_id]
					mutant.w_mutant = mutant_dict[wek_id]
					mutant.s_mutant = mutant_dict[sto_id]
		self.mutants.clear()
		for mut_id in range(0, len(mutant_dict)):
			self.mutants.append(mutant_dict[mut_id])
		return

	def __loads__(self, res_file_path: str):
		self.results.clear()
		with open(res_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					mutant = self.get_mutant(int(items[0].strip()))
					kill_string = items[1].strip()
					if not(kill_string in self.results):
						self.results[kill_string] = (kill_string, set())
					solution = self.results[kill_string]
					kill_string = solution[0]
					kill_result = solution[1]
					mutant.get_result().set_result(kill_string)
					kill_result.add(mutant.get_result())
		return


class MutationTestEvaluation:
	"""
	It implements the selection of mutation and test case and their evaluation of mutation score.
	"""
	def __init__(self, project: CProject):
		self.project = project
		return

	def __mutants__(self, mutants):
		if mutants is None:
			mutants = self.project.mutant_space.get_mutants()
		return mutants

	def __test_cases__(self, tests):
		if tests is None:
			tests = self.project.test_space.get_test_cases()
		return tests

	@staticmethod
	def __rand_select__(samples):
		"""
		:param samples:
		:return: a sample randomly selected from samples or none
		"""
		counter = random.randint(0, len(samples))
		selected_sample = None
		for sample in samples:
			selected_sample = sample
			counter = counter - 1
			if counter < 0:
				break
		return selected_sample

	@staticmethod
	def __find_test_for__(tests, mutant: Mutant):
		"""
		:param tests: find a random test that kill the mutant
		:param mutant:
		:return: None if no such tests exist in the given inputs
		"""
		mutant.get_killing_tests(tests)

	''' mutation selection '''
	def select_mutants_by_classes(self, classes, mutants=None):
		"""
		:param mutants: collection of mutants from which are selected
		:param classes: set of mutation operator classes
		:return: set of mutants of which class is in the given inputs
		"""
		selected_mutants = set()
		mutants = self.__mutants__(mutants)
		for mutant in mutants:
			mutant: Mutant
			if mutant.get_mutation().get_mutation_class() in classes:
				selected_mutants.add(mutant)
		return selected_mutants

	def select_mutants_by_results(self, killed: bool, mutants=None, tests=None):
		"""
		:param killed: true to select mutants that are killed
		:param mutants: from which the outputs are selected
		:param tests: the set of tests for killing the mutants
		:return: the set of mutants being killed (True) or not (False) by the given tests
		"""
		selected_mutants = set()
		mutants = self.__mutants__(mutants)
		for mutant in mutants:
			mutant: Mutant
			if tests is None:
				result = mutant.get_result().is_killable()
			else:
				result = mutant.get_result().is_killed_in(tests)
			if result == killed:
				selected_mutants.add(mutant)
		return selected_mutants

	''' test case selection '''
	def select_tests_for_random(self, min_number: int, tests=None):
		"""
		:param min_number: minimal number of selected tests
		:param tests: test cases from which the tests are selected
		:return: the set of test cases randomly selected from project or inputs
		"""
		tests = self.__test_cases__(tests)
		remain_tests = set()
		for test in tests:
			test: TestCase
			remain_tests.add(test)
		selected_tests = set()
		while len(selected_tests) < min_number and len(remain_tests) > 0:
			selected_test = MutationTestEvaluation.__rand_select__(remain_tests)
			selected_test: TestCase
			remain_tests.remove(selected_test)
			selected_tests.add(selected_test)
		return selected_tests

	def select_tests_for_mutants(self, mutants, tests=None):
		"""
		:param mutants: the mutants being killed by selected tests
		:param tests: test cases from which the tests are selected
		:return: selected_tests, remain_mutants
		"""
		''' 1. initialization '''
		remain_mutants = self.select_mutants_by_results(True, mutants, None)		# killable mutants among inputs
		tests = self.__test_cases__(tests)
		remain_tests, selected_tests = set(), set()
		for test in tests:
			test: TestCase
			remain_tests.add(test)

		''' 2. test case selection based on mutation '''
		while len(remain_tests) > 0 and len(remain_mutants) > 0:
			''' 2.1. select a random mutant for being killed '''
			mutant = MutationTestEvaluation.__rand_select__(remain_mutants)
			mutant: Mutant
			remain_mutants.remove(mutant)

			''' 2.2. select a random test for killing the mutants '''
			killing_tests = mutant.get_killing_tests(remain_tests)
			selected_test = MutationTestEvaluation.__rand_select__(killing_tests)
			selected_test: TestCase
			remain_tests.remove(selected_test)
			selected_tests.add(selected_test)

			''' 2.3. update the remaining mutants '''
			killed_mutants = selected_test.get_killing_mutants(remain_mutants)
			for killed_mutant in killed_mutants:
				remain_mutants.remove(killed_mutant)

		''' 3. return the test cases selected for killing all the mutants '''
		return selected_tests, remain_mutants

	''' mutation score evaluation '''
	def evaluate_mutation_score(self, mutants, tests):
		"""
		:param mutants:
		:param tests:
		:return: killed_number, over_score (on all mutants), valid_score (on killable mutants)
		"""
		mutants = self.__mutants__(mutants)
		total, valid, killed = 0, 0, 0
		for mutant in mutants:
			mutant: Mutant
			if tests is None:
				result = mutant.get_result().is_killable()
			else:
				result = mutant.get_result().is_killed_in(tests)
			if result:
				killed += 1
			if mutant.get_result().is_killable():
				valid += 1
			total += 1
		over_score, valid_score = 0.0, 0.0
		if killed > 0:
			over_score = killed / (total + 0.0)
			valid_score = killed / (valid + 0.0)
		return killed, over_score, valid_score


class SymCondition:
	"""
	It represents an instance that symbolically describes the condition required for being met during
	testing such that the specified objective of revealing mutation can be achieved in the context of
	program under test, which is modeled as following attributes.
		(1)	category: 	either satisfaction for constraint or observation for state error.
		(2) operator: 	refined type to describe what instance it was, such as "chg_bool".
		(3) execution:	the statement node in control flow graph in which it is evaluated.
		(4) location:	the location in C-intermediate code, where instance is defined on.
		(5) parameter:	the symbolic expression to refine description or none if not need.
	"""

	def __init__(self, category: str, operator: str, execution: jcparse.CirExecution,
				 location: jcparse.CirNode, parameter: jcbase.SymNode):
		"""
		:param category:	either satisfaction for constraint or observation for state error.
		:param operator:	refined type to describe what instance it was, such as "chg_bool".
		:param execution:	the statement node in control flow graph in which it is evaluated.
		:param location:	the location in C-intermediate code, where instance is defined on.
		:param parameter:	the symbolic expression to refine description or none if not need.
		"""
		self.category = category
		self.operator = operator
		self.execution= execution
		self.location = location
		self.parameter= parameter
		return

	def get_category(self):
		"""
		:return: either satisfaction for constraint or observation for state error.
		"""
		return self.category

	def get_operator(self):
		"""
		:return: refined type to describe what instance it was, such as "chg_bool".
		"""
		return self.operator

	def get_execution(self):
		"""
		:return: the statement node in control flow graph in which it is evaluated.
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the location in C-intermediate code, where instance is defined on.
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: the symbolic expression to refine description or none if not need.
		"""
		return self.parameter

	def has_parameter(self):
		"""
		:return: whether the parameter is symbolic expression
		"""
		return not(self.parameter is None)

	@staticmethod
	def encode(instance):
		"""
		:param instance:
		:return: encode the symbolic instance as category$operator$execution$location$parameter
		"""
		instance: SymCondition
		category = instance.category
		operator = instance.operator
		execution = "exe@{}@{}".format(instance.execution.get_function().get_name(), instance.execution.get_exe_id())
		location = "cir@{}".format(instance.location.get_cir_id())
		parameter = "n@null"
		if instance.has_parameter():
			parameter = "sym@{}@{}".format(instance.parameter.get_class_name(), instance.parameter.get_class_id())
		return "{}${}${}${}${}".format(category, operator, execution, location, parameter)

	@staticmethod
	def decode(word: str, project: CProject):
		"""
		:param word: category$operator$execution$location$parameter
		:param project: used to decode the word into structural description
		:return: SymInstance being decoded or None if word is invalid
		"""
		if len(word.strip()) > 0:
			items = word.strip().split('$')
			category = items[0].strip()
			operator = items[1].strip()
			exec_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
			loct_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
			para_token = jcbase.CToken.parse(items[4].strip()).get_token_value()
			execution = project.program.function_call_graph.get_execution(exec_token[0], exec_token[1])
			location = project.program.cir_tree.get_cir_node(loct_token)
			if para_token is None:
				parameter = None
			else:
				parameter = project.sym_tree.get_sym_node(items[4].strip())
			return SymCondition(category, operator, execution, location, parameter)
		else:
			return None

	def __str__(self):
		return SymCondition.encode(self)


INSTANCE_KEY = "instance"		# as source condition being evaluated and defined in testing
ANNOTATE_KEY = "annotate"		# as annotation to describe the semantics of condition under test
INFERRED_KEY = "inferred"		# as conditions inferred from the source symbolic condition


class SymInstance:
	"""
	The instance of the symbolic condition evaluated at some point during testing.
		(1)	condition: symbolic condition as the source being evaluated in testing.
		(2)	role: 	"instance" as the original source of condition being evaluated.
					"annotate" as the annotation generated from the context for refining evaluation.
					"inferred" as the conditions inferred from the input condition instance.
		(3) result:	True	--- if the condition is actually evaluated as true.
					False	---	if the condition is actually evaluated as false.
					None	---	the satisfaction of the condition is unknown.
	"""

	def __init__(self, role: str, condition: SymCondition, result: bool):
		"""
		:param role:	"instance" as the original source of condition being evaluated.
						"annotate" as the annotation generated from the context for refining evaluation.
						"inferred" as the conditions inferred from the input condition instance.
		:param condition: symbolic condition as the source being evaluated in testing.
		:param result:	True	--- if the condition is actually evaluated as true.
						False	---	if the condition is actually evaluated as false.
						None	---	the satisfaction of the condition is unknown.
		"""
		self.role = role
		self.condition = condition
		self.result = result
		return

	def get_role(self):
		"""
		:return:	"instance" as the original source of condition being evaluated.
					"annotate" as the annotation generated from the context for refining evaluation.
					"inferred" as the conditions inferred from the input condition instance.
		"""
		return self.role

	def get_condition(self):
		"""
		:return: symbolic condition as the source being evaluated in testing.
		"""
		return self.condition

	def get_result(self):
		"""
		:return:	True	--- if the condition is actually evaluated as true.
					False	---	if the condition is actually evaluated as false.
					None	---	the satisfaction of the condition is unknown.
		"""
		return self.result


class SymInstanceNode:
	"""
	The node in execution path with conditions annotated at each point, denoted as:
		---	instances: the collection of instances of symbolic conditions being met
	"""

	def __init__(self, path):
		"""
		:param path:
		"""
		path: SymInstancePath
		self.path = path
		self.instances = list()
		return

	def get_path(self):
		"""
		:return: the path where the state node is defined
		"""
		return self.path

	def get_instances(self):
		"""
		:return: the collection of instances of symbolic conditions being met
		"""
		return self.instances

	def get_instances_of(self, key: str):
		"""
		:param key: instance or annotate or inferred
		:return: the set of instances w.r.t. the given key
		"""
		instances = list()
		for instance in self.instances:
			instance: SymInstance
			if instance.get_role() == key:
				instances.append(instance)
		return instances

	def __add_word__(self, word: str):
		"""
		:param word: role$result$category$operator$execution$location$parameter
		:return: add the instance of condition into the node
		"""
		if '$' in word:
			items = word.strip().split('$')
			role = items[0].strip()
			result = jcbase.CToken.parse(items[1].strip()).get_token_value()
			category = items[2].strip()
			operator = items[3].strip()
			execution = items[4].strip()
			location = items[5].strip()
			parameter = items[6].strip()
			project = self.path.get_document().project
			word = "{}${}${}${}${}".format(category, operator, execution, location, parameter)
			if word in self.path.get_document().conditions:
				condition = self.path.get_document().conditions[word]
			else:
				condition = SymCondition.decode(word, project)
				self.path.get_document().conditions[word] = condition
			condition: SymCondition
			instance = SymInstance(role, condition, result)
			self.instances.append(instance)
		return


class SymInstancePath:
	"""
	The execution path w.r.t. a mutant and test case annotated with a set of condition instances defined.
	"""

	def __init__(self, document, mutant: Mutant, test: TestCase):
		"""
		:param document: where the path is created
		:param mutant: the mutation being revealed
		:param test: the test case executed or none
		"""
		document: SymDocument
		self.document = document
		self.mutant = mutant
		self.test = test
		self.nodes = list()
		return

	def get_document(self):
		"""
		:return: where the path is created
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation being revealed
		"""
		return self.mutant

	def has_test(self):
		"""
		:return: whether the path is generated from a test or static
		"""
		return not(self.test is None)

	def get_test(self):
		"""
		:return: the test case executed or none
		"""
		return self.test

	def get_nodes(self):
		"""
		:return: sequence of state node of symbolic condition instances required in the path
		"""
		return self.nodes


class SymDocument:
	"""
	The document to describe execution paths annotated with symbolic conditions and evaluation instances.
	"""

	def __init__(self, project: CProject):
		self.project = project		# It provides original entire dataset for mutation execution states.
		self.paths = list()			# The collection of symbolic execution paths performed, for testing.
		self.muta_paths = dict()	# Mapping from Mutant to the set of execution paths where it is used.
		self.test_paths = dict()	# Mapping from TestCase to set of execution paths where it is applied.
		self.conditions = dict()	# Mapping from string key to the unique instance of symbolic condition used.
		return

	def get_project(self):
		"""
		:return: It provides original entire dataset for mutation execution states.
		"""
		return self.project

	def get_paths(self):
		"""
		:return: The collection of symbolic execution paths performed, for testing.
		"""
		return self.paths

	def get_paths_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: the set of execution paths where the key is performed
		"""
		if key in self.muta_paths:
			paths = self.muta_paths[key]
		elif key in self.test_paths:
			paths = self.test_paths[key]
		else:
			paths = set()
		paths: set
		return paths

	def get_mutants(self):
		"""
		:return: The set of mutations used in testing
		"""
		return self.muta_paths.keys()

	def get_tests(self):
		"""
		:return: The set of test cases used in testing
		"""
		return self.test_paths.keys()

	def get_words(self):
		"""
		:return: the set of words encoding the conditions used
		"""
		return self.conditions.keys()

	def get_conditions(self):
		"""
		:return: the set of conditions used in mutation testing
		"""
		return self.conditions.values()

	def get_condition(self, word: str):
		return self.conditions[word]

	def __produce__(self, line: str):
		"""
		:param line: mid tid {[ condition+ ]}*
		:return: SymInstancePath or None
		"""
		if len(line.strip()) > 0:
			items = line.strip().split("\t")
			mid = int(items[0].strip())
			tid = int(items[1].strip())
			mutant = self.project.mutant_space.get_mutant(mid)
			if tid < 0:
				test = None
			else:
				test = self.project.test_space.get_test_case(tid)
			path = SymInstancePath(self, mutant, test)
			for k in range(2, len(items)):
				word = items[k].strip()
				if len(word) > 0:
					if word == "[":
						path.nodes.append(SymInstanceNode(path))
					elif word == "]":
						pass
					else:
						node = path.nodes[-1]
						node: SymInstanceNode
						node.__add_word__(word)
			return path
		else:
			return None

	def __consume__(self, path):
		"""
		:param path:
		:return: update the library using new path
		"""
		if not(path is None):
			path: SymInstancePath
			mutant = path.get_mutant()
			test = path.get_test()
			self.paths.append(path)
			if not(mutant in self.muta_paths):
				self.muta_paths[mutant] = set()
			self.muta_paths[mutant].add(path)
			if not(test is None):
				if not(test in self.test_paths):
					self.test_paths[test] = set()
				self.test_paths[test].add(path)
		return

	def __loading__(self, file_path: str):
		"""
		:param file_path:
		:return: load the execution paths recorded in mutation testing.
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				self.__consume__(self.__produce__(line))
		return


def print_sym_instance(instance: SymInstance):
	print("\t{}\t{}\t{}\t{}\t{}\t{}\t{}".format(instance.get_role(), instance.get_result(),
												instance.get_condition().get_category(),
												instance.get_condition().get_operator(),
												instance.get_condition().get_execution(),
												instance.get_condition().get_location().get_cir_code(),
												instance.get_condition().get_parameter()))
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for filename in os.listdir(root_path):
		directory_path = os.path.join(root_path, filename)
		c_project = CProject(directory_path, filename)
		document = c_project.load_documents(".sft")
		print(c_project.program.name, ": Load", len(document.get_mutants()), "mutants with", len(document.get_paths()),
			  "paths using", len(document.get_conditions()), "symbolic conditions required for them and against",
			  len(document.get_tests()), "tests.")
		for path in document.get_paths():
			path: SymInstancePath
			for node in path.get_nodes():
				node: SymInstanceNode
				for instance in node.get_instances():
					instance: SymInstance
					# print_sym_instance(instance)
		print()

