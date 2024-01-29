package com.example.homeserviceprovider;

import com.example.homeserviceprovider.domain.service.MainServices;
import com.example.homeserviceprovider.domain.service.SubServices;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.domain.user.enums.SpecialistStatus;
import com.example.homeserviceprovider.exception.AlphabetException;
import com.example.homeserviceprovider.exception.MainServicesIsExistException;
import com.example.homeserviceprovider.exception.SubServicesIsExistException;
import com.example.homeserviceprovider.service.AdminService;
import com.example.homeserviceprovider.service.MainServiceService;
import com.example.homeserviceprovider.service.SpecialistService;
import com.example.homeserviceprovider.service.SubServicesService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


import static com.example.homeserviceprovider.util.FileToBytesConverter.convertFileToBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminServiceImplTest {

      @Autowired
      private AdminService ADMIN_SERVICE;
      @Autowired
      private MainServiceService MAIN_SERVICE_SERVICE;
      @Autowired
      private SubServicesService SUBSERVICE_SERVICE;
      @Autowired
      private SpecialistService SPECIALIST_SERVICE;

      @Test
      @Order(1)
      void createNewMainServiceWithExceptionForInvalidInput() {
            MainServices mainServices = new MainServices("facilities12 Building");
            assertThrows(AlphabetException.class, () -> {
                  ADMIN_SERVICE.createMainService(mainServices);
            });
      }

      @Test
      @Order(2)
      void createNewMainService() {
            ADMIN_SERVICE.createMainService(new MainServices("facilities Building"));
            Optional<MainServices> optionalMainService =
                   MAIN_SERVICE_SERVICE.findByName("facilities Building");
            assertEquals("facilities Building", optionalMainService.get().getName());
      }

      @Test
      @Order(3)
      void createDuplicateMainService() {
            assertThrows(MainServicesIsExistException.class, () -> {
                  ADMIN_SERVICE.createMainService(new MainServices("facilities Building"));
            });
      }



      @Test
      @Order(4)
      void addSubServices() {
            ADMIN_SERVICE.addSubServices(
                   new SubServices("pipeing", 1000L, "hard work", new MainServices("facilities Building")));
            Optional<SubServices> room = SUBSERVICE_SERVICE.findByName("pipeing");
            assertEquals(1000L, room.get().getBasePrice());
      }

      @Test
      @Order(5)
      void addDuplicateSubServices() {
            assertThrows(SubServicesIsExistException.class, () -> {
                  ADMIN_SERVICE
                         .addSubServices(new SubServices("pipeing", 1000L,
                                "hard work", new MainServices("facilities Building")));
            });
      }
      @Test
      @Order(6)
      void signUpSpecialist() throws Exception {
            String pathImage = "C:\\Users\\sp\\IdeaProjects\\home-service-provider\\src\\main\\resources\\imageUploaded\\images.jpg";
            File file = new File(pathImage);
            byte[] imageBayte = convertFileToBytes(file);
            Specialist specialist =new Specialist("milad", "ahmadian",
                   "milad.ah@yahoo.com", "45#Po@iu","Tehran",imageBayte);
            SPECIALIST_SERVICE.addNewSpecialist(specialist,pathImage);
      }



      @Test
      @Order(7)
      void changeSpecialistStatus() throws Exception {
            Specialist specialist = SPECIALIST_SERVICE.findByUsername("milad.ah@yahoo.com").get();
            ADMIN_SERVICE.confirmSpecialist(specialist.getId());
            assertEquals(SPECIALIST_SERVICE.findByUsername("milad.ah@yahoo.com").
                   get().getStatus(), SpecialistStatus.CONFIRMED);
      }

      @Test
      @Order(8)
      void addSpecialistToSubServices() {
            Optional<Specialist> specialist =
                   SPECIALIST_SERVICE.findByUsername("milad.ah@yahoo.com");
            Optional<SubServices> serviceByName = SUBSERVICE_SERVICE.findByName("pipeing");
            ADMIN_SERVICE.addSpecialistToSubServices(serviceByName.get().getId(), specialist.get().getId());

            SubServices subServices = SUBSERVICE_SERVICE.findByName("pipeing").get();
            specialist.get().getSubServicesList().forEach(sub -> {
                  if (Objects.equals(sub.getId(), subServices.getId()))
                        assertEquals(sub.getId(), subServices.getId());
            });
      }


      @Test

      @Order(9)
      void findAllMainServices() {
            List<MainServices> allMainService = ADMIN_SERVICE.findAllMainService();
            int count = (int) allMainService.stream().
                   filter(Objects::nonNull).count();
            assertEquals(1, count);
      }


      @Test
      @Order(10)
      void findAllSubService() {
            List<SubServices> allSubService = ADMIN_SERVICE.findAllSubServices();
            int count = (int) allSubService.stream().
                   filter(Objects::nonNull).count();
            assertEquals(1, count);
      }

      @Test
      @Order(11)
      void editSubServicesCustom() {
            SubServices subServices = SUBSERVICE_SERVICE.findByName("pipeing").get();
            ADMIN_SERVICE.editSubServicesCustom(new SubServices(subServices.getId(),"wall", 125000L, "wood"));
            SubServices newSubServices = SUBSERVICE_SERVICE.findByName("wall").get();
            assertEquals(subServices.getId(), newSubServices.getId());
      }


}
