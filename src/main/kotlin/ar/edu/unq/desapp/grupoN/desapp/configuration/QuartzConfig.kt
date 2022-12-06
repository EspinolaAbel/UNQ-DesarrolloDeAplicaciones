package ar.edu.unq.desapp.grupoN.desapp.configuration

import ar.edu.unq.desapp.grupoN.desapp.service.job.CacheUpdaterJob
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory


@Configuration
class QuartzConfig(
    @Value("\${app.jobs.cache.interval}")
    val interval: Long,
    val applicationContext: ApplicationContext
    ) {

    @Bean
    fun createSimpleTriggerFactoryBean(jobDetail: JobDetail): SimpleTriggerFactoryBean {
        val simpleTriggerFactory = SimpleTriggerFactoryBean()
        simpleTriggerFactory.setJobDetail(jobDetail)
        simpleTriggerFactory.setStartDelay(0)
        simpleTriggerFactory.setRepeatInterval(interval)
        //simpleTriggerFactory.setRepeatCount(10)
        return simpleTriggerFactory
    }

    @Bean
    fun createJobDetailFactoryBean(): JobDetailFactoryBean {
        val jobDetailFactory = JobDetailFactoryBean()
        jobDetailFactory.setJobClass(CacheUpdaterJob::class.java)
        return jobDetailFactory
    }

    @Bean
    fun createSpringBeanJobFactory(): SpringBeanJobFactory? {
        return object : SpringBeanJobFactory() {
            @Throws(Exception::class)
            override fun createJobInstance(bundle: TriggerFiredBundle): Any {
                val job = super.createJobInstance(bundle)
                applicationContext
                    .autowireCapableBeanFactory
                    .autowireBean(job)
                return job
            }
        }
    }

    @Bean
    fun createSchedulerFactory(springBeanJobFactory: SpringBeanJobFactory, trigger: Trigger): SchedulerFactoryBean? {
        val schedulerFactory = SchedulerFactoryBean()
        schedulerFactory.isAutoStartup = true
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true)
        schedulerFactory.setTriggers(trigger)
        springBeanJobFactory.setApplicationContext(applicationContext)
        schedulerFactory.setJobFactory(springBeanJobFactory)
        return schedulerFactory
    }
}
