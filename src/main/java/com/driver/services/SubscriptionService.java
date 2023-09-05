package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto)
    {

           //Save The subscription Object into the Db and return the total Amount that user has to pay

            int useId=subscriptionEntryDto.getUserId();
            User user=userRepository.findById(useId).get();
            //user is here..

            Subscription subscription=new Subscription();

            subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
            subscription.setUser(user);
            subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
            int totalAmount=totalAmountPaid(subscriptionEntryDto.getSubscriptionType(),subscriptionEntryDto.getNoOfScreensRequired());
            subscription.setTotalAmountPaid(totalAmount);

            //subscription save
         //subscription= subscriptionRepository.save(subscription);

         user.setSubscription(subscription);

         //save the user it will save the Subscription too..
         user=userRepository.save(user);

        return totalAmount;
    }

    private int totalAmountPaid(SubscriptionType subscriptionType, int noOfScreensRequired)
    {
       // For Basic Plan : 500 + 200noOfScreensSubscribed
        if(subscriptionType.equals(SubscriptionType.BASIC))return 550+200*noOfScreensRequired;

        //For PRO Plan : 800 + 250noOfScreensSubscribed
        if(subscriptionType.equals(SubscriptionType.PRO))return 800+250*noOfScreensRequired;

       //For ELITE Plan : 1000 + 350*noOfScreensSubscribed

        return 1000+350*noOfScreensRequired;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception
    {

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user=userRepository.findById(userId).get();
       int noOfScreensSubscribed= user.getSubscription().getNoOfScreensSubscribed();
        if(SubscriptionType.ELITE.equals(user.getSubscription()))throw new Exception("Already the best Subscription");

        //if you are at basic just try to go in Pro..
        int total=0;
        if(SubscriptionType.BASIC.equals(user.getSubscription()))
        {
        total = totalAmountPaid(SubscriptionType.PRO, noOfScreensSubscribed) - totalAmountPaid(SubscriptionType.BASIC, noOfScreensSubscribed);
        Subscription subscription=user.getSubscription();
        subscription.setSubscriptionType(SubscriptionType.PRO);
        subscription.setTotalAmountPaid(subscription.getTotalAmountPaid()+total);
        subscriptionRepository.save(subscription);
    }
        if(SubscriptionType.PRO.equals(user.getSubscription()))
        {
            total = totalAmountPaid(SubscriptionType.ELITE, noOfScreensSubscribed) - totalAmountPaid(SubscriptionType.PRO, noOfScreensSubscribed);
            Subscription subscription=user.getSubscription();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(subscription.getTotalAmountPaid()+total);
            subscriptionRepository.save(subscription);
        }
        return total;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription>subscriptions=subscriptionRepository.findAll();
        int totalRevenue=0;
        for(Subscription subscription:subscriptions){
           totalRevenue+= subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }



}
