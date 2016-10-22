/*
 * TimerQueue.c
 *
 *  Created on: Oct 4, 2016
 *      Author: T440
 */

#include  "TimerQueue.h"
#include  "ReadyQueue.h"
#include <stdio.h>
#include <stdlib.h>

int timer_Queue_size=0;

struct timer_Node *T_Head = NULL;
struct timer_Node *T_Tail = NULL;

void Queue_T_Enqueue(struct timer_PCB *data){
	struct timer_Node *tmp = (struct timer_Node *)malloc(sizeof(struct timer_Node));
	tmp->data = data;

	if(T_Head == NULL && T_Head == T_Tail){
		tmp->next= NULL;
		T_Head = T_Tail = tmp;

	}else{
		struct timer_Node *head = T_Head;
		if(data->wakeUptime > head->data->wakeUptime ){
			while(head->next != NULL){
				head = head->next;
			}
			struct timer_Node *next = head->next;
			head->next = tmp;
			tmp->next = next;
			if(tmp->next == NULL){
				T_Tail = tmp;
			}
		}else{
			tmp->next = head;
			T_Head = tmp;
		}
	}
	timer_Queue_size++;
};
void Queue_T_Dequeue(){
	if(T_Head != NULL){
		struct timer_Node *tmp = T_Head;
		if(T_Head == T_Tail){
			T_Head = T_Tail = NULL;
		}else{
			T_Head = T_Head->next;
			if(tmp != NULL){
				free(tmp);
				tmp = NULL;
			}
		}
		timer_Queue_size--;
	}

};

boolean Queue_T_IsEmpty(){
	boolean isEmpty = true;
	if(timer_Queue_size ==0){
		isEmpty = false;
	}
	return isEmpty;
};


boolean Queue_T_Remove(int pid){
	struct timer_Node  *head = T_Head;
	boolean result = FALSE;

	if(pid == head->data->pid){
		T_Head = T_Head->next;
		timer_Queue_size--;
		result = TRUE;
		free(head);
		head = NULL;
	}else {
		while(head->next != NULL){
			if(pid == head->next->data->pid){
				struct ready_Node *removePCB = head->next;
				head->next = head->next->next;
				if(head->next == NULL){
					T_Tail = head;
				}
				free(removePCB);
				removePCB= NULL;
				timer_Queue_size--;
				result = TRUE;
				break;
			}
			head = head->next;
		}

	}
	return result;
};

struct timer_PCB * Get_T_Head(){
	struct timer_PCB * head;
	if(T_Head != NULL){
		head= T_Head->data;
	}
	else{
		head = NULL;
	}
	return head;

}



struct timer_PCB *Find_T_ByID(int pid){
	struct timer_Node *tmp = T_Head;
	struct timer_PCB *pcb= NULL;
	while(tmp != NULL){
		if(pid == tmp->data->pid){
			pcb = tmp->data;
			break;
		}
		tmp = tmp->next;

	}
	return pcb;
};

struct timer_PCB *Find_T_ByName(char processName[16]){
	struct timer_Node *tmp = T_Head;
	struct timer_PCB *pcb= NULL;
	while(tmp != NULL){
		if(processName == tmp->data->processName){
			pcb = tmp->data;
			break;
		}
		tmp = tmp->next;

	}
	return pcb;

};
struct timer_PCB *Find_T_ByContext(void *context){
	struct timer_Node *tmp = T_Head;
	struct timer_PCB *pcb= NULL;
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


