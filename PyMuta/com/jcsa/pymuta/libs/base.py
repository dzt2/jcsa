"""This file develops the transformation of tokens and objects in testing"""


import enum
import os
import random


class CTokenType(enum.Enum):
	"""
	It defines the type of token defined in feature database
	"""
	Nullptr = 0
	Boolean = 1
	Character = 2
	Integer = 3
	Float = 4
	Complex = 5
	Keyword = 6
	Punctuate = 7
	Operator = 8
	AstNode = 9
	CirNode = 10
	MutantID = 11
	Execution = 12
	Instance = 13
	String = 14
	DataType = 15
	SymNode = 16
	TestID = 17


class CToken:
	"""
	It defines the basic data token defined in feature database.
	"""

	def __init__(self, token_type: CTokenType, token_value):
		"""
		:param token_type:
		:param token_value:
				Nullptr			-->		None
				Boolean			-->		Bool
				Character		-->		int
				Integer			-->		int
				Float			--> 	float
				Complex			-->		complex
				Keyword			-->		string
				Punctuate		-->		string
				Operator		-->		string
				AstNode			-->		int
				CirNode			-->		int
				MutantID		-->		int
				Execution		-->		(string, int)
				InstanceNode	-->		(context, string, int)
				DependNode		-->		(context, string, int)
				SymNode			-->		(type, int)
				DataType		-->		string
		"""
		self.token_type = token_type
		self.token_value = token_value
		return

	def get_token_type(self):
		return self.token_type

	def get_token_value(self):
		"""
		:return:
				Nullptr			-->		None
				Boolean			-->		Bool
				Character		-->		int
				Integer			-->		int
				Float			--> 	float
				Complex			-->		complex
				Keyword			-->		string
				Punctuate		-->		string
				Operator		-->		string
				AstNode			-->		int
				CirNode			-->		int
				MutantID		-->		int
				Execution		-->		(string, int)
				InstanceNode	-->		(context, string, int)
				DependNode		-->		(context, string, int)
				SymNode			-->		(type, int)
				DataType		-->		string
		"""
		return self.token_value

	def __str__(self):
		return str(self.token_value)

	@staticmethod
	def __denormalize__(content: str):
		"""
		:param content:
		:return: translate the normalized text into original type
					\\s --> space
					\\a --> @
					\\p --> $
		"""
		content = content.replace("\\s", ' ')
		content = content.replace("\\a", '@')
		content = content.replace("\\p", '$')
		return content

	@staticmethod
	def parse(text: str):
		"""
		:param text:
		:return:
				n@null	b@bool	c@char	i@numb	f@real	i@x@y	s@text
				key@kw	opr@op	pun@pu	typ@ty	ast@id	cir@id	mut@int	tst@int
				exe@fun@int		ins@txt@int@int	sym@txt@int
		"""
		items = text.strip().split('@')
		if len(items) > 0:
			title = items[0].strip()
			if title == 'n':																			## n@null
				return CToken(CTokenType(CTokenType.Nullptr), None)
			elif title == 'b':																			## b@bool
				return CToken(CTokenType(CTokenType.Boolean), bool(items[1].strip() == "true"))
			elif title == 'c':																			## c@char
				return CToken(CTokenType(CTokenType.Character), int(items[1].strip()))
			elif title == 'i':																			## i@int
				return CToken(CTokenType(CTokenType.Integer), int(items[1].strip()))
			elif title == 'f':																			## f@real
				return CToken(CTokenType(CTokenType.Float), float(items[1].strip()))
			elif title == 'x':																			## x@real@real
				value = (float(items[1].strip()), float(items[2].strip()))
				return CToken(CTokenType(CTokenType.Complex), value)
			elif title == 's':																			##	s@txt
				return CToken(CTokenType(CTokenType.String), CToken.__denormalize__(items[1].strip()))
			elif title == 'key':																		##	key@txt
				return CToken(CTokenType(CTokenType.Keyword), CToken.__denormalize__(items[1].strip()))
			elif title == "opr":																		##	opr@txt
				return CToken(CTokenType(CTokenType.Operator), CToken.__denormalize__(items[1].strip()))
			elif title == "pun":																		##	pun@txt
				return CToken(CTokenType(CTokenType.Punctuate), CToken.__denormalize__(items[1].strip()))
			elif title == "typ":																		##	typ@txt
				return CToken(CTokenType(CTokenType.DataType), CToken.__denormalize__(items[1].strip()))
			elif title == "ast":																		## 	ast@int
				return CToken(CTokenType(CTokenType.AstNode), int(items[1].strip()))
			elif title == "cir":																		## 	cir@int
				return CToken(CTokenType(CTokenType.CirNode), int(items[1].strip()))
			elif title == "mut":																		## 	mut@int
				return CToken(CTokenType(CTokenType.MutantID), int(items[1].strip()))
			elif title == "tst":																		## 	tst@int
				return CToken(CTokenType(CTokenType.TestID), int(items[1].strip()))
			elif title == "exe":																		##	exe@str@int
				value = (items[1].strip(), int(items[2].strip()))
				return CToken(CTokenType(CTokenType.Execution), value)
			elif title == "ins":																		## 	ins@str@int@int
				value = (items[1].strip(), int(items[2].strip()), int(items[3].strip()))
				return CToken(CTokenType(CTokenType.Instance), value)
			elif title == "sym":																		##	sym@txt@int
				value = (items[1].strip(), int(items[2].strip()))
				return CToken(CTokenType(CTokenType.SymNode), value)
			else:																						## invalid
				return None
		return None																						## empty-string


def rand_select(objects):
	"""
	:param objects:
	:return: select a random object from the collection
	"""
	counter = random.randint(0, len(objects))
	selected_object = None
	for item in objects:
		selected_object = item
		counter -= 1
		if counter < 0:
			break
	return selected_object


def rand_resort(objects):
	"""
	:param objects:
	:return: list of objects randomly sorted from the inputs
	"""
	remains = set()
	for obj in objects:
		remains.add(obj)
	rlist = list()
	while len(remains) > 0:
		obj = rand_select(remains)
		remains.remove(obj)
		rlist.append(obj)
	return rlist

