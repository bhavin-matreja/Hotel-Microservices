package com.hotel.UserService.services.impl;

import com.hotel.UserService.entities.Rating;
import com.hotel.UserService.entities.User;
import com.hotel.UserService.exception.ResourceNotFoundException;
import com.hotel.UserService.repositories.UserRepository;
import com.hotel.UserService.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

/*
    @Autowired
    private HotelService hotelService;
*/

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        //generate  unique userid
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        //implement RATING SERVICE CALL: USING REST TEMPLATE
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        // get user from database with help of user repository
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id not found on server !! : " + userId));
        // fetch rating of the above user from RATING SERVICE
        // http://localhost:8083/ratings/users/797ad556-9933-4880-b441-c60635c6d233
        ArrayList<Rating> ratingsForUser = restTemplate.getForObject("http://localhost:8083/ratings/users/" + user.getUserId(), ArrayList.class);
        logger.info("{}", ratingsForUser);
        user.setRatings(ratingsForUser);
        return user;
    }
}
