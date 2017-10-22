package org.unirender.asr.wholeword.language;


public class LanguageModelGM extends ILanguageModel{

	@Override
	public double singleWordLanguageModel(String wordName, double likelihood) {
		//language model!
		double penalty=1;
		if (wordName.equalsIgnoreCase("NOVE")) 
			likelihood=likelihood/penalty;
		return likelihood;
	}

	@Override
	public double singleWordLanguageModelThr(String wordName) {
		//language model!
		double likelihood=-4200;
		if (wordName.equalsIgnoreCase("QUATTRO"))
			likelihood=-5100;
		if (wordName.equalsIgnoreCase("CINQUE"))
			likelihood=-4400;
		if (wordName.equalsIgnoreCase("SETTE"))
			likelihood=-4480;
		if (wordName.equalsIgnoreCase("AVANTI"))
			likelihood=-5140;
		if (wordName.equalsIgnoreCase("INDIETRO"))
			likelihood=-4800;
		if (wordName.equalsIgnoreCase("TERMINA"))
			likelihood=-4780;
		return likelihood;
	}
}
