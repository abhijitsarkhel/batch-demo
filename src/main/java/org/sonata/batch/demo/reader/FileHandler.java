package org.sonata.batch.demo.reader;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;

public class FileHandler implements Tasklet, InitializingBean {

	private List<String> directories;

	private List<String> files;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if (directories.isEmpty()) {
			return RepeatStatus.FINISHED;
		}
		String dirName = directories.remove(0);
		if (dirName.contains("ViewData") && dirName.endsWith(".java")) {
			files.add(dirName);
		}
		return RepeatStatus.CONTINUABLE;
	}

	public List<String> getDirectories() {
		return directories;
	}

	public void setDirectories(List<String> directories) {
		this.directories = directories;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

}
