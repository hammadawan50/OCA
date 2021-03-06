package com.javathreads.chapter9;
/*
 *
 * Copyright (c) 1997-1999 Scott Oaks and Henry Wong. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and
 * without fee is hereby granted.
 *
 * This sample source code is provided for example only,
 * on an unsupported, as-is basis. 
 *
 * AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. AUTHOR SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  AUTHOR
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

public class SinTable2 implements Runnable {
	private class SinTableRange {
		public int start, end;
	}

	private float lookupValues[];
	private Thread lookupThreads[];
	private int startLoop, endLoop, curLoop, numThreads;

	public SinTable2() {
		lookupValues = new float[360 * 100];
		lookupThreads = new Thread[12];
		startLoop = curLoop = 0;
		endLoop = (360 * 100);
		numThreads = 12;
	}

	private synchronized SinTableRange loopGetRange() {
		if (curLoop >= endLoop)
			return null;
		SinTableRange ret = new SinTableRange();
		ret.start = curLoop;
		curLoop += (endLoop - startLoop) / numThreads + 1;
		ret.end = (curLoop < endLoop) ? curLoop : endLoop;
		return ret;
	}

	private void loopDoRange(int start, int end) {
		for (int i = start; i < end; i += 1) {
			float sinValue = (float) Math.sin((i % 360) * Math.PI / 180.0);
			lookupValues[i] = sinValue * (float) i / 180.0f;
		}
	}

	public void run() {
		SinTableRange str;
		while ((str = loopGetRange()) != null) {
			loopDoRange(str.start, str.end);
		}
	}

	public float[] getValues() {
		for (int i = 0; i < numThreads; i++) {
			lookupThreads[i] = new Thread(this);
			lookupThreads[i].start();
		}
		for (int i = 0; i < numThreads; i++) {
			try {
				lookupThreads[i].join();
			} catch (InterruptedException iex) {
			}
		}
		return lookupValues;
	}
}
