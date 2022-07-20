package com.example.spingbootrest.events;

import com.example.spingbootrest.accounts.AccountService;
import com.example.spingbootrest.common.AppProperties;
import com.example.spingbootrest.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    EventRepository eventRepository;
    @Autowired
    AccountService accountService;

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
                        .with(jwt())
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
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
        // .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("Link to self"),
                                linkWithRel("query-events").description("Link to query-events"),
                                linkWithRel("profile").description("Link to profile"),
                                linkWithRel("update-events").description("Link to update an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("date time of beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("date time of endEventDateTime"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                        ),

                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")

                        ),

                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("date time of beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("date time of endEventDateTime"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("free").description("it tells if this event is free of not"),
                                fieldWithPath("offline").description("it tells if this event is offline event of not"),
                                fieldWithPath("eventStatus").description("it tells if this event is status"),
                                fieldWithPath("manager").description("it tells if this User is Auth Level"),
                                fieldWithPath("_links.self.href").description("Link to self"),
                                fieldWithPath("_links.query-events.href").description("Link to update event list"),
                                fieldWithPath("_links.profile.href").description("Link to profile"),
                                fieldWithPath("_links.update-events.href").description("Link to update existing events")
                        )
                ));
    }

    private String getBeararToken() throws Exception {
        return "Bearer " + getAccecessToken();
    }

    private String getAccecessToken() throws Exception {
        String userName = appProperties.getAdminUsername();
        UserDetails userDetails = this.accountService.loadUserByUsername(userName);

        String clientId = appProperties.getClientId();
        String clientSecret = appProperties.getClientSecret();
        ResultActions perform = this.mockMvc.perform(post("/oauth2/token")
                .with(httpBasic(clientId, clientSecret))
                .param("grant_type", "client_credentials"));
        var resultString = perform.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(resultString, Map.class).get("access_token").toString();
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
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists()


                );

    }


    @Test @DisplayName("입력 값이 없는 이벤트 생성 테스트")
    void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto event = EventDto.builder().build();

        mockMvc.perform(post("/api/events/")
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
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
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
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


    
    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    void queryEvents() throws Exception {
        //Given
        IntStream.range(0,30).forEach(this::generateEvent);
        //When
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "id,DESC")
                        .contentType(MediaTypes.HAL_JSON)
                        .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        //Todo Query Events 문서화 구현 중
                                links(
                                        linkWithRel("self").description("Link to self"),
                                        linkWithRel("profile").description("Link to profile"),
                                        linkWithRel("next").description("다음 페이지 URI"),
                                        linkWithRel("first").description("첫페이지 URI"),
                                        linkWithRel("prev").description("이전 페이지 URI"),
                                        linkWithRel("last").description("마지막 페이지 URI")
                                ),
                                requestHeaders(
                                        headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                                ))

                );

    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event_"+index)
                .description("test description"+index)
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11,24,14,21 ))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25,14,21))
                .endEventDateTime(LocalDateTime.of(2018, 11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }


    @Test
    @DisplayName("기존의 이벤트 하나 조회하기")
    void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                        .contentType(MediaTypes.HAL_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(print())
                .andDo(document("get-an-event")
                //Todo 단일 이벤트 조회 문서화 구현 필요
                );




        //When

    }

    @Test @DisplayName("없는 이벤트 조회하여 404 응답 받기")
    void getEvent404() throws Exception {
    //When & Then
        this.mockMvc.perform(get("/api/events/4894984")
                        .contentType(MediaTypes.HAL_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNotFound());
    }

    
    @Test @DisplayName("이벤트를 정상 적으로 수정하기")
    void updateEvent() throws Exception {
        //Given
        Event event = generateEvent(201);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        String evnetName = "Update Name";
        eventDto.setName(evnetName);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(evnetName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-events"));
    }

    @Test @DisplayName("없는 이벤트 수정 실패")
    void updateEvent_Bad_Request_Empty_Event() throws Exception {
        //Given
        Event event = generateEvent(202);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        //When & Then
        this.mockMvc.perform(put("/api/events/11234")
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test @DisplayName("입력값이 없는 경우의 이벤트 수정 실패")
    void updateEvent_Bad_Request_Empty_Input() throws Exception {
        //Given
        Event event = generateEvent(202);
        EventDto eventDto = new EventDto();


        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("입력값이 잘못된 경우의 이벤트 수정 실패")
    void updateEvent_Bad_Request_Bad_Input() throws Exception {
        //Given
        Event event = generateEvent(100);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(200);
        eventDto.setMaxPrice(100);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBeararToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());


    }

}
