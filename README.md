# Glytoucan Sparqlbeans
batch processing for the glytoucan project.  This utilizes the [spring batch framework](http://projects.spring.io/spring-batch/) for ETL processing of RDF.

# Background
Recommended to at least read through the spring batch quick start tutorial.  A concept called SparqlBeans are also used which are simply classes that store a sparql string, with other utilities provided as a layer between the application logic and the RDF triplestore.  By recording (or "objectifying") the SPARQL it is easier to see other examples of related RDF classes and possibly re-use/inherit for other means.  The SPARQLs used in this package are specific for the [glytoucan](http://glytoucan.org) project.

The separation of sparql from object mapping methods is to prevent issues that may arise from the third-party mapping methods.  This provides flexibility and freedom to use specialized sparql even though it is a standardized language.  The framework should not cause roadblocks between what the application developer needs versus the data store.  It should also make full use of sparql standardization by having the ability to completely change the underlying RDF data store vendor.

# What is it?
The SparqlItemReader and SparqlItemWriter are prepared to provide reading and writing to a triplestore using the sparql language.

By providing a SparqlBean select sparql to the ItemReader, it iterates via OFFSETs and LIMITs in order to efficiently process results.

Usually the SparqlItemReader is combined with a SparqlItemWriter, which can then be SparqlItemProcessed together to Read, Process, and then Write into the triplestore.

An example can be seen in the WurcsConvertSparqlBatch process.  
This is a Spring java-configured class which SELECT queries for strings from the RDF, runs a process to convert them, and then INSERTs the results back into the RDF:
```java

// configure the conversion process to use.
// Since there are a variety of conversion methods, a GlyConvert interface is used.
	@Bean
	GlyConvert getGlyConvert() {
		return new GlycoctToWurcsConverter();
	}

// configure the sparqlbeans, which contain the read and write sparql text.  
// Notice how easy it is to modify the initialization of a sparql string.
	@Bean
	SelectSparql getSelectSparql() {
		SelectSparql select = new WurcsConvertSelectSparql();
		select.setFrom("FROM <http://rdf.glytoucan.org> FROM <http://rdf.glytoucan.org/sequence/wurcs>");
		return select;
	}

	@Bean
	InsertSparql getInsertSparql() {
		ConvertInsertSparql convert = new ConvertInsertSparql();
		convert.setGraphBase(graphbase);
		return convert;
	}

// the sparql item reader is configured, specifying page size and the select.
	@Bean
	public ItemReader<SparqlEntity> reader() {
		SparqlItemReader<SparqlEntity> reader = new SparqlItemReader<SparqlEntity>();
		reader.setSelectSparql(getSelectSparql());
		reader.setPageSize(pageSize);
		return reader;
	}

// the item writer is also configured as normal.  
	@Bean
	public ItemWriter<SparqlEntity> writer() {
		SparqlItemWriter<SparqlEntity> reader = new SparqlItemWriter<SparqlEntity>();
		reader.setInsertSparql(getInsertSparql());
		return reader;
	}

// finally, a specific processor can be passed in order to execute the conversion for each item read.
	@Bean
	public ItemProcessor<SparqlEntity, SparqlEntity> processor() {
		ConvertSparqlProcessor process = new ConvertSparqlProcessor();
		process.setGlyConvert(getGlyConvert());

		return process;
	}

```

# Credit
This development is funded by the Integrated Database Project by MEXT (Ministry of Education, Culture, Sports, Science & Technology) 
and the Program for Coordination Toward Integration of Related Databases by JST (Japan Science and Technology Agency) as part of the [International Glycan Repository project](http://www.glytoucan.org).

