"""
It defines the model to describe token used in C program.
"""

import enum


class CTokenType(enum.Enum):
	"""
	The type of the token of C program is one of followings.
	"""
	nullptr = 0
	Boolean = 1
	Character = 2
	Integer = 3
	Double = 4
	String = 5
	Complex = 6
	AstNode = 7
	CirNode = 8
	Keyword = 9
	Punctuate = 10
	Operator = 11
	SymNode = 12
	CType = 13

	def __str__(self):
		return str(self.name)


class CToken:
	def __init__(self, token_type: CTokenType, token_value):
		"""
		:param token_type:  nullptr|Boolean|Character|Integer|Double|String|Complex|AstNode|CirNode|Keyword
		|Punctuate|Operator|SymNode|CType
		:param token_value: None|bool|int|int|float|str|complex|int|int|str|str|str|str|str
		"""
		self.token_type = token_type
		self.token_value = token_value
		return

	def get_type(self):
		return self.token_type

	def get_value(self):
		return self.token_value

	@staticmethod
	def parse(text: str):
		index = text.find('@')
		prev = text[0: index].strip()
		post = text[index + 1:].strip()
		if prev == 'n':
			return CToken(CTokenType(CTokenType.nullptr), None)
		elif prev == 'b':
			return CToken(CTokenType(CTokenType.Boolean), post == "true")
		elif prev == 'c':
			return CToken(CTokenType(CTokenType.Character), int(post))
		elif prev == 'i':
			return CToken(CTokenType(CTokenType.Integer), int(post))
		elif prev == 'f':
			return CToken(CTokenType(CTokenType.Double), float(post))
		elif prev == 's':
			return CToken(CTokenType(CTokenType.String), post)
		elif prev == 'x':
			index = post.find('@')
			real_part = float(post[0: index].strip())
			imag_part = float(post[index + 1:].strip())
			return CToken(CTokenType(CTokenType.Complex), complex(real_part, imag_part))
		elif prev == 'a':
			return CToken(CTokenType(CTokenType.AstNode), int(post))
		elif prev == 'r':
			return CToken(CTokenType(CTokenType.CirNode), int(post))
		elif prev == 'k':
			return CToken(CTokenType(CTokenType.Keyword), post)
		elif prev == 'o':
			return CToken(CTokenType(CTokenType.Operator), post)
		elif prev == 'p':
			return CToken(CTokenType(CTokenType.Punctuate), post)
		else:
			return None

	@staticmethod
	def get_data_type(word: str):
		if len(word) > 0:
			return CToken(CTokenType(CTokenType.CType), word)
		else:
			return CToken(CTokenType(CTokenType.nullptr), None)

	def __str__(self):
		return str(self.token_value)

