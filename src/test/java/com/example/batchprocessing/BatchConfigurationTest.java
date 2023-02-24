/**
 * 
 */
package com.example.batchprocessing;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author mjedli
 *
 */
@SpringBatchTest
@SpringBootTest
@RunWith(SpringRunner.class)
public class BatchConfigurationTest {
	
	private static final String EXPECTED_FILE = "src/main/resources/xmloutput-test.xml";
	private static final String OUTPUT_FILE = "xmloutput/xmloutput.xml";
	
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    
    @After
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }
    
    @Test
    public void testJob(@Autowired Job job) throws Exception {
        this.jobLauncherTestUtils.setJob(job);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    	assertTrue("The files differ!", FileUtils.contentEquals(new FileSystemResource(EXPECTED_FILE).getFile(), new FileSystemResource(OUTPUT_FILE).getFile()));
    }
    
    @Test
    public void testJobStep1() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("step1");
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
    
	@Test
    public void testJobStep2() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("step2");
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
    
}
