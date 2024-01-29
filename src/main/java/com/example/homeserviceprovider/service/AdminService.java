package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Admin;

import com.example.homeserviceprovider.service.base.BaseUsersService;
import org.springframework.stereotype.Service;


import java.util.List;

public interface AdminService extends BaseUsersService<Admin> {

    @Override
    void save(Admin admin);

    @Override
    List<Admin> findAll();

    void createMainService(MainServices mainServices);

    void deleteMainService(String name);

    void addSubServices(SubServices subServices);

    void deleteSubServices(String name);

    void addSpecialistToSubServices(Long subServicesId, Long specialistId);

    void deleteSubServicesFromSpecialist(Long subServicesId, Long specialistId);

    List<MainServices> findAllMainService();

    List<SubServices> findAllSubServices();

    void editSubServicesCustom(SubServices subServices);


    void confirmSpecialist(Long specialistId);

    List<SubServices> findAllSubServicesByMainService(Long mainServiceId);


    void addNewAdmin(Admin admin);


}
