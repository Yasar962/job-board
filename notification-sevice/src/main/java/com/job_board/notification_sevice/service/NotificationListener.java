package com.job_board.notification_sevice.service;

import com.job_board.notification_sevice.config.RabbitMQConfig;
import com.job_board.notification_sevice.entity.ApplicationCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {
    @Autowired
    private JavaMailSender mailSender;
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleApplicationCreated(ApplicationCreatedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getCandidateEmail());
        message.setSubject("Application Received: " + event.getJobTitle());
        message.setText(
                "Hi,\n\nYour application for '" + event.getJobTitle() +
                        "' at " + event.getCompanyName() + " has been received.\n\n" +
                        "We will review it and get back to you.\n\nBest regards,\nJob Board Team"
        );
        mailSender.send(message);
        System.out.println("Email sent to: " + event.getCandidateEmail());
    }
}