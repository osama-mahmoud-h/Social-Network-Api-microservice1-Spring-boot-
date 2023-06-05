package com.example.server.services.impl;

import com.example.server.models.Test;
import com.example.server.payload.request.profile.SocialRequestDto;
import com.example.server.repository.TestRepository;
import com.example.server.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImp implements TestService {
    private final TestRepository testRepository;

    @Override
    public SocialRequestDto[] saveName(SocialRequestDto name){


        Test test = new Test();

        SocialRequestDto []oldSocials = testRepository.findById(58l).get().getNames();
        System.out.println("socials : "+ Arrays.toString(oldSocials));

        if(oldSocials!=null){
            System.out.println("socials : "+ Arrays.toString(oldSocials));
        }
        SocialRequestDto[] newArr = new SocialRequestDto[3];

        for (int i = 0; i < newArr.length; i++) {
            newArr[i] = name;
        }
        test.setNames(newArr);
        testRepository.save(test);

        return test.getNames();
    }

}
