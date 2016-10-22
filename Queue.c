/////*
//// * Queue.c
//// *
//// *  Created on: Oct 4, 2016
//// *      Author: T440
//// */
////
//#include "Queue.h"
//#include  "syscalls.h"
//#include  "protos.h"
//#include  <stdlib.h>
//#include  <string.h>
//#include  "Process.h"
//
//
//Queue queue;
//Node node;
//INT32 LockResult;
//char Success1[] = "      Action Failed\0        Action Succeeded";
////PCB_TIMER_QUEUE pcb_timer_queue;
//
//
//void InitQueue(Queue* q){
//	q->front = q->rear = NULL;
//	q->size = 0;
//}
//
//void EnTimerQueue(Queue* q, void* data){
//
//	QueuePtr temp = (QueuePtr) malloc(sizeof(Node));
//	temp->data = data;
//	PCB* tmpPCB = (PCB*) data;
//
//	if(q->front == NULL){
//		q->front = temp;
//		q->rear = temp;
//
//	}else{
//		QueuePtr head = q->front;
//		PCB* headPCB = (PCB*) head->data;
//		QueuePtr pre = NULL;
//		while( head!= NULL){
//			if(headPCB->wakeUpTimer > tmpPCB->wakeUpTimer){
//				if(q->front == q->rear){
//					q->front = temp;
//					temp->next = head;
//					q->rear = head;
//				}
//				else{
//					pre->next = temp;
//					temp->next = head;
//				}
//				break;
//
//			}else{
//				if(head == q->rear){
//					q->rear->next = temp;
//					q->rear = temp;
//				} else{
//					pre = head;
//					head = head->next;
//
//				}
//			}
//		}
//	}
//	q->size++;
//
//}
//
//void EnQueue(Queue* q, void* data){
//
//
//	QueuePtr temp = (QueuePtr) malloc(sizeof(Node));
//	temp->data = data;
//	temp->next = NULL;
//	if(q->front == NULL){
//		q->front = temp;
//		q->rear = temp;
//	}else{
//		q->rear->next = temp;
//		q->rear = temp;
//	}
//	q->size++;
//
//}
//
//
//
//QueuePtr GetHead(Queue* q){
//
//	QueuePtr head;
//	if(q->front == NULL){
//		head = NULL;
//	}else{
//		head = q->front;
//	}
//
//
//	return head;
//}
//
//
//QueuePtr DeQueue(Queue* q){
//
//
//
//	QueuePtr temp;
//	if(q->front == NULL){
//		temp = NULL;
//	} else{
//	temp = q->front;
//	q->front = q->front->next;
//	if(q->front == NULL){
//		q->rear = NULL;
//	}
//	q->size--;
//	}
//
//
//	return temp;
//
//}
//
//
//
//
//
//
