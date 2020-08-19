#include <stdio.h>
#include <stdlib.h>
#include <string.h>


/* $Log: schedule.h,v $
 * Revision 1.4  1993/05/04  12:23:44  foster
 * Debug stuff removed
 *
 * Revision 1.3  1993/05/03  20:26:51  foster
 * Full functionality
 *
 * Revision 1.2  1993/05/03  17:14:24  foster
 * Restructure functions
 *
 * Revision 1.1  1993/05/01  11:35:36  foster
 * Initial revision
 * */

#define MAXPRIO 3
#define MAXLOPRIO 2
#define BLOCKPRIO 0
#define CMDSIZE 20 /* size of command buffer */
/* Scheduling commands */
#define NEW_JOB 1
#define UPGRADE_PRIO 2
#define BLOCK 3 
#define UNBLOCK 4
#define QUANTUM_EXPIRE 5
#define FINISH 6
#define FLUSH 7
/* stati */
#define OK 0
#define TRUE 1
#define FALSE 0
#define BADNOARGS -1 /* Wrong number of arguments */
#define BADARG -2    /* Bad argument (< 0) */
#define MALLOC_ERR -3
#define BADPRIO -4   /* priority < 0 or > MAXPRIO */
#define BADRATIO -5  /* ratio < 0 or > 1 */
#define NO_COMMAND -6 /* No such scheduling command */
struct process
{
    unsigned int pid;
    int priority;
    struct process *next;
};
extern int get_command(int *command, int *prio, float *ratio);
extern void exit_here(int status); /* Exit program with abs(status) */
extern int enqueue(int prio, struct process * job);
extern int new_job(int prio);
extern int schedule(int command, int prio, float ratio);
extern struct process * get_current();
extern int reschedule(int prio);
extern int put_end(int prio, struct process * process);
extern int get_process(int prio, float ratio, struct process ** job);
struct queue
{
    int length;
    struct process *head;
};
static struct queue prio_queue[MAXPRIO + 1]; /* blocked queue is [0] */


/* $Log: schedule.c,v $
 * Revision 1.4  1993/05/04  12:23:58  foster
 * Debug stuff removed
 *
 * Revision 1.3  1993/05/03  20:27:04  foster
 * Full Functionality
 *
 * Revision 1.2  1993/05/03  15:41:01  foster
 * Restructure functions
 *
 * Revision 1.1  1993/05/01  11:38:04  foster
 * Initial revision
 * */

#include <stdio.h>

static struct process * current_job;
static int next_pid = 0;

int enqueue(int prio, struct process *new_process)
{
    int status;
    if((status = put_end(prio, new_process)) != 0) 
        return(status); /* Error */
    return(reschedule(prio));
}

int get_command(int *command, int *prio, float *ratio)
{
    int status = OK;
    char buf[CMDSIZE];
    if(fgets(buf, CMDSIZE, stdin))
    {
        *prio = *command = -1; *ratio =-1.0;
        sscanf(buf, "%d", command);
        switch(*command)
        {
            case NEW_JOB :
                sscanf(buf, "%*s%d", prio);
                break;
            case UNBLOCK :
                sscanf(buf, "%*s%f", ratio);
                break;
            case UPGRADE_PRIO :
                sscanf(buf, "%*s%d%f", prio, ratio);
                break;
            default:
                break;
        }
        /* Find end of  line of input if no EOF */
        while(buf[strlen(buf)-1] != '\n' && fgets(buf, CMDSIZE, stdin));
        return(TRUE);
    }
    else 
        return(FALSE);
}

void exit_here(int status)
{
    exit(abs(status));
}

int new_job(int prio)
{
    int pid, status = OK;
    struct process *new_process;
    pid = next_pid++;
    new_process = (struct process *) malloc(sizeof(struct process));
    if(!new_process) 
        status = MALLOC_ERR;
    else
    {
        new_process->pid = pid;
        new_process->priority = prio;
        new_process->next = (struct process *) 0;
        status = enqueue(prio, new_process);
        if(status)
        {
            free(new_process); /* Return process block */
        }
    }
    if(status) 
        next_pid--; /* Unsuccess. Restore pid */
    return(status);
}

int upgrade_prio(int prio, float ratio) 
{
    int status;
    struct process * job;
    if(prio < 1 || prio > MAXLOPRIO) return(BADPRIO);
    if((status = get_process(prio, ratio, &job)) <= 0) 
        return(status);
    /* We found a job in that queue. Upgrade it */
    job->priority = prio + 1;
    return(enqueue(prio + 1, job));
}

int block() 
{
    struct process * job;
    job = get_current();
    if(job)
    {
        current_job = (struct process *)0; /* remove it */
        return(enqueue(BLOCKPRIO, job)); /* put into blocked queue */
    }
    return(OK);
}

int unblock(float ratio) 
{
    int status;
    struct process * job;
    if((status = get_process(BLOCKPRIO, ratio, &job)) <= 0) 
        return(status);
    /* We found a blocked process. Put it where it belongs. */
    return(enqueue(job->priority, job));
}

int quantum_expire() /* put current job at end of its queue */
{
    struct process * job;
    job = get_current();
    if(job)
    {
        current_job = (struct process *)0; /* remove it */
        return(enqueue(job->priority, job));
    }
    return(OK);
}

int finish()
{
    struct process * job;
    job = get_current();
    if(job)
    {
        current_job = (struct process *)0;
        reschedule(0);
        fprintf(stdout, " %d", job->pid);
        free(job);
        return(FALSE);
    }
    else 
    {
        return(TRUE);
    }
}

int flush() 
{
    while(!finish());
    fprintf(stdout, "\n");
    return(OK);
}

struct process * get_current() 
{
    int prio;
    if(!current_job)
    {
        for(prio = MAXPRIO; prio > 0; prio--)
        { 
            /* find head of highest queue with a process */
            if(get_process(prio, 0.0, &current_job) > 0) 
                break;
        }
    }
    return(current_job);
}

int reschedule(int prio) 
{
    if(current_job && prio > current_job->priority)
    {
        put_end(current_job->priority, current_job);
        current_job = (struct process *) 0;
    }
    get_current(); /* Reschedule */
    return(OK);
}

int schedule(int command, int prio, float ratio)
{
    int status = OK;
    switch(command)
    {
        case NEW_JOB :
            status = new_job(prio);
	        break;
        case QUANTUM_EXPIRE :
            status = quantum_expire();
	        break;
        case UPGRADE_PRIO :
            status = upgrade_prio(prio, ratio);
	        break;
        case BLOCK :
            status = block();
	        break;
        case UNBLOCK :
            status = unblock(ratio);
	        break;
        case FINISH :
            finish();
	        fprintf(stdout, "\n");
	        break;
        case FLUSH :
            status = flush();
	        break;
        default:
	        status = NO_COMMAND;
            break;
    }
    return(status);
}

int put_end(int prio, struct process *process) 
{
    struct process **next;
    if(prio > MAXPRIO || prio < 0) 
    {
        return(BADPRIO); /* Somebody goofed */
    }
    /* find end of queue */
    for(next = &prio_queue[prio].head; *next; next = &(*next)->next) ;
    *next = process;
    prio_queue[prio].length++;
    return(OK);
}

int get_process(int prio, float ratio, struct process ** job)
{
    int length, index;
    struct process **next;
    if(prio > MAXPRIO || prio < 0) 
    {
        return(BADPRIO); /* Somebody goofed */
    }
    if(ratio < 0.0 || ratio > 1.0) 
    {
        return(BADRATIO); /* Somebody else goofed */
    }
    length = prio_queue[prio].length;
    index = ratio * length;
    index = index >= length ? length -1 : index; /* If ratio == 1.0 */
    for(next = &prio_queue[prio].head; index && *next; index--)
    {
        next = &(*next)->next; /* Count up to it */
    }
    *job = *next;
    if(*job)
    {
	    *next = (*next)->next; /* Mend the chain */
	    (*job)->next = (struct process *) 0; /* break this link */
	    prio_queue[prio].length--;
	    return(TRUE);
    }
    else 
    {
        return(FALSE);
    }
}


int main(int argc, char *argv[]) 
{
    int command, prio;
    float ratio;
    int nprocs, status, pid;
    struct process *process;
    if(argc != MAXPRIO + 1) 
    {
        exit_here(BADNOARGS);
    }
    for(prio = MAXPRIO; prio > 0; prio--)
    {
	    if((nprocs = atoi(argv[MAXPRIO + 1 - prio])) < 0) 
        {
            exit_here(BADARG);
        }
        for(; nprocs > 0; nprocs--)
        {
            if((status = new_job(prio)) != 0) 
            {
                exit_here(status);
            }
        }
    }
    /* while there are commands, schedule it */
    while((status = get_command(&command, &prio, &ratio)) > 0)
    {
	    schedule(command, prio, ratio);
    }
    if(status < 0) 
    {
        exit_here(status); /* Real bad error */
    }
    else
    {
        exit_here(OK);
    }
}

