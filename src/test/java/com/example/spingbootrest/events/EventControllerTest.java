package com.example.spingbootrest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    /*@TestConfiguration
    void beenConfiguration(){

    }*/

    /**
     * HTTP Status code 201로 성공하는지 확인
     * @throws Exception JackMapper 예외
     */
    @Test @DisplayName("정상적인 이벤트가 생성되는 테스트")
    void createEvent() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11,24,14,21 ))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25,14,21))
                .endEventDateTime(LocalDateTime.of(2018, 11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-events").exists())
                // .andExpect(jsonPath("_links.profile").exists())
        ;


    }

    @Test @DisplayName("입력값이 잘못된 이벤트 생성 경우의 테스트")
    void createEvent_Bad_Request_Wrong_Input() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11,25,14,21 ))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24,14,21))
                .endEventDateTime(LocalDateTime.of(2018, 11,23,14,21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;

    }


    @Test @DisplayName("입력 값이 없는 이벤트 생성 테스트")
    void ctrateEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto event = EventDto.builder().build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test @DisplayName("입력 받을수 없는 데이터로 이벤트 생성하는 경우 테스트")
    void createEvent_Bad_Request_Input() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11,24,14,21 ))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25,14,21))
                .endEventDateTime(LocalDateTime.of(2018, 11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }





    @DisplayName("무료 테스트")
    @ParameterizedTest(name = "{index} => basePrice={0}, maxPrice={1}, isFree={2}")
    @MethodSource("paramsForTestFree")
   void testFree(int basePrice, int maxPrice, boolean isFree){
        //Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        //when
        event.update();

        //Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    // static 있어야 동작
    private static Object[] paramsForTestFree(){
        return new Object[]{
          new Object[] {0,0, true},
          new Object[] {100,0, false},
          new Object[] {0,100, false},
          new Object[] {100,200, false}
        } ;
    }

    @DisplayName("오프라인 테스트")
    @ParameterizedTest()
    @MethodSource("paramsForTestOffline")
    void testOffline(String location, boolean isOffline){
        //Given
        Event event = Event.builder()
                .location(location)
                .build();

        //What
        event.update();

        //Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }


    private static Object[] paramsForTestOffline(){
        return new Object[]{
          new Object[] {"강남역 D2 스타텁 팩토리", true},
          new Object[] {null, false},
          new Object[] {"", false}
        };
    }


}
