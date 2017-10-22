package org.unirender.asr.wholeword.audio;

public class MfccExtraction
{
	private double samplingRate;
	private int ncoeff;
	private static int defaultNCoeff = 13;
	
	public MfccExtraction( double samplingRate, int ncoeff)
	{
		this.samplingRate = samplingRate;
		this.ncoeff = ncoeff;
	}
	
	public MfccExtraction( double samplingRate)
	{
		this.samplingRate = samplingRate;
		this.ncoeff = defaultNCoeff;
	}
	
	public double[][] extractMFCC(short inputSignal[]) throws Exception
	{
		FeaturesExtraction fe = new FeaturesExtraction();
		double matr[][] = fe.process(inputSignal, samplingRate);
		Delta.calcDelta(matr, ncoeff);
		Delta.calcDoubleDelta(matr, ncoeff);
		
		return matr ;
	}
	
	public void setncoeff(int numcoeff)
	{
		this.ncoeff = numcoeff; 
	}
	
	public void setSamplingRate(int samplingRate)
	{
		this.samplingRate = samplingRate; 
	}
}
