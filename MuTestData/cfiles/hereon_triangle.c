/*********************************************************************************
 * The hereon_triangle is a triangle of which edges are integers, while the hero
 * formula (see http://rosettacode.org/wiki/Heronian_triangles) is also integer.
 * For example, triangle(3, 4, 5) is hereonian triangle and so is triangle(6, 8, 
 * 10). Usually the primitive hereonian triangle is needed, then (6, 8, 10) must 
 * be excluded. The sorted is also needed, such as (3, 4, 5) rather than (5, 3, 4).
 * 
 * This program receives three integers as the lengths of the triangles
 * and output whether the triangle is hereonal.
*********************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define INT_ERR 10e-9
/* maximum congruent number */
int gcd(int, int);  
/* is the triangle primitive? */
int is_primitive(int, int, int);
/* is the triangle a hereonal */                
int is_hereonal(int, int, int);


/* maximum congruent number */
int gcd(int a, int b) {
    int t;
    while(b != 0) {
        t = b;
        b = a % b;
        a = t;
    }
    return a;
}


/* is the triangle primitive? */
int is_primitive(int a, int b, int c) {
    int t;
    t = gcd(a, b);
    if(t > 1) 
    {
        if((c % t) == 0)
            return 0;
        else 
        	return 1;
    }
    return 1;
}


/* is the triangle a hereonal */                
int is_hereonal(int a, int b, int c) {
    int sum, s, area2, area;
    sum = a + b + c;
    if((sum % 2) != 0) 
    {
        return 0;
    }
    else 
    {
        s = sum / 2;
        area2 = s * (s - a) * (s - b) * (s - c);
        if(area2 <= 0) 
        {
            return 0;
        }
        else 
        {
            area = sqrt(area2);
            return area2 == area * area;
        }
    }
}


/* main tester */
int main(int argc, char *argv[]) 
{
    int x, y, z;
    if(argc > 3) 
    {
        x = atoi(argv[1]);
        y = atoi(argv[2]);
        z = atoi(argv[3]);
        if(is_primitive(x, y, z) && is_hereonal(x, y, z)) 
            printf("Yes!\n");
        else 
        	printf("No!\n");
    }
    return 0;
}


