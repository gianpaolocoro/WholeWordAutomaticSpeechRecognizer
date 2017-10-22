package org.unirender.asr.wholeword.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class UnZipper {
	
	public static void zipIt(File zipFile, File sourceFolder) {
        byte[] buffer = new byte[1024];
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;
            File [] innerFiles = sourceFolder.listFiles();
            for (File innerFile:innerFiles) {
                //System.out.println("File Added : " + innerFile.getName());
                ZipEntry ze = new ZipEntry(sourceFolder.getName()+File.separator+innerFile.getName());
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(innerFile);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();
            System.out.println("Folder successfully compressed");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public static void buildDigitsCorpus(File indexFile, File wavesFolder) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(indexFile));
		String line = br.readLine();
		LinkedHashMap<String, List<File>> files = new LinkedHashMap<String, List<File>>();
		System.out.println("Indexing");
		while (line!=null){
			String [] elements = line.trim().split(" ");
			//select one-digit only file
			if (elements.length==2){
				String word = elements[1]; 
				String filename = new File(elements[0]).getName();
				File waveFileToStore = new File(wavesFolder,filename);
				List<File> wordfiles = files.get(word);
				if (wordfiles==null)
					wordfiles = new ArrayList<File>();
				wordfiles.add(waveFileToStore);
				files.put(word, wordfiles);
			}
			line = br.readLine();
		}
		br.close();
		
		for (String word: files.keySet()){
			List<File> filesForWord = files.get(word);
			File folder = new File(indexFile.getParent(),word+"/");
			System.out.println("creating "+folder.getAbsolutePath());
			folder.mkdir();
			for (File fileForWord:filesForWord){
				Files.copy(fileForWord.toPath(),new File(folder,fileForWord.getName()).toPath());
			}
			File zipFile = new File(indexFile.getParent(),word+".zip");
			File outputFolder = new File(indexFile.getParent(),word);
			System.out.println("creating zip "+zipFile+" from "+outputFolder);
			zipIt(zipFile,outputFolder);	
		}
		
	}
	
	public static void unZip(File zipFFile) throws Exception {
		ZipFile zipFile = new ZipFile(zipFFile);
		Enumeration<?> enu = zipFile.entries();
		int nElements = 0;
		while (enu.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enu.nextElement();

			String name = zipEntry.getName();
			long size = zipEntry.getSize();
			long compressedSize = zipEntry.getCompressedSize();
			//System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize);

			// Do we need to create a directory ?
			File file = new File(zipFFile.getParent(), name);
			if (name.endsWith("/")) {
				file.mkdirs();
				continue;
			}

			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}

			// Extract the file
			if (!file.getName().endsWith(".IT0")) {
				InputStream is = zipFile.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = is.read(bytes)) >= 0) {
					fos.write(bytes, 0, length);
				}
				is.close();
				fos.close();
				nElements++;
			}
		}
		System.out.println(zipFFile.getName()+" N of Files: " + nElements);
		zipFile.close();
	}

	public static void unZipCorpus(File containerFolder) throws Exception {
		File[] corpusFiles = containerFolder.listFiles();
		for (File cFile : corpusFiles) {
			if (cFile.getName().endsWith(".zip"))
				UnZipper.unZip(cFile);
		}
	}

	public static void main(String[] args) throws Exception {
		//NOTE: After preparing the corpus Goldwave should be used to transform all wave files into PCM signed mono 8000Hz 16bit 128kbs
		
		//building corpus from a set of zipped folders
		unZipCorpus(new File("C:\\Users\\GP\\Desktop\\WorkFolder\\SpeechCorpus\\"));

		//building digits corpus
		/*
		File indexFile = new File("C:\\Users\\GP\\Desktop\\WorkFolder\\\SpeechCorpus\\Digits\\training single and connected.txt");
		File wavesFolder = new File("C:\\Users\\GP\\Desktop\\WorkFolder\\\SpeechCorpus\\Digits\\trainingsingle8k\\");
		
		buildDigitsCorpus(indexFile,wavesFolder);
		*/
	}
}