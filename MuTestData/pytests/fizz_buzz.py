import os
import sys
import random


def rand_list(length):
    lst = list()
    for k in range(0, length):
        lst.append(random.randint(-32, 32))
    return lst


def write_list(writer, lst):
    for value in lst:
        writer.write(" " + str(value))
    writer.write("\n")


def generate_tests(file_path):
    counter = 0
    with open(file_path, "w") as writer:
        for length in range(0, 1024):
            for k in range(0, 4):
                write_list(writer, rand_list(length))
                counter += 1
    return counter


if __name__ == "__main__":
    print("Generate", generate_tests(sys.argv[1]), "tests.")

