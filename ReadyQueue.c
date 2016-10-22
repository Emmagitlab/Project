/*
 * ReadyQueue.c
 *
 * Created on: Oct 4, 2016
 *    Author: T440
 */

#include  "ReadyQueue.h"
#include <stdio.h>
#include <stdlib.h>


struct ready_Node *R_Head = NULL;
struct ready_Node *R_Tail = NULL;

int ready_Queue_size=0;


void Queue_R_Enqueue(struct ready_PCB *data){
	struct ready_Node *tmp = (struct ready_Node *)malloc(sizeof(struct ready_Node));
	tmp->data = data;
	tmp->next = NULL;
	if(R_Head == NULL && R_Head == R_Tail){
		R_Head = R_Tail= tmp;
	}else{
		R_Tail->next = tmp;
		R_Tail = tmp;
	}
	ready_Queue_size++;

}
void Queue_R_Dequeue(){
	if(ready_Queue_size >0){
		struct ready_Node *tmp = R_Head;
		if(R_Head == R_Tail){
			R_Head = R_Tail = NULL;
		}else{
			R_Head = R_Head->next;
		}

		ready_Queue_size--;
	}

};
boolean Queue_R_IsEmpty(){
	boolean isEmpty = true;
	if(ready_Queue_size ==0){
		isEmpty = false;
	}
	return isEmpty;
};



boolean Queue_R_Remove(int pid){
	struct ready_Node *tmp = R_Head;
	boolean result = FALSE;
	if(tmp->data->pid == pid && tmp != NULL){
		R_Head = R_Head->next;
		ready_Queue_size--;
		result = TRUE;
	}else {
		while(tmp->next != NULL){
			if(pid == tmp->next->data->pid){
				struct ready_Node *removePCB = tmp->data;
				tmp->next = tmp->next->next;
				if(tmp->next == NULL){
					R_Tail = tmp;
				}
				ready_Queue_size--;
				result = TRUE;
				break;
			}
			tmp = tmp->next;
		}

	}
	return result;

};



struct ready_PCB *Find_R_ByID(int pid){
	struct ready_Node *tmp = R_Head;
	struct ready_PCB *pcb= NULL;
	while(tmp != NULL){
		if(pid == tmp->data->pid){
			pcb = tmp->data;
			break;
		}
		tmp = tmp->next;

	}
	return pcb;
};


struct ready_PCB *Find_R_ByName(char processName[16]){
	struct ready_Node *tmp = R_Head;
	struct ready_PCB *pcb= NULL;
	while(tmp != NULL){
		if(processName == tmp->data->processName){
			pcb = tmp->data;
			break;
		}
		tmp = tmp->next;

	}
	return pcb;

};


struct ready_PCB *Find_R_ByContext(void *context){
	struct ready_Node *tmp = R_Head;
	struct ready_PCB *pcb= NULL;
	while(tmp != NULL){
		long tmpContext = tmp->data->context.Field1;
		if((long)context == tmpContext ){
			pcb = tmp->data;
			break;
		}
		tmp = tmp->next;

	}
	return pcb;
};


struct ready_PCB * Get_R_Head(){
	struct ready_PCB * head;
	if(R_Head){
		head= R_Head->data;
	}
	else{
		head = NULL;
	}
	return head;

}

