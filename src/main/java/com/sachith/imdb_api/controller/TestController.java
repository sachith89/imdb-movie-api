package com.sachith.imdb_api.controller;

import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping
    public String runBatch(){
//        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
//        paramsBuilder.addString("file.input", input);
//        paramsBuilder.addString("file.output", output);
//        jobLauncher.run(transformBooksRecordsJob, paramsBuilder.toJobParameters());
        return "Hello";
    }
}
