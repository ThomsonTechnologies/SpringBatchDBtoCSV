package com.thomsoncodes.demo.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.thomsoncodes.demo.model.User;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		 User user = new User();
		 user.setUserId(rs.getInt("user_id"));
		 user.setFirstName(rs.getString("first_name"));
		 user.setLastName(rs.getString("last_name"));
		 user.setEmail(rs.getString("email"));
		return user;
	}

}
