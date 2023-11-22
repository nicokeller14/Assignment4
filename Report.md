Assignment 4
------------

# Team Members

>Nico Keller and Teo Field-Marsham

# GitHub link to your (forked) repository (if submitting through GitHub)

https://github.com/nicokeller14/Assignment4.git


# Task 4

1. What is the causal consistency? Explain it using the happened-before relation.
> Causal consistency ensures that operations that are causally related are seen by all 
> processes in a distributed system in the same order. The "happened-before"relation 
> says that if an event A causally affects B, then every process in the distributed 
> system must observe A before B. This ensures a logically coherent order of events 
> where all causal dependencies are respected, even if the system's processes are not 
> be perfectly synchronized.
> Source: https://www.educative.io/answers/what-is-causal-consistency-in-distributed-systems

2. You are responsible for designing a distributed system that maintains a partial ordering of operations on a data store (for instance, maintaining a time-series log database receiving entries from multiple independent processes/sensors with minimum or no concurrency). When would you choose Lamport timestamps over vector clocks? Explain your argument. 
   What are the design objectives you can meet with both?
> In this specific scenario Lamport timestamps are preferable to vector clocks because they
> are simpler to implement and manage, have a lower overhead and are more scalable and efficient. 
> Lamport timestamps and vector clocks both accomplish important design objectives including 
> concurrency management to help resolve conflicts, and event ordering which is vital for 
> preserving the order of logs. However, vector clocks offer more detailed causality insights but 
> come with a higher complexity and resource cost than Lamport timestamps, which are better suited 
> for simple event ordering in distributed systems.
> Source: https://cs.stackexchange.com/questions/101496/difference-between-lamport-timestamps-and-vector-clocks

3. Vector clocks are an extension of the Lamport timestamp algorithm. However, scaling a vector clock to handle multiple processes can be challenging. Propose some solutions to this and explain your argument. 
> Scaling vector clocks to handle multiple processes is challenging because they rely on a fixed index
> to represent each process's clock value. This means that the number of processes has to be constant and 
> known in advance which make running a complex system of processes difficult. Some solutions to this are 
> as follows:
> 
> Landes suggests using dynamic vector clocks which can adapt and allow a changing number of processes. 
> This works as follows: "Additionally, in a dynamic system, processes previously unknown to the receiver, 
> but already represented in the incoming timestamp, have to be included into the receiver's own vector."
> 
> Another option would be instead of having an expanding amount of processes, one could implement some form of
> garbage collection that deletes information about previous processes that are no longer in use and could 
> therefore free up space for new processes. 