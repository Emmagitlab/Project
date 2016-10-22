///*
// * Process.h
// *
// *  Created on: Sep 27, 2016
// *      Author: T440
// */
//
//#ifndef HEADER_PROCESS_H_
//#define HEADER_PROCESS_H_
//#include "Queue.h"
//
//typedef struct ProcessControlBlock{
//	int pid;
//	char processName[16];
//	long context;
//
//	int sectorID;
//	int diskID;
//    void* memoryBuffer;
//    int priority;
//
//    int wakeUpTimer;
//    struct ProcessControlBlock * parent;
//    struct ProcessControlBlock * childProcesses;
//
//
//}PCB;
//
//typedef struct PCB_Q{
//	PCB* pcb;
//	int size;
//	int maxSize;
//}PCB_Q;
//
//typedef Node PCB_TIMER_NODE;
//typedef Node PCB_READY_NODE;
//
//typedef Queue PCB_TIMER_QUEUE;
//typedef Queue PCB_READY_QUEUE;
//typedef Queue PCB_DISK_QUEUE;
//
//PCB_TIMER_QUEUE pcb_timer_queue;
//PCB_READY_QUEUE pcb_ready_queue;
//PCB_DISK_QUEUE pcb_disk_queue[8];
//
//
//
//
//PCB* OSCreateProcess(char* processName, long context, long priority, long pid, long ErrorReturned);
//extern PCB* FindPCBByName(char* name);
//extern PCB* FindPCBByPID(int pid);
//extern PCB* FindCurrent(int pid);
//extern void ReleasePCB();
//
//
//#endif /* HEADER_PROCESS_H_ */
