///*
// * Process.c
// *
// *  Created on: Sep 27, 2016
// *      Author: T440
// */
//
//#include  "Process.h"
//#include  "syscalls.h"
//#include  "protos.h"
//#include  <stdlib.h>
//#include  <string.h>
//#include  <Queue.h>
//#define MAX_PCB_NUM 15
//
//
//PCB pcb;
//PCB_Q pcb_q;
//
//void InitializePCDQ(int maxSize) {
//	pcb_q.maxSize = maxSize;
//	pcb_q.pcb = (PCB*) malloc(maxSize * sizeof(PCB));
//	pcb_q.size = 0;
//}
//
//void InitTimerQueue(int maxSize) {
//	InitQueue(&pcb_timer_queue, maxSize);
//}
//
//void InitDiskQueue(int maxSize) {
//	InitQueue(&pcb_disk_queue, maxSize);
//}
//
//void InitReadyQueue(int maxSize) {
//	InitQueue(&pcb_ready_queue, maxSize);
//}
//
//PCB* OSCreateProcess(char* processName, long context, long priority, long pid,
//		long ErrorReturned) {
//
//	pid = pcb_q.size;
//	PCB* pcb = FindPCBByName(processName);
//	if (pcb == NULL) {
//		if (pid < pcb_q.maxSize) {
//			strcpy(pcb_q.pcb[pcb_q.size].processName, processName);
//			pcb_q.pcb[pcb_q.size].pid = pid;
////			runningProcessPid = pcb_q.size;
//			pcb_q.pcb[pcb_q.size].context = context;
//			pcb_q.pcb[pcb_q.size].priority = priority;
//			pcb_q.size++;
//		} else {
//			printf("Reach Max PCB number\n");
//			return NULL;
//		}
//
//	} else {
//		printf("Duplicate Process Name\n");
//		return NULL;
//	}
//
//	return &pcb_q.pcb[pcb_q.size - 1];
//}
//
//PCB* FindCurrent(int pid) {
//	PCB* pcb = NULL;
//	int i;
//	for (i = 0; i < pcb_q.size; i++) {
//		if (pcb_q.pcb[i].pid == pid) {
//			pcb = &pcb_q.pcb[i];
//			break;
//		}
//	}
//	return pcb;
//}
//
//PCB* FindPCBByName(char* name) {
//	PCB* pcb = NULL;
//	int i = 0;
//	for (i = 0; i < pcb_q.size; i++) {
//		if (strcmp(pcb_q.pcb[i].processName, name) == 0) {
//			pcb = pcb_q.pcb + i;
//			break;
//		}
//
//	}
//	return pcb;
//}
//
//PCB* FindPCBByPID(int pid) {
//	PCB* pcb = NULL;
//	int i = 0;
//	for (i = 0; i < pcb_q.size; i++) {
//		if (pcb_q.pcb[i].pid == pid) {
//			pcb = pcb_q.pcb + i;
//			break;
//		}
//
//	}
//	return pcb;
//}
//
//void RemovePCB(Queue* q, void* data) {
//
//		QueuePtr temp = q->front;
//		QueuePtr pre = NULL;
//		while(temp != NULL){
//		if(data == temp->data){
//			if(temp == q->front){
//				q->front = q->front->next;
//				break;
//			}
//
//			else if(temp == q->rear){
//				q->rear = pre;
//				break;
//			}
//			else{
//				pre->next = temp->next;
//				break;
//			}
//
//		}else{
//			pre = temp;
//			temp= temp->next;
//
//		}
//	}
//
//
//
//
//
//
//
////	if (q->size > 0) {
////		QueuePtr temp = q->front->next;
////		if (data == temp->data) {
////			q->front->next = temp->next;
////			if (temp->next == NULL) {
////				q->rear = q->front;
////			}
////		} else {
////			while (temp->next != NULL) {
////				if (data == temp->next->data) {
////					temp->next = temp->next->next;
////					if (temp->next == NULL) {
////						q->rear->data = temp;
////					}
////					break;
////				}
////			}
////		}
////	} else {
////		q->front = q->rear = NULL;
////	}
//}
//
//void ReleasePCB() {
//	pcb_q.maxSize = 0;
//	pcb_q.size = 0;
//	free(pcb_q.pcb);
//
//}
