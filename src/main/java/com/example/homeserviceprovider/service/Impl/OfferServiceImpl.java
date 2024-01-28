package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;
import com.example.homeserviceprovider.repository.OfferRepository;
import com.example.homeserviceprovider.service.OfferService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.ACCEPTED;
import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.WAITING;


@Service
@Transactional
public class OfferServiceImpl extends BaseEntityServiceImpl<Offer, Long, OfferRepository>
        implements OfferService {


    public OfferServiceImpl(OfferRepository repository) {
        super(repository);
    }

    @Override
    public List<Offer> findOfferListByOrderIdBasedOnProposedPrice(Long orderId) {
        return repository.findOfferListByOrderIdBasedOnProposedPrice(orderId, WAITING );
    }

    @Override
    public List<Offer> findOfferListByOrderIdBasedOnSpecialistScore(Long orderId) {
        return repository.findOfferListByOrderIdBasedOnSpecialistScore(orderId,WAITING);
    }

    @Override
    public Optional<Offer> acceptedOffer(Long orderId) {
        return repository.findByOrderId(orderId, ACCEPTED );
    }

    @Override
    public List<Offer> findOffersBySpecialistIdAndOfferStatus(Long workerId, OfferStatus offerStatus) {
        return repository.findOffersBySpecialistIdAndOfferStatus(workerId, offerStatus);
    }
}
