package org.scd.service;

import org.scd.config.exception.BusinessException;
import org.scd.model.Location;
import org.scd.model.User;
import org.scd.model.dto.InputLocationDTO;
import org.scd.model.security.CustomUserDetails;
import org.scd.model.security.Role;
import org.scd.repository.LocationRepository;
import org.scd.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class LocationServiceImpl implements LocationService{
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public LocationServiceImpl(LocationRepository locationRepository, UserRepository userRepository){
        this.locationRepository=locationRepository;
        this.userRepository=userRepository;
    }

    @Override
    public Long createLocation(InputLocationDTO inputLocationDTO) throws BusinessException {
        final User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (Objects.isNull(inputLocationDTO)) {
            throw new BusinessException(400, "Body null !");
        }

        if (Objects.isNull(inputLocationDTO.getLatitude())) {
            throw new BusinessException(400, "Latitude cannot be null ! ");
        }

        if (Objects.isNull(inputLocationDTO.getLongitude())) {
            throw new BusinessException(400, "Longitude cannot be null !");
        }
        Location location = new Location();
        location.setLongitude(inputLocationDTO.getLongitude());
        location.setLatitude(inputLocationDTO.getLatitude());
        location.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        location.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        location.setUser(currentUser);
        locationRepository.save(location);
        return location.getId();
    }

    @Override
    public Location getSomeLocation(Long locationId) throws BusinessException{
        Location location=locationRepository.findById(locationId).orElse(null);
        if (Objects.isNull(location)) {
            throw new BusinessException(404, "Location does not exist!");
        }
        final User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (checkAuthority(location,currentUser,false)){
            return location;
        } else throw new BusinessException(401, "Unauthorised action!");
    }
    @Override
    public Location updateLocation(Long locationId, InputLocationDTO inputLocationDTO) throws BusinessException{
        Location location=locationRepository.findById(locationId).orElseThrow(
                () ->new BusinessException(404, "Location does not exist!"));

        final User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (checkAuthority(location,currentUser,false)) {
            location.setLatitude(inputLocationDTO.getLatitude());
            location.setLongitude(inputLocationDTO.getLongitude());
            location.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            locationRepository.save(location);
            return location;
        } else throw new BusinessException(401, "Unauthorized action!");
    }

    @Override
    public void deleteLocation(Long locationId) throws BusinessException{
        Location location=locationRepository.findById(locationId).orElseThrow(
                ()-> new BusinessException(404, "Location does not exist!"));

        final User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (checkAuthority(location,currentUser,false)) {
            locationRepository.delete(location);
        } else throw new BusinessException(401, "Unauthorized action!");
    }

    @Override
    public List<String> filterLocation(Long userId, Date startDate, Date endDate) throws BusinessException {
        final User currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (checkAuthority(null,currentUser,true)){
            User user=userRepository.findById(userId).orElse(null);
            if (Objects.isNull(user)){
                throw new BusinessException(404, "User with given id not found!");
            }
            final List<Location> locationList = locationRepository.filterLocationQuery(userId,startDate,endDate);
            List<String> prettyLocationList= new ArrayList<>();
            for (Location loc: locationList){
                prettyLocationList.add("Id:"+loc.getId()+" Lat:"+loc.getLatitude()+
                        " Long:"+loc.getLongitude()+" Date:"+loc.getUpdatedAt());
            }
            return prettyLocationList;
        }
        return null;
    }

    private boolean checkAuthority(Location location, User user,boolean strictAuth){
        boolean isAdmin = false;
        String adminRole = "ADMIN";
        Set<Role> roles = user.getRoles();
        for(Role role : roles){
            if ((role.getRole()).equals(adminRole)){
                isAdmin = true;
                break;
            }
        }
        if (strictAuth) return isAdmin;
        else return  isAdmin || (location.getUser().getId().equals((user.getId())));
    }
}
