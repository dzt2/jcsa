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

	def __load_documents__(self, post: str):
		"""
		:param post:
		:return: document w.r.t. given postfix
		"""
		document = SymInstanceDocument(self)
		directory = self.program.directory
		for file_name in os.listdir(directory):
			if file_name.endswith(post):
				file_path = os.path.join(directory, file_name)
				document.__loading__(file_path)
		return document

	def load_stat_documents(self):
		"""
		:return: xxx.sft
		"""
		return self.__load_documents__(".sft")

	def load_test_documents(self):
		"""
		:return: xxx.sft
		"""
		return self.__load_documents__(".dft")

	def load_exec_documents(self):
		"""
		:return: xxx.sft
		"""
		return self.__load_documents__(".dfp")


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


class SymInstance:
	"""
	It represents a condition that symbolically describes the objective need to be met during testing for
	killing a target mutation in analysis and modeled as following.
		(1) category: either satisfaction for constraint or observation for infected state.
		(2) operator: refined type of the condition for being evaluated such as "chg_bool".
		(3) execution: the statement point in control flow graph where the condition is defined.
		(4) location: the code location in C-intermediate representation that defines it.
		(5) parameter: symbolic expression to refine the description or none if it is not needed.
	"""

	def __init__(self, category: str, operator: str, execution: jcparse.CirExecution,
				 location: jcparse.CirNode, parameter: jcbase.SymNode):
		"""
		:param category: either satisfaction for constraint or observation for infected state.
		:param operator: refined type of the condition for being evaluated such as "chg_bool".
		:param execution: the statement point in control flow graph where the condition is defined.
		:param location: the code location in C-intermediate representation that defines it.
		:param parameter: symbolic expression to refine the description or none if it is not needed.
		"""
		self.category = category
		self.operator = operator
		self.location = location
		self.execution = execution
		self.parameter = parameter
		return

	def get_category(self):
		"""
		:return:	either "satisfaction" for constraint or "observation" for infected state.
		"""
		return self.category

	def get_operator(self):
		"""
		:return: 	the annotation name to refine type as [chg_numb, trap_stmt, ... mut_flow]
		"""
		return self.operator

	def get_execution(self):
		"""
		:return:	the statement node in control flow graph where the condition is evaluated
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the C-intermediate representation location where the condition is defined
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: the symbolic expression to refine its description or None if not needed
		"""
		return self.parameter

	def __str__(self):
		text = self.category + "$" + self.operator
		text = text + "$exe@{}${}".format(self.execution.get_function().get_name(), self.execution.get_exe_id())
		text = text + "$cir@{}".format(self.location.get_cir_id())
		if self.parameter is None:
			text = text + "$n@null"
		else:
			text = text + "$sym${}${}".format(self.parameter.get_class_name(), self.parameter.get_class_id())
		return text

	def encode(self):
		"""
		:return: category$operator$execution$location$parameter
		"""
		return str(self)

	@staticmethod
	def decode(project: CProject, text: str):
		"""
		:param project: It provides contextual information to decode the text into symbolic condition
		:param text: category$operator$execution$location$parameter
		:return: Symbolic Instance to define the condition being evaluated.
		"""
		if len(text.strip()) > 0:
			items = text.strip().split('$')
			category = items[0].strip()
			operator = items[1].strip()
			exec_tok = jcbase.CToken.parse(items[2].strip()).get_token_value()
			loct_tok = jcbase.CToken.parse(items[3].strip()).get_token_value()
			para_tok = jcbase.CToken.parse(items[4].strip()).get_token_value()
			execution = project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
			location = project.program.cir_tree.get_cir_node(loct_tok)
			if para_tok is None:
				parameter = None
			else:
				parameter = project.sym_tree.get_sym_node(items[4].strip())
			return SymInstance(category, operator, execution, location, parameter)
		return None


class SymInstanceNode:
	"""
	It describes the state of symbolic condition being evaluated at some execution point, denoted as:
		(1)	instance: 		the source instance being evaluated as source input during testing.
		(2)	annotations:	the set of annotations that describe the features of the source.
		(3) inferences:		the set of instances inferred from the source instance for testing.
		(4) evaluation:		True	--- if the instance is satisfied during testing.
							False	--- if the instance is rejected during testing.
							None	--- the satisfaction of the instance is unknown.
	"""

	def __init__(self, path, instance: SymInstance, annotations, inferences, evaluation: bool):
		"""
		:param instance:	the source instance being evaluated as source input during testing.
		:param annotations:	the set of annotations that describe the features of the source.
		:param inferences:	the set of instances inferred from the source instance for testing.
		:param evaluation:	True	--- if the instance is satisfied during testing.
							False	--- if the instance is rejected during testing.
							None	--- the satisfaction of the instance is unknown.
		"""
		path: SymInstancePath
		self.path = path
		self.instance = instance
		self.annotations = list()
		self.inferences = list()
		for annotation in annotations:
			annotation: SymInstance
			self.annotations.append(annotation)
		for inference in inferences:
			inference: SymInstance
			self.inferences.append(inference)
		self.evaluation = evaluation
		return

	def get_instance(self):
		"""
		:return: the source instance being evaluated as source input during testing.
		"""
		return self.instance

	def get_annotations(self):
		"""
		:return: the set of annotations that describe the features of the source.
		"""
		return self.annotations

	def get_inferences(self):
		"""
		:return: the set of instances inferred from the source instance for testing.
		"""
		return self.inferences

	def get_evaluation(self):
		"""
		:return: 	True	--- if the instance is satisfied during testing.
					False	--- if the instance is rejected during testing.
					None	--- the satisfaction of the instance is unknown.
		"""
		return self.evaluation


class SymInstancePath:
	"""
	The path of execution state of symbolic instance of conditions required for killing each mutant.
		(1) mutant:	the mutation as target for being revealed during testing
		(2) test:	the test case used to execute against the mutation as target
		(3) states:	the sequence of symbolic instance states for being evaluation.
	"""

	def __init__(self, document, mutant: Mutant, test: TestCase):
		"""
		:param mutant: the mutation as target for being revealed during testing
		:param test: the test case used to execute against the mutation as target
		"""
		document: SymInstanceDocument
		self.document = document
		self.mutant = mutant
		self.test = test
		self.nodes = list()
		return

	def get_document(self):
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation as target for being revealed during testing
		"""
		return self.mutant

	def get_test(self):
		"""
		:return: the test case used to execute against the mutation as target or None if it is generated from static
		"""
		return self.test

	def get_nodes(self):
		"""
		:return: the sequence of symbolic instance states for being evaluation.
		"""
		return self.nodes


class SymInstanceDocument:
	"""
	It preserves the set of execution paths for killing each mutant against each test
	"""

	def __init__(self, project: CProject):
		self.project = project		# It provides original entire data set
		self.paths = list()			# The set of state paths used in testing
		self.instances = dict()		# Mapping from unique key to symbolic instance as condition
		self.muta_paths = dict()	# Mapping from mutant to the paths where it is performed
		self.test_paths = dict()	# Mapping from test to the paths where it is executed on
		return

	def get_project(self):
		return self.project

	def get_paths(self):
		return self.paths

	def get_instances(self):
		"""
		:return: set of unique and non-duplicated instances
		"""
		return self.instances.values()

	def get_words(self):
		"""
		:return: set of words encoding the unique instance in testing
		"""
		return self.instances.keys()

	def get_mutants(self):
		return self.muta_paths.keys()

	def get_tests(self):
		return self.test_paths.keys()

	def get_paths_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: paths in which the key is performed
		"""
		if key in self.test_paths:
			paths = self.test_paths[key]
		elif key in self.muta_paths:
			paths = self.muta_paths[key]
		else:
			paths = set()
		paths: set
		return paths

	def __wording__(self, word: str):
		"""
		:param word:
		:return: role, result, instance
		"""
		role, result, instance = None, None, None
		if len(word.strip()) > 0:
			items = word.strip().split('$')
			role = items[0].strip()
			result = jcbase.CToken.parse(items[1].strip()).get_token_value()
			word = "{}${}${}${}${}".format(items[2].strip(),
										   items[3].strip(),
										   items[4].strip(),
										   items[5].strip(),
										   items[6].strip())
			if not(word in self.instances):
				self.instances[word] = SymInstance.decode(self.project, word)
			instance = self.instances[word]
		return role, result, instance

	def __produce__(self, line: str):
		"""
		:param line: mid tid {word+ ;}*
		:return: SymInstancePath or None
		"""
		mutant, test, instances = None, None, dict()
		instances["instance"] = None
		instances["annotate"] = set()
		instances["inferred"] = set()
		instances["result"] = None
		if len(line.strip()) > 0:
			items = line.strip().split('\t')
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
				if len(word) <= 0:
					continue
				elif word == ";":
					orig_instance = instances["instance"]
					annotations = instances["annotate"]
					inferences = instances["inferred"]
					evaluation = instances["result"]
					path.nodes.append(SymInstanceNode(path, orig_instance, annotations, inferences, evaluation))
					instances["instance"] = None
					instances["annotate"].clear()
					instances["inferred"].clear()
					instances["result"] = None
				else:
					role, result, instance = self.__wording__(word)
					if role == "instance":
						instances["instance"] = instance
						instances["result"] = result
					elif role == "annotate":
						instances["annotate"].add(instance)
					elif role == "inferred":
						instances["inferred"].add(instance)
			return path
		return None

	def __consume__(self, path):
		"""
		:param path:
		:return:
		"""
		if not(path is None):
			path: SymInstancePath
			mutant = path.get_mutant()
			test = path.get_test()
			if not(mutant in self.muta_paths):
				self.muta_paths[mutant] = set()
			self.muta_paths[mutant].add(path)
			if not(test is None) :
				if not(test in self.test_paths):
					self.test_paths[test] = set()
				self.test_paths[test].add(path)
			self.paths.append(path)
		return

	def __loading__(self, file_path: str):
		with open(file_path, 'r') as reader:
			for line in reader:
				self.__consume__(self.__produce__(line.strip()))
		return


def print_sym_instance(result, instance: SymInstance):
	print("\t{}\t{}\t{}\t{}\t\"{}\"\t{}".format(result, instance.get_category(), instance.get_operator(),
												instance.get_execution(), instance.get_location().get_cir_code(),
												instance.get_parameter()))
	return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for filename in os.listdir(root_path):
		directory_path = os.path.join(root_path, filename)
		c_project = CProject(directory_path, filename)
		document = c_project.load_stat_documents()
		print(c_project.program.name, ": Load", len(document.get_mutants()), "mutants with", len(document.get_paths()),
			  "paths using", len(document.get_instances()), "conditions required on",
			  len(document.get_tests()), "tests.")
		for path in document.get_paths():
			path: SymInstancePath
			for node in path.get_nodes():
				node: SymInstanceNode
				result = node.get_evaluation()
				# print_sym_instance(result, node.get_instance())
				# for annotation in node.get_annotations():
				#	print_sym_instance(result, annotation)
				# for inference in node.get_inferences():
				#	print_sym_instance(result, inference)
		print()

