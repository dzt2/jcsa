"""This file defines the data models of abstract state patterns for connection."""


import os
from collections import deque
from typing import TextIO
import com.jcsa.pymuta.libs.base as jcbase
import com.jcsa.pymuta.libs.test as jctest
import com.jcsa.pymuta.encode as jcencode


class CirStatePattern:
	"""
	It defines the pattern of CirAbstractState
	"""

	def __init__(self, document: jcencode.MerDocument, features, executions):
		"""
		:param document: 	the document where the pattern is defined and interpreted
		:param features: 	the set of integer features to encode CirAbstractState(s)
		:param executions: 	the set of MerExecution(s) being matched to this pattern
		"""
		self.document = document
		self.features = document.get_state_space().normal(features)
		if executions is None:
			executions = document.get_execution_space().get_executions()
		self.executions = set()
		for execution in executions:
			execution: jcencode.MerExecution
			if self.__matched__(execution):
				self.executions.add(execution)
		return

	def __matched__(self, execution: jcencode.MerExecution):
		"""
		:param execution:
		:return: whether the execution matches with this state
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __consist__(self, test):
		"""
		:param test:
		:return: whether the test produces identical outputs on all the matched executions in the pattern
		"""
		result = None
		for execution in self.executions:
			if result is None:
				result = execution.get_mutant().is_killed_by(test)
			elif result != execution.get_mutant().is_killed_by(test):
				return False
			else:
				continue
		return True

	def get_document(self):
		"""
		:return: the document where this pattern is defined
		"""
		return self.document

	def get_features(self):
		"""
		:return: the set of features encoding states being incorporated
		"""
		return self.features

	def get_states(self):
		"""
		:return: the set of states encoded by this pattern's features
		"""
		return self.document.get_state_space().decode(self.features)

	def get_executions(self):
		"""
		:return: the set of executions matched with this pattern
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the mutants of which executions match with this pattern
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def kac_measure(self, tests):
		"""
		:param tests: 	the set of MerTestCase or int, or None for all
		:return: 		killed, alive, confidence
						---	killed:		the number of executions killed by tests
						---	alive:		the number of executions not killed by tests
						---	confidence:	the likelihood to believe the prediction is right
		"""
		killed, alive, confidence = 0, 0, 0.0
		for execution in self.executions:
			if execution.get_mutant().is_killed_in(tests):
				killed += 1
			else:
				alive += 1
		if killed + alive > 0:
			confidence = max(killed, alive) / (killed + alive)
		return killed, alive, confidence

	def lsc_measure(self, tests):
		"""
		:param tests: 	the set of MerTestCase or int, or None for all
		:return: 		length, support, confidence
						---	length:		the length of the pattern's features
						---	support:	the number of executions that are not killed by test
						--- confidence:	the likelihood of the supports in all the executions
		"""
		killed, alive, _ = self.kac_measure(tests)
		length = len(self.features)
		support, confidence = alive, 0.0
		if support > 0:
			confidence = support / (killed + alive)
		return length, support, confidence

	def cnl_measure(self, tests):
		"""
		:param tests: 	the set of MerTestCase or int, or None for all
		:return:		consistence, non_consistence, likelihood
						---	consistence: the number of tests to produce identical results on all executions in pattern
						---	non_consistence: number of tests to produce not same results for all executions in pattern
						---	likelihood:		 the likelihoods of any test in the given set to produce identical results
		"""
		if tests is None:
			tests = self.document.get_test_space().get_test_cases()
		consistence, non_consistence, likelihood = 0, 0, 0.0
		for test in tests:
			if self.__consist__(test):
				consistence += 1
			else:
				non_consistence += 1
		if consistence > 0:
			likelihood = consistence / (consistence + non_consistence)
		return consistence, non_consistence, likelihood


class CirStatePatternNode:
	"""
	the node to encode a pattern of CirAbstractState
	"""

	def __init__(self, tree, parent, feature: int):
		"""
		:param tree: 	the tree where this node is specified
		:param parent: 	the parent of this tree node
		:param feature: the feature incorporated in the node
		"""
		tree: CirStatePatternTree
		self.tree = tree
		self.parent = parent
		self.feature = feature
		self.children = list()
		self.pattern = self.__new_pattern__()
		return

	def __new_pattern__(self):
		node = self
		features = set()
		while not (node is None):
			features.add(node.feature)
			node = node.get_parent()
		if self.parent is None:
			return CirStatePattern(self.tree.get_document(), features, None)
		else:
			return CirStatePattern(self.tree.get_document(), features, self.get_parent().get_pattern().get_executions())

	def get_tree(self):
		"""
		:return: the tree where this node is defined
		"""
		return self.tree

	def is_root(self):
		return self.parent is None

	def is_leaf(self):
		return len(self.children) == 0

	def get_parent(self):
		"""
		:return: the parent of this node or None
		"""
		if self.parent is None:
			return None
		self.parent: CirStatePatternNode
		return self.parent

	def get_children(self):
		"""
		:return: the children nodes created under this one
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children created under this node
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child under this node
		"""
		child = self.children[k]
		child: CirStatePatternNode
		return child

	def get_pattern(self):
		"""
		:return: the pattern of this tree node
		"""
		return self.pattern

	def ext_child(self, feature: int):
		"""
		:param feature:
		:return: 	1. invalid or existing feature: return self
					2. existing child: return existing ones
					3. otherwise: create a new child and return
		"""
		if feature <= self.feature:
			return self
		else:
			for child in self.children:
				child: CirStatePatternNode
				if child.feature == feature:
					return child
			child = CirStatePatternNode(self.tree, self, feature)
			self.children.append(child)
			return child


class CirStatePatternTree:
	"""
	the structural tree of CirAbstractState patterns
	"""

	def __init__(self, document: jcencode.MerDocument):
		self.document = document
		self.root = CirStatePatternNode(self, None, -1)
		return

	def get_document(self):
		return self.document

	def get_root(self):
		return self.root

	def get_node(self, features):
		"""
		:param features: the set of integer features encoding CirAbstractState(s)
		:return: the unique node of pattern referring to the states in the tree
		"""
		node = self.root
		features = self.document.get_state_space().normal(features)
		for feature in features:
			node = node.ext_child(feature)
		return node

	def get_nodes(self):
		"""
		:return: all the nodes created under this tree
		"""
		queue = deque()
		nodes = set()
		queue.append(self.root)
		while len(queue) > 0:
			node = queue.popleft()
			node: CirStatePatternNode
			for child in node.get_children():
				queue.append(child)
			nodes.add(node)
		return nodes

	def get_patterns_of(self, inputs):
		"""
		:param inputs: the set of CirStatePattern or CirStatePatternNode
		:return:
		"""
		patterns = set()
		if inputs is None:
			nodes = self.get_nodes()
			for node in nodes:
				patterns.add(node.get_pattern())
		else:
			for _input in inputs:
				if isinstance(_input, CirStatePatternNode):
					patterns.add(_input.get_pattern())
				elif isinstance(_input, CirStatePattern):
					patterns.add(_input)
				else:
					pass
		return patterns

	def filter_patterns(self, inputs, max_length: int, min_support: int, min_confidence: float, tests):
		"""
		:param inputs:
		:param max_length:
		:param min_support:
		:param min_confidence:
		:param tests:
		:return: the set of patterns created in the tree that match the input parameters
		"""
		patterns = self.get_patterns_of(inputs)
		good_patterns = set()
		for pattern in patterns:
			length, support, confidence = pattern.lsc_measure(tests)
			if (length <= max_length) and (support >= min_support) and (confidence >= min_confidence):
				good_patterns.add(pattern)
		return good_patterns

	def minimal_patterns(self, inputs):
		"""
		:param inputs: the set of CirStatePattern or CirStatePatternNode
		:return:
		"""
		## 1. collect all the executions in the input patterns
		all_executions = set()
		patterns = self.get_patterns_of(inputs)
		for pattern in patterns:
			for execution in pattern.get_executions():
				all_executions.add(execution)

		## 2. selected_patterns
		minimal_patterns = set()
		while (len(all_executions) > 0) and (len(patterns) > 0):
			rand_pattern = jcbase.rand_select(patterns)
			rand_pattern: CirStatePattern
			patterns.remove(rand_pattern)

			com_executions = rand_pattern.get_executions() & all_executions
			if len(com_executions) > 0:
				minimal_patterns.add(rand_pattern)
				all_executions = all_executions - com_executions

			rem_patterns = set()
			for pattern in patterns:
				pat_executions = pattern.get_executions()
				pat_executions = pat_executions & all_executions
				if len(pat_executions) == 0:
					rem_patterns.add(pattern)
			for pattern in rem_patterns:
				patterns.remove(pattern)

		## 3. return the minimal set
		return minimal_patterns


class CirStatePatternWriter:
	"""
	It writes the information of state patterns to specified file.
	"""

	def __init__(self, c_document: jctest.CDocument, m_document: jcencode.MerDocument):
		self.c_document = c_document
		self.m_document = m_document
		self.__writer__ = None
		self.max_code_length = 96
		return

	def __open__(self, writer: TextIO, title: str):
		self.__writer__ = writer
		self.__writer__.write(title)
		return

	def __output__(self, text: str):
		self.__writer__: TextIO
		self.__writer__.write(text)
		return

	def __close__(self, end_file: str):
		self.__writer__.write(end_file)
		self.__writer__ = None
		return

	@staticmethod
	def __percent__(value: float):
		return int(value * 1000000) / 10000.0

	def write_patterns_to_mutations(self, patterns, tests, file_path: str):
		"""
		:param patterns: the set of CirStatePattern or CirStatePatternNode
		:param tests:
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.__open__(writer, "Information for Each Pattern and its Mutations & States\n")

			for pattern in patterns:
				pattern: CirStatePattern
				self.__output__("\n#BEG\n")

				self.__output__("\tPattern Identifier\t{}\n".format(str(pattern)))
				killed, alive, prediction = pattern.kac_measure(tests)
				length, support, confidence = pattern.lsc_measure(tests)
				prediction = CirStatePatternWriter.__percent__(prediction)
				confidence = CirStatePatternWriter.__percent__(confidence)
				self.__output__("\tKilled = {}\tAlived = {}\tCorrectness = {}%\n".format(killed, alive, prediction))
				self.__output__("\tLength = {}\tSupport = {}\tConfidence = {}%\n".format(length, support, confidence))
				self.__output__("\n")

				self.__output__("\tMID\tRESULT\tCLASS\tOPRT\tLINE\tCODE\tPARAMETER\n")
				for mutant in pattern.get_mutants():
					c_mutant = mutant.find_source(self.c_document)
					mid = c_mutant.get_muta_id()
					res = c_mutant.get_result().is_killed_in(tests)
					m_class = c_mutant.get_mutation().get_mutation_class()
					operator = c_mutant.get_mutation().get_mutation_operator()
					parameter = c_mutant.get_mutation().get_parameter()
					location = c_mutant.get_mutation().get_location()
					c_line = location.line_of(False)
					c_code = location.get_code(True)
					if len(c_code) > self.max_code_length:
						c_code = c_code[0: self.max_code_length].strip() + "..."
					self.__output__("\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n".
									format(mid, res, m_class, operator, c_line, c_code, parameter))
				self.__output__("\n")

				self.__output__("\tSTID\tCLASS\tEXEC\tSTATEMENT\tLOCATION\tLOPERAND\tROPERAND\n")
				for state in pattern.get_states():
					stid = state.get_stid()
					c_state = state.find_source(self.c_document)
					category = c_state.get_category()
					execution = c_state.get_execution()
					statement = execution.get_statement().get_cir_code()
					location = c_state.get_location().get_cir_code()
					loperand = c_state.get_loperand().get_code()
					roperand = c_state.get_roperand().get_code()
					self.__output__("\t{}\t{}\t{}\t\"{}\"\t{}\t[{}]\t[{}]\n".
									format(stid, category, execution, statement, location, loperand, roperand))
				self.__output__("#END\n")

			self.__close__("\nEnd-of-File: {}\n".format(file_path))
		return

	def write_patterns_of_summarize(self, patterns, tests, file_path: str):
		"""
		:param patterns:
		:param tests:
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.__open__(writer, "Information for Summarizing State Patterns\n\n")

			## 1. generate the optimization metrics
			all_executions, cov_executions, mat_executions, unc_executions = set(), set(), set(), set()
			for execution in self.m_document.get_execution_space().get_executions():
				if execution.get_mutant().is_killed_in(tests):
					continue
				else:
					all_executions.add(execution)
			for pattern in patterns:
				pattern: CirStatePattern
				for execution in pattern.get_executions():
					cov_executions.add(execution)
			mat_executions = cov_executions & all_executions
			unc_executions = all_executions - mat_executions
			self.__output__("Efficiency Metrics for {} Patterns.\n".format(len(patterns)))
			self.__output__("\tUNK_EXECS = {}\tCOV_EXECS = {}\n".format(len(all_executions), len(cov_executions)))
			self.__output__("\tMAT_EXECS = {}\tUNC_EXECS = {}\n".format(len(mat_executions), len(unc_executions)))
			precision, recall, f1_score = 0.0, 0.0, 0.0
			if len(mat_executions) > 0:
				precision = len(mat_executions) / len(cov_executions)
				recall = len(mat_executions) / len(all_executions)
				f1_score = 2 * precision * recall / (precision + recall)
			precision = CirStatePatternWriter.__percent__(precision)
			recall = CirStatePatternWriter.__percent__(recall)
			f1_score = CirStatePatternWriter.__percent__(f1_score) / 100
			self.__output__("\tPREC = {}%\tRECL = {}%\tF1SC = {}\n".format(precision, recall, f1_score))
			optimize_rate = len(patterns) / len(cov_executions)
			missed_rate = len(unc_executions) / len(all_executions)
			optimize_rate = CirStatePatternWriter.__percent__(optimize_rate)
			missed_rate = CirStatePatternWriter.__percent__(missed_rate)
			self.__output__("\tNUMB = {}\tOPMR = {}%\tMISR = {}%\n".format(len(patterns), optimize_rate, missed_rate))
			self.__output__("\n")

			## 2. print the patterns and their measurements
			self.__output__("Metrics of the Summary of Selected Patterns\n")
			self.__output__("\tPID\tLENGTH\tMUTANTS\tKILLED\tALIVED\tCONFIDENCE(%)\n")
			for pattern in patterns:
				pid = str(pattern)
				length, support, confidence = pattern.lsc_measure(tests)
				killed, alive, _ = pattern.kac_measure(tests)
				mutants = len(pattern.get_mutants())
				confidence = CirStatePatternWriter.__percent__(confidence)
				self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\n".format(pid, length, mutants, killed, alive, confidence))
			self.__output__("\n")

			## 3. print the uncovered mutation information
			self.__output__("Summary of the Undetected Mutations from.\n")
			self.__output__("\tMID\tRESULT\tCLASS\tOPERATOR\tLINE\tCODE\tPARAMETER\n")
			for execution in unc_executions:
				mutant = execution.get_mutant()
				c_mutant = mutant.find_source(self.c_document)
				mid = c_mutant.get_muta_id()
				res = c_mutant.get_result().is_killed_in(tests)
				m_class = c_mutant.get_mutation().get_mutation_class()
				operator = c_mutant.get_mutation().get_mutation_operator()
				parameter = c_mutant.get_mutation().get_parameter()
				location = c_mutant.get_mutation().get_location()
				c_line = location.line_of(False)
				c_code = location.get_code(True)
				if len(c_code) > self.max_code_length:
					c_code = c_code[0: self.max_code_length].strip() + "..."
				self.__output__("\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n".
								format(mid, res, m_class, operator, c_line, c_code, parameter))
			self.__output__("\n")

			self.__close__("\nEnd-of-File: {}\n".format(file_path))
		return


def do_mine_patterns(m_document: jcencode.MerDocument, tests, max_length: int, min_support: int, min_confidence: float):
	"""
	:param m_document:
	:param tests:
	:param max_length:
	:param min_support:
	:param min_confidence:
	:return:
	"""
	## 1. select the initial features from undetected executions
	init_features = set()
	for execution in m_document.get_execution_space().get_executions():
		if execution.get_mutant().is_killed_in(tests):
			continue
		else:
			for feature in execution.get_features():
				init_features.add(feature)

	## 2. perform exhaustive generation of structural state patterns
	p_tree = CirStatePatternTree(m_document)
	counter, total = 0, len(init_features)
	for feature in init_features:
		node = p_tree.get_node([feature])
		counter += 1
		if counter % 1000 == 0:
			length, support, confidence = node.get_pattern().lsc_measure(None)
			print("\t\tMine[{}/{}]\tSupport = {}; Confidence = {}".format(counter, total, support, confidence))

	## 3. select the good patterns from the p_tree
	patterns = p_tree.filter_patterns(None, max_length, min_support, min_confidence, tests)
	return patterns, p_tree.minimal_patterns(patterns)


def test_patterns_in(c_document: jctest.CDocument, m_document: jcencode.MerDocument, directory: str):
	"""
	:param c_document:
	:param m_document:
	:param directory:
	:return:
	"""
	max_length, min_support, min_confidence, tests = 1, 2, 0.65, None
	print("\t1. Perform Mining over {}, {}, {}".format(max_length, min_support, min_confidence))

	patterns, min_patterns = do_mine_patterns(m_document, None, max_length, min_support, min_confidence)
	print("\t2. Select {} patterns and {} minimal.".format(len(patterns), len(min_patterns)))

	writer = CirStatePatternWriter(c_document, m_document)
	file_name = m_document.get_name()
	writer.write_patterns_to_mutations(patterns, tests, os.path.join(directory, file_name + ".mpt"))
	writer.write_patterns_of_summarize(min_patterns, tests, os.path.join(directory, file_name + ".smt"))
	print("\t3. Write {} selected patterns to specified file".format(len(patterns)))
	return


def main(in_directory: str, ou_directory: str, pt_directory: str):
	for file_name in os.listdir(in_directory):
		directory = os.path.join(in_directory, file_name)
		c_document = jctest.CDocument(directory, file_name, "pdg")
		directory = os.path.join(ou_directory, file_name)
		m_document = jcencode.MerDocument(directory, file_name)
		print("Testing on", m_document.get_name())
		directory = os.path.join(pt_directory, file_name)
		if not os.path.exists(directory):
			os.mkdir(directory)
		test_patterns_in(c_document, m_document, directory)
		print()
	return


if __name__ == "__main__":
	i_directory = "/home/dzt2/Development/Data/zext/features"
	o_directory = "/home/dzt2/Development/Data/zext/encoding"
	p_directory = "/home/dzt2/Development/Data/zext/patterns"
	main(i_directory, o_directory, p_directory)

