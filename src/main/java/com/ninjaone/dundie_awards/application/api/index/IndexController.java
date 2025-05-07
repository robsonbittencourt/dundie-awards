package com.ninjaone.dundie_awards.application.api.index;

import com.ninjaone.dundie_awards.infrastructure.cache.AwardsRedisCache;
import com.ninjaone.dundie_awards.infrastructure.repository.activity.ActivityRepository;
import com.ninjaone.dundie_awards.infrastructure.repository.employee.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AwardsRedisCache awardsRedisCache;

    @GetMapping()
    public String getIndex(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("activities", activityRepository.findAll());
        model.addAttribute("totalDundieAwards", awardsRedisCache.getCounter());
        return "index";
    }
}