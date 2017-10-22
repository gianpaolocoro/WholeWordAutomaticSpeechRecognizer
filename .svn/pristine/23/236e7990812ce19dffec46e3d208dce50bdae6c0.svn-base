package org.unirender.asr.wholeword.audio;

import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.ObservationVector;

public class AudioProcessing {

	public static List<ObservationVector> selectObservations(int from, int to,
			double[][] X) {
		List<ObservationVector> sequence = new ArrayList<ObservationVector>();

		for (int i = from; i <= to; i++) {
			double vett[] = X[i];
			ObservationVector obs = new ObservationVector(vett);
			sequence.add(obs);
		}

		return sequence;
	}
	
    public static short[] trim(short sample[], int framesize){
         boolean crossingBoolean[] = zeroCrossing(sample);
         int energy[] = energy(sample, framesize);
         int crossing[] = zeroCrossing(sample,energy,crossingBoolean,framesize);
         short chopped[] = chop(sample, crossing, framesize);
         return chopped;
    }

    public static boolean[] zeroCrossing(short sample[]){
        
    	boolean crossing[] = new boolean[sample.length];
        for (int c = 0; c < sample.length - 1; c++){
            if (((sample[c] > 0) && (sample[c + 1] < 0)) || ((sample[c] < 0) && (sample[c + 1] > 0))){
                crossing[c] = true;
            }
        }
        return crossing;
    }
    
    
    public static int[] zeroCrossing(short sample[], int energy[], boolean crossing[], int frameSize){
        final double crossingConst = 12.5;

        int crossingCut[] = new int[2];
        crossingCut[0] = energy[0];
        crossingCut[1] = energy[1] - 1;
        
        double crossingFrame[] = new double [(int)(sample.length/frameSize)];

        double crossingSD = 0;    
        double IZC = 0;    
        double IZCT = 0.15625;    

        int crossingPeak = 0;    

        double runningSum = 0;

        int location = 0;
        
        for (int c = 0; c < crossingFrame.length; c++){
            runningSum = 0;
            for (int d = c * frameSize; d < (c + 1) * frameSize; d++){
                if (crossing[d]){
                    runningSum++;
                }
            }
            crossingFrame[c] = runningSum / frameSize;
        }

        runningSum = 0;
        for (int c = 0; c < 10; c++){
            runningSum += crossingFrame[c];
        }
        IZC = runningSum / 10;
        
        runningSum = 0;
        for (int c = 0; c < 10; c++){
            runningSum += crossingFrame[c] * crossingFrame[c];
        }
        crossingSD = Math.sqrt((runningSum / 10) - (IZC * IZC));


        if ((0.15625) > (IZC * 2 * crossingSD)){
            IZCT = IZC * 2 * crossingSD;
        }
                
        IZCT *= crossingConst;    

        location = crossingCut[0] - 8;
        if (location < 10){
            location = 10;
        }

        crossingPeak = location;
        while(location != crossingCut[0]){
            if (crossingFrame[crossingPeak] < crossingFrame[location]){
                crossingPeak = location;
            }
            location++;
        }     

        if (IZCT < crossingFrame[crossingPeak]){
            crossingCut[0] = crossingPeak;
            location = crossingCut[0] - 5;
            if (location < 10){
                location = 10;
            }

            crossingPeak = location;
            while(location != crossingCut[0]){
                if (crossingFrame[crossingPeak] >= crossingFrame[location]){
                    crossingPeak = location;
                }
                location++;
            }
            crossingCut[0] = crossingPeak;

        }

        runningSum = 0;
        for (int c = crossingFrame.length - 11; c < crossingFrame.length; c++){
            runningSum += crossingFrame[c];

        }

        IZC = runningSum / 10;
        
        runningSum = 0;
        for (int c = crossingFrame.length - 11; c < crossingFrame.length; c++){
            runningSum += crossingFrame[c] * crossingFrame[c];

        }
        crossingSD = Math.sqrt((runningSum / 10) - (IZC * IZC));

        IZCT = 0.15625;
        if ((0.15625) > (IZC * 2 * crossingSD)){
            IZCT = IZC * 2 * crossingSD;
        }
                
        IZCT *= crossingConst;

        location = crossingCut[1];

        crossingPeak = location;
        while(location != crossingCut[1]){
            if (crossingFrame[crossingPeak] < crossingFrame[location]){
                crossingPeak = location;
            }
            location--;
        }     
        if (IZCT < crossingFrame[crossingPeak]){
            crossingCut[1] = crossingPeak;
            location = crossingCut[1] + 5;
            if (location > crossingFrame.length - 1){
                location = crossingFrame.length - 1;
            }
            crossingPeak = location;
            while(location != crossingCut[1]){
                if (crossingFrame[crossingPeak] >= crossingFrame[location]){
                    crossingPeak = location;
                }
                location--;
            }     
            crossingCut[1] = crossingPeak;

        }
        crossingCut[1]++;

        return crossingCut;
    }
    
    public static int[] energy(short sample[], int frameSize){
        final double energyConst = 1.95;
        
        int energyCut[] = new int[2];

        double energyFrame[] = new double[(int)(sample.length / frameSize)];

        double runningSum = 0;

        double noiseEnergy = 0;

        double noiseEnergyThreshold = 0;

        int location = 0;
        int backwardLocation = 0;

        boolean belowThreshold = true;
        boolean valleyFound = true;

        for (int c = 0; c < energyFrame.length; c++){
            runningSum = 0;
            for(int d = c * frameSize; d < (c + 1) * frameSize; d++){
                runningSum += (sample[d] * sample[d]);
            }
            energyFrame[c] = runningSum / frameSize;
        }
        runningSum = 0;
        for (int c = 0; c < 10; c++){
            runningSum += energyFrame[c];
        }
        noiseEnergy = runningSum / 10;
        noiseEnergyThreshold = noiseEnergy * energyConst;
        energyCut[1] = energyFrame.length - 11;  
        energyCut[0] = 10;
        location = 10;    
        belowThreshold = true;
        while((location < (energyFrame.length - 9)) && (belowThreshold)){

            if (energyFrame[location] > noiseEnergyThreshold){

                runningSum = 0;    
                for (int c = 1; c < 16; c++){
                    if (energyFrame[location + c] > noiseEnergyThreshold){
                        runningSum++;
                    }
                }

                if (runningSum >= 14){
                    belowThreshold = false;
                    energyCut[0] = location;

                   
                    valleyFound = true;
                    backwardLocation = 0;
                    while((valleyFound) && (backwardLocation < 16) && ((location - backwardLocation) > 20)){

                        if (energyFrame[location - backwardLocation - 1] < energyFrame[location - backwardLocation]){
                            
                            energyCut[0] = location - backwardLocation - 1;

                        }
                        else{
                            valleyFound = false;
                        }
                        backwardLocation++;

                    }

                }

            }
            location++;
        }

        noiseEnergy= energyFrame[energyFrame.length-1];
        noiseEnergyThreshold = noiseEnergy * energyConst;
        energyCut[1] = energyFrame.length - 1; 


        location = energyFrame.length - 1;    
        belowThreshold = true;
        while((location > 18) && (belowThreshold)){
            if (energyFrame[location] > noiseEnergyThreshold){
                runningSum = 0;    
                for (int c = 1; c < 9; c++){
                    if (energyFrame[location - c] > noiseEnergyThreshold){
                        runningSum++;
                    }
                }
                if (runningSum >= 7){
                    belowThreshold = false;
                    energyCut[1] = location;

                  
                    valleyFound = true;
                    backwardLocation = 0;
                    while((valleyFound) && (backwardLocation < 8) && ((location + backwardLocation) < (energyFrame.length - 1))){

                        if (energyFrame[location + backwardLocation + 1] < energyFrame[location + backwardLocation]){
                        
                            energyCut[1] = location + backwardLocation + 1;
                        }
                        else{
                            valleyFound = false;
                        }
                        backwardLocation++;
                    }
                    energyCut[1]++;    

                }
            }
            location--;
        }
        return energyCut;
    }
    
    
    public static short[] chop(short sample[], int cut[], int frameSize){

        cut[0] *= frameSize;
        cut[1] *= frameSize;
        short chopFile[] = new short[cut[1] - cut[0]];
        for (int c = 0; c < chopFile.length; c++){
            chopFile[c] = sample[cut[0] + c];
        }
        return chopFile;
    }

	
}
