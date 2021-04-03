"""This file implements the pattern mining for mutation testing in form of reachability, infection and propagation"""


import os
from typing import TextIO
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest
import pydotplus
from sklearn import tree
from scipy import sparse
import com.jcsa.mine.feature as jcfeature


# writer module for presentation


class RIPMineWriter:
	"""
	It implements the information output to text file for each generated RIPPattern
	"""

	def __init__(self):
		self.writer = None
		return

	# basic methods

	def __output__(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		return

	@staticmethod
	def __percentage__(ratio: float):
		return int(ratio * 1000000) / 10000.0

	@staticmethod
	def __proportion__(x: int, y: int):
		if x == 0:
			ratio = 0.0
		else:
			ratio = x / y
		return RIPMineWriter.__percentage__(ratio)

	@staticmethod
	def __prf_metric__(orig_samples: set, patt_samples: set):
		"""
		:param orig_samples: the collection of data samples from original document
		:param patt_samples: the collection of data samples from RIPPattern(s)
		:return: precision, recall, f1_score
		"""
		como_samples = orig_samples & patt_samples
		common = len(como_samples)
		if common == 0:
			precision = 0.0
			recall = 0.0
			f1_score = 0.0
		else:
			precision = common / len(patt_samples)
			recall = common / len(orig_samples)
			f1_score = 2 * precision * recall / (precision + recall)
		return precision, recall, f1_score

	# xxx.mpt

	def __write_pattern_head__(self, pattern: jcfeature.RIPPattern):
		"""
		:param pattern:
		:return:
				Summary 	Length Executions Mutations
				Counting	title UR UI UP KI UK CC
				Estimate	title total support confidence
		"""
		# Summary Length Executions Sequences Mutations
		self.__output__("\t{}\n".format("@SUMMARY"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\n".format("attribute", "length", "exe_num", "seq_num", "mut_num"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\n".format("value", len(pattern), len(pattern.get_executions()),
														  len(pattern.get_sequences()), len(pattern.get_mutants())))
		# Counting	title UR UI UP KI UK CC
		self.__output__("\t{}\n".format("@COUNTING"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("sample_class", "UR", "UI", "UP", "KI", "UK", "CC"))
		ur, ui, up, ki, uk, cc = pattern.counting(jcfeature.EXE_SAMPLE_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", ur, ui, up, ki, uk, cc))
		ur, ui, up, ki, uk, cc = pattern.counting(jcfeature.SEQ_SAMPLE_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", ur, ui, up, ki, uk, cc))
		ur, ui, up, ki, uk, cc = pattern.counting(jcfeature.MUT_SAMPLE_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", ur, ui, up, ki, uk, cc))
		# Estimate sample_class support_class total support negative confidence (%)
		self.__output__("\t{}\n".format("@ESTIMATE"))
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("sample_class", "support_class", "total", "support",
																  "negative", "confidence", "confidence(%)"))
		# Estimate EXE {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(jcfeature.EXE_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(jcfeature.EXE_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(jcfeature.EXE_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		# Estimate SEQ {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(jcfeature.SEQ_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(jcfeature.SEQ_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(jcfeature.SEQ_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		# Estimate MUT {UNK, WCC, SCC} total support negative confidence (%)
		total, support, confidence = pattern.estimate(jcfeature.MUT_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "UNK", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(jcfeature.MUT_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "WCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(jcfeature.MUT_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS)
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "SCC", total, support, total - support,
																  confidence, RIPMineWriter.__percentage__(confidence)))
		return

	def __write_pattern_body__(self, pattern: jcfeature.RIPPattern):
		"""
		:param pattern:
		:return:
			condition category operator execution statement location parameter
			Mutant Result Class Operator Function Line Location Parameter
		"""
		# condition category operator validate execution statement location parameter
		self.__output__("\t@CONDITION\n")
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("index", "category", "operator",
																  "execution", "statement", "location", "parameter"))
		index = 0
		for condition in pattern.get_conditions():
			index += 1
			category = condition.get_category()
			operator = condition.get_operator()
			execution= condition.get_execution()
			statement= "\"" + execution.get_statement().get_cir_code() + "\""
			location = "\"" + condition.get_location().get_cir_code() + "\""
			parameter = "{" + str(condition.get_parameter()) + "}"
			self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, category, operator, execution,
																	  statement, location, parameter))
		# mutant Result Class Operator Function Line Location Parameter
		self.__output__("\t@MUTATION\n")
		self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("index", "result", "class", "operator",
																  "line", "location", "parameter"))
		for mutant in pattern.get_mutants():
			mutant: jcmuta.Mutant
			index = mutant.get_muta_id()
			result = pattern.get_classifier().__find__(mutant)
			mutation_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.mutation.get_mutation_operator()
			location = mutant.mutation.get_location()
			parameter = mutant.mutation.get_parameter()
			line = location.line_of(False)
			code = "\"" + location.get_code(True) + "\""
			self.__output__("\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, result, mutation_class,
																	  operator, line, code, parameter))
		return

	def __write_pattern__(self, pattern: jcfeature.RIPPattern):
		self.__output__("#BEG\n")
		self.__write_pattern_head__(pattern)
		self.__write_pattern_body__(pattern)
		self.__output__("#END\n")

	def __write_patterns__(self, patterns, file_path: str):
		"""
		:param patterns:
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			for pattern in patterns:
				pattern: jcfeature.RIPPattern
				self.__write_pattern__(pattern)
				self.__output__("\n")
		return

	# xxx.bpt

	def __write_best_pattern__(self, mutant: jcmuta.Mutant, pattern: jcfeature.RIPPattern):
		"""
		:param mutant:
		:param pattern:
		:return: 	Mutant 		Result 		Class 		Operator 	Line 		Location 	Parameter
					Condition 	category 	operator 	execution 	statement	location	parameter	+
		"""
		# Mutant 		Result 		Class 		Operator 	Line 		Location 	Parameter
		self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("ID", "result", "class", "operator",
															  "line", "location", "parameter"))
		mutant_id = mutant.get_muta_id()
		result = pattern.get_classifier().__find__(mutant)
		mutation_class = mutant.get_mutation().get_mutation_class()
		operator = mutant.mutation.get_mutation_operator()
		location = mutant.mutation.get_location()
		parameter = mutant.mutation.get_parameter()
		line = location.line_of(False)
		code = "\"" + location.get_code(True) + "\""
		self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(mutant_id, result, mutation_class,
															  operator, line, code, parameter))
		# Condition 	category 	operator 	execution 	statement	location	parameter	+
		self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("condition", "category", "operator", "execution",
															  "statement", "location", "parameter"))
		index = 0
		for condition in pattern.get_conditions():
			index += 1
			category = condition.get_category()
			operator = condition.get_operator()
			execution = condition.get_execution()
			statement = "\"" + execution.get_statement().get_cir_code() + "\""
			location = "\"" + condition.get_location().get_cir_code() + "\""
			parameter = "{" + str(condition.get_parameter()) + "}"
			self.__output__("{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, category, operator, execution,
																  statement, location, parameter))
		return

	def __write_best_patterns__(self, mutant_pattern_dict: dict, file_path: str):
		"""
		:param mutant_pattern_dict: Mapping from Mutant to RIPPattern it best matches with
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			for mutant, pattern in mutant_pattern_dict.items():
				mutant: jcmuta.Mutant
				pattern: jcfeature.RIPPattern
				self.__write_best_pattern__(mutant, pattern)
				self.__output__("\n")
		return

	# xxx.sum

	@staticmethod
	def __evaluate__(output: jcfeature.RIPMineOutput, patterns, sample_class, support_class):
		"""
		:param output:
		:param patterns:
		:return: doc_samples pat_samples reduce precision recall f1_score
		"""
		number = len(patterns)
		orig_samples = output.get_doc_samples(sample_class)
		patt_samples = output.get_pat_samples(sample_class)
		orig_samples, __ = output.get_classifier().partition(orig_samples, support_class)
		reduce = number / len(orig_samples)
		precision, recall, f1_score = RIPMineWriter.__prf_metric__(orig_samples, patt_samples)
		reduce = RIPMineWriter.__percentage__(reduce)
		precision = RIPMineWriter.__percentage__(precision)
		recall = RIPMineWriter.__percentage__(recall)
		return len(orig_samples), len(patt_samples), reduce, precision, recall, f1_score

	def __write_evaluation_all__(self, output: jcfeature.RIPMineOutput, patterns):
		"""
		:param output:
		:return:  doc_samples pat_samples reduce precision recall f1_score
		"""
		self.__output__("@Cost-Effective\n")
		# sample_class support_class doc_samples pat_samples reduce precision recall f1_score
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("sample_class", "support_class", "orig_samples",
																	"patt_samples", "reduce(%)", "precision(%)",
																	"recall(%)", "f1_score"))
		# EX_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter.\
			__evaluate__(output, patterns, jcfeature.EXE_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.EXE_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.EXE_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("EXE", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		# SQ_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.SEQ_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.SEQ_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.SEQ_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("SEQ", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		# MU_SAMPLE_CLASS
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.MUT_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "UNK", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.MUT_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "WCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		orig_samples, patt_samples, reduce, precision, recall, f1_score = RIPMineWriter. \
			__evaluate__(output, patterns, jcfeature.MUT_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS)
		self.__output__("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("MUT", "SCC", orig_samples,
																	patt_samples, reduce, precision, recall, f1_score))
		return

	def __write_evaluation_one__(self, patterns):
		"""
		:param patterns:
		:return: 	index length executions sequences mutants
		"""
		index = 0
		self.__output__("@Counting\n")
		self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format("index", "length", "exe_numb", "seq_numb", "mut_numb"))
		for pattern in patterns:
			index += 1
			pattern: jcfeature.RIPPattern
			self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format(index, len(pattern), len(pattern.get_executions()),
															len(pattern.get_sequences()), len(pattern.get_mutants())))
		return

	def __write_evaluation_two__(self, output: jcfeature.RIPMineOutput, patterns, sample_class, support_class):
		"""
		:param patterns:
		:return: 	title uk_exe_supp uk_exe_conf
					title wc_exe_supp wc_exe_conf
					title sc_exe_supp sc_exe_conf
					title uk_seq_supp uk_seq_conf
					title wc_seq_supp wc_seq_conf
					title sc_seq_supp sc_seq_conf
					title uk_mut_supp uk_mut_conf
					title wc_mut_supp wc_mut_conf
					title sc_mut_supp sc_mut_conf
		"""
		if support_class == jcfeature.UNK_SUPPORT_CLASS:
			support_name = "UNK"
		elif support_class == jcfeature.WCC_SUPPORT_CLASS:
			support_name = "WCC"
		elif support_class == jcfeature.SCC_SUPPORT_CLASS:
			support_name = "SCC"
		elif support_class == jcfeature.KID_SUPPORT_CLASS:
			support_name = "KID"
		else:
			support_name = "XXX"
		self.__output__("@Measure_{}_{}\n".format(sample_class, support_name))
		self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format("index", "support", "confidence(%)", "recall(%)", "f1_score"))
		index = 0
		orig_samples = output.get_doc_samples(sample_class)
		for pattern in patterns:
			pattern: jcfeature.RIPPattern
			index += 1
			total, support, confidence = pattern.estimate(sample_class, support_class)
			patt_samples = pattern.get_samples(sample_class)
			precision, recall, f1_score = self.__prf_metric__(orig_samples, patt_samples)
			self.__output__("\t{}\t{}\t{}\t{}\t{}\n".format(index, support, RIPMineWriter.__percentage__(confidence),
														  	RIPMineWriter.__percentage__(recall), f1_score))
		return

	def __write_evaluation_sum__(self, output: jcfeature.RIPMineOutput, file_path: str):
		"""
		:param output:
		:param file_path:
		:return:
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			patterns = output.get_subsuming_patterns(False)
			self.__write_evaluation_all__(output, patterns)
			self.__output__("\n")
			self.__write_evaluation_one__(output.get_minimal_patterns(jcfeature.SEQ_SAMPLE_CLASS))
			self.__output__("\n")
			self.__write_evaluation_two__(output, patterns, output.inputs.get_sample_class(),
										  output.inputs.get_support_class())
			self.__output__("\n")
		return

	def write_to(self, output: jcfeature.RIPMineOutput, directory: str):
		"""
		:param output:
		:param directory:
		:return: xxx.mpt xxx.bpt xxx.sum
		"""
		file_name = output.get_document().get_program().name
		self.__write_patterns__(output.get_subsuming_patterns(True), os.path.join(directory, file_name + ".mpt"))
		self.__write_best_patterns__(output.get_best_patterns(jcfeature.SEQ_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS),
									 os.path.join(directory, file_name + ".bpt"))
		self.__write_evaluation_sum__(output, os.path.join(directory, file_name + ".sum"))
		return


# pattern mining machine


class RIPFPMiner:
	"""
	It implements the pattern mining using frequent pattern mining.
	"""

	def __init__(self):
		self.middle = None
		return

	def __root_words__(self):
		"""
		:return: The set of all possible words occurring in the given sequences of target samples.
		"""
		self.middle: jcfeature.RIPMineMiddle
		inputs = self.middle.inputs
		support_sequences, __ = inputs.get_classifier().partition(inputs.get_document().get_sequences(),
																  inputs.get_support_class())
		support_words = set()
		for support_sequence in support_sequences:
			support_sequence: jctest.SymSequence
			for support_word in support_sequence.get_words():
				support_word: str
				support_words.add(support_word.strip())
		words = list()
		for support_word in support_words:
			if len(support_word) > 0:
				words.append(support_word)
		return words

	def __pass_mine__(self, pattern: jcfeature.RIPPattern):
		"""
		:param pattern:
		:return: whether the mining should pass through the node
		"""
		self.middle: jcfeature.RIPMineMiddle
		inputs = self.middle.inputs
		length, support, confidence = self.middle.estimate(pattern)
		return length < inputs.get_max_length() and \
			   support >= inputs.get_min_support() and \
			   confidence <= inputs.get_max_confidence()

	def __recur_mine__(self, parent: jcfeature.RIPPattern, words: list):
		"""
		:param parent:
		:param words:
		:return: recursively solve the frequent pattern mining
		"""
		if self.__pass_mine__(parent):
			for j in range(0, len(words)):
				word = words[j].strip()
				child = self.middle.get_child(parent, word)
				if child != parent:
					self.__recur_mine__(child, words[j + 1:])
		return

	def mine(self, inputs: jcfeature.RIPMineInputs):
		"""
		:param inputs:
		:return: RIPMineOutput
		"""
		# 1. collect all the words w.r.t. supporting class of sequences
		self.middle = jcfeature.RIPMineMiddle(inputs)
		words = self.__root_words__()

		# 2. ready to mine within global words library
		print("\t\t*-- Frequent pattern mining over", len(words), "symbolic words.")
		for i in range(0, len(words)):
			word = words[i].strip()
			root = self.middle.get_root(word)
			self.__recur_mine__(root, words[i + 1:])

		# 3. generate good patterns for producing outputs
		return jcfeature.RIPMineOutput(self.middle)


def do_frequent_mining(mine_inputs: jcfeature.RIPMineInputs, output_directory: str):
	"""
	:param mine_inputs:
	:param output_directory:
	:return: Perform frequent pattern mining algorithm
	"""
	output_directory.strip()
	miner = RIPFPMiner()
	return miner.mine(mine_inputs)


class RIPDTMiner:
	"""
	It implements the pattern mining via decision tree model
	"""

	def __init__(self):
		self.classifier = None
		self.middle = None
		self.X = None
		self.Y = list()
		self.W = list()
		return

	def __input_context__(self, inputs: jcfeature.RIPMineInputs):
		"""
		:param inputs:
		:return:	(1) update context information
					(2) update X, Y, W to train the decision tree
		"""
		self.middle = jcfeature.RIPMineMiddle(inputs)
		D = dict()
		self.Y.clear()
		self.W.clear()
		for sequence in inputs.get_document().get_sequences():
			sequence: jctest.SymSequence
			total, support, confidence = inputs.get_classifier().estimate([sequence], inputs.get_support_class())
			if support > 0:
				self.Y.append(1)
			else:
				self.Y.append(0)
			for word in sequence.get_words():
				if not (word in D):
					D[word] = len(self.W)
					self.W.append(word)
		rows, columns, dataset = list(), list(), list()
		line = 0
		for execution in inputs.get_document().get_sequences():
			execution: jctest.SymSequence
			execution_words = set()
			for instance in execution.get_executions():
				instance: jctest.SymExecution
				for word in instance.get_words():
					execution_words.add(str(word))
			for word in execution_words:
				column = D[word]
				rows.append(line)
				columns.append(column)
				dataset.append(1)
			line += 1
		self.X = sparse.coo_matrix((dataset, (rows, columns)),
								   shape=(len(inputs.get_document().get_sequences()), len(self.W)))
		return

	@staticmethod
	def __normalize__(text: str):
		new_text = ""
		for k in range(0, len(text)):
			char = text[k]
			if char in ['{', '}', '\"']:
				char = ' '
			new_text += char
		return new_text

	def __gen_normal_WN__(self):
		"""
		:return: sequence of normalized words to describe the RIP conditions.
		"""
		self.middle: jcfeature.RIPMineMiddle
		WN = list()
		document = self.middle.inputs.get_document()
		for word in self.W:
			condition = document.conditions.get_condition(word)
			category = condition.get_category()
			operator = condition.get_operator()
			execution = condition.get_execution()
			location = condition.get_location().get_cir_code()
			if condition.get_parameter() is None:
				parameter = "null"
			else:
				parameter = condition.get_parameter().get_code()
			norm_word = "[{}, {}, {}, \"{}\", {}]".format(category, operator, execution, location, parameter)
			WN.append(RIPDTMiner.__normalize__(norm_word))
		return WN

	def __fit_decisions__(self, tree_file: str):
		"""
		:param tree_file:
		:return: create a classifier and training it using the context data and return the predicted results
		"""
		self.middle: jcfeature.RIPMineMiddle
		inputs = self.middle.inputs
		self.classifier = tree.DecisionTreeClassifier(min_samples_leaf=inputs.get_min_support())
		self.classifier.fit(self.X, self.Y)
		YP = self.classifier.predict(self.X)
		# print(metrics.classification_report(self.Y, YP, target_names=["Killable", "Equivalent"]))
		if not(tree_file is None):
			W = self.__gen_normal_WN__()
			dot_data = tree.export_graphviz(self.classifier, out_file=None, feature_names=W,
											class_names=["Killable", "Equivalent"])
			graph = pydotplus.graph_from_dot_data(dot_data)
			graph.write_pdf(tree_file)
		return YP

	def __get_leaf_path__(self, YP):
		"""
		:return: selecting leaf that decides type as equivalent and their corresponding path in the program
		"""
		self.middle: jcfeature.RIPMineMiddle
		inputs = self.middle.inputs
		self.classifier: tree.DecisionTreeClassifier
		leaf_path = dict()	# exec_id --> leaf_id, node_path
		X_array = self.X.toarray()
		node_indicators = self.classifier.decision_path(X_array)
		leave_ids = self.classifier.apply(X_array)
		for exec_id in range(0, len(inputs.get_document().get_sequences())):
			if YP[exec_id] == 1:
				leaf_node_id = leave_ids[exec_id]
				node_index = node_indicators.indices[
							 node_indicators.indptr[exec_id]:
							 node_indicators.indptr[exec_id + 1]]
				leaf_path[exec_id] = (leaf_node_id, node_index)
		return leaf_path

	def __path_patterns__(self, leaf_path: dict):
		"""
		:param leaf_path:
		:return: leaf_node_id, node_id_list
		"""
		self.middle: jcfeature.RIPMineMiddle
		X = self.X.toarray()
		patterns = set()
		features = self.classifier.tree_.feature
		thresholds = self.classifier.tree_.threshold
		for exec_id, value in leaf_path.items():
			words = list()
			leaf_id = value[0]
			node_path = value[1]
			for node_id in node_path:
				if node_id != leaf_id:
					word = self.W[features[node_id]]
					word: str
					if X[exec_id, features[node_id]] > thresholds[node_id]:
						words.append(word)		# select True-branch words
			pattern = self.middle.get_root("")
			for word in words:
				pattern = self.middle.get_child(pattern, word)
				patterns.add(pattern)
			patterns.add(pattern)
		return patterns

	def mine(self, inputs: jcfeature.RIPMineInputs, tree_path: str):
		self.__input_context__(inputs)
		YP = self.__fit_decisions__(tree_path)
		leaf_path = self.__get_leaf_path__(YP)
		patterns = self.__path_patterns__(leaf_path)
		self.middle: jcfeature.RIPMineMiddle
		return jcfeature.RIPMineOutput(self.middle)


def do_decision_mining(mine_inputs: jcfeature.RIPMineInputs, output_directory: str):
	"""
	:param mine_inputs:
	:param output_directory:
	:return: Perform decision tree based pattern mining algorithm
	"""
	miner = RIPDTMiner()
	filename = mine_inputs.get_document().get_program().name + ".pdf"
	return miner.mine(mine_inputs, os.path.join(output_directory, filename))


# main procedure methods


def get_model_directory_name(model_name: str, sample_class, support_class):
	"""
	:param model_name:
	:param sample_class:
	:param support_class:
	:return:
	"""
	if sample_class == jcfeature.MUT_SAMPLE_CLASS:
		sample_class = "mut"
	elif sample_class == jcfeature.EXE_SAMPLE_CLASS:
		sample_class = "exe"
	elif sample_class == jcfeature.SEQ_SAMPLE_CLASS:
		sample_class = "seq"
	else:
		sample_class = "xxx"
	if support_class == jcfeature.UNK_SUPPORT_CLASS:
		support_strategy = "unk"
	elif support_class == jcfeature.WCC_SUPPORT_CLASS:
		support_strategy = "wcc"
	elif support_class == jcfeature.SCC_SUPPORT_CLASS:
		support_strategy = "scc"
	elif support_class == jcfeature.KID_SUPPORT_CLASS:
		support_strategy = "kid"
	else:
		support_strategy = "xxx"
	return "{}_{}_{}".format(model_name, sample_class, support_strategy)


def do_mining(document: jctest.CDocument, output_directory: str, model_name: str,
			  sample_class, support_class, used_tests, max_length: int,
			  min_support: int, min_confidence: float, max_confidence: float,
			  do_model_mining):
	"""
	:param document:			It provides original and entire dataset for being mined.
	:param output_directory:	The directory where output-directory is created for.
	:param model_name:			The output files are preserved in output_directory/model_name
	:param sample_class:		MU_SAMPLE_CLASS, SQ_SAMPLE_CLASS, EX_SAMPLE_CLASS
	:param support_class:		UK_SUPPORT_CLASS, WC_SUPPORT_CLASS, SC_SUPPORT_CLASS
	:param used_tests:			The set of test cases assumed to be used during testing.
	:param max_length:			The maximal length of generated patterns for presentation.
	:param min_support:			The minimal number of supporting samples required for each pattern.
	:param min_confidence:		The minimal confidence achieved by the good patterns produced.
	:param max_confidence:		The maximal confidence achieved to terminate the searching on tree.
	:param do_model_mining:		do_frequent_mining, do_decision_mining, ...
	:return:
	"""
	model_name = get_model_directory_name(model_name, sample_class, support_class)
	print("\tDo Mining on", document.get_program().name, "for model of", model_name)
	# 1. create inputs of mining algorithm
	inputs = jcfeature.RIPMineInputs(document, used_tests, sample_class, support_class,
									 max_length, min_support, min_confidence, max_confidence)
	print("\t\t1. Create data mining inputs for", inputs.get_document().get_program().name)
	print("\t\t*-- parameters: {", inputs.get_sample_class(), inputs.get_max_length(),
		  inputs.get_min_support(), inputs.get_min_confidence(), inputs.get_max_confidence(), "}")

	# 2. perform data mining algorithms on
	if not os.path.exists(output_directory):
		os.mkdir(output_directory)
	output_directory = os.path.join(output_directory, model_name)
	if not os.path.exists(output_directory):
		os.mkdir(output_directory)
	output = do_model_mining(mine_inputs=inputs, output_directory=output_directory)
	output: jcfeature.RIPMineOutput
	print("\t\t2. Produce", len(output.get_patterns()), "good patterns from", inputs.get_document().get_program().name)

	# 3. Write the pattern outputs to specified directory
	writer = RIPMineWriter()
	writer.write_to(output, output_directory)
	print("\t\t3. Consume", len(output.get_subsuming_patterns(False)), "subsuming patterns to", output_directory)
	return


def do_mining_on_classes(document: jctest.CDocument, output_directory: str, model_name: str,
						 used_tests, max_length: int, min_support: int, min_confidence: float,
						 max_confidence: float, do_model_mining):
	"""
	:param document:
	:param output_directory:
	:param model_name:
	:param used_tests:
	:param max_length:
	:param min_support:
	:param min_confidence:
	:param max_confidence:
	:param do_model_mining:
	:return:
	"""
	do_mining(document, output_directory, model_name, jcfeature.MUT_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.MUT_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.MUT_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.SEQ_SAMPLE_CLASS, jcfeature.UNK_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.SEQ_SAMPLE_CLASS, jcfeature.WCC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	do_mining(document, output_directory, model_name, jcfeature.SEQ_SAMPLE_CLASS, jcfeature.SCC_SUPPORT_CLASS,
			  used_tests, max_length, min_support, min_confidence, max_confidence, do_model_mining)
	return


def do_testing(inputs_directory: str, output_directory: str, select_tests: bool):
	"""
	:param inputs_directory:
	:param output_directory:
	:param select_tests: True to use randomly selected test suite for classifier
	:return:
	"""
	for file_name in os.listdir(inputs_directory):
		c_document = jctest.CDocument(os.path.join(inputs_directory, file_name), file_name)
		print("Perform testing for", file_name, "with", len(c_document.get_mutants()), "mutants and",
			  len(c_document.get_project().test_space.get_test_cases()), "test cases annotated with",
			  len(c_document.get_conditions_lib().get_all_conditions()), "symbolic conditions used.")

		if select_tests:
			evaluator = jcmuta.MutationTestEvaluation(c_document.get_project())
			selected_mutants = evaluator.select_mutants_by_classes(["STRP", "BTRP"])
			mutation_tests = evaluator.select_tests_for_mutants(selected_mutants)
			random_number = int(len(c_document.get_project().test_space.get_test_cases()) * 0.01)
			random_tests = evaluator.select_tests_for_random(random_number)
			used_tests = random_tests | mutation_tests
			score = evaluator.measure_score(None, used_tests)
			print("\t==> Select", len(used_tests), "test cases with", int(score * 1000000) / 10000.0, "score.")
		else:
			used_tests = None
			print("\t==> Select all the possible test inputs from mutation test project...")

		# mining on all classes
		do_mining_on_classes(c_document, output_directory, "dtm", used_tests, 32, 2, 0.70, 0.95, do_decision_mining)
		do_mining_on_classes(c_document, output_directory, "fpm", used_tests, 1,  2, 0.70, 0.95, do_frequent_mining)
		print()
	return


if __name__ == "__main__":
	prev_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Data/upatterns"
	do_testing(prev_path, post_path, False)

