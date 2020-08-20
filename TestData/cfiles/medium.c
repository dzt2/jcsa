/**********************************************
 * The mediuum program receives three integers
 * as input and outputs the medium among them.
**********************************************/
#include <stdio.h>
#include <stdlib.h>

int medium(int a, int b, int c) 
{
    if(a > b) 
    {
        if(b > c) 
            return b;
        else 
        {
            if(a > c) 
                return c;
            else 
                return a;
        }
    }
    else 
    {
        if(a > c) 
            return a;
        else 
        {
            if(b > c)
                return c;
            else 
                return b;
        }
    }
}

int main(int argc, char **argv) 
{
    int a, b, c, r;
    if(argc > 3) 
    {
        a = atoi( argv[1] );
        b = atoi( argv[2] );
        c = atoi( argv[3] );
        r = medium(a, b, c);
        printf("medium: %d\n", r);
        return 0;
    }
    else
    {
    	fprintf(stderr, "Usage: a.exe num1 num2 num3\n");
    	return -1;
    }
}



