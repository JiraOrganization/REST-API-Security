package com.example.spingbootrest.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Event 도메인
 *
 *
 * EqualsAndHashCode id 만 추가해서 사용하며,
 * 연관관계가 있는 오브젝트를 피하여 상호 참조가 발생하는 것을 피한다.
 */
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;


}
