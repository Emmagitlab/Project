/*
 * base.c
 *
 *  Created on: 2016骞�9鏈�8鏃�
 *      Author: Sicy
 */
/************************************************************************

 This code forms the base of the operating system you will
 build.  It has only the barest rudiments of what you will
 eventually construct; yet it contains the interfaces that
 allow test.c and z502.c to be successfully built together.

 Revision History:
 1.0 August 1990
 1.1 December 1990: Portability attempted.
 1.3 July     1992: More Portability enhancements.
 Add call to SampleCode.
 1.4 December 1992: Limit (temporarily) printout in
 interrupt handler.  More portability.
 2.0 January  2000: A number of small changes.
 2.1 May      2001: Bug fixes and clear STAT_VECTOR
 2.2 July     2002: Make code appropriate for undergrads.
 Default program start is in test0.
 3.0 August   2004: Modified to support memory mapped IO
 3.1 August   2004: hardware interrupt runs on separate thread
 3.11 August  2004: Support for OS level locking
 4.0  July    2013: Major portions rewritten to support multiple threads
 4.20 Jan     2015: Thread safe code - prepare for multiprocessors
 ************************************************************************/

#include             "global.h"
#include             "syscalls.h"
#include             "protos.h"
#include             "string.h"
#include             <stdlib.h>
#include             <ctype.h>
#include			 "ReadyQueue.h"
#include             "Dispatcher.h"
#include             "TimerQueue.h"
#include             "WasteTime.h"

//  Allows the OS and the hardware to agree on where faults occur
extern void *TO_VECTOR[];


char *call_names[] = { "mem_read ", "mem_write", "read_mod ", "get_time ",
		"sleep    ", "get_pid  ", "create   ", "term_proc", "suspend  ",
		"resume   ", "ch_prior ", "send     ", "receive  ", "PhyDskRd ",
		"PhyDskWrt", "def_sh_ar", "Format   ", "CheckDisk", "Open_Dir ",
		"OpenFile ", "Crea_Dir ", "Crea_File", "ReadFile ", "WriteFile",
		"CloseFile", "DirContnt", "Del_Dir  ", "Del_File " };

char* currProcessName;

/************************************************************************
 INTERRUPT_HANDLER
 When the Z502 gets a hardware interrupt, it transfers control to
 this routine in the OS.
 ************************************************************************/
void InterruptHandler(void) {
	INT32 DeviceID;
	INT32 Status;
	INT32 ErrorState;
	INT32 CurrentTime;
	INT32 diskID;
	QueuePtr node;
	MEMORY_MAPPED_IO mmio;       // Enables communication with hardware
	PCB* nextPcb;
	QueuePtr TEMPnode;
	QueuePtr TempDnode;
	static BOOL remove_this_in_your_code = TRUE; /** TEMP **/
	static INT32 how_many_interrupt_entries = 0; /** TEMP **/

	// Get cause of interrupt
	mmio.Mode = Z502GetInterruptInfo;
	mmio.Field1 = mmio.Field2 = mmio.Field3 = 0;
	MEM_READ(Z502InterruptDevice, &mmio);
	DeviceID = mmio.Field1;			//diskid
	Status = mmio.Field2;
	ErrorState = mmio.Field4;

	mmio.Mode = Z502ReturnValue;
	mmio.Field1 = mmio.Field2 = mmio.Field3 = mmio.Field4 = 0;
	MEM_READ(Z502Clock, &mmio);
	CurrentTime = mmio.Field1;
	if (ErrorState == ERR_SUCCESS) {
		switch (DeviceID) {
		case TIMER_INTERRUPT :
			node = DeQueue(&pcb_timer_queue);
			if(node == NULL){
				Dispatcher();
			} else{

			PCB* pcb = (PCB*) node->data;
			if (pcb != NULL) {
				EnQueue(&(pcb_ready_queue), (void*) pcb);
			}
			TEMPnode = GetHead(&pcb_timer_queue);
						if (TEMPnode != NULL) {
							nextPcb = TEMPnode->data;
							if (nextPcb->wakeUpTimer > CurrentTime) {

								mmio.Mode = Z502Start;
								mmio.Field1 = (long) (nextPcb->wakeUpTimer - CurrentTime);
			       				mmio.Field2 = mmio.Field3 = 0;
								MEM_WRITE(Z502Timer, &mmio);

							} else {
								node = DeQueue(&pcb_timer_queue);
								EnQueue(&pcb_ready_queue, (void*) node->data);

							}
						}
			}


			break;
		case DISK_INTERRUPT_DISK0 :
		case DISK_INTERRUPT_DISK1 :
		case DISK_INTERRUPT_DISK2 :
		case DISK_INTERRUPT_DISK3 :
		case DISK_INTERRUPT_DISK4 :
		case DISK_INTERRUPT_DISK5 :
		case DISK_INTERRUPT_DISK6 :
		case DISK_INTERRUPT_DISK7 :
			diskID = DeviceID - DISK_INTERRUPT;
			TempDnode = DeQueue(&pcb_disk_queue[diskID]);
			PCB* diskPCB = (PCB*) TempDnode->data;
			if (diskPCB != NULL) {
				EnQueue(&pcb_ready_queue, diskPCB);
				printf("");
			}

			break;

		default:

			break;
		}
	}

//	if (ErrorState == ERR_BAD_PARAM) {
//		printf("Illegal disk ID or Illegal sector");
//	} else if (ErrorState == ERR_NO_PREVIOUS_WRITE) {
//		printf("Reading form no previously written sector");
//	} else if (ErrorState == ERR_DISK_IN_USE) {
//		printf("Disk in use");
//	}

	// Clear out this device - we're done with it
	mmio.Mode = Z502ClearInterruptStatus;
	mmio.Field1 = DeviceID;
	mmio.Field2 = mmio.Field3 = mmio.Field4 = 0;
	MEM_WRITE(Z502InterruptDevice, &mmio);
}           // End of InterruptHandler

/************************************************************************
 FAULT_HANDLER
 The beginning of the OS502.  Used to receive hardware faults.
 ************************************************************************/

void FaultHandler(void) {
	INT32 DeviceID;
	INT32 Status;

	MEMORY_MAPPED_IO mmio;       // Enables communication with hardware

	// Get cause of interrupt
	mmio.Field1 = mmio.Field2 = mmio.Field3 = 0;
	mmio.Mode = Z502GetInterruptInfo;
	MEM_READ(Z502InterruptDevice, &mmio);
	DeviceID = mmio.Field1;
	Status = mmio.Field2;

	printf("Fault_handler: Found vector type %d with value %d\n", DeviceID,
			Status);
	// Clear out this device - we're done with it
	mmio.Mode = Z502ClearInterruptStatus;
	mmio.Field1 = DeviceID;
	MEM_WRITE(Z502InterruptDevice, &mmio);
} // End of FaultHandler

/************************************************************************
 SVC
 The beginning of the OS502.  Used to receive software interrupts.
 All system calls come to this point in the code and are to be
 handled by the student written code here.
 The variable do_print is designed to print out the data for the
 incoming calls, but does so only for the first ten calls.  This
 allows the user to see what's happening, but doesn't overwhelm
 with the amount of data.
 ************************************************************************/

void svc(SYSTEM_CALL_DATA *SystemCallData) {
	short call_type;
	static short do_print = 10;
	short i;
	INT32 Time;
	int Status;
	void *PageTable;
	char* processName;

	MEMORY_MAPPED_IO mmio;
	call_type = (short) SystemCallData->SystemCallNumber;

	if (do_print > 0) {
		printf("SVC handler: %s\n", call_names[call_type]);
		for (i = 0; i < SystemCallData->NumberOfArguments - 1; i++) {
			//Value = (long)*SystemCallData->Argument[i];
			printf("Arg %d: Contents = (Decimal) %8ld,  (Hex) %8lX\n", i,
					(unsigned long) SystemCallData->Argument[i],
					(unsigned long) SystemCallData->Argument[i]);
		}
		do_print--;
	}

//	printf((char *) SystemCallData->Argument[i]);
	switch (call_type) {
	//get time service call
	case SYSNUM_GET_TIME_OF_DAY:
		mmio.Mode = Z502ReturnValue;
		mmio.Field1 = mmio.Field2 = mmio.Field3 = 0;
		MEM_READ(Z502Clock, &mmio);
		*(long *) SystemCallData->Argument[0] = mmio.Field1;
		break;
		//system sleep call
	case SYSNUM_SLEEP:

		mmio.Mode = Z502ReturnValue;
		mmio.Field1 = mmio.Field2 = mmio.Field3 = 0;
		MEM_READ(Z502Clock, &mmio);

		Time = (long) SystemCallData->Argument[0];

		pcb = FindCurrent(runningProcessPid);
		pcb->wakeUpTimer = Time;
		EnTimerQueue(&pcb_timer_queue, pcb);

		//start timer

		headPCB = GetHead(&pcb_timer_queue)->data;
		if (pcb == headPCB) {
			mmio.Mode = Z502Start;
			mmio.Field1 = (long) SystemCallData->Argument[0];
			mmio.Field2 = mmio.Field3 = 0;
			MEM_WRITE(Z502Timer, &mmio);
		}

		Dispatcher();

		break;
		//system create process
	case SYSNUM_CREATE_PROCESS:
		if ((long) SystemCallData->Argument[2] < 0) {
			*SystemCallData->Argument[4] = ERR_BAD_PARAM;
		} else {
			PageTable = (void *) calloc(2, NUMBER_VIRTUAL_PAGES);
			mmio.Mode = Z502InitializeContext;
			mmio.Field1 = 0;
			mmio.Field2 = (long) SystemCallData->Argument[1];
			mmio.Field3 = (long) PageTable;
			MEM_WRITE(Z502Context, &mmio);
			pcb = OSCreateProcess((char*) SystemCallData->Argument[0],
					(long) mmio.Field1, (long) SystemCallData->Argument[2],
					(long) SystemCallData->Argument[3],
					(long) SystemCallData->Argument[4]);
			if (pcb != NULL) {
				EnQueue(&pcb_ready_queue, (void*) pcb);
				*SystemCallData->Argument[3] = pcb->pid;
				*SystemCallData->Argument[4] = ERR_SUCCESS;
			} else {
				*SystemCallData->Argument[4] = ERR_BAD_PARAM;
			}
		}
		break;
		//system get process id

	case SYSNUM_GET_PROCESS_ID:
		processName = (char*) SystemCallData->Argument[0];
		if (strlen(processName) == 0) {
			*SystemCallData->Argument[1] = runningProcessPid;
			*SystemCallData->Argument[2] = ERR_SUCCESS;
		} else {
			pcb = FindPCBByName(processName);

		}
		if (pcb != NULL) {
			*SystemCallData->Argument[1] = pcb->pid;
			*SystemCallData->Argument[2] = ERR_SUCCESS;
		} else {
			*SystemCallData->Argument[2] = ERR_BAD_PARAM;
		}
		break;

	case SYSNUM_PHYSICAL_DISK_READ:
		do {
			mmio.Mode = Z502Status;
			mmio.Field1 = (long) SystemCallData->Argument[0];
			mmio.Field2 = mmio.Field3 = 0;
			MEM_READ(Z502Disk, &mmio);
			CALL(WasteTime());
		} while (mmio.Field2 != DEVICE_FREE);

		pcb = FindCurrent(runningProcessPid);
		if (pcb != NULL) {
			pcb->diskID = (int) SystemCallData->Argument[0];
			pcb->sectorID = (int) SystemCallData->Argument[1];
			pcb->memoryBuffer = (void*) SystemCallData->Argument[2];
			EnQueue(&(pcb_disk_queue[pcb->diskID]), (void*) pcb);
		}

		mmio.Mode = Z502DiskRead;
		mmio.Field1 = (long) SystemCallData->Argument[0];
		mmio.Field2 = (long) SystemCallData->Argument[1];
		mmio.Field3 = (long) SystemCallData->Argument[2];
		mmio.Field4 = 0;
		MEM_READ(Z502Disk, &mmio);

		do {
			mmio.Mode = Z502Status;
			mmio.Field1 = (long) SystemCallData->Argument[0];
			mmio.Field2 = mmio.Field3 = 0;
			MEM_READ(Z502Disk, &mmio);
			CALL(WasteTime());
		} while (mmio.Field2 != DEVICE_FREE);

		Dispatcher();

		break;
	case SYSNUM_PHYSICAL_DISK_WRITE:
		do {
			mmio.Mode = Z502Status;
			mmio.Field1 = (long) SystemCallData->Argument[0];
			mmio.Field2 = mmio.Field3 = 0;
			MEM_READ(Z502Disk, &mmio);
			CALL(WasteTime());
		} while (mmio.Field2 != DEVICE_FREE);

		pcb = FindCurrent(runningProcessPid);
		if (pcb != NULL) {
			pcb->diskID = (int) SystemCallData->Argument[0];
			pcb->sectorID = (int) SystemCallData->Argument[1];
			pcb->memoryBuffer = (void*) SystemCallData->Argument[2];
			EnQueue(&(pcb_disk_queue[pcb->diskID]), (void*) pcb);
		}

		mmio.Mode = Z502DiskWrite;
		mmio.Field1 = (long) SystemCallData->Argument[0];
		mmio.Field2 = (long) SystemCallData->Argument[1];
		mmio.Field3 = (long) SystemCallData->Argument[2];
		MEM_WRITE(Z502Disk, &mmio);

//		mmio.Mode = Z502Action;
//		mmio.Field1 = mmio.Field2 = mmio.Field3 = 0;
//		MEM_WRITE(Z502Idle, &mmio);
		do {
			mmio.Mode = Z502Status;
			mmio.Field1 = (long) SystemCallData->Argument[0];
			mmio.Field2 = mmio.Field3 = 0;
			MEM_READ(Z502Disk, &mmio);
			CALL(WasteTime());
		} while (mmio.Field2 != DEVICE_FREE);
		Dispatcher();
		break;

		//system terminate call
	case SYSNUM_TERMINATE_PROCESS:
		//If ProcessID = -1, then terminate self
		if ((long) SystemCallData->Argument[0] == -1) {
			pcb = FindCurrent(runningProcessPid);
			if (pcb != NULL) {
//				ReleasePCB();
//				RemovePCB(&pcb_ready_queue,(void*)pcb);
//				RemovePCB(&pcb_timer_queue,(void*)pcb);
//				*SystemCallData->Argument[1] = ERR_SUCCESS;
				if (pcb_ready_queue.size == 0 && pcb_timer_queue.size == 0) {
					mmio.Mode = Z502ReturnValue;
					mmio.Field1 = mmio.Field2 = mmio.Field3 = mmio.Field4 = 0;
					MEM_WRITE(Z502Halt, &mmio);
					*SystemCallData->Argument[1] = ERR_SUCCESS;
				} else {
					Dispatcher();
				}
			} else {
				ReleasePCB();
				mmio.Mode = Z502ReturnValue;
				mmio.Field1 = mmio.Field2 = mmio.Field3 = mmio.Field4 = 0;
				MEM_WRITE(Z502Halt, &mmio);
				*SystemCallData->Argument[1] = ERR_SUCCESS;
			}

		} else if ((long) SystemCallData->Argument[0] == -2) {
			// terminite all halt
			ReleasePCB();
			mmio.Mode = Z502ReturnValue;
			mmio.Field1 = mmio.Field2 = mmio.Field3 = mmio.Field4 = 0;
			MEM_WRITE(Z502Halt, &mmio);
			*SystemCallData->Argument[1] = ERR_SUCCESS;
//			pcb = FindCurrent(runningProcessPid);
//			RemovePCB(&pcb_ready_queue, (void*) pcb);
//			RemovePCB(&pcb_timer_queue, (void*) pcb);
//			RemovePCB(&pcb_ready_queue, (void*) pcb->childProcesses);
//			RemovePCB(&pcb_timer_queue, (void*) pcb->childProcesses);

		} else {
			pcb = FindPCBByPID(SystemCallData->Argument[0]);
			RemovePCB(&pcb_ready_queue, (void*) pcb);
			*SystemCallData->Argument[1] = ERR_SUCCESS;

		}
		break;
		defaut: printf("ERROR! call_type not recognized!\n");
		printf("Call_type is %i\n", call_type);
	}
}                                               // End of svc

/************************************************************************
 osInit
 This is the first routine called after the simulation begins.  This
 is equivalent to boot code.  All the initial OS components can be
 defined and initialized here.
 ************************************************************************/

void osInit(int argc, char *argv[]) {
	void *PageTable = (void *) calloc(2, NUMBER_VIRTUAL_PAGES);
	INT32 i;
	MEMORY_MAPPED_IO mmio;
	long ProcessID;
	long ErrorReturned;
	PCB* pcb;

	// Demonstrates how calling arguments are passed thru to here

	printf("Program called with %d arguments:", argc);
	for (i = 0; i < argc; i++)
		printf(" %s", argv[i]);
	printf("\n");
	printf("Calling with argument 'sample' executes the sample program.\n");

	// Here we check if a second argument is present on the command line.
	// If so, run in multiprocessor mode
	if (argc > 2) {
		if (strcmp(argv[2], "M") || strcmp(argv[2], "m")) {
			printf("Simulation is running as a MultProcessor\n\n");
			mmio.Mode = Z502SetProcessorNumber;
			mmio.Field1 = MAX_NUMBER_OF_PROCESSORS;
			mmio.Field2 = (long) 0;
			mmio.Field3 = (long) 0;
			mmio.Field4 = (long) 0;
			MEM_WRITE(Z502Processor, &mmio);   // Set the number of processors
		}
	} else {
		printf("Simulation is running as a UniProcessor\n");
		printf(
				"Add an 'M' to the command line to invoke multiprocessor operation.\n\n");
	}

	//          Setup so handlers will come to code in base.c

	TO_VECTOR[TO_VECTOR_INT_HANDLER_ADDR ] = (void *) InterruptHandler;
	TO_VECTOR[TO_VECTOR_FAULT_HANDLER_ADDR ] = (void *) FaultHandler;
	TO_VECTOR[TO_VECTOR_TRAP_HANDLER_ADDR ] = (void *) svc;

	//  Determine if the switch was set, and if so go to demo routine.

	PageTable = (void *) calloc(2, NUMBER_VIRTUAL_PAGES);
	if ((argc > 1) && (strcmp(argv[1], "sample") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) SampleCode;
		mmio.Field3 = (long) PageTable;

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context

	} // End of handler for sample code - This routine should never return here

	//  By default test0 runs if no arguments are given on the command line
	//  Creation and Switching of contexts should be done in a separate routine.
	//  This should be done by a "OsMakeProcess" routine, so that
	//  test0 runs on a process recognized by the operating system.
	if ((argc > 1) && (strcmp(argv[1], "test0") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test0;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}
	if ((argc > 1) && (strcmp(argv[1], "test1") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test1;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}

	if ((argc > 1) && (strcmp(argv[1], "test2") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test2;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}
	if ((argc > 1) && (strcmp(argv[1], "test3") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test3;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}

	if ((argc > 1) && (strcmp(argv[1], "test4") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test4;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		pcb=OSCreateProcess(argv[1], mmio.Field1, 10, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
//      	EnQueue(&pcb_ready_queue, (void*) pcb);
//		Dispatcher();

	}

	if ((argc > 1) && (strcmp(argv[1], "test5") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test5;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}
	if ((argc > 1) && (strcmp(argv[1], "test6") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test6;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}
	if ((argc > 1) && (strcmp(argv[1], "test7") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test7;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}
	if ((argc > 1) && (strcmp(argv[1], "test8") == 0)) {
		mmio.Mode = Z502InitializeContext;
		mmio.Field1 = 0;
		mmio.Field2 = (long) test8;
		mmio.Field3 = (long) PageTable;
		mmio.Field4 = 0;

		InitializePCDQ(15);
		InitTimerQueue(15);
		InitReadyQueue(15);
		InitDiskQueue(15);

		MEM_WRITE(Z502Context, &mmio);   // Start of Make Context Sequence
		OSCreateProcess(argv[1], mmio.Field1, 100, 0, mmio.Field4);
		mmio.Mode = Z502StartContext;
		// Field1 contains the value of the context returned in the last call
		mmio.Field2 = START_NEW_CONTEXT_AND_SUSPEND;
		MEM_WRITE(Z502Context, &mmio);     // Start up the context
	}

}                                               // End of osInit

