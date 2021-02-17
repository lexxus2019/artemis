package ru.krista.fm.artemisserver;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.TopicConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.TopicConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisNoOpBindingRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ArtemisConfig implements ArtemisConfigurationCustomizer {

    @Qualifier("jmsConnectionFactory")
    @Autowired
    private ConnectionFactory connectionFactory;

    @Value("${jms.queue}")
    String jmsQueue;

    @Override
    public void customize(org.apache.activemq.artemis.core.config.Configuration configuration) {
        configuration.addConnectorConfiguration("nettyConnector", new TransportConfiguration(NettyConnectorFactory.class.getName()));
        configuration.addAcceptorConfiguration(new TransportConfiguration(NettyAcceptorFactory.class.getName()));


        ////configuration.setSecurityEnabled(true);
    }

    /*@Bean
    public DefaultMessageListenerContainer messageListener() {
        var container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(this.connectionFactory);
        container.setDestinationName(jmsQueue);
        return container;
    }*/

    @Bean
    @ConditionalOnMissingBean
    public JMSConfiguration artemisJmsConfiguration(
            ObjectProvider<JMSQueueConfiguration> queuesConfiguration,
            ObjectProvider<TopicConfiguration> topicsConfiguration) {
        JMSConfiguration configuration = new JMSConfigurationImpl();
        addAll(configuration.getQueueConfigurations(), queuesConfiguration);
        addAll(configuration.getTopicConfigurations(), topicsConfiguration);
        addQueues(configuration, new String[] { "que1", "que2" });
        addTopics(configuration, new String[] { "top1", "top2" });
        return configuration;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public EmbeddedActiveMQ artemisServer(
            org.apache.activemq.artemis.core.config.Configuration configuration,
            JMSConfiguration jmsConfiguration,
            ObjectProvider<ArtemisConfigurationCustomizer> configurationCustomizers) {

        var securityManagement = new JAASSecurityManagerWrapper("qqq");
/////        server.setSecurityManager(securityManagement);
        ////(ActiveMQJAASSecurityManager)server.getActiveMQServer().getSecurityManager();
        var secConfig = securityManagement.getConfiguration();

        var userName1 = "test";
        secConfig.addUser(userName1, "123");
        secConfig.addRole(userName1, "auditsender");

        EmbeddedActiveMQ server = new EmbeddedActiveMQ();
        server.setSecurityManager(securityManagement);

        configuration.setSecurityEnabled(true);

        var auditSenderRole = new Role("auditsender", true, false, true, true, true, true, true, true, true, true);
        var auditReciverRole = new Role("auditreceiver", false, true, false, false, false, false, false, true, false, false);

        var rolse = new HashSet<Role>();
        rolse.add(auditReciverRole);
        rolse.add(auditSenderRole);
        configuration.putSecurityRoles("#",  rolse);

        configurationCustomizers.orderedStream()
                .forEach((customizer) -> customizer.customize(configuration));
        server.setConfiguration(configuration);


        /*server.setJmsConfiguration(jmsConfiguration);
        server.setRegistry(new ArtemisNoOpBindingRegistry());*/
        return server;
    }

    private <T> void addAll(List<T> list, ObjectProvider<T> items) {
        if (items != null) {
            list.addAll(items.orderedStream().collect(Collectors.toList()));
        }
    }

    private void addQueues(JMSConfiguration configuration, String[] queues) {
        boolean persistent = true; // ??????????????
        for (String queue : queues) {
            JMSQueueConfigurationImpl jmsQueueConfiguration = new JMSQueueConfigurationImpl();
            jmsQueueConfiguration.setName(queue);
            jmsQueueConfiguration.setDurable(persistent);
            jmsQueueConfiguration.setBindings("/queue/" + queue);
            configuration.getQueueConfigurations().add(jmsQueueConfiguration);
        }
    }

    private void addTopics(JMSConfiguration configuration, String[] topics) {
        for (String topic : topics) {
            TopicConfigurationImpl topicConfiguration = new TopicConfigurationImpl();
            topicConfiguration.setName(topic);
            topicConfiguration.setBindings("/topic/" + topic);
            configuration.getTopicConfigurations().add(topicConfiguration);
        }
    }


 /*   @Qualifier("jmsConnectionFactory")
    @Autowired
    private ConnectionFactory connectionFactory;

    @Value("${jms.queue}")
    String jmsQueue;

    public DefaultMessageListenerContainer messageListener() {
        var container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(this.connectionFactory);
        container.setDestinationName(jmsQueue);
        return container;
    }*/
}
