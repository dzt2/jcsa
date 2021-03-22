"""
This file develops the data model for describing the execution information during test execution, including:
	---	xxx.cov: the coverage matrix which records of which statement is covered by which test case.
	---	xxx.sit: the symbolic instance paths annotated with symbolic conditions for analysis and mining.
"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta


class CDocument:
	"""
	It preserves the feature information for data mining directly from each project
	"""

	def __init__(self, directory: str, file_name: str):
		self.project = jcmuta.CProject(directory, file_name)
		cov_file_path = os.path.join(directory, file_name + ".cov")
		sit_file_path = os.path.join(directory, file_name + ".sit")
		self.cmatrix = CoverageMatrix(self, cov_file_path)
		self.conditions = SymConditions(self)
		self.executions = list()
		self.muta_procs = dict()
		with open(sit_file_path, 'r') as reader:
			for line in reader:
				process = SymExecution.__parse__(self, line)
				if not(process is None):
					process: SymExecution
					self.executions.append(process)
					mutant = process.get_mutant()
					if not(mutant in self.muta_procs):
						self.muta_procs[mutant] = list()
					self.muta_procs[mutant].append(process)
		return

	def get_coverage_matrix(self):
		return self.cmatrix

	def get_condition_libs(self):
		return self.conditions

	def get_mutants(self):
		return self.muta_procs.keys()

	def get_processes(self):
		return self.executions

	def get_processes_of(self, mutant: jcmuta.Mutant):
		if mutant in self.muta_procs:
			return self.muta_procs[mutant]
		return list()

class CoverageMatrix:
	"""
	It describes the matrix with coverage information, in which each line refers to the coverage vector
	of each execution point in control flow graph, while the column refers to the statement vector that
	a given test case covers during testing.
	"""

	def __init__(self, document: CDocument, cov_file: str):
		self.matrix = dict()
		self.document = document
		program = document.project.program
		with open(cov_file, 'r') as reader:
			for line in reader:
				if len(line.strip()) > 0:
					items = line.strip().split('\t')
					exec_tok = jcbase.CToken.parse(items[0].strip()).get_token_value()
					execution = program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
					coverage = items[1].strip()
					self.matrix[execution] = coverage
		return

	def get_document(self):
		return self.document

	def is_covered_by(self, execution: jccode.CirExecution, test):
		"""
		:param execution:
		:param test: TestCase or int
		:return: whether the execution point is covered by specified test
		"""
		if isinstance(test, jcmuta.TestCase):
			test_id = test.get_test_id()
		else:
			test: int
			test_id = test
		if execution in self.matrix:
			coverage = self.matrix[execution]
			if test_id < 0 or test_id >= len(coverage):
				return False
			else:
				return coverage[test_id] == '1'
		else:
			return False

	def is_covered_in(self, execution: jccode.CirExecution, tests=None):
		"""
		:param execution:
		:param tests: the selected tests under which the coverage is evaluated or None to represent all.
		:return: Whether the execution point is covered by any test in set
		"""
		if execution in self.matrix:
			coverage = self.matrix[execution]
			if tests is None:
				return '1' in coverage
			else:
				for test in tests:
					if self.is_covered_by(execution, test):
						return True
				return False
		return False

	def get_line(self, execution: jccode.CirExecution):
		"""
		:param execution:
		:return: the set of TestCase (ID) covering the target execution point
		"""
		covering_set = list()
		if execution in self.matrix:
			coverage = self.matrix[execution]
			for test_id in range(0, len(coverage)):
				if coverage[test_id] == '1':
					covering_set.append(test_id)
		return covering_set

	def get_column(self, test):
		"""
		:param test: TestCase or int
		:return: the set of executions covered by the test
		"""
		column = list()
		for execution in self.matrix.keys():
			if self.is_covered_by(execution, test):
					column.append(execution)
		return column


class SymCondition:
	"""
	It describes a symbolic condition annotated at some program point for killing a target mutation.
		---	category: 	either "satisfaction" for constraint or "observations" for state error.
		---	operator: 	the type of the symbolic condition being analyzed.
		---	execution:	the execution point in control flow graph where the condition is checked.
		---	location:	the C-intermediate code location where the condition is defined upon.
		---	parameter:	the symbolic expression to refine the condition or None if not needed.
	"""

	def __init__(self, category: str, operator: str, execution: jccode.CirExecution, location: jccode.CirNode,
				 parameter: jcbase.SymNode):
		"""
		:param category:	either "satisfaction" for constraint or "observations" for state error.
		:param operator:	the type of the symbolic condition being analyzed.
		:param execution:	the execution point in control flow graph where the condition is checked.
		:param location:	the C-intermediate code location where the condition is defined upon.
		:param parameter:	the symbolic expression to refine the condition or None if not needed.
		"""
		self.category = category
		self.operator = operator
		self.execution = execution
		self.location = location
		self.parameter = parameter
		return

	def get_category(self):
		"""
		:return: either "satisfaction" for constraint or "observations" for state error.
		"""
		return self.category

	def get_operator(self):
		"""
		:return: the type of the symbolic condition being analyzed.
		"""
		return self.operator

	def get_execution(self):
		"""
		:return: the execution point in control flow graph where the condition is checked.
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the C-intermediate code location where the condition is defined upon.
		"""
		return self.location

	def has_parameter(self):
		"""
		:return: False if no parameter is needed to refine the description.
		"""
		return not(self.parameter is None)

	def get_parameter(self):
		"""
		:return: the symbolic expression to refine the condition or None if not needed.
		"""
		return self.parameter

	def __str__(self):
		category = self.category
		operator = self.operator
		execution = "exe@{}@{}".format(self.execution.get_function().get_name(), self.execution.get_exe_id())
		location = "cir@{}".format(self.location.get_cir_id())
		parameter = "n@null"
		if self.has_parameter():
			parameter = "sym@{}@{}".format(self.parameter.get_class_name(), self.parameter.get_class_id())
		return "{}${}${}${}${}".format(category, operator, execution, location, parameter)


class SymConditions:
	"""
	The library of symbolic conditions applied to describe symbolic execution for killing mutation.
	"""

	def __init__(self, document: CDocument):
		self.document = document
		self.conditions = dict()
		return

	def get_words(self):
		return self.conditions.keys()

	def get_conditions(self):
		return self.conditions.values()

	def get_condition(self, word: str):
		"""
		:param word: category$operator$execution$location$parameter
		:return:
		"""
		if not(word in self.conditions):
			items = word.strip().split('$')
			category = items[0].strip()
			operator = items[1].strip()
			exec_tok = jcbase.CToken.parse(items[2].strip()).get_token_value()
			loct_tok = jcbase.CToken.parse(items[3].strip()).get_token_value()
			execution= self.document.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
			location = self.document.project.program.cir_tree.get_cir_node(loct_tok)
			para_tok = jcbase.CToken.parse(items[4].strip()).get_token_value()
			if para_tok is None:
				parameter = None
			else:
				parameter = self.document.project.sym_tree.get_sym_node(items[4].strip())
			self.conditions[word] = SymCondition(category, operator, execution, location, parameter)
		condition = self.conditions[word]
		condition: SymCondition
		return condition


class SymInstance:
	"""
	It represents an execution node on the sequence for killing a mutant, which is annotated with a
	collection of symbolic conditions required.
	"""

	def __init__(self, process, stage: bool, execution: jccode.CirExecution, result: bool):
		"""
		:param stage: True before the mutation point is reached or otherwise False
		:param execution: the execution point where the instance is checked upon.
		:param result: 		True if the instance passes through the check-point
							False if the instance fails to pass through checking
							None if the satisfaction of the instance is unknown.
		"""
		process: SymExecution
		self.process = process
		self.stage = stage
		self.execution = execution
		self.result = result
		self.words = list()
		return

	def get_process(self):
		"""
		:return: the sequence of execution points annoted with conditions for killing mutant
		"""
		return self.process

	def get_stage(self):
		"""
		:return: True before the mutation point is reached or otherwise False
		"""
		return self.stage

	def get_execution(self):
		"""
		:return: the execution point where the instance is checked upon.
		"""
		return self.execution

	def get_result(self):
		"""
		:return: 	True if the instance passes through the check-point
					False if the instance fails to pass through checking
					None if the satisfaction of the instance is unknown.
		"""
		return self.result

	def get_words(self):
		"""
		:return: the set of words encoding the symbolic conditions required for being met.
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: the set of the symbolic conditions required for being met.
		"""
		conditions = list()
		document = self.process.document
		document: CDocument
		for word in self.words:
			conditions.append(document.conditions.get_condition(word))
		return conditions


class SymExecution:
	"""
	It describes an execution sequence of which nodes annotated with a sequence of
	"""

	def __init__(self, document: CDocument, mutant: jcmuta.Mutant):
		self.document = document
		self.mutant = mutant
		self.instances = list()
		return

	def get_document(self):
		return self.document

	def get_mutant(self):
		return self.mutant

	def get_instances(self):
		"""
		:return: the sequence of execution nodes annotated with conditions required for killing mutant
		"""
		return self.instances

	@staticmethod
	def __parse__(document: CDocument, line: str):
		if len(line.strip()) > 0:
			items = line.strip().split('\t')
			mid = int(items[0].strip())
			mutant = document.project.muta_space.get_mutant(mid)
			execution = SymExecution(document, mutant)
			words = list()
			for i in range(1, len(items)):
				word = items[i].strip()
				if len(word) > 0:
					if word == ';':
						keys = words[0].strip().split('$')
						stage = jcbase.CToken.parse(keys[0].strip()).get_token_value()
						result = jcbase.CToken.parse(keys[2].strip()).get_token_value()
						exec_tok = jcbase.CToken.parse(keys[1].strip()).get_token_value()
						point = document.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
						instance = SymInstance(execution, stage, point, result)
						for j in range(1, len(words)):
							document.conditions.get_condition(words[j].strip())
							instance.words.append(words[j].strip())
						execution.instances.append(instance)
						words.clear()
					else:
						words.append(word)
			return execution
		return None


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		directory = os.path.join(root_path, file_name)
		c_document = CDocument(directory, file_name)
		print(file_name, "loads", len(c_document.get_mutants()), "mutants used and",
			  len(c_document.get_processes()), "symbolic procedures annotated with",
			  len(c_document.get_condition_libs().get_conditions()), "conditions.")
		for process in c_document.get_processes():
			mutant = process.get_mutant()
			for instance in process.get_instances():
				instance: SymInstance
				for condition in instance.get_conditions():
					print("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}".format(mutant.get_muta_id(),
																		instance.get_stage(),
																		instance.get_result(),
																		instance.get_execution(),
																		condition.get_category(),
																		condition.get_operator(),
																		condition.get_execution(),
																		condition.get_location().get_cir_code(),
																		condition.get_parameter()))
		print()

