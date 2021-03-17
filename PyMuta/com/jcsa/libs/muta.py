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
		if tests is None:
			return self.is_killable()
		else:
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
	It describes the symbolic condition evaluated at some point of program, such that for killing the mutation,
	the condition specified needs to be met as a premise, which is modeled as following.
		---	category: 	either "satisfaction" for constraint or "observation" for state error
		---	operator: 	the refined type to define the condition under test, e.g. "neg_value"
		---	execution:	the statement node in the control flow graph where the condition is evaluated.
		---	location:	the code location in C-intermediate representation where it is defined.
		---	parameter:	the symbolic expression to refine the description or None if it is not needed.
	"""

	def __init__(self, category: str, operator: str, execution: jcparse.CirExecution,
				 location: jcparse.CirNode, parameter: jcbase.SymNode):
		"""
		:param category:	either "satisfaction" for constraint or "observation" for state error
		:param operator:	the refined type to define the condition under test, e.g. "neg_value"
		:param execution:	the statement node in the control flow graph where the condition is evaluated.
		:param location:	the code location in C-intermediate representation where it is defined.
		:param parameter:	the symbolic expression to refine the description or None if it is not needed.
		"""
		self.category 	= category
		self.operator	= operator
		self.execution	= execution
		self.location	= location
		self.parameter 	= parameter
		return

	def get_category(self):
		"""
		:return: either "satisfaction" for constraint or "observation" for state error
		"""
		return self.category

	def get_operator(self):
		"""
		:return: the refined type to define the condition under test, e.g. "neg_value"
		"""
		return self.operator

	def get_execution(self):
		"""
		:return: the statement node in the control flow graph where the condition is evaluated.
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the code location in C-intermediate representation where it is defined.
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: the symbolic expression to refine the description or None if it is not needed.
		"""
		return self.parameter

	def has_parameter(self):
		return not(self.parameter is None)

	def __str__(self):
		category = self.category
		operator = self.operator
		execution = "exe@{}@{}".format(self.execution.get_function().get_name(), self.execution.get_exe_id())
		location = "cir@{}".format(self.location.get_cir_id())
		parameter = "n@null"
		if self.has_parameter():
			parameter = "sym@{}@{}".format(self.parameter.get_class_name(), self.parameter.get_class_id())
		return "{}${}${}${}${}".format(category, operator, execution, location, parameter)

	@staticmethod
	def decode(project: CProject, word: str):
		"""
		:param project: It is used to decode the word into symbolic condition.
		:param word: category@operator@execution@location@parameter
		:return: symbolic condition w.r.t. the given word
		"""
		if len(word.strip()) > 0:
			items = word.strip().split('$')
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
			return SymCondition(category, operator, execution, location, parameter)
		return None


class SymInstance:
	"""
	The instance of a symbolic condition with an evaluation result during testing such that:
		True	---	The condition is actually satisfied during testing
		False	---	The condition remains not satisfied during testing
		None	---	The satisfaction of the condition remains unknown.
	"""

	def __init__(self, stage: bool, result: bool, condition: SymCondition):
		"""
		:param stage:		the time when the condition is evaluated during testing
		:param condition:	the condition as source to be evaluated and instanciate
		:param result:		the boolean result that specifies whether the condition is satisfied
							(True) or not (False) or unknown (None).
		"""
		self.stage = stage
		self.condition = condition
		self.result = result
		return

	def get_stage(self):
		"""
		:return: True as before mutation and False after mutation
		"""
		return self.stage

	def get_result(self):
		"""
		:return:	True	---	The condition is actually satisfied during testing
					False	---	The condition remains not satisfied during testing
					None	---	The satisfaction of the condition remains unknown.
		"""
		return self.result

	def get_condition(self):
		"""
		:return: the condition defined in code location being evaluated in the instance
		"""
		return self.condition

	def get_execution(self):
		"""
		:return: the statement node where the condition is evaluated
		"""
		return self.condition.get_execution()


class SymExecution:
	"""
	It represents the execution between a mutation and a test case annotated with a sequence of instances
	of symbolic conditions required for the mutation being killed on the given path.
	"""

	def __init__(self, document, mutant: Mutant, test: TestCase):
		"""
		:param document: where the execution line is created and preserved.
		:param mutant: the mutation as objective for being revealed
		:param test: the test case used in execution or None if the execution is generated abstractly
		"""
		self.document = document
		self.mutant = mutant
		self.test = test
		self.instances = list()
		return

	def get_document(self):
		"""
		:return: where the execution line is created and preserved.
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation as objective for being revealed
		"""
		return self.mutant

	def get_test(self):
		"""
		:return: the test case used in execution or None if the execution is generated abstractly
		"""
		return self.test

	def has_test(self):
		"""
		:return: False if the execution is generated abstractly
		"""
		return not(self.test is None)

	def get_instances(self, stage=None):
		"""
		:param stage: True to select instances before mutation, or False to select
						instances after mutation, or None to select all of them.
		:return: the set of instances of conditions required on the execution path
		"""
		if stage is None:
			return self.instances
		elif stage:
			instances = list()
			for instance in self.instances:
				instance: SymInstance
				if instance.get_stage():
					instances.append(instance)
			return instances
		else:
			instances = list()
			for instance in self.instances:
				instance: SymInstance
				if not instance.get_stage():
					instances.append(instance)
			return instances


class SymDocument:
	"""
	The document preserves all the executions between each mutant and test case involved.
	"""

	def __init__(self, project: CProject):
		self.project = project
		self.executions = list()
		self.muta_execs = dict()
		self.test_execs = dict()
		self.conditions = dict()
		return

	def get_project(self):
		"""
		:return: It provides original data for analysis and representation
		"""
		return self.project

	def get_executions(self):
		"""
		:return: the set of executions between mutants and tests annotated with conditions
		"""
		return self.executions

	def get_executions_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return: set of executions where the key is used
		"""
		if key in self.muta_execs:
			executions = self.muta_execs[key]
		elif key in self.test_execs:
			executions = self.test_execs[key]
		else:
			executions = set()
		executions: set
		return executions

	def get_mutants(self):
		return self.muta_execs.keys()

	def get_tests(self):
		return self.test_execs.keys()

	def get_words(self):
		"""
		:return: set of words encoding the symbolic conditions defined in mutation testing
		"""
		return self.conditions.keys()

	def get_conditions(self):
		"""
		:return: set of the symbolic conditions defined in mutation testing
		"""
		return self.conditions.values()

	def __condition__(self, word: str):
		"""
		:param word:
		:return: create a condition w.r.t. the word uniquely
		"""
		if not(word in self.conditions):
			self.conditions[word] = SymCondition.decode(self.project, word)
		condition = self.conditions[word]
		condition: SymCondition
		return condition

	def __produce__(self, line: str):
		"""
		:param line: mid tid {result condition+ ;}
		:return:
		"""
		if len(line.strip()) > 0:
			items = line.strip().split('\t')
			mid = int(items[0].strip())
			tid = int(items[1].strip())
			mutant = self.project.mutant_space.get_mutant(mid)
			if tid < 0:
				test = None
			else:
				test = self.project.test_space.get_test_case(tid)
			execution = SymExecution(self, mutant, test)
			words = list()
			for k in range(2, len(items)):
				word = items[k].strip()
				if len(word) > 0:
					if word == ";":
						stage = jcbase.CToken.parse(words[0].strip()).get_token_value()
						result = jcbase.CToken.parse(words[1].strip()).get_token_value()
						for i in range(2, len(words)):
							condition = self.__condition__(words[i])
							execution.instances.append(SymInstance(stage, result, condition))
						words.clear()
					else:
						words.append(word)
			return execution
		return None

	def __consume__(self, execution):
		if not(execution is None):
			execution: SymExecution
			self.executions.append(execution)
			mutant = execution.get_mutant()
			test = execution.get_test()
			if not(mutant in self.muta_execs):
				self.muta_execs[mutant] = set()
			self.muta_execs[mutant].add(execution)
			if execution.has_test():
				if not(test in self.test_execs):
					self.test_execs[test] = set()
				self.test_execs[test].add(execution)
		return

	def __loading__(self, file_path: str):
		with open(file_path, 'r') as reader:
			for line in reader:
				self.__consume__(self.__produce__(line.strip()))
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for filename in os.listdir(root_path):
		directory_path = os.path.join(root_path, filename)
		c_project = CProject(directory_path, filename)
		document = c_project.load_documents(".sft")
		print(c_project.program.name, ": Load", len(document.get_mutants()), "mutants,", len(document.get_executions()),
			  "executions using", len(document.get_conditions()), "symbolic conditions required for them, and against",
			  len(document.get_tests()), "tests.")
		for execution in document.get_executions():
			execution: SymExecution
			for instance in execution.get_instances():
				instance: SymInstance
				condition = instance.get_condition()
				print("\t{}\t{}\t{}\t{}\t{}\t{}\t{}".format(instance.get_stage(),
															instance.get_result(),
															condition.get_category(),
															condition.get_operator(),
															condition.get_execution(),
															condition.get_location().get_cir_code(),
															condition.get_parameter()))
		print()

