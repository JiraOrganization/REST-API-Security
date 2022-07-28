package com.example.spingbootrest.events;


import com.example.spingbootrest.common.ErrorsModel;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;


    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if (errors.hasErrors())
            return badRequest(errors);

        eventValidator.validate(eventDto,errors);
        if (errors.hasErrors())
            return badRequest(errors);



        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);
        WebMvcLinkBuilder webMvcLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = webMvcLinkBuilder.toUri();

        EventModel eventModel = new EventModel(event);

        eventModel.add(linkTo(EventController.class).withRel("query-events"));
        eventModel.add(webMvcLinkBuilder.withRel("update-events"));
        eventModel.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventModel);
    }


    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler, Authentication authentication){

        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedModel = assembler.toModel(page, e -> new EventModel(e));
        pagedModel.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedModel);
    }



    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){

        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if (optionalEvent.isEmpty())
            return ResponseEntity.notFound().build();

        EventModel eventModel =  new EventModel(optionalEvent.get());
        eventModel.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventModel);
    }


    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors){

        if (errors.hasErrors())
            return badRequest(errors);

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors())
            return badRequest(errors);

        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty())
            return ResponseEntity.notFound().build();

        Event exsitingEvent = optionalEvent.get();

        // 입력 값을
        this.modelMapper.map(eventDto, exsitingEvent);
        Event save = this.eventRepository.save(exsitingEvent);

        EventModel eventModel = new EventModel(save);
        eventModel.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventModel);
    }


    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsModel(errors));
    }


}
