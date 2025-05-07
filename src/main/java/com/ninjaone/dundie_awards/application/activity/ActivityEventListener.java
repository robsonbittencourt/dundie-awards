package com.ninjaone.dundie_awards.application.activity;

import com.ninjaone.dundie_awards.infrastructure.repository.activity.Activity;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.ActivityRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ActivityEventListener implements ApplicationListener<EmployeeEvent> {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityProducer activityProducer;

    @Override
    public void onApplicationEvent(EmployeeEvent event) {
        Activity activity = new Activity(event.getOccuredAt(), event.getEvent());
        activityRepository.save(activity);

        activityProducer.send(activity);
    }
}
