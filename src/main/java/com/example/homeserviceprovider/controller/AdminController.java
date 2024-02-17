package com.example.homeserviceprovider.controller;



import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.service.AdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/show-all-main-services")
    public List<MainServiceResponseDTO> findAllMainServices() {
        return adminService.findAllMainService();
    }

    @PostMapping("/add-main-service")
    public ResponseEntity<ProjectResponse> addMainService(
            @RequestBody MainServiceRequestDTO mainServiceRequestDTO) {
        return ResponseEntity.ok().body(adminService.createMainService(
                mainServiceRequestDTO));
    }

    @DeleteMapping("/delete-main-service/{name}")
    public ResponseEntity<ProjectResponse> deleteMainService(@PathVariable String name) {
        return ResponseEntity.ok().body(adminService.deleteMainService(name));
    }

    @GetMapping("/show-all-subServices")
    public List<SubServicesResponseDTO> findAllSubServices() {
        return adminService.findAllSubServices();
    }

    @GetMapping("/show-all-subServices-by-main-service/{mainServiceId}")
    public List<SubServicesResponseDTO> findAllSubServicesByMainService(@PathVariable Long mainServiceId) {
        return adminService.findAllSubServicessByMainService(mainServiceId);
    }

    @PostMapping("/add-subServices")
    public ResponseEntity<ProjectResponse> addSubServices(@RequestBody SubServicesRequestDTO subServicesRequestDTO) {
        return ResponseEntity.ok().body(adminService.addSubServices(subServicesRequestDTO));
    }

    @PutMapping("/edit-subServices-custom")
    public ResponseEntity<ProjectResponse> editSubServicesCustom(@RequestBody UpdateSubServicesDTO updateSubServicesDTO) {
        return ResponseEntity.ok().body(adminService.editSubServicesCustom(updateSubServicesDTO));
    }

    @PostMapping("/add-specialist-to-subServices/{subServicesId}/{specialistId}")
    public ResponseEntity<ProjectResponse> addSpecialistToSubServices(@PathVariable Long subServicesId, @PathVariable Long specialistId) {
        return ResponseEntity.ok().body(adminService.addSpecialistToSubServices(subServicesId, specialistId));
    }

    @DeleteMapping("/delete-subServices-form-specilaist/{subServiceId}/{specialistId}")
    public ResponseEntity<ProjectResponse> deleteSubServicesFromSpecialist(@PathVariable Long subServiceId, @PathVariable Long specialistId) {
        return ResponseEntity.ok().body(adminService.deleteSubServicesFromSpecialist(subServiceId, specialistId));
    }

    @PutMapping("/confirm-specialist/{specialistId}")
    public ResponseEntity<ProjectResponse> confirmSpecialist(@PathVariable Long specialistId) {
        return ResponseEntity.ok().body(adminService.confirmSpecialist(specialistId));
    }

    @PutMapping("/disable-specialist/{specialistId}")
    public ResponseEntity<ProjectResponse> disableSpecialist(@PathVariable Long specialistId) {
        return ResponseEntity.ok().body(adminService.deActiveSpecialist(specialistId));
    }

    @PostMapping("/filter-users")
    public List<FilterUserResponseDTO> userFilter(@RequestBody FilterUserDTO userDTO) {
        return adminService.userFilter(userDTO);
    }
    @PostMapping("/filter-order")
    public List<FilterOrderResponseDTO> orderFilter(@RequestBody FilterOrderDTO orderDTO) {
        return adminService.orderFilter(orderDTO);
    }



}
