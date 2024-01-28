package com.example.homeserviceprovider.service;

import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.service.base.BaseUsersService;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface SpecialistService extends BaseUsersService<Specialist> {
    @Override
    void save(Specialist worker);

    @Override
    void delete(Specialist worker);

    @Override
    Optional<Specialist> findById(Long aLong);

    @Override
    List<Specialist> findAll();

    @Override
    Optional<Specialist> findByUsername(String email);

    void editPassword(String newPassword, String confirmNewPassword,Long specialistId);

    void addNewSpecialist(Specialist specialist,String filePath) throws IOException;

    List<MainServices> showAllMainServices();

    List<SubServices> showSubServices(MainServices mainServices);

    List<Order> showRelatedOrders(Long specialistId);

    void submitAnOffer(Offer offer,Long specialistId);


    List<Offer> showAllOffersWaiting(Long specialistId);

    List<Offer> showAllOffersAccepted(Long specialistId);

    List<Offer> showAllOffersRejected(Long specialistId);

    Long getSpecialistCredit(Long specialistId);


}
