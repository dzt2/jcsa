"""
This implements the frequent pattern mining based on constraint with experimental evaluation.
"""

import enum
import os
from typing import TextIO

import com.jcsa.pymuta.muta as cmuta


UC_CLASS = "UC"		# un-covered
UI_CLASS = "UI"		# un-infected
UP_CLASS = "UP"		# un-propagate
KI_CLASS = "KI"		# being killed


class MutationClassifier:
	"""
	It implements the classification of mutation and its execution (MutationLine) in document.
	"""
	@staticmethod
	def classify_one(sample):
		"""
		:param sample: Mutation or MutationLine
		:return: category in one of [uc, ui, up, ki]
		"""
		if isinstance(sample, cmuta.MutationLine):
			sample: cmuta.MutationLine
			if sample.get_killing_result():
				return KI_CLASS
			elif sample.get_infection_result():
				return UP_CLASS
			elif sample.get_coverage_result():
				return UI_CLASS
			else:
				return UC_CLASS
		else:
			sample: cmuta.Mutation
			if sample.get_result().is_killed():
				return KI_CLASS
			elif sample.get_result().is_infected():
				return UP_CLASS
			elif sample.get_result().is_covered():
				return UI_CLASS
			else:
				return UC_CLASS

	@staticmethod
	def classify_set(samples):
		"""
		:param samples: collection of either Mutation or MutationLine
		:return: [UC, UI, UP, KI] --> Collection[MutationLine|Mutation]
		"""
		results = dict()
		results[UC_CLASS] = set()
		results[UI_CLASS] = set()
		results[UP_CLASS] = set()
		results[KI_CLASS] = set()
		for sample in samples:
			category = MutationClassifier.classify_one(sample)
			results[category].add(sample)
		return results


class MutationLinePattern:
	"""
	The pattern of MutationLine is a set of words that the lines in document can match with.
	"""
	def __init__(self):
		self.words = list()
		self.lines = list()
		return

	def get_words(self):
		"""
		:return: the collection of sorted words of document in this pattern
		"""
		return self.words

	def __len__(self):
		return len(self.words)

	def __str__(self):
		return str(self.words)

	def get_annotations(self, project: cmuta.CProject):
		"""
		:return: the collection of annotations parsed from words in this pattern in context of the project
		"""
		annotations = list()
		for word in self.words:
			annotation = cmuta.CAnnotation.parse(project, word)
			annotation: cmuta.CAnnotation
			annotations.append(annotation)
		return annotations

	def get_lines(self):
		"""
		:return: the set of MutationLine(s) that match with this pattern
		"""
		return self.lines

	def __match__(self, line: cmuta.MutationLine):
		"""
		:param line: MutationLine to be matched with
		:return: true if the words in line match with this pattern
		"""
		for word in self.words:
			if not(word in line.words):
				return False
		return True

	def set_lines(self, lines):
		"""
		:param lines: collection of new MutationLine(s) to update this pattern
		:return:
		"""
		self.lines.clear()
		for line in lines:
			line: cmuta.MutationLine
			if self.__match__(line):
				self.lines.append(line)
		return

	def get_mutations(self):
		"""
		:return: the collection of mutations of lines matching within this pattern
		"""
		mutations = set()
		for line in self.lines:
			line: cmuta.MutationLine
			mutations.add(line.get_mutation())
		return mutations

	def classify(self, line_or_mutation: bool):
		"""
		:param line_or_mutation: true for classifying the lines in this pattern, or false to classify their mutations
		:return: dict[UC|UI|UP|KI; MutationLine(True)|Mutation(False)]
		"""
		if line_or_mutation:
			samples = self.lines
		else:
			samples = self.get_mutations()
		return MutationClassifier.classify_set(samples)

	def counting(self, line_or_mutation: bool):
		"""
		:param line_or_mutation: true for classifying the lines in this pattern, or false to classify their mutations
		:return: uc, ui, up, ki, ukr, ccr
		"""
		classify_table = self.classify(line_or_mutation)
		uc = len(classify_table[UC_CLASS])
		ui = len(classify_table[UI_CLASS])
		up = len(classify_table[UP_CLASS])
		ki = len(classify_table[KI_CLASS])
		total_number = uc + ui + up + ki
		ukr, ccr = 0.0, 0.0
		uks, ccs = uc + ui + up, ui + up
		if total_number > 0:
			ukr = uks / (total_number + 0.0)
			ccr = ccs / (total_number + 0.0)
		return uc, ui, up, ki, ukr, ccr

	def measure(self, line_or_mutation: bool, uk_or_cc: bool):
		"""
		:param line_or_mutation: true for classifying the lines in this pattern, or false to classify their mutations
		:param uk_or_cc: true for un-killed mutation (lines) while false for coincidental correctness mutation (lines)
		:return: total, support, precision
		"""
		uc, ui, up, ki, ukr, ccr = self.counting(line_or_mutation)
		if uk_or_cc:
			return uc + ui + up + ki, uc + ui + up, ukr
		return uc + ui + up + ki, ui + up, ccr

	def get_child(self, word: str):
		"""
		:param word: new annotation word be inserted to this pattern
		:return: the child pattern by inserting one word in patterns
		"""
		word = word.strip()
		if (len(word) > 0) and (not(word in self.words)):
			child = MutationLinePattern()
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			return child
		return self

	def subsume(self, pattern):
		"""
		:param pattern:
		:return: whether the lines in pattern are matched by this pattern
		"""
		pattern: MutationLinePattern
		if len(self.lines) >= len(pattern.lines):
			for line in pattern.lines:
				if not(line in self.lines):
					return False
			return True
		return False


class MutationLinePatterns:
	"""
	It maintains the collection of mutation line patterns generated from the document,
	which implements a frequent pattern mining algorithm.
	"""
	def __init__(self, line_or_mutation: bool, uk_or_cc: bool, min_support: int, max_precision: float, max_length: int):
		"""
		:param line_or_mutation: true for classifying the lines in this pattern, or false to classify their mutations
		:param uk_or_cc: true for measuring patterns based on un-killed category, false based on coincidental correct
		:param min_support: minimal number of mutations or lines with the pattern that should be un-killed or CC.
		:param max_precision: the maximal rate achieved by lines of the pattern that should be un-killed or CC.
		:param max_length: the maximal number of words allowed in a pattern
		"""
		self.line_or_mutation = line_or_mutation
		self.uk_or_cc = uk_or_cc
		self.min_support = min_support
		self.max_precision = max_precision
		self.max_length = max_length
		self.patterns = dict()			# string  --> pattern
		self.solution = dict()			# pattern --> [total, support, precision]
		return

	def __filter_lines__(self, document: cmuta.MutationDocument):
		"""
		:param document:
		:return: the collection of lines in document of which category is either UK (uk_or_cc = True) or CC
				(uk_or_cc = False)
		"""
		lines_table = MutationClassifier.classify_set(document.get_lines())
		good_lines = lines_table[UP_CLASS] | lines_table[UI_CLASS]
		if self.uk_or_cc:
			good_lines = good_lines | lines_table[UC_CLASS]
		return good_lines

	def __root__(self, document: cmuta.MutationDocument, word: str):
		"""
		:param document: all the lines of mutation executions
		:param word: the unique word be inserted to the pattern
		:return: root pattern with one word and matching with all the lines in document
		"""
		root_pattern = MutationLinePattern().get_child(word)
		if not(str(root_pattern) in self.patterns):
			self.patterns[str(root_pattern)] = root_pattern
			root_pattern.set_lines(document.get_lines())
		root_pattern = self.patterns[str(root_pattern)]
		root_pattern: MutationLinePattern
		return root_pattern

	def __child__(self, parent: MutationLinePattern, word: str):
		"""
		:param parent:
		:param word:
		:return: the unique child pattern of which lines are updated based on matching words or the parent itself
				if the word is useless
		"""
		child = parent.get_child(word)
		if child != parent:
			if not(str(child) in self.patterns):
				self.patterns[str(child)] = child
				child.set_lines(parent.get_lines())
			child = self.patterns[str(child)]
			child: MutationLinePattern
			return child
		return parent

	def __generate__(self, parent: MutationLinePattern, words):
		"""
		:param parent: the parent pattern to generate child patterns
		:param words:
		:return:
		"""
		if not(parent in self.solution):
			total, support, precision = parent.measure(self.line_or_mutation, self.uk_or_cc)
			self.solution[parent] = [total, support, precision]
		support, precision = self.solution[parent][1], self.solution[parent][2]
		if (len(parent.words) < self.max_length) and (support >= self.min_support) and (precision <= self.max_precision):
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__generate__(child, words)
		return

	def __filter_solutions__(self):
		"""
		:return: the collection of good patterns selected from self.solution
		"""
		good_patterns = set()
		for pattern, solution in self.solution.items():
			pattern: MutationLinePattern
			support, precision = solution[1], solution[2]
			if support >= self.min_support and precision >= self.max_precision:
				good_patterns.add(pattern)
		return good_patterns

	@staticmethod
	def __minimal_solutions__(patterns):
		"""
		:param patterns:
		:return: the minimal set of patterns required for covering all matched lines
		"""
		remain_patterns, removed_patterns, minimal_patterns = set(), set(), set()
		for pattern in patterns:
			pattern: MutationLinePattern
			remain_patterns.add(pattern)
		while len(remain_patterns) > 0:
			biggest_pattern = None
			removed_patterns.clear()
			for pattern in remain_patterns:
				if biggest_pattern is None:
					biggest_pattern = pattern
					removed_patterns.add(pattern)
				elif biggest_pattern.subsume(pattern):
					removed_patterns.add(pattern)
				elif pattern.subsume(biggest_pattern):
					biggest_pattern = pattern
					removed_patterns.add(pattern)
			remain_patterns = remain_patterns - removed_patterns
			minimal_patterns.add(biggest_pattern)
		return minimal_patterns

	@staticmethod
	def __line_to_patterns__(patterns):
		"""
		:return: dictionary from mutation lines to the patterns they match with
		"""
		line_patterns = dict()
		for pattern in patterns:
			pattern: MutationLinePattern
			for line in pattern.get_lines():
				line: cmuta.MutationLine
				if not(line in line_patterns):
					line_patterns[line] = set()
				line_patterns[line].add(pattern)
		return line_patterns

	@staticmethod
	def __mutation_to_patterns__(patterns):
		"""
		:param patterns:
		:return: dictionary from mutation to the patterns they match with
		"""
		mutation_patterns = dict()
		for pattern in patterns:
			pattern: MutationLinePattern
			for mutation in pattern.get_mutations():
				if not(mutation in mutation_patterns):
					mutation_patterns[mutation] = set()
				mutation_patterns[mutation].add(pattern)
		return mutation_patterns

	def __find_best_pattern__(self, patterns: set):
		"""
		:param patterns:
		:return: the best pattern in the given set for matching unkilled or coincidental correctness classifier
		"""
		best_patterns = set()
		while len(best_patterns) < 5 and len(patterns) > 0:
			best_pattern, best_precision = None, 0.0
			for pattern in patterns:
				pattern: MutationLinePattern
				total, support, precision = pattern.measure(self.line_or_mutation, self.uk_or_cc)
				if precision > best_precision:
					best_pattern = pattern
					best_precision = precision
			if best_pattern is None:
				break
			else:
				patterns.remove(best_pattern)
				best_patterns.add(best_pattern)
		most_best_pattern, most_best_support = None, 0
		for pattern in best_patterns:
			pattern: MutationLinePattern
			total, support, precision = pattern.measure(self.line_or_mutation, self.uk_or_cc)
			if support > most_best_support:
				most_best_pattern = pattern
				most_best_support = support
		return most_best_pattern

	def __find_best_patterns__(self, key_patterns: dict):
		"""
		:param key_patterns: mapping from lines or mutations to the patterns
		:return: mapping from line or mutation to the best matched pattern
		"""
		key_best_pattern_dict = dict()
		for key, patterns in key_patterns.items():
			best_pattern = self.__find_best_pattern__(patterns)
			key_best_pattern_dict[key] = best_pattern
		return key_best_pattern_dict

	def generate(self, document: cmuta.MutationDocument):
		"""
		:param document:
		:return: good_patterns, min_patterns
		"""
		self.patterns = dict()
		self.solution = dict()
		init_lines = self.__filter_lines__(document)
		for init_line in init_lines:
			init_line: cmuta.MutationLine
			words = init_line.get_words()
			for word in words:
				root_pattern = self.__root__(document, word)
				self.__generate__(root_pattern, words)
		good_patterns = self.__filter_solutions__()
		minimal_patterns = MutationLinePatterns.__minimal_solutions__(good_patterns)
		line_patterns = MutationLinePatterns.__line_to_patterns__(good_patterns)
		mutation_patterns = MutationLinePatterns.__mutation_to_patterns__(good_patterns)
		return good_patterns, minimal_patterns, self.__find_best_patterns__(line_patterns), self.__find_best_patterns__(mutation_patterns)


class MutationLinePatternWriter:
	"""
	It implements writing the mutation line patterns to the output file
	"""
	def __init__(self, document: cmuta.MutationDocument, patterns):
		"""
		:param document: provide all the mutation execution lines in program under test
		:param patterns: the generated patterns to be printed and evaluated
		"""
		self.document = document
		self.patterns = set()
		for pattern in patterns:
			pattern: MutationLinePattern
			self.patterns.add(pattern)
		return

	def __get_document_lines_and_mutations__(self):
		lines, mutations = set(), set()
		for line in self.document.get_lines():
			line: cmuta.MutationLine
			lines.add(line)
			mutations.add(line.get_mutation())
		return lines, mutations

	def __get_patterns_lines_and_mutations__(self):
		lines, mutations = set(), set()
		for pattern in self.patterns:
			for line in pattern.get_lines():
				line: cmuta.MutationLine
				lines.add(line)
				mutations.add(line.get_mutation())
		return lines, mutations

	@staticmethod
	def __percentage__(x: int, y: int):
		if y == 0:
			return 0.0
		rate = x / (y + 0.0)
		return int(rate * 10000) / 100.0

	@staticmethod
	def __precision_and_recall_f1__(pat_samples: set, doc_samples: set):
		"""
		:param pat_samples:
		:param doc_samples:
		:return: precision, recall, f1_score
		"""
		int_samples = pat_samples & doc_samples
		precision, recall, f1_score = 0.0, 0.0, 0.0
		if len(int_samples) > 0:
			precision = MutationLinePatternWriter.__percentage__(len(int_samples), len(pat_samples))
			recall = MutationLinePatternWriter.__percentage__(len(int_samples), len(doc_samples))
			f1_score = 2 * precision * recall / ((precision + recall) * 100.0)
			f1_score = int(f1_score * 10000) / 10000.0
		return precision, recall, f1_score

	def __write_pattern_evaluation__(self, writer: TextIO):
		"""
		:return:
		"""
		doc_lines, doc_mutations = self.__get_document_lines_and_mutations__()
		pat_lines, pat_mutations = self.__get_patterns_lines_and_mutations__()
		pat_number, lin_number, mut_number = len(self.patterns), len(pat_lines), len(pat_mutations)
		pat_lin_optimize_rate = MutationLinePatternWriter.__percentage__(pat_number, lin_number)
		pat_mut_optimize_rate = MutationLinePatternWriter.__percentage__(pat_number, mut_number)
		writer.write("Evaluation\n")
		writer.write("\tPatterns = {}\tLINE := {}({}%)\tMUTATION := {}({}%)\n".
					 format(pat_number, lin_number, pat_lin_optimize_rate, mut_number, pat_mut_optimize_rate))
		doc_line_table = MutationClassifier.classify_set(doc_lines)
		doc_mutation_table = MutationClassifier.classify_set(doc_mutations)
		doc_uk_lines = doc_line_table[UC_CLASS] | doc_line_table[UI_CLASS] | doc_line_table[UP_CLASS]
		doc_cc_lines = doc_line_table[UI_CLASS] | doc_line_table[UP_CLASS]
		doc_uk_mutations = doc_mutation_table[UC_CLASS] | doc_mutation_table[UI_CLASS] | doc_mutation_table[UP_CLASS]
		doc_cc_mutations = doc_mutation_table[UI_CLASS] | doc_mutation_table[UP_CLASS]
		writer.write("\tTitle\tPrecision(%)\tRecall(%)\tF1_Score\n")
		precision, recall, f1_score = MutationLinePatternWriter.__precision_and_recall_f1__(pat_lines, doc_uk_lines)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("UK-LINE", precision, recall, f1_score))
		precision, recall, f1_score = MutationLinePatternWriter.__precision_and_recall_f1__(pat_lines, doc_cc_lines)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("CC-LINE", precision, recall, f1_score))
		precision, recall, f1_score = MutationLinePatternWriter.__precision_and_recall_f1__(pat_mutations, doc_uk_mutations)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("UK-MUTA", precision, recall, f1_score))
		precision, recall, f1_score = MutationLinePatternWriter.__precision_and_recall_f1__(pat_mutations, doc_cc_mutations)
		writer.write("\t{}\t{}%\t{}%\t{}\n".format("CC-MUTA", precision, recall, f1_score))
		return

	@staticmethod
	def __write_pattern_words__(writer: TextIO, pattern: MutationLinePattern, project: cmuta.CProject):
		"""
		:param writer:
		:param pattern:
		:param project:
		:return: annotation[k] type execution statement code parameter
		"""
		annotations = pattern.get_annotations(project)
		aid = 0
		for annotation in annotations:
			stmt_code = annotation.get_execution().get_statement().get_code()
			loct_code = annotation.get_location().get_code()
			parameter = str(annotation.get_parameter())
			writer.write("\tannotation[{}]\t{}\t{}\t{}\t{}\t{}\n".format(aid, annotation.annotation_type, annotation.execution, stmt_code, loct_code, parameter))
			aid += 1
		return

	@staticmethod
	def __write_pattern_counting__(writer: TextIO, pattern: MutationLinePattern):
		"""
		:param writer: output stream
		:param pattern:
		:return: metrics UC UI UP KI UKR CCR
		"""
		writer.write("\tMetrics\tUC\tUI\tUP\tKI\tUKR(%)\tCCR(%)\n")
		uc, ui, up, ki, ukr, ccr = pattern.counting(True)
		ukr, ccr = int(ukr * 10000) / 100.0, int(ccr * 10000) / 100.0
		writer.write("\t{}\t{}\t{}\t{}\t{}\t{}%\t{}%\n".format("Line", uc, ui, up, ki, ukr, ccr))
		uc, ui, up, ki, ukr, ccr = pattern.counting(False)
		ukr, ccr = int(ukr * 10000) / 100.0, int(ccr * 10000) / 100.0
		writer.write("\t{}\t{}\t{}\t{}\t{}\t{}%\t{}%\n".format("Mutation", uc, ui, up, ki, ukr, ccr))
		return

	@staticmethod
	def __write_pattern_mutations__(writer: TextIO, pattern: MutationLinePattern):
		"""
		:param writer:
		:param pattern:
		:return: ID RES class operator line code parameter
		"""
		writer.write("\tID\tRES\tCLASS\tOPRT\tLINE\tCODE\tPARAM\n")
		template = "\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n"
		for mutation in pattern.get_mutations():
			mid = mutation.get_muta_id()
			res = MutationClassifier.classify_one(mutation)
			mclass = mutation.get_muta_class()
			moprt = mutation.get_muta_operator()
			location = mutation.get_location()
			line = location.get_line() + 1
			code = location.get_code(True)
			parameter = str(mutation.get_parameter())
			writer.write(template.format(mid, res, mclass, moprt, line, code, parameter))
		return

	def __write_patterns__(self, writer: TextIO):
		self.__write_pattern_evaluation__(writer)
		writer.write("\n")
		pid = 0
		for pattern in self.patterns:
			writer.write("BEG\t" + str(pid) + "\n")
			MutationLinePatternWriter.__write_pattern_words__(writer, pattern, self.document.get_project())
			writer.write("\n")
			MutationLinePatternWriter.__write_pattern_counting__(writer, pattern)
			writer.write("\n")
			MutationLinePatternWriter.__write_pattern_mutations__(writer, pattern)
			writer.write("END\n\n")
			pid = pid + 1
		return

	def write_patterns(self, output_file_path: str):
		with open(output_file_path, 'w') as writer:
			self.__write_patterns__(writer)
			writer.flush()
		return

	@staticmethod
	def __write_mutation_and_pattern__(writer: TextIO, mutation: cmuta.Mutation, pattern: MutationLinePattern, project: cmuta.CProject):
		"""
		:param writer:
		:param mutation:
		:param pattern:
		:param project
		:return:
		"""
		template = "Muta\t{}\t{}\t{}\t{}\t{}\t\"{}\"\t{}\n"
		mid = mutation.get_muta_id()
		res = MutationClassifier.classify_one(mutation)
		mclass = mutation.get_muta_class()
		moprt = mutation.get_muta_operator()
		location = mutation.get_location()
		line = location.get_line() + 1
		code = location.get_code(True)
		parameter = str(mutation.get_parameter())
		writer.write(template.format(mid, res, mclass, moprt, line, code, parameter))
		MutationLinePatternWriter.__write_pattern_words__(writer, pattern, project)
		return

	@staticmethod
	def write_best_patterns(project: cmuta.CProject, key_pattern_dict: dict, output_file_path: str):
		"""
		:param project:
		:param key_pattern_dict: lines or mutations to the pattern they matched with
		:param output_file_path:
		:return:
		"""
		with open(output_file_path, 'w') as writer:
			for key, pattern in key_pattern_dict.items():
				if isinstance(key, cmuta.MutationLine):
					key: cmuta.MutationLine
					mutation = key.get_mutation()
				else:
					key: cmuta.Mutation
					mutation = key
				MutationLinePatternWriter.__write_mutation_and_pattern__(writer, mutation, pattern, project)
				writer.write("\n")
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	post_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/patterns"
	line_or_mutation, uk_or_cc, min_support, max_precision, max_length = True, True, 2, 0.80, 1
	for file_name in os.listdir(root_path):
		print("Testing on program", file_name)
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		print("\tLoad", len(c_project.muta_space.mutations), "mutations and", len(c_project.test_space.test_cases), "tests for", file_name)
		sym_file_path = os.path.join(directory, file_name + ".sym")
		test_id = -1
		docs = c_project.load_document(sym_file_path, test_id)
		print("\tGet", len(docs.get_lines()), "lines of annotations with", len(docs.get_corpus()), "words.")
		good_patterns, min_patterns, line_patterns, mutation_patterns = MutationLinePatterns(line_or_mutation, uk_or_cc, min_support, max_precision, max_length).generate(docs)
		MutationLinePatternWriter(docs, good_patterns).write_patterns(os.path.join(post_path, file_name + ".gpt"))
		print("\tWrite", len(good_patterns), "selected set of patterns to output file.")
		MutationLinePatternWriter(docs, min_patterns).write_patterns(os.path.join(post_path, file_name + ".mpt"))
		print("\tWrite", len(min_patterns), "minimal set of patterns to output file.")
		MutationLinePatternWriter.write_best_patterns(c_project, mutation_patterns, os.path.join(post_path, file_name + ".bpt"))
		print("\tWrite", len(mutation_patterns), "mutations and their best patterns to output.")
		print()

