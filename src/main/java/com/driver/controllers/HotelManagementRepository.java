package com.driver.repositories;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class HotelManagementRepository {
    private List<Hotel> hotelList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private HashMap<Integer,List<Booking> > userWithBookingsMap = new HashMap<>();
    public String addHotel(Hotel hotel){
        hotelList.add(hotel);
        return "SUCCESS";
    }

    public boolean checkHotelExists(Hotel hotel) {
        for(Hotel registeredHotel : hotelList){
            if(hotel.getHotelName().equals(registeredHotel.getHotelName())){
                return true;
            }
        }
        return false;
    }

    public Integer addUser(User user) {
        userList.add(user);
        return user.getaadharCardNo();
    }


    public String getHotelWithMostFacilities() {
        int max = 0;
        String ans = "";
        for(Hotel hotel:hotelList){
            if(hotel.getFacilities().size()>max){
                ans = hotel.getHotelName();
            }
            else if(hotel.getFacilities().size()>0 && hotel.getFacilities().size()==max){
                if(ans.compareTo(hotel.getHotelName())>0){
                    ans = hotel.getHotelName();
                }
            }
        }
        return ans;
    }

    public int bookARoom(Booking booking) {
        String bookingId = UUID.randomUUID().toString();
        booking.setBookingId(bookingId);
        Hotel hotel = getHotelFromName(booking.getHotelName());
        if(hotel.getAvailableRooms()<booking.getNoOfRooms()){
            booking.setAmountToBePaid(-1);
            addBookingIntoUserWithBookingMap(booking);
            return -1;
        }
        hotel.setAvailableRooms(hotel.getAvailableRooms()-booking.getNoOfRooms());
        int amount = booking.getNoOfRooms()*hotel.getPricePerNight();
        booking.setAmountToBePaid(amount);
        addBookingIntoUserWithBookingMap(booking);
        return amount;
    }

    private void addBookingIntoUserWithBookingMap(Booking booking) {
        if(userWithBookingsMap.containsKey(booking.getBookingAadharCard())){
            userWithBookingsMap.get(booking.getBookingAadharCard()).add(booking);
        }
        else{
            ArrayList<Booking> bookingArrayList = new ArrayList<>();
            bookingArrayList.add(booking);
            userWithBookingsMap.put(booking.getBookingAadharCard(),bookingArrayList);
        }
    }

    public Hotel getHotelFromName(String name){
        for(Hotel hotel:hotelList){
            if(hotel.getHotelName().equals(name)){
                return hotel;
            }
        }
        return null;
    }

    public int getBookings(Integer aadharCard) {
        if(userWithBookingsMap.containsKey(aadharCard)){
            return userWithBookingsMap.get(aadharCard).size();
        }
        return 0;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        Hotel hotel = getHotelFromName(hotelName);
        List<Facility> hotelFacilites = hotel.getFacilities();
        for(Facility facility:newFacilities){
            if(!hotelFacilites.contains(facility)){
                hotelFacilites.add(facility);
            }
        }
        return hotel;
    }
}