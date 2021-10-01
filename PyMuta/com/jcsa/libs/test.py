""" This file refines the model of symbolic execution along with condition for description. """


import os
from collections import deque

import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta
import graphviz


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
		graph_file = os.path.join(directory, file_name + ".cmt")
		self.muta_graph = CirMutationTree(self, graph_file)
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
		self.children = list()
		return

	def get_logic_type(self):
		return self.logic_type

	def get_execution(self):
		return self.execution

	def get_store_unit(self):
		return self.store_unit

	def get_symb_value(self):
		return self.symb_value

	def get_children(self):
		"""
		:return: the set of CirAnnotation(s) subsumed by this one
		"""
		return self.children

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
			for child in self.children:
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
		words = self.__load_words__(file_path)
		self.__build_from__(words)
		self.__link_nodes__(file_path)
		return

	def __load_words__(self, file_path: str):
		"""
		:param file_path:
		:return: the set of words encoding the annotations uniquely
		"""
		words = set()
		self.annotations.clear()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.strip().split('\t')
					for item in items:
						word = item.strip()
						if len(word) > 0:
							words.add(word)
		return words

	def __build_from__(self, words: set):
		"""
		:param words: set of words encoding annotations uniquely
		:return:
		"""
		program = self.document.get_program()
		project = self.document.get_project()
		for word in words:
			word: str
			items = word.split('$')
			logic_type = items[0].strip()
			exec_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
			unit_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
			execution = program.function_call_graph.get_execution(exec_token[0], exec_token[1])
			store_unit = program.cir_tree.get_cir_node(unit_token)
			symb_value = project.sym_tree.get_sym_node(items[3].strip())
			self.annotations[word] = CirAnnotation(logic_type, execution, store_unit, symb_value)
		return

	def __link_nodes__(self, file_path: str):
		"""
		:param file_path:
		:return: connects each annotation to their directly subsumed ones
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.strip().split('\t')
					parent = self.annotations[items[0].strip()]
					parent.children.clear()
					for k in range(1, len(items)):
						child = self.annotations[items[k].strip()]
						parent.children.append(child)
		return

	def get_document(self):
		return self.document

	def get_words(self):
		"""
		:return: the set of words encoding the annotations uniquely
		"""
		return self.annotations.keys()

	def get_annotations(self):
		"""
		:return: the set of annotations that are uniquely defined
		"""
		return self.annotations.values()

	def get_annotation(self, word: str):
		"""
		:param word:
		:return: the annotation w.r.t. the word encoding it in the tree
		"""
		return self.annotations[word]

	def __len__(self):
		"""
		:return: the number of nodes under the tree
		"""
		return len(self.annotations)


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
		self.__load__(file_path)
		self.__link__()
		return

	def __load__(self, file_path: str):
		"""
		:param file_path:
		:return: it loads the execution lines from given file
		"""
		document = self.get_document()
		self.executions.clear()
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
		return

	def __link__(self):
		"""
		:return: builds the links between executions and mutations
		"""
		self.muta_execs.clear()
		for sym_execution in self.executions:
			mutant = sym_execution.get_mutant()
			if not (mutant in self.muta_execs):
				self.muta_execs[mutant] = set()
			self.muta_execs[mutant].add(sym_execution)
		return

	def get_document(self):
		return self.document

	def get_executions(self):
		return self.executions

	def get_execution(self, eid: int):
		return self.executions[eid]

	def get_executions_of(self, mutant: jcmuta.Mutant):
		"""
		:param mutant:
		:return:
		"""
		if mutant in self.muta_execs:
			return self.muta_execs[mutant]
		else:
			return set()

	def get_mutants(self):
		return self.muta_execs.keys()


class CirMutationTree:
	"""
	It models the program impacts graph for each mutation in a document
	"""

	def __init__(self, document: CDocument, file_path: str):
		self.document = document
		self.nodes = dict()
		self.__parse__(file_path)
		return

	def get_document(self):
		return self.document

	def __parse__(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		self.nodes.clear()
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					for item in items:
						item = item.strip()
						if len(item) > 0:
							key = item
							if not (key in self.nodes):
								self.nodes[key] = CirMutationNode(self, key)
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					items = line.split('\t')
					for k in range(0, len(items) - 1):
						prev_node = self.nodes[items[k + 1].strip()]
						post_node = self.nodes[items[k].strip()]
						prev_node: CirMutationNode
						post_node: CirMutationNode
						if not (post_node in prev_node.ou_nodes):
							prev_node.ou_nodes.append(post_node)
		return

	def write(self, o_directory: str):
		"""
		:param o_directory:
		:return:
		"""
		file_name = self.document.get_program().name
		graph = graphviz.Digraph(comment="Mutation Impacts Graph for {}".format(file_name))
		for node in self.nodes.values():
			node: CirMutationNode
			key = node.get_text()
			value = str(node)
			graph.node(key, value)
		for node in self.nodes.values():
			node: CirMutationNode
			parent = node.get_text()
			for ou_node in node.get_ou_nodes():
				ou_node: CirMutationNode
				child = ou_node.get_text()
				graph.edge(parent, child)
		graph.render(filename=file_name + ".cmt", directory=o_directory, format="pdf")
		file_path = os.path.join(o_directory, file_name + ".cmt")
		os.remove(file_path)
		return

	def write_on(self, o_directory: str, mutant: jcmuta.Mutant):
		"""
		:param o_directory:
		:param mutant:
		:return:
		"""
		key = "mut@{}".format(mutant.get_muta_id())
		if key in self.nodes:
			node = self.nodes[key]
			queue = deque()
			nodes = set()
			queue.append(node)
			while len(queue) > 0:
				node = queue.popleft()
				node: CirMutationNode
				nodes.add(node)
				for ou_node in node.get_ou_nodes():
					if not (ou_node in nodes):
						queue.append(ou_node)
			if len(nodes) > 0:
				file_name = self.document.get_program().name
				graph = graphviz.Digraph(comment="Mutation Impacts Graph for Mutant#{}".format(mutant.get_muta_id()))
				for node in nodes:
					key = node.get_text()
					value = str(node)
					graph.node(key, value)
				for node in nodes:
					node: CirMutationNode
					parent = node.get_text()
					for ou_node in node.get_ou_nodes():
						ou_node: CirMutationNode
						child = ou_node.get_text()
						graph.edge(parent, child)
				graph.render(filename=file_name + ".cmt." + str(mutant.get_muta_id()), directory=o_directory, format="pdf")
				file_path = os.path.join(o_directory, file_name + ".cmt"  + str(mutant.get_muta_id()))
				os.remove(file_path)
		return


class CirMutationNode:
	"""
	It denotes a unique node in the CirMutationTree.
	"""

	def __init__(self, tree: CirMutationTree, text: str):
		self.tree = tree
		self.text = text
		self.ou_nodes = list()
		return

	def get_tree(self):
		return self.tree

	def get_text(self):
		return self.text

	def get_ou_nodes(self):
		return self.ou_nodes

	def __str__(self):
		document = self.tree.get_document()
		if '$' in self.text:
			items = self.text.split('$')
			logic_class = items[0].strip()
			exec_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
			unit_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
			execution = document.get_program().function_call_graph.get_execution(exec_token[0], exec_token[1])
			store_unit = document.get_program().cir_tree.get_cir_node(unit_token)
			symb_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
			if symb_token is None:
				symb_value = None
			else:
				symb_value = document.get_project().sym_tree.get_sym_node(items[3].strip())
			return "Class: {}\nExec: {}\nUnit: {}\nValue: {}".format(logic_class, execution, store_unit.code, symb_value)
		else:
			mutant_token = jcbase.CToken.parse(self.text.strip()).get_token_value()
			source_mutant = document.get_project().muta_space.get_mutant(mutant_token)
			mid = source_mutant.get_muta_id()
			if source_mutant.get_result().is_killed_in(None):
				result = "killed"
			else:
				result = "survive"
			m_class = source_mutant.get_mutation().get_mutation_class()
			m_operator = source_mutant.get_mutation().get_mutation_operator()
			m_location = source_mutant.get_mutation().get_location()
			m_function = m_location.function_definition_of()
			func_name = m_function.get_code(True)
			index = func_name.index('(')
			func_name = func_name[0: index].strip()
			line = m_location.line_of(tail=False)
			code = m_location.get_code(True)
			parameter = source_mutant.get_mutation().get_parameter()
			return "ID: {}\nRes: {}\nClass: {}\nOperator: {}\nFunc: {}\nLine: {}\nCode: {}\nParam: {}".format(mid, result, m_class, m_operator, func_name, line, code, parameter)


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features"
	impa_path = "/home/dzt2/Development/Data/zexp/impacts"
	print_condition = False
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

		c_document.muta_graph.write(impa_path)
		print()
	print("Testing end for all.")

