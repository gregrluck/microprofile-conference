/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.microprofile.showcase.schedule.cdi;

import io.microprofile.showcase.schedule.model.Schedule;
import io.microprofile.showcase.schedule.persistence.LongKey;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author mike
 */
@ApplicationScoped
public class CacheProducer {

  @Inject
  private CacheManager cm;

  private Cache<LongKey, Schedule> scheduleCache;

  @PostConstruct
  public void init() {
    scheduleCache = cm.createCache("schedule", new MutableConfiguration<LongKey, Schedule>());
  }

  @Produces
  @ApplicationScoped
  @ScheduleCache
  public Cache<LongKey, Schedule> getCache() {
      return scheduleCache;
  }

}
