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
>TEST TEST

3. Vector clocks are an extension of the Lamport timestamp algorithm. However, scaling a vector clock to handle multiple processes can be challenging. Propose some solutions to this and explain your argument. 
>