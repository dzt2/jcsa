"""
This file implements data model to represent testing state of results, including:
	xxx.cov | xxx.dov
	xxx.sym | xxx.dym
	xxx.sft | xxx.dft
	xxx.sfp | xxx.dfp
"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta


class CDocument:
	"""
	It preserves all the test results and analysis results.
	"""
	def __init__(self, project: jcmuta.CProject,
				 cov_file_path: str,
				 sym_file_path: str,
				 sym_tree_path: str,
				 tests):
		self.project = project
		self.coverage = CoverageMatrix(self, cov_file_path)
		self.sym_tree = jcbase.SymTree(sym_file_path)
		self.conditions = SymConditions(self)
		self.procedures = list()
		self.muta_procs = dict()
		self.tests = tests
		with open(sym_tree_path, 'r') as reader:
			for line in reader:
				self.__load__(line.strip())
		return

	def get_project(self):
		return self.project

	def get_coverage(self):
		return self.coverage

	def get_sym_tree(self):
		return self.sym_tree

	def get_conditions(self):
		return self.conditions

	def get_procedures(self):
		return self.procedures

	def get_mutants(self):
		return self.muta_procs.keys()

	def get_tests(self):
		return self.tests

	def get_procedures_of(self, mutant: jcmuta.Mutant):
		if mutant in self.muta_procs:
			procedures = self.muta_procs[mutant]
		else:
			procedures = set()
		procedures: set
		return procedures

	def __load__(self, line: str):
		if len(line.strip()) > 0:
			items = line.strip().split('\t')
			# generate root procedure
			mutant = self.project.mutant_space.get_mutant(int(items[0].strip()))
			procedure = SymProcedure(self, mutant)
			if not(mutant in self.muta_procs):
				self.muta_procs[mutant] = list()
			self.muta_procs[mutant].append(procedure)
			self.procedures.append(procedure)
			# generate for each instance in buffer
			words = list()
			for k in range(1, len(items)):
				word = items[k].strip()
				if len(word) > 0:
					if word == ';':
						keys = words[0].split('$')
						exec_tok = jcbase.CToken.parse(keys[0].strip()).get_token_value()
						stage = jcbase.CToken.parse(keys[1].strip()).get_token_value()
						result = jcbase.CToken.parse(keys[2].strip()).get_token_value()
						execution = self.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
						instance = SymInstance(procedure, stage, execution, result)
						procedure.instances.append(instance)
						for i in range(1, len(words)):
							cword = words[i].strip()
							condition = self.conditions.get_condition(cword)
							instance.conditions.append(condition)
						words.clear()
					else:
						words.append(word)
		return None



class CoverageMatrix:
	"""
	It preserves the mapping from CirExecution to bitstring coverage state.
	"""

	def __init__(self, document: CDocument, file_path: str):
		"""
		:param document:
		:param file_path:
		"""
		self.document = document
		self.matrix = dict()
		program = self.document.project.program
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					exec_token = jcbase.CToken.parse(items[0].strip()).get_token_value()
					execution = program.function_call_graph.get_execution(exec_token[0], exec_token[1])
					result = items[1].strip()
					self.matrix[execution] = result
		return

	def get_document(self):
		"""
		:return: the document where the coverage matrix is loaded
		"""
		return self.document

	def get_executions(self):
		"""
		:return: the set of executions of which coverage lines exist
		"""
		return self.matrix.keys()

	def is_covered_by(self, execution: jccode.CirExecution, test):
		"""
		:param execution: the execution of which coverage is searched
		:param test: TestCase or int
		:return: True if the test covers the statement of execution
		"""
		if execution in self.matrix:
			result = self.matrix[execution]
			if isinstance(test, jcmuta.TestCase):
				test: jcmuta.TestCase
				test = test.get_test_id()
			else:
				test: int
			return result[test] == '1'
		return False

	def is_covered_in(self, execution: jccode.CirExecution, tests):
		"""
		:param execution:
		:param tests:
		:return: True if any tests in the input parameter cover the statement
		"""
		if execution in self.matrix:
			result = self.matrix[execution]
			for test in tests:
				if isinstance(test, jcmuta.TestCase):
					test: jcmuta.TestCase
					tid = test.get_test_id()
				else:
					test: int
					tid = test
				if result[tid] == '1':
					return True
		return False

	def is_coverable(self, execution: jccode.CirExecution):
		"""
		:param execution:
		:return: Whether covered by any test in record
		"""
		if execution in self.matrix:
			result = self.matrix[execution]
			return '1' in result
		return False

	def get_tests_to_cover(self, execution: jccode.CirExecution):
		"""
		:param execution:
		:return: the set of TestCase(s) covering the target execution
		"""
		tests = list()
		tspace = self.document.project.test_space
		if execution in self.matrix:
			result = self.matrix[execution]
			for tid in range(0, len(result)):
				if result[tid] == '1':
					test = tspace.get_test_case(tid)
					tests.append(test)
		return tests

	def get_executions_covered_by(self, test):
		"""
		:param test: TestCase or int
		:return: the set of executions covered by the test
		"""
		executions = list()
		for execution in self.matrix.keys():
			if self.is_covered_by(execution, test):
				executions.append(execution)
		return executions

	def get_executions_covered_in(self, tests):
		"""
		:param tests:
		:return: the set of executions covered by tests
		"""
		executions = list()
		for execution in self.matrix.keys():
			if self.is_covered_in(execution, tests):
				executions.append(execution)
		return executions


class SymCondition:
	"""
	Symbolic condition used to describe the requirement at each point on the process
	of killing or revealing a mutant, including attributes as following:
		--- category:	either "satisfaction" for constraint or "observation" for state error.
		---	operator:	refined type to describe the condition defined on program statement.
		---	execution:	the execution point of statement in control flow graph where it's set.
		---	location:	the C-intermediate code location where the condition is evaluated.
		---	parameter:	the symbolic expression to refine the description of the condition.
	"""

	def __init__(self, category: str, operator: str, execution: jccode.CirExecution, location: jccode.CirNode,
				 parameter: jcbase.SymNode):
		"""
		:param category: 	either "satisfaction" for constraint or "observation" for state error.
		:param operator: 	refined type to describe the condition defined on program statement.
		:param execution: 	the execution point of statement in control flow graph where it's set.
		:param location:	the C-intermediate code location where the condition is evaluated.
		:param parameter:	the symbolic expression to refine the description of the condition.
		"""
		self.category = category
		self.operator = operator
		self.execution = execution
		self.location = location
		self.parameter = parameter
		return

	def get_category(self):
		"""
		:return: either "satisfaction" for constraint or "observation" for state error
		"""
		return self.category

	def get_operator(self):
		"""
		:return: refined type to describe the condition defined on program statement.
		"""
		return self.operator

	def get_execution(self):
		"""
		:return: the execution point of statement in control flow graph where it's set.
		"""
		return self.execution

	def get_location(self):
		"""
		:return: the C-intermediate code location where the condition is evaluated.
		"""
		return self.location

	def get_parameter(self):
		"""
		:return: the symbolic expression to refine the description of the condition
		"""
		return self.parameter

	def has_parameter(self):
		"""
		:return: False if no parameter is needed to refine the description
		"""
		return not(self.parameter is None)

	def __str__(self):
		category = self.category
		operator = self.operator
		execution= "exe@{}@{}".format(self.execution.get_function().get_name(), self.execution.get_exe_id())
		location = "cir@{}".format(self.location.get_cir_id())
		parameter= "n@null"
		if self.has_parameter():
			parameter = "sym@{}@{}".format(self.parameter.get_class_name(), self.parameter.get_class_id())
		return "{}${}${}${}${}".format(category, operator, execution, location, parameter)


class SymConditions:
	"""
	Library from key to unique symbolic condition
	"""
	def __init__(self, document: CDocument):
		self.document = document
		self.conditions = dict()
		return

	def get_words(self):
		"""
		:return: the set of words encoding the symbolic conditions used
		"""
		return self.conditions.keys()

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions used in document
		"""
		return self.conditions.values()

	def get_condition(self, word: str):
		"""
		:param word:
		:return: the symbolic condition w.r.t. the given word
		"""
		if not(word in self.conditions):
			items = word.strip().split('$')
			category = items[0].strip()
			operator = items[1].strip()
			exec_tok = jcbase.CToken.parse(items[2].strip()).get_token_value()
			loct_tok = jcbase.CToken.parse(items[3].strip()).get_token_value()
			para_tok = jcbase.CToken.parse(items[4].strip()).get_token_value()
			execution = self.document.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
			location = self.document.project.program.cir_tree.get_cir_node(loct_tok)
			if para_tok is None:
				parameter = None
			else:
				parameter = self.document.sym_tree.get_sym_node(items[4].strip())
			self.conditions[word] = SymCondition(category, operator, execution, location, parameter)
		condition = self.conditions[word]
		condition: SymCondition
		return condition


class SymInstance:
	"""
	The instance of a check point in the process of revealing fault, including:
		(1) stage:		True if before infection or False after that.
		(2) location:	The execution point where checkpoint is defined.
		(3) result:		True --- if the checkpoint is passed
						False --- if the checkpoint failed to be met
						None --- the satisfaction of checkpoint is unknown
	"""

	def __init__(self, procedure, stage: bool, execution: jccode.CirExecution, result: bool):
		"""
		:param stage: 		True if before infection or False after that
		:param execution: 	The execution point where checkpoint is defined
		:param result: 		True --- if the checkpoint is passed
							False --- if the checkpoint failed to be met
							None --- the satisfaction of checkpoint is unknown
		"""
		procedure: SymProcedure
		self.procedure = procedure
		self.stage = stage
		self.execution = execution
		self.result = result
		self.conditions = list()
		return

	def get_procedure(self):
		"""
		:return: whether the checkpoint is defined
		"""
		return self.procedure

	def get_execution(self):
		"""
		:return: the execution node where the checkpoint is used
		"""
		return self.execution

	def get_stage(self):
		"""
		:return: True if before infection or False after that
		"""
		return self.stage

	def get_result(self):
		"""
		:return: 	True --- if the checkpoint is passed
					False --- if the checkpoint failed to be met
					None --- the satisfaction of checkpoint is unknown
		"""
		return self.result

	def get_conditions(self):
		"""
		:return: the set of conditions required being met for passing through the checkpoint
		"""
		return self.conditions


class SymProcedure:
	"""
	The symbolic procedure to describe the process for revealing a fault as a sequence of checkpoints
	that are annotated with symbolic conditions during testing.
	"""

	def __init__(self, document: CDocument, mutant: jcmuta.Mutant):
		"""
		:param document:
		:param mutant:
		"""
		self.document = document
		self.mutant = mutant
		self.instances = list()
		return

	def get_document(self):
		return self.document

	def get_mutant(self):
		return self.mutant

	def get_instances(self):
		return self.instances


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	for filename in os.listdir(root_path):
		directory_path = os.path.join(root_path, filename)
		c_project = jcmuta.CProject(directory_path, filename)
		c_document = CDocument(c_project,
							   os.path.join(directory_path, filename + ".cov"),
							   os.path.join(directory_path, filename + ".sym"),
							   os.path.join(directory_path, filename + ".sft"),
							   c_project.test_space.get_test_cases())
		print(c_project.program.name, ": Load",
			  len(c_document.get_mutants()), "mutants,",
			  len(c_document.get_procedures()), "procedures using",
			  len(c_document.get_conditions().get_words()), "symbolic conditions required for them, and against",
			  len(c_document.get_tests()), "test cases.")
		for procedure in c_document.get_procedures():
			procedure: SymProcedure
			for instance in procedure.get_instances():
				instance: SymInstance
				conditions = instance.get_conditions()
				for condition in conditions:
					condition: SymCondition
					print("\t{}\t{}\t{}\t{}\t{}\t{}\t{}".format(instance.get_stage(),
																instance.get_result(),
																condition.get_category(),
																condition.get_operator(),
																condition.get_execution(),
																condition.get_location().get_cir_code(),
																condition.get_parameter()))
		print("Testing over for", filename, "mutation project.")
	print()

