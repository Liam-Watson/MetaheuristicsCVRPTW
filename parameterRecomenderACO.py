import os 
import subprocess
from tqdm import tqdm

ACObase = 'cd ACO \n java Application.java'

swarmSize = [10,100,500,1500,3500]
alpha = [0.5,1,1.5,2,2.5]
beta = [2,2.5,3,3.5,4]
rho = [0.1,0.3,0.5,0.7,0.9]
# swarmSize = [10,100]
# alpha = [0.5,1]
# beta = [2,2.5]
# rho = [0.1,0.3]

fitnesses = []
processes = set()
counter = 0

numThreads = 5

outPutControlArr = []

for sSize in tqdm((swarmSize), desc= "Swarm size"):
    for a in (alpha):
        for b in (beta):
            for rh in (rho):
                processes.add(subprocess.Popen(ACObase + " " + str(sSize) + " " +  str(a) + " " +  str(b) + " " +  str(rh), shell=True, stdout=subprocess.PIPE))
                counter+=1
                outPutControlArr.append("swarm size: " + str(sSize) + " alpha: " +  str(a) + " beta: " +  str(b) + " rho: " +  str(rh))
                if(counter%numThreads == 0 and counter != 0):
                    print("Starting Thread Batch: " + str(counter/numThreads) + "/" + str(len(swarmSize)*len(alpha)*len(beta)*len(rho)/numThreads))
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
    fitnesses.append(outPutControlArr[outputControlCounter] + " " + out)
    outputControlCounter+=1

fitMin = 100000
saveFitMin = ""
for f in fitnesses:
    if(float(f.split(" ")[-1]) < fitMin):
        fitMin = float(f.split(" ")[-1])
        saveFitMax = f

#write fitnesses to file
with open('ACOparamRecomender.txt', 'w') as f:
    for item in fitnesses:
        f.write(item)
    f.write("Recomended parameters for ACO: " + str(saveFitMax))