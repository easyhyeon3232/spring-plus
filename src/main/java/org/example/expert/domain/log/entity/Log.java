package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Entity
@Table(name = "log")
@Getter
@NoArgsConstructor
public class Log extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long requesterId;
    private Long todoId;
    private Long managerUserId;
    private String status;
    private String message;
    private String errorMessage;

    public Log(Long requesterId, Long todoId, Long managerUserId, String status, String message, String errorMessage) {
        this.requesterId = requesterId;
        this.todoId = todoId;
        this.managerUserId = managerUserId;
        this.status = status;
        this.message = message;
        this.errorMessage = errorMessage;
    }
}
