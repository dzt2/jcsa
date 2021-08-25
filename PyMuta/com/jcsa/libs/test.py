""" This file refines the model of symbolic execution along with condition for description. """


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta


class CDocument:
	"""
	It denotes the document of mutation testing project features.
	"""

	def __init__(self, directory: str, file_name: str, exec_postfix: str):
		"""
		:param directory:	feature directory using project data
		:param file_name:	the name of mutation testing project
		:param exec_postfix:	the postfix of file to preserve the symbolic execution features.
		"""
		self.project = jcmuta.CProject(directory, file_name)
		self.conditions = SymConditionSpace(self)
		exec_file = os.path.join(directory, file_name + exec_postfix)
		self.exec_space = SymExecutionSpace(self, exec_file)
		return

	def get_project(self):
		"""
		:return: the mutation testing project as the basis of this document
		"""
		return self.project

	def get_program(self):
		"""
		:return: the source program on which the mutation test project is defined
		"""
		return self.project.program

	def get_condition_space(self):
		"""
		:return: the space where the symbolic conditions are created and defined
		"""
		return self.conditions

	def get_condition(self, word: str):
		if word:
			return self.conditions.decode(word)
		else:
			return None

	def get_conditions(self, words):
		"""
		:param words: the set of words encoding symbolic conditions
		:return:
		"""
		conditions = set()
		for word in words:
			condition = self.get_condition(word)
			if not (condition is None):
				conditions.add(condition)
		return conditions

	def get_execution_space(self):
		return self.exec_space


class SymCondition:
	"""
	It models a symbolic condition defined in C-intermediate representation of source program.
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
		return not (self.parameter is None)

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


class SymConditionSpace:
	"""
	It maintains the mapping from string-key to unique SymCondition defined in execution file.
	"""

	def __init__(self, document: CDocument):
		"""
		:param document: the feature document of mutation test project
		"""
		self.document = document
		self.condition_dict = dict()
		return

	def get_document(self):
		return self.document

	def get_words(self):
		return self.condition_dict.keys()

	def get_conditions(self):
		return self.condition_dict.values()

	def decode(self, word: str):
		"""
		:param word: in terms of category$operator$execution$location$parameter
		:return: SymCondition (unique)
		"""
		if not (word in self.condition_dict):
			items = word.strip().split('$')
			category = items[0].strip()
			operator = items[1].strip()
			exec_tok = jcbase.CToken.parse(items[2].strip()).get_token_value()
			loct_tok = jcbase.CToken.parse(items[3].strip()).get_token_value()
			execution = self.document.project.program.function_call_graph.get_execution(exec_tok[0], exec_tok[1])
			location = self.document.project.program.cir_tree.get_cir_node(loct_tok)
			para_tok = jcbase.CToken.parse(items[4].strip()).get_token_value()
			if para_tok is None:
				parameter = None
			else:
				parameter = self.document.project.sym_tree.get_sym_node(items[4].strip())
			self.condition_dict[word] = SymCondition(category, operator, execution, location, parameter)
		condition = self.condition_dict[word]
		condition: SymCondition
		return condition


class SymExecutionState:
	"""
	It denotes a state node in symbolic mutant execution
	"""

	def __init__(self, execution, attribute: SymCondition, annotations):
		"""
		:param execution: 	symbolic mutant execution
		:param attribute: 	the condition to represent
		:param annotations: the set of conditions being annotated
		"""
		execution: SymExecution
		self.execution = execution
		self.attribute = attribute
		self.annotations = set()
		for annotation in annotations:
			annotation: SymCondition
			self.annotations.add(annotation)
		return

	def get_sym_execution(self):
		return self.execution

	def get_cir_execution(self):
		return self.attribute.get_execution()

	def get_attribute(self):
		return self.attribute

	def get_annotations(self):
		return self.annotations


class SymExecution:
	"""
	It denotes an execution between mutant and test case
	"""

	def __init__(self, space, eid: int, line: str):
		"""
		:param document:
		:param eid: the integer of the execution in document
		:param line: mid tid {attribute {annotation}* ;}* \n
		"""
		space: SymExecutionSpace
		self.space = space
		self.eid = eid
		document = space.document

		items = line.strip().split('\t')
		project = document.get_project()
		mutant_token = jcbase.CToken.parse(items[0].strip()).get_token_value()
		test_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
		self.mutant = project.muta_space.get_mutant(mutant_token)
		self.test_case = None
		if not (test_token is None):
			self.test_case = project.test_space.get_test_case(test_token)

		self.states = list()
		self.conditions = set()
		condition_buffer = list()
		for k in range(2, len(items)):
			word = items[k].strip()
			if len(word) > 0:
				if word != ";":
					condition = document.get_condition(word)
					condition_buffer.append(condition)
				else:
					state = SymExecutionState(self, condition_buffer[0], condition_buffer[1: ])
					condition_buffer.clear()
					for annotation in state.get_annotations():
						self.conditions.add(annotation)
					self.states.append(state)

		return

	def get_space(self):
		"""
		:return: the space where the execution is defined
		"""
		return self.space

	def get_eid(self):
		"""
		:return: the integer ID of the execution in space
		"""
		return self.eid

	def get_mutant(self):
		"""
		:return: the mutant to be killed
		"""
		return self.mutant

	def get_test(self):
		"""
		:return: the test case used to execute
		"""
		return self.test_case

	def has_test(self):
		"""
		:return: False if the execution is generated symbolically
		"""
		return not (self.test_case is None)

	def get_states(self):
		"""
		:return: the sequence of state nodes in the execution
		"""
		return self.states

	def get_conditions(self):
		"""
		:return: the set of symbolic conditions required in the execution
		"""
		return self.conditions


class SymExecutionSpace:
	"""
	It defines the space of symbolic mutant executions
	"""

	def __init__(self, document: CDocument, exec_file: str):
		"""
		:param document: the feature document of project
		:param exec_file: xxx.stn or xxx.stp
		"""
		self.document = document
		self.exec_list = list()
		self.muta_exec = dict()		# Mutant 	--> SymExecution+
		self.test_exec = dict()		# TestCase	-->	SymExecution+
		with open(exec_file, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					execution = SymExecution(self, len(self.exec_list), line)
					self.exec_list.append(execution)
					mutant = execution.get_mutant()
					if not (mutant in self.muta_exec):
						self.muta_exec[mutant] = list()
					self.muta_exec[mutant].append(execution)
					test = execution.get_test()
					if not (test in self.test_exec):
						self.test_exec[test] = list()
					self.test_exec[test].append(execution)
		return

	def get_document(self):
		return self.document

	def get_executions(self):
		return self.exec_list

	def get_execution(self, eid: int):
		return self.exec_list[eid]

	def get_executions_of(self, key):
		"""
		:param key: either Mutant or TestCase or None
		:return: the set of SymExecution* referring to the key
		"""
		if key in self.muta_exec:
			return self.muta_exec[key]
		elif key in self.test_exec:
			return self.test_exec[key]
		else:
			return list()

	def get_mutants(self):
		return self.muta_exec.keys()

	def get_tests(self):
		return self.test_exec.keys()


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features"
	print_condition = False
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		c_directory = os.path.join(root_path, file_name)
		c_document = CDocument(c_directory, file_name, ".stn")
		exec_space = c_document.get_execution_space()
		print(file_name, "loads", len(exec_space.get_mutants()), "mutants used and",
			  len(exec_space.get_executions()), "symbolic instance paths annotated with",
			  len(c_document.get_condition_space().get_conditions()), "conditions.")

		for sym_execution in exec_space.get_executions():
			print("\tPath[{}]: Killing mutant#{} using {} instances and {} conditions.".
				  format(sym_execution.get_eid(),
						 sym_execution.get_mutant().get_muta_id(),
						 len(sym_execution.get_states()),
						 len(sym_execution.get_conditions())))
			if print_condition:
				for condition in sym_execution.get_conditions():
					print("\t\t--> {}\t{}\t{}\t{}\t{}".format(condition.get_category(),
															  condition.get_operator(),
															  condition.get_execution(),
															  condition.get_location().get_cir_code(),
															  condition.get_parameter()))
		print()
	print("Testing end for all.")

