/*
 * Dispatcher.c
 *
 *  Created on: Oct 10, 2016
 *      Author: T440
 */
#include             "global.h"
#include             "syscalls.h"
#include             "protos.h"
#include             "string.h"
#include             <stdlib.h>
#include             <ctype.h>
#include			 "Process.h"
#include             "Dispatcher.h"
#include             "WasteTime.h"




void Dispatcher(){
	MEMORY_MAPPED_IO mmio;
	struct ready_PCB *runningPCB;
	int runnningPID;

//	runningPcb = FindCurrent(runningProcessPid);

	runningPCB=Get_R_Head();
	while(runningPCB==NULL) {

//		if(GetHead(&pcb_timer_queue)!= NULL || GetHead(&pcb_disk_queue!=NULL)) {
//			mmio.Mode=Z502Action;
//			mmio.Field1=mmio.Field2=mmio.Field3=0;
//			MEM_WRITE(Z502Idle, &mmio);
//
//		}
		CALL(WasteTime());
	}
	//pcb not null, start PCB if pcb is null call idle

	    runnningPID = runningPCB->pid;
	    Queue_R_Dequeue();

		mmio.Mode = Z502StartContext;
		mmio.Field1 =runningPCB->context.Field1;
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);


}
