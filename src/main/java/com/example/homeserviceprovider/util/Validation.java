package com.example.homeserviceprovider.util;

import com.example.homeserviceprovider.domain.address.Address;
import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.exception.*;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;
@Component
public class Validation {
      public static boolean checkBlank(String str) {
            if (str.isBlank()) throw new TextBlankException("the string input is blank!");
            return true;
      }

      public static boolean checkText(String text) {
            String textRegex = "^[^0-9]+$";
            if (!Pattern.matches(textRegex, text))
                  throw new AlphabetException("the wording of the text is not incorrect!");
            return true;
      }

      public  static boolean checkPositiveNumber(Long longDigit) {
            if (longDigit <= 0)
                  throw new PositiveNumberException("the number is negative!");
            return true;
      }

      public static boolean checkEmail(String email) {
            String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
            if (!Pattern.matches(emailRegex, email))
                  throw new EmailFormatException("the format of the email is incorrect!");
            return true;
      }


      public static boolean checkPassword(String password) {
            String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
            if (!Pattern.matches(passwordRegex, password))
                  throw new PasswordFormatException("the format of the password is incorrect!");
            return true;
      }

      public static byte[] checkImage(String imageName) throws IOException {
            File file = new File(imageName);
            checkBlank(imageName);
            if (!imageName.contains(".jpg"))
                  throw new ImageFormatException("the format of the image is incorrect!");
            long imageSize = (file.length()) / 1024;
            if (imageSize > 300L)
                  throw new ImageSizeException("the size of the image is bigger than 300kb!");
            return Files.readAllBytes(file.toPath());
      }

      public static boolean checkAddress(Address address) {
            if (

                   address.getProvince().isBlank() ||
                   address.getCity().isBlank() ||
                   address.getAvenue().isBlank() ||
                   address.getPostalCode().isBlank()
            )
                  throw new AddressFormatException("your address is invalid!");
            return true;
      }

      public static boolean checkScore(int score) {
            if (score < 0 || score > 5)
                  throw new ScoreOutOfBoundsException("the score must be between 0 to 5!");
            return true;
      }

      public static boolean checkOwnerOfTheOrder(Long orderId, Customer customer) {
            if (customer.getOrderList().stream().filter(o -> o.getId().equals(orderId)).findFirst().isEmpty())
                  throw new OrderIsNotExistException("you are not the owner of this order");
            return true;
      }
      public boolean checkOfferBelongToTheOrder(Long offerId, Customer customer) {
            boolean[] exist = new boolean[1];
            customer.getOrderList().forEach(order -> exist[0] = order.getOfferList().stream().filter(offer ->
                   offer.getId().equals(offerId)).findFirst().isEmpty());
            if (exist[0])
                  throw new OfferNotExistException("this offer not belong to your orders");
            return true;
      }


}
