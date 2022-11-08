import argparse, os

parser = argparse.ArgumentParser()

parser.add_argument("--algorithm", help="Algorithm to use", type=str, default="ACO")
parser.add_argument("--search_best_configuration", help="Search for best configuration", type=str, default="None")

args = parser.parse_args()

if(args.search_best_configuration == "None"):
    if(args.algorithm == "ACO"):
        print("executing ACO...")
        os.system("cd ACO \n java Application.java")
        print("done")
    elif(args.algorithm == "GA"):
        print("executing GA...")
        os.system("cd GA \n java Application.java")
        print("done")
elif(args.search_best_configuration == "ACO"):
    print("Searching for best ACO configuration...")
    os.system("python3 parameterRecomenderACO.py")
    print("done")
elif(args.search_best_configuration == "GA"):
    print("Searching for best GA configuration...")
    os.system("python3 parameterRecomenderGA.py")
    print("done")