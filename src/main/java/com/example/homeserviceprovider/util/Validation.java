package com.example.homeserviceprovider.util;

import com.example.homeserviceprovider.domain.user.Customer;
import com.example.homeserviceprovider.domain.user.Specialist;
import com.example.homeserviceprovider.dto.request.AddressDTO;
import com.example.homeserviceprovider.dto.request.BalanceRequestDTO;
import com.example.homeserviceprovider.dto.request.PaymentRequestDTO;
import com.example.homeserviceprovider.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class Validation {

    public boolean checkBlank(String str) {
        if (str.isBlank()) throw new TextBlankException("the string input is blank!");
        return true;
    }

    public boolean checkText(String text) {
        String textRegex = "^[^0-9]+$";
        if (!Pattern.matches(textRegex, text))
            throw new AlphabetException("the wording of the text is not incorrect!");
        return true;
    }

    public boolean checkPositiveNumber(Long longDigit) {
        if (longDigit <= 0)
            throw new PositiveNumberException("the number is negative!");
        return true;
    }
    public boolean checkPositiveNumber(Double longDigit) {
        if (longDigit <= 0)
            throw new PositiveNumberException("the number is negative!");
        return true;
    }

    public boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        if (!Pattern.matches(emailRegex, email))
            throw new EmailFormatException("the format of the email is incorrect!");
        return true;
    }


    public boolean checkPassword(String password) {
        String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        if (!Pattern.matches(passwordRegex, password))
            throw new PasswordFormatException("the format of the password is incorrect!");
        return true;
    }

    public boolean checkImage(MultipartFile image) {
        String imageName = image.getContentType();
        if (Objects.equals(null, imageName))
            throw new ImageFormatException("the image is empty!");
        checkBlank(imageName);
        if (!imageName.contains("/jpg"))
            throw new ImageFormatException("the format of the image is incorrect!");
        long imageSize = (image.getSize()) / 1024;
        if (imageSize > 300L)
            throw new ImageSizeException("the size of the image is bigger than 300kb!");
        return true;
    }

    public boolean checkScore(int score) {
        if (score < 0 || score > 5)
            throw new PositiveNumberException("the score must be between 0 to 5!");
        return true;
    }

    public boolean checkOwnerOfTheOrder(Long orderId, Customer customer) {
        if (customer.getOrderList().stream().filter(o -> o.getId().equals(orderId)).findFirst().isEmpty())
            throw new OrderIsNotExistException("you are not the owner of this order");
        return true;
    }

    public boolean checkOfferBelongToTheOrder(Long offerId, Customer client) {
        boolean[] exist = new boolean[1];
        client.getOrderList().forEach(order -> exist[0] = order.getOfferList().stream().filter(offer ->
                offer.getId().equals(offerId)).findFirst().isEmpty());
        if (exist[0])
            throw new OfferNotExistException("this offer not belong to your orders");
        return true;
    }

    public boolean checkOwnerOfTheOffer(Long offerId, Specialist worker) {
        if (worker.getOfferList().stream().filter(o -> o.getId().equals(offerId)).findFirst().isEmpty())
            throw new OrderIsNotExistException("you are not the owner of this offer");
        return true;
    }

    public boolean checkPaymentRequest(PaymentRequestDTO dto) {
        if (!dto.getCaptcha().equals(dto.getHidden())) {
            throw new CaptchaException("wrong captcha");
        }
        if (Integer.parseInt(dto.getYear()) < LocalDateTime.now().getYear()) {
            throw new DateTimeException("expired card ");
        }
        if (Integer.parseInt(dto.getYear()) == LocalDateTime.now().getYear() &&
            Integer.parseInt(dto.getMonth()) < LocalDateTime.now().getMonth().getValue()) {
            throw new DateTimeException("expired card ");
        }
        return true;
    }
    public boolean checkBalanceRequest(BalanceRequestDTO dto) {
        if (!dto.getCaptcha().equals(dto.getHidden())) {
            throw new CaptchaException("wrong captcha");
        }
        if (Integer.parseInt(dto.getYear()) < LocalDateTime.now().getYear()) {
            throw new DateTimeException("expired card ");
        }
        if (Integer.parseInt(dto.getYear()) == LocalDateTime.now().getYear() &&
            Integer.parseInt(dto.getMonth()) < LocalDateTime.now().getMonth().getValue()) {
            throw new DateTimeException("expired card ");
        }
        return true;
    }

    public boolean checkAddress(AddressDTO dto) {
        if (
                dto.getProvince().isBlank() ||
                dto.getCity().isBlank() ||
                dto.getAvenue().isBlank() ||
                dto.getPostalCode().isBlank()
        )
            throw new AddressFormatException("your address is invalid!");
        return true;
    }
}
