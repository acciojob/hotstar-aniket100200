package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        user =userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId)
    {

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        User user=userRepository.findById(userId).get();

        if(user==null)return 0;

        int userAge=user.getAge();
        SubscriptionType subscriptionType=user.getSubscription().getSubscriptionType();
        List<WebSeries>webSeries=webSeriesRepository.findAll();

        //Subscription_Type and Age will also matter..
       if(subscriptionType.equals(SubscriptionType.ELITE))return webSeries.size();


       int pro=0;
       int basic=0;
       for(WebSeries web :webSeries)
       {
           if(web.getSubscriptionType().equals(SubscriptionType.BASIC))basic++;
           else if(web.getSubscriptionType().equals(SubscriptionType.PRO))pro++;
       }

        return subscriptionType.equals(SubscriptionType.PRO) ? pro+basic:basic;
    }


}
