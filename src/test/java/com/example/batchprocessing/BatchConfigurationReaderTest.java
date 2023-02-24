/**
 * 
 */
package com.example.batchprocessing;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author mjedli
 *
 */
@SpringBatchTest
@SpringBootTest
@SpringJUnitConfig
@RunWith(SpringRunner.class)
public class BatchConfigurationReaderTest {

    // This component is defined step-scoped, so it cannot be injected unless
    // a step is active...
    @Autowired
    private FlatFileItemReader<Person> reader;

    public StepExecution getStepExecution() {
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.getExecutionContext().put(new ClassPathResource("sample-data.xml").getFilename(), new String[]{"firstName", "lastName"});
        return execution;
    }

    @Test
    public void testReader() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
        // The reader is initialized and bound to the input data
    	reader.open(new ExecutionContext());
        assertNotNull(reader.read());
        reader.close();
    }
	
    @Test
    public void givenMockedStep_whenReaderCalled_thenSuccess() throws Exception {
        // when
        StepScopeTestUtils.doInStepScope(getStepExecution(), () -> {
            Person person;
            reader.open(new ExecutionContext());
            if ((person = reader.read()) != null) {

                // then
            	Assert.assertEquals(person.getFirstName(), "Jill");
            	Assert.assertEquals(person.getLastName(), "Doe");
            }
            reader.close();
            return null;
        });
    }    
    
}
