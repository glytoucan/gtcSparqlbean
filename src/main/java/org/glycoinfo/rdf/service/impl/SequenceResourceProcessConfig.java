package org.glycoinfo.rdf.service.impl;

import org.glycoinfo.rdf.service.impl.GlycanProcedureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value=GlycanProcedureConfig.class)
public class SequenceResourceProcessConfig {
	public static Logger logger = (Logger) LoggerFactory
			.getLogger(SequenceResourceProcessConfig.class);
	
//	@Bean
//	SequenceResourceProcess sequenceResourceProcess() {
//		return new SequenceResourceProcess();
//	}
}