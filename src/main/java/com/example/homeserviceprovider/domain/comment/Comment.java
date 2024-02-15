package com.example.homeserviceprovider.domain.comment;


import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.order.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment extends BaseEntity<Long> {
      int score;
      String textComment;
      @OneToOne
      Order order;
      public Comment(int score, String comment) {
            this.score = score;
            this.textComment = comment;
      }
}
