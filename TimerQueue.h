/*
 * TimerQueue.h
 *
 * Created on: Oct 4, 2016
 *    Author: T440
 */

#ifndef HEADER_TIMERQUEUE_H_
#define HEADER_TIMERQUEUE_H_

#include <stdio.h>
#include <stdlib.h>
typedef int boolean;
#define true 1
#define false 0



struct timer_Context{
	int Mode;
	long Field1;
	long Field2;
	long Field3;
	long Field4;
}timer_Context;

struct timer_Node{
	struct timer_PCB *data;
	struct timer_Node *next;

}timer_Node;

struct timer_PCB{
	int pid;
	long state;
	char processName[16];
	long wakeUptime;
	struct timer_Context context;

}timer_PCB;

void Queue_T_Enqueue(struct timer_PCB *data);
void Queue_T_Dequeue();
int Queue_T_IsEmpty();
boolean Queue_T_Remove(int pid);




struct timer_PCB *Find_T_ByID(int pid);
struct timer_PCB *Find_T_ByName(char processName[16]);
struct timer_PCB *Find_T_ByContext(void *context);
struct timer_PCB *Get_T_Head();


#endif /* HEADER_TIMERQUEUE_H_ */
