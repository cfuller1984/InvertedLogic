package com.invertedlogic.project;

import java.util.HashMap;

public class ProjectEntry {
	String mFilename;
	HashMap<String, ProjectEntry> mChildren;
	
	public ProjectEntry(String pFilename) {
		mFilename = pFilename;
	}
	
	ProjectEntry addProjectEntry(String pFilename) {
		ProjectEntry entry = new ProjectEntry(pFilename);
		mChildren.put(pFilename, entry);
		
		return entry;
	}
	
	ProjectEntry findProjectEntry(String pFilename) {
		return mChildren.get(pFilename);
	}
}
