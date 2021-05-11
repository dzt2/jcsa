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

	def __init__(self, directory: str, name: str, file_postfix: str):
		"""
		:param directory: feature directory with project data
		:param name: project name
		:param file_postfix: ".sip" or ".sit"
		"""
		self.project = jcmuta.CProject(directory, name)
		cov_file_path = os.path.join(directory, name + ".cov")
		sip_file_path = os.path.join(directory, name + file_postfix)
		self.coverage_matrix = CoverageMatrix(self, cov_file_path)
		self.conditions = SymConditions(self)
		self.executions = list()		# the collection of executions for killing mutant
		self.muta_executions = dict()	# mapping from each mutant to the executions for killing it
		with open(sip_file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					sym_execution = SymExecution(self, len(self.executions), line)
					self.executions.append(sym_execution)
					if not (sym_execution.get_mutant() in self.muta_executions):
						self.muta_executions[sym_execution.get_mutant()] = list()
					self.muta_executions[sym_execution.get_mutant()].append(sym_execution)
		return

	def get_project(self):
		return self.project

	def get_program(self):
		return self.project.program

	def get_coverage_matrix(self):
		return self.coverage_matrix

	def get_conditions_lib(self):
		return self.conditions

	def get_executions(self):
		"""
		:return: the collection of symbolized executions for killing mutants
		"""
		return self.executions

	def get_execution(self, eid: int):
		return self.executions[eid]

	def get_mutants(self):
		"""
		:return: the set of mutants being killed by any executions in the document
		"""
		return self.muta_executions.keys()

	def get_executions_of(self, mutant: jcmuta.Mutant):
		"""
		:param mutant:
		:return: the set of executions for killing the specified mutant
		"""
		if not (mutant in self.muta_executions):
			return list()
		else:
			return self.muta_executions[mutant]

	@staticmethod
	def sip_document(directory: str, name: str):
		"""
		:param directory: feature directory with project data
		:param name: project name
		:return: document with execution paths of each mutant
		"""
		return CDocument(directory, name, ".sip")

	@staticmethod
	def sit_document(directory: str, name: str):
		"""
				:param directory: feature directory with project data
				:param name: project name
				:return: document with execution sets for each mutant
				"""
		return CDocument(directory, name, ".sit")


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


class SymInstanceStatus:
	"""
	It preserves the accumulated evaluation results for a specific symbolic instance or condition
	defined in the process for killing a target mutation.
	"""

	def __init__(self):
		"""
		create a symbolic instance status for accumulating evaluation results
		"""
		self.counters = [0, 0, 0]
		return

	def number_of_executions(self):
		"""
		:return: how many times the instance is evaluated
		"""
		return self.counters[0]

	def number_of_accepts(self):
		"""
		:return: how many times the instance is evaluated as true
		"""
		return self.counters[1]

	def number_of_rejects(self):
		"""
		:return: how many times the instance is evaluated as false
		"""
		return self.counters[2]

	def number_of_unknowns(self):
		"""
		:return: how many times the instance is evaluated as null
		"""
		return self.counters[0] - self.counters[1] - self.counters[2]

	def is_executed(self):
		"""
		:return: whether the instance or condition has been evaluated
		"""
		return self.counters[0] > 0

	def is_accepted(self):
		"""
		:return: whether the instance or condition is evaluated as true at least once
		"""
		return self.counters[1] > 0

	def is_rejected(self):
		"""
		:return: whether the instance of condition is evaluated and always false.
		"""
		if self.counters[0] > 0:
			return self.counters[2] >= self.counters[0]
		return False

	def is_available(self):
		"""
		:return: whether the instance or condition is evaluated as true or null
		"""
		if self.counters[0] > 0:
			if self.counters[1] > 0:
				return True
			else:
				return self.counters[0] > self.counters[2]
		else:
			return False

	def clc(self):
		"""
		:return: clear the status counters
		"""
		self.counters[0] = 0
		self.counters[1] = 0
		self.counters[2] = 0
		return

	def add(self, status):
		"""
		:param status:
		:return: accumulate the counters to this one
		"""
		status: SymInstanceStatus
		for k in range(0, len(self.counters)):
			self.counters[k] += status.counters[k]
		return

	def accumulate(self, executions: int, accepts: int, rejects: int):
		"""
		:param executions:
		:param accepts:
		:param rejects:
		:return: accumulate the number of evaluations, accepts and rejects into this status
		"""
		self.counters[0] += executions
		self.counters[1] += accepts
		self.counters[2] += rejects
		return


class SymInstanceNode:
	"""
	An instance that can be evaluated in the process for killing a target mutant.
	"""

	def __init__(self, conditions):
		"""
		:param conditions: the collection of symbolic conditions being evaluated in the instance phase
		"""
		self.status = SymInstanceStatus()
		self.conditions = list()
		for sym_condition in conditions:
			sym_condition: SymCondition
			self.conditions.append(sym_condition)
		return

	def get_status(self):
		"""
		:return: the accumulated status of the conditions evaluated in the node
		"""
		return self.status

	def get_conditions(self):
		"""
		:return: the collection of symbolic conditions being evaluated in the instance phase
		"""
		return self.conditions

	def get_condition(self, k: int):
		"""
		:param k:
		:return: the kth condition used in the instance node
		"""
		return self.conditions[k]

	def __len__(self):
		"""
		:return: the number of symbolic conditions used in the node
		"""
		return len(self.conditions)


class SymExecution:
	"""
	It describes the execution for killing a mutation using a sequence of local instance nodes and
	accumulated global instance nodes for each condition.
	"""

	def __init__(self, document: CDocument, eid: int, line: str):
		"""
		:param document: the document where the execution is defined
		:param eid: the integer ID of the execution in its documents
		:param line: mid {cov$acc$rej {category$operator$execution$location$parameter}+ ;}+
		"""
		# 1. declarations
		items = line.strip().split('\t')
		self.document = document
		self.eid = eid
		self.mutant = self.document.project.muta_space.get_mutant(int(items[0].strip()))
		self.instance_nodes = list()	# the sequence of instance nodes in execution path
		self.condition_dict = dict()	# the mapping from each condition to its instance with status
		items = items[1: ]				# remove the mid item

		# 2. parse to self.instance_nodes
		instance_items = list()
		for item in items:
			item = item.strip()
			if len(item) > 0:
				if item != ';':
					instance_items.append(item)
				else:
					# I. head parsing
					head_items = instance_items[0].strip().split('$')
					ex = int(head_items[0].strip())
					ac = int(head_items[1].strip())
					re = int(head_items[2].strip())

					# II. conditions parsing
					conditions = set()
					for k in range(1, len(instance_items)):
						word = instance_items[k].strip()
						conditions.add(self.document.get_conditions_lib().get_condition(word))

					# III. instance construction
					instance_items.clear()
					instance_node = SymInstanceNode(conditions)
					instance_node.get_status().accumulate(ex, ac, re)
					self.instance_nodes.append(instance_node)

		# 3. construct dictionary
		for instance_node in self.instance_nodes:
			for sym_condition in instance_node.get_conditions():
				if not (sym_condition in self.condition_dict):
					self.condition_dict[sym_condition] = SymInstanceNode([sym_condition])
				self.condition_dict[sym_condition].get_status().add(instance_node.get_status())

		return

	def get_document(self):
		"""
		:return: the document where the execution is defined
		"""
		return self.document

	def get_id(self):
		"""
		:return: the integer ID of the execution in its documents
		"""
		return self.eid

	def get_mutant(self):
		"""
		:return: the mutation to be killed in the execution process
		"""
		return self.mutant

	def get_instance_nodes(self):
		"""
		:return: the collection of instance nodes in the sequence of execution path
		"""
		return self.instance_nodes

	def __len__(self):
		"""
		:return: the length of the execution path with multiple condition instance nodes
		"""
		return len(self.instance_nodes)

	def get_instance_node(self, k: int):
		"""
		:param k:
		:return: the kth instance node in the execution path.
		"""
		return self.instance_nodes[k]

	def get_conditions(self):
		"""
		:return: the collection of symbolic conditions used in
		"""
		return self.condition_dict.keys()

	def get_condition_node(self, sym_condition: SymCondition):
		"""
		:param sym_condition:
		:return: the instance node representing the symbolic condition
		"""
		return self.condition_dict[sym_condition]

	def get_condition_nodes(self):
		"""
		:return: the collection of instance nodes representing the conditions used in this process
		"""
		return self.condition_dict.values()


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	print_condition = False
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		c_directory = os.path.join(root_path, file_name)
		c_document = CDocument.sip_document(c_directory, file_name)
		print(file_name, "loads", len(c_document.get_mutants()), "mutants used and",
			  len(c_document.get_executions()), "symbolic instance paths annotated with",
			  len(c_document.get_conditions_lib().get_all_conditions()), "conditions.")

		for sym_execution in c_document.get_executions():
			print("\tPath[{}]: Killing mutant#{} using {} instances and {} conditions.".
				  format(sym_execution.get_id(),
						 sym_execution.get_mutant().get_muta_id(),
						 len(sym_execution.get_instance_nodes()),
						 len(sym_execution.get_conditions())))
			if print_condition:
				for condition in sym_execution.get_conditions():
					print("\t\t--> {}\t{}\t{}\t{}\t{}".format(condition.get_category(),
															  condition.get_operator(),
															  condition.get_execution(),
															  condition.get_location().get_cir_code(),
															  condition.get_parameter()))
		print()

