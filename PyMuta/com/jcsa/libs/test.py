"""
This file defines the data model of feature and coverage information.
"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta


class CDocument:
	"""
	It preserves the feature information for data mining directly from each project
	"""

	def __init__(self, directory: str, name: str):
		self.project = jcmuta.CProject(directory, name)
		cov_file_path = os.path.join(directory, name + ".cov")
		sip_file_path = os.path.join(directory, name + ".sip")
		self.coverage_matrix = CoverageMatrix(self, cov_file_path)
		self.conditions = SymConditions(self)
		self.paths = list()
		self.mutant_paths = dict()
		with open(sip_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					sym_instance_path = SymInstancePath(self, line)
					self.paths.append(sym_instance_path)
					if not (sym_instance_path.get_mutant() in self.mutant_paths):
						self.mutant_paths[sym_instance_path.get_mutant()] = list()
					self.mutant_paths[sym_instance_path.get_mutant()].append(sym_instance_path)
		return

	def get_project(self):
		return self.project

	def get_program(self):
		return self.project.program

	def get_coverage_matrix(self):
		return self.coverage_matrix

	def get_conditions_lib(self):
		return self.conditions

	def get_paths(self):
		"""
		:return: the collection of paths for killing a mutation using symbolic instances and conditions
		"""
		return self.paths

	def get_mutants(self):
		"""
		:return: the set of mutations being killed by any paths under the document
		"""
		return self.mutant_paths.keys()

	def get_paths_of(self, target_mutant: jcmuta.Mutant):
		"""
		:param target_mutant:
		:return: the set of symbolic sequence of execution nodes w.r.t. the mutant
		"""
		if target_mutant in self.mutant_paths:
			return self.mutant_paths[target_mutant]
		else:
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
	The symbolic condition defined in program context for killing a mutation.
	"""

	def __init__(self, category: str, operator: str, execution: jccode.CirExecution,
				 location: jccode.CirNode, parameter: jcbase.SymNode):
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
		"""
		:param document: the original data document as database
		"""
		self.document = document
		self.conditions = dict()
		return

	def get_all_words(self):
		"""
		:return: the set of words encoding all the symbolic conditions defined in library
		"""
		return self.conditions.keys()

	def get_all_conditions(self):
		"""
		:return: the set of all symbolic conditions encoded in the library
		"""
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
		cond_instance = self.conditions[word]
		cond_instance: SymCondition
		return cond_instance

	def get_conditions(self, words):
		"""
		:param words: the collection of words encoding the symbolic conditions being extracted
		:return: the collection of symbolic conditions being extracted from the input words
		"""
		conditions = list()
		for word in words:
			word: str
			conditions.append(self.get_condition(word.strip()))
		return conditions






if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	print_condition = False
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		c_directory = os.path.join(root_path, file_name)
		c_document = CDocument(c_directory, file_name)
		print(file_name, "loads", len(c_document.get_mutants()), "mutants used and",
			  len(c_document.get_paths()), "symbolic instance paths annotated with",
			  len(c_document.get_conditions_lib().get_all_conditions()), "conditions.")
		for instance_path in c_document.get_paths():
			mutant = instance_path.get_mutant()
			print("\tPath: Killing mutant#{} using {} instances and {} conditions.".
				  format(mutant.get_muta_id(), len(instance_path.get_instances()), len(instance_path.get_conditions())))
			for condition in instance_path.get_conditions():
				condition: SymCondition
				if print_condition:
					print("\t\t--> {}\t{}\t{}\t{}\t{}\t{}".format(mutant.get_muta_id(),
																  condition.get_category(),
																  condition.get_operator(),
																  condition.get_execution(),
																  condition.get_location().get_cir_code(),
																  condition.get_parameter()))
		print()

