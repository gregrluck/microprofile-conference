/*
 * Copyright 2016 Microprofile.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microprofile.showcase.schedule.persistence;

import io.microprofile.showcase.bootstrap.BootstrapData;
import io.microprofile.showcase.schedule.cdi.ScheduleCache;
import io.microprofile.showcase.schedule.model.Schedule;
import io.microprofile.showcase.schedule.model.adapters.LocalDateAdapter;
import io.microprofile.showcase.schedule.model.adapters.LocalTimeAdapter;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheRemove;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static io.microprofile.showcase.schedule.persistence.LongKey.wrap;
import static java.lang.Long.parseLong;

@ApplicationScoped
@CacheDefaults(cacheName = "schedule")
public class ScheduleDAO {

    @Inject
    @ScheduleCache
    private Cache<LongKey, Schedule> scheduleCache;


    @Inject
    BootstrapData bootstrapData;

    private final AtomicLong sequence = new AtomicLong(11);

    @PostConstruct
    private void initStore() {
        Logger.getLogger(ScheduleDAO.class.getName()).log(Level.INFO, "Initialise schedule DAO from bootstrap data");
        bootstrap();
    }

    private void bootstrap() {
        LocalDateAdapter dateAdapter = new LocalDateAdapter();
        LocalTimeAdapter timeAdapter = new LocalTimeAdapter();
        IdGenerator<String> venueNameGen = new IdGenerator<>(sequence);
        bootstrapData.getSchedules()
            .forEach(bootstrap -> {
                try {
                    Long venueId = venueNameGen.getOrGenerateId(bootstrap.getVenue());
                    Schedule sched = new Schedule(
                        parseLong(bootstrap.getId()),
                        parseLong(bootstrap.getSessionId()),
                        bootstrap.getVenue(),
                        venueId,
                        dateAdapter.unmarshal(bootstrap.getDate()),
                        timeAdapter.unmarshal(bootstrap.getStartTime()),
                        Duration.ofMinutes(new Double(bootstrap.getLength()).longValue())
                    );

                    LongKey key = wrap(sched.getId());
                    scheduleCache.put(key, sched);
                } catch (Exception e) {
                    System.out.println("Failed to parse bootstrap data: "+ e.getMessage());
                }
            });
    }

    public Schedule addSchedule(Schedule schedule) {
        long id = sequence.getAndIncrement();
        schedule.setId(id);

        if (schedule.getSessionId() == null) {
            schedule.setSessionId(sequence.getAndIncrement());
        }
        LongKey key = wrap(schedule.getId());
        scheduleCache.put(key, schedule);
        return schedule;
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        for (Cache.Entry<LongKey, Schedule> scheduleEntry : scheduleCache) {
            schedules.add(scheduleEntry.getValue());
        }
        return schedules;
    }

    public Optional<Schedule> findById(long id) {
        Schedule schedule = scheduleCache.get(wrap(id));
        return Optional.ofNullable(schedule);
    }

    public Schedule updateSchedule(Schedule schedule) {
        if (schedule.getId() == null) {
            return addSchedule(schedule);
        }

        LongKey key = wrap(schedule.getId());
        scheduleCache.put(key, schedule);
        return schedule;
    }



    @CacheRemove(cacheName="schedule", cacheKeyGenerator = LongKeyGenerator.class)
    public void deleteSchedule(Long scheduleId) {
//        if (scheduleId != null) {
//            scheduleCache.remove(new MyKey(scheduleId));
//        }
    }

    public List<Schedule> findByVenue(Long venueId) {
        return getAllSchedules()
            .stream()
            .filter(schedule -> schedule.getVenueId().equals(venueId))
            .collect(Collectors.toList());
    }

    public List<Schedule> findByDate(LocalDate date) {
        return getAllSchedules()
                .stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .collect(Collectors.toList());
    }

}
