import os
import sys
import random


def write_list(writer, lst):
	for value in lst:
		writer.write(" " + str(value))
	writer.write("\n")


def rand_list(length):
	lst = list()
	for k in range(0, length):
		lst.append(random.randint(2, 10))
	return lst


def generate_tests(file_path):
	counter = 0
	with open(file_path, "w") as writer:
		for x in range(-4, 11):
			for y in range(-4, 11):
				for z in range(-4, 11):
					write_list(writer, [x, y, z])
					counter += 1
		for x in range(-10, 10):
			write_list(writer, [x])
			counter += 1
		for x in range(-10, 10):
			for y in range(-10, 10):
				write_list(writer, [x, y])
				counter += 1
		for length in range(0, 100):
			for k in range(0, 3):
				write_list(writer, rand_list(length))
				counter += 1
	return counter


if __name__ == "__main__":
    print("Generate", generate_tests(sys.argv[1]), "tests.")




