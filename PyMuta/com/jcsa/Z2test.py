import z3


if __name__ == "__main__":
	x = z3.Int('x')
	print(z3.solve((x == 0), (abs(5) < 0)))

