/*************************************************************
 * The days program computes the number of the day of a date
 * in a given year. Usually, it is assumed that the first day
 * in that year corresponds to 1 and the last day refers to
 * either 365 or 366, depending on whether it's a leap year.
************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>


/* determine whether the year is a leap-year. */
bool is_leap_year(int year) 
{
	return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
}

/* determine whether the date is valid */
bool is_valid_date(int year, int month, int day)
{
	if(day > 0)
	{
		switch(month)
		{
		case 1:		return day <= 31;
		case 2:
		{
			if(is_leap_year(year))
			{
				return day <= 29;
			}
			else 
			{
				return day <= 28;
			}
		}
		case 3:		return day <= 31;
		case 4:		return day <= 30;
		case 5:		return day <= 31;
		case 6:		return day <= 30;
		case 7:		return day <= 31;	
		case 8:		return day <= 31;
		case 9:		return day <= 30;
		case 10:	return day <= 31;
		case 11:	return day <= 30;
		case 12:	return day <= 31;
		default:	return false;
		}
	}
	else 
	{
		return false;
	}
}

/* determine the number of days in given date */
int count_date(int year, int month, int day)
{
	int counter;
	switch(month)
	{
	case 1:     counter=0;      break;
    case 2:     counter=31;     break;
    case 3:     counter=59;     break;
    case 4:     counter=90;     break;
    case 5:     counter=120;    break;
    case 6:     counter=151;    break;
    case 7:     counter=181;    break;
    case 8:     counter=212;    break;
    case 9:     counter=243;    break;
    case 10:    counter=273;    break;
    case 11:    counter=304;    break;
    case 12:    
	default:	counter=334;    break;
	}
	
	counter = counter + day;
	if(is_leap_year(year) && month > 2)
	{
		counter++;
	}
	
	return counter;
}


/* main testing method */
int main(int argc, char *argv[]) {
    int day, month,year, counter;
    if(argc > 3) 
    {
        year = atoi(argv[1]);
        month = atoi(argv[2]);
        day = atoi(argv[3]); 
        if(is_valid_date(year, month, day))
        {
        	counter = count_date(year, month, day);
        	fprintf(stdout, "%d-%d is the %dth day in %d.\n", month, day, counter, year);
        }
        else
        {
        	fprintf(stdout, "%d-%d-%d is not valid date.\n", year, month, day);
        }
        return 0;
    }
    else 
    {
    	fprintf(stderr, "Usage: a.exe year month day\n");
    	return 1;
    }
}

