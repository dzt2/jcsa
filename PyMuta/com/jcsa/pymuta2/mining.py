"""This file implements the pattern mining algorithm."""


import os
from typing import TextIO
import com.jcsa.pymuta2.libs.base as jcbase
import com.jcsa.pymuta2.libs.muta as jcmuta
import com.jcsa.pymuta2.encode as jcencode


class CirStatePatternRule:
	"""
	It defines the structural rule to describe the pattern of mutant execution features.
	"""

	def __init__(self, document: jcencode.MerDocument, features, executions):
		"""
		:param document: 	the document in which the pattern-rule is defined
		:param features: 	the set of integer features to describe this rule
		:param executions: 	the set of executions where matched-set is defined
		"""
		self.document = document
		self.features = self.document.get_state_space().normal(features)
		self.executions = set()
		if executions is None:
			executions = self.document.get_state_space().get_executions()
		for execution in executions:
			execution: jcencode.MerContextExecution
			if self.__matched__(execution):
				self.executions.add(execution)
		return

	def __matched__(self, execution: jcencode.MerContextExecution):
		"""
		:param execution:
		:return: 			whether the execution and its mutant match with this pattern-rule
		"""
		for feature in self.features:
			if not (feature in execution.get_features()):
				return False
		return True

	def __consist__(self, test):
		"""
		:param test: either MerTestCase or int
		:return: 	True if the test produces identical results on every execution matched with this pattern
		"""
		result = None
		for execution in self.executions:
			m_result = execution.get_mutant().is_killed_by(test)
			if result is None:
				result = m_result
			elif result != m_result:
				return False
		return len(self.executions) > 1

	def get_document(self):
		"""
		:return: the document in which the pattern-rule is defined
		"""
		return self.document

	def get_features(self):
		"""
		:return: the feature-vector to define this pattern-rule
		"""
		return self.features

	def get_states(self):
		"""
		:return: the set of states encoded by the pattern-rule's features
		"""
		return self.document.get_state_space().decode(self.features)

	def get_executions(self):
		"""
		:return: the set of execution instances that match with this rule
		"""
		return self.executions

	def get_mutants(self):
		"""
		:return: the set of mutant instances that match with this pattern
		"""
		mutants = set()
		for execution in self.executions:
			mutants.add(execution.get_mutant())
		return mutants

	def has_sample(self, key):
		"""
		:param key: either MerMutant or MerExecution
		:return:
		"""
		if key in self.executions:
			return True
		else:
			for execution in self.executions:
				if execution.get_mutant() == key:
					return True
			return False

	def __len__(self):
		return len(self.features)

	def __str__(self):
		return str(self.features)

	def k_measure(self, used_tests):
		"""
		:param used_tests: 	the set of MerTestCase or int, or None to specify the all
		:return: 			killed, alive, kill_ratio
							---	killed:	the number of executions killed by used tests
							---	alive:	the number of executions not killed by used tests
							---	kill_ratio:	the ratio of executions killed by used tests
		"""
		killed, alive = 0, 0
		for execution in self.executions:
			if execution.get_mutant().is_killed_in(used_tests):
				killed += 1
			else:
				alive += 1
		kill_ratio = 0.0
		if killed > 0:
			kill_ratio = killed / (killed + alive)
		return killed, alive, kill_ratio

	def s_measure(self, used_tests):
		"""
		:param used_tests: 	the set of MerTestCase or int, or None to specify the all
		:return: 			length, support, confidence
							---	length: 	the length of this pattern-rule (complexity)
							---	support:	the number of executions that survive from used tests
							---	confidence:	the ratio of executions surviving from used tests
		"""
		length, support, confidence = len(self.features), 0, 0.0
		for execution in self.executions:
			if execution.get_mutant().is_killed_in(used_tests):
				pass
			else:
				support += 1
		if support > 0:
			confidence = support / len(self.executions)
		return length, support, confidence

	def c_measure(self, used_tests):
		"""
		:param used_tests:	the set of MerTestCase or int, or None to specify the all
		:return:			consist, in_consist, consist_ratio
							---	consist:	the number of tests that produce identical results on every execution
							---	in_consist:	the number of tests producing different results on all the executions
							---	consist_ratio:	the ratio of tests that keep consistent results on the executions
		"""
		consist, in_consist, consist_ratio = 0, 0, 0.0
		if used_tests is None:
			used_tests = self.document.get_test_space().get_test_cases()
		for test in used_tests:
			if self.__consist__(test):
				consist += 1
			else:
				in_consist += 1
		if consist > 0:
			consist_ratio = consist / (consist + in_consist)
		return consist, in_consist, consist_ratio


class CirStatePatternNode:
	"""
	It denotes a node in structural pattern tree for mining.
	"""

	def __init__(self, tree, parent, feature: int):
		"""
		:param tree: 		the tree in which the node is created
		:param parent: 		the parent of this node or None for root
		:param feature: 	the feature annotated on the parent edge
		"""
		tree: CirStatePatternTree
		self.tree = tree
		self.parent = parent
		self.feature = feature
		self.children = list()
		self.rule = self.__rule__()
		self.get_tree().maps[self.rule] = self
		return

	def __rule__(self):
		"""
		:return: it generates the pattern-rule that this node specifies
		"""
		## 	1. collect the features from this node until root
		node, features = self, set()
		while not node.is_root():
			features.add(node.feature)
			node = node.get_parent()

		##	2. collect the parent execution set for generation
		if self.is_root():
			executions = self.get_tree().get_document().get_state_space().get_executions()
		else:
			parent = self.get_parent()
			executions = parent.get_rule().get_executions()

		## 	3. generate the pattern-rule in the parent context
		return CirStatePatternRule(self.get_tree().get_document(), features, executions)

	def get_tree(self):
		return self.tree

	def is_root(self):
		return self.parent is None

	def get_parent(self):
		"""
		:return: the parent of this node or None for root
		"""
		if self.parent is None:
			return None
		self.parent: CirStatePatternNode
		return self.parent

	def get_rule(self):
		"""
		:return: the pattern-rule of this node
		"""
		return self.rule

	def __str__(self):
		return str(self.get_rule().get_features())

	def get_children(self):
		"""
		:return: the children created under this node
		"""
		return self.children

	def number_of_children(self):
		"""
		:return: the number of children under this node
		"""
		return len(self.children)

	def get_child(self, k: int):
		"""
		:param k:
		:return: the kth child under this children
		"""
		child = self.children[k]
		child: CirStatePatternNode
		return child

	def ext_child(self, feature: int):
		"""
		:param feature: integer feature to extend child from this node
		:return:		1) feature <= self.feature: return self
						2) otherwise, return the unique child referring to the feature
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
	the structural pattern tree for mining patterns of execution states
	"""

	def __init__(self, document: jcencode.MerDocument):
		self.document = document
		self.maps = dict()		##	CirStatePatternRule --> CirStatePatternNode
		self.root = CirStatePatternNode(self, None, -1)
		self.maps[self.root.get_rule()] = self.root
		return

	def get_document(self):
		"""
		:return: the document in which the tree is defined
		"""
		return self.document

	def get_root(self):
		"""
		:return: the root node of this tree
		"""
		return self.root

	def get_node(self, features):
		"""
		:param features: 	the set of integer features to generate unique node
		:return: 			the unique node specified by the input features
		"""
		features = self.document.get_state_space().normal(features)
		node = self.root
		for feature in features:
			node = node.ext_child(feature)
		return node

	def get_nodes(self):
		"""
		:return: all the nodes created under this tree
		"""
		return self.maps.values()

	def get_rules(self):
		"""
		:return: the pattern-rules created under this tree
		"""
		return self.maps.keys()


class CirStatePatternInputs:
	"""
	The module to preserve the input parameters for mining.
	"""

	def __init__(self, document: jcencode.MerDocument, max_length: int,
				 min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document: 		the document in which the patterns will be defined and generated
		:param max_length: 		the maximal length of the patterns to be selected and generated
		:param min_support: 	the minimal support required for each filtered pattern produced
		:param min_confidence: 	the minimal confidence required to be achieved by selected node
		:param max_confidence: 	the maximal confidence to stop pattern traversal and generation
		"""
		self.p_tree = CirStatePatternTree(document)
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		return

	## rule-getter

	def get_document(self):
		return self.p_tree.get_document()

	def get_tree(self):
		return self.p_tree

	def get_rule(self, features):
		"""
		:param features:
		:return: the unique pattern-rule referring to the specified features
		"""
		return self.p_tree.get_node(features).get_rule()

	def get_rules(self, inputs=None):
		"""
		:param inputs: None for all, or the set of CirStatePatternRule|CirStatePatternNode|features
		:return:
		"""
		rules = set()
		if inputs is None:
			for rule in self.p_tree.get_rules():
				rules.add(rule)
		else:
			for input_element in inputs:
				if isinstance(input_element, CirStatePatternNode):
					rules.add(input_element.get_rule())
				elif isinstance(input_element, CirStatePatternRule):
					input_element: CirStatePatternRule
					rules.add(input_element)
				elif isinstance(input_element, set) or isinstance(input_element, list):
					rules.add(self.get_rule(input_element))
				else:
					continue
		return rules

	## parameters

	def get_max_length(self):
		"""
		:return: the maximal length of the patterns to be selected and generated
		"""
		return self.max_length

	def get_min_support(self):
		"""
		:return: the minimal support required for each filtered pattern produced
		"""
		return self.min_support

	def get_min_confidence(self):
		"""
		:return: the minimal confidence required to be achieved by selected node
		"""
		return self.min_confidence

	def get_max_confidence(self):
		"""
		:return: the maximal confidence to stop pattern traversal and generation
		"""
		return self.max_confidence

	## selections

	def filter_rules(self, inputs, used_tests):
		"""
		:param inputs: 	the set of CirStatePatternRule or CirStatePatternNode or integer feature vector; or None for all
		:param used_tests:	the set of MerTestCase(s) or integers to evaluate the length-support-confidence of the rules
		:return: 		the set of (inputs)-CirStatePatternRule(s) that match with the minimal parameters required among
		"""
		rules = self.get_rules(inputs)
		good_rules = set()
		for rule in rules:
			length, support, confidence = rule.s_measure(used_tests)
			if (length <= self.max_length) and (support >= self.min_support) and (confidence >= self.min_confidence):
				good_rules.add(rule)
		return good_rules

	def select_rules(self, inputs, key):
		"""
		:param inputs:	the set of CirStatePatternRule or CirStatePatternNode or integer feature vector; or None for all
		:param key:		the MerMutant or MerExecution to which CirStatePatternRule(s) correspond; or None to select none
		:return:		the set of CirStatePatternRule(s) that correspond to the given set of MerMutant and MerExecution
		"""
		selected_rules = set()
		if not (key is None):
			rules = self.get_rules(inputs)
			for rule in rules:
				if rule.has_sample(key):
					selected_rules.add(rule)
		return selected_rules

	def re_map_rules(self, inputs):
		"""
		:param inputs:	the set of CirStatePatternRule or CirStatePatternNode or integer feature vector; or None for all
		:return:		the mapping from MerMutant to the CirStatePatternRule(s) that match with the specified mutations
		"""
		re_dict = dict()
		rules = self.get_rules(inputs)
		for rule in rules:
			for mutant in rule.get_mutants():
				if not (mutant in re_dict):
					re_dict[mutant] = set()
				re_dict[mutant].add(rule)
		return re_dict

	def s_eval_rules(self, inputs, used_tests):
		"""
		:param inputs: 	the set of CirStatePatternRule or CirStatePatternNode or integer feature vector; or None for all
		:param used_tests:	the set of MerTestCase(s) or integers to evaluate the length-support-confidence of the rules
		:return: 		the mapping from CirStatePatternRule(s) to the metrics of [length, support, confidence]
		"""
		rules = self.get_rules(inputs)
		edict = dict()
		for rule in rules:
			length, support, confidence = rule.s_measure(used_tests)
			edict[rule] = (length, support, confidence)
		return edict

	def sorted_rules(self, inputs, used_tests):
		"""
		:param inputs: 	the set of CirStatePatternRule or CirStatePatternNode or integer feature vector; or None for all
		:param used_tests:	the set of MerTestCase(s) or integers to evaluate the length-support-confidence of the rules
		:return:		the list of CirStatePatternRule(s) that are sorted according to their supports (then confidence)
		"""
		## 1. evaluate each input and connect with metrics
		eval_dict = self.s_eval_rules(inputs, used_tests)

		## 2. sorting the rules according to their support
		supp_list, supp_dict = list(), dict()
		for rule, metrics in eval_dict.items():
			support = metrics[1]
			if not (support in supp_dict):
				supp_list.append(support)
				supp_dict[support] = set()
			supp_dict[support].add(rule)
		supp_list.sort(reverse=True)

		## 3. sort the rules according to their confidence
		sorted_rule_list = list()
		for support in supp_list:
			## 3-1. capture the support-rules
			supp_rules = supp_dict[support]
			conf_list, conf_dict = list(), dict()

			## 3-2. sort the local confidence
			for rule in supp_rules:
				rule: CirStatePatternRule
				metrics = eval_dict[rule]
				confidence = metrics[2]
				confidence = int(confidence * 10000)
				if not (confidence in conf_dict):
					conf_list.append(confidence)
					conf_dict[confidence] = set()
				conf_dict[confidence].add(rule)
			conf_list.sort(reverse=True)

			## 3-3. update the final sorted list
			for confidence in conf_list:
				conf_rules = conf_dict[confidence]
				for rule in conf_rules:
					rule: CirStatePatternRule
					sorted_rule_list.append(rule)

		## 4. return the final output sorted rules
		return sorted_rule_list

	def reduce_rules(self, inputs):
		"""
		:param inputs: 	the set of CirStatePatternRule or CirStatePatternNode or integer feature vector; or None for all
		:return:		the set of minimal CirStatePatternRule(s) that could cover all the executions matched with input
		"""
		## 1. collect all the executions matched in the rules
		all_rules = self.get_rules(inputs)
		all_executions, min_rules = set(), set()
		for rule in all_rules:
			for execution in rule.get_executions():
				all_executions.add(execution)

		## 2. randomly minimal reduction algorithms for rules
		while (len(all_executions) > 0) and (len(all_rules) > 0):
			## 2-1. randomly select a CirStatePatternRule from
			rand_rule = jcbase.rand_select(all_rules)
			rand_rule: CirStatePatternRule
			all_rules.remove(rand_rule)

			## 2-2. update the local rule and minimal subset
			com_executions = all_executions & rand_rule.get_executions()
			if len(com_executions) > 0:
				min_rules.add(rand_rule)
				all_executions = all_executions - com_executions

			## 2-3. update the rule-set and remove the useless ones
			rem_rules = set()
			for rule in all_rules:
				com_executions = rule.get_executions() & all_executions
				if len(com_executions) == 0:
					rem_rules.add(rule)
			all_rules = all_rules - rem_rules

		## 3. return minimal reduced set of CirStatePatternRule
		return min_rules


class CirStatePatternOutput:
	"""
	It implements the output methods to write information of generated CirStatePatternRule(s).
	"""

	def __init__(self, c_project: jcmuta.CProject, m_document: jcencode.MerDocument,
				 max_code_length: int, max_rules_print: int):
		"""
		:param c_project: the original document
		:param m_document: the memory-reduced document
		:param max_code_length: maximal length of printed code
		:param max_rules_print: maximal number of rules for each mutant
		"""
		self.c_project = c_project
		self.m_document = m_document
		self.__p_tree__ = CirStatePatternTree(m_document)
		self.__writer__ = None
		self.max_code_length = max_code_length
		self.__max_length__ = 96
		self.__precision__ = 10000
		self.max_rules_print = max_rules_print
		return

	## 1. basic methods

	def __opens__(self, writer: TextIO, title: str):
		"""
		:param writer:
		:param title:
		:return:
		"""
		self.__writer__ = writer
		self.__writer__.write(title)
		self.__writer__.write("\n")
		return

	def __write__(self, text: str):
		self.__writer__.write(text)
		return

	def __close__(self, end_of_file: str):
		self.__writer__.write("EOF:\t")
		self.__writer__.write(end_of_file)
		self.__writer__.write("\n")
		self.__writer__ = None
		return

	def __percent__(self, ratio: float):
		return int(ratio * self.__precision__) / (self.__precision__ / 100)

	## 2. string methods

	def __str2str__(self, code: str):
		if len(code) > self.max_code_length:
			code = code[0: self.max_code_length] + "..."
		return "\"{}\"".format(code)

	def __mut2str__(self, mutant: jcencode.MerMutant, used_tests):
		"""
		:param mutant: 		the mutant of which information is written in the given used tests
		:param used_tests: 	the set of MerTestCase or int to decide whether mutant is revealed
		:return:			mid \t result \t class \t operator \t line \t location \t parameter
		"""
		mid = mutant.get_mid()
		res = mutant.is_killed_in(used_tests)
		c_mutant = mutant.find_source(self.c_project)
		cls = c_mutant.get_mutation().get_mutation_class()
		ort = c_mutant.get_mutation().get_mutation_operator()
		loc = c_mutant.get_mutation().get_location()
		lin = loc.line_of(False)
		cod = self.__str2str__(loc.get_code(True))
		pam = c_mutant.get_mutation().get_parameter()
		return mid, res, cls, ort, lin, cod, pam

	def __rul2str__(self, rule: CirStatePatternRule, used_tests):
		"""
		:param rule: 		the state pattern rule to be written to the files string
		:param used_tests: 	the set of MerTestCase or int to evaluate the input rule
		:return:			rid \t length \t executions \t killed \t kill_ratio(%) \t support \t confidence(%)
		"""
		rid = str(rule)
		killed, alive, kill_ratio = rule.k_measure(used_tests)
		length, support, confidence = rule.s_measure(used_tests)
		executions = len(rule.get_executions())
		kill_ratio = self.__percent__(kill_ratio)
		confidence = self.__percent__(confidence)
		return rid, length, executions, killed, kill_ratio, support, confidence

	def __sta2str__(self, state: jcencode.MerContextState):
		"""
		:param state:	the CirAbstractState of which information is written to the file
		:return:		stid \t category \t node_type \t line \t code \t loperand \t roperand
		"""
		stid = state.get_sid()
		c_state = state.find_source(self.c_project)
		category = c_state.get_category()
		location = c_state.get_location()
		node_type = location.get_node_type()
		code_line = location.get_ast_source().line_of(False)
		code_text = self.__str2str__(location.get_ast_source().get_code(True))
		loperand = c_state.get_loperand().get_code()
		roperand = c_state.get_roperand().get_code()
		return stid, category, node_type, code_line, code_text, loperand, roperand

	def __ant2str__(self, state: jcencode.MerContextState, used_tests):
		"""
		:param state: 		the CirAbstractState as the annotation to be evaluated
		:param used_tests:	the set of MerTestCase(s) or int to evaluate the state
		:return:			stid, category, node_type, node_code, loperand, roperand, support, confidence
			stid, category, location, loperand, roperand, length, support, confidence(%)
		"""
		stid = state.get_sid()
		c_state = state.find_source(self.c_project)
		category = c_state.get_category()
		location = c_state.get_location()
		loc_code = "{}[{}]".format(location.get_ast_source().get_class_name(), location.get_node_id())
		loperand = c_state.get_loperand().get_code()
		roperand = c_state.get_roperand().get_code()
		rule = self.__p_tree__.get_node([state.get_sid()]).get_rule()
		length, support, confidence = rule.s_measure(used_tests)
		confidence = self.__percent__(confidence)
		return stid, category, loc_code, loperand, roperand, length, support, confidence

	## 3. writing methods

	def write_patterns_to_mutations(self, file_path: str, rules, used_tests):
		"""
		:param file_path: 	the xxx.ptm in which the pattern-mutation table is written
		:param rules:		the set of CirStatePatternRule(s) that are written to file
		:param used_tests:	the set of MerTestCase(s) or int to measure the input rule
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.__opens__(writer, "Table of Pattern-to-Mutation")

			for rule in rules:
				rule: CirStatePatternRule
				self.__write__("\n#BEG\n")

				## 1. evaluate the rule and get the metrics
				killed, alive, kill_ratio = rule.k_measure(used_tests)
				length, support, confidence = rule.s_measure(used_tests)
				exec_number, muta_number = len(rule.get_executions()), len(rule.get_mutants())
				kill_ratio = self.__percent__(kill_ratio)
				confidence = self.__percent__(confidence)

				## 2. write the summary of metrics
				self.__write__("\t[EVALUATION]\n")
				self.__write__("\tRID\t{}\tEXE\t{}\tMUT\t{}\n".format(str(rule), exec_number, muta_number))
				self.__write__("\tKIL\t{}\tALV\t{}\tKRT\t{}%\n".format(killed, alive, kill_ratio))
				self.__write__("\tLEN\t{}\tSUP\t{}\tCOF\t{}%\n".format(length, support, confidence))
				self.__write__("\n")

				## 3. write the definition of the rule
				self.__write__("\t[DEFINITION]\n")
				self.__write__("\tSTID\tCategory\tNodeType\tNodeLine\tNodeCode\tLoperand\tRoperand\n")
				for state in rule.get_states():
					stid, category, node_type, node_line, node_code, loperand, roperand = self.__sta2str__(state)
					self.__write__("\t{}\t{}\t{}\t#{}\t\"{}\"\t[{}]\t[{}]\n".format(stid, category, node_type, node_line,
																					node_code, loperand, roperand))
				self.__write__("\n")

				## 4. write the matched mutations set
				self.__write__("\t[MUTATIONS]\n")
				self.__write__("\tMID\tRESULT\tCLASS\tOPERATOR\tLINE\tLOCATION\tPARAMETER\n")
				for mutant in rule.get_mutants():
					mid, result, cls, operator, line, location, parameter = self.__mut2str__(mutant, used_tests)
					self.__write__("\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t({})\n".
								   format(mid, result, cls, operator, line, location, parameter))
				self.__write__("\n")

				self.__write__("#END\n")

			self.__close__(file_path)
		return

	def write_patterns_of_summarize(self, file_path: str, rules, used_tests):
		"""
		:param file_path: 	the xxx.pts in which the patterns-summary table is written
		:param rules:		the set of CirStatePatternRule(s) that are written to file
		:param used_tests:	the set of MerTestCase(s) or int to measure the input rule
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.__opens__(writer, "Table of Pattern-Summarizes")

			## 1. evaluate the rule-set for precision and recall...
			all_executions, cov_executions = set(), set()
			for execution in self.m_document.get_state_space().get_executions():
				if execution.get_mutant().is_killed_in(used_tests):
					continue
				else:
					all_executions.add(execution)
			for rule in rules:
				rule: CirStatePatternRule
				for execution in rule.get_executions():
					cov_executions.add(execution)
			com_executions = all_executions & cov_executions
			los_executions = all_executions - cov_executions
			mis_ratio, precision, recall, score, optimal_ratio = 0.0, 0.0, 0.0, 0.0, 0.0
			if len(all_executions) > 0:
				mis_ratio = len(los_executions) / len(all_executions)
				recall = len(com_executions) / len(all_executions)
			if len(cov_executions) > 0:
				precision = len(com_executions) / len(cov_executions)
				optimal_ratio = len(rules) / len(com_executions)
			if len(com_executions) > 0:
				score = 2 * precision * recall / (precision + recall)
			mis_ratio = self.__percent__(mis_ratio)
			precision = self.__percent__(precision)
			recall = self.__percent__(recall)
			optimal_ratio = self.__percent__(optimal_ratio)
			score = self.__percent__(score) / 100.0

			## 2. print the summary of evaluation metrics
			self.__write__("\n[METRICS]\n")
			self.__write__("ALL_EXEC\t{}\tCOV_EXEC\t{}\tMIS_RATE\t{}%\n".
						   format(len(all_executions), len(cov_executions), mis_ratio))
			self.__write__("USE_RULE\t{}\tRED_RATE\t{}%\tOPT_RATE\t{}%\n".
						   format(len(rules), 100 - optimal_ratio, optimal_ratio))
			self.__write__("RULE_PRC\t{}%\tRULE_REC\t{}%\tF_SCORE\t{}\n".format(precision, recall, score))
			self.__write__("\n")

			## 3. write the rule and evaluation metrics to the summary
			self.__write__("[USED_RULES]\n")
			self.__write__("RID\tLENGTH\tEXECUTIONS\tKILLED\tKILL_RATIO(%)\tSUPPORT\tCONFIDENCE(%)\n")
			for rule in rules:
				rule: CirStatePatternRule
				rid, length, executions, killed, kill_ratio, support, confidence = self.__rul2str__(rule, used_tests)
				self.__write__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".
							   format(rid, length, executions, killed, kill_ratio, support, confidence))
			self.__write__("\n")

			## 4. write the uncovered mutations and the information
			self.__write__("[MUTATIONS]\n")
			self.__write__("MID\tRESULT\tCLASS\tOPERATOR\tLINE\tCODE\tPARAMETER\n")
			for execution in los_executions:
				mid, result, cls, operator, line, location, param = self.__mut2str__(execution.get_mutant(), used_tests)
				self.__write__("{}\t{}\t{}\t{}\t{}\t{}\t({})\n".format(mid, result, cls, operator, line, location, param))
			self.__write__("\n")

			self.__close__(file_path)
		return

	def write_mutations_to_patterns(self, file_path: str, rules, used_tests):
		"""
		:param file_path:	the xxx.mtp in which the information of mutation-pattern table
		:param rules:		the set of CirStatePatternRule(s) to be selected among mutants
		:param used_tests:	the set of MerTestCase(s) or int to evaluate corresponding rule
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.__opens__(writer, "Table of Mutation-Patterns")

			## 1. collect the mutant and the corresponding pattern rules
			mutant_patterns = dict()	# MerMutant --> CirStatePatternRule
			for rule in rules:
				rule: CirStatePatternRule
				for mutant in rule.get_mutants():
					if not (mutant in mutant_patterns):
						mutant_patterns[mutant] = list()
					mutant_patterns[mutant].append(rule)

			## 2. write each mutation and their corresponding patterns
			for mutant, patterns in mutant_patterns.items():
				if mutant.is_killed_in(used_tests):
					continue
				else:
					self.__write__("\n#BEG\n")

					mid, res, cls, operator, line, location, parameter = self.__mut2str__(mutant, used_tests)
					self.__write__("\t[MUTATION]\n")
					self.__write__("\tMID: {}\tCLS: {}; {}; ({})\n".format(mid, cls, operator, parameter))
					self.__write__("\tLIN#{}:\t{}\n".format(line, location))
					self.__write__("\n")

					self.__write__("\t[PATTERNS]\n")
					self.__write__("\tRID\tSUPPORT\tCONFIDENCE(%)\tSTID\tCATEGORY\tNODE\tLINE\tCODE\tLOPERAND\tROPERAND\n")
					for rule in patterns:
						rule: CirStatePatternRule
						rid = str(rule)
						length, support, confidence = rule.s_measure(used_tests)
						confidence = self.__percent__(confidence)
						for state in rule.get_states():
							stid, category, node_type, node_line, node_code, loperand, roperand = self.__sta2str__(state)
							self.__write__("\t{}\t{}\t{}\t{}\t{}\t{}\t#{}\t{}\t[{}]\t[{}]\n".format(
								rid, support, confidence, stid, category, node_type, node_line, node_code, loperand, roperand))
					self.__write__("\n")

					self.__write__("#END\n")
			self.__close__(file_path)
		return

	def write_mutation_to_summarize(self, file_path: str, rules, used_tests):
		"""
		:param file_path:	the xxx.mts in which the information of mutation-summary table
		:param rules:		the set of CirStatePatternRule(s) to be selected among mutants
		:param used_tests:	the set of MerTestCase(s) or int to evaluate corresponding rule
		:return:
		"""
		with open(file_path, 'w') as writer:
			## 0. open the file and write information
			self.__opens__(writer, "Table of Mutation-Summary\n")

			## 1. collect the mapping from undetected mutant to corresponding rules
			mutants_rules = dict()
			for mutant in self.m_document.get_mutant_space().get_mutants():
				if not mutant.is_killed_in(used_tests):
					mutant_rules = list()
					for rule in rules:
						rule: CirStatePatternRule
						if rule.has_sample(mutant) and len(rule.get_features()) > 0:
							mutant_rules.append(rule)
					if len(mutant_rules) > self.max_rules_print:
						mutant_rules = mutant_rules[0: self.max_rules_print]
					mutants_rules[mutant] = mutant_rules
			self.__write__("Summary.\tMutants\t{}\tUndetected\t{}\n\n".
						   format(len(self.m_document.get_mutant_space().get_mutants()), len(mutants_rules)))

			## 2. write the mutation and corresponding patterns to each file line
			self.__write__("ID\tCLAS\tOPRT\tLINE\tCODE\tPARM\tTYPE\tCONF(%)\tCATE\tNODE\tLOCT\tLOPR\tROPR\n")
			for mutant, mutant_rules in mutants_rules.items():
				mid, res, cls, operator, line, code, parameter = self.__mut2str__(mutant, used_tests)
				if len(mutant_rules) == 0:
					self.__write__("{}\t{}\t{}\t{}\t{}\t{}\t{}".format(mid, cls, operator, line, code, parameter, None))
					self.__write__("\n")
				else:
					for rule in mutant_rules:
						state = rule.get_states()[0]
						length, support, confidence = rule.s_measure(used_tests)
						confidence = self.__percent__(confidence)
						stid, category, node_type, node_line, node_code, loperand, roperand = self.__sta2str__(state)
						if (category == "eva_cond") and ("false" == loperand):
							mutant_type = "CEQ"
						else:
							mutant_type = str(rule)
						self.__write__("{}\t{}\t{}\t{}\t{}\t{}\t{}".
									   format(mid, cls, operator, line, code, parameter, mutant_type))
						self.__write__("\t{}\t{}\t{}:{}\t{}\t[{}]\t[{}]".format(
							confidence, category, node_type, node_line, node_code, loperand, roperand))
						self.__write__("\n")

			## 3. close the file and write end-of-file tag...
			self.__close__(file_path)
		return


## testing methods


def do_minings(document: jcencode.MerDocument, used_tests, max_length: int,
			   min_support: int, min_confidence: float):
	"""
	:param document:
	:param used_tests:
	:param max_length:
	:param min_support:
	:param min_confidence:
	:return:	good_rules, min_rules, mutant_rule_dict
	"""
	## 1. collect the features in undetected mutations
	inputs = CirStatePatternInputs(document, max_length, min_support, min_confidence, 1.0)
	init_features = set()
	for execution in document.get_state_space().get_executions():
		if execution.get_mutant().is_killed_in(used_tests):
			continue
		else:
			for feature in execution.get_features():
				init_features.add(feature)
	index, number = 0, len(init_features)
	for feature in init_features:
		rule = inputs.get_rule([feature])
		if index % 1000 == 0:
			length, support, confidence = rule.s_measure(used_tests)
			confidence = int(confidence * 10000) / 100.0
			print("\t==>\tMINE[{}/{}]\t{}\t{}\t{}%".format(index, number, length, support, confidence))
		index += 1
	## 2. select good and minimal set of rules
	selected_rules = inputs.filter_rules(None, used_tests)
	minimized_rules = inputs.reduce_rules(selected_rules)
	sorted_rules = inputs.sorted_rules(selected_rules, used_tests)
	mutant_rule_dict = dict()
	for rule in sorted_rules:
		for mutant in rule.get_mutants():
			if not (mutant in mutant_rule_dict):
				mutant_rule_dict[mutant] = list()
			mutant_rule_dict[mutant].append(rule)
	return selected_rules, minimized_rules, sorted_rules, mutant_rule_dict


def do_testing(c_project: jcmuta.CProject, m_document: jcencode.MerDocument, used_tests,
			   max_length: int, min_support: int, min_confidence: float, directory: str):
	"""
	:param c_project:
	:param m_document:
	:param used_tests:
	:param max_length:
	:param min_support:
	:param min_confidence:
	:param directory:
	:return:
	"""
	## 1. mining patterns and generation
	file_name = m_document.get_name()
	mutant_number = len(m_document.get_mutant_space().get_mutants())
	test_number = len(m_document.get_test_space().get_test_cases())
	state_number = len(m_document.get_state_space().get_states())
	symbol_number = len(c_project.sym_tree.get_sym_nodes())
	print("\t1. Testing on {}: {} mutants, {} tests, {} states, {} symbols.".
		  format(m_document.get_name(), mutant_number, test_number, state_number, symbol_number))

	## 2. perform mining using naive algorithm
	print("\t2. Start do mining algorithm...")
	good_rules, min_rules, sort_rules, mr_dict = do_minings(
		m_document, used_tests, max_length, min_support, min_confidence)

	## 3. start to write information to files
	writer = CirStatePatternOutput(c_project, m_document, 64, 1)
	print("\t3. Write {} rules, {} minimal, {} sorted.".format(len(good_rules), len(min_rules), len(sort_rules)))
	writer.write_patterns_to_mutations(os.path.join(directory, file_name + ".ptm"), good_rules, used_tests)
	writer.write_patterns_of_summarize(os.path.join(directory, file_name + ".sum"), min_rules, used_tests)
	writer.write_mutations_to_patterns(os.path.join(directory, file_name + ".mtp"), good_rules, used_tests)
	writer.write_mutation_to_summarize(os.path.join(directory, file_name + ".mum"), sort_rules, used_tests)
	return


if __name__ == "__main__":
	features_directory = "/home/dzt2/Development/Data/zext2/features"
	encoding_directory = "/home/dzt2/Development/Data/zext2/encoding"
	patterns_directory = "/home/dzt2/Development/Data/zext2/patterns"
	for fname in os.listdir(features_directory):
		cdocument = jcmuta.CProject(os.path.join(features_directory, fname), fname)
		mdocument = jcencode.MerDocument(os.path.join(encoding_directory, fname), fname)
		o_directory = os.path.join(patterns_directory, fname)
		if not os.path.exists(o_directory):
			os.mkdir(o_directory)
		print("Testing on {}.".format(fname))
		do_testing(cdocument, mdocument, None, 1, 2, 0.54, o_directory)
		print()
	print("End-All-Testing.")


