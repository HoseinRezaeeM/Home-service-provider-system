package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.exception.*;
import com.example.homeserviceprovider.mapper.MainServiceMapper;
import com.example.homeserviceprovider.mapper.OfferMapper;
import com.example.homeserviceprovider.mapper.SpecialistMapper;
import com.example.homeserviceprovider.repository.SpecialistRepository;
import com.example.homeserviceprovider.security.token.entity.Token;
import com.example.homeserviceprovider.security.token.service.TokenService;
import com.example.homeserviceprovider.service.*;


import com.example.homeserviceprovider.util.Validation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class SpecialistServiceImpl extends BaseEntityServiceImpl<Specialist, Long, SpecialistRepository>
       implements SpecialistService {

      private final OfferService offerService;
      private final OrderService orderService;
      private final MainServiceService mainService;
      private final SubServicesService jobService;

      private final SpecialistMapper workerMapper;
      private final MainServiceMapper mainServiceMapper;
      private final OfferMapper offerMapper;

      private final Validation validation;
      private final TokenService tokenService;
      private final EmailService emailService;
      private final PasswordEncoder passwordEncoder;

      @PersistenceContext
      private EntityManager entityManager;

      public SpecialistServiceImpl(SpecialistRepository repository, OfferService offerService,
                               OrderService orderService, MainServiceService mainService,
                               SubServicesService jobService, SpecialistMapper workerMapper,
                               MainServiceMapper mainServiceMapper, OfferMapper offerMapper,
                               Validation validation, TokenService tokenService,
                               EmailService emailService, PasswordEncoder passwordEncoder,
                               EntityManager entityManager) {
            super(repository);
            this.offerService = offerService;
            this.orderService = orderService;
            this.mainService = mainService;
            this.jobService = jobService;
            this.workerMapper = workerMapper;
            this.mainServiceMapper = mainServiceMapper;
            this.offerMapper = offerMapper;
            this.validation = validation;
            this.tokenService = tokenService;
            this.emailService = emailService;
            this.passwordEncoder = passwordEncoder;
            this.entityManager = entityManager;
      }

      @Override
      public String addNewSpecialist(SpecialistRegistrationDTO workerRegistrationDTO) throws IOException {
            validation.checkEmail(workerRegistrationDTO.getEmail());
            if (repository.findByEmail(workerRegistrationDTO.getEmail()).isPresent())
                  throw new DuplicateEmailException("this Email already exist!");
            validation.checkPassword(workerRegistrationDTO.getPassword());
            validation.checkText(workerRegistrationDTO.getFirstname());
            validation.checkText(workerRegistrationDTO.getLastname());
            validation.checkImage(workerRegistrationDTO.getFile());
            Specialist worker = workerMapper.convertToNewSpecialist(workerRegistrationDTO);
            repository.save(worker);
            String newToken = UUID.randomUUID().toString();
            Token token = new Token(LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), worker);
            token.setToken(newToken);
            tokenService.saveToken(token);
            SimpleMailMessage mailMessage =
                   emailService.createEmail(worker.getEmail(), worker.getFirstname(), token.getToken(), worker.getRole());
            emailService.sendEmail(mailMessage);
            return newToken;
      }


      @Override
      @Transactional
      public ProjectResponse editPassword(ChangePasswordDTO changePasswordDTO, Long workerId) {
            validation.checkPassword(changePasswordDTO.getNewPassword());
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword()))
                  throw new DuplicatePasswordException("this confirmNewPassword not match with newPassword!");
            repository.editPassword(workerId, passwordEncoder.encode(changePasswordDTO.getConfirmNewPassword()));
            return new ProjectResponse("200", "CHANGE PASSWORD SUCCESSFULLY");
      }

      @Override
      @Transactional(readOnly = true)
      public List<MainServiceResponseDTO> showAllMainServices() {
            List<MainServices> mainServices = mainService.findAll();
            List<MainServiceResponseDTO> msDTOS = new ArrayList<>();
            if (!mainServices.isEmpty())
                  mainServices.forEach(ms -> msDTOS.add(mainServiceMapper.convertToDTO(ms)));
            return msDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServicesResponseDTO> showSubServices(ChooseMainServiceDTO dto) {
            Optional<MainServices> dbMainService = Optional.empty();
            if (!dto.getMainServiceName().isBlank()) {
                  validation.checkText(dto.getMainServiceName());
                  dbMainService = mainService.findByName(dto.getMainServiceName());
            } else if (!dto.getMainServiceId().toString().isBlank()) {
                  validation.checkPositiveNumber(dto.getMainServiceId());
                  dbMainService = mainService.findById(dto.getMainServiceId());
            }
            if (dbMainService.isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            return jobService.findByMainServiceId(dbMainService.get().getId());
      }

      @Override
      @Transactional(readOnly = true)
      public List<LimitedOrderResponseDTO> showRelatedOrders(Long workerId) {
            Optional<Specialist> specialist = repository.findById(workerId);
            if (specialist.get().getSubServicesList().isEmpty())
                  throw new SpecialistNoAccessException("you do not have a job title!");
            List<LimitedOrderResponseDTO> lorDTOS = new ArrayList<>();
            specialist.get().getSubServicesList().forEach(job ->
                   lorDTOS.addAll(orderService.findAllOrdersBySubServicesNameAndProvince(
                          job.getName(), specialist.get().getProvince())));
            return lorDTOS;
      }

      @Override
      public ProjectResponse submitAnOffer(OfferRequestDTO offerRequestDTO, Long workerId) {
            validation.checkPositiveNumber(offerRequestDTO.getOrderId());
            validation.checkPositiveNumber(offerRequestDTO.getOfferProposedPrice());
            Optional<Specialist> specialist = repository.findById(workerId);
            if (!(specialist.get().getIsActive()))
                  throw new SpecialistNoAccessException("this specialist is inActive");
            if (!(specialist.get().getStatus().equals(SpecialistStatus.CONFIRMED)))
                  throw new SpecialistNoAccessException("the status of specialist is not CONFIRMED");
            Optional<Order> order = orderService.findById(offerRequestDTO.getOrderId());
            if (order.isEmpty())
                  throw new OrderIsNotExistException("this order does not exist!");
            OrderStatus orderStatus = order.get().getOrderStatus();
            if (!(orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION) ||
                  orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION)))
                  throw new OrderStatusException("!this order has already accepted the offer");
            if (!(specialist.get().getSubServicesList().contains(order.get().getSubServices())))
                  throw new SpecialistNoAccessException("this specialist does not have such job title!");
            if (offerRequestDTO.getProposedEndDate().isBefore(offerRequestDTO.getProposedStartDate()))
                  throw new TimeException("time does not go back!");
            if (offerRequestDTO.getProposedStartDate().isBefore(order.get().getExecutionTime()))
                  throw new TimeException("no order has been in your proposed time for begin job!");
            if (order.get().getProposedPrice() > offerRequestDTO.getOfferProposedPrice())
                  throw new AmountLessExseption("the proposed-price should not be lower than the order proposed-price!");
            Offer offer = offerMapper.convertToNewOffer(offerRequestDTO);
            offer.setSpecialist(specialist.get());
            offer.setOrder(order.get());
            offerService.save(offer);
            if (orderStatus.equals(OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION))
                  order.get().setOrderStatus(OrderStatus.WAITING_FOR_SPECIALIST_SELECTION);
            orderService.save(order.get());
            return new ProjectResponse("200", "ADDED OFFER SUCCESSFUL");
      }

      @Override
      @Transactional(readOnly = true)
      public double getSpecialistRate(Long workerId) {
            validation.checkPositiveNumber(workerId);
            Optional<Specialist> worker = repository.findById(workerId);
            if (worker.isEmpty())
                  throw new SpecialistIsNotExistException("this worker does not exist!");
            return worker.get().getScore();
      }

      @Override
      @Transactional(readOnly = true)
      public Long getSpecialistCredit(Long workerId) {
            validation.checkPositiveNumber(workerId);
            Optional<Specialist> worker = repository.findById(workerId);
            if (worker.isEmpty())
                  throw new SpecialistIsNotExistException("this worker does not exist!");
            return worker.get().getCredit();
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> showAllOffersWaiting(Long workerId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(workerId, OfferStatus.WAITING);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            offers.forEach(o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> showAllOffersAccepted(Long workerId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(workerId, OfferStatus.ACCEPTED);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            offers.forEach(o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> showAllOffersRejected(Long workerId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(workerId, OfferStatus.REJECTED);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            offers.forEach(o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<Specialist> findAll() {
            List<Specialist> workerList = repository.findAll();
            if (workerList.isEmpty())
                  throw new SpecialistIsNotExistException("there are no workers!");
            return workerList;
      }

      @Override
      public Optional<Specialist> findByUsername(String email) {
            return Optional.empty();
      }

      @Override
      public List<FilterUserResponseDTO> allSpecialist(FilterUserDTO userDTO) {
            List<FilterUserResponseDTO> furDList = new ArrayList<>();
            List<Specialist> workerList = repository.findAll();
            if (!workerList.isEmpty())
                  workerList.forEach(c ->
                         furDList.add(workerMapper.convertToFilterDTO(c)));
            return furDList;
      }

      @Override
      @Transactional(readOnly = true)
      public List<FilterUserResponseDTO> specialistFilter(FilterUserDTO workerDTO) {
            List<Predicate> predicateList = new ArrayList<>();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Specialist> workerCriteriaQuery = criteriaBuilder.createQuery(Specialist.class);
            Root<Specialist> workerRoot = workerCriteriaQuery.from(Specialist.class);
            createFilters(workerDTO, predicateList, criteriaBuilder, workerRoot);
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);
            workerCriteriaQuery.select(workerRoot).where(predicates);
            List<Specialist> resultList = entityManager.createQuery(workerCriteriaQuery).getResultList();
            List<FilterUserResponseDTO> fuDTOS = new ArrayList<>();
            resultList.forEach(rl -> fuDTOS.add(workerMapper.convertToFilterDTO(rl)));
            return fuDTOS;
      }

      private void createFilters(FilterUserDTO workerDTO, List<Predicate> predicateList,
                                 CriteriaBuilder criteriaBuilder, Root<Specialist> workerRoot) {

            if (workerDTO.getFirstname() != null) {
                  String firstname = "%" + workerDTO.getFirstname() + "%";
                  predicateList.add(criteriaBuilder.like(workerRoot.get("firstname"), firstname));
            }
            if (workerDTO.getLastname() != null) {
                  String lastname = "%" + workerDTO.getLastname() + "%";
                  predicateList.add(criteriaBuilder.like(workerRoot.get("lastname"), lastname));
            }
            if (workerDTO.getUsername() != null) {
                  String email = "%" + workerDTO.getUsername() + "%";
                  predicateList.add(criteriaBuilder.like(workerRoot.get("email"), email));
            }

            if (workerDTO.getIsActive() != null)
                  if (workerDTO.getIsActive())
                        predicateList.add(criteriaBuilder.isTrue(workerRoot.get("isActive")));
                  else
                        predicateList.add(criteriaBuilder.isFalse(workerRoot.get("isActive")));

            if (workerDTO.getUserStatus() != null)
                  predicateList.add(criteriaBuilder.equal(workerRoot.get("status"),
                         workerDTO.getUserStatus().toString()));

            if (workerDTO.getMinCredit() == null && workerDTO.getMaxCredit() != null)
                  workerDTO.setMinCredit(0L);
            if (workerDTO.getMinCredit() != null && workerDTO.getMaxCredit() == null)
                  workerDTO.setMaxCredit(Long.MAX_VALUE);
            if (workerDTO.getMinCredit() != null && workerDTO.getMaxCredit() != null)
                  predicateList.add(criteriaBuilder.between(workerRoot.get("credit"),
                         workerDTO.getMinCredit(), workerDTO.getMaxCredit()));

            if (workerDTO.getMinScore() == null && workerDTO.getMaxScore() != null)
                  workerDTO.setMinScore(0.0);
            if (workerDTO.getMinScore() != null && workerDTO.getMaxScore() == null)
                  workerDTO.setMaxScore(5.0);
            if (workerDTO.getMinScore() != null && workerDTO.getMaxScore() != null)
                  predicateList.add(criteriaBuilder.between(workerRoot.get("score"),
                         workerDTO.getMinScore(), workerDTO.getMaxScore()));

            if (workerDTO.getMinUserCreationAt() == null && workerDTO.getMaxUserCreationAt() != null)
                  workerDTO.setMinUserCreationAt(LocalDateTime.now().minusYears(2));
            if (workerDTO.getMinUserCreationAt() != null && workerDTO.getMaxUserCreationAt() == null)
                  workerDTO.setMaxUserCreationAt(LocalDateTime.now());
            if (workerDTO.getMinUserCreationAt() != null && workerDTO.getMaxUserCreationAt() != null)
                  predicateList.add(criteriaBuilder.between(workerRoot.get("registrationTime"),
                         workerDTO.getMinUserCreationAt(), workerDTO.getMaxUserCreationAt()));

            if (workerDTO.getMinNumberOfOperation() == null && workerDTO.getMaxNumberOfOperation() != null)
                  workerDTO.setMinNumberOfOperation(0);
            if (workerDTO.getMinNumberOfOperation() != null && workerDTO.getMaxNumberOfOperation() == null)
                  workerDTO.setMaxNumberOfOperation(Integer.MAX_VALUE);
            if (workerDTO.getMinNumberOfOperation() != null && workerDTO.getMaxNumberOfOperation() != null)
                  predicateList.add(criteriaBuilder.between(workerRoot.get("numberOfOperation"),
                         workerDTO.getMinNumberOfOperation(), workerDTO.getMaxNumberOfOperation()));

            if (workerDTO.getMinNumberOfDoneOperation() == null && workerDTO.getMaxNumberOfDoneOperation() != null)
                  workerDTO.setMinNumberOfDoneOperation(0);
            if (workerDTO.getMinNumberOfDoneOperation() != null && workerDTO.getMaxNumberOfDoneOperation() == null)
                  workerDTO.setMaxNumberOfDoneOperation(Integer.MAX_VALUE);
            if (workerDTO.getMinNumberOfDoneOperation() != null && workerDTO.getMaxNumberOfDoneOperation() != null)
                  predicateList.add(criteriaBuilder.between(workerRoot.get("rateCounter"),
                         workerDTO.getMinNumberOfDoneOperation(), workerDTO.getMaxNumberOfDoneOperation()));

      }



}
