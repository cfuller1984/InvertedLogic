package com.invertedlogic.util;

import java.util.ArrayList;

public class Align {
	private static final ArrayList<Align> mAligns = new ArrayList<Align>();
	
    private final String[] mName;

    private Align(String pName) {
    	mName = new String[1];
    	mName[0] = pName;
    	
    	mAligns.add(this);
    }
    
    private Align(String[] pName) {
    	mName = new String[pName.length];
    	
    	for (int i = 0; i < pName.length; ++i) {
    		mName[i] = pName[i];
    	}
    	
    	mAligns.add(this);
    }

    public String toString()  { return mName[0]; }

    public static final Align Left =
        new Align("left");
    public static final Align Center =
        new Align(new String[] { "center", "centre" });
    public static final Align Right =
        new Align("right");
    
    public static final Align Top =
            new Align("top");
        public static final Align Middle =
            new Align("middle");
        public static final Align Bottom =
            new Align("bottom");
    
    public static Align getAlign(String pName) {
    	for (Align align : mAligns) {
    		for (String str : align.mName) {
    			if (str.equalsIgnoreCase(pName)) {
    				return align;
    			}
    		}
    	}
    	
    	return null;
    }
}