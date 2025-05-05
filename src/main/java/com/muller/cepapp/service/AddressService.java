package com.muller.cepapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.muller.cepapp.entity.Address;
import com.muller.cepapp.entity.User;
import com.muller.cepapp.exception.AddressNotFoundException;
import com.muller.cepapp.exception.InvalidZipCodeException;
import com.muller.cepapp.exception.UserNotFoundException;
import com.muller.cepapp.integration.ViaCEPResponse;
import com.muller.cepapp.integration.ViaCEPService;
import com.muller.cepapp.repository.AddressRepository;

@Service
public class AddressService {

    public static final String ADDRESS_NOT_FOUND_MESSAGE = "Address with ID '%s' not found.";

    private final AddressRepository addressRepository;
    private final ViaCEPService viaCEPService;
    private final UserService userService;

    @Autowired
    public AddressService(AddressRepository addressRepository, ViaCEPService viaCEPService, UserService userService) {
        this.addressRepository = addressRepository;
        this.viaCEPService     = viaCEPService;
        this.userService       = userService;
    }

    public Address createAddress(Address address, Long userId) {
        String zipCode = address.getZipCode();

        if(zipCode == null || zipCode.isEmpty()) {
            throw new InvalidZipCodeException("CEP is required");
        }

        ViaCEPResponse viaCEPResponse = viaCEPService.getAddressByZipCode(zipCode);

        address.setStreet(viaCEPResponse.getStreet());
        String number = viaCEPResponse.getNumber();
        address.setNumber((number == null || number.isEmpty()) ? "-1" : number);
        address.setComplement(viaCEPResponse.getComplement());
        address.setNeighborhood(viaCEPResponse.getNeighborhood());
        address.setCity(viaCEPResponse.getCity());
        address.setState(viaCEPResponse.getState());
        address.setZipCode(viaCEPResponse.getZipCode().replace("-", ""));

        Optional<User> user = userService.getUserById(userId);
        if(user.isPresent()) {
            address.setUser(user.get());
        }else {
            throw new UserNotFoundException(String.format("User ID '%s' not found", userId.toString()));
        }

        return addressRepository.save(address);
    }

    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public Address updateAddress(Long id, Address updatedAddress) {
        Optional<Address> existingAddressOptional = addressRepository.findById(id);

        if (existingAddressOptional.isPresent()) {
            String zipCode = updatedAddress.getZipCode();

            if(zipCode == null || zipCode.isEmpty()) {
                throw new InvalidZipCodeException("CEP is required");
            }

            ViaCEPResponse viaCEPResponse = viaCEPService.getAddressByZipCode(zipCode);

            Address existingAddress = existingAddressOptional.get();
            existingAddress.setStreet(viaCEPResponse.getStreet());
            String number = viaCEPResponse.getNumber();
            existingAddress.setNumber((number == null || number.isEmpty()) ? "-1" : number);
            existingAddress.setComplement(viaCEPResponse.getComplement());
            existingAddress.setNeighborhood(viaCEPResponse.getNeighborhood());
            existingAddress.setCity(viaCEPResponse.getCity());
            existingAddress.setState(viaCEPResponse.getState());
            existingAddress.setZipCode(viaCEPResponse.getZipCode());

            return addressRepository.save(existingAddress);
        }
        
        throw new AddressNotFoundException(String.format(ADDRESS_NOT_FOUND_MESSAGE, id.toString()));
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
    
}
