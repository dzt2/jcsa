/**********************************************
 * The profit program receives a real number as
 * input and outputs the profit against it.
 *	>> a.exe x1 x2 x3 ... xN
 *	>> profit(xi) = yi
**********************************************/
#include <stdio.h>
#include <stdlib.h>

double get_profit(double);

double get_profit(double i) 
{
    double bonus1,bonus2,bonus4,bonus6,bonus10,bonus;
    bonus1=100000*0.1;
    bonus2=100000*0.1+100000*0.075;
    bonus4=100000*0.1+100000*0.075+200000*0.05;
    bonus6=100000*0.1+100000*0.075+200000*0.05+200000*0.03;
    bonus10=100000*0.1+100000*0.075+200000*0.05+200000*0.03+400000*0.015;
    if(i<=100000)
        bonus=i*0.1;
    else if(i<=200000)
      bonus=bonus1+(i-100000)*0.075;
    else if(i<=400000)
        bonus=bonus2+(i-200000)*0.05;
    else if(i<=600000)
        bonus=bonus4+(i-400000)*0.03;
    else if(i<=1000000)
        bonus=bonus6+(i-600000)*0.015;
    else 
    	bonus=bonus10+(i-1000000)*0.01;
    return(bonus);
}

int main(int argc, char **argv) {
    double x, y; int k;
    for(k = 1; k < argc; k++)
    {
    	x = atof(argv[k]);
    	y = get_profit(x);
    	printf("profit(%lf) = %lf\n", x, y);
    }
    return 0;
}


