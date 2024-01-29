package com.example.homeserviceprovider;
import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.comment.Comment;
import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.enums.OrderStatus;
import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.service.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.homeserviceprovider.domain.order.enums.OrderStatus.WAITING_FOR_SPECIALIST_TO_COME;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerAndSpecialistSeviceImplTest {

      @Autowired
      private CustomerService customerService;
      @Autowired
      private OrderService orderService;
      @Autowired
      private OfferService offerService;
      @Autowired
      private SpecialistService specialistService;
      @Autowired
      private SubServicesService subServicesService;

      @Test
      @Order(2)
      void findByUsername() {
            assertEquals("ali",
                   customerService.findByUsername("ali.akbari@gmail.com").get().getFirstname());
      }

      @Test
      @Order(3)
      void editPassword() {
            Optional<Customer> customer = customerService.findByUsername("ali.akbari@gmail.com");
            customerService.editPassword("12345!qQwe", "12345!qQwe", customer.get().getId());
            Optional<Customer> newclient = customerService.findByUsername("ali.akbari@gmail.com");
            assertEquals(newclient.get().getPassword(), "12345!qQwe");
      }

      @Test
      @Order(1)
      void signUp() {
            Customer customer = new Customer("ali", "akbari",
                   "ali.akbari@gmail.com", "4582!pOj");
            customerService.addNewCustomer(customer);
            assertEquals("ali",
                   customerService.findByUsername("ali.akbari@gmail.com").get().getFirstname());
      }

      @Test
      @Order(4)
      void findAllMainServices() {
            List<MainServices> allMainService = customerService.showAllMainServices();
            int count = (int) allMainService.stream().
                   filter(Objects::nonNull).count();
            assertEquals(1, count);
      }

      @Test
      @Order(5)
      void findSubserviceByMainServicesName() {
            List<SubServices> allJob = customerService.showSubServices("facilities Building");
            int count = (int) allJob.stream().
                   filter(Objects::nonNull).count();
            assertEquals(1, count);
      }

      @Test
      @Order(6)
      void addOrder() {
            Customer customer = customerService.findByUsername("ali.akbari@gmail.com").get();
            SubServices subServices = subServicesService.findByName("wall").get();
            Address address = new Address("Tehran"," sari","koche7","pelak12","1254","nabshe",customer);
            customerService.addAddress(address,customer.getId());
            com.example.homeserviceprovider.domain.order.Order order
                   =new com.example.homeserviceprovider.domain.order.Order(LocalDateTime.of(2024, 2, 1, 12, 12),
                   LocalDateTime.of(2024, 2, 25, 18, 12), 132000L, "nasty",customer,
                   subServices,address);
            customerService.addNewOrder(order, customer.getId());
            assertEquals(orderService.findByCustomerEmailAndOrderStatus("ali.akbari@gmail.com",
                   OrderStatus.WAITING_FOR_SPECIALIST_SUGGESTION).get(0).getDescription(), "nasty");
      }

      @Test
      @Order(7)
      void addOffers() {
            Offer offer = new Offer(LocalDateTime.of(2024, 2, 2, 12, 13)
               ,LocalDateTime.of(2024, 3, 1, 18, 11), 133000L);
            Optional<com.example.homeserviceprovider.domain.order.Order> order =
                   Optional.of(orderService.findAll().get(0));
            offer.setOrder(order.get());
            Optional<Specialist> specialist = specialistService.findByUsername("milad.ah@yahoo.com");
            offer.setSpecialist(specialist.get());
            specialistService.submitAnOffer(offer,specialist.get().getId());
            assertEquals(1, offerService.findAll().size());
      }

      @Test
      @Order(8)
      void findOfferListByOrderIdBasedOnProposedPrice() {
            Customer customer = customerService.findByUsername("ali.akbari@gmail.com").get();
            List<Offer> offerList =
                   customerService.findOfferListByOrderIdBasedOnProposedPrice(customer.getOrderList().get(0).getId(),customer);
            assertNotNull(offerList);
      }

      @Test
      @Order(9)
      void findOfferListByOrderIdBasedOnSpecilistrScore() {
            Customer customer = customerService.findByUsername("ali.akbari@gmail.com").get();
            Specialist specialist1 = specialistService.findByUsername("milad.ah@yahoo.com").get();
            specialist1.setScore(3);
            specialistService.save(specialist1);
            List<Offer> offerList = customerService.findOfferListByOrderIdBasedOnSpecialistScore(
                        customer.getOrderList().get(0).getId(),customer);
            assertNotNull(offerList);
      }

      @Test
      @Order(10)
      void acceptOffer() {
            Customer customer = customerService.findByUsername("ali.akbari@gmail.com").get();
            Long id = offerService.findAll().get(0).getId();
            customerService.chooseSpecialistForOrder(id,customer);
            Optional<Offer> optionalOffer = offerService.findById(id);
            assertEquals(WAITING_FOR_SPECIALIST_TO_COME,optionalOffer.get().getOrder().getOrderStatus());
      }

      @Test
      @Order(11)
      void changeOrderStatusAfterSpecialistComesAndToStart() {
            Customer customer = customerService.findByUsername("ali.akbari@gmail.com").get();
            Long id = orderService.findAll().get(0).getId();
            customerService.changeOrderStatusToStarted(id,customer);
            assertEquals(orderService.findById(id).get().getOrderStatus(),
                   OrderStatus.STARTED);
      }

      @Test
      @Order(12)
      void changeOrderStatusAfterStarted() {
            Customer customer = customerService.findByUsername("ali.akbari@gmail.com").get();
            Long id = orderService.findAll().get(0).getId();
            customerService.changeOrderStatusToDone(id,customer);
            assertEquals(orderService.findById(id).get().getOrderStatus(),
                   OrderStatus.DONE);
      }
      @Test
      @Order(13)
      void registerCommentForOrder(){
            Long id = orderService.findAll().get(0).getId();
            Comment comment =new Comment(3,"Very Good"
                   ,new com.example.homeserviceprovider.domain.order.Order(id));
            Customer customer = customerService.findByUsername("ali.akbari@gmail.com").get();
            customerService.registerComment(comment,customer);
      }

}
