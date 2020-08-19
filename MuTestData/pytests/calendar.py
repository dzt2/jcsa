import os
import sys
import random


def write_list(writer, lst):
    for value in lst:
        writer.write(" " + str(value))
    writer.write("\n")
    return


def generate_tests(file_path):
    counter = 0
    with open(file_path, "w") as writer:
        year = -2400
        for k in range(0, 80):
            for month in range(-1, 14):
                write_list(writer, ["m", year, month])
                year += 1
                write_list(writer, ["s", year, month])
                year += 1
                write_list(writer, ["c", year, month])
                year += 1
                write_list(writer, ["y", year, month])
                year += 1
                write_list(writer, ["t", year, month])
                year += 1
                counter += 5
        write_list(writer, [])
        counter += 1
        for option in range(-100, 100):
            write_list(writer, [option, random.randint(-100, 100), random.randint(-100, 100)])
            counter += 1
        for option in ['m', 's', 'c', 'y']:
            write_list(writer, [option])
            write_list(writer, [option, random.randint(-100, 100)])
            write_list(writer, [option, random.randint(-100, 100), random.randint(-100, 100)])
            write_list(writer, [option, random.randint(-100, 100), random.randint(-100, 100), random.randint(-100, 100)])
            counter += 4
    return counter


if __name__ == "__main__":
    print("Generate", generate_tests(sys.argv[1]), "tests.")

