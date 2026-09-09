package com.ead.notification.adapters.configs;

import com.ead.notification.NotificationApplication;
import com.ead.notification.core.ports.NotificationPersistencePort;
import com.ead.notification.core.services.NotificationServicePortImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = NotificationApplication.class)
public class BeanConfiguration {

    @Bean
    NotificationServicePortImpl notificationServicePortImpl(NotificationPersistencePort persistence) {
        return new NotificationServicePortImpl(persistence);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
