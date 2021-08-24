""" This file develops the model to describe the symbolic features used to describe mutant execution """


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta


class CDocument:
	"""
	It defines the group of features and source program
	"""
	def __init__(self, directory: str, name: str):
		"""
		:param directory: feature directory with project data
		:param name: project name
		"""
		self.project = jcmuta.CProject(directory, name)
		self.conditions = SymConditions(self)
		self.executions = list()
		self.muta_executions = dict()  # mapping from each mutant to the executions for killing it

		stn_file_path = os.path.join(directory, name + ".stn")
		with open(stn_file_path, 'r') as reader:
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
	def load_document(directory: str, name: str):
		"""
				:param directory: feature directory with project data
				:param name: project name
				:return: document with execution paths of each mutant
				"""
		return CDocument(directory, name)


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


class SymExecutionState:
	"""
	It represents the state at a particular point in the program
	"""

	def __init__(self, sym_execution, all_conditions):
		"""
		:param sym_execution: 	SymExecution
		:param all_conditions: 	CirAnnotation+
		"""
		sym_execution: SymExecution
		self.sym_execution = sym_execution
		self.all_conditions = list()
		for condition in all_conditions:
			condition: SymCondition
			self.all_conditions.append(condition)
		return

	def get_sym_execution(self):
		"""
		:return: the execution where the state is defined
		"""
		return self.sym_execution

	def get_all_conditions(self):
		"""
		:return: annotations of the state
		"""
		return self.all_conditions


class SymExecution:
	"""
	mutant, test_case, result, state+
	"""
	def __init__(self, document: CDocument, eid:int, line: str):
		"""
		:param document:
		:param line: mid tid [attribute annotation* ;]*
		"""
		self.document = document
		self.eid = eid

		items = line.strip().split('\t')
		project = document.get_project()
		mutant_token = jcbase.CToken.parse(items[0].strip()).get_token_value()
		test_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
		self.mutant = project.muta_space.get_mutant(mutant_token)
		self.test_case = None
		if not(test_token is None):
			self.test_case = project.test_space.get_test_case(test_token)

		self.state_list = list()
		condition_lib = document.get_conditions_lib()
		condition_buffer = list()
		self.condition_set = set()
		for k in range(2, len(items)):
			word = items[k].strip()
			if len(word) > 0:
				if word != ';':
					condition = condition_lib.get_condition(word)
					condition_buffer.append(condition)
				else:
					state = SymExecutionState(self, condition_buffer)
					for sub_condition in state.get_all_conditions():
						self.condition_set.add(sub_condition)
					self.state_list.append(state)
		return

	def get_document(self):
		return self.document

	def get_id(self):
		return self.eid

	def get_mutant(self):
		return self.mutant

	def get_test_case(self):
		return self.test_case

	def has_test_case(self):
		return not(self.test_case is None)

	def get_state_list(self):
		return self.state_list

	def get_condition_set(self):
		"""
		:return: the set of conditions used in execution states
		"""
		return self.condition_set


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features_s"
	print_condition = False
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		c_directory = os.path.join(root_path, file_name)
		c_document = CDocument.load_document(c_directory, file_name)
		print(file_name, "loads", len(c_document.get_mutants()), "mutants used and",
			  len(c_document.get_executions()), "symbolic instance paths annotated with",
			  len(c_document.get_conditions_lib().get_all_conditions()), "conditions.")

		for sym_execution in c_document.get_executions():
			print("\tPath[{}]: Killing mutant#{} using {} instances and {} conditions.".
				  format(sym_execution.get_id(),
						 sym_execution.get_mutant().get_muta_id(),
						 len(sym_execution.get_state_list()),
						 len(sym_execution.get_condition_set())))
			if print_condition:
				for condition in sym_execution.get_condition_set():
					print("\t\t--> {}\t{}\t{}\t{}\t{}".format(condition.get_category(),
															  condition.get_operator(),
															  condition.get_execution(),
															  condition.get_location().get_cir_code(),
															  condition.get_parameter()))
		print()
	print("Testing end for all.")

