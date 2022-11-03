import os 
import subprocess
from tqdm import tqdm
# GA 
GAbase = 'cd GA \n java Application.java'

crossOverVals = [0.5,0.55,0.6,0.65,0.7]
mutationVals = [0.001,0.002,0.003,0.004,0.005]
populationSize = [1000,1500,2000,2500,3000,3500]

fitnesses = []
processes = set()
counter = 0

numThreads = 10

outPutControlArr = []

for crossOver in tqdm(crossOverVals, desc= "cross over values"):
    for mutation in (mutationVals):
        for popSize in (populationSize):
             
            processes.add(subprocess.Popen(GAbase + " " + str(crossOver) + " " +  str(mutation) + " " +  str(popSize), shell=True, stdout=subprocess.PIPE))
            counter+=1
            outPutControlArr.append("crossover: " + str(crossOver) + " mutation: " +  str(mutation) + " popSize: " +  str(popSize))
            if(counter%numThreads == 0 and counter != 0):
                print("Starting Thread Batch: " + str(counter/numThreads) + "/" + str(len(crossOverVals)*len(mutationVals)*len(populationSize)/numThreads))
                outputControlCounter = 0
                for p in processes:
                    out = p.communicate()[0].decode("utf-8")
                    fitnesses.append(outPutControlArr[outputControlCounter] + " fitness: " + out)
                    outputControlCounter+=1
                processes.clear()
                outPutControlArr = []

#Clean up any remaining threads this happens if total runs is not divisible by numThreads
outputControlCounter = 0
for p in processes:
    out = p.communicate()[0].decode("utf-8")
    fitnesses.append(outPutControlArr[outputControlCounter] + " fitness: " + out)
    outputControlCounter+=1


fitMin = 100000
saveFitMin = ""
for f in fitnesses:
    if(float(f.split(" ")[-1]) < fitMax):
        fitMax = float(f.split(" ")[-1])
        saveFitMax = f

#write fitnesses to file
with open('GAparamRecomender.txt', 'w') as f:
    for item in fitnesses:
        f.write(item)
    f.write("Recomended parameters for GA: " + saveFitMax)