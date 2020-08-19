import os
import sys
import random


def generate_normal_tests(min_val, max_val, writer):
    counter = 0
    for x in range(min_val, max_val + 1):
        for y in range(min_val, max_val + 1):
            for z in range(min_val, max_val + 1):
                writer.write(str(x) + " " + str(y) + " " + str(z) + "\n")
                counter += 1
    return counter


def rand_list(min_val, max_val, length):
    lst = list()
    for k in range(0, length):
        lst.append(random.randint(min_val, max_val))
    return lst


if __name__ == "__main__":
    counter = 0
    with open(sys.argv[1], "w") as writer:
        counter += generate_normal_tests(-2, 15, writer)
        for length in range(0, 64):
            for k in range(0, 4):
                lst = rand_list(14, 59, length)
                for value in lst:
                    writer.write(str(value) + " ")
                writer.write("\n")
                counter += 1
    print("Generate", counter, "tests.")

