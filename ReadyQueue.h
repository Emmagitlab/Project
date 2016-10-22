/*
 * readyQueue.h
 *
 * Created on: Oct 4, 2016
 *    Author: T440
 */

#ifndef HEADER_READYQUEUE_H_
#define HEADER_READYQUEUE_H_

#include  "global.h"
#include <stdio.h>
#include <stdlib.h>

typedef int boolean;
#define true 1
#define false 0


struct ready_Context{
	int Mode;
	long Field1;
	long Field2;
	long Field3;
	long Field4;
}ready_Context;

struct ready_Node{
	struct ready_PCB *data;
	struct ready_Node *next;
}ready_Node;

struct ready_PCB{
	int pid;
	long state;
	char processName[16];
	long wakeUpTime;
	struct ready_Context context;

}ready_PCB;


void Queue_R_Enqueue(struct ready_PCB *data);
void Queue_R_Dequeue();
int Queue_R_IsEmpty();
boolean Queue_R_Remove(int pid);




struct ready_PCB *Find_R_ByID(int pid);
struct ready_PCB *Find_R_ByName(char processName[16]);
struct ready_PCB *Find_R_ByContext(void *context);
struct ready_PCB *Get_R_Head();




#endif /* HEADER_READYQUEUE_H_ */
