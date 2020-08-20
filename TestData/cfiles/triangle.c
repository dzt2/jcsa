/********************************************************
 * The triangle program determines the type of a triangle
 * which receives integers of three edges as inputs and
 * output the type of triangles.
********************************************************/

#include <stdio.h>
#include <stdlib.h>

#define EQ_TR   0   /* equilateral */
#define IS_TR   1   /* isosceles */
#define OD_TR   2   /* otherwise */
#define NT_TR   -1  /* not a triangle */

/* determine the triangle type */
int triangle(int a, int b, int c) {
    if((a > 0) && (b > 0) && (c > 0)) {
        if((a+b>c) && (b+c>a) && (a+c>b)) {
            if((a==b) && (b==c))
                return EQ_TR;
            else if((a==b) || (b==c) || (a==c))
                return IS_TR;
            else return OD_TR;
        }
        else return NT_TR;
    }
    else return NT_TR;
}

/* main function for testing */
int main(int argc, char **argv) 
{
    int x, y, z, t;
    if(argc > 3) 
    {
        x = atoi(argv[1]);
        y = atoi(argv[2]);
        z = atoi(argv[3]);
        t = triangle(x,y,z);
        switch(t) 
        {
            case EQ_TR: 
                printf("equilateral triangle\n"); 	
                break;
            case IS_TR:
                printf("isosceles triangle\n"); 	
                break;
            case OD_TR:
                printf("ordinary triangle\n"); 		
                break;
            default:
                printf("not the triangle\n");  
                break;
        }
        return 0;
    }
    else
    {
    	fprintf(stderr, "Invalid usage: a.exe x y z\n");
    	return -1;
    }
}


