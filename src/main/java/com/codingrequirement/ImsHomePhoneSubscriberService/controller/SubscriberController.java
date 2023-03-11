package com.codingrequirement.ImsHomePhoneSubscriberService.controller;

import com.codingrequirement.ImsHomePhoneSubscriberService.model.Subscriber;
import com.codingrequirement.ImsHomePhoneSubscriberService.repository.SubscriberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class SubscriberController {

    @Autowired
    SubscriberRepository subscriberRepository;


    @PostMapping("/ims/subscriber")
    public ResponseEntity<Subscriber> saveSubscriber(@RequestBody Subscriber subscriber) {

        return ResponseEntity.ok(subscriberRepository.save(subscriber));
    }

    @GetMapping("/ims/subscriber")
    public ResponseEntity<List<Subscriber>> getAllSubscriber() {
        return ResponseEntity.ok(subscriberRepository.findAll());
    }

    @GetMapping("/ims/subscriber/phoneNumber")
    public ResponseEntity<Subscriber> getSubscriber(@RequestParam String phoneNumber) {
        return new ResponseEntity<Subscriber>(subscriberRepository.findByPhoneNumber(phoneNumber), HttpStatus.OK);
    }

    @PutMapping("/ims/subscriber/{phoneNumber}")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber newSubscriber, @PathVariable String phoneNumber) {

        Optional<Subscriber> subscriberOptional = Optional.ofNullable(subscriberRepository.findByPhoneNumber(phoneNumber));

        if (subscriberOptional.isEmpty()) {
            throw new IllegalArgumentException("Subscriber Cannot be found!");
        }

        return subscriberOptional.map(updateSubscriber -> {
            updateSubscriber.setPhoneNumber(newSubscriber.getPhoneNumber());
            updateSubscriber.setDomain(newSubscriber.getDomain());
            updateSubscriber.setStatus(newSubscriber.getStatus());
            updateSubscriber.getFeatures().getCallForwardNoReply().setProvisioned(newSubscriber.getFeatures().getCallForwardNoReply().getProvisioned());
            updateSubscriber.getFeatures().getCallForwardNoReply().setDestination(newSubscriber.getFeatures().getCallForwardNoReply().getDestination());

            return ResponseEntity.ok(subscriberRepository.save(updateSubscriber));
        })
                .orElseGet(() -> {
                    newSubscriber.setPhoneNumber(phoneNumber);
                    return ResponseEntity.ok(subscriberRepository.save(newSubscriber));
                });
    }

    @DeleteMapping("/ims/subscriber/{phoneNumber}")
    public ResponseEntity<String> deleteSubscriber(@PathVariable String phoneNumber) {
        subscriberRepository.deleteByPhoneNumber(phoneNumber);
        return ResponseEntity.ok("Successfully deleted!");
    }


}
