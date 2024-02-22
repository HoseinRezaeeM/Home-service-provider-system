package com.example.homeserviceprovider.service.Impl;


import cn.apiclub.captcha.Captcha;
import com.example.homeserviceprovider.base.service.impl.BaseEntityServiceImpl;
import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.comment.Comment;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.Order;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Admin;
import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.Users;
import com.example.homeserviceprovider.dto.request.*;
import com.example.homeserviceprovider.dto.response.*;
import com.example.homeserviceprovider.exception.*;
import com.example.homeserviceprovider.mapper.*;
import com.example.homeserviceprovider.repository.CustomerRepository;
import com.example.homeserviceprovider.security.token.entity.Token;
import com.example.homeserviceprovider.security.token.service.TokenService;
import com.example.homeserviceprovider.service.*;
import com.example.homeserviceprovider.util.CaptchaUtil;
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
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.ACCEPTED;
import static com.example.homeserviceprovider.domain.offer.enums.OfferStatus.REJECTED;
import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.*;
import static com.example.homeserviceprovider.domain.user.enums.CustomerStatus.*;


@Service
@Transactional
public class CustomerServiceImpl extends BaseEntityServiceImpl<Customer, Long, CustomerRepository>
       implements CustomerService {
      private static final String PRE_WRITTEN_MESSAGE =
             "This is a pre-written message: I have no particular opinion.";

      private final MainServiceService mainServiceService;
      private final SubServicesService subServicesService;
      private final OrderService orderService;
      private final OfferService offerService;
      private final CommentService commentService;
      private final SpecialistService specialistService;
      private final TokenService tokenService;
      private final EmailService emailService;

      private final CustomerMapper customerMapper;
      private final MainServiceMapper mainServiceMapper;
      private final CommentMapper commentMapper;
      private final OrderMapper orderMapper;
      private final OfferMapper offerMapper;
      private final AddressMapper addressMapper;

      private final Validation validation;
      private final PasswordEncoder passwordEncoder;

      @PersistenceContext
      private final EntityManager entityManager;

      public CustomerServiceImpl(CustomerRepository repository, MainServiceService mainServiceService, SubServicesService subServicesService, OrderService orderService,
                                 OfferService offerService, CommentService commentService, SpecialistService specialistService, TokenService tokenService,
                                 EmailService emailService, CustomerMapper customerMapper, MainServiceMapper mainServiceMapper, CommentMapper commentMapper,
                                 OrderMapper orderMapper, OfferMapper offerMapper, AddressMapper addressMapper, Validation validation, PasswordEncoder passwordEncoder
             , EntityManager entityManager) {
            super(repository);
            this.mainServiceService = mainServiceService;
            this.subServicesService = subServicesService;
            this.orderService = orderService;
            this.offerService = offerService;
            this.commentService = commentService;
            this.specialistService = specialistService;
            this.tokenService = tokenService;
            this.emailService = emailService;
            this.customerMapper = customerMapper;
            this.mainServiceMapper = mainServiceMapper;
            this.commentMapper = commentMapper;
            this.orderMapper = orderMapper;
            this.offerMapper = offerMapper;
            this.addressMapper = addressMapper;
            this.validation = validation;
            this.passwordEncoder = passwordEncoder;
            this.entityManager = entityManager;
      }

      @Override
      public Optional<Customer> findByUsername(String email) {
            return repository.findByEmail(email);
      }


      @Override
      public String addNewCustomer(CustomerRegistrationDTO dto) {
            validation.checkEmail(dto.getEmail());
            if (repository.findByEmail(dto.getEmail()).isPresent())
                  throw new DuplicateEmailException("this Email already exist!");
            validation.checkPassword(dto.getPassword());
            validation.checkText(dto.getFirstname());
            validation.checkText(dto.getLastname());
            Customer customer = customerMapper.convertToNewClient(dto);
            repository.save(customer);
            String newToken = UUID.randomUUID().toString();
            Token token = new Token(LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), customer);
            token.setToken(newToken);
            tokenService.saveToken(token);
            SimpleMailMessage mailMessage =
                   emailService.createEmail(customer.getEmail(), customer.getFirstname(), token.getToken(), customer.getRole());
            emailService.sendEmail(mailMessage);
            return newToken;
      }

      @Override
      public ProjectResponse editPassword(ChangePasswordDTO changePasswordDTO, Long clientId) {
            validation.checkPassword(changePasswordDTO.getNewPassword());
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword()))
                  throw new DuplicatePasswordException("this confirmNewPassword not match with newPassword!");
            Optional<Customer> customer = repository.findById(clientId);
            customer.get().setPassword(passwordEncoder.encode(changePasswordDTO.getConfirmNewPassword()));
            return new ProjectResponse("200", "CHANGE PASSWORD SUCCESSFULLY");
      }

      @Override
      @Transactional(readOnly = true)
      public List<MainServiceResponseDTO> showAllMainServices() {
            List<MainServiceResponseDTO> msrDTOS = new ArrayList<>();
            mainServiceService.findAll().forEach(ms -> msrDTOS.add(mainServiceMapper.convertToDTO(ms)));
            return msrDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<SubServicesResponseDTO> showSubServices(String mainServiceName) {
            validation.checkBlank(mainServiceName);
            if (mainServiceService.findByName(mainServiceName).isEmpty())
                  throw new MainServicesIsNotExistException("this main service dose not exist!");
            return subServicesService.findByMainServiceName(mainServiceName);
      }

      @Override
      public ProjectResponse addNewOrder(SubmitOrderDTO soDTO, Long clientId) {
            Customer dbClient = repository.findById(clientId).get();
            if (dbClient.getCustomerStatus().equals(NEW))
                  throw new CustomerStatusException("you can't sumbit order," +
                                                    "because your account is NEW," +
                                                    " please added Address and update your account.");
            validation.checkBlank(soDTO.getAddressProvince());
            if ((dbClient.getAddressList().stream().filter(a ->
                   a.getProvince().equals(soDTO.getAddressProvince()))).findFirst().isEmpty())
                  throw new AddressFormatException("you did not added such an address!");
            if (soDTO.getWorkStartDate().isBefore(LocalDateTime.now()))
                  throw new TimeException("passed this date!");
            if (soDTO.getWorkEndDate().isBefore(soDTO.getWorkStartDate()))
                  throw new TimeException("Time does not go back!");
            validation.checkBlank(soDTO.getSubServiceName());
            validation.checkPositiveNumber(soDTO.getProposedPrice());
            validation.checkBlank(soDTO.getDescription());
            Optional<SubServices> subServices = subServicesService.findByName(soDTO.getSubServiceName());
            if (subServices.isEmpty())
                  throw new SubServicesIsNotExistException("this subServices does not exist!");
            if (subServices.get().getBasePrice() > soDTO.getProposedPrice())
                  throw new AmountLessExseption("this proposed price is less than base price of the subServices!");
            Order order = orderMapper.convertToNewOrder(soDTO, dbClient, subServices.get());
            if (dbClient.getCustomerStatus().equals(HAS_NOT_ORDER_YET))
                  dbClient.setCustomerStatus(HAS_ORDERS);
            dbClient.setNumberOfOperation(dbClient.getNumberOfOperation() + 1);
            orderService.save(order);
            return new ProjectResponse("200", "THE ORDER HAS BEEN ADDED SUCCESSFULLY");
      }

      @Override
      @Transactional(readOnly = true)
      public List<FilterOrderResponseDTO> showAllOrders(Long clientId) {
            List<FilterOrderResponseDTO> orDTS = new ArrayList<>();
            List<Order> orderList = repository.findById(clientId).get().getOrderList();
            if (orderList.isEmpty())
                  return orDTS;
            orderList.forEach(o -> orDTS.add(orderMapper.convertToFilterDTO(o)));
            return orDTS;
      }

      @Override
      @Transactional(readOnly = true)
      public Long getCustomerCredit(Long clientId) {
            Optional<Customer> client = repository.findById(clientId);
            return client.get().getCredit();
      }

      @Override
      public List<FilterOrderResponseDTO> filterOrder(String orderStatus, Long clientId) {
            Optional<Customer> client = repository.findById(clientId);
            List<Order> dbOrderList = client.get().getOrderList();
            List<FilterOrderResponseDTO> orDTO = new ArrayList<>();
            if (dbOrderList.isEmpty())
                  return orDTO;
            List<Order> orderList = dbOrderList.stream().filter(o ->
                   o.getOrderStatus().name().equals(orderStatus)).toList();
            if (orderList.isEmpty())
                  return orDTO;
            orderList.forEach(o ->
                   orDTO.add(orderMapper.convertToFilterDTO(o)));
            return orDTO;
      }

      @Override
      public ProjectResponse changeOrderStatusToPaidByOnlinePayment(CustomerIdOrderIdDTO dto) {
            validation.checkPositiveNumber(dto.getCustomerId());
            validation.checkPositiveNumber(dto.getOrderId());
            Optional<Customer> customer = repository.findById(dto.getCustomerId());
            if (customer.isEmpty())
                  throw new CustomerNotExistException("not found user");
            Optional<Order> order = orderService.findById(dto.getOrderId());
            if (order.isEmpty())
                  throw new OrderIsNotExistException("ont found order");
            accounting(order.get());
            customer.get().setPaidCounter(customer.get().getPaidCounter() + 1);
            repository.save(customer.get());
            return new ProjectResponse("200", "payment was successfully");
      }

      @Override
      public ProjectResponse increaseCustomerCredit(CustomerIdPriceDTO dto) {
            validation.checkPositiveNumber(dto.getCustomerId());
            validation.checkPositiveNumber(dto.getPrice());
            Optional<Customer> customerOptional = repository.findById(dto.getCustomerId());
            if (customerOptional.isEmpty())
                  throw new CustomerNotExistException("not found user");
            customerOptional.get().setCredit(customerOptional.get().getCredit() + dto.getPrice());
            customerOptional.get().setNumberOfOperation(customerOptional.get().getNumberOfOperation() + 1);
            repository.save(customerOptional.get());
            return new ProjectResponse("200", "your account credit has been successfully " +
                                              "increased by $" + dto.getPrice() + ".");
      }

      @Override
      public ProjectResponse paidByInAppCredit(Long orderId, Users customer) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) customer);
            Optional<Customer> dbClient = repository.findById(customer.getId());
            Optional<Order> order = orderService.findById(orderId);
            Long credit = dbClient.get().getCredit();
            Long offerPrice = paymentPriceCalculator(orderId);
            if (credit < offerPrice)
                  throw new AmountLessExseption("not enough credit to pay in app");
            ((Customer) customer).setCredit(credit - offerPrice);
            accounting(order.get());
            dbClient.get().setPaidCounter(dbClient.get().getPaidCounter() + 1);
            repository.save(dbClient.get());
            return new ProjectResponse("200", "payment was successful");
      }

      private void accounting(Order order) {
            order.setOrderStatus(PAID);
            Offer offer = order.getOfferList().stream().filter(o ->
                   o.getOfferStatus().equals(ACCEPTED)).findFirst().get();
            orderService.save(order);
            Long offerPrice = offer.getProposedPrice();
            Specialist specialist = offer.getSpecialist();
            long managerShare = Math.round(offerPrice * 0.3);
            specialist.setCredit(specialist.getCredit() + offerPrice - managerShare);
            specialist.setPaidCounter(specialist.getPaidCounter() + 1);
            specialistService.save(specialist);
      }

      @Override
      public ModelAndView increaseAccountBalance(Long price, Long customerId, Model model) {
            validation.checkPositiveNumber(price);
            BalancePageDTO balancePageDTO = new BalancePageDTO();
            balancePageDTO.setCustomerId(customerId);
            balancePageDTO.setPrice(price);
            setupCaptcha(balancePageDTO);
            model.addAttribute("bdto", balancePageDTO);
            return new ModelAndView("incBalance");
      }

      @Override
      public ModelAndView payByOnlinePayment(Long orderId, Users users, Model model) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            PaymentPageDTO paymentPageDTO = new PaymentPageDTO();
            CustomerIdOrderIdDTO customerIdOrderIdDTO = new CustomerIdOrderIdDTO(users.getId(), orderId);
            paymentPageDTO.setCustomerIdOrderIdDTO(customerIdOrderIdDTO);
            paymentPageDTO.setPrice(paymentPriceCalculator(orderId));
            setupCaptcha(paymentPageDTO);
            model.addAttribute("dto", paymentPageDTO);
            return new ModelAndView("payment");
      }

      private void setupCaptcha(PaymentPageDTO dto) {
            Captcha captcha = CaptchaUtil.createCaptcha(350, 100);
            dto.setHidden(captcha.getAnswer());
            dto.setCaptcha("");
            dto.setImage(CaptchaUtil.encodeBase64(captcha));
      }

      private void setupCaptcha(BalancePageDTO dto) {
            Captcha captcha = CaptchaUtil.createCaptcha(350, 100);
            dto.setHidden(captcha.getAnswer());
            dto.setCaptcha("");
            dto.setImage(CaptchaUtil.encodeBase64(captcha));
      }

      @Override
      public ProjectResponse addAddress(AddressDTO addressDTO, Long clientId) {
            validation.checkAddress(addressDTO);
            Optional<Customer> customer = repository.findById(clientId);
            Address address = addressMapper.convertToAddress(addressDTO);
            address.setCustomer(customer.get());
            customer.get().getAddressList().add(address);
            if (customer.get().getCustomerStatus().equals(NEW))
                  customer.get().setCustomerStatus(HAS_NOT_ORDER_YET);
            repository.save(customer.get());
            return new ProjectResponse("200", "ADDED NEW ADDRESS SUCCESSFULLY");
      }

      private Long paymentPriceCalculator(Long orderId) {
            Optional<Order> order = orderService.findById(orderId);
            OrderStatus orderStatus = order.get().getOrderStatus();
            if (!orderStatus.equals(DONE)) {
                  if (orderStatus.equals(PAID))
                        throw new OrderStatusException(
                               "the cost of this order has already been \"PAID\"!");
                  else
                        throw new OrderStatusException(
                               "This order has not yet reached the payment stage, this order is in the " +
                               orderStatus + " stage!");
            }
            Optional<Offer> offer = order.get().getOfferList().stream().
                   filter(o -> o.getOfferStatus().equals(ACCEPTED)).findFirst();
            return offer.get().getProposedPrice();
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> findOfferListByOrderIdBasedOnProposedPrice(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            List<Offer> list = offerService.findOfferListByOrderIdBasedOnProposedPrice(orderId);
            if (list.isEmpty())
                  return orDTOS;
            list.forEach(
                   o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      @Transactional(readOnly = true)
      public List<OfferResponseDTO> findOfferListByOrderIdBasedOnSpecialistScore(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            List<OfferResponseDTO> orDTOS = new ArrayList<>();
            List<Offer> list = offerService.findOfferListByOrderIdBasedOnSpecialistScore(orderId);
            if (list.isEmpty())
                  return orDTOS;
            list.forEach(
                   o -> orDTOS.add(offerMapper.convertToDTO(o)));
            return orDTOS;
      }

      @Override
      public ProjectResponse chooseSpecialistForOrder(Long offerId, Users users) {
            validation.checkPositiveNumber(offerId);
            validation.checkOfferBelongToTheOrder(offerId, (Customer) users);
            Optional<Offer> offer = offerService.findById(offerId);
            if (offer.get().getOfferStatus().equals(ACCEPTED))
                  throw new OfferStatusException(" this offer alrady accepted");
            else if (offer.get().getOfferStatus().equals(REJECTED))
                  throw new OfferStatusException(" this offer alrady rejected");
            else
                  orderService.chooseOffer(offer.get().getOrder(), offerId);
            return new ProjectResponse("200", "CHOOSE SUCCESSFULLY");
      }


      @Override
      public ProjectResponse changeOrderStatusToStarted(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            Optional<Order> order = orderService.findById(orderId);
            if (!order.get().getOrderStatus().equals(WAITING_FOR_SPECIALIST_TO_COME))
                  throw new OrderIsNotExistException
                         ("the status of this order is not yet \"WAITING FOR EXPERT TO COME\"!");
            order.get().getOfferList().forEach(o -> {
                  if (o.getOfferStatus().equals(ACCEPTED))
                        if (o.getExecutionTime().isBefore(LocalDateTime.now()))
                              throw new TimeException("the specialist has not arrived at your place yet!");
            });
            order.get().setOrderStatus(STARTED);
            orderService.save(order.get());
            return new ProjectResponse("200", "ORDER STATUS CHANGED SUCCESSFULLY");
      }

      @Override
      public ProjectResponse changeOrderStatusToDone(Long orderId, Users users) {
            validation.checkPositiveNumber(orderId);
            validation.checkOwnerOfTheOrder(orderId, (Customer) users);
            Optional<Order> order = orderService.findById(orderId);
            if (!order.get().getOrderStatus().equals(STARTED))
                  throw new OrderIsNotExistException
                         ("the status of this order is not yet \"STARTED\"!");
            final Offer[] offer = new Offer[1];
            order.get().getOfferList().forEach(o -> {
                  if (o.getOfferStatus().equals(ACCEPTED))
                        offer[0] = o;
            });
            order.get().setOrderStatus(DONE);
            orderService.save(order.get());
            int hourlyDelay = (LocalDateTime.now().getHour()) - (offer[0].getEndTime().getHour());
            if (hourlyDelay > 0) {
                  Specialist specialist = offer[0].getSpecialist();
                  specialist.delay(hourlyDelay);
                  specialistService.save(specialist);
                  String delayFormat = " HOUR";
                  if (hourlyDelay > 1)
                        delayFormat = " HOURS";
                  return new ProjectResponse("200", "ORDER STATUS CHANGED SUCCESSFULLY WITH " +
                                                    hourlyDelay + delayFormat + "DELAY");
            }
            return new ProjectResponse("200", "ORDER STATUS CHANGED SUCCESSFULLY");
      }

      @Override
      public ProjectResponse registerComment(CommentRequestDTO dto, Users users) {
            validation.checkPositiveNumber(dto.getOrderId());
            validation.checkOwnerOfTheOrder(dto.getOrderId(), (Customer) users);
            validation.checkScore(dto.getScore());
            if (dto.getComment().isBlank())
                  dto.setComment(PRE_WRITTEN_MESSAGE);
            else
                  validation.checkText(dto.getComment());
            Optional<Order> order = orderService.findById(dto.getOrderId());
            if (!order.get().getOrderStatus().equals(DONE))
                  throw new OrderIsNotExistException
                         ("the status of this order is not yet \"DONE\"!");
            Specialist specialist = order.get().getOfferList().stream().filter(o ->
                   o.getOfferStatus().equals(ACCEPTED)).findFirst().get().getSpecialist();
            specialist.setScore(dto.getScore());
            Comment comment = commentMapper.convertToComment(dto);
            comment.setOrder(order.get());
            commentService.save(comment);
            order.get().setComment(comment);
            orderService.save(order.get());
            return new ProjectResponse("200",
                   "THE COMMENT ABOUT THE ORDER WAS SUCCESSFULLY REGISTERED.");
      }

      @Override
      public List<FilterUserResponseDTO> customerFilter(FilterUserDTO clientDTO) {

            List<FilterUserResponseDTO> fcDTOS = new ArrayList<>();
            List<Predicate> predicateList = new ArrayList<>();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Customer> clientCriteriaQuery = criteriaBuilder.createQuery(Customer.class);
            Root<Customer> clientRoot = clientCriteriaQuery.from(Customer.class);

            createFilters(clientDTO, predicateList, criteriaBuilder, clientRoot);
            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);
            clientCriteriaQuery.select(clientRoot).where(predicates);
            List<Customer> resultList = entityManager.createQuery(clientCriteriaQuery).getResultList();
            if (resultList.isEmpty())
                  return fcDTOS;
            resultList.forEach(rl -> fcDTOS.add(customerMapper.convertToFilterDTO(rl)));
            return fcDTOS;
      }

      private void createFilters(FilterUserDTO dto, List<Predicate> predicateList,
                                 CriteriaBuilder criteriaBuilder, Root<Customer> customerRoot) {
            if (dto.getFirstname() != null) {
                  String firstname = "%" + dto.getFirstname() + "%";
                  predicateList.add(criteriaBuilder.like(customerRoot.get("firstname"), firstname));
            }
            if (dto.getLastname() != null) {
                  String lastname = "%" + dto.getLastname() + "%";
                  predicateList.add(criteriaBuilder.like(customerRoot.get("lastname"), lastname));
            }
            if (dto.getUsername() != null) {
                  String email = "%" + dto.getUsername() + "%";
                  predicateList.add(criteriaBuilder.like(customerRoot.get("email"), email));
            }
            if (dto.getIsActive() != null)
                  if (dto.getIsActive())
                        predicateList.add(criteriaBuilder.isTrue(customerRoot.get("isActive")));
                  else
                        predicateList.add(criteriaBuilder.isFalse(customerRoot.get("isActive")));

            if (dto.getUserStatus() != null)
                  predicateList.add(criteriaBuilder.equal(customerRoot.get("customerStatus"),
                         dto.getUserStatus()));

            if (dto.getMinCredit() == null && dto.getMaxCredit() != null)
                  dto.setMinCredit(0L);
            if (dto.getMinCredit() != null && dto.getMaxCredit() == null)
                  dto.setMaxCredit(Long.MAX_VALUE);
            if (dto.getMinCredit() != null && dto.getMaxCredit() != null)
                  predicateList.add(criteriaBuilder.between(customerRoot.get("credit"),
                         dto.getMinCredit(), dto.getMaxCredit()));
            if (dto.getMinUserCreationAt() == null && dto.getMaxUserCreationAt() != null)
                  dto.setMinUserCreationAt(LocalDateTime.now().minusYears(2));
            if (dto.getMinUserCreationAt() != null && dto.getMaxUserCreationAt() == null)
                  dto.setMaxUserCreationAt(LocalDateTime.now());
            if (dto.getMinUserCreationAt() != null && dto.getMaxUserCreationAt() != null)
                  predicateList.add(criteriaBuilder.between(customerRoot.get("registrationTime"),
                         dto.getMinUserCreationAt(), dto.getMaxUserCreationAt()));
            if (dto.getMinNumberOfOperation() == null && dto.getMaxNumberOfOperation() != null)
                  dto.setMinNumberOfOperation(0);
            if (dto.getMinNumberOfOperation() != null && dto.getMaxNumberOfOperation() == null)
                  dto.setMaxNumberOfOperation(Integer.MAX_VALUE);
            if (dto.getMinNumberOfOperation() != null && dto.getMaxNumberOfOperation() != null)
                  predicateList.add(criteriaBuilder.between(customerRoot.get("numberOfOperation"),
                         dto.getMinNumberOfOperation(), dto.getMaxNumberOfOperation()));

            if (dto.getMinNumberOfDoneOperation() == null && dto.getMaxNumberOfDoneOperation() != null)
                  dto.setMinNumberOfDoneOperation(0);
            if (dto.getMinNumberOfDoneOperation() != null && dto.getMaxNumberOfDoneOperation() == null)
                  dto.setMaxNumberOfDoneOperation(Integer.MAX_VALUE);
            if (dto.getMinNumberOfDoneOperation() != null && dto.getMaxNumberOfDoneOperation() != null)
                  predicateList.add(criteriaBuilder.between(customerRoot.get("paidCounter"),
                         dto.getMinNumberOfDoneOperation(), dto.getMaxNumberOfDoneOperation()));


      }
}
