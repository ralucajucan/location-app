package org.scd.repository;

import org.scd.model.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

public interface LocationRepository extends CrudRepository<Location,Long> {

    @Query(value="SELECT * FROM LOCATIONS WHERE user_id=?1 AND UPDATE_DATE>=?2 AND UPDATE_DATE <=?3",nativeQuery = true)
    List <Location> filterLocationQuery(final Long userId, final Date startDate,final Date endDate);
}

