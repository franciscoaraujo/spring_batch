package br.com.bitewisebytes.batchhello;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Log4j2
@EnableBatchProcessing
@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job imprimeHelloJob;



    @PostConstruct
    public void runJob() throws Exception {
        log.info("Iniciando Job...........");
        JobExecution execution = jobLauncher.run(imprimeHelloJob, new JobParameters());
        System.out.println("Job Status: " + execution.getStatus());
    }

    @Bean
    public Job imprimeHelloJob() {
        log.info("JobBuilder Job...........");
        return new JobBuilder("imprimeHelloJob", jobRepository)
                .start(imprimeHelloStep())
                .build();
    }

    @Bean
    public Step imprimeHelloStep() {
        log.info("StepBuilder Step...........");
        return new StepBuilder("imprimeHelloStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Hello, World!");

                    log.info("Hello, World!...........");

                    return RepeatStatus.FINISHED;
                })
                .transactionManager(transactionManager)
                .build();
    }

}
