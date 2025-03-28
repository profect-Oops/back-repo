package org.oops.api.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CpuStressController {

    @GetMapping("/cpu-stress")
    public String cpuStress() {
        long startTime = System.currentTimeMillis();
        long duration = 3000; // 3초간 CPU 부하

        // CPU 부하 유도: 3초 동안 busy loop
        while (System.currentTimeMillis() - startTime < duration) {
            double dummy = Math.sqrt(Math.random()) * Math.random();
        }

        return "CPU stress for " + duration + "ms completed.";
    }
}
