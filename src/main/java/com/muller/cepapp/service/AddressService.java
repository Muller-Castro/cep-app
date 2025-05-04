package com.muller.cepapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.muller.cepapp.entity.Address;
import com.muller.cepapp.exception.AddressNotFoundException;
import com.muller.cepapp.repository.AddressRepository;

@Service
public class AddressService {

    public static final String ADDRESS_NOT_FOUND_MESSAGE = "Address with ID '%s' not found.";

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address createAddress(Address address) {
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
            Address existingAddress = existingAddressOptional.get();
            existingAddress.setStreet(updatedAddress.getStreet());
            existingAddress.setNumber(updatedAddress.getNumber());
            existingAddress.setComplement(updatedAddress.getComplement());
            existingAddress.setNeighborhood(updatedAddress.getNeighborhood());
            existingAddress.setCity(updatedAddress.getCity());
            existingAddress.setState(updatedAddress.getState());
            existingAddress.setZipCode(updatedAddress.getZipCode());

            return addressRepository.save(existingAddress);
        }
        
        throw new AddressNotFoundException(String.format(ADDRESS_NOT_FOUND_MESSAGE, id.toString()));
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
    
}
