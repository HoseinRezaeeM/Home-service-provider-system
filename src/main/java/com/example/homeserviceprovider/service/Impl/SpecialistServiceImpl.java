package com.example.homeserviceprovider.service.Impl;

import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.offer.enums.OfferStatus;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.MainServices;
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


import com.example.homeserviceprovider.util.SaveImageToFile;
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
import java.util.*;


@Service
@Transactional
public class SpecialistServiceImpl extends BaseEntityServiceImpl<Specialist, Long, SpecialistRepository>
       implements SpecialistService {

      private final OfferService offerService;
      private final OrderService orderService;
      private final MainServiceService mainService;
      private final SubServicesService subServicesService;

      private final SpecialistMapper specialistMapper;
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
                                   SubServicesService subServicesService, SpecialistMapper specialistMapper,
                                   MainServiceMapper mainServiceMapper, OfferMapper offerMapper,
                                   Validation validation, TokenService tokenService,
                                   EmailService emailService, PasswordEncoder passwordEncoder,
                                   EntityManager entityManager) {
            super(repository);
            this.offerService = offerService;
            this.orderService = orderService;
            this.mainService = mainService;
            this.subServicesService = subServicesService;
            this.specialistMapper = specialistMapper;
            this.mainServiceMapper = mainServiceMapper;
            this.offerMapper = offerMapper;
            this.validation = validation;
            this.tokenService = tokenService;
            this.emailService = emailService;
            this.passwordEncoder = passwordEncoder;
            this.entityManager = entityManager;
      }

      @Override
      public String addNewSpecialist(SpecialistRegistrationDTO specialistRegistrationDTO) throws IOException {
            validation.checkEmail(specialistRegistrationDTO.getEmail());
            if (repository.findByEmail(specialistRegistrationDTO.getEmail()).isPresent())
                  throw new DuplicateEmailException("this Email already exist!");
            validation.checkPassword(specialistRegistrationDTO.getPassword());
            validation.checkText(specialistRegistrationDTO.getFirstname());
            validation.checkText(specialistRegistrationDTO.getLastname());
            //validation.checkImage(specialistRegistrationDTO.getFile());
            Specialist specialist = specialistMapper.convertToNewSpecialist(specialistRegistrationDTO);
            repository.save(specialist);
            SaveImageToFile.saveImageToFile(specialist.getImage(),
                   "C:\\Users\\sp\\IdeaProjects\\home-service-provider\\src\\main\\resources\\imageUploaded\\"
                   + specialist.getFirstname()+""+specialist.getLastname() + ".jpg");
            String newToken = UUID.randomUUID().toString();
            Token token = new Token(LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), specialist);
            token.setToken(newToken);
            tokenService.saveToken(token);
            SimpleMailMessage mailMessage =
                   emailService.createEmail(specialist.getEmail(), specialist.getFirstname(), token.getToken(), specialist.getRole());
            emailService.sendEmail(mailMessage);
            return newToken;
      }


      @Override
      @Transactional
      public ProjectResponse editPassword(ChangePasswordDTO changePasswordDTO, Long specialistId) {
            validation.checkPassword(changePasswordDTO.getNewPassword());
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword()))
                  throw new DuplicatePasswordException("this confirmNewPassword not match with newPassword!");
            repository.editPassword(specialistId, passwordEncoder.encode(changePasswordDTO.getConfirmNewPassword()));
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
            return subServicesService.findByMainServiceId(dbMainService.get().getId());
      }

      @Override
      @Transactional(readOnly = true)
      public List<LimitedOrderResponseDTO> showRelatedOrders(Long specialistId) {
            Optional<Specialist> specialist = repository.findById(specialistId);
            if (specialist.get().getSubServicesList().isEmpty())
                  throw new SpecialistNoAccessException("you do not have a subServices title!");
            List<LimitedOrderResponseDTO> lorDTOS = new ArrayList<>();
            specialist.get().getSubServicesList().forEach(subServices ->
                   lorDTOS.addAll(orderService.findAllOrdersBySubServicesNameAndProvince(
                          subServices.getName(), specialist.get().getProvince())));
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
            specialist.get().setNumberOfOperation(specialist.get().getNumberOfOperation() + 1);
            orderService.save(order.get());
            return new ProjectResponse("200", "ADDED OFFER SUCCESSFUL");
      }

      @Override
      @Transactional(readOnly = true)
      public double getSpecialistRate(Long specialistId) {
            validation.checkPositiveNumber(specialistId);
            Optional<Specialist> specialist = repository.findById(specialistId);
            if (specialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            return specialist.get().getScore();
      }

      @Override
      @Transactional(readOnly = true)
      public Long getSpecialistCredit(Long specialistId) {
            validation.checkPositiveNumber(specialistId);
            Optional<Specialist> specialist = repository.findById(specialistId);
            if (specialist.isEmpty())
                  throw new SpecialistIsNotExistException("this specialist does not exist!");
            return specialist.get().getCredit();
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> showAllOffersWaiting(Long specialistId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(specialistId, OfferStatus.WAITING);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            offers.forEach(o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> showAllOffersAccepted(Long specialistId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(specialistId, OfferStatus.ACCEPTED);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            offers.forEach(o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> showAllOffersRejected(Long specialistId) {
            List<Offer> offers = offerService.findOffersBySpecialistIdAndOfferStatus(specialistId, OfferStatus.REJECTED);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            offers.forEach(o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<Specialist> findAll() {
            List<Specialist> specialistList = repository.findAll();
            if (specialistList.isEmpty())
                  throw new SpecialistIsNotExistException("there are no specialists!");
            return specialistList;
      }

      @Override
      public Optional<Specialist> findByUsername(String email) {
            return repository.findByEmail(email);
      }

      @Override
      public List<FilterUserResponseDTO> allSpecialist(FilterUserDTO userDTO) {
            List<FilterUserResponseDTO> furDList = new ArrayList<>();
            List<Specialist> list = repository.findAll();
            if (!list.isEmpty())
                  list.forEach(c ->
                         furDList.add(specialistMapper.convertToFilterDTO(c)));
            return furDList;
      }

      @Override
      @Transactional
      public List<FilterUserResponseDTO> specialistFilter(FilterUserDTO specialistDto) {
            List<Predicate> predicateList = new ArrayList<>();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Specialist> specialistCriteriaQuery = criteriaBuilder.createQuery(Specialist.class);
            Root<Specialist> specialistRoot = specialistCriteriaQuery.from(Specialist.class);
            if (specialistDto.getFirstname() != null) {
                  String firstname = "%" + specialistDto.getFirstname() + "%";
                  predicateList.add(criteriaBuilder.like(specialistRoot.get("firstname"), firstname));
            }
            if (specialistDto.getLastname() != null) {
                  String lastname = "%" + specialistDto.getLastname() + "%";
                  predicateList.add(criteriaBuilder.like(specialistRoot.get("lastname"), lastname));
            }
            if (specialistDto.getUsername() != null) {
                  String email = "%" + specialistDto.getUsername() + "%";
                  predicateList.add(criteriaBuilder.like(specialistRoot.get("email"), email));
            }
            if (specialistDto.getIsActive() != null)
                  if (specialistDto.getIsActive())
                        predicateList.add(criteriaBuilder.isTrue(specialistRoot.get("isActive")));
                  else
                        predicateList.add(criteriaBuilder.isFalse(specialistRoot.get("isActive")));

            if (specialistDto.getUserStatus() != null)
                  predicateList.add(criteriaBuilder.equal(specialistRoot.get("status"),
                         specialistDto.getUserStatus().toString()));

            if (specialistDto.getMinCredit() == null && specialistDto.getMaxCredit() != null)
                  specialistDto.setMinCredit(0L);
            if (specialistDto.getMinCredit() != null && specialistDto.getMaxCredit() == null)
                  specialistDto.setMaxCredit(Long.MAX_VALUE);
            if (specialistDto.getMinCredit() != null && specialistDto.getMaxCredit() != null)
                  predicateList.add(criteriaBuilder.between(specialistRoot.get("credit"),
                         specialistDto.getMinCredit(), specialistDto.getMaxCredit()));

            if (specialistDto.getMinScore() == null && specialistDto.getMaxScore() != null)
                  specialistDto.setMinScore(0.0);
            if (specialistDto.getMinScore() != null && specialistDto.getMaxScore() == null)
                  specialistDto.setMaxScore(5.0);
            if (specialistDto.getMinScore() != null && specialistDto.getMaxScore() != null)
                  predicateList.add(criteriaBuilder.between(specialistRoot.get("score"),
                         specialistDto.getMinScore(), specialistDto.getMaxScore()));

            if (specialistDto.getMinUserCreationAt() == null && specialistDto.getMaxUserCreationAt() != null)
                  specialistDto.setMinUserCreationAt(LocalDateTime.now().minusYears(2));
            if (specialistDto.getMinUserCreationAt() != null && specialistDto.getMaxUserCreationAt() == null)
                  specialistDto.setMaxUserCreationAt(LocalDateTime.now());
            if (specialistDto.getMinUserCreationAt() != null && specialistDto.getMaxUserCreationAt() != null)
                  predicateList.add(criteriaBuilder.between(specialistRoot.get("registrationTime"),
                         specialistDto.getMinUserCreationAt(), specialistDto.getMaxUserCreationAt()));

            if (specialistDto.getMinNumberOfOperation() == null && specialistDto.getMaxNumberOfOperation() != null)
                  specialistDto.setMinNumberOfOperation(0);
            if (specialistDto.getMinNumberOfOperation() != null && specialistDto.getMaxNumberOfOperation() == null)
                  specialistDto.setMaxNumberOfOperation(Integer.MAX_VALUE);
            if (specialistDto.getMinNumberOfOperation() != null && specialistDto.getMaxNumberOfOperation() != null)
                  predicateList.add(criteriaBuilder.between(specialistRoot.get("numberOfOperation"),
                         specialistDto.getMinNumberOfOperation(), specialistDto.getMaxNumberOfOperation()));

            if (specialistDto.getMinNumberOfDoneOperation() == null && specialistDto.getMaxNumberOfDoneOperation() != null)
                  specialistDto.setMinNumberOfDoneOperation(0);
            if (specialistDto.getMinNumberOfDoneOperation() != null && specialistDto.getMaxNumberOfDoneOperation() == null)
                  specialistDto.setMaxNumberOfDoneOperation(Integer.MAX_VALUE);
            if (specialistDto.getMinNumberOfDoneOperation() != null && specialistDto.getMaxNumberOfDoneOperation() != null)
                  predicateList.add(criteriaBuilder.between(specialistRoot.get("rateCounter"),
                         specialistDto.getMinNumberOfDoneOperation(), specialistDto.getMaxNumberOfDoneOperation()));

            specialistCriteriaQuery.select(specialistRoot).where(criteriaBuilder.or(predicateList.toArray(new Predicate[0])));
            List<Specialist> resultList = entityManager.createQuery(specialistCriteriaQuery).getResultList();
            List<FilterUserResponseDTO> fuDTOS = new ArrayList<>();
            resultList.forEach(rl -> fuDTOS.add(specialistMapper.convertToFilterDTO(rl)));
            return fuDTOS;
      }

}
