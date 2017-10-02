/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2017, Telestax Inc and individual contributors
 * by the @authors tag. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.restcomm.media.codec.opus;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.restcomm.media.codec.opus.Decoder;
import org.restcomm.media.codec.opus.Encoder;
import org.restcomm.media.codec.opus.OpusJni;
import org.restcomm.media.spi.memory.Frame;
import org.restcomm.media.spi.memory.Memory;


/**
 * Opus codec test class
 * 
 * @author Vladimir Morosev (vladimir.morosev@telestax.com)
 * 
 */
public class OpusCodecTest implements OpusJni.Observer {

    private static final Logger log = Logger.getLogger(OpusCodecTest.class);

    private Frame buffer = Memory.allocate(512);
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    public OpusCodecTest() {        
    }            

    @Before
    public void setUp() throws Exception {
        buffer.setLength(512);         
    }

    @After
    public void tearDown() throws Exception {        
    }
    
    /**
     * Test of process method, for Encoder and Decoder.
     */
    @Test
    public void testCodec() {
    	
    	try {
	        FileInputStream inputStream = new FileInputStream("src\\main\\jni\\test_sound_mono_48.pcm");
	        FileOutputStream outputStream = new FileOutputStream("src\\main\\jni\\test_sound_mono_48_decoded.pcm", false);
	    	
	    	OpusJni opus = new OpusJni();
	    	opus.setOpusObserverNative(this);
	    	opus.sayHelloNative();
	    	opus.initNative();

	        try {
	        	byte[] input = new byte[960];
	        	short[] inputData = new short[480];
        		byte[] output = new byte[960];
	        	while (inputStream.read(input) == 960) {
	        		ByteBuffer.wrap(input).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(inputData);
	        		byte[] encodedData = opus.encodeNative(inputData);
	        		short[] decodedData = opus.decodeNative(encodedData);
	        		ByteBuffer.wrap(output).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(decodedData);
	        		outputStream.write(output);
	        	}
	        } finally {
	        	inputStream.close();
	        	outputStream.close();
	        	opus.closeNative();
	        }
    	} catch (IOException exc) {
        	return;
    		log.error("IOException: " + exc.getMessage());
    	}
    	
        org.restcomm.media.spi.dsp.Codec compressor = new Encoder();
        long s = System.nanoTime();
        compressor.process(buffer);
        long f = System.nanoTime();
        log.info("Duration=" + (f-s));
        
        org.restcomm.media.spi.dsp.Codec decompressor = new Decoder();
        s = System.nanoTime();
        decompressor.process(buffer);
        f = System.nanoTime();
        log.info("Duration=" + (f-s));
    }
    
    @Override
    public void onHello() {
    	log.info("Hello World - Java!");
    }
}
