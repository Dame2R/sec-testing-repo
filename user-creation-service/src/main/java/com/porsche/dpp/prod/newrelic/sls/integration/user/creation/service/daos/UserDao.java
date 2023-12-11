package com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.daos;

import com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.entities.User;
import com.porsche.dpp.prod.utils.aws.dynamodb.GenericDao;

public interface UserDao extends GenericDao<User> {
   
   void save(User user);
}
