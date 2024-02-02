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
      private AdminService adminService;
      @Autowired
      private MainServiceService mainServiceService;
      @Autowired
      private SubServicesService subServicesService;
      @Autowired
      private SpecialistService specialistService;

      @Test
      @Order(1)
      void createNewMainServiceWithExceptionForInvalidInput() {
            MainServices mainServices = new MainServices("facilities12 Building");
            assertThrows(AlphabetException.class, () -> {
                  adminService.createMainService(mainServices);
            });
      }

      @Test
      @Order(2)
      void createNewMainService() {
            adminService.createMainService(new MainServices("facilities Building"));
            Optional<MainServices> optionalMainService =
                   mainServiceService.findByName("facilities Building");
            assertEquals("facilities Building", optionalMainService.get().getName());
      }

      @Test
      @Order(3)
      void createDuplicateMainService() {
            assertThrows(MainServicesIsExistException.class, () -> {
                  adminService.createMainService(new MainServices("facilities Building"));
            });
      }



      @Test
      @Order(4)
      void addSubServices() {
            adminService.addSubServices(
                   new SubServices("pipeing", 1000L, "hard work", new MainServices("facilities Building")));
            Optional<SubServices> room = subServicesService.findByName("pipeing");
            assertEquals(1000L, room.get().getBasePrice());
      }

      @Test
      @Order(5)
      void addDuplicateSubServices() {
            assertThrows(SubServicesIsExistException.class, () -> {
                  adminService
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
            specialistService.addNewSpecialist(specialist,pathImage);
      }



      @Test
      @Order(7)
      void changeSpecialistStatus() throws Exception {
            Specialist specialist = specialistService.findByUsername("milad.ah@yahoo.com").get();
            adminService.confirmSpecialist(specialist.getId());
            assertEquals(specialistService.findByUsername("milad.ah@yahoo.com").
                   get().getStatus(), SpecialistStatus.CONFIRMED);
      }

      @Test
      @Order(8)
      void addSpecialistToSubServices() {
            Optional<Specialist> specialist =
                   specialistService.findByUsername("milad.ah@yahoo.com");
            Optional<SubServices> serviceByName = subServicesService.findByName("pipeing");
            adminService.addSpecialistToSubServices(serviceByName.get().getId(), specialist.get().getId());

            SubServices subServices = subServicesService.findByName("pipeing").get();
            specialist.get().getSubServicesList().forEach(sub -> {
                  if (Objects.equals(sub.getId(), subServices.getId()))
                        assertEquals(sub.getId(), subServices.getId());
            });
      }


      @Test

      @Order(9)
      void findAllMainServices() {
            List<MainServices> allMainService = adminService.findAllMainService();
            int count = (int) allMainService.stream().
                   filter(Objects::nonNull).count();
            assertEquals(1, count);
      }


      @Test
      @Order(10)
      void findAllSubService() {
            List<SubServices> allSubService = adminService.findAllSubServices();
            int count = (int) allSubService.stream().
                   filter(Objects::nonNull).count();
            assertEquals(1, count);
      }

      @Test
      @Order(11)
      void editSubServicesCustom() {
            SubServices subServices = subServicesService.findByName("pipeing").get();
            adminService.editSubServicesCustom(new SubServices(subServices.getId(),"wall", 125000L, "wood"));
            SubServices newSubServices = subServicesService.findByName("wall").get();
            assertEquals(subServices.getId(), newSubServices.getId());
      }


}
