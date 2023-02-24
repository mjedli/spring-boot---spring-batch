/**
 * 
 */
package com.example.batchprocessing;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author mjedli
 *
 */
@SpringBatchTest
@SpringJUnitConfig
public class BatchConfigurationReaderTest {

    // This component is defined step-scoped, so it cannot be injected unless
    // a step is active...
    @Autowired
    private FlatFileItemReader<Person> reader;

    public StepExecution getStepExecution() {
        StepExecution execution = MetaDataInstanceFactory.createStepExecution();
        execution.getExecutionContext().put("sample-data.csv", null);
        return execution;
    }

    @Test
    public void testReader() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
        // The reader is initialized and bound to the input data
        assertNotNull(reader.read());
    }
	
}
