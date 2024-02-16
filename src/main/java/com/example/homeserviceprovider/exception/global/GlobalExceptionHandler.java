package com.example.homeserviceprovider.exception.global;




import com.example.homeserviceprovider.dto.response.ProjectResponse;
import com.example.homeserviceprovider.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
      @ExceptionHandler(AddressFormatException.class)
      ResponseEntity<ProjectResponse> addressFormatHandler(AddressFormatException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("01", e.getMessage()));
      }

      @ExceptionHandler(AlphabetException.class)
      ResponseEntity<ProjectResponse> alphabetHandler(AlphabetException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("02", e.getMessage()));
      }

      @ExceptionHandler(AmountLessExseption.class)
      ResponseEntity<ProjectResponse> amountLessHandler(AmountLessExseption e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("03", e.getMessage()));
      }


      @ExceptionHandler(CustomerNotExistException.class)
      ResponseEntity<ProjectResponse> customerNotExistHandler(CustomerNotExistException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("05", e.getMessage()));
      }

      @ExceptionHandler(CustomerStatusException.class)
      ResponseEntity<ProjectResponse> customerStatusHandler(CustomerStatusException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("06", e.getMessage()));
      }

      @ExceptionHandler(DuplicateEmailException.class)
      ResponseEntity<ProjectResponse> duplicateEmailHandler(DuplicateEmailException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("07", e.getMessage()));
      }

      @ExceptionHandler(DuplicatePasswordException.class)
      ResponseEntity<ProjectResponse> duplicatePasswordHandler(DuplicatePasswordException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("08", e.getMessage()));
      }

      @ExceptionHandler(EmailFormatException.class)
      ResponseEntity<ProjectResponse> emailFormatHandler(EmailFormatException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("09", e.getMessage()));
      }

      @ExceptionHandler(ImageFormatException.class)
      ResponseEntity<ProjectResponse> imageFormatHandler(ImageFormatException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("10", e.getMessage()));
      }

      @ExceptionHandler(ImageSizeException.class)
      ResponseEntity<ProjectResponse> imageSizeHandler(ImageSizeException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("11", e.getMessage()));
      }

      @ExceptionHandler(SubServicesIsExistException.class)
      ResponseEntity<ProjectResponse> subServicesIsExistHandler(SubServicesIsExistException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("12", e.getMessage()));
      }

      @ExceptionHandler(SubServicesIsNotExistException.class)
      ResponseEntity<ProjectResponse> subServicesIsNotExistHandler(SubServicesIsNotExistException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("13", e.getMessage()));
      }

      @ExceptionHandler(MainServicesIsExistException.class)
      ResponseEntity<ProjectResponse> mainServiceIsExistHandler(MainServicesIsExistException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("14", e.getMessage()));
      }

      @ExceptionHandler(MainServicesIsNotExistException.class)
      ResponseEntity<ProjectResponse> mainServiceIsNotExistHandler(MainServicesIsNotExistException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("15", e.getMessage()));
      }


      @ExceptionHandler(OfferNotExistException.class)
      ResponseEntity<ProjectResponse> offerNotExistHandler(OfferNotExistException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("17", e.getMessage()));
      }

      @ExceptionHandler(OfferStatusException.class)
      ResponseEntity<ProjectResponse> offerStatusHandler(OfferStatusException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("18", e.getMessage()));
      }


      @ExceptionHandler(OrderIsNotExistException.class)
      ResponseEntity<ProjectResponse> orderIsNotExistHandler(OrderIsNotExistException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("19", e.getMessage()));
      }

      @ExceptionHandler(OrderStatusException.class)
      ResponseEntity<ProjectResponse> orderStatusHandler(OrderStatusException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("20", e.getMessage()));
      }

      @ExceptionHandler(PasswordFormatException.class)
      ResponseEntity<ProjectResponse> passwordFormatHandler(PasswordFormatException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("21", e.getMessage()));
      }

      @ExceptionHandler(PasswordIncorrect.class)
      ResponseEntity<ProjectResponse> passwordIncorrectHandler(PasswordIncorrect ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("22", ae.getMessage()));
      }

      @ExceptionHandler(PositiveNumberException.class)
      ResponseEntity<ProjectResponse> positiveNumberHandler(PositiveNumberException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("23", e.getMessage()));
      }

      @ExceptionHandler(ScoreOutOfBoundsException.class)
      ResponseEntity<ProjectResponse> scoreOutOfBoundsHandler(ScoreOutOfBoundsException e) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("24", e.getMessage()));
      }

      @ExceptionHandler(TextBlankException.class)
      ResponseEntity<ProjectResponse> textBlankHandler(TextBlankException ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("25", ae.getMessage()));
      }

      @ExceptionHandler(TimeException.class)
      ResponseEntity<ProjectResponse> timeHandler(TimeException ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("26", ae.getMessage()));
      }
      @ExceptionHandler(ValidationTokenException.class)
      ResponseEntity<ProjectResponse> ValidationTokenHandler(ValidationTokenException ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("27", ae.getMessage()));
      }
      @ExceptionHandler(VerifyCodeException.class)
      ResponseEntity<ProjectResponse> VerifyCodeHandler(VerifyCodeException ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("28", ae.getMessage()));
      }

      @ExceptionHandler(SpecialistIsHoldsExistException.class)
      ResponseEntity<ProjectResponse> specialistIsHoldsExistHandler(SpecialistIsHoldsExistException ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("29", ae.getMessage()));
      }

      @ExceptionHandler(SpecialistIsNotExistException.class)
      ResponseEntity<ProjectResponse> specialistIsNotExistHandler(SpecialistIsNotExistException ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("30", ae.getMessage()));
      }

      @ExceptionHandler(SpecialistNoAccessException.class)
      ResponseEntity<ProjectResponse> specialistNoAccessHandler(SpecialistNoAccessException ae) {
            return ResponseEntity.badRequest().body(
                   new ProjectResponse("31", ae.getMessage()));
      }
}
