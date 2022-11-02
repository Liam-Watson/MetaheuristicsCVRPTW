public class doors {
    public static final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());

    public static void main(String [] args){
        int totalItr = 100000000;
        int countCorrectFirst = 0;
        int countCorrectSwitch = 0;

        for(int j = 0; j < totalItr; j++){
            int [] doors = new int[3];

            for(int i = 0; i < 3; i++){
                doors[i] = 0;
            }
            int goatDoor = randomGenerator.nextInt(3);
            
            doors[goatDoor] = 1;

            int pick = randomGenerator.nextInt(3);

            if(doors[pick] == 1){
                countCorrectFirst++;
                // System.out.println("Correct First");
            }

            int reveal = -1;
            // do{
            //     reveal = randomGenerator.nextInt(3);
            // }while(reveal != pick && reveal != goatDoor);

            // if(reveal < 0){
            //     System.out.println("Error");
            // }

            for(int i =0 ; i < 3; i++){
                if(goatDoor != i && i != pick){
                    reveal = i;
                    break;
                }
            }
            
           int pickSwap = pick; 
            for(int i = 0; i < 3; i++){
                if(i != reveal && i != pick){
                    pickSwap = i;
                    if(doors[pickSwap] == 1){
                        countCorrectSwitch++;
                    }
                }
            }
            // System.out.println("Correct First: " + pick + " Correct Switch: " + pickSwap);
        }
        System.out.println("Correct first: " +(double)((double)(countCorrectFirst)/(double)totalItr)*100);
        System.out.println("Correct switch: " +(double)((double)(countCorrectSwitch)/(double)totalItr)*100);
    }
}
