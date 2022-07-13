package com.example.spingbootrest.events;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventModel extends EntityModel<Event> {
    public EventModel(Event content) {
        super(content);
        add(linkTo(EventController.class).slash(content.getId()).withSelfRel());
    }
}
