package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonIXmltemProcessor implements ItemProcessor<Person, PersonXml> {

	private static final Logger log = LoggerFactory.getLogger(PersonIXmltemProcessor.class);
	
    @Override
    public PersonXml process(Person item) throws Exception {
    	PersonXml xml = new PersonXml();
        xml.setFirstName(item.getFirstName());
        xml.setLastName(item.getLastName());
        
        log.info("Converting (" + item + ") into (" + xml + ")");
        
        
        return xml;
    }

}
