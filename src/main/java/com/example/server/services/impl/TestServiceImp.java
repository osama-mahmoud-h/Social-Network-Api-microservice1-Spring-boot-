package com.example.server.services.impl;

import com.example.server.models.Test;
import com.example.server.payload.request.profile.SocialRequestDto;
import com.example.server.repository.TestRepository;
import com.example.server.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Override
    public Set<SocialRequestDto> addTag(String tag){
        Test test = new Test();

        Set<SocialRequestDto> tagSet = new HashSet<>();

        SocialRequestDto scldto = new SocialRequestDto("name1","url1");

        tagSet.add(scldto);

        Optional<Test> test2 = testRepository.findById(60l);
        if (test2.isPresent()){
            test2.get().getTags().add(new SocialRequestDto("name2","url2"));
            testRepository.save(test);
            return test2.get().getTags();
        }else {
            //
            test.setTags(tagSet);
            testRepository.save(test);
            return test.getTags();
        }

    }

}
