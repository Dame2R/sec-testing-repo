package com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.daos.impl;

import com.porsche.dpp.prod.newrelic.sls.integration.user.creation.service.entities.User;
import com.porsche.dpp.prod.utils.aws.dynamodb.GenericDaoImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDaoImpl extends GenericDaoImpl<User> {
   private static final Logger LOG = LogManager
                     .getLogger(UserDaoImpl.class);
   @Override
   protected String getTableNameKey() {
      return "USER_TABLE";
   }

   @Override
   public Logger getLogger()
   {
      return LOG;
   }
}
