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

	def __init__(self, directory: str, file_name: str):
		"""
		:param directory:	feature directory using project data
		:param file_name:	the name of mutation testing project
		"""
		self.project = jcmuta.CProject(directory, file_name)
		anot_file = os.path.join(directory, file_name + ".ant")
		self.annotation_tree = CirAnnotationTree(self, anot_file)
		exec_file = os.path.join(directory, file_name + ".stp")
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

	## visualization

	def __do_cancel__(self):
		return self

	def __get_label__(self, obj):
		"""
		:param obj: Mutant or CirAnnotation
		:return:
		"""
		self.__do_cancel__()
		if isinstance(obj, jcmuta.Mutant):
			obj: jcmuta.Mutant
			mutant = obj
			mutation = mutant.get_mutation()
			mid = mutant.get_muta_id()
			res = mutant.get_result().is_killed_in(None)
			cls = mutation.get_mutation_class()
			opr = mutation.get_mutation_operator()
			lin = mutation.get_location().line_of(False)
			typ = mutation.get_location().get_class_name()
			code = mutation.get_location().get_code(True)
			if len(code) > 64:
				code = code[0: 64] + "..."
			label = "MID: {}#{}\nCLS: {}#{}\nLOC: {}#{}\nCOD: {}\nPAR: {}".format(mid, res, cls, opr, typ, lin, code,
																				  mutation.get_parameter()).strip()
		else:
			obj: CirAnnotation
			annotation = obj
			logic_type = annotation.get_logic_type()
			exec_text = str(annotation.get_execution())
			stmt_type = annotation.get_execution().get_statement().get_class_name()
			stmt_code = annotation.get_execution().get_statement().get_cir_code()
			if len(stmt_code) > 64:
				stmt_code = stmt_code[0: 64] + "..."
			store_unit = annotation.get_store_unit().get_cir_code()
			if len(store_unit) > 64:
				store_unit = store_unit[0: 64] + "..."
			symb_value = annotation.get_symb_value().get_code()
			label = "LOG: {}\nEXE: {}#{}\nSTM: {}\nUNT: {}\nVAL: {}".format(logic_type, exec_text, stmt_type,
																			stmt_code, store_unit, symb_value).strip()
		label = label.replace('\"', '\'\'')
		return label

	def __visualize__(self, nodes: dict, edges: dict, o_directory: str, file_name: str):
		"""
		:param nodes:
		:param edges:
		:param o_directory:
		:param file_name:
		:return:
		"""
		if len(nodes) > 0:
			graph = graphviz.Digraph()
			for name, label in nodes.items():
				graph.node(name, label)
			for parent, children in edges.items():
				for child in children:
					graph.edge(parent, child)
			if not os.path.exists(o_directory):
				os.mkdir(o_directory)
			graph.render(filename=file_name, directory=o_directory, format="pdf")
			file_path = os.path.join(o_directory, file_name)
			os.remove(file_path)
		return self

	def visualize_annotation_trees(self, roots, o_directory: str, file_name: str, used_tests):
		"""
		:param roots: 		the root nodes for printing annotation tree
		:param o_directory:	the directory where the pdf file is generated
		:param file_name:	the name of the pdf file for visualizing the annotation trees from given roots.
		:param used_tests:	the set of test cases for evaluating the annotation in the node
		:return:
		"""
		## 1. capture the annotation nodes for being printed from the root
		queue, records = deque(), set()
		for root in roots:
			root: CirAnnotation
			queue.append(root)
			while len(queue) > 0:
				parent = queue.popleft()
				parent: CirAnnotation
				records.add(parent)
				for child in parent.get_children():
					child: CirAnnotation
					if not (child in records):
						queue.append(child)

		## 2. construct the directed graph's nodes and edges in dictionary
		name_nodes, name_edges = dict(), dict()
		for annotation in records:
			name = str(annotation)
			label = self.__get_label__(annotation)
			result, killed, alives, confidence = annotation.evaluate(self, used_tests)
			score = "[{}: {}, {}; {}%]".format(result, killed, alives, confidence)
			name_nodes[name] = label + "\n" + score
			if not (name in name_edges):
				name_edges[name] = set()
			for child in annotation.get_children():
				child_name = str(child)
				if child_name != name:
					name_edges[name].add(child_name)

		## 3. construct the directed graph instance for being visualized
		self.__visualize__(name_nodes, name_edges, o_directory, file_name)
		return

	def visualize_execution_graphs(self, executions, o_directory: str, file_name: str):
		"""
		:param executions: 	the sequences of symbolic executions for killing mutant in visualization
		:param o_directory: the directory where the pdf file will be generated
		:param file_name: 	the name of the pdf file for visualizing the execution trees for printed
		:return:
		"""
		## 1. it generates the mutant and node and their corresponding labels
		name_nodes = dict()
		for execution in executions:
			## 1-1. generate the mutation and its label
			execution: SymExecution
			mutant = execution.get_mutant()
			mutant_name = str(mutant)
			mutant_label = self.__get_label__(mutant)
			name_nodes[mutant_name] = mutant_label

			## 1-2. generate the labels for each node
			for node in execution.get_nodes():
				attribute = node.get_attribute()
				attr_name = str(attribute)
				attr_label = self.__get_label__(attribute)
				name_nodes[attr_name] = attr_label

		## 2. it generates the edges that connect the mutation and annotations
		name_edges = dict()
		for execution in executions:
			execution: SymExecution
			pred_node, kill_node = None, None
			for node in execution.get_nodes():
				node: SymExecutionNode
				if pred_node is not None:
					parent = str(pred_node.get_attribute())
					child = str(node.get_attribute())
					if not (parent in name_edges):
						name_edges[parent] = set()
					if child != parent:
						name_edges[parent].add(child)
				pred_node = node
				if node.get_attribute().get_logic_type() == "kill_muta":
					kill_node = node
			if kill_node is not None:
				parent = str(execution.get_mutant())
				child = str(kill_node.get_attribute())
				if not (parent in name_edges):
					name_edges[parent] = set()
				name_edges[parent].add(child)

		## 3. generate the directed graph and visualize them onto the pdf file
		self.__visualize__(name_nodes, name_edges, o_directory, file_name)
		return


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
		if self.symb_value is None:
			sym_value = "n@null"
		else:
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

	@staticmethod
	def parse(document: CDocument, word: str):
		"""
		:param document:
		:param word:
		:return:
		"""
		program = document.get_program()
		project = document.get_project()
		items = word.split('$')
		logic_type = items[0].strip()
		exec_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
		unit_token = jcbase.CToken.parse(items[2].strip()).get_token_value()
		execution = program.function_call_graph.get_execution(exec_token[0], exec_token[1])
		store_unit = program.cir_tree.get_cir_node(unit_token)
		symb_token = jcbase.CToken.parse(items[3].strip()).get_token_value()
		if symb_token is None:
			symb_value = None
		else:
			symb_value = project.sym_tree.get_sym_node(items[3].strip())
		return CirAnnotation(logic_type, execution, store_unit, symb_value)

	def evaluate(self, document: CDocument, used_tests):
		"""
		:param document:
		:param used_tests:
		:return: result, killed, alives, confidence(%)
		"""
		killed, alives = 0, 0
		for execution in document.exec_space.get_executions():
			execution: SymExecution
			if self in execution.get_annotations():
				mutant = execution.get_mutant()
				if mutant.get_result().is_killed_in(used_tests):
					killed += 1
				else:
					alives += 1
		if killed >= alives:
			result = True
		else:
			result = False
		confidence = 0.0
		if alives > 0:
			confidence = alives / (killed + alives)
		confidence = int(confidence * 1000000) / 10000.0
		return result, killed, alives, confidence


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
		self.__parse__(file_path)
		return

	def __parse__(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					words = line.split('\t')
					parent = self.get_annotation(words[0].strip())
					for k in range(1, len(words)):
						child = self.get_annotation(words[k].strip())
						if child != parent:
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
		if not (word in self.annotations):
			annotation = CirAnnotation.parse(self.document, word)
			self.annotations[word] = annotation
		annotation = self.annotations[word]
		annotation: CirAnnotation
		return annotation

	def __len__(self):
		"""
		:return: the number of nodes under the tree
		"""
		return len(self.annotations)


class SymExecutionNode:
	"""
	It denotes a node in symbolic execution for killing mutation.
	"""

	def __init__(self, attribute: CirAnnotation, annotations):
		"""
		:param execution: 	the symbolic execution where the node is defined
		:param attribute: 	the header attribute representing execution node
		:param annotations:	the set of annotations, defined within this node
		"""
		self.attribute = attribute
		self.annotations = list()
		for annotation in annotations:
			annotation: CirAnnotation
			self.annotations.append(annotation)
		return

	def get_attribute(self):
		"""
		:return: the header attribute representing execution node
		"""
		return self.attribute

	def get_annotations(self):
		"""
		:return: the set of annotations, defined within this node
		"""
		return self.annotations


class SymExecution:
	"""
	It denotes an symbolic execution path for killing a mutation.
	"""

	def __init__(self, space, eid: int, mutant: jcmuta.Mutant, test: jcmuta.TestCase, nodes):
		"""
		:param space: 	the space where the symbolic execution is defined
		:param eid: 	the unique integer ID of execution path for space
		:param mutant: 	the mutant to be killed by the execution procedure
		:param test: 	the test case used or None if the execution path is static
		:param nodes: 	the set of SymExecutionNode created from the line
		"""
		space: SymExecutionSpace
		self.space = space
		self.eid = eid
		self.mutant = mutant
		self.test = test
		self.annotations = set()
		self.nodes = list()
		for index in range(0, len(nodes)):
			node = nodes[index]
			node: SymExecutionNode
			node.index = index
			self.nodes.append(node)
			for annotation in node.get_annotations():
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

	def get_nodes(self):
		"""
		:return: the sequence of nodes for cir-mutation path
		"""
		return self.nodes

	def get_annotations(self):
		return self.annotations


class SymExecutionSpace:
	"""
	The set of mutants to their corresponding symbolic execution.
	"""

	def __init__(self, document: CDocument, file_path: str):
		"""
		:param document:
		:param file_path: xxx.stp
		"""
		self.document = document
		self.executions = list()
		self.muta_execs = dict()
		self.__load__(file_path)
		return

	def __read__(self, line: str):
		"""
		:param line: mid tid {attribute {annotation}+ ;}+
		:return: SymExecution parsed from line or None
		"""
		line = line.strip()
		if len(line) > 0:
			## 1. get the library data-base from document
			project = self.document.get_project()
			anot_tree = self.document.annotation_tree
			items = line.split('\t')

			## 2. parse the mutant and test case
			muta_token = jcbase.CToken.parse(items[0].strip()).get_token_value()
			test_token = jcbase.CToken.parse(items[1].strip()).get_token_value()
			mutant = project.muta_space.get_mutant(muta_token)
			if test_token is None:
				test = None
			else:
				test = project.test_space.get_test_case(test_token)
			words = items[2: ]

			## 3. create the nodes list for further analysis
			buffer, nodes = list(), list()
			for word in words:
				word = word.strip()
				if len(word) == 0:
					continue
				elif word != ';':
					buffer.append(word)
				else:
					attribute = anot_tree.get_annotation(buffer[0])
					annotations = set()
					for k in range(1, len(buffer)):
						annotations.add(anot_tree.get_annotation(buffer[k]))
					node = SymExecutionNode(attribute, annotations)
					nodes.append(node)
					buffer.clear()

			## 4. generate the symbolic execution finally
			return SymExecution(self, len(self.executions), mutant, test, nodes)
		return None

	def __save__(self, execution: SymExecution):
		"""
		:param execution:
		:return: it saves the execution into the library space
		"""
		if not (execution is None):
			self.executions.append(execution)
			mutant = execution.get_mutant()
			if not (mutant in self.muta_execs):
				self.muta_execs[mutant] = list()
			self.muta_execs[mutant].append(execution)
		return

	def __load__(self, file_path: str):
		"""
		:param file_path:
		:return:
		"""
		with open(file_path, 'r') as reader:
			for line in reader:
				line = line.strip()
				if len(line) > 0:
					self.__save__(self.__read__(line))
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


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features"
	impa_path = "/home/dzt2/Development/Data/zexp/impacts"
	print_condition, print_number, alive_number = False, 12, 8
	for file_name in os.listdir(root_path):
		## 1. print the summaries of the c-document and its project, program.
		print("Testing on", file_name)
		c_directory = os.path.join(root_path, file_name)
		c_document = CDocument(c_directory, file_name)
		exec_space = c_document.get_execution_space()
		print("\t1.", file_name, "loads", len(exec_space.get_mutants()), "mutants used and",
			  len(exec_space.get_executions()), "symbolic instance paths annotated with",
			  len(c_document.get_annotation_tree().get_annotations()), "annotations.")

		## 2. remove all the files from the directory for graph visualization
		o_directory = impa_path + "/" + file_name
		if not os.path.exists(o_directory):
			os.mkdir(o_directory)
		for o_file in os.listdir(o_directory):
			o_file_path = os.path.join(o_directory, o_file)
			os.remove(o_file_path)

		## 2. print the symbolic execution and corresponding annotations in Console.
		if print_condition:
			for sym_execution in exec_space.get_executions():
				print("\tPath[{}]: Killing mutant#{} using {} annotations in.".
					  format(sym_execution.get_eid(),
							 sym_execution.get_mutant().get_muta_id(),
							 len(sym_execution.get_annotations())))
				for annotation in sym_execution.get_annotations():
					print("\t\t--> {}[{}]({}, {})".format(annotation.get_logic_type(),
														  annotation.get_execution(),
														  annotation.get_store_unit().get_cir_code(),
														  annotation.get_symb_value()))

		## 3. print the symbolic execution trees for a set of random selected mutants
		if print_number > 0:
			rand_mutants = set()
			while len(rand_mutants) < print_number:
				rand_mutant = jcbase.rand_select(c_document.get_project().muta_space.get_mutants())
				rand_mutant: jcmuta.Mutant
				rand_mutants.add(rand_mutant)
			postfix, mutants_executions = ".attr", set()
			for rand_mutant in rand_mutants:
				mutant_executions = c_document.exec_space.get_executions_of(rand_mutant)
				c_document.visualize_execution_graphs(mutant_executions, o_directory,
													  file_name + postfix + "." + str(rand_mutant.get_muta_id()))
				for mutant_execution in mutant_executions:
					mutant_execution: SymExecution
					mutants_executions.add(mutant_execution)
			c_document.visualize_execution_graphs(mutants_executions, o_directory, file_name + postfix)
			print("\t2. Print the attribute trees for", len(rand_mutants), "mutations in", file_name)

		## 4. print the annotation trees for randomly selected undetected mutants
		if alive_number > 0:
			alive_mutants = set()
			for mutant in c_document.get_project().muta_space.get_mutants():
				mutant: jcmuta.Mutant
				if not mutant.get_result().is_killed_in(None):
					alive_mutants.add(mutant)
			if len(alive_mutants) > alive_number:
				select_mutants = set()
				while len(select_mutants) < alive_number:
					mutant = jcbase.rand_select(alive_mutants)
					mutant: jcmuta.Mutant
					select_mutants.add(mutant)
				alive_mutants = select_mutants

			if len(alive_mutants) > 0:
				postfix, mutants_annotations = ".anot", set()
				for mutant in alive_mutants:
					mutant_annotations = set()
					for execution in c_document.exec_space.get_executions_of(mutant):
						execution: SymExecution
						for annotation in execution.get_annotations():
							mutant_annotations.add(annotation)
							mutants_annotations.add(annotation)
					c_document.visualize_annotation_trees(mutant_annotations, o_directory,
														  file_name + postfix + "." + str(mutant.get_muta_id()), None)
				c_document.visualize_annotation_trees(mutants_annotations, o_directory, file_name + postfix, None)
			print("\t3. Print the annotation trees for", len(alive_mutants), "alive mutations in", file_name)

		## 5. inform the users to reach the end of testing of each mutation project
		print()
	print("Testing end for all.")

