package org.unirender.asr.wholeword.audio;

public class FeaturesExtraction{
    protected final static int frameLength = 128;
    protected final static int shiftInterval = frameLength / 2;
    public int numCepstra = 13;
    protected final static int fftSize = frameLength;
    protected final static double preEmphasisAlpha = 0.95;
    protected final static double lowerFilterFreq = 200;
    protected final static double upperFilterFreq = 4000;
    protected final static int numMelFilters = 23;
    protected double frames[][];
    protected double hammingWindow[];
    protected FFT fft;
    
    public double[][] process(short inputSignal[],double samplingRate){
        double MFCC[][];
        fft = new FFT();
        // Pre-Emphasis
        double outputSignal[] = preEmphasis(inputSignal);
        
        // Frame Blocking
        framing(outputSignal);

        // Initializes the MFCC array
        MFCC = new double[frames.length][numCepstra*3];

        // apply Hamming Window to ALL frames
        hammingWindow();
        
        //
        // Below computations are all based on individual frames with Hamming Window already applied to them
        //
        for (int k = 0; k < frames.length; k++){
            
            // Magnitude Spectrum
            double bin[] = magnitudeSpectrum(frames[k]);

            // Mel Filtering
            int cbin[] = fftBinIndices(samplingRate,128);
            // get Mel Filterbank
            double fbank[] = melFilter(bin, cbin);

            // Non-linear transformation
            double f[] = nonLinearTransformation(fbank);

            // Cepstral coefficients
            double cepc[] = cepCoefficients(f);

            // Add resulting MFCC to array
            for (int i = 0; i < numCepstra; i++)
            {
                MFCC[k][i] = cepc[i];
                
              // if ( i==0 )
                //	MFCC[k][i] = 10 * ( Math.log10( MFCC[k][i] ) ); 
                //	MFCC[k][i] /= 100;
            }
        }        
        return MFCC;
    }
    
        public int[] fftBinIndices(double samplingRate,int frameSize){
        int cbin[] = new int[numMelFilters + 2];
        
        cbin[0] = (int)Math.round(lowerFilterFreq / samplingRate * frameSize);
        cbin[cbin.length - 1] = (int)(frameSize / 2);
        
        for (int i = 1; i <= numMelFilters; i++){
            double fc = centerFreq(i,samplingRate);

            cbin[i] = (int)Math.round(fc / samplingRate * frameSize);
        }
        
        return cbin;
    }
    
        public double[] melFilter(double bin[], int cbin[]){
        double temp[] = new double[numMelFilters + 2];

        for (int k = 1; k <= numMelFilters; k++){
            double num1 = 0, num2 = 0;

            for (int i = cbin[k - 1]; i <= cbin[k]; i++){
                num1 += ((i - cbin[k - 1] + 1) / (cbin[k] - cbin[k-1] + 1)) * bin[i];
            }

            for (int i = cbin[k] + 1; i <= cbin[k + 1]; i++){
                num2 += (1 - ((i - cbin[k]) / (cbin[k + 1] - cbin[k] + 1))) * bin[i];
            }

            temp[k] = num1 + num2;
        }

        double fbank[] = new double[numMelFilters];
        for (int i = 0; i < numMelFilters; i++){
            fbank[i] = temp[i + 1];
        }

        return fbank;
    }
        
    public double[] cepCoefficients(double f[]){
        double cepc[] = new double[numCepstra];
        
        for (int i = 0; i < cepc.length; i++){
            for (int j = 1; j <= numMelFilters; j++){
                cepc[i] += f[j - 1] * Math.cos(Math.PI * i / numMelFilters * (j - 0.5));
            }
        }
        
        return cepc;
    }

    public double[] nonLinearTransformation(double fbank[]){
        double f[] = new double[fbank.length];
        final double FLOOR = -50;
        
        for (int i = 0; i < fbank.length; i++){
            f[i] = Math.log(fbank[i]);
            
            // check if ln() returns a value less than the floor
            if (f[i] < FLOOR) f[i] = FLOOR;
        }
        
        return f;
    }
    protected static double log10(double value){
        return Math.log(value) / Math.log(10);
    }
    private static double centerFreq(int i,double samplingRate){
        double mel[] = new double[2];
        mel[0] = freqToMel(lowerFilterFreq);
        mel[1] = freqToMel(samplingRate / 2);
        
        // take inverse mel of:
        double temp = mel[0] + ((mel[1] - mel[0]) / (numMelFilters + 1)) * i;
        return inverseMel(temp);
    }
    private static double inverseMel(double x){
        double temp = Math.pow(10, x / 2595) - 1;
        return 700 * (temp);
    }
    protected static double freqToMel(double freq){
        return 2595 * log10(1 + freq / 700);
    }
    public double[] magnitudeSpectrum(double frame[]){
        double magSpectrum[] = new double[frame.length];
        
        // calculate FFT for current frame
        fft.computeFFT( frame );
        
        // calculate magnitude spectrum
        for (int k = 0; k < frame.length; k++){
            magSpectrum[k] = Math.pow(fft.real[k] * fft.real[k] + fft.imag[k] * fft.imag[k], 0.5);
        }

        return magSpectrum;
    }
    private void hammingWindow(){
        double w[] = new double[frameLength];
        for (int n = 0; n < frameLength; n++){
            w[n] = 0.54 - 0.46 * Math.cos( (2 * Math.PI * n) / (frameLength - 1) );
        }

        for (int m = 0; m < frames.length; m++){
            for (int n = 0; n < frameLength; n++){
                frames[m][n] *= w[n];
            }
        }
    }
    protected void framing(double inputSignal[]){
        double numFrames = (double)inputSignal.length / (double)(frameLength - shiftInterval);
        
        // unconditionally round up
        if ((numFrames / (int)numFrames) != 1){
            numFrames = (int)numFrames + 1;
        }
        
        // use zero padding to fill up frames with not enough samples
        double paddedSignal[] = new double[(int)numFrames * frameLength];
        for (int n = 0; n < inputSignal.length; n++){
            paddedSignal[n] = inputSignal[n];
        }

        frames = new double[(int)numFrames][frameLength];

        // break down speech signal into frames with specified shift interval to create overlap
        for (int m = 0; m < numFrames; m++){
            for (int n = 0; n < frameLength; n++){
                frames[m][n] = paddedSignal[m * (frameLength - shiftInterval) + n];
            }
        }
    }
    protected static double[] preEmphasis(short inputSignal[]){
        double outputSignal[] = new double[inputSignal.length];
        
        // apply pre-emphasis to each sample
        for (int n = 1; n < inputSignal.length; n++){
            outputSignal[n] = inputSignal[n] - preEmphasisAlpha * inputSignal[n - 1];
        }
        
        return outputSignal;
    }
}