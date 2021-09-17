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
		anot_file = os.path.join(directory, file_name + ".ant")
		self.annotation_tree = CirAnnotationTree(self, anot_file)
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

	def get_annotation_tree(self):
		return self.annotation_tree

	def get_execution_space(self):
		return self.exec_space


class CirAnnotation:
	"""
	It defines an annotation feature that is annotated with some execution point and store_unit with specified
	logical predicate as: logic_type{execution}(store_unit, symb_value)
	"""

	def __init__(self, logic_type: str, execution: jccode.CirExecution, store_unit: jccode.CirNode, value: jcbase.SymNode):
		"""
		:param logic_type: 	the logical predicate defining this annotation
		:param execution: 	the execution point where this annotation is introduced
		:param store_unit: 	the location on which the feature is annotated with
		:param value: 		the symbolic value to refine the description of the logic predicate
		"""
		self.logic_type = logic_type
		self.execution = execution
		self.store_unit = store_unit
		self.symb_value = value
		self.next_annotations = list()
		return

	def get_logic_type(self):
		return self.logic_type

	def get_execution(self):
		return self.execution

	def get_store_unit(self):
		return self.store_unit

	def get_symb_value(self):
		return self.symb_value

	def get_next_annotations(self):
		"""
		:return: the set of CirAnnotation(s) subsumed by this one
		"""
		return self.next_annotations

	def __str__(self):
		logic_type = self.logic_type
		execution = "exe@" + self.execution.function.name + "@" + str(self.execution.get_exe_id())
		store_unit = "cir@" + str(self.store_unit.get_cir_id())
		sym_value = "sym@" + self.symb_value.class_name + "@" + str(self.symb_value.get_class_id())
		return logic_type + "$" + execution + "$" + store_unit + "$" + sym_value

	def get_all_children(self):
		"""
		:return: the set of all the subsumed annotations by this one
		"""
		annotations = set()
		self.__all_children__(annotations)
		return annotations

	def __all_children__(self, annotations: set):
		if not (self in annotations):
			annotations.add(self)
			for child in self.next_annotations:
				child: CirAnnotation
				child.__all_children__(annotations)
		return


class CirAnnotationTree:
	"""
	The hierarchical structure to manage the CirAnnotation being created
	"""

	def __init__(self, document: CDocument, file_path: str):
		"""
		:param document:
		:param file_path:
		"""
		self.document = document
		self.annotations = dict()
		words = set()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.strip().split('\t')
					for item in items:
						word = item.strip()
						if len(word) > 0:
							words.add(word)
		for word in words:
			items = word.split('$')
			logic_type = items[0].strip()
			exec_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
			unit_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
			execution = document.get_program().function_call_graph.get_execution(exec_token[0], exec_token[1])
			store_unit = document.get_program().cir_tree.get_cir_node(unit_token)
			symb_value = document.get_project().sym_tree.get_sym_node(items[3].strip())
			self.annotations[word] = CirAnnotation(logic_type, execution, store_unit, symb_value)
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.strip().split('\t')
					head = self.annotations[items[0].strip()]
					for k in range(1, len(items)):
						child = self.annotations[items[k].strip()]
						head.next_annotations.append(child)
		return

	def get_document(self):
		return self.document

	def get_annotations(self):
		return self.annotations.values()

	def get_annotation(self, word: str):
		return self.annotations[word]


class SymExecution:
	def __init__(self, space, eid: int, mutant: jcmuta.Mutant, test: jcmuta.TestCase, annotations):
		space: SymExecutionSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.test = test
		self.annotations = set()
		for annotation in annotations:
			annotation: CirAnnotation
			self.annotations.add(annotation)
		return

	def get_space(self):
		return self.space

	def get_eid(self):
		return self.eid

	def get_mutant(self):
		return self.mutant

	def has_test(self):
		return not (self.test is None)

	def get_test(self):
		return self.test

	def get_annotations(self):
		return self.annotations


class SymExecutionSpace:
	"""
	The set of symbolic execution lines
	"""
	def __init__(self, document: CDocument, file_path: str):
		"""
		:param document:
		:param file_path:
		"""
		self.document = document
		self.executions = list()
		self.muta_execs = dict()
		self.test_execs = dict()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					muta_token = jcbase.CToken.parse(items[0].strip()).get_token_value()
					test_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
					mutant = document.get_project().muta_space.get_mutant(muta_token)
					if test_token is None:
						test = None
					else:
						test = document.get_project().test_space.get_test_case(test_token)
					annotations = set()
					for k in range(2, len(items)):
						word = items[k].strip()
						if word != ';':
							annotation = document.get_annotation_tree().get_annotation(word)
							annotations.add(annotation)
					sym_execution = SymExecution(self, len(self.executions), mutant, test, annotations)
					self.executions.append(sym_execution)
					if not (mutant in self.muta_execs):
						self.muta_execs[mutant] = set()
					self.muta_execs[mutant].add(sym_execution)
					if not (test in self.test_execs):
						self.test_execs[test] = set()
					self.test_execs[test].add(sym_execution)
		return

	def get_document(self):
		return self.document

	def get_executions(self):
		return self.executions

	def get_execution(self, eid: int):
		return self.executions[eid]

	def get_executions_of(self, key):
		"""
		:param key: Mutant or TestCase
		:return:
		"""
		if key in self.muta_execs:
			return self.muta_execs[key]
		elif key in self.test_execs:
			return self.test_execs[key]
		else:
			return set()

	def get_mutants(self):
		return self.muta_execs.keys()

	def get_tests(self):
		return self.test_execs.keys()


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features"
	print_condition = True
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		c_directory = os.path.join(root_path, file_name)
		c_document = CDocument(c_directory, file_name, ".stn")
		exec_space = c_document.get_execution_space()
		print(file_name, "loads", len(exec_space.get_mutants()), "mutants used and",
			  len(exec_space.get_executions()), "symbolic instance paths annotated with",
			  len(c_document.get_annotation_tree().get_annotations()), "annotations.")

		for sym_execution in exec_space.get_executions():
			print("\tPath[{}]: Killing mutant#{} using {} annotations in.".
				  format(sym_execution.get_eid(),
						 sym_execution.get_mutant().get_muta_id(),
						 len(sym_execution.get_annotations())))
			if print_condition:
				for annotation in sym_execution.get_annotations():
					print("\t\t--> {}[{}]({}, {})".format(annotation.get_logic_type(),
														  annotation.get_execution(),
														  annotation.get_store_unit().get_cir_code(),
														  annotation.get_symb_value()))
		print()
	print("Testing end for all.")

