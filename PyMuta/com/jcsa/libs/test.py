""" This file refines the model of symbolic execution along with condition for description. """


import os
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
			program = self.document.get_program()
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

	def write_impacts_graph(self, o_directory: str, file_name: str, mutants):
		"""
		:param o_directory: the directory where the xxx.pdf file will be generated
		:param mutants: the set of mutants of which cir-mutation nodes will be printed or None for all
		:return:
		"""
		## 1. capture the set of symbolic executions for printing
		executions = set()
		if mutants is None:
			for execution in self.executions:
				execution: SymExecution
				executions.add(execution)
		else:
			for mutant in mutants:
				mutant_executions = self.get_executions_of(mutant)
				for execution in mutant_executions:
					execution: SymExecution
					executions.add(execution)

		## 2. create the key and the corresponding label in nodes
		name_labels, name_edges = dict(), dict()
		for execution in executions:
			for node in execution.get_nodes():
				attribute = node.get_attribute()
				name = str(attribute)
				label = "C: {}\nE: {}\nU: {}\nT: {}\nV: {}".format(attribute.get_logic_type(), attribute.get_execution(),
																   attribute.get_store_unit(), attribute.store_unit.code,
																   attribute.get_symb_value())
				name_labels[name] = label
			mutant = execution.get_mutant()
			mutant_name = str(execution.get_mutant())
			mutant_result = mutant.get_result().is_killed_in(None)
			mutant_code = mutant.mutation.get_location().get_code(True)
			if len(mutant_code) > 32:
				mutant_code = mutant_code[0: 32].strip()
			mutant_line = mutant.mutation.get_location().line_of(False)
			mutant_label = "MID: {}[{}]\nC: {}\nO: {}\nL: [{}]#{}\nV: {}".format(mutant.get_muta_id(), mutant_result,
																				 mutant.mutation.get_mutation_class(),
																				 mutant.mutation.get_mutation_operator(),
																				 mutant_code, mutant_line,
																				 mutant.mutation.get_parameter())
			name_labels[mutant_name] = mutant_label

			node_list = execution.get_nodes()
			for k in range(0, len(node_list) - 1):
				parent = node_list[k].get_attribute()
				child = node_list[k + 1].get_attribute()
				pred_name = str(parent)
				post_name = str(child)
				if not (pred_name in name_edges):
					name_edges[pred_name] = set()
				if pred_name != post_name:
					name_edges[pred_name].add(post_name)
			last_name = str(node_list[-1].get_attribute())
			if not (last_name in name_edges):
				name_edges[last_name] = set()
			name_edges[last_name].add(mutant_name)

		## 3. create nodes and initialize the graph at first
		graph = graphviz.Digraph(name="Mutation Impacts Graph.")
		for name, label in name_labels.items():
			graph.node(name, label)

		## 4. link the nodes using execution sequence at all
		for pred_name, post_names in name_edges.items():
			for post_name in post_names:
				graph.edge(pred_name, post_name)

		## 5. visualize the directed graph for program impacts
		if not os.path.exists(o_directory):
			os.mkdir(o_directory)
		graph.render(filename=file_name, directory=o_directory, format="pdf")
		file_path = os.path.join(o_directory, file_name)
		os.remove(file_path)
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Data/zexp/features"
	impa_path = "/home/dzt2/Development/Data/zexp/impacts"
	print_condition, print_number = True, 6
	for file_name in os.listdir(root_path):
		print("Testing on", file_name)
		c_directory = os.path.join(root_path, file_name)
		c_document = CDocument(c_directory, file_name)
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

		rand_mutants = set()
		while len(rand_mutants) < print_number:
			rand_mutant = jcbase.rand_select(c_document.get_project().muta_space.get_mutants())
			rand_mutant: jcmuta.Mutant
			rand_mutants.add(rand_mutant)
		o_directory = impa_path + "/" + file_name
		for rand_mutant in rand_mutants:
			c_document.exec_space.write_impacts_graph(o_directory, file_name + "." + str(rand_mutant.get_muta_id()), [rand_mutant])
		c_document.exec_space.write_impacts_graph(o_directory, file_name, rand_mutants)
		print()
	print("Testing end for all.")

