import os
import sys
import random


def write_list(writer, lst):
    for value in lst:
        writer.write(" " + str(value))
    writer.write("\n")
    return


def rand_list(length):
    lst = list()
    for k in range(0, length):
        lst.append(random.randint(-16, 16))
    return lst


def generate_tests(file_path):
    counter = 0
    with open(file_path, "w") as writer:
        write_list(writer, [])
        counter += 1
        for value in range(-12, 12):
            write_list(writer, [value])
            counter += 1
        for x in range(-4, 4):
            for y in range(-4, 4):
                write_list(writer, [x, y])
                counter += 1
        year = -2000
        for k in range(0, 10):
            for month in range(-2, 15):
                for day in range(-2, 34):
                    write_list(writer, [year, month, day])
                    counter += 1
                    year = year + 1
        for length in range(4, 256):
            write_list(writer, rand_list(length))
            counter += 1
    return counter


if __name__ == "__main__":
    print("Generate", generate_tests(sys.argv[1]), "tests.")
