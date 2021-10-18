"""This file implements the clustering and visualization algorithms for pattern mining."""


import os
from collections import deque
from typing import TextIO
from sklearn import metrics
import sklearn.tree as sktree
import graphviz
import pydotplus
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.test as jctest
import com.jcsa.mine.code as jecode
import com.jcsa.mine.rule as jerule


## mining algorithms


class KillPredictionFPMiner:
	"""
	Frequent Pattern Mining.
	"""

	def __init__(self, inputs: jerule.KillPredictionInputs):
		self.middle = inputs.get_middle_module()
		self.solutions = dict()	# KillPredictionRule --> (length, support, confidence)
		return

	def __mine__(self, parent: jerule.KillPredictionNode, features: list, used_tests):
		"""
		:param parent:
		:param features:
		:param used_tests:
		:return:
		"""
		## 1. evaluate and solve the rule
		length, support, confidence = parent.get_rule().lsc_measure(used_tests)
		self.solutions[parent.get_rule()] = (length, support, confidence)
		inputs = self.middle.get_inputs()

		## 2. filter the rules by input parameters
		if (length < inputs.get_max_length()) and \
				(support >= inputs.get_min_support()) and \
				(confidence < inputs.get_max_confidence()):
			for k in range(0, len(features)):
				child = self.middle.get_child(parent, features[k])
				if child != parent:
					self.__mine__(child, features[k + 1:], used_tests)
		return

	def __outs__(self, used_tests):
		"""
		:return: the set of good prediction rules generated from the solution cache
		"""
		all_rules = self.solutions.keys()
		good_rules = self.middle.filters_rules(all_rules, used_tests)
		return self.middle.sorting_rules(good_rules, used_tests)

	def __visz__(self, directory: str, c_document: jctest.CDocument, output_rules: list, used_tests):
		"""
		:param directory:
		:param c_document:
		:param output_rules:
		:param used_tests:
		:return: it generates directory/file_name.pdf for visualizing the annotation trees for output rules
		"""
		annotation_roots = set()
		for rule in output_rules:
			rule: jerule.KillPredictionRule
			for annotation in rule.get_annotations():
				c_annotation = annotation.find_source(c_document)
				annotation_roots.add(c_annotation)
		file_name = self.middle.get_document().file_name + ".fpm"
		c_document.visualize_annotation_trees(annotation_roots, directory, file_name, used_tests)
		return

	def mine(self, features, used_tests, is_reported: bool, directory=None, c_document=None):
		"""
		:param features: 	the set of integer features from which the rules are generated
		:param used_tests: 	the set of test cases used for evaluating the generated rules
		:param is_reported: whether to report the input-output information in the mining algorithm
		:param directory: 	the directory where the annotation subtree of rules are printed or None not to visualize
		:param c_document: 	the document used for visualizing the annotation subtree for annotations in output rules
		:return: 			sorted_good_rules
		"""
		## 1. initialization
		feature_list = self.middle.get_document().anot_space.normal(features)
		if used_tests is None:
			used_tests_number = len(self.middle.get_document().test_space)
		else:
			used_tests_number = len(used_tests)

		## 2. recursive frequent pattern mining
		if is_reported:
			print("\t\tInputs[{}, {}]".format(len(feature_list), used_tests_number), end='\t')
		self.solutions.clear()
		self.__mine__(self.middle.get_root(), feature_list, used_tests)
		sorted_good_rules = self.__outs__(used_tests)
		if is_reported:
			print("==>\tOutput[{} rules & {} goods]".format(len(self.solutions), len(sorted_good_rules)))

		## 3. visualization when it is required
		if not (directory is None):
			if not (c_document is None):
				self.__visz__(directory, c_document, sorted_good_rules, used_tests)
		return sorted_good_rules


class KillPredictionDTMiner:
	"""
	Decision Tree based Mining
	"""

	def __init__(self, inputs: jerule.KillPredictionInputs):
		self.middle = inputs.get_middle_module()
		return

	def __fits__(self, used_tests, is_reported: bool):
		"""
		:param used_tests: 	the set of MerTestCase (or its Integer ID) or None to evaluate the generated rules
		:param is_reported: whether to report the classification metrics from the generated decision tree
		:return: 			the decision tree classifier fitting based on input parameters
		"""
		x_matrix = self.middle.get_document().exec_space.new_feature_matrix()
		y_labels = self.middle.get_document().exec_space.new_label_sequence(used_tests)
		dc_tree = sktree.DecisionTreeClassifier()
		dc_tree.fit(x_matrix, y_labels)
		if is_reported:
			p_labels = dc_tree.predict(x_matrix)
			print(metrics.classification_report(y_labels, p_labels), end='')
		return dc_tree

	def __norm__(self, annotation: jecode.MerAnnotation, c_document: jctest.CDocument):
		"""
		:param annotation:
		:param c_document:
		:return: the normalized text for describing the input annotation of the edge of decision path
		"""
		self.middle = self.middle
		c_annotation = annotation.find_source(c_document)
		execution = c_annotation.get_execution()
		logic_type = c_annotation.logic_type
		code = c_annotation.store_unit.get_cir_code()
		if c_annotation.symb_value is None:
			parameter = None
		else:
			parameter = c_annotation.symb_value.get_code()
		text = "{}::{}({}::{})".format(logic_type, execution, code, parameter)
		text = text.replace('\"', '\'\'')
		return text

	def __visz__(self, dc_tree: sktree.DecisionTreeClassifier, directory: str, c_document: jctest.CDocument):
		"""
		:param dc_tree:
		:param directory:
		:param c_document:
		:return:
		"""
		m_document = self.middle.get_document()
		names = list()
		for annotation in m_document.anot_space.get_annotations():
			annotation: jecode.MerAnnotation
			names.append(self.__norm__(annotation, c_document))
		dot_data = sktree.export_graphviz(dc_tree, out_file=None,
										  feature_names=names,
										  class_names=["Alive", "Killed"],
										  filled=True)
		graph = pydotplus.graph_from_dot_data(dot_data)
		file_name = self.middle.get_document().file_name + ".dtm.pdf"
		graph.write_pdf(os.path.join(directory, file_name))
		return

	def __outs__(self, dc_tree: sktree.DecisionTreeClassifier):
		"""
		:param dc_tree:
		:return: the set of generated prediction rules from the decision tree and achieve the input parameters
		"""
		x_matrix = self.middle.get_document().exec_space.new_feature_matrix()
		node_indicator = dc_tree.decision_path(x_matrix)
		leaf_id = dc_tree.apply(x_matrix)
		dc_feature = dc_tree.tree_.feature
		dc_threshold = dc_tree.tree_.threshold
		all_rules = set()
		for eid in range(0, len(self.middle.get_document().exec_space.get_executions())):
			node_index = node_indicator.indices[node_indicator.indptr[eid]: node_indicator.indptr[eid + 1]]
			eid_features = set()
			for node_id in node_index:
				if leaf_id[eid] == node_id:
					pass
				elif x_matrix[eid, dc_feature[node_id]] > dc_threshold[node_id]:
					eid_features.add(dc_feature[node_id])
				else:
					pass
			rule = self.middle.get_node(eid_features).get_rule()
			all_rules.add(rule)
		return all_rules

	def __filt__(self, rules, features, used_tests):
		"""
		:param rules:
		:param features:
		:param used_tests:
		:return:
		"""
		feature_rules = set()
		for rule in rules:
			rule: jerule.KillPredictionRule
			if rule.has_features(features):
				feature_rules.add(rule)
		good_rules = self.middle.filters_rules(feature_rules, used_tests)
		return self.middle.sorting_rules(good_rules, used_tests)

	def mine(self, features, used_tests, is_reported: bool, directory=None, c_document=None):
		"""
		:param features:
		:param used_tests:
		:param is_reported:
		:param directory:
		:param c_document:
		:return:
		"""
		dc_tree = self.__fits__(used_tests, is_reported)
		if not (directory is None):
			if not (c_document is None):
				self.__visz__(dc_tree, directory, c_document)
		rules = self.__outs__(dc_tree)
		return self.__filt__(rules, features, used_tests)


## mining file writer


class KillPredictionWriter:
	"""
	It implements the output writer for evaluating killable prediction rules
	"""

	def __init__(self, c_document: jctest.CDocument):
		self.c_document = c_document
		self.file_name = c_document.get_program().name
		self.max_length = 64
		self.writer = None
		return

	## basic methods

	def __opens__(self, writer: TextIO, beg_line: str):
		"""
		:param writer:
		:param beg_line:
		:return:
		"""
		self.writer = writer
		if beg_line:
			self.writer.write(beg_line.strip() + "\n\n")
		return

	def __ratio__(self, ratio: float):
		self.c_document = self.c_document
		return int(ratio * 1000000) / 10000.0

	def __strip__(self, text: str):
		if len(text) > self.max_length:
			text = text[0: self.max_length] + "..."
		return text

	def __write__(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		return

	def __close__(self, end_line: str):
		"""
		:return:
		"""
		if end_line:
			self.__write__(end_line)
		self.writer = None
		return

	## format methods

	def __format_mutant__(self, mutant: jecode.MerMutant, used_tests):
		"""
		:param mutant:
		:param used_tests:
		:return: id result class operator function line code parameter
		"""
		mid = mutant.get_mid()
		if mutant.is_killed_in(used_tests):
			result = "Killed"
		else:
			result = "Alived"
		mutation = mutant.find_source(self.c_document).get_mutation()
		m_class = mutation.get_mutation_class()
		operator = mutation.get_mutation_operator()
		function = mutation.get_location().function_definition_of()
		func_name = function.get_code(True)
		index = func_name.index('(')
		func_name = func_name[0: index].strip()
		line = mutation.get_location().line_of(tail=False)
		code = mutation.get_location().get_code(True)
		code = self.__strip__(code)
		parameter = mutation.get_parameter()
		return "{}\t{}\t{}\t{}\t{}\t#{}\t\"{}\"\t{}".format(mid, result, m_class, operator, func_name, line, code, parameter)

	def __format_rule__(self, rule: jerule.KillPredictionRule, used_tests):
		"""
		:param rule:
		:param used_tests:
		:return: rid exe_number mut_number result length support confidence(%)
		"""
		rid = str(rule)
		exe_number = len(rule.get_executions())
		mut_number = len(rule.get_mutants())
		result, support, confidence = rule.rsc_measure(used_tests)
		length, support, confidence = rule.lsc_measure(used_tests)
		confidence = self.__ratio__(confidence)
		return "{}\t{}\t{}\t{}\t{}\t{}\t{}%".format(rid, exe_number, mut_number, result, length, support, confidence)

	def __format_annotation__(self, annotation: jecode.MerAnnotation, used_tests):
		"""
		:param annotation:
		:param used_tests:
		:return: logic_type execution line statement store_unit symb_value
		"""
		c_annotation = annotation.find_source(self.c_document)
		logic_type = c_annotation.get_logic_type()
		execution = c_annotation.get_execution()
		statement = execution.get_statement()
		store_unit = c_annotation.get_store_unit()
		symb_value = c_annotation.get_symb_value()
		if store_unit.has_ast_source():
			line = store_unit.get_ast_source().line_of(tail=False)
		else:
			line = None
		statement_code = self.__strip__(statement.get_cir_code())
		store_unit_code = self.__strip__(store_unit.get_cir_code())
		return "{}\t{}\t#{}\t\"{}\"\t\"{}\"\t[{}]".format(logic_type, execution, line, statement_code,
														  store_unit_code, symb_value.get_code())

	## write methods

	def write_rules_objects(self, middle: jerule.KillPredictionMiddle, rules, file_path: str, used_tests):
		"""
		:param middle: 		the middle module is used to evaluate the generated rules from mining algorithm
		:param rules: 		the set of killable prediction rules mined from mining algorithm module
		:param file_path: 	the path of the output file for printing the input killable prediction rules
		:param used_tests: 	the set of test cases for evaluation or None to represent all the defined tests
		:return:			The format of the killable prediction rules are presented as following.

							==> Table of Killable Prediction Rules and Definitions for {file_name}
							==> [BEG_RULE]
									[PID] exe_number mut_number prd_result length support confidence(%)
									[AID] logic_type execution line statement store_unit [symbol_value]
										[MID] result class operator function line location [parameter]
										[MID] result class operator function line location [parameter]
										......
										[MID] result class operator function line location [parameter]
							==>	[END_RULE]
							==> End_Of_File
		"""
		with open(file_path, 'w') as writer:
			self.__opens__(writer, "Table of Killable Prediction Rules and Definitions in {}.\n".format(self.file_name))

			all_rules = middle.sorting_rules(rules, used_tests)
			for rule in all_rules:
				self.__write__("\n[BEG_RULE]\n")

				self.__write__("\t[PID]\texe_number\tmut_number\tprd_result\tlength\tsupport\tconfidence(%)\n")
				self.__write__("\t{}\n".format(self.__format_rule__(rule, used_tests)))
				self.__write__("\n")

				self.__write__("\t[AID]\tlogic_type\texecution\tline\tstatement\tstore_unit\tsymbol_value\n")
				for annotation in rule.get_annotations():
					self.__write__("\t{}\n".format(self.__format_annotation__(annotation, used_tests)))
				self.__write__("\n")

				self.__write__("\t\t[MID]\tresult\tclass\toperator\tfunction\tline\tlocation\tparameter\n")
				for mutant in rule.get_mutants():
					self.__write__("\t\t{}\n".format(self.__format_mutant__(mutant, used_tests)))
				self.__write__("[END_RULE]\n")

			self.__close__("\nEnd_Of_File")
		return

	def write_rules_metrics(self, middle: jerule.KillPredictionMiddle, rules, file_path: str, used_tests):
		"""
		:param middle: 		the middle module is used to evaluate the generated rules from mining algorithm
		:param rules: 		the set of killable prediction rules mined from mining algorithm module
		:param file_path: 	the path of the output file for printing the input killable prediction rules
		:param used_tests: 	the set of test cases for evaluation or None to represent all the defined tests
		:return:			The format of the killable prediction rules' metrics for evaluation on all.

							==> Table of Killable Prediction Rules and Evaluation Metrics for {file_name}
							==> [BEG_SUMMARY]
									original xxx predicted xxx matched xxx
									precision xxx% recall xxx% f1_score xxx
									rules xxx minimal xxx optimal_ratio xxx%
							==>	[END_SUMMARY]
							==> [BEG_EVALUATION]
									PID EXE MUT LEN RESULT KILLED ALIVED CONFIDENCE(%)
									......
							==> [END_EVALUATION]
							==> [BEG_UNCOVERED]
									EID MID RESULT CLASS OPERATOR FUNCTION LINE LOCATION PARAMETER
									......
							==> [END_UNCOVERED]
							==> End_Of_File
		"""
		with open(file_path, 'w') as writer:
			self.__opens__(writer, "Table of Killable Prediction with Evaluation Metrics in {}.\n".format(self.file_name))

			orig_number, pred_number, matc_number, precision, recall, f1_score = middle.prf_evaluate(rules, used_tests)
			target_keys = set()
			for execution in middle.get_document().exec_space.get_executions():
				if execution.get_mutant().is_killed_in(used_tests):
					pass
				else:
					target_keys.add(execution)
			minimal_rules, uncovered_keys = middle.minimal_rules(rules, target_keys)
			all_rules_number, min_rules_number = len(rules), len(minimal_rules)
			optimal_ratio = self.__ratio__(min_rules_number / (orig_number + 0.000000001))
			precision = self.__ratio__(precision)
			recall = self.__ratio__(recall)

			self.__write__("\n[BEG_SUMMARY]\n")
			self.__write__("\tOriginal\t{}\tPredicted\t{}\tMatched\t{}\n".format(orig_number, pred_number, matc_number))
			self.__write__("\tPrecision\t{}%\tRecall\t{}%\tF1_Score\t{}\n".format(precision, recall, f1_score))
			self.__write__("\tALL_RULES\t{}\tMIN_RULES\t{}\tOPTIMAL_RATIO\t{}%\n".format(all_rules_number, min_rules_number, optimal_ratio))
			self.__write__("[END_SUMMARY]\n")

			self.__write__("\n[BEG_RULES]\n")
			self.__write__("\tPID\tEXE\tMUT\tLENGTH\tRESULT\tKILLED\tALIVE\tCONFIDENCE(%)\n")
			for rule in minimal_rules:
				self.__write__("\t{}\n".format(self.__format_rule__(rule, used_tests)))
			self.__write__("[END_RULES]\n")

			self.__write__("\n[BEG_UNCOVERED]\n")
			self.__write__("\tEID\tMID\tRESULT\tCLASS\tOPERATOR\tFUNCTION\tLINE\tLOCATION\tPARAMETER\n")
			for execution in uncovered_keys:
				eid = execution.get_eid()
				self.__write__("\t{}\t{}\n".format(eid, self.__format_mutant__(execution.get_mutant(), used_tests)))
			self.__write__("[END_UNCOVERED]\n")

			self.__close__("\nEnd_Of_File")
		return


def do_fpm_mining(c_document: jctest.CDocument, inputs: jerule.KillPredictionInputs,
				  o_directory: str, file_name: str, used_tests, is_reported: bool):
	"""
	:param c_document: 		the document of mutation testing project and its data source
	:param inputs: 			the input module to drive the pattern mining procedures
	:param o_directory:		the directory where the pattern files will be generated
	:param file_name:		the name of the project file as the prefix of output files
	:param used_tests:		the set of test cases used to evaluate patterns or None for all
	:param is_reported:		whether to report the mining algorithm debugging details
	:return:
	"""
	## 1. collect the features from undetected mutants within the project
	features = set()
	for execution in inputs.get_document().exec_space.get_executions():
		execution: jecode.MerExecution
		if not execution.get_mutant().is_killed_in(used_tests):
			for feature in execution.get_features():
				features.add(feature)

	## 2. construct the frequent pattern mining and its middle module
	fp_miner = KillPredictionFPMiner(inputs)
	fp_middle = fp_miner.middle
	ou_rules = fp_miner.mine(features, used_tests, is_reported, o_directory, c_document)

	## 3. write the output patterns and their scores to specified directory
	writer = KillPredictionWriter(c_document)
	writer.write_rules_objects(fp_middle, ou_rules, os.path.join(o_directory, file_name + ".fpm.p2o"), used_tests)
	writer.write_rules_metrics(fp_middle, ou_rules, os.path.join(o_directory, file_name + ".fpm.p2e"), used_tests)
	return


def do_dtm_mining(c_document: jctest.CDocument, inputs: jerule.KillPredictionInputs,
				  o_directory: str, file_name: str, used_tests, is_reported: bool):
	"""
	:param c_document: 		the document of mutation testing project and its data source
	:param inputs: 			the input module to drive the pattern mining procedures
	:param o_directory:		the directory where the pattern files will be generated
	:param file_name:		the name of the project file as the prefix of output files
	:param used_tests:		the set of test cases used to evaluate patterns or None for all
	:param is_reported:		whether to report the mining algorithm debugging details
	:return:
	"""
	## 1. construct the decision tree based mining and its middle module
	dt_miner = KillPredictionDTMiner(inputs)
	dt_middle = dt_miner.middle
	ou_rules = dt_miner.mine(None, used_tests, is_reported, o_directory, c_document)

	## 2. write the output patterns and their scores to specified directory
	writer = KillPredictionWriter(c_document)
	writer.write_rules_objects(dt_middle, ou_rules, os.path.join(o_directory, file_name + ".dtm.p2o"), used_tests)
	writer.write_rules_metrics(dt_middle, ou_rules, os.path.join(o_directory, file_name + ".dtm.p2e"), used_tests)
	return


def do_mining(c_document: jctest.CDocument, m_document: jecode.MerDocument,
			  output_directory: str, file_name: str, used_tests, is_reported: bool,
			  max_length: int, min_support: int, min_confidence: float, max_confidence: float):
	"""
	:param used_tests:
	:param c_document: original document
	:param m_document: encoded document
	:param output_directory: the output directory where files are printed
	:param file_name: the project name
	:param is_reported: whether to report the pattern mining details
	:param max_length: the maximal length of generated patterns
	:param min_support: minimal support for mining
	:param min_confidence: minimal confidence for mining
	:param max_confidence: maximal confidence for mining
	:return:
	"""
	# I. create output directory for pattern generation
	print("BEG-Project #{}".format(file_name))
	o_directory = os.path.join(output_directory, file_name)
	if not os.path.exists(o_directory):
		os.mkdir(o_directory)
	print("\tI. Load {} executions between {} mutants and {} tests.".format(
		len(m_document.exec_space.get_executions()),
		len(m_document.exec_space.get_mutants()),
		len(m_document.test_space.get_test_cases())))

	# II. construct the input module for driving pattern mining procedures
	inputs = jerule.KillPredictionInputs(m_document, max_length, min_support, min_confidence, max_confidence)
	print("\tII. Inputs: max_len = {}; min_supp = {}; min_conf = {}; max_conf = {}.".format(inputs.get_max_length(),
																							inputs.get_min_support(),
																							inputs.get_min_confidence(),
																							inputs.get_max_confidence()))

	## III. perform frequent pattern mining and evaluate it
	print("\tIII. Perform Frequent Pattern Mining and Evaluate for Output.")
	do_fpm_mining(c_document, inputs, o_directory, file_name, used_tests, is_reported)

	## IV. perform decision tree based mining and evaluated
	print("\tIV. Perform Decision Tree Mining and Evaluate it for Output.")
	old_max_length = inputs.get_max_length()
	inputs.max_length = 256
	do_dtm_mining(c_document, inputs, o_directory, file_name, used_tests, is_reported)
	inputs.max_length = old_max_length

	## VI. end of all of the mutation testing project
	print("END-Project #{}".format(file_name))
	return


def main(project_directory: str, encoding_directory: str, output_directory: str):
	"""
	:param project_directory:
	:param encoding_directory:
	:param output_directory:
	:return:
	"""
	## establish the pattern mining and output parameters
	max_length, min_support, min_confidence, max_confidence, used_tests, is_reported = 1, 2, 0.70, 0.95, None, True

	## testing on every project in the project directory
	for file_name in os.listdir(project_directory):
		## load document and encoded features into memory
		c_document_directory = os.path.join(project_directory, file_name)
		m_document_directory = os.path.join(encoding_directory, file_name)
		c_document = jctest.CDocument(c_document_directory, file_name)
		m_document = jecode.MerDocument(m_document_directory, file_name)

		## perform pattern mining and evaluation proceed
		do_mining(c_document, m_document,
				  output_directory, file_name, used_tests, is_reported,
				  max_length, min_support, min_confidence, max_confidence)
		print()
	return


## execution script


if __name__ == "__main__":
	proj_directory = "/home/dzt2/Development/Data/zexp/features"
	enco_directory = "/home/dzt2/Development/Data/zexp/encoding"
	outs_directory = "/home/dzt2/Development/Data/zexp/patterns"
	main(proj_directory, enco_directory, outs_directory)
	exit(0)

