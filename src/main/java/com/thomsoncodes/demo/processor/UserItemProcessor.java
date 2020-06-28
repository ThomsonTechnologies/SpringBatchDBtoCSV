package com.thomsoncodes.demo.processor;

import org.springframework.batch.item.ItemProcessor;

import com.thomsoncodes.demo.model.User;

public class UserItemProcessor implements ItemProcessor<User, User>{

	@Override
	public User process(User person) throws Exception {
		System.out.println("processing resultset>>> " + person.getFirstName());
		return person;
	}

}
