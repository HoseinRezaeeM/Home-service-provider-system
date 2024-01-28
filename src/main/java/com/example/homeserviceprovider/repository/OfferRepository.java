package com.example.homeserviceprovider.repository;


import com.example.homeserviceprovider.base.repository.BaseEntityRepository;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends BaseEntityRepository<Offer, Long> {

    @Query(" select o from Offer o where o.order.id = :orderId and o.offerStatus= :offerStatus order by o.proposedPrice asc")
    List<Offer> findOfferListByOrderIdBasedOnProposedPrice(Long orderId, OfferStatus offerStatus);

    @Query(" select o from Offer o where o.order.id = :orderId and o.offerStatus= :offerStatus order by o.specialist.score desc")
    List<Offer> findOfferListByOrderIdBasedOnSpecialistScore(Long orderId, OfferStatus offerStatus);

    @Query("select o from Offer o where o.specialist.id = :specialistId and o.offerStatus = :offerStatus")
    List<Offer> findOffersBySpecialistIdAndOfferStatus(Long specialistId, OfferStatus offerStatus);

    @Query("select o from Offer o where o.order.id = :orderId and o.offerStatus = :offerStatus")
    Optional<Offer> findByOrderId(Long orderId, OfferStatus offerStatus);


}
