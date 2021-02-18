package org.scd.service;

import org.scd.config.exception.BusinessException;
import org.scd.model.Location;
import org.scd.model.dto.InputLocationDTO;
import java.util.Date;
import java.util.List;

public interface LocationService {
    Long createLocation(final InputLocationDTO inputLocationDTO) throws BusinessException;
    Location getSomeLocation(final Long locationId) throws BusinessException;
    Location updateLocation(final Long locationId,final InputLocationDTO inputLocationDTO) throws BusinessException;
    void deleteLocation(final Long locationId) throws BusinessException;
    List<String> filterLocation(Long userId, Date startDate, Date endDate) throws BusinessException;
}
