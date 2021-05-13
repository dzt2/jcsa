"""This file implements the mining algorithm for discovering killable prediction rules"""


import os
from typing.io import TextIO
import com.jcsa.libs.test as jctest
import com.jcsa.mine.encode as jcenco


class MerPredictionInputs:
	"""
	It maintains the input parameters for mining killable prediction rules.
	"""

	def __init__(self, m_document: jcenco.MerDocument, max_length: int, min_support: int, min_confidence: float,
				 max_confidence: float, min_good_rules: int):
		"""
		:param m_document: 		the document provides memory-reduced data source
		:param max_length: 		the maximal length of prediction rules
		:param min_support: 	the minimal support to select good rules
		:param min_confidence: 	the minimal confidence required
		:param max_confidence: 	the maximal confidence to stop mining
		:param min_good_rules: 	the minimal number of rules being printed
		"""
		self.document = m_document
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		self.min_good_rules = min_good_rules
		return

	def get_document(self):
		return self.document

	def get_max_length(self):
		return self.max_length

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_min_good_rules(self):
		return self.min_good_rules


class MerPredictionMemory:
	"""
	It provides memory to manage the prediction rule tree used in mining algorithm.
	"""

	def __init__(self, inputs: MerPredictionInputs):
		self.inputs = inputs
		self.__tree__ = jcenco.MerPredictRuleTree(self.inputs.get_document())
		return

	def get_inputs(self):
		return self.inputs

	def get_document(self):
		return self.inputs.get_document()

	def get_tree(self):
		return self.__tree__

	def get_root(self):
		return self.__tree__.get_root()

	def get_child(self, parent: jcenco.MerPredictRuleNode, feature: int):
		return self.__tree__.get_child(parent, feature)

	def get_node(self, features):
		return self.__tree__.get_node(features)

	def evaluate(self, node: jcenco.MerPredictRuleNode, used_tests):
		"""
		:param node: the prediction rule to be evaluated
		:param used_tests:
		:return: length, support, confidence
		"""
		node = self.get_node(node.get_features())
		result, killed, alive, p_confidence = node.predict(used_tests)
		length = len(node)
		support = alive
		total = alive + killed
		if total > 0:
			confidence = support / total
		else:
			confidence = 0.0
		return length, support, confidence

	def extract_good_rules(self, used_tests):
		"""
		:param used_tests:
		:return:
		"""
		node_evaluation_dict = dict()
		for node in self.__tree__.get_nodes():
			node: jcenco.MerPredictRuleNode
			length, support, confidence = self.evaluate(node, used_tests)
			if length <= self.inputs.get_max_length() and support >= self.inputs.get_min_support() and confidence >= self.inputs.get_min_confidence():
				node_evaluation_dict[node] = (length, support, confidence)
		return node_evaluation_dict


def sort_prediction_rules_by_keys(node_evaluation_dict: dict, key_index: int, reverse: bool):
	key_dict = dict()
	key_list = list()
	for node, evaluation in node_evaluation_dict.items():
		node: jcenco.MerPredictRuleNode
		key = evaluation[key_index]
		if not (key in key_dict):
			key_dict[key] = set()
			key_list.append(key)
		key_dict[key].add(node)
	node_list = list()
	key_list.sort(reverse=reverse)
	for key in key_list:
		for node in key_dict[key]:
			node: jcenco.MerPredictRuleNode
			node_list.append(node)
	return node_list


def sort_prediction_rules_by_support(node_evaluation_dict: dict):
	return sort_prediction_rules_by_keys(node_evaluation_dict, 1, True)


def sort_prediction_rules_by_confidence(node_evaluation_dict: dict):
	return sort_prediction_rules_by_keys(node_evaluation_dict, 2, True)


def precision_recall_evaluate(orig_samples: set, pred_samples: set):
	"""
	:param orig_samples:
	:param pred_samples:
	:return: precision, recall, f1_score
	"""
	como_samples = orig_samples & pred_samples
	if len(como_samples) > 0:
		precision = len(como_samples) / len(pred_samples)
		recall = len(como_samples) / len(orig_samples)
		f1_score = 2 * precision * recall / (precision + recall)
		return precision, recall, f1_score
	else:
		return 0.0, 0.0, 0.0


class MerPredictionMiner:
	"""
	It implements the mining algorithm.
	"""

	def __init__(self, inputs: MerPredictionInputs):
		"""
		:param inputs:
		"""
		self.memory = MerPredictionMemory(inputs)
		self.solutions = dict()	# MerPredictTreeNode --> {length, support, confidence}
		return

	def __mine__(self, parent: jcenco.MerPredictRuleNode, features: list, used_tests):
		"""
		:param parent:
		:param features:
		:param used_tests:
		:return: recursively mining rules under the parent
		"""
		if not (parent in self.solutions):
			length, support, confidence = self.memory.evaluate(parent, used_tests)
			self.solutions[parent] = (length, support, confidence)
		solution = self.solutions[parent]
		length = solution[0]
		support = solution[1]
		confidence = solution[2]
		if length < self.memory.get_inputs().get_max_length() and support >= self.memory.get_inputs().get_min_support() and confidence < self.memory.get_inputs().get_max_confidence():
			for k in range(0, len(features)):
				child = self.memory.get_child(parent, features[k])
				if (child != parent) and not (child is None):
					self.__mine__(child, features[k + 1: ], used_tests)
		return

	def __outs__(self):
		"""
		:return: good_node --> [length, support, confidence]
		"""
		node_evaluation_dict = dict()
		for node, evaluation in self.solutions.items():
			node: jcenco.MerPredictRuleNode
			length = evaluation[0]
			support = evaluation[1]
			confidence = evaluation[2]
			length: int
			support: int
			confidence: float
			if length <= self.memory.get_inputs().get_max_length() and support >= self.memory.get_inputs().get_min_support() and confidence >= self.memory.get_inputs().get_min_confidence():
				node_evaluation_dict[node] = (length, support, confidence)
		if len(node_evaluation_dict) == 0:
			sort_node_list = sort_prediction_rules_by_confidence(self.solutions)
			sort_node_size = max(1, self.memory.get_inputs().get_min_good_rules())
			if len(sort_node_list) > sort_node_size:
				sort_node_list = sort_node_list[0: sort_node_size]
			for node in sort_node_list:
				evaluation = self.solutions[node]
				length = evaluation[0]
				support = evaluation[1]
				confidence = evaluation[2]
				length: int
				support: int
				confidence: float
				node_evaluation_dict[node] = (length, support, confidence)
		return node_evaluation_dict

	def mine(self, features, used_tests):
		"""
		:param features:
		:param used_tests:
		:return:
		"""
		feature_list = jcenco.MerPredictRuleTree.__get_feature_list__(features)
		self.solutions.clear()
		if used_tests is None:
			used_tests_size = None
		else:
			used_tests_size = len(used_tests)
		print("\t\t\tMining:\tIN[{}, {}]".format(len(feature_list), used_tests_size), end="")
		self.__mine__(self.memory.get_root(), feature_list, used_tests)
		node_evaluation_dict = self.__outs__()
		print("\t--> OU[{}; {}/{}]".format(len(node_evaluation_dict), len(self.solutions), len(self.memory.get_tree())))
		self.solutions.clear()
		return node_evaluation_dict

	def __get_features__(self, mutants):
		features = set()
		for mutant in mutants:
			mutant: jcenco.MerMutant
			for execution in self.memory.get_inputs().get_document().exec_space.get_executions_of(mutant):
				execution: jcenco.MerExecution
				for feature in execution.get_features():
					features.add(feature)
		return features

	def mine_mutants(self, mutants, used_tests):
		"""
		:param mutants:
		:param used_tests:
		:return:
		"""
		features = self.__get_features__(mutants)
		return self.mine(features, used_tests)


class MerPredictionOutput:
	"""
	It manages the evaluation of rule outputs
	"""

	def __init__(self, c_document: jctest.CDocument, m_document: jcenco.MerDocument):
		self.c_document = c_document
		self.m_document = m_document
		self.writer = None
		return

	def __output__(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		self.writer.flush()
		return

	def __mut2str__(self, mutant: jcenco.MerMutant):
		"""
		:param mutant:
		:return: id result class operator function line code parameter
		"""
		orig_mutant = self.c_document.project.muta_space.get_mutant(mutant.get_mid())
		mid = orig_mutant.get_muta_id()
		result = orig_mutant.get_result().is_killed_in(None)
		if result:
			result = "Killed"
		else:
			result = "Survive"
		m_class = orig_mutant.get_mutation().get_mutation_class()
		operator = orig_mutant.get_mutation().get_mutation_operator()
		location = orig_mutant.get_mutation().get_location()
		line = location.line_of(tail=False) + 1
		definition = location.function_definition_of()
		code = "\"{}\"".format(location.get_code(True))
		def_code = definition.get_code(True)
		index = def_code.index('(')
		fun_code = def_code[0: index].strip()
		parameter = orig_mutant.get_mutation().get_parameter()
		return "{}\t{}\t{}\t{}\t{}\t{}\t{}\t[{}]".format(mid, result, m_class, operator, fun_code, line, code,
														 parameter)

	def __nod2str__(self, node: jcenco.MerPredictRuleNode, evaluation):
		"""
		:param node:
		:param evaluation: length, support, confidence
		:return: features length support confidence(%) executions mutants
		"""
		self.c_document = self.c_document
		features = node.get_features()
		length = evaluation[0]
		support = evaluation[1]
		confidence = int(evaluation[2] * 10000) / 100.0
		executions = len(node.get_executions())
		mutants = len(node.get_mutants())
		return "{}\t{}\t{}\t{}%\t{}\t{}".format(str(features), length, support, confidence, executions, mutants)

	def __cod2str__(self, condition: jcenco.MerCondition):
		"""
		:param condition:
		:return: category operator execution statement location parameter
		"""
		sym_condition = self.c_document.conditions.get_condition(condition.get_code())
		return "{}\t{}\t{}\t\"{}\"\t\"{}\"\t[{}]".format(sym_condition.get_category(), sym_condition.get_operator(),
														 sym_condition.get_execution(),
														 sym_condition.get_execution().get_statement().get_cir_code(),
														 sym_condition.get_location().get_cir_code(),
														 sym_condition.get_parameter())

	## write mutant-rules pair

	def __write_mutant_rules__(self, miner: MerPredictionMiner, mutant: jcenco.MerMutant, max_print_size: int):
		"""
		:param miner:
		:param mutant:
		:param max_print_size: the maximal number of rules being printed
		:return:
		"""
		node_evaluation_dict = miner.mine_mutants([mutant], mutant.get_result().get_tests_of(False))
		good_rules = sort_prediction_rules_by_support(node_evaluation_dict)
		if (max_print_size > 0) and (len(good_rules) > max_print_size):
			good_rules = good_rules[0: max_print_size]
		self.__output__("[M]\t{}\n".format(self.__mut2str__(mutant)))
		for rule in good_rules:
			self.__output__("\t[R]\t{}\n".format(self.__nod2str__(rule, node_evaluation_dict[rule])))
			index = 0
			for condition in rule.get_conditions():
				self.__output__("\t\t[C.{}]\t{}\n".format(index, self.__cod2str__(condition)))
				index += 1
		self.__output__("\n")
		return

	def write_mutants_rules(self, file_path: str, inputs: MerPredictionInputs, mutants, max_print_size=-1):
		"""
		:param file_path:
		:param max_print_size:
		:param inputs:
		:param mutants: the set of mutants being used to generate prediction rules
		:return:
		"""
		miner = MerPredictionMiner(inputs)
		with open(file_path, 'w') as writer:
			self.writer = writer
			index = 0
			for mutant in mutants:
				index += 1
				print("\t\t-->\tMining on progress: [{}/{}]".format(index, len(mutants)))
				self.__write_mutant_rules__(miner, mutant, max_print_size)
		return miner

	def write_predict_rules(self, file_path: str, inputs: MerPredictionInputs, mutants):
		"""
		:param file_path:
		:param inputs:
		:param mutants:
		:return: the absolute prediction rules for undetected mutants
		"""
		miner = MerPredictionMiner(inputs)
		node_evaluation_dict = miner.mine_mutants(mutants, None)
		with open(file_path, 'w') as writer:
			self.writer = writer
			for node, evaluation in node_evaluation_dict.items():
				self.__output__("BEG_RULE\n")
				self.__output__("\t[R]\t{}\n".format(self.__nod2str__(node, evaluation)))
				index = 0
				for condition in node.get_conditions():
					index += 1
					self.__output__("\t\t[C.{}]\t{}\n".format(index, self.__cod2str__(condition)))
				node_mutants = node.get_mutants()
				self.__output__("\t[S]\t{} mutants\n".format(len(node_mutants)))
				index = 0
				for mutant in node_mutants:
					index += 1
					self.__output__("\t\t[M.{}]\t{}\n".format(index, self.__mut2str__(mutant)))
				self.__output__("END_RULE\n")
				self.__output__("\n")
		return miner

	def write_predict_trees(self, file_path: str, middle: MerPredictionMemory):
		"""
		:param file_path:
		:param middle:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			orig_samples, pred_samples = set(), set()
			for mutant in middle.inputs.get_document().muta_space.get_mutants():
				mutant: jcenco.MerMutant
				if not (mutant.get_result().is_killed_in(None)):
					orig_samples.add(mutant)
			tree_nodes = middle.extract_good_rules(None)
			for tree_node in tree_nodes.keys():
				for mutant in tree_node.get_mutants():
					pred_samples.add(mutant)
			precision, recall, f1_score = precision_recall_evaluate(orig_samples, pred_samples)
			self.__output__("BEG_SUM\n")
			self.__output__("\tPREDICT_RULE:\t{}\t({}%)\n".format(len(tree_nodes),
																  int(len(tree_nodes) / len(orig_samples) * 10000) / 100.0))
			self.__output__("\tORIG_MUTANTS:\t{}\n".format(len(orig_samples)))
			self.__output__("\tPRED_MUTANTS:\t{}\n".format(len(pred_samples)))
			self.__output__("\tPRECISION(%):\t{}%\n".format(int(precision * 10000) / 100.0))
			self.__output__("\tRECALLING(%):\t{}%\n".format(int(recall * 10000) / 100.0))
			self.__output__("\tF1_SCORES(%):\t{}\n".format(f1_score))
			self.__output__("END_SUM\n")
			self.__output__("\n")
			self.__output__("Rule\tLength\tSupport\tConfidence(%)\tExec_Number\tMuta_Number\n")
			for tree_node in tree_nodes:
				tree_node: jcenco.MerPredictRuleNode
				length, support, confidence = middle.evaluate(tree_node, None)
				exec_number = len(tree_node.get_executions())
				muta_number = len(tree_node.get_mutants())
				self.__output__("{}\t{}\t{}\t{}%\t{}\t{}\n".format(str(tree_node),
																   length, support,
																   int(confidence * 10000) / 100.0,
																   exec_number, muta_number))
			self.__output__("\n")
		return


def main(features_directory: str, encoding_directory: str, postfix: str, output_directory: str, select_alive: bool):
	"""
	:param features_directory:
	:param encoding_directory:
	:param postfix:
	:param output_directory:
	:param select_alive:
	:return:
	"""
	max_length, min_support, min_confidence, max_confidence, min_good_rules, max_print_size = 1, 2, 0.75, 0.95, 3, 8
	for file_name in os.listdir(features_directory):
		## 1. load documents
		inputs_directory = os.path.join(features_directory, file_name)
		encode_directory = os.path.join(encoding_directory, file_name)
		c_document = jctest.CDocument(inputs_directory, file_name, postfix)
		m_document = jcenco.MerDocument(encode_directory, file_name)
		print("Testing on", file_name)
		print("\tSummary:{} mutants\t{} executions\t{} conditions.".format(len(m_document.muta_space.get_mutants()),
																		   len(m_document.exec_space.get_executions()),
																		   len(m_document.cond_space.get_conditions())))
		## 2. construct mining machine
		inputs = MerPredictionInputs(m_document, max_length, min_support, min_confidence, max_confidence, min_good_rules)
		mutants = set()
		for mutant in m_document.exec_space.get_mutants():
			mutant: jcenco.MerMutant
			if select_alive:
				if not (mutant.get_result().is_killed_in(None)):
					mutants.add(mutant)
			else:
				mutants.add(mutant)
		output = MerPredictionOutput(c_document, m_document)

		## 3. output information to directory
		output.write_mutants_rules(os.path.join(output_directory, file_name + ".mur"), inputs, mutants, max_print_size)
		miner = output.write_predict_rules(os.path.join(output_directory, file_name + ".pur"), inputs, mutants)
		output.write_predict_trees(os.path.join(output_directory, file_name + ".tur"), miner.memory)
		print("\tOutput all prediction rules to directory...")
		print()
	return


if __name__ == "__main__":
	features_dir = "/home/dzt2/Development/Data/zexp/features"
	encoding_dir = "/home/dzt2/Development/Data/zexp/encoding"
	output_dir = "/home/dzt2/Development/Data/zexp/rules"
	main(features_dir, encoding_dir, ".sip", output_dir, True)

