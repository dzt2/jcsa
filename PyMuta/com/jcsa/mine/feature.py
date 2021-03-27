"""
This file defines the data model for describing the inputs segment of mining algorithm (pre-processing)
"""
from typing import TextIO

import com.jcsa.libs.base as jcbase
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


UR_CLASS = "UR"		# the testing failed to reach the faulty point
UI_CLASS = "UI"		# the testing reach but failed to infect state
UP_CLASS = "UP"		# the testing infect but failed to kill mutant
KI_CLASS = "KI"		# the testing manage to kill the target mutant


class RIPClassifier:
	"""
	It defines the classification and evaluation system for data mining
	"""

	def __init__(self, tests):
		"""
		:param tests:
				1. the collection of test cases (or their unique integer ID) used to decide whether mutant is killed
				2. None to establish the entire test cases in space used in experiment as tests
		"""
		self.tests = tests
		self.solutions = dict()		# muta_id --> [UR|UI|UP|KI]
		return

	def __solving__(self, mutant: jcmuta.Mutant):
		"""
		:param mutant:
		:return: 	UR	---	if the mutant is not reached by any tests
					UI	---	if the mutant is reached but not infected
					UP	---	if the mutant is infected but not killed
					KI	--- if the mutant is successfully killed by given tests
		"""
		if not(mutant.get_muta_id() in self.solutions):
			s_result = mutant.get_result()
			w_result = mutant.get_w_mutant().get_result()
			c_result = mutant.get_c_mutant().get_result()
			if s_result.is_killed_in(self.tests):
				label = KI_CLASS
			elif w_result.is_killed_in(self.tests):
				label = UP_CLASS
			elif c_result.is_killed_in(self.tests):
				label = UI_CLASS
			else:
				label = UR_CLASS
			self.solutions[mutant.get_muta_id()] = label
		label = self.solutions[mutant.get_muta_id()]
		label: str
		return label

	def __classify__(self, sample):
		if isinstance(sample, jctest.SymSequence):
			mutant = sample.get_mutant()
		else:
			sample: jcmuta.Mutant
			mutant = sample
		return self.__solving__(mutant)

	def classify(self, samples):
		"""
		:param samples: the collection of Mutant or SymSequence
		:return: mapping from {UR|UI|UP|KI} --> set of samples w.r.t.
		"""
		results = dict()
		results[UR_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			results[self.__classify__(sample)].add(sample)
		return results

	def counting(self, samples):
		"""
		:param samples:
		:return: 	ur, ui, up, ki, uk, cc
					ur --- the number of samples failed to be reached
					ui --- the number of samples failed to be infected but reached
					up --- the number of samples failed to be killed but infected
					ki --- the number of samples successfully to be killed by tests
					uk --- the number of samples failed to be killed {ur + ui + up}
					cc --- the number of samples with coincidental correct {ui + up}
		"""
		ur, ui, up, ki = 0, 0, 0, 0
		for sample in samples:
			label = self.__classify__(sample)
			if label == KI_CLASS:
				ki += 1
			elif label == UP_CLASS:
				up += 1
			elif label == UI_CLASS:
				ui += 1
			else:
				ur += 1
		return ur, ui, up, ki, ur + ui + up, ui + up

	def estimate(self, samples, supp_class):
		"""
		:param samples:
		:param supp_class:
				1. True to take strong coincidental correctness (only UP_CLASS are under consideration)
				2. False to take weak coincidental correctness (union of UP_CLASS and UI_CLASS account)
				3. None to take all the undetected samples in support (UR + UI + UP)
		:return: total, support, confidence
		"""
		ur, ui, up, ki, uk, cc = self.counting(samples)
		if supp_class is None:
			support = uk
		elif supp_class:
			support = up
		else:
			support = cc
		total = support + ki
		if support > 0:
			confidence = support / total
		else:
			confidence = 0.0
		return total, support, confidence

	def select(self, samples, supp_class):
		"""
		:param samples:
		:param supp_class:
				1. True to take strong coincidental correctness (only UP_CLASS are under consideration)
				2. False to take weak coincidental correctness (union of UP_CLASS and UI_CLASS account)
				3. None to take all the undetected samples in support (UR + UI + UP)
		:return: the set of samples w.r.t. the supporting class as established
		"""
		results = self.classify(samples)
		if supp_class is None:
			return results[UP_CLASS] | results[UI_CLASS] | results[UR_CLASS]
		elif supp_class:
			return results[UP_CLASS]
		else:
			return results[UP_CLASS] | results[UI_CLASS]


class RIPPattern:
	"""
	Each pattern is described as a set of symbolic conditions annotated in execution and sequence of mutant
	"""

	def __init__(self, document: jctest.CDocument, classifier: RIPClassifier):
		self.document = document
		self.classifier = classifier
		self.words = list()
		self.sequences = set()
		self.mutants = set()
		return

	def get_document(self):
		"""
		:return: it provides original dataset for interpretation
		"""
		return self.document

	def get_sequences(self):
		"""
		:return: the set of SymSequence matched by this pattern
		"""
		return self.sequences

	def get_mutants(self):
		"""
		:return: the set of Mutant of which sequences is matched by this pattern
		"""
		return self.mutants

	def get_samples(self, seq_or_mut: bool):
		"""
		:param seq_or_mut: True to take SymSequence or Mutant as samples
		:return:
		"""
		if seq_or_mut:
			return self.sequences
		else:
			return self.mutants

	def __matching__(self, sequence: jctest.SymSequence):
		"""
		:param sequence:
		:return: True if the conditions in the pattern are included by the sequence
		"""
		for word in self.words:
			if not(word in sequence.get_words()):
				return False
		return True

	def set_samples(self, parent):
		"""
		:param parent: parent pattern from which this one is directly extended or None to set this one on document
		:return:
		"""
		if parent is None:
			sequences = self.document.get_sequences()
		else:
			parent: RIPPattern
			sequences = parent.get_sequences()
		self.sequences.clear()
		self.mutants.clear()
		for sequence in sequences:
			sequence: jctest.SymSequence
			if self.__matching__(sequence):
				self.sequences.add(sequence)
				self.mutants.add(sequence.get_mutant())
		return

	def get_classifier(self):
		return self.classifier

	def classify(self, seq_or_mut: bool):
		return self.classifier.classify(self.get_samples(seq_or_mut))

	def counting(self, seq_or_mut: bool):
		return self.classifier.counting(self.get_samples(seq_or_mut))

	def estimate(self, seq_or_mut: bool, supp_class):
		return self.classifier.estimate(self.get_samples(seq_or_mut), supp_class)

	def select(self, seq_or_mut: bool, supp_class):
		return self.classifier.select(self.get_samples(seq_or_mut), supp_class)

	def get_words(self):
		return self.words

	def get_conditions(self):
		return self.document.get_conditions_lib().get_conditions(self.words)

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def extends(self, word: str):
		"""
		:param word:
		:return: the child pattern directly extended from this one by adding one new word
		"""
		word = word.strip()
		if len(word) > 0 and not(word in self.words):
			child = RIPPattern(self.document, self.classifier)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def subsume(self, pattern, strip: bool):
		"""
		:param pattern:
		:param strip: true to compute strict subsumption or non-strict one
		:return:
		"""
		pattern: RIPPattern
		for sequence in pattern.get_sequences():
			if not(sequence in self.sequences):
				return False
		if strip:
			return len(self.sequences) > len(pattern.sequences)
		else:
			return True


class RIPMineInputs:
	"""
	The inputs dataset for data mining.
	"""

	def __init__(self, document: jctest.CDocument, used_tests, seq_or_mut: bool, supp_class,
				 max_length: int, min_support: int, min_confidence: float, max_confidence: float):
		"""
		:param document: the original dataset
		:param used_tests: the set of tests used in classifier
		:param seq_or_mut: true to take SymSequence or Mutant as samples
		:param supp_class:
				1. True to take strong coincidental correctness (only UP_CLASS are under consideration)
				2. False to take weak coincidental correctness (union of UP_CLASS and UI_CLASS account)
				3. None to take all the undetected samples in support (UR + UI + UP)
		:param max_length: the maximal length of patterns allowed to be selected
		:param min_support: the minimal support of samples being matched by good patterns
		:param min_confidence: the minimal confidence required to be achieved by patterns
		:param max_confidence: the maximal confidence to terminate recursive search space
		"""
		self.document = document
		self.classifier = RIPClassifier(used_tests)
		self.seq_or_mut = seq_or_mut
		self.supp_class = supp_class
		self.max_length = max_length
		self.min_support = min_support
		self.min_confidence = min_confidence
		self.max_confidence = max_confidence
		return

	def get_document(self):
		return self.document

	def get_classifier(self):
		return self.classifier

	def is_seq_or_mut(self):
		return self.seq_or_mut

	def get_supp_class(self):
		return self.supp_class

	def get_min_support(self):
		return self.min_support

	def get_min_confidence(self):
		return self.min_confidence

	def get_max_confidence(self):
		return self.max_confidence

	def get_max_length(self):
		return self.max_length


class RIPMineMiddle:
	"""
	It preserves the intermediate generation of objects such as patterns and estimation data.
	"""

	def __init__(self, inputs: RIPMineInputs):
		self.inputs = inputs
		self.patterns = dict()		# String --> RIPPattern (Unique)
		self.estimate = dict()		# RIPPattern --> [length, support, confidence]
		return

	def get_document(self):
		return self.inputs.get_document()

	def get_classifier(self):
		return self.inputs.get_classifier()

	def __new_pattern__(self, parent, word: str):
		"""
		:param parent: None to create root pattern
		:param word:
		:return: unique pattern as parent::word
		"""
		if parent is None:
			child = RIPPattern(self.inputs.get_document(), self.inputs.get_classifier())
			child = child.extends(word)
		else:
			parent: RIPPattern
			child = parent.extends(word)
		if not(str(child) in self.patterns):
			self.patterns[str(child)] = child
			child.set_samples(parent)
			self.get_estimate(child)
		child = self.patterns[str(child)]
		child: RIPPattern
		return child

	def get_root(self, word: str):
		return self.__new_pattern__(None, word)

	def get_child(self, parent: RIPPattern, word: str):
		return self.__new_pattern__(parent, word)

	def get_estimate(self, pattern: RIPPattern):
		if not(pattern in self.estimate):
			length = len(pattern)
			total, support, confidence = pattern.estimate(self.inputs.is_seq_or_mut(), self.inputs.get_supp_class())
			self.estimate[pattern] = (length, support, confidence)
		solution = self.estimate[pattern]
		length = solution[0]
		support = solution[1]
		confidence = solution[2]
		length: int
		support: int
		confidence: float
		return length, support, confidence

	def get_good_patterns(self):
		"""
		:return: the collection of good patterns w.r.t. given metrics
		"""
		good_patterns = set()
		for pattern, estimate in self.estimate.items():
			pattern: RIPPattern
			length = len(pattern)
			support = estimate[1]
			confidence = estimate[2]
			if length <= self.inputs.get_max_length() and support >= self.inputs.get_min_support() and \
					confidence >= self.inputs.get_min_confidence():
				good_patterns.add(pattern)
		return good_patterns


class RIPMineOutput:
	"""
	The output segment of RIPPattern(s)
	"""

	def __init__(self, inputs: RIPMineInputs, good_patterns):
		"""
		:param inputs:
		:param good_patterns: set of good patterns generated from
		"""
		self.inputs = inputs
		self.document = inputs.get_document()
		self.classifier = inputs.get_classifier()
		self.doc_seqs = set()
		self.doc_muta = set()
		for sequence in self.document.get_sequences():
			sequence: jctest.SymSequence
			self.doc_seqs.add(sequence)
			self.doc_muta.add(sequence.get_mutant())
		self.patterns = set()
		self.pat_seqs = set()
		self.pat_muta = set()
		for pattern in good_patterns:
			pattern: RIPPattern
			self.patterns.add(pattern)
			for sequence in pattern.get_sequences():
				sequence: jctest.SymSequence
				self.pat_seqs.add(sequence)
				self.pat_muta.add(sequence.get_mutant())
		return

	def get_document(self):
		return self.document

	def get_doc_sequences(self):
		return self.doc_seqs

	def get_doc_mutants(self):
		return self.doc_muta

	def get_classifier(self):
		return self.classifier

	def get_patterns(self):
		return self.patterns

	def get_pat_sequences(self):
		return self.pat_seqs

	def get_pat_mutants(self):
		return self.pat_muta

	@staticmethod
	def select_subsuming_patterns(patterns, strict: bool):
		"""
		:param strict: whether to generate strictly subsuming set
		:param patterns: set of RIP-testability patterns
		:return: minimal set of patterns that subsume the others
		"""
		remain_patterns, remove_patterns, minimal_patterns = set(), set(), set()
		patterns = jcbase.rand_resort(patterns)
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			subsume_pattern = None
			remove_patterns.clear()
			for pattern in remain_patterns:
				if subsume_pattern is None:
					subsume_pattern = pattern
					remove_patterns.add(pattern)
				elif subsume_pattern.subsume(pattern, strict):
					remove_patterns.add(pattern)
				elif pattern.subsume(subsume_pattern, strict):
					subsume_pattern = pattern
					remove_patterns.add(pattern)
			for pattern in remove_patterns:
				remain_patterns.remove(pattern)
			if not (subsume_pattern is None):
				minimal_pattern = subsume_pattern
				minimal_pattern: RIPPattern
				minimal_patterns.add(minimal_pattern)
		return minimal_patterns

	@staticmethod
	def remap_keys_patterns(patterns, seq_or_mut: bool):
		"""
		:param patterns:
		:param seq_or_mut: True to use SymSequence or Mutant as key
		:return: Sample ==> set of RIPPattern
		"""
		results = dict()
		for pattern in patterns:
			pattern: RIPPattern
			samples = pattern.get_samples(seq_or_mut)
			for sample in samples:
				if not (sample in results):
					results[sample] = set()
				results[sample].add(pattern)
		return results

	@staticmethod
	def select_best_pattern(patterns, seq_or_mut: bool, supp_class):
		"""
		:param patterns:
		:param seq_or_mut:
		:param supp_class:
		:return:
		"""
		remain_patterns, solutions = set(), dict()
		for pattern in patterns:
			pattern: RIPPattern
			remain_patterns.add(pattern)
			total, support, confidence = pattern.estimate(seq_or_mut, supp_class)
			length = len(pattern)
			solutions[pattern] = (length, support, confidence)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_confidence, worst_pattern = 1.0, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				confidence = solution[2]
				if worst_pattern is None or confidence <= worst_confidence:
					worst_confidence = confidence
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_support, worst_pattern = 99999, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				support = solution[1]
				if worst_pattern is None or support <= worst_support:
					worst_support = support
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		remain_length = max(1, int(len(remain_patterns) / 2))
		while len(remain_patterns) > remain_length:
			worst_length, worst_pattern = 0, None
			for pattern in remain_patterns:
				solution = solutions[pattern]
				length = solution[0]
				if worst_pattern is None or length >= worst_length:
					worst_length = length
					worst_pattern = pattern
			remain_patterns.remove(worst_pattern)

		for pattern in remain_patterns:
			return pattern
		return None

	@staticmethod
	def select_minimal_patterns(patterns, seq_or_mut: bool):
		"""
		:param patterns:
		:param seq_or_mut: True to cover RIPExecution or Mutant
		:return: minimal set of patterns covering all the executions in the set
		"""
		keys_patterns = RIPMineOutput.remap_keys_patterns(patterns, seq_or_mut)
		minimal_patterns, removed_keys = set(), set()
		while len(keys_patterns) > 0:
			removed_keys.clear()
			for sample, patterns in keys_patterns.items():
				selected_pattern = jcbase.rand_select(patterns)
				if not (selected_pattern is None):
					pattern = selected_pattern
					pattern: RIPPattern
					for pat_sample in pattern.get_samples(seq_or_mut):
						removed_keys.add(pat_sample)
					minimal_patterns.add(pattern)
					break
			for sample in removed_keys:
				if sample in keys_patterns:
					keys_patterns.pop(sample)
		return minimal_patterns

	def get_subsuming_patterns(self, strict: bool):
		"""
		:return:
		"""
		return RIPMineOutput.select_subsuming_patterns(self.patterns, strict)

	def get_minimal_patterns(self, seq_or_mut: bool):
		"""
		:param seq_or_mut:
		:return: the minimal set of RIP patterns covering all the samples in the space
		"""
		return RIPMineOutput.select_minimal_patterns(self.patterns, seq_or_mut)

	def get_best_patterns(self, seq_or_mut: bool, supp_class):
		"""
		:param seq_or_mut: used to estimate
		:param supp_class: used to estimate
		:return: mapping from mutant to the pattern that best matches with the mutant
		"""
		mutants_patterns = RIPMineOutput.remap_keys_patterns(self.patterns, False)
		best_patterns = dict()
		for mutant, patterns in mutants_patterns.items():
			mutant: jcmuta.Mutant
			best_pattern = RIPMineOutput.select_best_pattern(patterns, seq_or_mut, supp_class)
			if not (best_pattern is None):
				best_pattern: RIPPattern
				best_patterns[mutant] = best_pattern
		return best_patterns


class RIPMineWriter:
	"""
	It implements the writing of RIP conditions patterns.
	"""

	def __init__(self):
		self.writer = None
		return

	def output(self, text: str):
		self.writer: TextIO
		self.writer.write(text)
		return

	def __write__(self, text: str):
		self.output(text)

	@staticmethod
	def __percentage__(ratio: float):
		return int(ratio * 1000000) / 10000.0

	@staticmethod
	def __proportion__(x: int, y: int):
		if x == 0:
			ratio = 0.0
		else:
			ratio = x / (y + 0.0)
		return RIPMineWriter.__percentage__(ratio)

	@staticmethod
	def __f1_measure__(doc_samples: set, pat_samples: set):
		"""
		:param doc_samples: samples from document
		:param pat_samples: samples matched with pattern
		:return: precision, recall, f1_score
		"""
		int_samples = doc_samples & pat_samples
		common = len(int_samples)
		if common > 0:
			precision = common / len(pat_samples)
			recall = common / len(doc_samples)
			f1_score = 2 * precision * recall / (precision + recall)
		else:
			precision = 0.0
			recall = 0.0
			f1_score = 0.0
		return precision, recall, f1_score

	# pattern writing

	def __write_pattern_summary__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return:
				Summary 	Length Executions Mutations
				Counting	title UR UI UP KI UK CC
				Estimate	title total support confidence
		"""
		self.__write__("\tBEG_SUMMARY\n")
		# Attribute Length Sequences Mutations
		self.__write__("\t\t{}\t{}\n".format("ATTRIBUTE", "VALUE"))
		self.__write__("\t\t{}\t{}\n".format("length", len(pattern)))
		self.__write__("\t\t{}\t{}\n".format("seq_num", len(pattern.get_sequences())))
		self.__write__("\t\t{}\t{}\n".format("mut_num", len(pattern.get_mutants())))
		# Counting title UR UI UP KI UK CC
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\t{}\n".format("COUNTING", "UR", "UI", "UP", "KI", "UK", "CC"))
		ur, ui, up, ki, uk, cc = pattern.counting(True)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\t{}\n".format("seq_cot", ur, ui, up, ki, uk, cc))
		ur, ui, up, ki, uk, cc = pattern.counting(False)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\t{}\n".format("mut_cot", ur, ui, up, ki, uk, cc))
		# Estimate	title total support negative confidence
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\n".format("ESTIMATE", "TOTAL", "SUPPORT", "NEGATIVE", "CONFIDENCE(%)"))
		total, support, confidence = pattern.estimate(True, None)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\n".format("uk_seq", total, support, total - support,
														 RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(True, False)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\n".format("wc_seq", total, support, total - support,
														 RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(True, True)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\n".format("sc_seq", total, support, total - support,
														 RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, None)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\n".format("uk_mut", total, support, total - support,
														 RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, False)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\n".format("wc_mut", total, support, total - support,
														 RIPMineWriter.__percentage__(confidence)))
		total, support, confidence = pattern.estimate(False, True)
		self.__write__("\t\t{}\t{}\t{}\t{}\t{}\n".format("sc_mut", total, support, total - support,
														 RIPMineWriter.__percentage__(confidence)))
		self.__write__("\tEND_SUMMARY\n")
		return

	def __write_pattern_feature__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: condition category operator validate execution statement location parameter
		"""
		self.__write__("\t#BEG_FEATURES\n")
		template = "\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.__write__(template.format("Condition", "Category", "Operator", "Validate",
									   "Execution", "Statement", "Location", "Parameter"))
		index = 0
		for condition in pattern.get_conditions():
			index += 1
			category = condition.get_category()
			operator = condition.get_operator()
			execution = condition.get_execution()
			statement = execution.get_statement()
			location = condition.get_location()
			if condition.has_parameter():
				parameter = condition.get_parameter().get_code()
			else:
				parameter = "None"
			self.__write__(template.format(index, category, operator, True, execution,
										   "\"" + statement.get_cir_code() + "\"",
										   "\"" + location.get_cir_code() + "\"",
										   "{" + parameter + "}"))
		self.__write__("\t#END_FEATURES\n")
		return

	def __write_pattern_mutants__(self, pattern: RIPPattern):
		"""
		:param pattern:
		:return: Mutant Result Class Operator Line Location Parameter
		"""
		self.__write__("\t#BEG_MUTATIONS\n")
		template = "\t\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.__write__(template.format("ID", "Result", "Class", "Operator", "Line", "Location", "Parameter"))
		for mutant in pattern.get_mutants():
			mutant: jcmuta.Mutant
			mutant_id = mutant.get_muta_id()
			result = pattern.get_classifier().__classify__(mutant)
			mutation_class = mutant.get_mutation().get_mutation_class()
			operator = mutant.mutation.get_mutation_operator()
			location = mutant.mutation.get_location()
			parameter = mutant.mutation.get_parameter()
			line = location.line_of(False)
			code = location.get_code(True)
			self.__write__(template.format(mutant_id, result, mutation_class, operator, line, code, parameter))
		self.__write__("\t#END_MUTATIONS\n")
		return

	def __write_pattern__(self, pattern: RIPPattern):
		self.output("#BEG\n")
		self.__write_pattern_summary__(pattern)
		self.__write_pattern_feature__(pattern)
		self.__write_pattern_mutants__(pattern)
		self.output("#END\n")

	def write_patterns(self, patterns, file_path: str):
		with open(file_path, 'w') as writer:
			self.writer = writer
			for pattern in patterns:
				self.__write_pattern__(pattern)
				self.writer.write("\n")
		return

	def write_matching(self, output: RIPMineOutput, file_path: str, seq_or_mut: bool, supp_class):
		"""
		:param output:
		:param uk_or_cc:
		:param exe_or_mut:
		:param file_path:
		:return: 	Mutant 	ID RESULT CLASS OPERATOR LINE LOCATION PARMETER
					Pattern
					Category Operator Validate Execution Statement Location Parameter*
		"""
		mutants_patterns = output.get_best_patterns(seq_or_mut, supp_class)
		with open(file_path, 'w') as writer:
			self.writer = writer
			for mutant, pattern in mutants_patterns.items():
				mutant_id = mutant.get_muta_id()
				result = pattern.get_classifier().__classify__(mutant)
				mutation_class = mutant.get_mutation().get_mutation_class()
				operator = mutant.get_mutation().get_mutation_operator()
				location = mutant.get_mutation().get_location()
				parameter = mutant.get_mutation().get_parameter()
				line = location.line_of(False)
				code = location.get_code(True)
				self.output("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format("Mutant", mutant_id, result,
																	  mutation_class, operator, line, code, parameter))
				self.__write_pattern_feature__(pattern)
				self.output("\n")
		return

	@staticmethod
	def __evaluate__(document: jctest.CDocument, patterns, seq_or_mut: bool, supp_class,
					 classifier: RIPClassifier):
		"""
		:param document:
		:param patterns:
		:return: length doc_samples pat_samples reduce precision recall f1_score
		"""
		length = len(patterns)
		if seq_or_mut:
			doc_samples = classifier.select(document.get_sequences(), supp_class)
		else:
			doc_samples = classifier.select(document.get_mutants(), supp_class)
		pat_samples = set()
		for pattern in patterns:
			pattern: RIPPattern
			samples = pattern.get_samples(seq_or_mut)
			for sample in samples:
				pat_samples.add(sample)
		reduce = length / (len(doc_samples) + 0.0)
		precision, recall, f1_score = RIPMineWriter.__f1_measure__(doc_samples, pat_samples)
		return length, len(doc_samples), len(pat_samples), reduce, precision, recall, f1_score

	def __write_evaluate_all__(self, output: RIPMineOutput):
		document = output.get_document()
		patterns = output.get_subsuming_patterns(False)
		classifier = output.get_classifier()

		self.output("# Cost-Effective Analysis #\n")
		template = "\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n"
		self.output(template.format("title", "LEN", "DOC", "PAT", "REDUCE(%)", "PRECISION(%)", "RECALL(%)", "F1_SCORE"))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPMineWriter.__evaluate__(document, patterns, True, None, classifier)
		self.output(template.format("UK_EXE", length, doc_number, pat_number,
									RIPMineWriter.__percentage__(reduce_rate),
									RIPMineWriter.__percentage__(precision),
									RIPMineWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPMineWriter.__evaluate__(document, patterns, True, False, classifier)
		self.output(template.format("CC_EXE", length, doc_number, pat_number,
									RIPMineWriter.__percentage__(reduce_rate),
									RIPMineWriter.__percentage__(precision),
									RIPMineWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPMineWriter.__evaluate__(document, patterns, False, None, classifier)
		self.output(template.format("UK_MUT", length, doc_number, pat_number,
									RIPMineWriter.__percentage__(reduce_rate),
									RIPMineWriter.__percentage__(precision),
									RIPMineWriter.__percentage__(recall),
									f1_score))

		length, doc_number, pat_number, reduce_rate, precision, recall, f1_score = \
			RIPMineWriter.__evaluate__(document, patterns, False, False, classifier)
		self.output(template.format("CC_MUT", length, doc_number, pat_number,
									RIPMineWriter.__percentage__(reduce_rate),
									RIPMineWriter.__percentage__(precision),
									RIPMineWriter.__percentage__(recall),
									f1_score))

		self.output("\n")
		return

	def __write_evaluate_one__(self, index: int, pattern: RIPPattern):
		"""
		:param pattern:
		:return: index length executions mutants uk_exe_supp uk_exe_conf cc_exe_supp cc_exe_conf uk_mut_supp
				uk_mut_conf cc_mut_supp cc_mut_conf
		"""
		executions = len(pattern.get_sequences())
		mutants = len(pattern.get_mutants())
		_, uk_exe_supp, uk_exe_conf = pattern.estimate(True, None)
		_, cc_exe_supp, cc_exe_conf = pattern.estimate(True, False)
		_, uk_mut_supp, uk_mut_conf = pattern.estimate(False, None)
		_, cc_mut_supp, cc_mut_conf = pattern.estimate(False, False)
		self.output("\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\n".format(index, executions, mutants,
																			uk_exe_supp,
																			RIPMineWriter.__percentage__(uk_exe_conf),
																			cc_exe_supp,
																			RIPMineWriter.__percentage__(cc_exe_conf),
																			uk_mut_supp,
																			RIPMineWriter.__percentage__(uk_mut_conf),
																			cc_mut_supp,
																			RIPMineWriter.__percentage__(cc_mut_conf)
																			)
					)
		return

	def write_evaluate(self, space: RIPMineOutput, file_path: str):
		"""
		:param space:
		:param file_path:
		:return:
			# Cost-Effective Analysis
			title	LEN DOC PAT REDUCE(%) PRECISION(%) RECALL(%) F1_SCORE
			UK_EXE
			CC_EXE
			UK_MUT
			CC_MUT
		"""
		with open(file_path, 'w') as writer:
			self.writer = writer
			self.__write_evaluate_all__(space)
			self.output("# Pattern Evaluate #\n")
			self.output("\tindex\tlength\texecutions\tmutants\tuk_exe_supp\tuk_exe_conf(%)\tcc_exe_supp\tcc_exe_conf(%)"
						"\tuk_mut_supp\tuk_mut_conf(%)\tcc_mut_supp\tcc_mut_conf(%)\n")
			index = 0
			for pattern in space.get_subsuming_patterns(False):
				index += 1
				self.__write_evaluate_one__(index, pattern)
		return

