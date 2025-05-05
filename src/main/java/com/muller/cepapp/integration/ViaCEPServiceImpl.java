package com.muller.cepapp.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.muller.cepapp.exception.ViaCEPException;

@Service
public class ViaCEPServiceImpl implements ViaCEPService {

    private final RestTemplate restTemplate;
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    @Autowired
    public ViaCEPServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ViaCEPResponse getAddressByZipCode(String zipCode) {
        try {
            ResponseEntity<ViaCEPResponse> response = restTemplate.getForEntity(VIA_CEP_URL, ViaCEPResponse.class, zipCode);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ViaCEPException("Error fetching address from ViaCEP: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
             throw new ViaCEPException("Error connecting to ViaCEP: " + e.getMessage(), e);
        }
    }
    
}
