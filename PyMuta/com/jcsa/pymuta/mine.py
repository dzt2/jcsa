"""
It implements a constraint-based frequent pattern mining for mining the patterns of failed or coincidental correct
mutation test results.
"""

import os
from typing import TextIO
import com.jcsa.pymuta.muta as cmuta


class MuExecutionPattern:
	"""
	The pattern of mutant execution is a set of annotation words as either constraints or state errors
	required for killing the target mutation in testing.
	"""
	def __init__(self):
		self.words = list()
		self.lines = list()
		return

	def get_words(self):
		"""
		:return: the collection of annotation words that define this pattern
		"""
		return self.words

	def get_annotations(self, project: cmuta.CProject):
		"""
		:param project:
		:return: collection of annotations generated from the words in pattern
		"""
		annotations = list()
		for word in self.words:
			annotation = cmuta.CAnnotation.parse(project, word)
			annotations.append(annotation)
		return annotations

	def get_lines(self):
		"""
		:return: the collection of mutant execution line (MutationLine) that match with this pattern
		"""
		return self.lines

	def get_mutations(self):
		"""
		:return: the set of mutations of which execution lines match with this pattern
		"""
		mutations = set()
		for line in self.lines:
			line: cmuta.MutationLine
			mutations.add(line.get_mutation())
		return mutations

	def __match__(self, line: cmuta.MutationLine):
		"""
		:param line:
		:return: whether the mutant execution line matches with this pattern
		"""
		for word in self.words:
			if not(word in line.words):
				return False
		return True

	def set_lines(self, lines):
		"""
		:param lines: the collection of mutant execution lines (MutationLine)
		:return: the set of mutant execution lines matching with this pattern
		"""
		self.lines.clear()
		for line in lines:
			line: cmuta.MutationLine
			if self.__match__(line):
				self.lines.append(line)
		return

	def get_child(self, word: str):
		word = word.strip()
		if (len(word) > 0) and (not(word in self.words)):
			child = MuExecutionPattern()
			for old_word in self.words:
				child.words.append(old_word)
			child.words.append(word)
			child.words.sort()
			return child
		return self

	def __str__(self):
		return str(self.words)

	def count(self, line_metrics: bool):
		"""
		:param line_metrics: whether to count based on mutation execution line (True) or mutation (False)
		:return: uc_number, ui_number, up_number, ki_number
		"""
		uc_number, ui_number, up_number, ki_number = 0, 0, 0, 0
		if line_metrics:
			for line in self.lines:
				line: cmuta.MutationLine
				if line.get_killing_result():
					ki_number += 1
				elif line.get_infection_result():
					up_number += 1
				elif line.get_coverage_result():
					ui_number += 1
				else:
					uc_number += 1
		else:
			mutations = self.get_mutations()
			for mutation in mutations:
				if mutation.get_result().is_killed():
					ki_number += 1
				elif mutation.get_result().is_infected():
					up_number += 1
				elif mutation.get_result().is_covered():
					ui_number += 1
				else:
					uc_number += 1
		return uc_number, ui_number, up_number, ki_number

	def measure(self, line_metrics: bool, ukil_metrics: bool):
		"""
		:param line_metrics: whether to count based on mutation execution line (True) or mutation (False)
		:param ukil_metrics: the supporting samples are unkilled execution (True) or coincidental correct execution (False)
		:return: total, support, precision
		"""
		uc_number, ui_number, up_number, ki_number = self.count(line_metrics)
		total, support, precision = uc_number + ui_number + up_number + ki_number, 0, 0.0
		if ukil_metrics:
			support = uc_number + ui_number + up_number
		else:
			support = ui_number + up_number
		if total > 0:
			precision = support / (total + 0.0)
		return total, support, precision

	def __subsume__(self, pattern):
		"""
		:param pattern:
		:return: whether lines in this pattern is the super-set of the lines in target
		"""
		pattern: MuExecutionPattern
		if len(self.lines) > len(pattern.get_lines()):
			for line in pattern.get_lines():
				if not(line in self.lines):
					return False
			return True
		return False


class MuExecutionPatterns:
	"""
	It implements algorithm to find frequent patterns among given range of specifics
	"""
	def __init__(self, line_metrics: bool, ukil_metrics: bool, min_support: int, max_precision: float, max_length: int):
		"""
		:param line_metrics: whether to count the mutation execution line (True) or mutation (False) as sample
		:param ukil_metrics: whether to measure the patterns using unkilled or coincidental correct classifier
		:param min_support: the minimal number of good samples (Unkilled or CC) for good patterns
		:param max_precision: the maximal precision to stop further searching
		:param max_length: the maximal length of the patterns allowed being generated in the algorithm machine
		"""
		self.line_metrics = line_metrics
		self.ukil_metrics = ukil_metrics
		self.min_support = min_support
		self.max_precision = max_precision
		self.max_length = max_length
		self.patterns = dict()		# string --> MuExecutionPattern
		self.solution = dict()		# MuExecutionPattern --> [total, support, precision]
		return

	def __root_pattern__(self, lines, word: str):
		"""
		:param lines: collection of all the execution lines in program
		:param word: the word used to generate root pattern in analysis
		:return: the root pattern with one single word and updated with the lines matching with it
		"""
		pattern = MuExecutionPattern()
		pattern.words.append(word.strip())
		if not(str(pattern) in self.patterns):
			self.patterns[str(pattern)] = pattern
			pattern.set_lines(lines)
		pattern = self.patterns[str(pattern)]
		pattern: MuExecutionPattern
		return pattern

	def __child_pattern__(self, parent: MuExecutionPattern, word):
		"""
		:param parent:
		:param word:
		:return: the child pattern generated from parent with adding one single word
		"""
		child = parent.get_child(word)
		if (child != parent) and (not(str(child) in self.patterns)):
			self.patterns[str(child)] = child
			child.set_lines(parent.get_lines())
		child = self.patterns[str(child)]
		child: MuExecutionPattern
		return child

	def __initial_lines__(self, document: cmuta.MutationDocument):
		"""
		:param document:
		:return: the collection of execution lines of specified classifier (either unkilled or CC)
		"""
		initial_lines = list()
		for line in document.get_lines():
			line: cmuta.MutationLine
			if line.get_killing_result():
				is_uk, is_cc = False, False
			elif line.get_infection_result():
				is_uk, is_cc = True, True
			elif line.get_coverage_result():
				is_uk, is_cc = True, True
			else:
				is_uk, is_cc = True, False
			if is_uk and self.ukil_metrics:
				initial_lines.append(line)
			elif is_cc and (not self.ukil_metrics):
				initial_lines.append(line)
		return initial_lines

	def __generate__(self, parent: MuExecutionPattern, words):
		"""
		:param parent: parent pattern from which the children will be created and traversed
		:param words: the collection of annotation words
		:return:
		"""
		if not(parent in self.solution):
			total, support, precision = parent.measure(self.line_metrics, self.ukil_metrics)
			self.solution[parent] = [total, support, precision]
			# print("\t\t~~>[", total, ",", support, ",", precision, "]:", str(parent.lines))
		support = self.solution[parent][1]
		precision = self.solution[parent][2]
		if (len(parent.words) < self.max_length) and (support >= self.min_support) and (precision <= self.max_precision):
			for word in words:
				child = self.__child_pattern__(parent, word)
				if child != parent:
					self.__generate__(child, words)
		return

	def __filter__(self):
		"""
		:return: the collection of good patterns filtered from the solution space
		"""
		good_patterns = list()
		for pattern, solution in self.solution.items():
			pattern: MuExecutionPattern
			total, support, precision = pattern.measure(self.line_metrics, self.ukil_metrics)
			if (support >= self.min_support) and (precision >= self.max_precision):
				good_patterns.append(pattern)
		return good_patterns

	def generate_good_patterns(self, document: cmuta.MutationDocument):
		"""
		:param document:
		:return: the set of good patterns generated from the lines in document
		"""
		self.patterns = dict()
		self.solution = dict()
		initial_lines = self.__initial_lines__(document)
		for initial_line in initial_lines:
			words = initial_line.get_words()
			for word in words:
				root_pattern = self.__root_pattern__(document.get_lines(), word)
				self.__generate__(root_pattern, words)
		good_patterns = self.__filter__()
		return good_patterns


class MuExecutionPatternWriter:
	"""
	It implements output the patterns and their lines + mutations information, including evaluation metrics.
	"""
	def __init__(self, document: cmuta.MutationDocument, patterns):
		self.document = document
		self.patterns = set()
		for pattern in patterns:
			pattern: MuExecutionPattern
			self.patterns.add(pattern)
		return

	def __classify_document__(self):
		uc_lines, cc_lines, ki_lines, uc_mutations, cc_mutations, ki_mutations = set(), set(), set(), set(), set(), set()
		for line in self.document.get_lines():
			line: cmuta.MutationLine
			mutation = line.get_mutation()
			if line.get_killing_result():
				ki_lines.add(line)
			elif line.get_infection_result() or line.get_coverage_result():
				cc_lines.add(line)
			else:
				uc_lines.add(line)
			if mutation.get_result().is_killed():
				ki_mutations.add(mutation)
			elif mutation.get_result().is_infected() or mutation.get_result().is_covered():
				cc_mutations.add(mutation)
			else:
				uc_mutations.add(mutation)
		return uc_lines, cc_lines, ki_lines, uc_mutations, cc_mutations, ki_mutations

	def __minimal_patterns__(self):
		"""
		:return: collection of the minimal necessary patterns for covering all the lines they match with
		"""
		remain_patterns, removed_patterns = set(), set()
		for pattern in self.patterns:
			remain_patterns.add(pattern)
		minimal_patterns = set()
		while len(remain_patterns) > 0:
			removed_patterns.clear()
			biggest_pattern = None
			for pattern in remain_patterns:
				if biggest_pattern is None:
					biggest_pattern = pattern
					removed_patterns.add(pattern)
				elif pattern.__subsume__(biggest_pattern):
					biggest_pattern = pattern
					removed_patterns.add(pattern)
				elif biggest_pattern.__subsume__(pattern):
					removed_patterns.add(pattern)
			for pattern in removed_patterns:
				remain_patterns.remove(pattern)
			if not (biggest_pattern is None):
				minimal_patterns.add(biggest_pattern)
		return minimal_patterns

	@staticmethod
	def __write_mutation_pattern_summary__(pattern: MuExecutionPattern, writer: TextIO):
		writer.write("\tEvaluation\n")
		writer.write("\tMetric\tUC\tUI\tUP\tKI\tUKR\tCCR\n")
		# line-metric
		uc, ui, up, ki = pattern.count(True)
		writer.write("\tLine\t" + str(uc) + "\t" + str(ui) + "\t" + str(up) + "\t" + str(ki))
		total, ukr, ccr = uc + ui + up + ki, 0.0, 0.0
		if total > 0:
			ukr = (uc + ui + up) / (total + 0.0)
			ccr = (uc + ui + up) / (total + 0.0)
		writer.write("\t" + str(ukr) + "\t" + str(ccr) + "\n")
		# mutation-metric
		uc, ui, up, ki = pattern.count(False)
		writer.write("\tMutant\t" + str(uc) + "\t" + str(ui) + "\t" + str(up) + "\t" + str(ki))
		total, ukr, ccr = uc + ui + up + ki, 0.0, 0.0
		if total > 0:
			ukr = (uc + ui + up) / (total + 0.0)
			ccr = (uc + ui + up) / (total + 0.0)
		writer.write("\t" + str(ukr) + "\t" + str(ccr) + "\n")
		writer.write("\n")
		return

	@staticmethod
	def __write_mutations_in_pattern__(pattern: MuExecutionPattern, writer: TextIO):
		"""
		:param pattern:
		:param writer:
		:return: ID Result Class Operator Line Code Parameter
		"""
		mutations = pattern.get_mutations()
		writer.write("\tMutations\t" + str(len(mutations)) + "\n")
		writer.write("\tID\tRes\tClass\tOperator\tLine\tLocation\tParameter\n")
		for mutation in mutations:
			mutation: cmuta.Mutation
			writer.write("\t" + str(mutation.get_muta_id()))
			if mutation.get_result().is_killed():
				result = "Killed"
			elif mutation.get_result().is_infected():
				result = "No_propagate"
			elif mutation.get_result().is_covered():
				result = "No_infection"
			else:
				result = "No_executed"
			writer.write("\t" + result + "\t" + mutation.get_muta_class() + "\t" + mutation.get_muta_operator())
			line = mutation.get_location().get_line() + 1
			code = mutation.get_location().get_code(True)
			writer.write("\t" + str(line))
			writer.write("\t\"" + code + "\"")
			writer.write("\t" + str(mutation.get_parameter()))
			writer.write("\n")
		writer.write("\n")
		return

	@staticmethod
	def __write_mutation_lines_in_pattern__(project: cmuta.CProject, pattern: MuExecutionPattern, writer: TextIO):
		"""
		:param pattern:
		:param writer:
		:return: type execution location line code parameter
		"""
		annotations = pattern.get_annotations(project)
		writer.write("\tAnnotations\t" + str(len(annotations)) + "\n")
		for annotation in annotations:
			annotation: cmuta.CAnnotation
			writer.write("\t" + annotation.get_annotation_type())
			writer.write("\t" + str(annotation.get_execution()))
			writer.write("\t" + str(annotation.get_location()))
			location = annotation.get_location()
			if location.has_ast_source():
				ast_location = location.get_ast_source()
				writer.write("\t" + str(ast_location.get_line() + 1))
				writer.write("\t\"" + location.get_code() + "\"")
			else:
				writer.write("\t-1\t" + location.get_code())
			writer.write("\t" + str(annotation.get_parameter()))
			writer.write("\n")
		writer.write("\n")
		return

	def write_mutation_patterns(self, output_file_path: str):
		with open(output_file_path, 'w') as writer:
			pid = 0
			for pattern in self.patterns:
				pattern: MuExecutionPattern
				writer.write("#BEG\t" + str(pid) + "\n")
				MuExecutionPatternWriter.__write_mutation_pattern_summary__(pattern, writer)
				MuExecutionPatternWriter.__write_mutation_lines_in_pattern__(self.document.project, pattern, writer)
				MuExecutionPatternWriter.__write_mutations_in_pattern__(pattern, writer)
				writer.write("#END\t" + str(pid) + "\n\n")
				pid += 1
			self.__write_mutation_pattern_evaluations__(writer)
		return

	@staticmethod
	def __write_evaluation_metrics__(title: str, og_samples, pt_samples, min_patterns, writer: TextIO):
		"""
		:param og_samples:
		:param pt_samples:
		:param min_patterns:
		:return: samples_size optimize_rate(%) precision(%) recall(%) f1_score
		"""
		writer.write(title)
		optimize_rate = 0.0
		if len(min_patterns) > 0 and len(og_samples) > 0:
			optimize_rate = len(min_patterns) / (len(og_samples) + 0.0)
		optimize_rate = int(optimize_rate * 10000) / 100.0
		writer.write("\t" + str(len(og_samples)) + "\t" + str(optimize_rate))
		intersections = set()
		for sample in pt_samples:
			if sample in og_samples:
				intersections.add(sample)
		precision, recall, f1_score = 0.0, 0.0, 0.0
		if len(intersections) > 0:
			precision = len(intersections) / (len(pt_samples) + 0.0)
			recall = len(intersections) / (len(og_samples) + 0.0)
			f1_score = 2 * precision * recall / (precision + recall)
		precision = int(10000 * precision) / 100.0
		recall = int(10000 * recall) / 100.0
		writer.write("\t" + str(precision) + "\t" + str(recall) + "\t" + str(f1_score))
		writer.write("\n")
		return

	def __write_mutation_pattern_evaluations__(self, writer: TextIO):
		"""
		:param writer:
		:return:
		"""
		writer.write("Summarize\n")
		# patterns + minimal patterns
		minimal_patterns = self.__minimal_patterns__()
		writer.write("Patterns\t" + str(len(self.patterns)) + "\tMinimal-Patterns\t" + str(len(minimal_patterns)) + "\n")
		# count and classify the lines and mutations in both document and patterns
		uc_lines, cc_lines, ki_lines, uc_mutations, cc_mutations, ki_mutations = self.__classify_document__()
		uk_lines, uk_mutations = set(), set()
		for line in cc_lines:
			uk_lines.add(line)
		for line in uc_lines:
			uk_lines.add(line)
		for mutation in uc_mutations:
			uk_mutations.add(mutation)
		for mutation in cc_mutations:
			uk_mutations.add(mutation)
		pt_lines, pt_mutations = set(), set()
		for pattern in self.patterns:
			for line in pattern.get_lines():
				line: cmuta.MutationLine
				pt_lines.add(line)
				pt_mutations.add(line.get_mutation())
		# uk-lines: lines [min_patterns/uk_lines]
		writer.write("Metrics\tNumber\tOptimizeRate(%)\tPrecision(%)\tRecalling(%)\tF1_Score\n")
		MuExecutionPatternWriter.__write_evaluation_metrics__("uk_lines", uk_lines, pt_lines, minimal_patterns, writer)
		MuExecutionPatternWriter.__write_evaluation_metrics__("cc_lines", cc_lines, pt_lines, minimal_patterns, writer)
		MuExecutionPatternWriter.__write_evaluation_metrics__("uk_mutants\t", uk_mutations, pt_mutations, minimal_patterns, writer)
		MuExecutionPatternWriter.__write_evaluation_metrics__("cc_mutants\t", cc_mutations, pt_mutations, minimal_patterns, writer)
		writer.write("\n")
		return


if __name__ == "__main__":
	root_path = "/home/dzt2/Development/Code/git/jcsa/JCMutest/result/features"
	line_metric, uk_metric, min_support, max_precision, max_length = True, True, 2, 0.75, 1
	for file_name in os.listdir(root_path):
		directory = os.path.join(root_path, file_name)
		c_project = cmuta.CProject(directory, file_name)
		print("Load", len(c_project.muta_space.mutations), "mutations and", len(c_project.test_space.test_cases), "tests for", file_name)
		sym_file_path = os.path.join(directory, file_name + ".sym")
		test_id = -1
		docs = c_project.load_document(sym_file_path, test_id)
		print("\t==> Get", len(docs.get_lines()), "lines of annotations with", len(docs.get_corpus()), "words.")
		generator = MuExecutionPatterns(line_metric, uk_metric, min_support, max_precision, max_length)
		patterns = generator.generate_good_patterns(docs)
		writer = MuExecutionPatternWriter(docs, patterns)
		output_file_path = os.path.join(directory, file_name + ".pth")
		print("\t==> Write", len(patterns), "patterns to", output_file_path)
		writer.write_mutation_patterns(output_file_path)
	print("Testing end for all...")

