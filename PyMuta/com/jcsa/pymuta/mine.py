"""
This file implements the frequent pattern mining algorithm.
"""

import os
from typing import TextIO

import com.jcsa.pymuta.base as cbase
import com.jcsa.pymuta.code as ccode
import com.jcsa.pymuta.muta as cmuta


class MutationPattern:
	def __init__(self):
		self.words = list()
		self.lines = list()
		return

	def get_words(self):
		return self.words

	def get_lines(self):
		return self.lines

	def get_mutations(self):
		"""
		:return: compute the collection of mutations belonging to the lines in the pattern
		"""
		mutations = set()
		for line in self.lines:
			line: cmuta.MutationLine
			mutations.add(line.get_mutation())
		return mutations

	def get_annotations(self, project: cmuta.CProject):
		annotations = list()
		for word in self.words:
			annotation = cmuta.CAnnotation.parse(project, word)
			annotations.append(annotation)
		return annotations

	def match(self, line: cmuta.MutationLine):
		"""
		:param line:
		:return: whether the line matches with this pattern
		"""
		for word in self.words:
			if not(word in line.words):
				return False
		return True

	def set_lines(self, lines):
		"""
		:param lines: collection of mutation lines
		:return: update the lines in this pattern as the matched ones in given
		"""
		self.lines.clear()
		for line in lines:
			line: cmuta.MutationLine
			if self.match(line):
				self.lines.append(line)
		return

	def get_child(self, word: str):
		"""
		:param word:
		:return: the child pattern by adding one word or itself if the word is duplicated
		"""
		word = word.strip()
		if len(word) > 0 and (not(word in self.words)):
			child = MutationPattern()
			child.words.append(word)
			for old_word in self.words:
				child.words.append(old_word)
			child.words.sort()
			return child
		return self

	def measure(self, line_metric=True):
		"""
		measure the effectiveness of mutation lines matched within this patter
		:param line_metric: whether to use line as subject for counting (False will use mutations as subjects to count)
		:return: uc_number, ui_number, up_number, ki_number
		"""
		uc_number, ui_number, up_number, ki_number = 0, 0, 0, 0
		if line_metric:
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
			uc_mutations, ui_mutations, up_mutations, ki_mutations = set(), set(), set(), set()
			for line in self.lines:
				line: cmuta.MutationLine
				mutation = line.get_mutation()
				if line.get_killing_result():
					ki_mutations.add(mutation)
				elif line.get_infection_result():
					up_mutations.add(mutation)
				elif line.get_coverage_result():
					ui_mutations.add(mutation)
				else:
					uc_mutations.add(mutation)
			uc_number = len(uc_mutations)
			ui_number = len(ui_mutations)
			up_number = len(up_mutations)
			ki_number = len(ki_mutations)
		return uc_number, ui_number, up_number, ki_number

	def __str__(self):
		return str(self.words)


class MutationPatterns:
	"""
	It generates the mutation patterns using recursive based frequent pattern mining
	"""
	def __init__(self, line_metric: bool, uk_metric: bool, min_support: int, max_precision: float, max_length: int):
		"""
		:param line_metric: whether to use mutation line (or mutation) as subjects being counted
		:param uk_metric: whether to use uk_rate (or cc_rate) to determine as the precision
		:param min_support: the minimal support of either unkilled or coincidental correctness
		:param max_precision: the maximal precision expected to be reached by the pattern
		"""
		self.line_metric = line_metric
		self.uk_metric = uk_metric
		self.min_support = min_support
		self.max_precision = max_precision
		self.max_length = max_length
		self.patterns = dict()			# string --> pattern
		self.solutions = dict()			# pattern --> support, precision
		return

	def __root__(self, document: cmuta.MutationDocument, word: str):
		root_pattern = MutationPattern()
		root_pattern.words.append(word)
		key = str(root_pattern)
		if not(key in self.patterns):
			root_pattern.set_lines(document.get_lines())
			self.patterns[key] = root_pattern
		root_pattern = self.patterns[key]
		root_pattern: MutationPattern
		return root_pattern

	def __child__(self, parent: MutationPattern, word: str):
		child = parent.get_child(word)
		key = str(child)
		if not(key in self.patterns):
			self.patterns[key] = child
			child.set_lines(parent.lines)
		child = self.patterns[key]
		child: MutationPattern
		return child

	def __measure__(self, pattern: MutationPattern):
		"""
		:param pattern:
		:return: support, precision
		"""
		uc_number, ui_number, up_number, ki_number = pattern.measure(self.line_metric)
		total_number, support = uc_number + ui_number + up_number + ki_number, 0
		if self.uk_metric:
			support = uc_number + ui_number + up_number
		else:
			support = ui_number + up_number
		precision = 0.0
		if total_number > 0:
			precision = support / (total_number + 0.0)
		return support, precision

	def __solve__(self, parent: MutationPattern, words):
		"""
		:param parent: parent pattern being created
		:param words: feature words to be appended to create children
		:return:
		"""
		if not(parent in self.solutions):
			support, precision = self.__measure__(parent)
			self.solutions[parent] = [support, precision]
			print("\t--> Solve pattern with", str(self.solutions[parent]), "on", str(parent))
		support = self.solutions[parent][0]
		precision = self.solutions[parent][1]
		if (support >= self.min_support) and (precision <= self.max_precision) and (len(parent.words) < self.max_length):
			for word in words:
				child = self.__child__(parent, word)
				if child != parent:
					self.__solve__(child, words)
		return

	def __initial_lines__(self, document: cmuta.MutationDocument):
		"""
		:param document:
		:return: the collection of lines w.r.t. unkilled (uk_metric = True) or coincidental correctness (uk_metric = False)
		"""
		initial_lines = list()
		for line in document.get_lines():
			line: cmuta.MutationLine
			if line.get_killing_result():
				is_cc = False
				is_uk = False
			elif line.get_infection_result():
				is_uk = True
				is_cc = True
			elif line.get_coverage_result():
				is_uk = True
				is_cc = True
			else:
				is_uk = True
				is_cc = False
			if self.uk_metric and is_uk:
				initial_lines.append(line)
			elif (not self.uk_metric) and is_cc:
				initial_lines.append(line)
		return initial_lines

	def __filter__(self):
		"""
		:return: the set of patterns >= min_support and >= max_precision as good results
		"""
		good_patterns = set()
		for pattern in self.patterns.values():
			pattern: MutationPattern
			support, precision = self.__measure__(pattern)
			if support >= self.min_support and precision >= self.max_precision and len(pattern.words) <= self.max_length:
				good_patterns.add(pattern)
		return good_patterns

	def solve_good_patterns(self, document: cmuta.MutationDocument):
		"""
		:param document:
		:return: the collection of patterns solved from the document lines such that they achieve the good
				metrics and return the good samples as final outputs.
		"""
		self.patterns = dict()
		self.solutions = dict()
		initial_lines = self.__initial_lines__(document)
		for initial_line in initial_lines:
			words = initial_line.get_words()
			for word in words:
				root_parent = self.__root__(document, word)
				self.__solve__(root_parent, words)
		return self.__filter__()


def output_mutation_metrics(pattern: MutationPattern, writer: TextIO):
	writer.write("\tEvaluation\n")
	writer.write("\tMetric\tUC\tUI\tUP\tKI\tUKR\tCCR\n")
	# line-metric
	uc, ui, up, ki = pattern.measure(True)
	writer.write("\tLine\t" + str(uc) + "\t" + str(ui) + "\t" + str(up) + "\t" + str(ki))
	total, ukr, ccr = uc + ui + up + ki, 0.0, 0.0
	if total > 0:
		ukr = (uc + ui + up) / (total + 0.0)
		ccr = (uc + ui + up) / (total + 0.0)
	writer.write("\t" + str(ukr) + "\t" + str(ccr) + "\n")
	# mutation-metric
	uc, ui, up, ki = pattern.measure(False)
	writer.write("\tMutant\t" + str(uc) + "\t" + str(ui) + "\t" + str(up) + "\t" + str(ki))
	total, ukr, ccr = uc + ui + up + ki, 0.0, 0.0
	if total > 0:
		ukr = (uc + ui + up) / (total + 0.0)
		ccr = (uc + ui + up) / (total + 0.0)
	writer.write("\t" + str(ukr) + "\t" + str(ccr) + "\n")
	writer.write("\n")
	return


def output_mutation_samples(mutations, writer: TextIO):
	"""
	:param mutations:
	:param writer:
	:return: ID Result Class Operator Line Code Parameter
	"""
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


def output_mutation_annotations(annotations, writer: TextIO):
	"""
	:param annotations:
	:param writer:
	:return: type execution location line code parameter
	"""
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


def output_mutation_patterns(project: cmuta.CProject, patterns, output_file: str):
	"""
	:param project:
	:param patterns: the set of patterns being output
	:param output_file:
	:return:
	"""
	with open(output_file, 'w') as writer:
		pid = 0
		for pattern in patterns:
			pattern: MutationPattern
			writer.write("#BEG\t" + str(pid) + "\n")
			output_mutation_metrics(pattern, writer)
			annotations = pattern.get_annotations(project)
			output_mutation_annotations(annotations, writer)
			mutations = pattern.get_mutations()
			output_mutation_samples(mutations, writer)
			writer.write("#END\t" + str(pid) + "\n\n")
			pid += 1
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
		generator = MutationPatterns(line_metric, uk_metric, min_support, max_precision, max_length)
		patterns = generator.solve_good_patterns(docs)
		output_file_path = os.path.join(directory, file_name + ".pth")
		output_mutation_patterns(c_project, patterns, output_file_path)
		print("\tWrite patterns on", output_file_path)
	print("Testing finished for all...")


