package com.invertedlogic.project;

import java.io.File;

public class Project {
	protected static Project smThis = null;
	
	public static void Create() {
		smThis = new Project();
	}
	
	public static Project Get() {
		return smThis;
	}
	
	ProjectEntry mRoot = new ProjectEntry(".\\xml");
	
	public Project() {
		populate("C:\\Users\\cfuller\\workspace\\InvertedLogicTestBeds\\assets", mRoot);
	}
	
	void populate(String pDirectoryPath, ProjectEntry pParentEntry) {
		File[] files = new File(pDirectoryPath).listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
		    	ProjectEntry entry = pParentEntry.addProjectEntry(file.getName());
		    	populate(pDirectoryPath + "\\" + file.getName(), entry);
			} else {
				pParentEntry.addProjectEntry(file.getName());
			}
		}
	}
	
	public ProjectEntry findProjectEntry(String pId) {
		return mRoot.findProjectEntry(pId);
	}
}
