# Metaheuristic CVRPTW solver 
In this work we construct two algorithms for solving the capacitated vehicle routing problem with time windows - namely a Genetic Algorithm and Ant Colony Optimisation.
For more details see the writeup pdf.

## Requirements
Python 3.9.6 and tqdm
openjdk 17.0.4
Written on ubuntu 22.04 but any other operating system should work. 

## Usage
First you will need to compile the java code.

If you want to suppress the output of the java program, add the output change the debug variable in the configuration file to false. 
To run the program use the python wrapper, simply run the following command in the root directory:
`python3 main.py --algorithm [GA|ACO]`

or to search fro parameters (you will need to install tqdm with pip install tqdm) additionally you will need to ensure that debug is set to false in the configuration file for either ACO or  GA respectively:

```python3 main.py --search_best_configuration [GA|ACO]```

## Direct execution

For the individual algorithms you can run the following commands:
```cd ACO \n java Application.java```
```cd GA \n java Application.java```


You can access the github repo at: https://github.com/Liam-Watson/MetaheuristicsCVRPTW
