#include <stdio.h>
#include <stdbool.h>

int main(int argc, char *argv[]) {
    printf("bool : %llu\n", sizeof(bool));
    printf("char : %llu\n", sizeof(char));
    printf("short : %llu\n", sizeof(short));
    printf("int : %llu\n", sizeof(int));
    printf("long : %llu\n", sizeof(long));
    printf("llong : %llu\n", sizeof(long long));
    printf("float : %llu\n", sizeof(float));
    printf("double : %llu\n", sizeof(double));
    printf("ldouble : %llu\n", sizeof(long double));
    printf("cp_float : %llu\n", 2 * sizeof(float));
    printf("cp_double : %llu\n", 2 * sizeof(double));
    printf("cp_ldouble : %llu\n", 2 * sizeof(long double));
    printf("im_float : %llu\n", sizeof(float));
    printf("im_double : %llu\n", sizeof(double));
    printf("im_ldouble : %llu\n", sizeof(long double));
    printf("pointer : %llu\n", sizeof(void *));
    return 0;
}