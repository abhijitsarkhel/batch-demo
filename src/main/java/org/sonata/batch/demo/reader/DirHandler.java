package org.sonata.batch.demo.reader;

import java.io.File;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;

public class DirHandler implements Tasklet, InitializingBean {

	private String dirPath;

	private List<String> directories;

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		File dir = new File(dirPath);
		boolean found = false;
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				directories.add(file.getAbsolutePath());
				found = true;
			}
		}
		if (found) {
			return RepeatStatus.CONTINUABLE;
		} else {
			return RepeatStatus.FINISHED;
		}
	}

	public List<String> getDirectories() {
		return directories;
	}

	public void setDirectories(List<String> dirPath) {
		this.directories = dirPath;
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

}
