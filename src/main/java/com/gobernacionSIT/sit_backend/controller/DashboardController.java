package com.gobernacionSIT.sit_backend.controller;

import com.gobernacionSIT.sit_backend.dto.response.DashboardResponse;
import com.gobernacionSIT.sit_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor

public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('SUPERVISOR')")
    @GetMapping
    public DashboardResponse resumen() {
        return dashboardService.obtenerResumen();
    }
}
