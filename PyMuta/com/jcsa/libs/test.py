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
		self.sequences = list()
		self.muta_seqs = dict()
		self.__loading__(sit_file_path)
		return

	def get_project(self):
		return self.project

	def get_program(self):
		return self.project.program

	def get_coverage_matrix(self):
		return self.cmatrix

	def get_conditions_lib(self):
		return self.conditions

	def get_sequences(self):
		"""
		:return: the collection of sequence of execution nodes annotated with symbolic conditions
		"""
		return self.sequences

	def get_mutants(self):
		return self.muta_seqs.keys()

	def get_sequences_of(self, mutant: jcmuta.Mutant):
		"""
		:param mutant:
		:return: the set of symbolic sequence of execution nodes w.r.t. the mutant
		"""
		if mutant in self.muta_seqs:
			return self.muta_seqs[mutant]
		return set()

	def __wording__(self, word: str):
		"""
		:param word:
		:return: generate the condition w.r.t. the word
		"""
		word = word.strip()
		if len(word) > 0:
			self.conditions.get_condition(word)
		return word

	def __produce__(self, sequence, head: str, words):
		"""
		:param sequence:
		:param words: {stage$exec$result {condition_word}+ ;}
		:return: SymExecution
		"""
		items = head.strip().split('$')
		stage = jcbase.CToken.parse(items[0].strip()).get_token_value()
		exec_tok = jcbase.CToken.parse(items[1].strip()).get_token_value()
		result = jcbase.CToken.parse(items[2].strip()).get_token_value()
		execution = self.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
		condition_words = list()
		for word in words:
			word = self.__wording__(word)
			if len(word) > 0:
				condition_words.append(word)
		return SymExecution(sequence, stage, execution, result, condition_words)

	def __consume__(self, line: str):
		"""
		:param line: mid {head condition+ ;}+
		:return:
		"""
		line = line.strip()
		if len(line) > 0:
			items = line.split('\t')
			mutant = self.project.muta_space.get_mutant(int(items[0].strip()))
			sequence = SymSequence(self, mutant)
			exec_list = list()
			for i in range(1, len(items)):
				item = items[i].strip()
				if len(item) > 0:
					if item != ';':
						exec_list.append(item)
					else:
						head = exec_list[0]
						words = exec_list[1:]
						execution = self.__produce__(sequence, head, words)
						for word in execution.get_words():
							if not(word in sequence.get_words()):
								sequence.words.append(word)
						sequence.executions.append(execution)
						exec_list.clear()
			return sequence
		return None

	def __loading__(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				sequence = self.__consume__(line.strip())
				if not(sequence is None):
					sequence: SymSequence
					self.sequences.append(sequence)
					mutant = sequence.get_mutant()
					if not(mutant in self.muta_seqs):
						self.muta_seqs[mutant] = set()
					self.muta_seqs[mutant].add(sequence)
		return


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
		condition = self.conditions[word]
		condition: SymCondition
		return condition

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


class SymExecution:
	"""
	It represents an execution point annotated with a series of symbolic conditions being met in an execution sequence.
	"""

	def __init__(self, sequence, stage: bool, execution: jccode.CirExecution, result: bool, words):
		"""
		:param sequence: 	the sequence of symbolic executions where the node is preserved
		:param stage: 		True  ---	if the execution is performed before reaching the mutated point
							False ---	if the execution is performed after the mutated point is reached
		:param execution:	the execution point in control flow graph in form of C-intermediate representation
		:param result:		True  ---	if the conditions among the node must be satisfied by any tests
							False ---	if the conditions among the node be non-satisfiable by any test
							None  ---	if the satisfaction of the conditions among depends on the test
		:param words:		the collection of words encoding the symbolic conditions annotated with this node
		"""
		sequence: SymSequence
		self.sequence = sequence
		self.stage = stage
		self.execution = execution
		self.result = result
		self.words = list()
		for word in words:
			word: str
			self.words.append(word.strip())
		return

	def get_sequence(self):
		"""
		:return: the sequence of symbolic executions where the node is preserved
		"""
		return self.sequence

	def get_stage(self):
		"""
		:return: 	True  ---	if the execution is performed before reaching the mutated point
					False ---	if the execution is performed after the mutated point is reached
		"""
		return self.stage

	def get_execution(self):
		"""
		:return: 	the execution point in control flow graph in form of C-intermediate representation
		"""
		return self.execution

	def get_result(self):
		"""
		:return:	True  ---	if the conditions among the node must be satisfied by any tests
					False ---	if the conditions among the node be non-satisfiable by any test
					None  ---	if the satisfaction of the conditions among depends on the test
		"""
		return self.result

	def get_words(self):
		"""
		:return: the collection of words encoding the symbolic conditions annotated with this node
		"""
		return self.words

	def get_conditions(self):
		"""
		:return: the collection of the symbolic conditions annotated with this node
		"""
		document = self.sequence.document
		return document.get_conditions_lib().get_conditions(self.words)


class SymSequence:
	"""
	The sequence of execution nodes annotated with symbolic conditions required for killing a specific mutation
	"""

	def __init__(self, document: CDocument, mutant: jcmuta.Mutant):
		"""
		:param document: the document that provides entire database
		:param mutant: the mutation as target for being detected by tests
		"""
		self.document = document
		self.mutant = mutant
		self.executions = list()
		self.words = list()
		return

	def get_document(self):
		"""
		:return: the document that provides entire database
		"""
		return self.document

	def get_mutant(self):
		"""
		:return: the mutation as target for being detected by tests
		"""
		return self.mutant

	def get_executions(self):
		"""
		:return: the set of execution nodes annotated with symbolic conditions in the program
		"""
		return self.executions

	def get_execution(self, k: int):
		"""
		:param k:
		:return: the kth execution node annotated with symbolic conditions in the sequence
		"""
		execution = self.executions[k]
		execution: SymExecution
		return execution

	def get_words(self):
		"""
		:return: the set of words encoding the symbolic conditions among the execution nodes in the sequence
		"""
		return self.words

	def get_conditions(self):
		return self.document.get_conditions_lib().get_conditions(self.words)


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		directory = os.path.join(root_path, file_name)
		c_document = CDocument(directory, file_name)
		print(file_name, "loads", len(c_document.get_mutants()), "mutants used and",
			  len(c_document.get_sequences()), "symbolic sequences annotated with",
			  len(c_document.get_conditions_lib().get_all_conditions()), "conditions.")
		for sequence in c_document.get_sequences():
			sequence: SymSequence
			mutant = sequence.get_mutant()
			for condition in sequence.get_conditions():
				print("\t{}\t{}\t{}\t{}\t{}\t{}".format(mutant.get_muta_id(),
														condition.get_category(),
														condition.get_operator(),
														condition.get_execution(),
														condition.get_location().get_cir_code(),
														condition.get_parameter()))
		print()

