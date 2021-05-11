"""This file implements the data model of killable prediction rules to support mutation analysis"""


import os
import com.jcsa.libs.base as jcbase
import com.jcsa.libs.code as jccode
import com.jcsa.libs.muta as jcmuta
import com.jcsa.libs.test as jctest


# Encoding-Decoding: SymCondition --> str --> int

class SymWordCorpus:
	"""
	It manages the encoding and decoding for symbolic condition to unique string and its corresponding integer feature.
	"""

	def __init__(self, document: jctest.CDocument):
		"""
		:param document: it is used to decode the string word to symbolic condition
		"""
		self.document = document
		self.words = list()
		self.index = dict()
		return

	def get_document(self):
		"""
		:return: the document is used to decode string word to symbolic condition
		"""
		return self.document

	def get_words(self):
		"""
		:return: the collection of string words encoding symbolic conditions in the corpus
		"""
		return self.words

	def __str2int__(self, word: str):
		"""
		:param word: the string word to be encoded as integer
		:return: the unique integer ID of the word or -1 if the word is empty
		"""
		word = word.strip()
		if len(word) > 0:
			if not (word in self.index):
				self.index[word] = len(self.words)
				self.words.append(word)
			code = self.index[word]
			code: int
			return code
		else:
			return -1

	def __int2str__(self, code: int):
		"""
		:param code: the unique integer ID of the string word being decoded
		:return: the string word w.r.t. the integer ID or none if not exist
		"""
		if (code >= 0) and (code < len(self.words)):
			word = self.words[code]
			word: str
			return word
		else:
			return None



















