import os
import sys
import random


if __name__ == "__main__":
    with open(sys.argv[1], "w") as writer:
        writer.write("\n")
        for value in range(-16, 3600):
            writer.write(str(value) + "\n")
        for length in range(1, 256):
            for k in range(0, length):
                writer.write(str(random.randint(3600, 7200)) + " ")
            writer.write("\n")
