/******************************************************************************
 *                                                                            *
 *           Another CALendar Copyright (C) 2016 Chris Marsh                  *
 *               <https://github.com/chris-marsh/calendar                     *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU General Public License as published by the      *
 * Free Software Foundation, either version 3 of the License, or any later    *
 * version.                                                                   *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License   *
 * at <http://www.gnu.org/licenses/> for more details.                        *
 *                                                                            *
 ******************************************************************************/
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* ---------------------- acal.h ----------------------------- */
/* system configure arguments */
#define HEADERFILE_ACAL
#define VERSION "0.1"
/* setting arguments */
#define ANSI_BLUE     ""
#define ANSI_REVERSE  ""
#define ANSI_RESET    ""
/*  */
#define CAL_HEIGHT 8
/* Visible is 21,allow control chars(2*7) and term(1) */
#define CAL_WIDTH 36 
/* Limited by the inc_month function */
#define MAX_YEAR (INT_MAX / 12 - 1)  

/* name of days in week */
enum Days_of_Week{
    SUNDAY = 0,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY
};

/* object to represent the current date */
struct Date {
    int day;
    int month;
    int year;
};

/* calculate the calendar and data item */
static void set_weekday_start(enum Days_of_Week day);
static void set_highlight_date(struct Date Date);
static void display_one_month(struct Date date);
static void display_three_months(struct Date date);
static void display_twelve_months(struct Date date);

/* Header of the day in week */
static char *weekday_column_headers[7] = {
    "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"
};

/* objects of months (with name and day numbers) */
static struct {
    char *long_name;
    int num_days;
} months[12] = {
    {"January", 31},
    {"February", 28},
    {"March", 31},
    {"April", 30},
    {"May", 31},
    {"June", 30},
    {"July", 31},
    {"August", 31},
    {"September", 30},
    {"October", 31},
    {"November", 30},
    {"December", 31}
};
static enum Days_of_Week weekday_start = MONDAY;
static struct Date highlight_date = { 0, 0, 0 };

/* ---------------------- acal.c ----------------------------- */
/*
 * Set which day of the week will be the calendars first column
 */
static void set_weekday_start(enum Days_of_Week day) {
    weekday_start = day;
}

/*
 * Set date to highlight on the calendar
 */
static void set_highlight_date(struct Date date) {
    highlight_date = date;
}

/*
 * Add -/+num to month and inc/dec year as necessary. To simplify the
 * calculation, months are converted to 0 base and years converted to months.
 */
static void inc_month(int num, int *month, int *year) {
    int tempYear  = *year;
    int tempMonth = *month - 1;
    *year = (tempYear * 12 + tempMonth + num) / 12;
    *month = (tempYear * 12 + tempMonth + num) % 12 + 1;
}

/*
 * Given month and year, calculate the day of week of the first day.
 * Day is returned as an integer 0-6, Sun-Sat
 * Based on Zeller's congruence;
 * https://en.wikipedia.org/wiki/Zeller's_congruence
 */
static int first_day_of_month(int month, int year) {
	int century;
    if (month < 3) {
        month += 12;
        year--;
    }
    century = year / 100;
    year = year % 100;
    return (((13 * (month + 1)) / 5) +
            (century / 4) + (5 * century) +
            year + (year / 4)) % 7;
}

/*
 * Given month and year, populate the string array with a calendar month.
 */
static void assemble_calendar_month(
        char calendar[CAL_HEIGHT][CAL_WIDTH], int month, int year) {
    int day, first_day, line=0;

    /* Adjust Februarys days for leap year */
    if ((month == 2) &&
            ((!(year % 4) && (year % 100)) || !(year % 400))) {
        months[month-1].num_days = 29;
    }

    /* Initialize calendar strings - needed when appending */
    for (line=0; line<CAL_HEIGHT; line++)
        calendar[line][0]='\0';

    /* Calendar month/year header */
    sprintf(calendar[0], "    %s %d", months[month-1].long_name, year);

    /* Day column headers */
    strcpy(calendar[1], ANSI_BLUE);
    for (day=0; day <7; day++) {
        sprintf(calendar[1], "%s%s ",
                calendar[1],
                weekday_column_headers[(day+weekday_start) % 7]);
    }
    strcat(calendar[1], ANSI_RESET);

    /* Pad line to 1st day of month */
    first_day = first_day_of_month(month, year);
    for (day = 0; day < (first_day-(int)weekday_start+7)%7; day++)
        strcat(calendar[2], "   ");

    /* Add each day of month, incrementing to next line on weekend */
    line = 2;
    for (day = 1; day <= months[month-1].num_days; day++) {
        if (day == highlight_date.day &&
                year == highlight_date.year &&
                month == highlight_date.month) {
            sprintf(calendar[line],
                    "%s%s%2d%s ",
                    calendar[line],
                    ANSI_REVERSE, day, ANSI_RESET); 
        } else {
            sprintf(calendar[line],
                    "%s%2d ",
                    calendar[line],
                    day);
        } 

        /* if current_day == weekend */
        if (((first_day+day - 1) % 7) == 
                (((int)weekday_start + 6) % 7))
            line++;
    }
}

/*
 * Output a single calendar month
 */
static void display_one_month(struct Date date) {
    char calendar[CAL_HEIGHT][CAL_WIDTH];
    int line;

    assemble_calendar_month(calendar, date.month, date.year);
    for (line=0; line<CAL_HEIGHT; line++)
        puts(calendar[line]);
}

/*
 * Output 3 calendar months in vertical row
 */
static void display_three_months(struct Date date) {
    int i;
    char calendar[3][CAL_HEIGHT][CAL_WIDTH];

    inc_month(-1, &date.month, &date.year);

    for (i=0; i<3; i++) {
        assemble_calendar_month(calendar[i], date.month, date.year);
        inc_month(1, &date.month, &date.year);
    }

    for (i=0; i<CAL_HEIGHT; i++) {
        printf("%-21s  %-21s  %-21s\n",
                calendar[0][i],
                calendar[1][i],
                calendar[2][i]);
    }
}

/*
 * Output a 12 month calendar arranged as a 3x4
 */
static void display_twelve_months(struct Date date) {
    int i;

    inc_month(1, &date.month, &date.year);
    for (i=0; i<4; i++) {
        display_three_months(date);
        inc_month(3, &date.month, &date.year);
    }
}

/* ---------------------- main.c ----------------------------- */
/**
 *	HOW-TO-USE
 *	>>> gcc or clang the calendar.c
 *	>>> a.exe m year month	[print only one month as specified]
 *	>>> a.exe s year month	[print three months after specified]
 *	>>> a.exe c year month	[print twelve months since specified]
 *	>>> a.exe y year		[print the calendar of the input year]
 *	>>> a.exe another_other
 **/


/** Date.day is used to record the selection {m, s, c, y} **/
struct Date parse_input(int argc, char *argv[])
{
	struct Date input_date;
	if(argc > 2 && strlen(argv[1]) == 1)
	{
		input_date.day = argv[1][0];
		switch(argv[1][0])
		{
		case 'm':	
		case 's':
		case 'c':
			input_date.year = atoi(argv[2]);
			input_date.month = atoi(argv[3]);
			break;
		case 'y':
			input_date.year = atoi(argv[2]);
			input_date.month = 1;
			break;
		default:
			fprintf(stderr, "Usage:\na.exe m|s|c|y year [month]\n");
			exit(1);
		}
	}
	else
	{
		fprintf(stderr, "Usage:\na.exe m|s|c|y year [month]\n");
		exit(1);
	}
	return input_date;
}

/* main testing function */
int main(int argc, char *argv[])
{
	struct Date input_date;
	input_date = parse_input(argc, argv);
	switch((char) input_date.day)
	{
		case 'm':    display_one_month(input_date);    	break;
        case 's':    display_three_months(input_date); 	break;
        case 'c':   display_twelve_months(input_date);	break;
        case 'y':      display_twelve_months(input_date);	break;
        default:        break;		// infeasible case branch
	}
	return 0;
}


