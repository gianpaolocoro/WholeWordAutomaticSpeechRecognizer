package org.unirender.asr.wholeword.examples;

import java.io.File;

import org.unirender.asr.wholeword.language.RecognizerPreset;
import org.unirender.asr.wholeword.language.SupportedLanguages;
import org.unirender.asr.wholeword.recognizer.LiveSpeechRecognizer;

public class LiveRecognitionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		LiveSpeechRecognizer liveASR = new LiveSpeechRecognizer(SupportedLanguages.IT, new File("./MODELS/"),RecognizerPreset.YESNO);
		while (true) {
			liveASR.listenAndRecognize();
		}
	}

}
