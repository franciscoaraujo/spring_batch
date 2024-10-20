package br.com.bitewisebytes.batchhello;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Se o Job for executado no momento da inicialização da aplicação, o método runJob() será chamado.
     * O Job roda apenas uma vez, pois o método runJob() é chamado apenas uma vez.
     * Se o Job for executado em outro momento, o método runJob() não será chamado.
     * Tem tomar cuidado com isso porque se nao o Job nao vai conseguir ser reinicializados
     * @throws Exception
     */

    @PostConstruct
    public void runJob() throws Exception {
        JobExecution execution = jobLauncher
                .run(imprimeHelloJob, new JobParametersBuilder()
                        .addString("names", "Bitewise Bytes")
                        .toJobParameters());
        log.info("Job Execution: " + execution.getStatus());
    }

    @Bean
    public Job imprimeHelloJob() {
        return new JobBuilder("imprimeHelloJob", jobRepository)
                .start(imprimeHelloStep())
                .build();
    }

    @Bean
    public Step imprimeHelloStep() {
        return new StepBuilder("imprimeHelloStep", jobRepository)
                .tasklet(imprimeTasklet(null))
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    @StepScope
    private static Tasklet imprimeTasklet(@Value("#{jobParameters['names']}") String names) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello, World! " + names);
                return RepeatStatus.FINISHED;
            }
        };
    }
}
