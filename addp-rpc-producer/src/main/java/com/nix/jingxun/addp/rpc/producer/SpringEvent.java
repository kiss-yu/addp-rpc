package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.producer.event.ProducerBeanPostEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * create by keray
 * date:2019/7/16 10:29
 */
@Component
@Slf4j
public class SpringEvent implements ProducerEventProcessor {
    @Resource
    private ApplicationContext applicationContext;

    private final Set<ProducerBeanPostEvent> events = new LinkedHashSet<>(32);

    @Override
    public void beanPostEvent(ProducerBeanPostEvent producerBeanPostEvent) {
        events.add(producerBeanPostEvent);
    }

    @Override
    public void contextStartEvent(ApplicationStartedEvent startedEvent) {
        for (ProducerBeanPostEvent event:events) {
            registeredBean(event);
            log.info("{}注册完成",event);
        }
        log.info("服务接口注册完成");
    }

    private void registeredBean(ProducerBeanPostEvent event) {
        Object bean = applicationContext.getBean(event.getBeanName());
        try {
            RPCProducer.registerProducer(event.getClazz(), bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
