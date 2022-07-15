package com.example.spingbootrest.index;

import com.example.spingbootrest.events.EventController;
import com.example.spingbootrest.events.EventModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api")
    public RepresentationModel index(){
        var index = new RepresentationModel();

        index.add(linkTo(EventController.class).withRel("events"));
        return index;


    }

}
