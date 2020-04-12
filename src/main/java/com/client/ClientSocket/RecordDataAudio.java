package com.client.ClientSocket;

import javax.sound.sampled.*;
import  sun.audio.*;    
import  java.io.*;


public class RecordDataAudio {
	// record duration, in milliseconds
    static final long RECORD_TIME = 60000;  // 1 minute
 
    // path of the wav file
    public static File wavFile = new File("/tmp/RecordAudio.wav");
 
    // format of audio file
    public static AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    public static  TargetDataLine line;
 
    /**
     * Defines an audio format
     */
    public static AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
 
    /**
     * Captures the sound and record into a WAV file
     */
    public static void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
            
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
 
    /**
     * Closes the target data line to finish capturing and recording
     */
    public static void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
 
    public void playAudio() throws IOException {
    	//** add this into your application code as appropriate
    	// Open an input stream  to the audio file.
    	InputStream in = new FileInputStream(wavFile);

    	// Create an AudioStream object from the input stream.
    	AudioStream as = new AudioStream(in);         

    	// Use the static class member "player" from class AudioPlayer to play
    	// clip.
    	AudioPlayer.player.start(as);            

    	// Similarly, to stop the audi
    	AudioPlayer.player.stop(as); 
    }
    
    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        for(int i=0; i< 10; i++) {
        	start();
        	try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        finish();
    }
}
